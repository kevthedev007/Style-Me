package com.interswitch.StyleMe.service.impl;

import com.interswitch.StyleMe.config.GoogleDriveManager;
import com.interswitch.StyleMe.dto.UserClothingDTO;
import com.interswitch.StyleMe.dto.UserDto;
import com.interswitch.StyleMe.dto.responses.ItemCategoryDto;
import com.interswitch.StyleMe.dto.responses.OutfitResponseDto;
import com.interswitch.StyleMe.dto.responses.RecommendationResponseDto;
import com.interswitch.StyleMe.enums.EventType;
import com.interswitch.StyleMe.enums.ItemCategory;
import com.interswitch.StyleMe.exceptions.StyleMeException;
import com.interswitch.StyleMe.model.ClothingItem;
import com.interswitch.StyleMe.model.User;
import com.interswitch.StyleMe.repository.ClothingItemRepository;
import com.interswitch.StyleMe.repository.UserRepository;
import com.interswitch.StyleMe.service.OutfitService;
import com.interswitch.StyleMe.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

import static com.interswitch.StyleMe.dto.ClothDto.clothingItemDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutfitServiceImpl implements OutfitService {

    private final UserRepository userRepository;
    private final GoogleDriveManager googleDriveManager;
    private final ClothingItemRepository clothingItemRepository;
    private final WebClient webClient;

    @Override
    public String uploadOutfit(MultipartFile file, String itemCategory, List<EventType> eventTypes) throws StyleMeException {
        try {
            String email = UserUtil.getAuthenticatedUserEmail();
            User user = userRepository.findUserByEmail(email).orElseThrow(
                    () -> new StyleMeException("User cannot be found", HttpStatus.NOT_FOUND)
            );

            String fileId = googleDriveManager.uploadFile(file);
//            String fileURL = googleDriveManager.getImageURL(fileId);
            String fileURL = "https://drive.google.com/uc?export=view&id=" + fileId;

            ClothingItem clothingItem = new ClothingItem();
            clothingItem.setDriveId(fileId);
            clothingItem.setUrl(fileURL);
            clothingItem.setItemCategory(ItemCategory.valueOf(itemCategory));
            clothingItem.setEvent(eventTypes);
            clothingItem.setUser(user);

            clothingItemRepository.save(clothingItem);

            return fileId;
        } catch (StyleMeException e) {
            log.error("Error uploading outfit: [{}]", e.getMessage());
            throw new StyleMeException("Error uploading outfit", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public String deleteFile(String fileId) throws StyleMeException {
        try {
            String email = UserUtil.getAuthenticatedUserEmail();
            User user = userRepository.findUserByEmail(email).orElseThrow(
                    () -> new StyleMeException("User cannot be found", HttpStatus.NOT_FOUND)
            );

            googleDriveManager.deleteFile(fileId);
            Optional<ClothingItem> clothingItem = Optional.ofNullable(clothingItemRepository.findByUserAndDriveId(user, fileId)
                    .orElseThrow(() -> new StyleMeException("Clothing Item Not Found.", HttpStatus.NOT_FOUND)));

            clothingItemRepository.delete(clothingItem.get());
            return "Item deleted successfully";
        } catch (Exception e) {
            throw new StyleMeException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<OutfitResponseDto> getOutfitsByEvent(String event) throws StyleMeException {
        String email = UserUtil.getAuthenticatedUserEmail();
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new StyleMeException("User cannot be found", HttpStatus.NOT_FOUND)
        );

        List<OutfitResponseDto> outfitResponseDtos = new ArrayList<>();
        return clothingItemRepository.findByUserAndEventIn(user, event)
                .stream().map(clothingItem -> {
                    OutfitResponseDto outfitResponseDto = new OutfitResponseDto();
                    outfitResponseDto.setUrl(clothingItem.getUrl());
                    outfitResponseDto.setItemCategory(clothingItem.getItemCategory());
                    outfitResponseDto.setDriveId(clothingItem.getDriveId());
                    outfitResponseDto.setEvent(event);
                    outfitResponseDto.setForSale(clothingItem.isForSale());
                    outfitResponseDtos.add(outfitResponseDto);
                    return outfitResponseDto;
                }).collect(Collectors.toList());
    }

    @Override
    public RecommendationResponseDto recommendOutfit(String eventType) throws StyleMeException {
        String email = UserUtil.getAuthenticatedUserEmail();
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new StyleMeException("User cannot be found", HttpStatus.NOT_FOUND)
        );

        List<ClothingItem> clothingItems = clothingItemRepository.findByUserAndEventIn(user, eventType);

        //get each cloth type for the event
        List<String> shirts = getItemCategoryList(clothingItems, ItemCategory.Shirt);
        List<String> trousers = getItemCategoryList(clothingItems, ItemCategory.Trouser);
        List<String> skirts = getItemCategoryList(clothingItems, ItemCategory.Skirt);
        List<String> dresses = getItemCategoryList(clothingItems, ItemCategory.Dress);
        List<String> apparels = getItemCategoryList(clothingItems, ItemCategory.Apparel);
        List<String> shoes = getItemCategoryList(clothingItems, ItemCategory.Shoe);
        List<String> bags = getItemCategoryList(clothingItems, ItemCategory.Bag);

        // Select 3 random shirts and 3 random dresses
        List<String> selectedShirts = selectRandomItems(shirts, 3);
        List<String> selectedDresses = selectRandomItems(dresses, 3);

        System.out.println(selectedDresses);

        ItemCategoryDto itemCategoryDto = new ItemCategoryDto();
        itemCategoryDto.setApparels(apparels);
        itemCategoryDto.setBags(bags);
        itemCategoryDto.setDresses(selectedDresses);
        itemCategoryDto.setShoes(shoes);
        itemCategoryDto.setSkirts(skirts);
        itemCategoryDto.setShirts(selectedShirts);
        itemCategoryDto.setTrousers(trousers);


        //send to python model
        RecommendationResponseDto recommendations = webClient.post()
                .uri("http://127.0.0.1:5000/recommendations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(itemCategoryDto)
                .retrieve()
                .bodyToMono(RecommendationResponseDto.class)
                .block();

        if (!recommendations.getRecommendations().isEmpty()) {
            List<List<String>> newRecommendation = recommendations.getRecommendations().stream()
                    .map(recommendation -> recommendation.stream()
                            .map(driveId -> clothingItemRepository.findByDriveId(driveId).get().getUrl())
                            .collect(Collectors.toList())).collect(Collectors.toList());

            recommendations.setRecommendations(newRecommendation);
        }
        return recommendations;
    }


    @Override
    public void markClothForSale(String clothId) throws StyleMeException {
        ClothingItem clothingItem = clothingItemRepository.findById(clothId).orElseThrow(() -> new StyleMeException("cloth does not exist", HttpStatus.NOT_FOUND));
        clothingItem.setForSale(true);
        clothingItemRepository.save(clothingItem);
    }

    @Override
    public List<UserClothingDTO> getUsersAndClothingForSale() {
        List<UserClothingDTO> userClothingDTOList = new ArrayList<>();

        List<ClothingItem> clothingItemsForSale = clothingItemRepository.findByForSaleIsTrue();

        Map<User, List<ClothingItem>> clothingItemsByUser = clothingItemsForSale.stream()
                .collect(Collectors.groupingBy(ClothingItem::getUser));

        for (Map.Entry<User, List<ClothingItem>> entry : clothingItemsByUser.entrySet()) {
            User user = entry.getKey();
            List<ClothingItem> userClothingItems = entry.getValue();
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            UserClothingDTO userClothingDTO = new UserClothingDTO();
            userClothingDTO.setUser(userDto);
            userClothingDTO.setClothingItemsForSale(clothingItemDTO(userClothingItems));
            userClothingDTOList.add(userClothingDTO);
        }
        return userClothingDTOList;
    }


    private List<String> getItemCategoryList(List<ClothingItem> clothingItems, ItemCategory type) {
        return clothingItems.stream()
                .filter(item -> item.getItemCategory().equals(type))
                .map(ClothingItem::getDriveId)
                .collect(Collectors.toList());
    }

    private List<String> selectRandomItems(List<String> itemList, int numItems) {
        // Shuffle the list randomly
        Collections.shuffle(itemList, new Random());

        // Select the specified number of items or all if fewer items are available
        return itemList.subList(0, Math.min(itemList.size(), numItems));
    }

}
