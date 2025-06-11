package com.huifeng.trademanagesystem.exception;


public class DuplicateTradeException extends RuntimeException {

    private String msg;

    public DuplicateTradeException(String msg) {
        this.msg = msg;
    }
}
