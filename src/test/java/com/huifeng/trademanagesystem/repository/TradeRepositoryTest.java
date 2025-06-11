package com.huifeng.trademanagesystem.repository;

import com.huifeng.trademanagesystem.common.TradeType;
import com.huifeng.trademanagesystem.model.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TradeRepositoryTest {

    private TradeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TradeRepository();
    }

    private Trade createSampleTrade() {
        Trade trade = new Trade();
        trade.setAmount(new BigDecimal("100.50"));
        trade.setType(TradeType.DEPOSIT);
        trade.setDescription("Salary deposit");
        trade.setReferenceId("REF-123456");
        return trade;
    }

    @Test
    void save_shouldAssignIdAndTimestamp() {
        Trade trade = createSampleTrade();
        Trade saved = repository.save(trade);

        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
        assertNotNull(saved.getTimestamp());
    }

    @Test
    void save_shouldIncrementId() {
        Trade trade1 = repository.save(createSampleTrade());
        Trade trade2 = repository.save(createSampleTrade());

        assertEquals(1, trade1.getId());
        assertEquals(2, trade2.getId());
    }

    @Test
    void existsByReferenceId_shouldDetectDuplicates() {
        Trade trade = createSampleTrade();
        repository.save(trade);

        assertFalse(repository.existsByReferenceId("REF-123456"));
        assertFalse(repository.existsByReferenceId("REF-999999"));
    }

    @Test
    void findById_shouldReturnSavedTrade() {
        Trade trade = repository.save(createSampleTrade());
        Optional<Trade> found = repository.findById(trade.getId());

        assertTrue(found.isPresent());
        assertEquals(trade.getId(), found.get().getId());
    }

    @Test
    void findById_shouldReturnEmptyForMissingTrade() {
        Optional<Trade> found = repository.findById(99L);
        assertTrue(found.isEmpty());
    }

    @Test
    void deleteById_shouldRemoveTrade() {
        Trade trade = repository.save(createSampleTrade());
        repository.deleteById(trade.getId());

        Optional<Trade> found = repository.findById(trade.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void deleteById_shouldHandleMissingTrade() {
        repository.deleteById(99L); // 不应该抛出异常
    }

    @Test
    void findPaginated_shouldReturnCorrectPage() {
        // 保存10笔交易
        for (int i = 0; i < 10; i++) {
            Trade trade = createSampleTrade();
            trade.setReferenceId("REF-" + i);
            repository.save(trade);
        }

        // 获取第0页，每页5条
        List<Trade> page0 = repository.findPaginated(0, 5);
        assertEquals(5, page0.size());

        // 获取第1页，每页5条
        List<Trade> page1 = repository.findPaginated(1, 5);
        assertEquals(5, page1.size());

        // 获取第2页，每页5条（应空）
        List<Trade> page2 = repository.findPaginated(2, 5);
        assertTrue(page2.isEmpty());
    }

    @Test
    void findPaginated_shouldReturnTradesInReverseChronologicalOrder() {
        Trade trade1 = createSampleTrade();
        Trade trade2 = createSampleTrade();
        trade2.setReferenceId("REF-2");

        repository.save(trade1);
        // 确保第二笔交易时间更晚
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        repository.save(trade2);

        List<Trade> trades = repository.findPaginated(0, 10);
        assertEquals(2, trades.size());
        assertEquals(trade2.getId(), trades.get(0).getId()); // 最近的在最前面
    }
}