package com.huifeng.trademanagesystem.service.impl;

import com.huifeng.trademanagesystem.exception.DuplicateTradeException;
import com.huifeng.trademanagesystem.exception.TradeNotFoundException;
import com.huifeng.trademanagesystem.model.Trade;
import com.huifeng.trademanagesystem.repository.TradeRepository;
import com.huifeng.trademanagesystem.service.TradeService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {

    private final TradeRepository repository;

    public TradeServiceImpl(TradeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Trade createTrade(Trade trade) {
        if (repository.existsByReferenceId(trade.getReferenceId())) {
            throw new DuplicateTradeException(
                    "Trade with reference ID " + trade.getReferenceId() + " already exists");
        }
        if (trade.getAmount() == null){
            throw new ResponseStatusException(HttpStatusCode.valueOf(500));
        }
        return repository.save(trade);
    }

    @Override
    @CacheEvict(value = "trades", key = "#id")
    public Trade updateTrade(Long id, Trade trade) {
        return repository.findById(id)
                .map(existing -> {
                    trade.setId(id);
                    return repository.save(trade);
                })
                .orElseThrow(() -> new TradeNotFoundException(id));
    }

    @Override
    @CacheEvict(value = "trades", key = "#id")
    public void deleteTrade(Long id) {
        if (!repository.findById(id).isPresent()) {
            throw new TradeNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Override
    @Cacheable(value = "trades", key = "#page + '-' + #size")
    public Page<Trade> getAllTrades(int page, int size) {
        List<Trade> content = repository.findPaginated(page, size);
        long total = repository.findAll().size();
        return new PageImpl<>(content, PageRequest.of(page, size), total);
    }

    @Override
    public Trade getTradeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TradeNotFoundException(id));
    }
}
