import pandas as pd

game_level_data = pd.read_csv("../game_level_data.csv")
round_level_data = pd.read_csv("../round_level_data.csv")

print(game_level_data.columns)

#get all games between player 0 and player 1 
game_p0_vs_p1 = game_level_data[ (game_level_data['playerID'] == 0) & (game_level_data['opponentID'] == 1) ]


#get the game with game id 10
game_with_id_10 = game_level_data[game_level_data['gameID'] == 10]
print(game_with_id_10)

#get all round with game id 10
round_with_id_10 = round_level_data[round_level_data['gameID'] == 10]
print(round_with_id_10)