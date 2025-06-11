package com.huifeng.trademanagesystem.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TradeTypeTest {

    @Test
    void enumValues_shouldContainDepositAndWithdrawal() {
        TradeType[] values = TradeType.values();
        assertEquals(2, values.length);
        assertEquals(TradeType.DEPOSIT, values[0]);
        assertEquals(TradeType.WITHDRAWAL, values[1]);
    }

    @Test
    void valueOf_shouldReturnCorrectEnum() {
        assertEquals(TradeType.DEPOSIT, TradeType.valueOf("DEPOSIT"));
        assertEquals(TradeType.WITHDRAWAL, TradeType.valueOf("WITHDRAWAL"));
    }

    @Test
    void enum_shouldHaveCorrectToString() {
        assertEquals("DEPOSIT", TradeType.DEPOSIT.toString());
        assertEquals("WITHDRAWAL", TradeType.WITHDRAWAL.toString());
    }
}