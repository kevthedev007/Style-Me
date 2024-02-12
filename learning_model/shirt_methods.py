import torch
from PIL import Image, ImageTk
import os
from io import BytesIO
import io
from imp import reload
import matplotlib.pyplot as plt
import torchvision.models as models
from preprocess import preprocess_image
import torchvision.transforms as transforms
from get_image_from_folder import get_images_from_drive
from calculate_similarities import calculate_color_similarity, calculate_cosine_similarity, calculate_color_histogram


# Load the pre-trained ResNet model with 18 layers
resnet18 = models.resnet18(pretrained=True)
# Alternatively, you can load other versions of ResNet, such as resnet34, resnet50, etc.

# Set the model to evaluation mode
resnet18.eval()


def get_id(best_matches, image_id):
    final_lis=[]
    for i in best_matches:
            lis=[]
            for j in range(len(i)-1):
                  a=image_id[j][i[j]]
                  lis.append(a)
            final_lis.append(lis)
    return final_lis


def shirt_and_trousers_only(shirts_folder_id, trousers_folder_id):
    best_matches=[]
    shirt_images, shirt_ids=get_images_from_drive(shirts_folder_id)
    trousers_images, trouser_ids=get_images_from_drive(trousers_folder_id)
    shirt_tensors=[preprocess_image(image) for image in shirt_images]
    trouser_tensors=[preprocess_image(image) for image in trousers_images]
    with torch.no_grad():
        features_list1=[resnet18(input_tensor).squeeze().numpy() for input_tensor in shirt_tensors]
        features_list2=[resnet18(input_tensor).squeeze().numpy() for input_tensor in trouser_tensors]
    for i in range(len(shirt_images)):
        hist1 = calculate_color_histogram(shirt_images[i])
        for j in range(len(trousers_images)):
            hist2 = calculate_color_histogram(trousers_images[j])
            feature_similarity = calculate_cosine_similarity(features_list1[i], features_list2[j])
            color_similarity  = calculate_color_similarity(hist1, hist2)
            final_similarity_score = 0.5 * feature_similarity + 0.5 * color_similarity
            best_matches.append([i,j,final_similarity_score])
    best_matches.sort(key=lambda x: x[2], reverse=True)
    best_match_five=best_matches[:5]
    recommended_shirts=get_id(best_match_five, [shirt_ids, trouser_ids])
    return recommended_shirts

def shirts_and_trousers_with_bags_and_shoes(shirts_folder_id, trousers_folder_id, bags_folder_id, shoes_folder_id):
    best_matches=[]
    shirt_images, shirt_ids=get_images_from_drive(shirts_folder_id)
    trousers_images, trouser_ids=get_images_from_drive(trousers_folder_id)
    bags_images, bags_ids=get_images_from_drive(bags_folder_id)
    shoes_images, shoes_ids=get_images_from_drive(shoes_folder_id)
    shirt_tensors=[preprocess_image(image) for image in shirt_images]
    trouser_tensors=[preprocess_image(image) for image in trousers_images]
    bag_tensors=[preprocess_image(image) for image in bags_images]
    shoe_tensors=[preprocess_image(image) for image in shoes_images]
    with torch.no_grad():
        features_list1=[resnet18(input_tensor).squeeze().numpy() for input_tensor in shirt_tensors]
        features_list2=[resnet18(input_tensor).squeeze().numpy() for input_tensor in trouser_tensors]
        features_list3=[resnet18(input_tensor).squeeze().numpy() for input_tensor in bag_tensors]
        features_list4=[resnet18(input_tensor).squeeze().numpy() for input_tensor in shoe_tensors]
    for i in range(len(shirt_images)):
        hist1 = calculate_color_histogram(shirt_images[i])
        for j in range(len(trousers_images)):
            hist2 = calculate_color_histogram(trousers_images[j])
            feature_similarity = calculate_cosine_similarity(features_list1[i], features_list2[j])
            color_similarity  = calculate_color_similarity(hist1, hist2)
            shirt_trouser_similarity_score = 0.5 * feature_similarity + 0.5 * color_similarity
            for k in range(len(shoes_images)):
                hist3 = calculate_color_histogram(shoes_images[k])
                trouser_shoe_similarity = calculate_cosine_similarity(features_list2[j], features_list4[k])
                color_trouser_shoe_similarity  = calculate_color_similarity(hist2, hist3)
                final_trouser_shoe_similarity = 0.5 * trouser_shoe_similarity + 0.5 * color_trouser_shoe_similarity
                for l in range(len(bags_images)):
                    hist4 = calculate_color_histogram(bags_images[k])
                    bag_shoe_similarity = calculate_cosine_similarity(features_list3[k], features_list4[l])
                    color_bag_shoe_similarity  = calculate_color_similarity(hist3, hist4)
                    final_bag_shoe_similarity = 0.5 * bag_shoe_similarity + 0.5 * color_bag_shoe_similarity
                    final_similarity_score= 0.4*shirt_trouser_similarity_score + 0.2 * final_bag_shoe_similarity + 0.4*final_trouser_shoe_similarity
                    best_matches.append([i,j,k,l,final_similarity_score])
    best_matches.sort(key=lambda x: x[4], reverse=True)
    best_match_five=best_matches[:5]
    recommended_shirts=get_id(best_match_five, [shirt_ids, trouser_ids, shoes_ids, bags_ids])
    # best_match=best_matches[3]
    # print(len(best_matches))
    # plt.subplot(1, 4, 1)
    # plt.imshow(shirt_images[best_match[0]])
    # plt.title("Top")
    # plt.subplot(1, 4, 2)
    # plt.imshow(trousers_images[best_match[1]])
    # plt.subplot(1, 4, 3)
    # plt.imshow(shoes_images[best_match[2]])
    # plt.subplot(1, 4, 4)
    # plt.imshow(bags_images[best_match[3]])
    # plt.show()
    return recommended_shirts



def shirts_and_trousers_with_bags(shirts_folder_id, trousers_folder_id, bags_folder_id):
    best_matches=[]
    shirt_images, shirt_ids=get_images_from_drive(shirts_folder_id)
    trousers_images, trouser_ids=get_images_from_drive(trousers_folder_id)
    bags_images, bags_ids=get_images_from_drive(bags_folder_id)
    shirt_tensors=[preprocess_image(image) for image in shirt_images]
    trouser_tensors=[preprocess_image(image) for image in trousers_images]
    bag_tensors=[preprocess_image(image) for image in bags_images]
    with torch.no_grad():
        features_list1=[resnet18(input_tensor).squeeze().numpy() for input_tensor in shirt_tensors]
        features_list2=[resnet18(input_tensor).squeeze().numpy() for input_tensor in trouser_tensors]
        features_list3=[resnet18(input_tensor).squeeze().numpy() for input_tensor in bag_tensors]
    for i in range(len(shirt_images)):
        hist1 = calculate_color_histogram(shirt_images[i])
        for j in range(len(trousers_images)):
            hist2 = calculate_color_histogram(trousers_images[j])
            feature_similarity = calculate_cosine_similarity(features_list1[i], features_list2[j])
            color_similarity  = calculate_color_similarity(hist1, hist2)
            shirt_trouser_similarity_score = 0.5 * feature_similarity + 0.5 * color_similarity
            for k in range(len(bags_images)):
                hist3 = calculate_color_histogram(bags_images[k])
                trouser_bag_feature_similarity = calculate_cosine_similarity(features_list2[j], features_list3[k])
                trouser_bag_color_similarity  = calculate_color_similarity(hist2, hist3)
                trouser_bag_similarity_score = 0.5 * trouser_bag_feature_similarity + 0.5 * trouser_bag_color_similarity
                final_score=0.6* shirt_trouser_similarity_score + 0.4 * trouser_bag_similarity_score
                best_matches.append([i,j,k,final_score])
    best_matches.sort(key=lambda x: x[3], reverse=True)
    best_match_five=best_matches[:5]
    recommended_shirts=get_id(best_match_five, [shirt_ids, trouser_ids, bags_ids])
    # best_match=best_matches[5]
    # print(len(best_matches))
    # plt.subplot(1, 3, 1)
    # plt.imshow(shirt_images[best_match[0]])
    # plt.title("Top")
    # plt.subplot(1, 3, 2)
    # plt.imshow(trousers_images[best_match[1]])
    # plt.subplot(1, 3, 3)
    # plt.imshow(bags_images[best_match[2]])
    # plt.show()
    return recommended_shirts



def shirts_and_trousers_with_shoes(shirts_folder_id, trousers_folder_id, shoes_folder_id):
    best_matches=[]
    shirt_images, shirt_ids=get_images_from_drive(shirts_folder_id)
    trousers_images, trouser_ids=get_images_from_drive(trousers_folder_id)
    shoes_images, shoes_ids=get_images_from_drive(shoes_folder_id)
    shirt_tensors=[preprocess_image(image) for image in shirt_images]
    trouser_tensors=[preprocess_image(image) for image in trousers_images]
    shoe_tensors=[preprocess_image(image) for image in shoes_images]
    with torch.no_grad():
        features_list1=[resnet18(input_tensor).squeeze().numpy() for input_tensor in shirt_tensors]
        features_list2=[resnet18(input_tensor).squeeze().numpy() for input_tensor in trouser_tensors]
        features_list3=[resnet18(input_tensor).squeeze().numpy() for input_tensor in shoe_tensors]
    for i in range(len(shirt_images)):
        hist1 = calculate_color_histogram(shirt_images[i])
        for j in range(len(trousers_images)):
            hist2 = calculate_color_histogram(trousers_images[j])
            feature_similarity = calculate_cosine_similarity(features_list1[i], features_list2[j])
            color_similarity  = calculate_color_similarity(hist1, hist2)
            shirt_trouser_similarity_score = 0.5 * feature_similarity + 0.5 * color_similarity
            for k in range(len(shoes_images)):
                hist3 = calculate_color_histogram(shoes_images[k])
                trouser_bag_feature_similarity = calculate_cosine_similarity(features_list2[j], features_list3[k])
                trouser_bag_color_similarity  = calculate_color_similarity(hist2, hist3)
                trouser_bag_similarity_score = 0.5 * trouser_bag_feature_similarity + 0.5 * trouser_bag_color_similarity
                final_score=0.6* shirt_trouser_similarity_score + 0.4 * trouser_bag_similarity_score
                best_matches.append([i,j,k,final_score])
    best_matches.sort(key=lambda x: x[3], reverse=True)
    # best_match=best_matches[10]
    best_match_five=best_matches[:5]
    recommended_shirts=get_id(best_match_five, [shirt_ids, trouser_ids, shoes_ids])
    # print(len(best_matches))
    # plt.subplot(1, 3, 1)
    # plt.imshow(shirt_images[best_match[0]])
    # plt.title("Top")
    # plt.subplot(1, 3, 2)
    # plt.imshow(trousers_images[best_match[1]])
    # plt.subplot(1, 3, 3)
    # plt.imshow(shoes_images[best_match[2]])
    # plt.show()
    return recommended_shirts


def get_trousers_recommendations(shirts, trousers, shoes, bags):
    trouser_list=[]
    if shirts and trousers and shoes and bags:
        trouser_list=shirts_and_trousers_with_bags_and_shoes(shirts_folder_id=shirts, trousers_folder_id=trousers, bags_folder_id=bags, shoes_folder_id=shoes)
    elif shirts and trousers and bags and not shoes:
        trouser_list=shirts_and_trousers_with_bags(shirts_folder_id=shirts, trousers_folder_id=trousers, bags_folder_id=bags)
    elif shirts and trousers and shoes and not bags:
        trouser_list=shirts_and_trousers_with_shoes(shirts_folder_id=shirts, trousers_folder_id=trousers, shoes_folder_id=shoes)
    elif shirts and trousers and not bags and not shoes:
        trouser_list=shirt_and_trousers_only(shirts_folder_id=shirts, trousers_folder_id=trousers)
    return trouser_list
    


# bags_folder_id=['1XeDnomLgO67rHf1WEO6H6zpbiOQEI17w', '1Pjo2GwcmkynK51HXoF_4RuF3xH56gpZ8', '1inpBYiTRfkdrZa-QQstQqUjRACiNhXT8']
# shirts_folder_id=['1QBUbNmF0zD69SNRkDPfLmY1eX2-Bk55-', '1p84-D9I_1TWnZcPVzL7QUAqliVun1lwo', '1R9T1dRAd7yV_Z1AA1nNvuiXfDU9wpPQv', '1uqxmzuqBn47h-PkajE0CqoBLLDCIZCa4']
# trousers_folder_id=['1FrqgR63Tq6kJWrsn9Ft9iBoizDtrN3U-', '1cbzTEO9JT3yIWhGwEQVgnjDHyhn6jd9L', '1cE3Yqo4NsSm6DSTzcxvZwlOgaBOiu-GZ', '1OXPHSXYM_TDxm7aY97cF8g6Z8Mz8A6Wl', '1JbR0lDpenJODww4IxWzpIt9aiGSEx4WV']
# shoes_folder_id=['16gwN8wLqG-91GNZRUtm9O9jQzSjwMkun', '1KCm0lXU8bS6Hl-XhUcgZK7bEdrbFj5yG', '1LLMdDbuZ5UK_PdYTEQyM8iGwYLuERwg3']

# a=shirts_and_trousers_with_bags_and_shoes(shirts_folder_id=shirts_folder_id, trousers_folder_id=trousers_folder_id, shoes_folder_id=shoes_folder_id, bags_folder_id=bags_folder_id)
# print(a)
# a=shirts_and_trousers_with_bags(shirts_folder_id=shirts_folder_id, trousers_folder_id=trousers_folder_id, bags_folder_id=bags_folder_id)
# print(a)
# a=shirts_and_trousers_with_shoes(shirts_folder_id=shirts_folder_id, trousers_folder_id=trousers_folder_id, shoes_folder_id=shoes_folder_id)
# print(a)