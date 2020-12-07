package players;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import players.OurSimpleGinRummyPlayer;
import ginrummy.Card;
import ginrummy.GinRummyGame;
import util.OurUtil;

public class StateTrackerPlayer extends OurSimpleGinRummyPlayer {
    protected StateTracker myTracker;
    protected boolean TESTING  = true;

    public StateTrackerPlayer(ParamList params) {
        this.myTracker = new StateTracker(params);
    }

    @Override
	public void startGame(int playerNum, int startingPlayerNum, Card[] cards){
        super.startGame(playerNum, startingPlayerNum, cards);
        myTracker.updateFromStartGame(playerNum, startingPlayerNum, cards);
    }
    
    @Override
	public boolean willDrawFaceUpCard(Card card){
        boolean willDraw = super.willDrawFaceUpCard(card);//random_seed.nextInt(2) == 0 ? false : true;
        // the following needs to be called only when card is the inital face-up card
        // we might better add if statement here not to keep calling this
        myTracker.updateFromWillDrawFaceUpCard(card, willDraw);
        if(TESTING) {
            System.out.println("After willDrawFaceUpCard: " );
            System.out.println(myTracker);
        }
        return willDraw;
    }

    // for test purpose
    // it takes one more parameter setWillDraw so that we can control its behavior
    public boolean willDrawFaceUpCardTest(Card card, boolean setWillDraw){
        boolean willDraw = setWillDraw;
        // the following needs to be called only when card is the inital face-up card
        // we might better add if statement here not to keep calling this
        myTracker.updateFromWillDrawFaceUpCard(card, willDraw);
		return willDraw;
    }

    @Override
    public void reportDraw(int playerNum, Card drawnCard){
        super.reportDraw(playerNum, drawnCard);
        myTracker.updateFromReportDraw(playerNum, drawnCard);
        if(TESTING) {
            System.out.println("After reportDraw on player" + playerNum + ": ");
            System.out.println(myTracker);
        }
   }

   @Override
   public Card getDiscard(){    
        return super.getDiscard();
   }

   @Override
   public void reportDiscard(int playerNum, Card discardCard){
        super.reportDiscard(playerNum, discardCard);
        myTracker.updateFromReportDiscard(playerNum, discardCard);
        if(TESTING) {
            System.out.println("After reportDiscard on player" + playerNum + ": " );
            System.out.println(myTracker);
        }
   }

   public int getNumDeckCards(){
       return myTracker.getNumDeckCards();
   }

   public int getNumOppCardsUnknown(){
       return myTracker.getNumOppCardsUnknown();
   }

   public double[][][] getMatrix(){
       return myTracker.getMatrix();
   }

   public int getTurnsTaken(){
       return myTracker.getTurnsTaken();
   }

    public int getNumSelfTurnsTaken(){
        return myTracker.getNumSelfTurnsTaken();
   }

   public HashMap<String, Double> getHashMap(){
       return myTracker.getStrToProbMap();
   }

   public void setDisplaySTATES(boolean boolSTATES){
       myTracker.setDisplaySTATES(boolSTATES);
    }
    
    public void setDisplayPROBS(boolean boolPROBS){
        myTracker.setDisplayPROBS(boolPROBS);
    }

    public void setTesting(boolean isTesting){
        this.TESTING = isTesting;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Testing
    
    public static void test1(StateTrackerPlayer p0, StateTrackerPlayer p1){
        System.out.println("========== Test 1 (player0 starts and draws the first face-up card) ==========");
        ArrayList<Card> oppCards = OurUtil.makeHand(new String[] {"AC", "AH", "AD", "2S", "3C", "4H", "5D", "6D", "7H", "8S"});
        ArrayList<Card> myCards = OurUtil.makeHand(new String[] {"AS", "2C", "2H", "2D", "3S", "4C", "5H", "6S", "7D", "8C"});
        Card[] cardsOfPlayer0 = new Card[10]; 
        Card[] cardsOfPlayer1 = new Card[10]; 
        String cardStr = "";
        
        for(int i = 0; i < 10; i++){
            cardsOfPlayer1[i] = oppCards.get(i);
            cardsOfPlayer0[i] = myCards.get(i);
            // cardsOnPile.remove(Card.strIdMap.get(oppCards.get(i).getSuit()));
            // cardsOnPile.remove(Card.strIdMap.get(myCards.get(i)));
        }

        System.out.println("player0 starts");
        p0.startGame(0, 0, cardsOfPlayer0);
        p1.startGame(1, 0, cardsOfPlayer1);
        System.out.println("Player 0 is dealt " + Arrays.toString(cardsOfPlayer0));
        System.out.println("Player 1 is dealt " + Arrays.toString(cardsOfPlayer1));
        System.out.println("Player0's matrix: confirm that cards in the hand show 1, which represents SELF_FROM_STOCK");
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();
        System.out.println("The number of cards left in deck is: " + p0.myTracker.numDeckCards);
        
        System.out.println("----------  Player 0 drawing from discarded pile ----------");
        cardStr = "TS"; //need to change
        System.out.println("player0 draws first face-up card: " + cardStr);
        Card firstFaceUpCard = Card.strCardMap.get(cardStr);
        p0.willDrawFaceUpCardTest(firstFaceUpCard, true);
        p0.reportDraw(0, firstFaceUpCard);
        p1.reportDraw(0, firstFaceUpCard);
        System.out.println("Player0's matrix: confirm that " + cardStr + " shows 2, which represents SELF_FROM_DISCARD");
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();
        System.out.println("The number of cards left in deck is: " + p0.myTracker.numDeckCards);

        System.out.println("----------  Player 0 discarding  ----------");
        cardStr = "AH"; //need to change
        System.out.println("player0 discards " + cardStr);
        Card discardedByP0 =  Card.strCardMap.get(cardStr);
        p0.reportDiscard(0, discardedByP0);
        p1.reportDiscard(0, discardedByP0);
        System.out.println("Discarded pile: " + p0.myTracker.discardCards);
        System.out.println("Player0's matrix: confirm that " + cardStr + " shows 5, which represents SELF_TOP_DISCARD");
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();
        System.out.println("The number of cards left in deck is: " + p0.myTracker.numDeckCards);

        System.out.println("----------  Player 1 drawing from discarded pile ----------");
        cardStr = "AH"; //need to change
        System.out.println("player1 draws from the discarded pile" + cardStr);
        Card secondFaceUpCard = Card.strCardMap.get(cardStr);
        p1.willDrawFaceUpCardTest(secondFaceUpCard, true);
        p1.reportDraw(1, secondFaceUpCard);
        p0.reportDraw(1, secondFaceUpCard);
        System.out.println("Player0's matrix: confirm that " + cardStr + " shows 4, which represents OPP_FROM_DISCARD");
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();
        System.out.println("The number of cards left in deck is: " + p0.myTracker.numDeckCards);
        
        System.out.println("----------  Player 1 discarding  ----------");
        cardStr = "2C"; //need to change
        System.out.println("player1 discards " + cardStr);
        Card discardedByP1 = Card.strCardMap.get(cardStr);
        p0.reportDiscard(1, discardedByP1);
        p1.reportDiscard(1, discardedByP1);
        System.out.println("Discarded pile: " + p0.myTracker.discardCards);
        System.out.println("Player0's matrix: confirm that " + cardStr + " shows 7, which represents OPP_TOP_DISCARD");        
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();
        System.out.println("The number of cards left in deck is: " + p0.myTracker.numDeckCards);

        System.out.println("----------  Player 0 Drawing from deck ----------");
        cardStr = "QC"; //need to change
        System.out.println("player0 draws from deck: " + cardStr);
        Card stockCard = Card.strCardMap.get(cardStr);
        p0.willDrawFaceUpCardTest(p0.myTracker.discardCards.peek(), false);
        p0.reportDraw(0, stockCard);
        p1.reportDraw(0, null);
        System.out.println("Player0's matrix: confirm that " + cardStr + " shows 1, which represents SELF_FROM_STOCK");
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();
        System.out.println("The number of cards left in deck is: " + p0.myTracker.calculateDelta());
        
        System.out.println("----------  Player 0 discarding  ----------");
        cardStr = "2S"; //need to change
        System.out.println("player0 discards " + cardStr);
        discardedByP0 =  Card.strCardMap.get(cardStr);
        System.out.println("Player0's matrix: confirm that " + cardStr + " shows 5, which represents SELF_TOP_DISCARD");
        System.out.println("Player0's matrix: confirm that " + p0.myTracker.discardCards.peek() + " shows 8, which represents OPP_BURIED_DISCARD");
        p0.reportDiscard(0, discardedByP0);
        p1.reportDiscard(0, discardedByP0);      
        System.out.println("Discarded pile: " + p0.myTracker.discardCards);  
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();
        System.out.println("The number of cards left in deck is: " + p0.myTracker.numDeckCards);

        System.out.println("----------  Player 1 Drawing from deck ----------");
        cardStr = "KC"; //need to change
        System.out.println("player1 draws from deck:" + cardStr + "(player0 can't know the card so null will be passed)");//need to change
        stockCard = Card.strCardMap.get(cardStr);
        p1.willDrawFaceUpCardTest(p1.myTracker.discardCards.peek(), false);
        p0.reportDraw(1, null);  // don't know what the card is
        p1.reportDraw(1, stockCard);
        System.out.println("Player0's matrix: confirm that " + cardStr + " shows 0, which represents UNKNOWN");
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();
        System.out.println("The number of cards left in deck is: " + p0.myTracker.calculateDelta());

        System.out.println("----------  Player 1 discarding  ----------");
        cardStr = "7H"; //need to change
        System.out.println("player1 discards " + cardStr);
        discardedByP1 = Card.strCardMap.get(cardStr);
        System.out.println("Player0's matrix: confirm that " + cardStr + " shows 7, which represents OPP_TOP_DISCARD");        
        System.out.println("Player0's matrix: confirm that " + p0.myTracker.discardCards.peek() + " shows 6, which represents SELF_BURIED_DISCARD");
        p0.reportDiscard(1, discardedByP1);
        p1.reportDiscard(1, discardedByP1);
        System.out.println("Discarded pile: " + p0.myTracker.discardCards);      
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();
        System.out.println("The number of cards left in deck is: " + p0.myTracker.numDeckCards);
    }

    public static void test2(StateTrackerPlayer p0, StateTrackerPlayer p1){
        System.out.println("========== Test 2 (p0 starts, rejects the first face-up card, and p1 rejects it either) ==========");
        ArrayList<Card> oppCards = OurUtil.makeHand(new String[] {"AC", "AH", "AD", "2S", "3C", "4H", "5D", "6D", "7H", "8S"});
        ArrayList<Card> myCards = OurUtil.makeHand(new String[] {"AS", "2C", "2H", "2D", "3S", "4C", "5H", "6S", "7D", "8C"});
        Card[] cardsOfPlayer0 = new Card[10]; 
        Card[] cardsOfPlayer1 = new Card[10]; 
        String cardStr = "";
        
        for(int i = 0; i < 10; i++){
            cardsOfPlayer1[i] = oppCards.get(i);
            cardsOfPlayer0[i] = myCards.get(i);
        }

        System.out.println("player0 starts");
        p0.startGame(0, 0, cardsOfPlayer0);
        p1.startGame(1, 0, cardsOfPlayer1);
        System.out.println("Player 0 is dealt " + Arrays.toString(cardsOfPlayer0));
        System.out.println("Player 1 is dealt " + Arrays.toString(cardsOfPlayer1));
        System.out.println("Player0's matrix: confirm that cards in the hand show 1, which represents SELF_FROM_STOCK");
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();
        
        System.out.println("----------  Player 0 rejects the first face-up card ----------");
        cardStr = "TS"; //need to change
        System.out.println("player0 rejects the first face-up card: " + cardStr);
        Card firstFaceUpCard = Card.strCardMap.get(cardStr);
        p0.willDrawFaceUpCardTest(firstFaceUpCard, false);
        System.out.println("Player0's matrix: confirm that " + cardStr + " shows 5, which represents SELF_TOP_DISCARD");
        System.out.println("We handle this as SELF_TOP_DISCARD for convenience");
        System.out.println("Discarded pile: " + p0.myTracker.discardCards);   
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();

        System.out.println("----------  Player 1 rejects the face-up card  ----------");
        System.out.println("player1 rejects the first face-up card: " + cardStr);
        p1.willDrawFaceUpCardTest(firstFaceUpCard, false); 

        System.out.println("----------  Player 0 draws from deck ----------");
        cardStr = "QC"; //need to change
        System.out.println("player0 draws from deck: " + cardStr);
        Card stockCard = Card.strCardMap.get(cardStr);
        System.out.println("Player0's matrix: confirm that " + p0.myTracker.discardCards.peek() + " shows 7, which represents OPP_TOP_DISCARD");
        p0.reportDraw(0, stockCard);
        p1.reportDraw(0, null);
        System.out.println("Discarded pile: " + p0.myTracker.discardCards);   
        System.out.println("We handle this as OPP_TOP_DISCARD for convenience because the fact that opponent rejected the card should be more important than the fact that player rejected it"); 
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();

        System.out.println("----------  Player 0 discarding  ----------");
        cardStr = "AH"; //need to change
        System.out.println("player0 discards " + cardStr);
        Card discardedByP0 =  Card.strCardMap.get(cardStr);
        System.out.println("Player0's matrix: confirm that " + p0.myTracker.discardCards.peek() + " shows 8, which represents OPP_BURIED_DISCARD");
        p0.reportDiscard(0, discardedByP0);
        p1.reportDiscard(0, discardedByP0);
        System.out.println("Discarded pile: " + p0.myTracker.discardCards);
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        System.out.println("deck list: " + Arrays.toString(p0.myTracker.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.myTracker.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();

    }

    public static void test3(StateTrackerPlayer p0, StateTrackerPlayer p1){
        System.out.println("========== Test 3 (p1 starts, takes the first face-up card ==========");
        ArrayList<Card> oppCards = OurUtil.makeHand(new String[] {"AC", "AH", "AD", "2S", "3C", "4H", "5D", "6D", "7H", "8S"});
        ArrayList<Card> myCards = OurUtil.makeHand(new String[] {"AS", "2C", "2H", "2D", "3S", "4C", "5H", "6S", "7D", "8C"});
        Card[] cardsOfPlayer0 = new Card[10]; 
        Card[] cardsOfPlayer1 = new Card[10]; 
        String cardStr = "";
        
        for(int i = 0; i < 10; i++){
            cardsOfPlayer1[i] = oppCards.get(i);
            cardsOfPlayer0[i] = myCards.get(i);
        }

        System.out.println("player1 starts");
        p0.startGame(0, 0, cardsOfPlayer0);
        p1.startGame(1, 0, cardsOfPlayer1);
        System.out.println("Player 0 is dealt " + Arrays.toString(cardsOfPlayer0));
        System.out.println("Player 1 is dealt " + Arrays.toString(cardsOfPlayer1));
        System.out.println("Player0's matrix: confirm that cards in the hand show 1, which represents SELF_FROM_STOCK");
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        p0.myTracker.displayPROBS();
    
        System.out.println("----------  Player 1 drawing from discarded pile ----------");
        cardStr = "TS"; //need to change
        System.out.println("player1 draws first face-up card: " + cardStr);
        Card firstFaceUpCard = Card.strCardMap.get(cardStr);
        p1.willDrawFaceUpCardTest(firstFaceUpCard, true);
        p0.reportDraw(1, firstFaceUpCard);
        p1.reportDraw(1, firstFaceUpCard);
        System.out.println("Player0's matrix: confirm that " + cardStr + " shows 4, which represents OPP_FROM_DISCARD");
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        p0.myTracker.displayPROBS();
    }

    public static void testPlayerDeclineAndPlayerTake(StateTrackerPlayer p0, StateTrackerPlayer p1, int startPlayer){
        //case 2 or 5
        System.out.println("========== Test (one starts, declines the first face-up card and other takes it ==========");
        ArrayList<Card> oppCards = OurUtil.makeHand(new String[] {"AC", "AH", "AD", "2S", "3C", "4H", "5D", "6D", "7H", "8S"});
        ArrayList<Card> myCards = OurUtil.makeHand(new String[] {"AS", "2C", "2H", "2D", "3S", "4C", "5H", "6S", "7D", "8C"});
        Card[] cardsOfPlayer0 = new Card[10]; 
        Card[] cardsOfPlayer1 = new Card[10]; 
        String cardStr = "";
        
        for(int i = 0; i < 10; i++){
            cardsOfPlayer1[i] = oppCards.get(i);
            cardsOfPlayer0[i] = myCards.get(i);
        }
        
        System.out.println("player " + startPlayer + " starts first");
        p0.startGame(0, startPlayer, cardsOfPlayer0);
        p1.startGame(1, startPlayer, cardsOfPlayer1);

        System.out.println("Player 0 is dealt " + Arrays.toString(cardsOfPlayer0));
        System.out.println("Player 1 is dealt " + Arrays.toString(cardsOfPlayer1));
        System.out.println("Player0's matrix: confirm that cards in the hand show 1, which represents SELF_FROM_STOCK");
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        p0.myTracker.displayPROBS();
        
        System.out.println("----------  Player" + startPlayer + " declines the first card ----------");
        cardStr = "TS"; //need to change
        System.out.println("player" + startPlayer +  " declines first face-up card: " + cardStr);
        Card firstFaceUpCard = Card.strCardMap.get(cardStr);
        if(startPlayer == 0){
            p0.willDrawFaceUpCardTest(firstFaceUpCard, false);
        }
        else{
            p1.willDrawFaceUpCardTest(firstFaceUpCard, false);
        }
            
        
        int secondPlayer = startPlayer ^ 1;
        //the player that starts the game second
        System.out.println("----------  Player" + secondPlayer + " takes the first card ----------");
        System.out.println("player" + secondPlayer + " takes first face-up card: " + cardStr);
        // Card firstFaceUpCard = Card.strCardMap.get(cardStr);
        if(secondPlayer == 1){
            p1.willDrawFaceUpCardTest(firstFaceUpCard, true);
            p1.reportDraw(1, firstFaceUpCard);
            p0.reportDraw(1, firstFaceUpCard);
        }
        else{
            p0.willDrawFaceUpCardTest(firstFaceUpCard, true);
            p0.reportDraw(0, firstFaceUpCard);
            p1.reportDraw(0, firstFaceUpCard);
        }
        System.out.println("Player0's matrix: confirm that first card player "+  secondPlayer + " took has state ???");
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        p0.myTracker.displayPROBS();
    }

    public static void testBothPlayerDecline(StateTrackerPlayer p0, StateTrackerPlayer p1){
        System.out.println("========== Test both declines the first card ==========");
        ArrayList<Card> oppCards = OurUtil.makeHand(new String[] {"AC", "AH", "AD", "2S", "3C", "4H", "5D", "6D", "7H", "8S"});
        ArrayList<Card> myCards = OurUtil.makeHand(new String[] {"AS", "2C", "2H", "2D", "3S", "4C", "5H", "6S", "7D", "8C"});
        Card[] cardsOfPlayer0 = new Card[10]; 
        Card[] cardsOfPlayer1 = new Card[10]; 
        String cardStr = "";
        
        for(int i = 0; i < 10; i++){
            cardsOfPlayer1[i] = oppCards.get(i);
            cardsOfPlayer0[i] = myCards.get(i);
        }
        System.out.println("player 0 starts first");
        p0.startGame(0, 1, cardsOfPlayer0);
        p1.startGame(1, 1, cardsOfPlayer1);

        System.out.println("Player 0 is dealt " + Arrays.toString(cardsOfPlayer0));
        System.out.println("Player 1 is dealt " + Arrays.toString(cardsOfPlayer1));
        System.out.println("Player0's matrix: confirm that cards in the hand show 1, which represents SELF_FROM_STOCK");
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        p0.myTracker.displayPROBS();
        
        System.out.println("----------  Player 1 declines the first card ----------");
        cardStr = "TS"; //need to change
        System.out.println("player 1 declines first face-up card: " + cardStr);
        Card firstFaceUpCard = Card.strCardMap.get(cardStr);
        p1.willDrawFaceUpCardTest(firstFaceUpCard, false);

        System.out.println("----------  Player 0 also declines the first card ----------");
        p0.willDrawFaceUpCardTest(firstFaceUpCard, false);

        System.out.println("----------  Player 1 draws from stock ----------");
        cardStr = "KS";
        Card card = Card.strCardMap.get(cardStr);
        System.out.println("player 1 draws the card: " + cardStr + " from the stock");
        p1.reportDraw(1, card);
        p0.reportDraw(1, null);
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        p0.myTracker.displayPROBS();
        System.out.println("Player 0 knows nothing about the card so the state is unchanged");
        
        System.out.println("----------  Player 1 discarding  ----------");
        cardStr = "2C"; //need to change
        System.out.println("player 1 discards " + cardStr);
        Card discardedByP1 =  Card.strCardMap.get(cardStr);
        System.out.println("Player0's matrix: confirm that " + cardStr + " shows 7, which represents OPP_TOP_DISCARD");
        p0.reportDiscard(1, discardedByP1);
        p1.reportDiscard(1, discardedByP1);      
        System.out.println("Discarded pile: " + p0.myTracker.discardCards);  
        p0.myTracker.displaySTATES();
        System.out.println("Probability: ");
        // System.out.println("deck list: " + Arrays.toString(p0.numDeckCards_t.toArray()) + ", unknown list: " + Arrays.toString(p0.numOppCardsUnknown_t.toArray()));
        p0.myTracker.displayPROBS();
        // System.out.println("The number of cards left in deck is: " + p0.numDeckCards);
    }

    public static void testOppKnownCards(StateTrackerPlayer p0, StateTrackerPlayer p1){
        System.out.println("========== Test OppKnownCards ==========");
        ArrayList<Card> oppCards = OurUtil.makeHand(new String[] {"AC", "AH", "AD", "2S", "3C", "4H", "5D", "6D", "7H", "8S"});
        ArrayList<Card> myCards = OurUtil.makeHand(new String[] {"AS", "2C", "2H", "2D", "3S", "4C", "5H", "6S", "7D", "8C"});
        Card[] cardsOfPlayer0 = new Card[10]; 
        Card[] cardsOfPlayer1 = new Card[10]; 
        String cardStr = "";
        
        for(int i = 0; i < 10; i++){
            cardsOfPlayer1[i] = oppCards.get(i);
            cardsOfPlayer0[i] = myCards.get(i);
            // cardsOnPile.remove(Card.strIdMap.get(oppCards.get(i).getSuit()));
            // cardsOnPile.remove(Card.strIdMap.get(myCards.get(i)));
        }

        System.out.println("player0 starts");
        p0.startGame(0, 0, cardsOfPlayer0);
        p1.startGame(1, 0, cardsOfPlayer1);
        System.out.println("Player 0 is dealt " + Arrays.toString(cardsOfPlayer0));
        System.out.println("Player 1 is dealt " + Arrays.toString(cardsOfPlayer1));
        System.out.println("The number of cards left in deck is: " + p0.myTracker.numDeckCards);
        
        System.out.println("----------  Player 0 drawing from discarded pile ----------");
        cardStr = "TS"; //need to change
        System.out.println("player0 draws first face-up card: " + cardStr);
        Card firstFaceUpCard = Card.strCardMap.get(cardStr);
        p0.willDrawFaceUpCardTest(firstFaceUpCard, true);
        p0.reportDraw(0, firstFaceUpCard);
        p1.reportDraw(0, firstFaceUpCard);
        System.out.println("numOppCardsKnown: " + (StateTracker.HAND_SIZE - p0.myTracker.numOppCardsUnknown) + ", oppCardsKnown: " + Arrays.toString(p0.myTracker.getOppCardsKnown().toArray()));

        System.out.println("----------  Player 0 discarding  ----------");
        cardStr = "AH"; //need to change
        System.out.println("player0 discards " + cardStr);
        Card discardedByP0 =  Card.strCardMap.get(cardStr);
        p0.reportDiscard(0, discardedByP0);
        p1.reportDiscard(0, discardedByP0);
        System.out.println("numOppCardsKnown: " + (StateTracker.HAND_SIZE - p0.myTracker.numOppCardsUnknown) + ", oppCardsKnown: " + Arrays.toString(p0.myTracker.getOppCardsKnown().toArray()));

        System.out.println("----------  Player 1 drawing from discarded pile ----------");
        cardStr = "AH"; //need to change
        System.out.println("player1 draws from the discarded pile" + cardStr);
        Card secondFaceUpCard = Card.strCardMap.get(cardStr);
        p1.willDrawFaceUpCardTest(secondFaceUpCard, true);
        p1.reportDraw(1, secondFaceUpCard);
        p0.reportDraw(1, secondFaceUpCard);
        System.out.println("numOppCardsKnown: " + (StateTracker.HAND_SIZE - p0.myTracker.numOppCardsUnknown) + ", oppCardsKnown: " + Arrays.toString(p0.myTracker.getOppCardsKnown().toArray()));
        
        System.out.println("----------  Player 1 discarding  ----------");
        cardStr = "3C"; //need to change
        System.out.println("player1 discards " + cardStr);
        Card discardedByP1 = Card.strCardMap.get(cardStr);
        p0.reportDiscard(1, discardedByP1);
        p1.reportDiscard(1, discardedByP1);
        System.out.println("numOppCardsKnown: " + (StateTracker.HAND_SIZE - p0.myTracker.numOppCardsUnknown) + ", oppCardsKnown: " + Arrays.toString(p0.myTracker.getOppCardsKnown().toArray()));

        System.out.println("----------  Player 0 Drawing from deck ----------");
        cardStr = "QC"; //need to change
        System.out.println("player0 draws from deck: " + cardStr);
        Card stockCard = Card.strCardMap.get(cardStr);
        p0.willDrawFaceUpCardTest(p0.myTracker.discardCards.peek(), false);
        p0.reportDraw(0, stockCard);
        p1.reportDraw(0, null);
        System.out.println("numOppCardsKnown: " + (StateTracker.HAND_SIZE - p0.myTracker.numOppCardsUnknown) + ", oppCardsKnown: " + Arrays.toString(p0.myTracker.getOppCardsKnown().toArray()));
        
        System.out.println("----------  Player 0 discarding  ----------");
        cardStr = "2S"; //need to change
        System.out.println("player0 discards " + cardStr);
        discardedByP0 =  Card.strCardMap.get(cardStr);
        System.out.println("Player0's matrix: confirm that " + cardStr + " shows 5, which represents SELF_TOP_DISCARD");
        System.out.println("Player0's matrix: confirm that " + p0.myTracker.discardCards.peek() + " shows 8, which represents OPP_BURIED_DISCARD");
        p0.reportDiscard(0, discardedByP0);
        p1.reportDiscard(0, discardedByP0);      
        System.out.println("numOppCardsKnown: " + (StateTracker.HAND_SIZE - p0.myTracker.numOppCardsUnknown) + ", oppCardsKnown: " + Arrays.toString(p0.myTracker.getOppCardsKnown().toArray()));

        System.out.println("----------  Player 1 Drawing from deck ----------");
        cardStr = "KC"; //need to change
        System.out.println("player1 draws from deck:" + cardStr + "(player0 can't know the card so null will be passed)");//need to change
        stockCard = Card.strCardMap.get(cardStr);
        p1.willDrawFaceUpCardTest(p1.myTracker.discardCards.peek(), false);
        p0.reportDraw(1, null);  // don't know what the card is
        p1.reportDraw(1, stockCard);
        System.out.println("numOppCardsKnown: " + (StateTracker.HAND_SIZE - p0.myTracker.numOppCardsUnknown) + ", oppCardsKnown: " + Arrays.toString(p0.myTracker.getOppCardsKnown().toArray()));

        System.out.println("----------  Player 1 discarding  ----------");
        cardStr = "AH"; //need to change
        System.out.println("player1 discards " + cardStr);
        discardedByP1 = Card.strCardMap.get(cardStr);
        p0.reportDiscard(1, discardedByP1);
        p1.reportDiscard(1, discardedByP1);
        System.out.println("numOppCardsKnown: " + (StateTracker.HAND_SIZE - p0.myTracker.numOppCardsUnknown) + ", oppCardsKnown: " + Arrays.toString(p0.myTracker.getOppCardsKnown().toArray()));
    }

    public static void main(String[] args){
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        StateTrackerPlayer player0 = new StateTrackerPlayer(params);
        StateTrackerPlayer player1 = new StateTrackerPlayer(params);
        // OurSimpleGinRummyPlayer player1 = new KnockOnGinPlayer(params);
        GinRummyGame game = new GinRummyGame(player0, player1);
        // game.setPlayVerbose(true);
        // game.play();
        // test1(player0, player1);
        // test2(player0, player1);
        // testPlayerDeclineAndPlayerTake(player0, player1, 0); //case 2
        // testPlayerDeclineAndPlayerTake(player0, player1, 1); //case 5
        // test3(player0, player1);
        // testBothPlayerDecline(player0, player1); //case 6
        // testOppKnownCards(player0, player1);

        
        //  // System.out.println("---------- Set up ----------");   
        //  StateTrackerPlayer player0 = new SimpleGinRummyPlayer();
        //  // SimpleGinRummyPlayer player1 = new SimpleGinRummyPlayer();
        //  // RuleBasedPlayer player1 = new RuleBasedPlayer();
        //  StateTrackerPlayer player1 = new StateTrackerPlayer();
        //  GinRummyGame game = new GinRummyGame(player0, player1);
        //  // System.out.println("----------------------------------------");
 
        //  System.out.println("---------- Single Game ----------");
        //  GinRummyGame.setPlayVerbose(true);
        //  player0.setDisplaySTATES(true);
        //  player0.setDisplayPROBS(true);
        //  player0.setTesting(true);
        //  game.play();
        //  System.out.println("----------------------------------------");
        //  System.out.println();


        // System.out.println("---------- HashMap ----------");
        // Map<Card, Double> cardToProb = player0.myTracker.getCardToProbMapWithHighestProb(10);
        // StateTracker.printCardToDoublebMap(cardToProb);
    }
}