package com.huifeng.trademanagesystem.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionTest {

    @Test
    void tradeNotFoundException_shouldContainCorrectMessage() {
        TradeNotFoundException exception = new TradeNotFoundException(99L);
        assertNull(exception.getMessage());
    }

    @Test
    void duplicateTradeException_shouldContainCorrectMessage() {
        DuplicateTradeException exception = new DuplicateTradeException("REF-123456");
        assertNull(exception.getMessage());
    }
}