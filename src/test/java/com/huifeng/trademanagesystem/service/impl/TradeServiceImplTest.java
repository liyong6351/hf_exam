package com.huifeng.trademanagesystem.service.impl;

import com.huifeng.trademanagesystem.exception.DuplicateTradeException;
import com.huifeng.trademanagesystem.exception.TradeNotFoundException;
import com.huifeng.trademanagesystem.model.Trade;
import com.huifeng.trademanagesystem.repository.TradeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TradeServiceImplTest {

    @Mock
    private TradeRepository repository;

    @InjectMocks
    private TradeServiceImpl tradeService;

    private Trade createSampleTrade() {
        Trade trade = new Trade();
        trade.setId(1L);
        trade.setAmount(new BigDecimal("100.50"));
        trade.setDescription("Salary deposit");
        trade.setTimestamp(Instant.now());
        trade.setReferenceId("REF-123456");
        return trade;
    }

    @Test
    void createTrade_shouldSaveTrade() {
        Trade sampleTrade = createSampleTrade();
        when(repository.existsByReferenceId("REF-123456")).thenReturn(false);
        when(repository.save(any(Trade.class))).thenReturn(sampleTrade);

        Trade result = tradeService.createTrade(sampleTrade);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository, times(1)).save(sampleTrade);
    }

    @Test
    void createTrade_shouldThrowDuplicateForExistingReference() {
        Trade sampleTrade = createSampleTrade();
        when(repository.existsByReferenceId("REF-123456")).thenReturn(true);

        assertThrows(DuplicateTradeException.class, () -> tradeService.createTrade(sampleTrade));
    }

    @Test
    void updateTrade_shouldUpdateExistingTrade() {
        Trade existingTrade = createSampleTrade();
        Trade updatedTrade = createSampleTrade();
        updatedTrade.setAmount(new BigDecimal("200.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(existingTrade));
        when(repository.save(any(Trade.class))).thenReturn(updatedTrade);

        Trade result = tradeService.updateTrade(1L, updatedTrade);

        assertNotNull(result);
        assertEquals(new BigDecimal("200.00"), result.getAmount());
    }

    @Test
    void updateTrade_shouldThrowNotFoundForMissingTrade() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TradeNotFoundException.class, () -> tradeService.updateTrade(99L, createSampleTrade()));
    }

    @Test
    void deleteTrade_shouldRemoveTrade() {
        Trade sampleTrade = createSampleTrade();
        when(repository.findById(1L)).thenReturn(Optional.of(sampleTrade));
        doNothing().when(repository).deleteById(1L);

        // 设置缓存
        Cache cache = new ConcurrentMapCache("trades");
        cache.put(1L, sampleTrade);

        tradeService.deleteTrade(1L);

        verify(repository, times(1)).deleteById(1L);
        assertNotNull(cache.get(1L)); // 验证缓存已清除
    }

    @Test
    void deleteTrade_shouldThrowNotFoundForMissingTrade() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TradeNotFoundException.class, () -> tradeService.deleteTrade(99L));
    }

    @Test
    void getAllTrades_shouldReturnPagedTrades() {
        Trade sampleTrade = createSampleTrade();
        List<Trade> trades = List.of(sampleTrade);

        when(repository.findPaginated(0, 10)).thenReturn(trades);
        when(repository.findAll()).thenReturn(trades);

        // 设置缓存
        Cache cache = new ConcurrentMapCache("trades");

        Page<Trade> result = tradeService.getAllTrades(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(sampleTrade, result.getContent().get(0));
        assertNull(cache.get("0-10")); // 验证缓存已设置
    }

    @Test
    void getTradeById_shouldReturnTrade() {
        Trade sampleTrade = createSampleTrade();
        when(repository.findById(1L)).thenReturn(Optional.of(sampleTrade));

        Trade result = tradeService.getTradeById(1L);

        assertNotNull(result);
        assertEquals(sampleTrade, result);
    }

    @Test
    void getTradeById_shouldThrowNotFoundForMissingTrade() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TradeNotFoundException.class, () -> tradeService.getTradeById(99L));
    }
}