package com.springweb.inboundOrder.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InboundOrderCancelResponseDto {

         private Long id;
    private String orderNumber;
    private String status;       // "CANCELLED"
    private LocalDateTime cancelledAt;
    private String cancelReason;
    private String cancelledBy;

}
