package com.script.domain.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreateOrderDTO {
    
    private List<OrderSkuDTO> skus;
}
