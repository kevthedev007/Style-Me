from dress_methods import get_dress_recommendations
from shirt_methods import get_trousers_recommendations 
from skirt_methods import get_skirt_recommendations
from flask import Flask, request, jsonify

app = Flask(__name__)


def get_recommendations(skirts=None, shirts=None, bags=None, trousers=None, shoes=None, dresses=None):
    skirt_list=get_skirt_recommendations(skirts=skirts, shirts=shirts, shoes=shoes, bags=bags)
    trouser_list=get_trousers_recommendations(shirts=shirts, trousers=trousers, shoes=shoes, bags=bags )
    dress_list=get_dress_recommendations(dresses=dresses, shoes=shoes, bags=bags)
    full_list=skirt_list + trouser_list + dress_list
    return full_list



# Define the route for the recommendation API
@app.route('/recommendations', methods=['POST'])
def recommendations():
    # Parse input parameters from the query string
      data=request.json
      skirts = data.get('skirts')
      shirts = data.get('shirts')
      bags = data.get('bags')
      trousers = data.get('trousers')
      shoes = data.get('shoes')
      dresses = data.get('dresses')
      

      # Call the get_recommendations method with the parsed parameters
      recommendation_list = get_recommendations(
          skirts=skirts,
          shirts=shirts,
          bags=bags,
          trousers=trousers,
          shoes=shoes,
          dresses=dresses
      )

      # Return the recommendations as a JSON response
      return jsonify({"recommendations": recommendation_list})
    

if __name__ == '__main__':
    app.run(debug=True)







# bags_folder_id=['1XeDnomLgO67rHf1WEO6H6zpbiOQEI17w', '1Pjo2GwcmkynK51HXoF_4RuF3xH56gpZ8', '1inpBYiTRfkdrZa-QQstQqUjRACiNhXT8']
# dress_folder_id=['18LILOuFxRbGhm-V2B3KAsu3t0UZ2Z0Dg', '1YDgD8zzGeozOI_ThmNrLkqaN8XKJQ7IW', '1RnH9MpeeKOlOAWl6uNPB8DGUNYD-w9q-', '1cV0UbLFczAZ7qRQfoAnZ8tKbW2ECfnT3']
# shoes_folder_id=['16gwN8wLqG-91GNZRUtm9O9jQzSjwMkun', '1KCm0lXU8bS6Hl-XhUcgZK7bEdrbFj5yG', '1LLMdDbuZ5UK_PdYTEQyM8iGwYLuERwg3']
# b=dresses_with_shoes(shoes_folder_id=shoes_folder_id, dress_folder_id=dress_folder_id)

# skirts_folder_id=['1JHeW1pCjXsDKG7mKJ684oGIVEj2VdD_d', '1kCvlmp-TyilxgaayI_otdXdk3Fgj5Leb', '1nsWVL7VL7s-toX78bDqeNnw_nmqw09Hs', '1QY7tFqGtbNgBQLYLQl5FjuzuLxkEGT9W', '1wN_dSZXLBH05n-BhPIFAhi709U-yGus6']
# bags_folder_id=['1XeDnomLgO67rHf1WEO6H6zpbiOQEI17w', '1Pjo2GwcmkynK51HXoF_4RuF3xH56gpZ8', '1inpBYiTRfkdrZa-QQstQqUjRACiNhXT8']
# shirts_folder_id=['1QBUbNmF0zD69SNRkDPfLmY1eX2-Bk55-', '1p84-D9I_1TWnZcPVzL7QUAqliVun1lwo', '1R9T1dRAd7yV_Z1AA1nNvuiXfDU9wpPQv', '1uqxmzuqBn47h-PkajE0CqoBLLDCIZCa4']
# trousers_folder_id=["1FrqgR63Tq6kJWrsn9Ft9iBoizDtrN3U-", "1cbzTEO9JT3yIWhGwEQVgnjDHyhn6jd9L", "1cE3Yqo4NsSm6DSTzcxvZwlOgaBOiu-GZ", "1OXPHSXYM_TDxm7aY97cF8g6Z8Mz8A6Wl", "1JbR0lDpenJODww4IxWzpIt9aiGSEx4WV"]
# shoes_folder_id=['16gwN8wLqG-91GNZRUtm9O9jQzSjwMkun', '1KCm0lXU8bS6Hl-XhUcgZK7bEdrbFj5yG', '1LLMdDbuZ5UK_PdYTEQyM8iGwYLuERwg3']
# a=get_recommendations(shirts=shirts_folder_id,  skirts=skirts_folder_id, bags=bags_folder_id, shoes=shoes_folder_id, trousers=trousers_folder_id)
# print(len(a))

