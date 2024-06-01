package com.script.service;

import com.script.domain.dto.CreateOrderDTO;

public interface StockOperateService {

    void logTransfer();

    void frozenStock(CreateOrderDTO dto);

    void releaseFrozenStock(CreateOrderDTO dto);

    void cleanFrozenStock(CreateOrderDTO dto);

    void addSaleStock(CreateOrderDTO dto);
}
