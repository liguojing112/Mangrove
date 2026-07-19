package com.mangrove.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class MusicPhotoOrderRequest {
    private List<Item> items;

    @Data
    public static class Item {
        private Long id;
        private Integer sortOrder;
    }
}
