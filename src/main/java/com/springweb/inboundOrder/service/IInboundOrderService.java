package com.springweb.inboundOrder.service;

import java.util.List;
import java.util.Map;

import com.springweb.inboundOrder.domain.InboundOrder;
import com.springweb.inboundOrder.dto.InboundOrderCancelResponseDto;
import com.springweb.inboundOrder.dto.InboundOrderDtoInsert;
import com.springweb.inboundOrder.dto.InboundOrderDtoReturn;
import com.springweb.inboundOrder.dto.InboundOrderPendingSelectDto;
import com.springweb.inboundOrder.dto.InboundOrderPreviewDto;
import com.springweb.inboundOrder.dto.InboundOrderReceiveDto;
import com.springweb.inboundOrder.dto.InboundReceiptResponseDto;

public interface IInboundOrderService {

  public InboundOrderDtoReturn createInboundOrder(InboundOrderDtoInsert dto);
  public InboundOrder getInboundOrderById (Long id );
  public InboundReceiptResponseDto receiveAndAdjust(String orderNumber, InboundOrderReceiveDto body);
  List<InboundOrderPendingSelectDto> listPendingForSelect();

 // InboundOrderQueryService.java
Map<String, Object> getOrderDetailForReceipt(String orderNumber);


 InboundOrderPreviewDto getPreview(Long id);

  InboundOrderCancelResponseDto cancel(Long id, String reason, String actor);












}
