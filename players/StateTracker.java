package players;

import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;

import ginrummy.Card;
import util.OurUtil;

public class StateTracker implements Serializable {

    protected static final long serialVersionUID = 1L;

    public static final int DIMENSION = 2;
    public static final int STATE = 0; // value at index indicates state that is one of the below
    public static final int PROB_IN_STOCK = 1; // value at index 1 indicates probabilities about where the card is

    protected double[][][] matrix = new double[Card.NUM_SUITS][Card.NUM_RANKS][DIMENSION];

    public static final double KNOW_WHERE_IT_IS = -1.0; //“we know exactly where the card is. Check the STATE index for that information.”
    public static final double UNKNOWN = 0.0; //we dont know about this card
    public static final double SELF_FROM_STOCK = 1.0; //self has that card, came from stock
    public static final double SELF_FROM_DISCARD = 2.0; // self has that card, came from discard
    public static final double OPP_FROM_STOCK = 3.0; //not used - would mean opp has that card but it didn’t come from discard - but we can’t know this
    public static final double OPP_FROM_DISCARD = 4.0; //opp has that card (came from discard)
    public static final double SELF_TOP_DISCARD = 5.0; //self discard it, and it’s currently the face-up discard
    public static final double SELF_BURIED_DISCARD = 6.0; //self discard it, and it’s now buried
    public static final double OPP_TOP_DISCARD = 7.0; // opp discard it, and it’s currently the face-up discard
    public static final double OPP_BURIED_DISCARD = 8.0; // opp discard it, and it’s now buried
    public static final double SELF_FROM_START = 9.0; // self took the initial face-up card and opp did not have a chance to pick it up  
    public static final double OPP_FROM_START = 10.0; // opp tool the initial face-up card and self did not have a chance to pick it up
    
    public static final int HAND_SIZE = 10;
    public static final double INITIAL_PROB =  (double)(Card.NUM_CARDS - 2 * HAND_SIZE - 1)/(Card.NUM_CARDS - HAND_SIZE - 1);  //32 cards in stock, 10 cards in opponent's hand, 1 card that will be face-up

    protected transient Stack <Card> discardCards = new Stack<>();  //stack of discard cards
    protected Stack<String> discardCardsOfString = new Stack<>();  // for serialization 

    protected boolean printSTATES = false;
    protected boolean printPROBS = false;
    protected static final boolean ASC = true;
    protected static final boolean DESC = false;
    public static boolean TESTING = false;

    protected int turnsTaken;  // count the number of turns takn
    protected int numSelfTurnsTaken;  // count the number of self turns taken
    protected int numDrawnAll; // count the number of cards drawn by either player (almost equal to the number of turns passed but need to be careful about the first face-up card)
    protected int numDrawnSelf; // count the number of cards drawn by self
    protected int numDeckCards;  // the number of cards left in the deck
    protected int numOppCardsUnknown; // count the number of unknown cards in opponent's hand
    protected ArrayList<Integer> numDeckCards_t;  // the history of numbers of cards left in the deck
    protected ArrayList<Integer> numOppCardsUnknown_t;  // the history of numbers of unknown cards in opponent's hand
    protected transient ArrayList<Card> oppCardsKnown;
    protected ArrayList<String> oppCardsKnownOfString = new ArrayList<String>();  // for serialization
    protected static Random RANDOM = new Random(1);
    protected int playerNum;
    protected int startingPlayerNum;
    
    protected ParamList params;
    
    public StateTracker(ParamList params) {
        this.params = params;
    }

    public boolean hasDifferentParamList(ParamList otherParams) {
        return this.params != otherParams;
    }
    
	public void updateFromStartGame(int playerNum, int startingPlayerNum, Card[] cards){
        this.playerNum = playerNum;
		this.startingPlayerNum = startingPlayerNum;
        
        matrix = new double[Card.NUM_SUITS][Card.NUM_RANKS][DIMENSION];
        discardCards.clear();
        this.turnsTaken = 0;
        this.numSelfTurnsTaken = 0;
        this.numDrawnAll = 0;
        this.numDrawnSelf = 0;
        this.numDeckCards = Card.NUM_CARDS - 2 * HAND_SIZE - 1; //not sure if 1 should be subracted here for the first face-up card
        this.numOppCardsUnknown = HAND_SIZE;
        this.numDeckCards_t = new ArrayList<Integer>();
        this.numDeckCards_t.add(this.numDeckCards);
        this.numOppCardsUnknown_t = new ArrayList<Integer>();
        this.numOppCardsUnknown_t.add(this.numOppCardsUnknown);
        oppCardsKnown = new ArrayList<Card>(HAND_SIZE/2);
		for (Card card : cards){
            matrix[card.getSuit()][card.getRank()][STATE] = SELF_FROM_STOCK;
            matrix[card.getSuit()][card.getRank()][PROB_IN_STOCK] = KNOW_WHERE_IT_IS;
        }
        
        for(int i = 0; i < Card.NUM_SUITS; i++){
            for(int j = 0; j < Card.NUM_RANKS; j++){
                if(matrix[i][j][PROB_IN_STOCK] != KNOW_WHERE_IT_IS){
                    matrix[i][j][PROB_IN_STOCK] = INITIAL_PROB;
                }
            }
        }        
        if(this.printSTATES) displaySTATES();
        if(this.printPROBS) displayPROBS();
    }

    public void updateFromWillDrawFaceUpCard(Card card, boolean willDraw){        
        // this is for the first top-faceup card that comes from deck not from either player
        if(discardCards.size() == 0){
            matrix[card.getSuit()][card.getRank()][PROB_IN_STOCK] = KNOW_WHERE_IT_IS;
            if(this.startingPlayerNum != this.playerNum){
                turnsTaken++;
            }
            if(!willDraw){
                matrix[card.getSuit()][card.getRank()][STATE] = SELF_TOP_DISCARD;
                turnsTaken++;
                numSelfTurnsTaken++;
            }
            else{
                if(this.startingPlayerNum == this.playerNum) // added this if statement because when the opponent starts the game, rejects the initial face-up card and our player takes the card, STATE for the card should be SELF_FROM_DISCARD
                   matrix[card.getSuit()][card.getRank()][STATE] = SELF_FROM_START;
            }   
            discardCards.push(card);
        }
    }

    public void updateFromReportDraw(int playerNum, Card drawCard){
        boolean isOurTurn = (playerNum == this.playerNum);
        boolean isFromStock;
        Card notDrawnCard = null;
       //our turn 
       if (isOurTurn) {
        if(drawCard == discardCards.peek()){ //our player drew from the discard pile
               isFromStock = false;
               if(matrix[drawCard.getSuit()][drawCard.getRank()][STATE] != SELF_FROM_START)
                   matrix[drawCard.getSuit()][drawCard.getRank()][STATE] = SELF_FROM_DISCARD;
               if(this.numDrawnAll == 0 && this.startingPlayerNum != this.playerNum)
                   notDrawnCard = drawCard; //that is the initial face-up card that the opponent rejected.
                                            // for the OPPONENT, this is a notDrawnCard
               discardCards.pop();  // remove the card from the stack of the discard cards
            }
           else{ // our player drew from deck
               isFromStock = true;
               this.numDeckCards--;
               matrix[drawCard.getSuit()][drawCard.getRank()][STATE] = SELF_FROM_STOCK;
               matrix[drawCard.getSuit()][drawCard.getRank()][PROB_IN_STOCK] = KNOW_WHERE_IT_IS;  
               
                notDrawnCard = discardCards.peek();             
                if(numDrawnAll == 0){  // drawCard is from deck but this is the first time when reportDraw is called -> opponent rejected the first face-up card after the player did so
                    matrix[notDrawnCard.getSuit()][notDrawnCard.getRank()][STATE] = OPP_TOP_DISCARD;
                    turnsTaken++;
                }
           }        
           numSelfTurnsTaken++; 
           numDrawnSelf++;     
       }
       //opponent's turn
       else{
           if(drawCard != null){ // opponent drew from discard pile
               isFromStock = false;
               if(discardCards.size() != 0){ //not the inital face-up card
                   discardCards.pop();
                   matrix[drawCard.getSuit()][drawCard.getRank()][STATE] = OPP_FROM_DISCARD;
               }
               else{ //the initial face-up card
                   matrix[drawCard.getSuit()][drawCard.getRank()][STATE] = OPP_FROM_START;
                   matrix[drawCard.getSuit()][drawCard.getRank()][PROB_IN_STOCK] = KNOW_WHERE_IT_IS;               
               }
               oppCardsKnown.add(drawCard);
           }
           else{ //opponent drew from deck
               isFromStock = true;
                notDrawnCard = discardCards.peek();
                // drawCard is from deck but this is the first time when reportDraw is called -> our player rejected the first face-up card after the opponent did so
                if(numDrawnAll == 0)
                    matrix[notDrawnCard.getSuit()][notDrawnCard.getRank()][STATE] = OPP_TOP_DISCARD;
               this.numDeckCards--;
               this.numOppCardsUnknown++;
           }
       }
       updateProbForDrawnCard(drawCard, notDrawnCard, isOurTurn, isFromStock);
       numDrawnAll++;
       turnsTaken++;

       if(this.printSTATES) displaySTATES();
       if(this.printPROBS) displayPROBS();      
   }

   public void updateFromReportDiscard(int playerNum, Card discardCard){
       boolean isOurTurn = (playerNum == this.playerNum);
       // update information of the previous top card
       if(discardCards.size() > 0){
           Card previousFaceUpCard = discardCards.peek();
           // if the previous top card belonged to the opponent, set the state as buried by opp
           if(matrix[previousFaceUpCard.getSuit()][previousFaceUpCard.getRank()][STATE] == OPP_TOP_DISCARD){
                matrix[previousFaceUpCard.getSuit()][previousFaceUpCard.getRank()][STATE] = OPP_BURIED_DISCARD;
            }
            //if the previous top card belonged to the player, set the state as buried by self 
            else{
                matrix[previousFaceUpCard.getSuit()][previousFaceUpCard.getRank()][STATE] = SELF_BURIED_DISCARD;
            }
       }
       discardCards.push(discardCard);       

       // update information of the card just discard
       if (isOurTurn){
           matrix[discardCard.getSuit()][discardCard.getRank()][STATE] = SELF_TOP_DISCARD;
        }
       else{
           if(matrix[discardCard.getSuit()][discardCard.getRank()][PROB_IN_STOCK] != KNOW_WHERE_IT_IS)
              this.numOppCardsUnknown--;
           matrix[discardCard.getSuit()][discardCard.getRank()][STATE] = OPP_TOP_DISCARD;
           oppCardsKnown.remove(discardCard);  
       }
       matrix[discardCard.getSuit()][discardCard.getRank()][PROB_IN_STOCK] = KNOW_WHERE_IT_IS;
       
       this.numDeckCards_t.add(this.numDeckCards); // update numDeckCard history 
       this.numOppCardsUnknown_t.add(this.numOppCardsUnknown);  // update numOppCardsUnknown history
       updateProbForDiscardCard(discardCard, isOurTurn);  // update probability of neigboring cards
       addDelta();  // add delta to every prob whose state is UNKNOWN
       
       if(this.printSTATES) displaySTATES();
       if(this.printPROBS) displayPROBS();        
    }

    public void updateProbForDrawnCard(Card drawCard, Card notDrawnCard, boolean isOurTurn, boolean isFromStock){
        if(!isOurTurn && !isFromStock){ // opponent drew from the discard pile
            decreaseProbForCardSameSuit(drawCard, params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY), params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY));
            decreaseProbForCardSameRank(drawCard, params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK));
        }
        else if(!isOurTurn && isFromStock){ // opponent drew from stock, which means opponent rejected the face-up card
            increaseProbForCardSameSuit(notDrawnCard, params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY), params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY));
            increaseProbForCardSameRank(notDrawnCard, params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK));
        }
        else if(isOurTurn && !isFromStock){
            if(this.numDrawnAll == 0 && this.startingPlayerNum != this.playerNum && notDrawnCard != null){ //the first time when a card is drawn but the opponent was the starting player, which means the opponent rejected the initial face-up card and our player has took the card
                increaseProbForCardSameSuit(notDrawnCard, params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY), params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY));
                increaseProbForCardSameRank(notDrawnCard, params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK));
            }
        }
        else if(isOurTurn && isFromStock){ // our player drew from stock
            if(this.numDrawnAll == 0){ // the first time when a card is drawn, which means this is case where our player starts a game and both player declined the inital face-up card
                increaseProbForCardSameSuit(notDrawnCard, params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY), params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY));
                increaseProbForCardSameRank(notDrawnCard, params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK));
            }
        }
    }

    public void updateProbForDiscardCard(Card discardCard, boolean isOurTurn){
        if(!isOurTurn){ //opponent's discarding a card
            // addDelta();
            increaseProbForCardSameSuit(discardCard, params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY), params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY));
            increaseProbForCardSameRank(discardCard, params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK));
        }
    }

    public double calculateDelta(){
        int d_t = this.numDeckCards_t.get(this.numDeckCards_t.size() - 2);
        int d_t1 = this.numDeckCards_t.get(this.numDeckCards_t.size() - 1);
        int u_t = this.numOppCardsUnknown_t.get(this.numOppCardsUnknown_t.size() - 2);
        int u_t1 = this.numOppCardsUnknown_t.get(this.numOppCardsUnknown_t.size() - 1);
        double p_t = (double)d_t/(d_t + u_t);
        double p_t1 = (double)d_t1/(d_t1 + u_t1);
        double delta_p = p_t1 - p_t;
        // System.out.println("delta_p: " + delta_p + ", p_t: " + p_t + ", p_t1: " + p_t1);
        return delta_p;
    }

    public void addDelta(){
        double delta_p = calculateDelta();
        for(int i = 0; i < Card.NUM_SUITS; i++){
            for(int j = 0; j < Card.NUM_RANKS; j++){
                if(matrix[i][j][STATE] == UNKNOWN)
                    matrix[i][j][PROB_IN_STOCK] += delta_p;
            }
        }
    }

    public void increaseProbForCardSameSuit(Card discardCard, double parameter, double smallerParameter){
        //for example, if opponent discard 7H -> increase chance that 8H 6H  in stock 
        for(int rank = Math.max(discardCard.getRank() - 2, 0); Math.abs(rank - discardCard.getRank()) <= 2 && rank < Card.NUM_RANKS; rank++){
            double probability = matrix[discardCard.getSuit()][rank][PROB_IN_STOCK];
            //one rank away
            if(Math.abs(rank - discardCard.getRank()) == 1)
                matrix[discardCard.getSuit()][rank][PROB_IN_STOCK] = (probability != KNOW_WHERE_IT_IS) ? increaseProb(probability, parameter) : KNOW_WHERE_IT_IS;
            //two rank away
            else if(Math.abs(rank - discardCard.getRank()) == 2)
                matrix[discardCard.getSuit()][rank][PROB_IN_STOCK] = (probability != KNOW_WHERE_IT_IS) ? increaseProb(probability, smallerParameter) : KNOW_WHERE_IT_IS;
        }
    }

    public void increaseProbForCardSameRank(Card discardCard, double parameter){
        //for example, if opponent discard 7H -> increase chance that 7D in stock 
        for(int i = 0; i < Card.NUM_SUITS; i++){
            if(i != discardCard.getSuit() && matrix[i][discardCard.getRank()][PROB_IN_STOCK] != KNOW_WHERE_IT_IS){
                matrix[i][discardCard.getRank()][PROB_IN_STOCK] = increaseProb(matrix[i][discardCard.getRank()][PROB_IN_STOCK], parameter); 
            }
        }
    }

    public void decreaseProbForCardSameSuit(Card drawCard, double parameter, double smallParameter){
        for(int rank = Math.max(drawCard.getRank() - 2, 0); Math.abs(rank - drawCard.getRank()) <= 2 && rank < Card.NUM_RANKS; rank++){
            if(Math.abs(rank - drawCard.getRank())== 1){
                matrix[drawCard.getSuit()][rank][PROB_IN_STOCK] = (matrix[drawCard.getSuit()][rank][PROB_IN_STOCK] != KNOW_WHERE_IT_IS) ? decreaseProb(matrix[drawCard.getSuit()][rank][PROB_IN_STOCK], parameter) : KNOW_WHERE_IT_IS;
            }
            else if(Math.abs(rank - drawCard.getRank())== 2){
                matrix[drawCard.getSuit()][rank][PROB_IN_STOCK] = (matrix[drawCard.getSuit()][rank][PROB_IN_STOCK] != KNOW_WHERE_IT_IS) ? decreaseProb(matrix[drawCard.getSuit()][rank][PROB_IN_STOCK], smallParameter) : KNOW_WHERE_IT_IS;
            }
        }

    }

    public void decreaseProbForCardSameRank(Card drawCard, double parameter){
        for(int i = 0; i < Card.NUM_SUITS; i++){
            if(i != drawCard.getSuit() && matrix[i][drawCard.getRank()][PROB_IN_STOCK] != KNOW_WHERE_IT_IS)
                matrix[i][drawCard.getRank()][PROB_IN_STOCK] = decreaseProb(matrix[i][drawCard.getRank()][PROB_IN_STOCK], parameter);
        }
    }

    public double increaseProb(double p, double parameter){
        return p + (1 - p) * parameter;
    }

    // public double increaseProb(double p){
    //     return p + (1 - p) * params.get(ParamList.ST_PARAMETER);
    // }

    public double decreaseProb(double p, double parameter){
        return p - p * parameter;
    }

    // public double decreaseProb(double p){
    //     return p - p * params.get(ParamList.ST_PARAMETER);
    // }

    public int getNumDrawnSelf(){
        return this.numDrawnSelf;
    }

    //Add this method only to test TwoStageDrawDecider
    public void setNumDrawnSelf(int numDrawnSelf){
        this.numDrawnSelf = numDrawnSelf;
    }

    public int getNumDeckCards(){
        return this.numDeckCards;
    }
 
    public int getNumOppCardsUnknown(){
        return this.numOppCardsUnknown;
    }

    public int getNumOppCardsKnown(){
        return this.oppCardsKnown.size();
    }
 
    public double[][][] getMatrix(){
        return this.matrix;
    }

    public int getTurnsTaken(){
        return this.turnsTaken;
    }

    public int getNumSelfTurnsTaken(){
        return this.numSelfTurnsTaken;
    }

    public ArrayList<Card> getOppCardsKnown(){
        return this.oppCardsKnown;
    }

    public void setParamList(ParamList params){
        this.params = params;
    }

    public HashMap<String, Double> getStrToProbMap(){
        HashMap<String, Double> probMap = new HashMap<String, Double>();
        for(int suit = 0; suit < Card.NUM_SUITS; suit++){
            for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                Card card = OurUtil.getCard(rank, suit);
                String str = card.toString();
                double probability = matrix[suit][rank][PROB_IN_STOCK];
                probMap.put(str, probability);
            }
        }
        return probMap;
    }
    
    public HashMap<Card, Double> getCardToProbMap(){
        HashMap<Card, Double> probMap = new HashMap<Card, Double>();
        for(int suit = 0; suit < Card.NUM_SUITS; suit++){
            for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                Card card = OurUtil.getCard(rank, suit);
                double probability = matrix[suit][rank][PROB_IN_STOCK];
                probMap.put(card, probability);
            }
        }
        return probMap;
    }

    /**
     * private method for getStrToProbMapWithHighestProbInOppHand
     * sorts card of string to probablity map by probability
     * @param order true means the ascending order and false means the descending order
     * Static variables, ASC and DESC, are defined
     */
    private Map<String, Double> sortStringsByValue(final boolean order){
        Map<String, Double> unsortMap = getStrToProbMap(); //needs to be change
        List<Entry<String, Double>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey()) : o1.getValue().compareTo(o2.getValue()) 
                : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey()) : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }

    /**
     * private method for getCardToProbMapWithHighestProbInOppHand
     * sorts card to probablity map by probability
     * @param order true means the ascending order and false means the descending order
     * Static variables, ASC and DESC, are defined
     */
    private Map<Card, Double> sortCardsByValue(final boolean order){
        Map<Card, Double> unsortMap = getCardToProbMap(); //needs to be change
        List<Entry<Card, Double>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) 
                                    : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }

    /**
     * This method returns a specified number of cards that are sorted by probabiliy of cards being in the opponent's hand
     * @param numCards number of cards to get 
     * @return LinkedHashMap<String, Double> that stores cards with higest (1 - PROB_IN_STOCK) values in order
     */
    public Map<String, Double> getStrToProbMapWithHighestProbInOppHand(int numCards){
        Map<String, Double> sortedMap = sortStringsByValue(ASC);
        Map<String, Double> mapWithHighestProb = new LinkedHashMap<String, Double>();
        int ct = 0;
        for(String key: sortedMap.keySet()){
            if(ct < numCards){
                double value = sortedMap.get(key);
                if(value != KNOW_WHERE_IT_IS){
                    value = 1 - value;
                    mapWithHighestProb.put(key, value);
                    ct++;
                }
            }
            else
                break;
        }
        return mapWithHighestProb;
    }

    /**
     * This method returns a specified number of cards that are sorted by probabiliy of cards being in the opponent's hand
     * @param numCards number of cards to get 
     * @return LinkedHashMap<Card, Double> that stores cards with higest (1 - PROB_IN_STOCK) values in order
     */
    public Map<Card, Double> getCardToProbMapWithHighestProbInOppHand(int numCards){
        Map<Card, Double> sortedMap = sortCardsByValue(ASC);
        Map<Card, Double> mapWithHighestProb = new LinkedHashMap<Card, Double>();
        int ct = 0;
        for(Card key: sortedMap.keySet()){
            if(ct < numCards){
                double value = sortedMap.get(key);
                if(value != KNOW_WHERE_IT_IS){
                    value = 1 - value;
                    mapWithHighestProb.put(key, value);
                    ct++;
                }
            }
            else
                break;
        }
        return mapWithHighestProb;
    }
    
    /**
     * Print out map of string of card and probability
     * @param map map you want to print out
     */
    public static void printStrToDoublebMap(Map<String, Double> map){
        map.forEach((key, value) -> System.out.println("Key : " + key + ", Value : " + value));
    }


    /**
     * Print out map of card and probability
     * @param map map you want to print out
     */
    public static void printCardToDoublebMap(Map<Card, Double> map){
        map.forEach((key, value) -> System.out.println("Key : " + key.toString() + ", Value : " + value));
    }

    public static void setTesting(boolean testing){
        TESTING = testing;
    }

    /**
     * Print out the matrix that stores information of state of cards
     */
    public void displaySTATES(){
        System.out.print("  ");
        for(int i = 0; i < Card.NUM_RANKS; i++){
            System.out.print(Card.rankNames[i] + " ");
        }
        System.out.println();
        for(int j = 0; j < Card.NUM_SUITS; j++){
            for(int k = 0; k < Card.NUM_RANKS + 1; k++){
                if(k == 0)
                    System.out.print(Card.suitNames[j] + " ");
                else
                    System.out.print((int)matrix[j][k-1][STATE] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Print out the matrix that stores information of probality that cards are in stock 
     */
    public void displayPROBS(){
        System.out.print("  ");
        for(int i = 0; i < Card.NUM_RANKS; i++){
            System.out.print("  " + Card.rankNames[i] + "   ");
        }
        System.out.println();
        for(int j = 0; j < Card.NUM_SUITS; j++){
            for(int k = 0; k < Card.NUM_RANKS + 1; k++){
                if(k == 0)
                    System.out.print(Card.suitNames[j] + " ");
                else{
                    if(matrix[j][k-1][PROB_IN_STOCK] == KNOW_WHERE_IT_IS)
                        System.out.print(matrix[j][k-1][PROB_IN_STOCK] + "  ");
                    else
                        System.out.printf("%.3f ", matrix[j][k-1][PROB_IN_STOCK]);
                }
            }
            System.out.println();
        }
    }

    /**
     * Print out the information on StateTracker object
     */
    public void displayFields(){
        System.out.print("turnsTaken: " + this.turnsTaken + ", ");
        System.out.print("numSelfTurnsTaken: " + this.numSelfTurnsTaken + ", ");
        System.out.print("numDrawnAll: " + this.numDrawnAll + ", ");
        System.out.print("numDeckCards: " + this.numDeckCards + ", ");
        System.out.print("numDeckCards_t: " + this.numDeckCards_t.toString() + ", ");
        System.out.print("discard pile: " + this.discardCards.toString() + ", ");
        System.out.print("numOppCardsUnknown: " + this.numOppCardsUnknown + ", ");
        System.out.print("numOppCardsUnknown_t: " + this.numOppCardsUnknown_t.toString() + ", ");
        System.out.print("oppCardsKnown: " + this.oppCardsKnown.toString() + " ");
        System.out.println();
    }

    /**
	 * Set whether or not matrix on STATE is to be printed output during gameplay.
	 * @param printSTATES whether or not matrix on STATE is to be printed output during gameplay
	 */
    public void setDisplaySTATES(boolean printSTATES){
        this.printSTATES = printSTATES;
    }

    /**
	 * Set whether or not matrix on PROB_IN_STOCK is to be printed output during gameplay.
	 * @param printPROBS whether or not matrix on PROB_IN_STOCK is to be printed output during gameplay
	 */
    public void setDisplayPROBS(boolean printPROBS){
        this.printPROBS = printPROBS;
    }

    /**
	 * Set STATE and PROB_IN_STOCK of a card that is passed to the method 
	 * @param strOfCard string of a card on which you want to set state and probability 
     * @param state state of the card
     * @param probability probabiilty of the card being in stock
	 */
    public void setMatrixWithStrOfCard(String strOfCard, double state, double probability){
        Card card = Card.strCardMap.get(strOfCard);
        int suit = card.getSuit();
        int rank = card.getRank();
        this.matrix[suit][rank][STATE] = state;
        this.matrix[suit][rank][PROB_IN_STOCK] = probability;
    }

    @Override
    public String toString(){
        String str = "turnsTaken: " + this.turnsTaken + ", "
                   + "numSelfTurnsTaken: " + this.numSelfTurnsTaken + ", "
                   + "numDrawnAll: " + this.numDrawnAll + ", "
                   + "numDeckCards: " + this.numDeckCards + ", "
                   + "numDeckCards_t: " + this.numDeckCards_t.toString() + ", "
                   + "discard pile: " + this.discardCards.toString() + ", "
                   + "numOppCardsUnknown: " + this.numOppCardsUnknown + ", "
                   + "numOppCardsUnknown_t: " + this.numOppCardsUnknown_t.toString() + ", "
                   + "oppCardsKnown: " + this.oppCardsKnown.toString() + ", "
                   + "params" + this.params.toString() + " "
                   + "\n";

        str += "---------- State Matrix ----------\n";
        str += "  ";
        for(int i = 0; i < Card.NUM_RANKS; i++){
            str += Card.rankNames[i] + " ";
        }
        str += "\n";
        for(int j = 0; j < Card.NUM_SUITS; j++){
            for(int k = 0; k < Card.NUM_RANKS + 1; k++){
                if(k == 0)
                    str += Card.suitNames[j] + " ";
                else
                    str += (int)matrix[j][k-1][STATE] + " ";
            }
            str += "\n";
        }

        str += "---------- Probability Matrix ----------\n";
        str += "  ";
        for(int i = 0; i < Card.NUM_RANKS; i++){
            str += "  " + Card.rankNames[i] + "   ";
        }
        str += "\n";
        for(int j = 0; j < Card.NUM_SUITS; j++){
            for(int k = 0; k < Card.NUM_RANKS + 1; k++){
                if(k == 0)
                    str += Card.suitNames[j] + " ";
                else{
                    if(matrix[j][k-1][PROB_IN_STOCK] == KNOW_WHERE_IT_IS)
                        str += matrix[j][k-1][PROB_IN_STOCK] + "  ";
                    else
                        str += String.format("%.3f ", matrix[j][k-1][PROB_IN_STOCK]);
                }
            }
            str += "\n";
        }
        return str;
    }

    //don't trust this method too much
    public static StateTracker readFromFile(String fileName){
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        StateTracker myTracker = new StateTracker(params);
        Card[] selfHand = new Card[10];
        int playerNum = 0;
        int numLine = 1;
        Card faceUpCard = null;     
        
        BufferedReader reader;
        FileReader file;
        try{
            file = new FileReader(fileName);
            reader = new BufferedReader(file);
            String line = reader.readLine();
            while(line != null){
                if(TESTING) System.out.println(line);
                String[] ls = line.split("\\s+");
                if(ls[2].equals("is") && ls[1].equals("0")){
                    for(int i = 4; i < ls.length; i++){
                        String card = ls[i];
                        card = card.replaceAll("[^\\w]", "");
                        selfHand[i-4] = Card.strCardMap.get(card);
                    }
                }
                else if(ls[2].equals("starts.")){
                    int startingPlayer = Integer.parseInt(ls[1]);
                    myTracker.updateFromStartGame(playerNum, startingPlayer, selfHand);
                }
                else if(numLine == 5 && ls[2].equals("draws") && ls[1].equals("0")){
                    String card = ls[3].replaceAll("[^\\w]", "");
                    myTracker.updateFromWillDrawFaceUpCard(Card.strCardMap.get(card), true);
                }
                else if(numLine == 6 && ls[2].equals("draws") && ls[1].equals("0")){
                    String card = ls[3].replaceAll("[^\\w]", "");
                    myTracker.updateFromWillDrawFaceUpCard(Card.strCardMap.get(card), true);
                }
                else if(ls[2].equals("draws")){
                    String str = ls[3].replaceAll("[^\\w]", "");
                    Card card = Card.strCardMap.get(str);
                    int currentPlayer = Integer.parseInt(ls[1]);
                    Card drawnCard = (currentPlayer == 0 || card == faceUpCard) ? card: null;
                    myTracker.updateFromReportDraw(currentPlayer, drawnCard);
                }
                else if(ls[2].equals("discards")){
                    String str = ls[3].replaceAll("[^\\w]", "");
                    Card discardCard = Card.strCardMap.get(str);
                    int currentPlayer = Integer.parseInt(ls[1]);
                    myTracker.updateFromReportDiscard(currentPlayer, discardCard);
                }
                numLine++;
                line = reader.readLine();
            }
            reader.close();
        }
        catch(Exception e){
            System.out.println(e);
        }

        return myTracker;
    }
    
    public void makeOppCardsKnownOfString(){    
        if(TESTING) System.out.println("-------------------- Making oppCardsKnownOfString --------------------");
        this.oppCardsKnownOfString.clear();  
        for(int cardIndex = 0; cardIndex<oppCardsKnown.size(); cardIndex++){
            this.oppCardsKnownOfString.add(oppCardsKnown.get(cardIndex).toString());
        }
    } 
    
    public void recoverOppCardsKnown(){
        if(TESTING) System.out.println("-------------------- Recovering oppCardsKnownOfString --------------------");
        this.oppCardsKnown = new ArrayList<Card>();
        for(int i = 0; i < oppCardsKnownOfString.size(); i++){
            String card = oppCardsKnownOfString.get(i);
            this.oppCardsKnown.add(Card.strCardMap.get(card));
        }
    }
    
    public void makeDiscardCardsOfString(){
        if(TESTING) System.out.println("-------------------- Making discardCardsOfString --------------------");
        this.discardCardsOfString.clear();
        for(Card card: this.discardCards){
            String str = card.toString();
            this.discardCardsOfString.push(str);
        }
    }

    public void recoverDiscardCards(){
        if(TESTING) System.out.println("-------------------- Recovering discardCards --------------------");
        this.discardCards = new Stack<>();
        for(String str: this.discardCardsOfString){
            Card card = Card.strCardMap.get(str);
            this.discardCards.push(card);
        }
    }

    public static void serializeStateTrackerToFile(StateTracker trackerToRecord, String fileName) throws IOException, ClassNotFoundException{
        if(TESTING) System.out.println("-------------------- Serializing StateTracker to File --------------------");
        trackerToRecord.makeDiscardCardsOfString();
        trackerToRecord.makeOppCardsKnownOfString();
        //trackerToRecord.makeParamsAL();
        FileOutputStream fos = new FileOutputStream(fileName); 
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(trackerToRecord);
        oos.flush();
        oos.close();
    }

    public static StateTracker deserializeStateTrackerFromFile(String fileName)throws IOException, ClassNotFoundException{
        if(TESTING) System.out.println("-------------------- Deserializing StateTracker from File  --------------------");
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        StateTracker tracker = (StateTracker) ois.readObject();
        ois.close();
        tracker.recoverDiscardCards();
        tracker.recoverOppCardsKnown();
        //tracker.recoverParamsAL();
        return tracker;
    }

    public static String serializeStateTrackerToString(StateTracker trackerToRecord) throws IOException, ClassNotFoundException{
        if(TESTING) System.out.println("-------------------- Serializing StateTracker to String --------------------");
        trackerToRecord.makeDiscardCardsOfString();
        trackerToRecord.makeOppCardsKnownOfString();
        //trackerToRecord.makeParamsAL();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(trackerToRecord);
        oos.flush();
        String serializedObject = Base64.getEncoder().encodeToString(bos.toByteArray());
        return serializedObject;
    }

    public static StateTracker deserializeStateTrackerFromString(String serializedObject)throws IOException, ClassNotFoundException{
        if(TESTING) System.out.println("-------------------- Deserializing StateTracker from String  --------------------");
        byte b[] = Base64.getDecoder().decode(serializedObject.getBytes());
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bis);
        StateTracker tracker = (StateTracker) ois.readObject();
        ois.close();
        tracker.recoverDiscardCards();
        tracker.recoverOppCardsKnown();
        //tracker.recoverParamsAL();
        return tracker;
    }

    public ArrayList<Card> getSelfHandForHardcodedStateTracker1() {
        return OurUtil.makeHand(new String[] {"2S", "3S", "4S", "5S", "6S", "9C", "9H", "9S", "4C", "AD"});
    }

    public ArrayList<Card> getOppHandForHardcodedStateTracker1() {
        return OurUtil.makeHand(new String[] {"8D", "9D", "TD", "AS", "AH", "5C", "5D", "3D", "6H", "2D"});
    }

    /**
     * win by no-gin
     * use the corresponding getSelf/OppHandForHardcodedStateTracker methods
     */
    public void setToHardcodedStateTracker1(){
        // our player has {"2S", "3S", "4S", "5S", "6S", "9C", "9H", "9S", "4C", "AD"}
        // opp has {"8D", "9D", "TD", "AS", "AH", "5C", "5D", "3D", "6H", "2D"}
        this.turnsTaken = 16;
        this.numSelfTurnsTaken = 8;
        this.numDrawnAll = 14;
        this.numDrawnSelf = 7;
        this.numDeckCards = 19;
        this.numDeckCards_t = new ArrayList<Integer>(Arrays.asList(31, 30, 29, 28, 27, 26, 25, 24, 24, 24, 23, 22, 21, 20, 19));
        this.discardCards.push(Card.strCardMap.get("QS"));
        this.discardCards.push(Card.strCardMap.get("KH"));
        this.discardCards.push(Card.strCardMap.get("KS"));
        this.discardCards.push(Card.strCardMap.get("TH"));
        this.discardCards.push(Card.strCardMap.get("TS"));
        this.discardCards.push(Card.strCardMap.get("KC"));
        this.discardCards.push(Card.strCardMap.get("JS"));
        this.discardCards.push(Card.strCardMap.get("8H"));
        this.discardCards.push(Card.strCardMap.get("JC"));
        this.discardCards.push(Card.strCardMap.get("8C"));
        this.discardCards.push(Card.strCardMap.get("QC"));
        this.discardCards.push(Card.strCardMap.get("6D"));
        this.discardCards.push(Card.strCardMap.get("QH"));
        this.numOppCardsUnknown = 9;
        this.numOppCardsUnknown_t = new ArrayList<Integer>(Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 9, 9, 9, 9, 9, 9));
        this.oppCardsKnown = new ArrayList<Card>(Arrays.asList(Card.strCardMap.get("TD")));
        this.setMatrixWithStrOfCard("AC", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("AH", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("AS", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("AD", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("2C", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("2H", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("2S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("2D", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("3C", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("3H", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("3S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("3D", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("4C", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("4H", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("4S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("4D", UNKNOWN, 0.709);
        this.setMatrixWithStrOfCard("5C", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("5H", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("5S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("5D", UNKNOWN, 0.739);
        this.setMatrixWithStrOfCard("6C", UNKNOWN, 0.761);
        this.setMatrixWithStrOfCard("6H", UNKNOWN, 0.762);
        this.setMatrixWithStrOfCard("6S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("6D", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("7C", UNKNOWN, 0.758);
        this.setMatrixWithStrOfCard("7H", UNKNOWN, 0.737);
        this.setMatrixWithStrOfCard("7S", UNKNOWN, 0.679);
        this.setMatrixWithStrOfCard("7D", UNKNOWN, 0.739);
        this.setMatrixWithStrOfCard("8C", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("8H", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("8S", UNKNOWN, 0.791);
        this.setMatrixWithStrOfCard("8D", UNKNOWN, 0.761);
        this.setMatrixWithStrOfCard("9C", SELF_FROM_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("9H", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("9S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("9D", UNKNOWN, 0.583);
        this.setMatrixWithStrOfCard("TC", UNKNOWN, 0.707);
        this.setMatrixWithStrOfCard("TH", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("TS", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("TD", OPP_FROM_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("JC", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("JH", UNKNOWN, 0.790);
        this.setMatrixWithStrOfCard("JS", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("JD", UNKNOWN, 0.600);
        this.setMatrixWithStrOfCard("QC", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("QH", SELF_TOP_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("QS", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("QD", UNKNOWN, 0.665);
        this.setMatrixWithStrOfCard("KC", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("KH", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("KS", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("KD", UNKNOWN, 0.788);
    }
    
    public ArrayList<Card> getSelfHandForHardcodedStateTracker2() {
        return OurUtil.makeHand(new String[] {"2S", "3S", "4S", "5S", "6S", "9C", "9H", "9S", "4C", "QH"});
    }

    public ArrayList<Card> getOppHandForHardcodedStateTracker2() {
        return OurUtil.makeHand(new String[] {"8D", "9D", "TD", "AS", "AH", "5C", "5D", "3D", "6H", "8C"});
    }

    /**
     * self cannot knock
     * use the corresponding getSelf/OppHandForHardcodedStateTracker methods
     */
    public void setToHardcodedStateTracker2(){
        //Player 0 has [[2S, 3S, 4S, 5S, 6S], [9C, 9H, 9S], [4C, QH]] with 14 deadwood.
        //Player 1 has [[8D, 9D, TD], [8C, AS, AH, 5C, 5D, 3D, 6H]] with 29 deadwood
        this.turnsTaken = 12;
        this.numSelfTurnsTaken = 6;
        this.numDrawnAll = 10;
        this.numDrawnSelf = 5;
        this.numDeckCards = 23;
        this.numDeckCards_t = new ArrayList<Integer>(Arrays.asList(31, 30, 29, 28, 27, 26, 25, 24, 24, 24, 24, 23));
        this.discardCards.push(Card.strCardMap.get("QS"));
        this.discardCards.push(Card.strCardMap.get("KH"));
        this.discardCards.push(Card.strCardMap.get("KS"));
        this.discardCards.push(Card.strCardMap.get("TH"));
        this.discardCards.push(Card.strCardMap.get("TS"));
        this.discardCards.push(Card.strCardMap.get("KC"));
        this.discardCards.push(Card.strCardMap.get("JS"));
        this.discardCards.push(Card.strCardMap.get("8H"));
        this.discardCards.push(Card.strCardMap.get("JC"));
        this.numOppCardsUnknown = 9;
        this.numOppCardsUnknown_t = new ArrayList<Integer>(Arrays.asList(10, 10, 10, 10, 10, 10, 10, 10, 10, 9, 9));
        this.oppCardsKnown = new ArrayList<Card>(Arrays.asList(Card.strCardMap.get("TD")));
        this.setMatrixWithStrOfCard("AC", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("AH", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("AS", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("AD", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("2C", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("2H", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("2S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("2D", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("3C", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("3H", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("3S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("3D", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("4C", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("4H", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("4S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("4D", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("5C", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("5H", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("5S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("5D", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("6C", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("6H", UNKNOWN, 0.748);
        this.setMatrixWithStrOfCard("6S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("6D", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("7C", UNKNOWN, 0.747);
        this.setMatrixWithStrOfCard("7H", UNKNOWN, 0.778);
        this.setMatrixWithStrOfCard("7S", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("7D", UNKNOWN, 0.719);
        this.setMatrixWithStrOfCard("8C", UNKNOWN, 0.823);
        this.setMatrixWithStrOfCard("8H", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("8S", UNKNOWN, 0.788);
        this.setMatrixWithStrOfCard("8D", UNKNOWN, 0.721);
        this.setMatrixWithStrOfCard("9C", SELF_FROM_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("9H", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("9S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("9D", UNKNOWN, 0.670);
        this.setMatrixWithStrOfCard("TC", UNKNOWN, 0.707);
        this.setMatrixWithStrOfCard("TH", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("TS", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("TD", OPP_FROM_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("JC", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("JH", UNKNOWN, 0.811);
        this.setMatrixWithStrOfCard("JS", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("JD", UNKNOWN, 0.600);
        this.setMatrixWithStrOfCard("QC", UNKNOWN, 0.792);
        this.setMatrixWithStrOfCard("QH", SELF_TOP_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("QS", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("QD", UNKNOWN, 0.670);
        this.setMatrixWithStrOfCard("KC", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("KH", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("KS", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("KD", UNKNOWN, 0.828);
    }
    
    public ArrayList<Card> getSelfHandForHardcodedStateTracker3() {
        return OurUtil.makeHand(new String[] {"2H", "3H", "4H", "AS", "2S", "3S", "7C", "7S", "7D", "4C"}); // this is what the comments say
    }

    public ArrayList<Card> getOppHandForHardcodedStateTracker3() {
        return OurUtil.makeHand(new String[] {"6H", "7H", "8H", "9C", "9H", "9S", "9D", "AH", "AD", "2D"});
    }

    /**
     * gets undercut
     * use the corresponding getSelf/OppHandForHardcodedStateTracker methods
     */
    public void setToHardcodedStateTracker3(){
        // our player has {"2H", "3H", "4H", "AS", "2S", "3S", "7C", "7S", "7D", "4C"}
        // opp has {"6H", "7H", "8H", "9C", "9H", "9S", "9D", "AH", "AD", "2D"}
        this.turnsTaken = 14;
        this.numSelfTurnsTaken = 7;
        this.numDrawnAll = 12;
        this.numDrawnSelf = 6;
        this.numDeckCards = 22;
        this.numDeckCards_t = new ArrayList<Integer>(Arrays.asList(31, 30, 29, 28, 27, 26, 25, 24 ,24 ,23, 22, 22));
        this.discardCards.push(Card.strCardMap.get("2C"));
        this.discardCards.push(Card.strCardMap.get("JS"));
        this.discardCards.push(Card.strCardMap.get("KH"));
        this.discardCards.push(Card.strCardMap.get("KC"));
        this.discardCards.push(Card.strCardMap.get("8S"));
        this.discardCards.push(Card.strCardMap.get("QD"));
        this.discardCards.push(Card.strCardMap.get("8C"));
        this.discardCards.push(Card.strCardMap.get("5C"));
        this.discardCards.push(Card.strCardMap.get("KS"));
        this.discardCards.push(Card.strCardMap.get("5D"));
        this.numOppCardsUnknown = 8;
        this.numOppCardsUnknown_t = new ArrayList<Integer>(Arrays.asList(10, 10, 10, 10, 10, 10, 9, 9, 9, 9, 8, 8, 8, 8));
        this.oppCardsKnown = new ArrayList<Card>(Arrays.asList(Card.strCardMap.get("9H"), Card.strCardMap.get("8H")));
        this.setMatrixWithStrOfCard("AC", UNKNOWN, 0.758);
        this.setMatrixWithStrOfCard("AH", UNKNOWN, 0.785);
        this.setMatrixWithStrOfCard("AS", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("AD", UNKNOWN, 0.733);
        this.setMatrixWithStrOfCard("2C", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("2H", SELF_FROM_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("2S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("2D", UNKNOWN, 0.804);
        this.setMatrixWithStrOfCard("3C", UNKNOWN, 0.783);
        this.setMatrixWithStrOfCard("3H", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("3S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("3D", UNKNOWN, 0.733);
        this.setMatrixWithStrOfCard("4C", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("4H", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("4S", UNKNOWN, 0.733);
        this.setMatrixWithStrOfCard("4D", UNKNOWN, 0.733);
        this.setMatrixWithStrOfCard("5C", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("5H", UNKNOWN, 0.788);
        this.setMatrixWithStrOfCard("5S", UNKNOWN, 0.788);
        this.setMatrixWithStrOfCard("5D", SELF_TOP_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("6C", UNKNOWN, 0.808);
        this.setMatrixWithStrOfCard("6H", UNKNOWN, 0.661);
        this.setMatrixWithStrOfCard("6S", UNKNOWN, 0.760);
        this.setMatrixWithStrOfCard("6D", UNKNOWN, 0.733);
        this.setMatrixWithStrOfCard("7C", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("7H", UNKNOWN, 0.530);
        this.setMatrixWithStrOfCard("7S", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("7D", SELF_FROM_STOCK, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("8C", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("8H", OPP_FROM_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("8S", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("8D", UNKNOWN, 0.664);
        this.setMatrixWithStrOfCard("9C", UNKNOWN, 0.668);
        this.setMatrixWithStrOfCard("9H", OPP_FROM_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("9S", UNKNOWN, 0.686);
        this.setMatrixWithStrOfCard("9D", UNKNOWN, 0.587);
        this.setMatrixWithStrOfCard("TC", UNKNOWN, 0.759);
        this.setMatrixWithStrOfCard("TH", UNKNOWN, 0.529);
        this.setMatrixWithStrOfCard("TS", UNKNOWN, 0.804);
        this.setMatrixWithStrOfCard("TD", UNKNOWN, 0.746);
        this.setMatrixWithStrOfCard("JC", UNKNOWN, 0.803);
        this.setMatrixWithStrOfCard("JH", UNKNOWN, 0.714);
        this.setMatrixWithStrOfCard("JS", OPP_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("JD", UNKNOWN, 0.803);
        this.setMatrixWithStrOfCard("QC", UNKNOWN, 0.805);
        this.setMatrixWithStrOfCard("QH", UNKNOWN, 0.782);
        this.setMatrixWithStrOfCard("QS", UNKNOWN, 0.822);
        this.setMatrixWithStrOfCard("QD", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("KC", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("KH", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("KS", SELF_BURIED_DISCARD, KNOW_WHERE_IT_IS);
        this.setMatrixWithStrOfCard("KD", UNKNOWN, 0.840);
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException{
        setTesting(true);
        ParamList params1 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        // ParamList params3 = new ParamList();
        ParamList params3 = ParamList.getRandomParamList(0);
        StateTracker originalTracker1 = new StateTracker(params1);
        StateTracker originalTracker3 = new StateTracker(params3);
        originalTracker1.setToHardcodedStateTracker1();
        originalTracker3.setToHardcodedStateTracker3();
        // System.out.println(originalTracker3);
        // serializeStateTrackerToFile(originalTracker, "StateTracker_test.txt");
        // StateTracker importedTracker = deserializeStateTrackerFromFile("StateTracker_test.txt");
        // System.out.println(importedTracker);
        String serializedObject1 = serializeStateTrackerToString(originalTracker1);
        String serializedObject3 = serializeStateTrackerToString(originalTracker3);
        // System.out.println("String of serializedObject1: " + serializedObject1);
        // System.out.println("String of serializedObject3: " + serializedObject3);
        StateTracker importedTracker1 = deserializeStateTrackerFromString(serializedObject1);
        StateTracker importedTracker3 = deserializeStateTrackerFromString(serializedObject3);
        System.out.println("--------------- original tracker 1 ---------------");
        System.out.println(originalTracker1);
        System.out.println("--------------- imported tracker 1 ---------------");
        System.out.println(importedTracker1);
        System.out.println("--------------- original tracker 3 ---------------");
        System.out.println(originalTracker3);
        System.out.println("--------------- imported tracker 3 ---------------");
        System.out.println(importedTracker3);
    }
}
