package com.script.service;

import com.script.domain.dto.CreateOrderDTO;

public interface StockOperateService {

    void frozenStock(CreateOrderDTO dto);
}
