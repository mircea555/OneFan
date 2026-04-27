package com.onefan.backend.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public class OrderRequest {

    public static class Item {
        @NotNull
        private Long collectibleId;

        @Min(1)
        private int quantity;

        public Long getCollectibleId() {
            return collectibleId;
        }

        public void setCollectibleId(Long collectibleId) {
            this.collectibleId = collectibleId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    @NotEmpty
    private List<Item> items;


    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
