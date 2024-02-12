package com.interswitch.StyleMe.model;

import com.interswitch.StyleMe.enums.EventType;
import com.interswitch.StyleMe.enums.ItemCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document
public class ClothingItem {
    @Id
    private String id;

    @Indexed(unique = true)
    private String driveId;

    @Indexed(unique = true)
    private String url;

    private ItemCategory itemCategory;

    private List<EventType> event;

    @DBRef
    private User user;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    private boolean forSale;
}
