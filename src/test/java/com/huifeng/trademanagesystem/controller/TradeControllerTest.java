package com.huifeng.trademanagesystem.controller;

import com.huifeng.trademanagesystem.common.TradeType;
import com.huifeng.trademanagesystem.exception.DuplicateTradeException;
import com.huifeng.trademanagesystem.model.Trade;
import com.huifeng.trademanagesystem.service.TradeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class TradeControllerTest {

    @Mock
    private TradeService tradeService;

    @InjectMocks
    private TradeController tradeController;

    private Trade createSampleTrade() {
        Trade trade = new Trade();
        trade.setId(1L);
        trade.setAmount(new BigDecimal("100.50"));
        trade.setType(TradeType.DEPOSIT);
        trade.setDescription("Salary deposit");
        trade.setTimestamp(Instant.now());
        trade.setReferenceId("REF-123456");
        return trade;
    }

    @Test
    void createTrade_shouldReturnCreatedTrade() {
        Trade sampleTrade = createSampleTrade();
        when(tradeService.createTrade(any(Trade.class))).thenReturn(sampleTrade);

        ResponseEntity<Trade> response = tradeController.createTrade(sampleTrade);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleTrade.getId(), response.getBody().getId());
        verify(tradeService, times(1)).createTrade(sampleTrade);
    }

    @Test
    void createTrade_shouldThrowExceptionWhenInvalidInput() {
        Trade invalidTrade = createSampleTrade();
        invalidTrade.setAmount(null); // 无效输入
        ResponseEntity<Trade> trade = tradeController.createTrade(invalidTrade);
        assertEquals(trade.getStatusCode(), BAD_REQUEST);
    }

    @Test
    void updateTrade_shouldReturnUpdatedTrade() {
        Trade updatedTrade = createSampleTrade();
        updatedTrade.setAmount(new BigDecimal("200.00"));

        when(tradeService.updateTrade(eq(1L), any(Trade.class))).thenReturn(updatedTrade);

        Trade result = tradeController.updateTrade(1L, updatedTrade);

        assertNotNull(result);
        assertEquals(new BigDecimal("200.00"), result.getAmount());
        verify(tradeService, times(1)).updateTrade(1L, updatedTrade);
    }

    @Test
    void deleteTrade_shouldCallService() {
        doNothing().when(tradeService).deleteTrade(1L);

        tradeController.deletetrade(1L);

        verify(tradeService, times(1)).deleteTrade(1L);
    }

    @Test
    void getAllTrades_shouldReturnPagedTrades() {
        Trade sampleTrade = createSampleTrade();
        Page<Trade> pagedTrades = new PageImpl<>(List.of(sampleTrade));
        when(tradeService.getAllTrades(0, 10)).thenReturn(pagedTrades);

        Page<Trade> result = tradeController.getAllTrades(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(sampleTrade, result.getContent().get(0));
        verify(tradeService, times(1)).getAllTrades(0, 10);
    }

    @Test
    void getAllTrades_shouldReturnEmptyPage() {
        Page<Trade> emptyPage = new PageImpl<>(Collections.emptyList());
        when(tradeService.getAllTrades(0, 10)).thenReturn(emptyPage);

        Page<Trade> result = tradeController.getAllTrades(0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTradeById_shouldReturnTrade() {
        Trade sampleTrade = createSampleTrade();
        when(tradeService.getTradeById(1L)).thenReturn(sampleTrade);

        Trade result = tradeController.getTradeById(1L);

        assertNotNull(result);
        assertEquals(sampleTrade, result);
        verify(tradeService, times(1)).getTradeById(1L);
    }

    @Test
    void createTrade_shouldHandleDuplicateReference() {
        Trade duplicateTrade = createSampleTrade();
        when(tradeService.createTrade(any(Trade.class)))
                .thenThrow(new DuplicateTradeException("Duplicate reference ID"));

        assertThrows(DuplicateTradeException.class, () -> tradeController.createTrade(duplicateTrade));
    }
}