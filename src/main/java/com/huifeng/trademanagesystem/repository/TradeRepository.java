package com.huifeng.trademanagesystem.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.huifeng.trademanagesystem.model.Trade;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TradeRepository {

    private final ConcurrentHashMap<Long, Trade> trades = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Cache<Object, Object> referenceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public Trade save(Trade trade) {
        Long id = idGenerator.getAndIncrement();
        trade.setId(id);
        trade.setTimestamp(Instant.now());
        trades.put(id, trade);
        referenceCache.invalidateAll();
        return trade;
    }

    public boolean existsByReferenceId(String referenceId) {
        return referenceCache.getIfPresent(referenceId) != null;
    }

    public Optional<Trade> findById(Long id) {
        return Optional.ofNullable(trades.get(id));
    }

    public void deleteById(Long id) {
        Trade trade = trades.remove(id);
        if (trade != null) {
            referenceCache.invalidate(trade.getReferenceId());
        }
    }

    public List<Trade> findPaginated(int page, int size) {
        List<Trade> alltrades = new ArrayList<>(trades.values());
        alltrades.sort(Comparator.comparing(Trade::getTimestamp).reversed());

        int start = page * size;
        if (start >= alltrades.size()) {
            return Collections.emptyList();
        }

        int end = Math.min(start + size, alltrades.size());
        return alltrades.subList(start, end);
    }

    public Collection<Trade> findAll() {
        return new ArrayList<>(trades.values());
    }
}
