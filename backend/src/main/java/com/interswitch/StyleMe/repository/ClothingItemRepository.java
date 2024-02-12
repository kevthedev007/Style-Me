package com.interswitch.StyleMe.repository;

import com.interswitch.StyleMe.model.ClothingItem;
import com.interswitch.StyleMe.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClothingItemRepository extends MongoRepository<ClothingItem, String> {
    Optional<ClothingItem> findByUserAndDriveId(User user, String driveId);
    List<ClothingItem> findByUserAndEventIn(User user, String eventType);
    Optional<ClothingItem> findByDriveId(String driveId);
    List<ClothingItem> findByForSaleIsTrue();
}
