package com.huifeng.trademanagesystem.exception;

public class TradeNotFoundException extends RuntimeException {

    private final Long id;

    public TradeNotFoundException(Long id) {
        this.id = id;
    }
}
