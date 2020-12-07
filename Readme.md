My AI Gin Rummy Player that win against simple player at 80% win rate.  

# Algorithm used and functionality  

## Draw a card  
An ensembled method created by adjusting hyperparameters of:  
- Draw a card with high deadwood score  
- Draw a card that form melds in the current user's hand  

## Discard a card  
An ensembled method created by adjusting hyperparameters of:  
- Discard a card not in meld with highest deadwood score  
- Discard a card with low potential to form melds in the future.  
- Discard a card with low "score," with score is determined with the other unmelded cards in hand with same rank/suit.  
- Higher the punishment on high deadwood.  

## Knock 
An ensembled method created by adjusting hyperparameters of:
- Knock as soon as possible.  
- Knock only when user has gin (deadwood = 0).  

## Algorithms
- Grid search and Genetic algorithms used to tune the hyperparameters. How good a player is determined by win rate against simple players. 