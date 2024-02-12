import torch
from PIL import Image, ImageTk
import os
from io import BytesIO
import io
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


def dresses_with_bags_and_shoes(dress_folder_id, bags_folder_id, shoes_folder_id):
    best_matches=[]
    dress_images, dress_ids=get_images_from_drive(dress_folder_id)
    bags_images, bags_ids=get_images_from_drive(bags_folder_id)
    shoes_images, shoes_ids=get_images_from_drive(shoes_folder_id)
    dress_tensors=[preprocess_image(image) for image in dress_images]
    bag_tensors=[preprocess_image(image) for image in bags_images]
    shoe_tensors=[preprocess_image(image) for image in shoes_images]
    with torch.no_grad():
        features_list1=[resnet18(input_tensor).squeeze().numpy() for input_tensor in dress_tensors]
        features_list2=[resnet18(input_tensor).squeeze().numpy() for input_tensor in bag_tensors]
        features_list3=[resnet18(input_tensor).squeeze().numpy() for input_tensor in shoe_tensors]
    for i in range(len(dress_images)):
        hist1 = calculate_color_histogram(dress_images[i])
        for j in range(len(shoes_images)):
            hist2 = calculate_color_histogram(shoes_images[j])
            feature_similarity = calculate_cosine_similarity(features_list1[i], features_list2[j])
            color_similarity  = calculate_color_similarity(hist1, hist2)
            similarity_score = 0.5 * feature_similarity + 0.5 * color_similarity
            for k in range(len(bags_images)):
                hist3 = calculate_color_histogram(bags_images[k])
            
                bag_similarity = calculate_cosine_similarity(features_list2[j], features_list3[k])
                color_bag_similarity  = calculate_color_similarity(hist2, hist3)
                bag_similarity_score = 0.5 * bag_similarity + 0.5 * color_bag_similarity
                feature_similarity_two = calculate_cosine_similarity(features_list1[i], features_list3[k])
                color_similarity_two  = calculate_color_similarity(hist1, hist3)
                similarity_score_two = 0.5 * feature_similarity_two + 0.5 * color_similarity_two
                final_similarity_score= 0.4*similarity_score + 0.2 * bag_similarity_score + 0.4*similarity_score_two
                best_matches.append([i,j,k,final_similarity_score])
    best_matches.sort(key=lambda x: x[3], reverse=True)
    best_match_five=best_matches[:5]
    recommended_dresses=get_id(best_match_five, [dress_ids, bags_ids, shoes_ids])
    # plt.subplot(1, 3, 1)
    # plt.imshow(dress_images[best_match[0]])
    # plt.title("Top")
    # plt.subplot(1, 3, 2)
    # plt.imshow(shoes_images[best_match[1]])
    # plt.subplot(1, 3, 3)
    # plt.imshow(bags_images[best_match[2]])
    # plt.show()
    return recommended_dresses 


def dresses_with_bags(dress_folder_id, bags_folder_id):
    best_matches=[]
    dress_images, dress_ids=get_images_from_drive(dress_folder_id)
    bags_images, bags_ids=get_images_from_drive(bags_folder_id)
    dress_tensors=[preprocess_image(image) for image in dress_images]
    bag_tensors=[preprocess_image(image) for image in bags_images]
    with torch.no_grad():
        features_list1=[resnet18(input_tensor).squeeze().numpy() for input_tensor in dress_tensors]
        features_list2=[resnet18(input_tensor).squeeze().numpy() for input_tensor in bag_tensors]
    for i in range(len(dress_images)):
        hist1 = calculate_color_histogram(dress_images[i])
        for j in range(len(bags_images)):
            hist2 = calculate_color_histogram(bags_images[j])
            feature_similarity = calculate_cosine_similarity(features_list1[i], features_list2[j])
            color_similarity  = calculate_color_similarity(hist1, hist2)
            final_similarity_score = 0.5 * feature_similarity + 0.5 * color_similarity
            best_matches.append([i,j,final_similarity_score])
    best_matches.sort(key=lambda x: x[2], reverse=True)
    best_match_five=best_matches[:5]
    recommended_dresses=get_id(best_match_five, [dress_ids, bags_ids])
    best_match=best_matches[6]
    # plt.subplot(1, 2, 1)
    # plt.imshow(dress_images[best_match[0]])
    # plt.title("Top")
    # plt.subplot(1, 2, 2)
    # plt.imshow(bags_images[best_match[1]])
    # plt.show()
    return recommended_dresses

def dresses_with_shoes(dress_folder_id, shoes_folder_id):
    best_matches=[]
    dress_images, dress_ids=get_images_from_drive(dress_folder_id)
    shoes_images, shoes_ids=get_images_from_drive(shoes_folder_id)
    dress_tensors=[preprocess_image(image) for image in dress_images]
    shoe_tensors=[preprocess_image(image) for image in shoes_images]
    with torch.no_grad():
        features_list1=[resnet18(input_tensor).squeeze().numpy() for input_tensor in dress_tensors]
        features_list2=[resnet18(input_tensor).squeeze().numpy() for input_tensor in shoe_tensors]
    for i in range(len(dress_images)):
        hist1 = calculate_color_histogram(dress_images[i])
        for j in range(len(shoes_images)):
            hist2 = calculate_color_histogram(shoes_images[j])
            feature_similarity = calculate_cosine_similarity(features_list1[i], features_list2[j])
            color_similarity  = calculate_color_similarity(hist1, hist2)
            final_similarity_score = 0.5 * feature_similarity + 0.5 * color_similarity
            best_matches.append([i,j,final_similarity_score])
    best_matches.sort(key=lambda x: x[2], reverse=True)
    best_match_five=best_matches[:5]
    recommended_dresses=get_id(best_match_five, [dress_ids, shoes_ids])
    # best_match=best_matches[8]
    # plt.subplot(1, 2, 1)
    # plt.imshow(dress_images[best_match[0]])
    # plt.title("Top")
    # plt.subplot(1, 2, 2)
    # plt.imshow(shoes_images[best_match[1]])
    # plt.show()
    return recommended_dresses



def get_dress_recommendations(dresses, shoes, bags):
    dress_list=[]
    if dresses and shoes and bags:
        dress_list=dresses_with_bags_and_shoes(dress_folder_id=dresses, bags_folder_id=bags, shoes_folder_id=shoes)
    elif dresses and bags and not shoes:
        dress_list=dresses_with_bags(dress_folder_id=dresses, bags_folder_id=bags)
    elif dresses and shoes and not bags:
        dress_list=dresses_with_shoes(dress_folder_id=dresses, shoes_folder_id=shoes)
    
    return dress_list

# bags_folder_id=['1XeDnomLgO67rHf1WEO6H6zpbiOQEI17w', '1Pjo2GwcmkynK51HXoF_4RuF3xH56gpZ8', '1inpBYiTRfkdrZa-QQstQqUjRACiNhXT8']
# dress_folder_id=['18LILOuFxRbGhm-V2B3KAsu3t0UZ2Z0Dg', '1YDgD8zzGeozOI_ThmNrLkqaN8XKJQ7IW', '1RnH9MpeeKOlOAWl6uNPB8DGUNYD-w9q-', '1cV0UbLFczAZ7qRQfoAnZ8tKbW2ECfnT3']
# shoes_folder_id=['16gwN8wLqG-91GNZRUtm9O9jQzSjwMkun', '1KCm0lXU8bS6Hl-XhUcgZK7bEdrbFj5yG', '1LLMdDbuZ5UK_PdYTEQyM8iGwYLuERwg3']
# b=dresses_with_shoes(shoes_folder_id=shoes_folder_id, dress_folder_id=dress_folder_id)
# a=dresses_with_bags_and_shoes(bags_folder_id='1i46TTlXXD5eSQPNMxkBb7QQgGdOJfOIM', dress_folder_id='1jJmfz6CbA6ljIvi15PGdXHz-FoCrJ5H-', shoes_folder_id='18EMeELFWs-IzVcwKe1PyMxMmRolz_86e')
# print(a)
# b=dresses_with_bags(bags_folder_id='1i46TTlXXD5eSQPNMxkBb7QQgGdOJfOIM', dress_folder_id='1jJmfz6CbA6ljIvi15PGdXHz-FoCrJ5H-')   
# print(b)             
      
        
        
    
        
        
        
    
        






    


