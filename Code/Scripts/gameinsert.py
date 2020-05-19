import json
import requests

base_url = "https://api.rawg.io/api/"
grid_url = "http://localhost:8080/grid/"

url = base_url + "developers"
count = 0
while count < 100 and url:
	print(url)
	response = requests.get(url).json()
	for dev in response["results"]:
		requests.post(grid_url + "developer", data={"name": dev["name"]})
		count += 1
	url = response["next"]

## Insert genres
url = base_url + "genres"
count = 0
while count < 50 and url:
	print(url)
	response = requests.get(url).json()
	for genre in response["results"]:
		genre_info = requests.get(base_url + "genres/" + str(genre["id"]))
		requests.post(grid_url + "genre", data={"name": genre_info["name"], "description": genre_info["description"]})
		count += 1
	url = response["next"]

## Insert publishers
url = base_url + "publishers"
count = 0
while count < 100 and url:
	print(url)
	response = requests.get(url).json()
	for genre in response["results"]:
		pub_info = requests.get(base_url + "publishers/" + str(genre["id"]))
		requests.post(grid_url + "publisher", data={"name": pub_info["name"], "description": pub_info["description"]})
		count += 1
	url = response["next"]

## Insert games
url = base_url + "games"
count = 0
while count < 300:
	print(url)
	response = requests.get(url).json()
	for genre in response["results"]:
		game_info = requests.get(base_url + "games/" + str(genre["id"]))
		requests.post(grid_url + "game", data={
			"name": game_info["name"],
			"description": game_info["description"],
			"releaseDate": game_info["released"],
			"coverUrl": game_info["background_image"],
			"developers": [dev["name"] for dev in game_info["developers"]],
			"gameGenres": [genre["name"] for genre in game_info["genres"]],
			"publisher": game_info["publishers"][0]["name"]
		})
	url = response["next"]
