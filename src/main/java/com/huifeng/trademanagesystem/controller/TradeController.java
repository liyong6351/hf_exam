package com.huifeng.trademanagesystem.controller;

import com.huifeng.trademanagesystem.model.Trade;
import com.huifeng.trademanagesystem.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/trade")
public class TradeController {
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping
    public ResponseEntity<Trade> createTrade(@Valid @RequestBody Trade trade) throws ResponseStatusException {
        if (trade.getAmount() == null){
            return ResponseEntity.status(BAD_REQUEST).body(null);
        }
        Trade created = tradeService.createTrade(trade);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Trade updateTrade(@PathVariable Long id, @Valid @RequestBody Trade trade) {
        return tradeService.updateTrade(id, trade);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletetrade(@PathVariable Long id) {
        tradeService.deleteTrade(id);
    }

    @GetMapping
    public Page<Trade> getAllTrades(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return tradeService.getAllTrades(page, size);
    }

    @GetMapping("/{id}")
    public Trade getTradeById(@PathVariable Long id) {
        return tradeService.getTradeById(id);
    }
}
