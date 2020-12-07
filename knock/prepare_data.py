#normalize the data

import pandas as pd 
#rankOfMeld1, isRun1, isRank1, length1, rankOfMeld2, isRun2, isRank2, length2, rankOfMeld3, isRun3, isRank3, length3, selfDeadwoodPoints, numTurn, finalScore

#Read train data
unprocessed_data = pd.read_csv("processed_raw_data.txt", names = ["rankOfMeld1", "isRun1", "isRank1", "length1", "rankOfMeld2", "isRun2", "isRank2", "length2", "rankOfMeld3", "isRun3", "isRank3", "length3", "selfDeadwoodPoints", "numTurn", "multiOppScore", "finalScore"])

#unprocessed_data.to_csv("unprepared_data.csv", index = False)
#print("num columns", len(data.columns))
data = unprocessed_data.copy()
#normalize selfDeadwoodPoints, numTurn, finalScore
data['selfDeadwoodPoints'] = data['selfDeadwoodPoints'].map(lambda x: x / max(data['selfDeadwoodPoints']))
data['numTurn'] = data['numTurn'].map(lambda x: x / max(data['numTurn']))

minFinalScore = min(data['finalScore'])
maxFinalScore = max(data['finalScore'])
data['finalScore'] = data['finalScore'].map(lambda x: x + abs(minFinalScore))
data['finalScore'] = data['finalScore'].map(lambda x: x / maxFinalScore)

maxRank = 12
data['rankOfMeld1'] = data['rankOfMeld1'].map(lambda x: x / maxRank)
data['rankOfMeld2'] = data['rankOfMeld2'].map(lambda x: x / maxRank)
data['rankOfMeld3'] = data['rankOfMeld3'].map(lambda x: x / maxRank)

normalize_length = 5 #divide by 5 to normalize the length
data['length1'] = data['length1'].map(lambda x: x / normalize_length)
data['length2'] = data['length2'].map(lambda x: x / normalize_length)
data['length3'] = data['length3'].map(lambda x: x / normalize_length)

data['multiOppScore'] = data['multiOppScore'].map(lambda x: (x + abs(min(data['multiOppScore'])) ) / max(data['multiOppScore']) )

#extract csv file
#data.to_csv("data.csv", index = False)





def getFinalScore(normalized_score):
    return normalized_score * maxFinalScore - abs(minFinalScore)
prediction = pd.read_csv("prediction_weka.txt", names = ['MultilayerPerceptron', 'MLPRegressor'])
prediction['MultilayerPerceptron'] = prediction['MultilayerPerceptron'].map(lambda x: getFinalScore(x))
prediction['MLPRegressor'] = prediction['MLPRegressor'].map(lambda x: getFinalScore(x))

pd.concat([unprocessed_data,prediction], axis = 1).to_csv("data_with_prediction_weka.csv", index = False)

print("Done!")