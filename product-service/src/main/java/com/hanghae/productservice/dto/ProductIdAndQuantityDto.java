package com.hanghae.productservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductIdAndQuantityDto {
    private Long productId;
    private int quantity;
}
