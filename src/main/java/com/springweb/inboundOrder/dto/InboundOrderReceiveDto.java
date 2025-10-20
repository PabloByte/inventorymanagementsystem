package com.springweb.inboundOrder.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class InboundOrderReceiveDto {

     @NotEmpty   
     private List<InboundOrderReceiveItemDto> items;
    private String note;        // opcional
    private String receivedBy;  // opcional (si no, "system")
    
    public InboundOrderReceiveDto(@NotEmpty List<InboundOrderReceiveItemDto> items, String note, String receivedBy) {
        this.items = items;
        this.note = note;
        this.receivedBy = receivedBy;
    }
    public InboundOrderReceiveDto() {
    }
    public List<InboundOrderReceiveItemDto> getItems() {
        return items;
    }
    public void setItems(List<InboundOrderReceiveItemDto> items) {
        this.items = items;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public String getReceivedBy() {
        return receivedBy;
    }
    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    
    

}
