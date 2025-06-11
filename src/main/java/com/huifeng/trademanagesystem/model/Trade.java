package com.huifeng.trademanagesystem.model;

import com.huifeng.trademanagesystem.common.TradeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Trade {
    private Long id;
    @Min(1)
    @NotNull
    private BigDecimal amount;
    private TradeType type;
    private String description;
    private Instant timestamp;
    private String referenceId;

    public void setId(Long id) {
        this.id = id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setType(TradeType type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}