package com.huifeng.trademanagesystem.service;

import com.huifeng.trademanagesystem.model.Trade;
import org.springframework.data.domain.Page;

public interface TradeService {

    Page<Trade> getAllTrades(int page, int size);

    Trade getTradeById(Long id);

    void deleteTrade(Long id);

    Trade createTrade(Trade trade);

    Trade updateTrade(Long id, Trade trade);
}
