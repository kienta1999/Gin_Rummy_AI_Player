package players;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.Stack;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import ginrummy.Card;
import ginrummy.GinRummyPlayer;
// import jdk.internal.jshell.tool.resources.l10n;


public class StateTrackerPlayer_UnitTests {
    public static int HAND_SIZE = 10;
    public static Random random = new Random(20200605);
    public static int NUM_TESTS = 1000;

    @Test
    public void testStartGame(){
        for(int t = 0; t < NUM_TESTS; t++){
            ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer myPlayer = new StateTrackerPlayer(params);
            Stack<Card> shuffledCards = Card.getShuffle(random.nextInt());
            Card[] myCards = new Card[HAND_SIZE];
            for(int i = 0; i < HAND_SIZE; i++){
                myCards[i] = shuffledCards.pop();
            }
    
            myPlayer.startGame(0, 0, myCards);
            double[][][] matrix = myPlayer.getMatrix();
            for(Card card: myCards){
                assertEquals(StateTracker.SELF_FROM_STOCK, matrix[card.getSuit()][card.getRank()][StateTracker.STATE]);
                assertEquals(StateTracker.KNOW_WHERE_IT_IS, matrix[card.getSuit()][card.getRank()][StateTracker.PROB_IN_STOCK]);
            }
            for(Card card: shuffledCards){
                assertEquals(StateTracker.INITIAL_PROB, matrix[card.getSuit()][card.getRank()][StateTracker.PROB_IN_STOCK]);
            }
        }
    }

    // test for willDrawFaceUpCard and STATE for the inital face-up card
    // three cases are being handled
    // (1) our player starts and takes the initial face-up card
    // (2) our player starts and rejects the initial face-up card, and the opponent takes the card
    // (3) our player starts and rejects the initial face-up card, and the opponent also rejects the card, so our player draws a card from deck
    @Test
    public void testWillDrawFaceUpCard1State(){
        for(int t = 0; t < NUM_TESTS; t++){
            ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer player0 = new StateTrackerPlayer(params);
            OurSimpleGinRummyPlayer player1 = new OurSimpleGinRummyPlayer();
            Stack<Card> shuffledCards = Card.getShuffle(random.nextInt());
            Card[] Cards0 = new Card[HAND_SIZE];
            Card[] Cards1 = new Card[HAND_SIZE];
            for(int i = 0; i < HAND_SIZE; i++){
                Cards0[i] = shuffledCards.pop();
                Cards1[i] = shuffledCards.pop();
            }
            Card faceUpCard = shuffledCards.pop();
            Card drawCard = null;
            
            player0.startGame(0, 0, Cards0);
            player1.startGame(1, 0, Cards1);
            boolean willDraw0 = player0.willDrawFaceUpCard(faceUpCard);
            boolean willDraw1 = false;
            
            //our player takes the initial face-up card
            if(willDraw0){
                player0.reportDraw(0, faceUpCard);
                player1.reportDraw(0, faceUpCard);
            }
            //our player rejects the initial face-up card
            else{
                willDraw1 = player1.willDrawFaceUpCard(faceUpCard);
                // the opponent takes the initial face-up card
                if(willDraw1){
                    player0.reportDraw(1, faceUpCard);
                    player1.reportDraw(1, faceUpCard);
                }
                // the opponent also rejects the initial face-up card so our player draws a card from deck
                else{
                    drawCard = shuffledCards.pop();
                    player0.reportDraw(0, drawCard);
                    player1.reportDraw(0, null);
                }

            }
            
            double[][][] matrix = player0.getMatrix();

            if(willDraw0){
                // our player draws the inital face-up card before the opponent so the state should be SELF_FROM_START
                assertEquals(StateTracker.SELF_FROM_START, matrix[faceUpCard.getSuit()][faceUpCard.getRank()][StateTracker.STATE]);
                assertEquals(StateTracker.KNOW_WHERE_IT_IS, matrix[faceUpCard.getSuit()][faceUpCard.getRank()][StateTracker.PROB_IN_STOCK]);
            }
            else if(willDraw1){
                // the opponet draws the initial face-up card after our player rejects it so it should be seen as OPP_FROM_DISCARD
                assertEquals(StateTracker.OPP_FROM_DISCARD, matrix[faceUpCard.getSuit()][faceUpCard.getRank()][StateTracker.STATE]);
                assertEquals(StateTracker.KNOW_WHERE_IT_IS, matrix[faceUpCard.getSuit()][faceUpCard.getRank()][StateTracker.PROB_IN_STOCK]);
            }
            else{
                // both player rejected the inital face-up card but it seems more important to keep information on the opponent's move
                assertEquals(StateTracker.OPP_TOP_DISCARD, matrix[faceUpCard.getSuit()][faceUpCard.getRank()][StateTracker.STATE]);
                assertEquals(StateTracker.KNOW_WHERE_IT_IS, matrix[faceUpCard.getSuit()][faceUpCard.getRank()][StateTracker.PROB_IN_STOCK]);
                // STATE of a card that our player draws from stock becomes SELF_FROM_STOCK
                assertEquals(StateTracker.SELF_FROM_STOCK, matrix[drawCard.getSuit()][drawCard.getRank()][StateTracker.STATE]);
                assertEquals(StateTracker.KNOW_WHERE_IT_IS, matrix[drawCard.getSuit()][drawCard.getRank()][StateTracker.PROB_IN_STOCK]);
            }
        }        
    }

    // test for willDrawFaceUpCard and STATE for the inital face-up card
    // the other three cases are being handled
    // (4) the opponent starts and takes the initial face-up card
    // (5) the opponent starts and rejects the initial face-up card, and our player takes the card
    // (6) the opponent starts and rejects the initial face-up card, and our player also rejects the card, so the opponent draws a card from deck
    @Test
    public void testWillDrawFaceUpCard2State(){
        for(int t = 0; t < NUM_TESTS; t++){
            ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer player0 = new StateTrackerPlayer(params);
            OurSimpleGinRummyPlayer player1 = new OurSimpleGinRummyPlayer();
            Stack<Card> shuffledCards = Card.getShuffle(random.nextInt());
            Card[] Cards0 = new Card[HAND_SIZE];
            Card[] Cards1 = new Card[HAND_SIZE];
            for(int i = 0; i < HAND_SIZE; i++){
                Cards0[i] = shuffledCards.pop();
                Cards1[i] = shuffledCards.pop();
            }
            Card faceUpCard = shuffledCards.pop();
            Card drawCard = null;
            
            player0.startGame(0, 1, Cards0);
            player1.startGame(1, 1, Cards1);
            boolean willDraw1 = player1.willDrawFaceUpCard(faceUpCard);
            boolean willDraw0 = false;

            // the opponent takes the initial face-up card
            if(willDraw1){
                player0.reportDraw(1, faceUpCard);
                player1.reportDraw(1, faceUpCard);
            }
            // the opponent rejects the initial face-up card
            else {
                willDraw0 = player0.willDrawFaceUpCard(faceUpCard);
                // our player takes the inital face-up card
                if(willDraw0){
                    player0.reportDraw(0, faceUpCard);
                    player1.reportDraw(0, faceUpCard);    
                }
                // our player also rejects the initial face=up card so the opponent draws a card from deck
                else{
                    drawCard = shuffledCards.pop();
                    player0.reportDraw(1, null);
                    player1.reportDraw(1, drawCard);
                }
            }
            
            double[][][] matrix = player0.getMatrix();

            if(willDraw1){
                // the opponent takes the initial face-up card before our player so the state should be OPP_FROM_START
                assertEquals(StateTracker.OPP_FROM_START, matrix[faceUpCard.getSuit()][faceUpCard.getRank()][StateTracker.STATE]);
                assertEquals(StateTracker.KNOW_WHERE_IT_IS, matrix[faceUpCard.getSuit()][faceUpCard.getRank()][StateTracker.PROB_IN_STOCK]);
            }
            else if(willDraw0){
                // our player take the initial face-up card after the opponent rejects it so the state should be seen as SELF_FROM_DISCARD
                assertEquals(StateTracker.SELF_FROM_DISCARD, matrix[faceUpCard.getSuit()][faceUpCard.getRank()][StateTracker.STATE]);
                assertEquals(StateTracker.KNOW_WHERE_IT_IS, matrix[faceUpCard.getSuit()][faceUpCard.getRank()][StateTracker.PROB_IN_STOCK]);
            }
            else{
                // both player rejected the inital face-up card but it seems more important to keep information on the opponent's move (so this is not SELF_TOP_DISCARD although the last person who rejected the card is SELF)
                assertEquals(StateTracker.OPP_TOP_DISCARD, matrix[faceUpCard.getSuit()][faceUpCard.getRank()][StateTracker.STATE]);
                assertEquals(StateTracker.KNOW_WHERE_IT_IS, matrix[faceUpCard.getSuit()][faceUpCard.getRank()][StateTracker.PROB_IN_STOCK]);
                assertEquals(StateTracker.UNKNOWN, matrix[drawCard.getSuit()][drawCard.getRank()][StateTracker.STATE]);
            }
        }        
    }


    //let the two player starts the game - method to avoid code duplication
    public void twoPlayerStartGame(StateTrackerPlayer p0, StateTrackerPlayer p1, Stack<Card> shuffledCards){
            Card[] player0Cards = new Card[HAND_SIZE];
            Card[] player1Cards = new Card[HAND_SIZE];
            
            for(int i = 0; i < HAND_SIZE; i++){
                player0Cards[i] = shuffledCards.pop();
                player1Cards[i] = shuffledCards.pop();
            }
            p0.startGame(0, 0, player0Cards);
            p1.startGame(1, 0, player1Cards);

        Card firstFaceUpCard = shuffledCards.pop();
        p0.willDrawFaceUpCardTest(firstFaceUpCard, false);
        p1.willDrawFaceUpCardTest(firstFaceUpCard, false);

    }
    
    //case 1: we are player 0, player 0 draws from discarded pile - (SELF_FROM_DISCARD)
    @Test
    public void testReportDrawCase1State(){
        for(int t = 0; t < NUM_TESTS; t++){
            ParamList params0 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            ParamList params1 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer p0 = new StateTrackerPlayer(params0);
            StateTrackerPlayer p1 = new StateTrackerPlayer(params1);
            Stack<Card> shuffledCards = Card.getShuffle(random.nextInt());
            twoPlayerStartGame(p0, p1, shuffledCards);
            
            Card nextCard = shuffledCards.pop();
            
            //player 1 draws from stock
            p1.reportDraw(1, nextCard);
            p0.reportDraw(1, null);

            //player 1 discards
            Card discardedCard = p1.getDiscard();
            p1.reportDiscard(1, discardedCard);
            p0.reportDiscard(1, discardedCard);
            
            //player 0 picks up the discarded card
            p1.reportDraw(0, discardedCard);
            p0.reportDraw(0, discardedCard);
            assertEquals(StateTracker.SELF_FROM_DISCARD, p0.getMatrix()[discardedCard.getSuit()][discardedCard.getRank()][StateTracker.STATE]);
            assertEquals(StateTracker.KNOW_WHERE_IT_IS, p0.getMatrix()[discardedCard.getSuit()][discardedCard.getRank()][StateTracker.PROB_IN_STOCK]);

        }
    }

    //case 2: we are player 0, player 1 draws from discarded pile - (OPP_FROM_DISCARD)
    @Test
    public void testReportDrawCase2State(){
        for(int t = 0; t < NUM_TESTS; t++){
            ParamList params0 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            ParamList params1 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer p0 = new StateTrackerPlayer(params0);
            StateTrackerPlayer p1 = new StateTrackerPlayer(params1);
            Stack<Card> shuffledCards = Card.getShuffle(random.nextInt());
            twoPlayerStartGame(p0, p1, shuffledCards);
            
            Card nextCard = shuffledCards.pop();
            //player 0 draws from stock
            p0.reportDraw(0, nextCard);
            p1.reportDraw(0, null);

            //player 0 discards
            Card discardedCard = p0.getDiscard();
            p0.reportDiscard(0, discardedCard);
            p1.reportDiscard(0, discardedCard);

            //player 1 picks up from discarded pile
            p1.reportDraw(1, discardedCard);
            p0.reportDraw(1, discardedCard);

            assertEquals(StateTracker.OPP_FROM_DISCARD, p0.getMatrix()[discardedCard.getSuit()][discardedCard.getRank()][StateTracker.STATE]);
            assertEquals(StateTracker.KNOW_WHERE_IT_IS, p0.getMatrix()[discardedCard.getSuit()][discardedCard.getRank()][StateTracker.PROB_IN_STOCK]);
        }
    }

    //case 3: we are player 0, player 0 draws from stock - (SELF_FROM_STOCK)
    @Test
    public void testReportDrawCase3State(){
        for(int t = 0; t < NUM_TESTS; t++){
            ParamList params0 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            ParamList params1 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer p0 = new StateTrackerPlayer(params0);
            StateTrackerPlayer p1 = new StateTrackerPlayer(params1);
            Stack<Card> shuffledCards = Card.getShuffle(random.nextInt());
            twoPlayerStartGame(p0, p1, shuffledCards);
            
            //player 0 draws from stock
            Card nextCard = shuffledCards.pop();
            p0.reportDraw(0, nextCard);
            p1.reportDraw(0, null);
            assertEquals(p0.getMatrix()[nextCard.getSuit()][nextCard.getRank()][StateTracker.STATE], StateTracker.SELF_FROM_STOCK);
            assertEquals(p0.getMatrix()[nextCard.getSuit()][nextCard.getRank()][StateTracker.PROB_IN_STOCK], StateTracker.KNOW_WHERE_IT_IS);
        }
    }

    //case 4: we are player 0, player 1 draws from stock - (UNKNOWN)
    @Test
    public void testReportDrawCase4State(){
        for(int t = 0; t < NUM_TESTS; t++){
            ParamList params0 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            ParamList params1 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer p0 = new StateTrackerPlayer(params0);
            StateTrackerPlayer p1 = new StateTrackerPlayer(params1);
            Stack<Card> shuffledCards = Card.getShuffle(random.nextInt());
            twoPlayerStartGame(p0, p1, shuffledCards);
            
            //player 1 draws from stock
            Card nextCard = shuffledCards.pop();
            p0.reportDraw(1, null);
            p1.reportDraw(1, nextCard);
            assertEquals(p0.getMatrix()[nextCard.getSuit()][nextCard.getRank()][StateTracker.STATE], StateTracker.UNKNOWN);
            assertFalse(p0.getMatrix()[nextCard.getSuit()][nextCard.getRank()][StateTracker.PROB_IN_STOCK] == StateTracker.KNOW_WHERE_IT_IS);
        }
    }

    //Case 1: player discard and then the card is buried - (SELF_TOP_DISCARD and SELF_BURIED_DISCARD)
    @Test
    public void testReportDiscard1State(){

        for(int t = 0; t < NUM_TESTS; t++){
            ParamList params0 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            ParamList params1 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer p0 = new StateTrackerPlayer(params0);
            StateTrackerPlayer p1 = new StateTrackerPlayer(params1);
            Stack<Card> shuffledCards = Card.getShuffle(random.nextInt());
            twoPlayerStartGame(p0, p1, shuffledCards);

            Card nextCard = shuffledCards.pop();
            //player 0 draws from stock
            p0.reportDraw(0, nextCard);
            p1.reportDraw(0, null);

            //player 0 discards
            Card discardedCard = p0.getDiscard();
            p0.reportDiscard(0, discardedCard);
            p1.reportDiscard(0, discardedCard);

            //now the card is top faced up card (from player)
            assertEquals(StateTracker.SELF_TOP_DISCARD, p0.getMatrix()[discardedCard.getSuit()][discardedCard.getRank()][StateTracker.STATE]);
            assertEquals(StateTracker.KNOW_WHERE_IT_IS, p0.getMatrix()[discardedCard.getSuit()][discardedCard.getRank()][StateTracker.PROB_IN_STOCK]);
        
            nextCard = shuffledCards.pop();
    
            //player 1 draw from stock
            p0.reportDraw(1, null);
            p1.reportDraw(1, nextCard);
            
            //player 1 discards
            Card discardedCardFrom1 = p1.getDiscard();
            p0.reportDiscard(1, discardedCardFrom1);
            p1.reportDiscard(1, discardedCardFrom1);

            //now the card is buried (from player)
            assertEquals(StateTracker.SELF_BURIED_DISCARD, p0.getMatrix()[discardedCard.getSuit()][discardedCard.getRank()][StateTracker.STATE]);
            assertEquals(StateTracker.KNOW_WHERE_IT_IS, p0.getMatrix()[discardedCard.getSuit()][discardedCard.getRank()][StateTracker.PROB_IN_STOCK]);
        }
    }

    //case 2: opponent discarded and then the card is buried - (OPP_TOP_DISCARD and OPP_BURIED_DISCARD)
    @Test
    public void testReportDiscard2State(){
        for(int t = 0; t < NUM_TESTS; t++){
            ParamList params0 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            ParamList params1 = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer p0 = new StateTrackerPlayer(params0);
            StateTrackerPlayer p1 = new StateTrackerPlayer(params1);
            Stack<Card> shuffledCards = Card.getShuffle(random.nextInt());
            twoPlayerStartGame(p0, p1, shuffledCards);

            Card nextCard = shuffledCards.pop();
            //player 1 draws from stock
            p1.reportDraw(1, nextCard);
            p0.reportDraw(1, null);

            //player 1 discards
            Card discardedCard = p1.getDiscard();
            p1.reportDiscard(1, discardedCard);
            p0.reportDiscard(1, discardedCard);

            //now the card is top faced up card (from opponent)
            assertEquals(StateTracker.OPP_TOP_DISCARD, p0.getMatrix()[discardedCard.getSuit()][discardedCard.getRank()][StateTracker.STATE]);
            assertEquals(StateTracker.KNOW_WHERE_IT_IS, p0.getMatrix()[discardedCard.getSuit()][discardedCard.getRank()][StateTracker.PROB_IN_STOCK]);
        
            nextCard = shuffledCards.pop();
    
            //player 0 draw from stock
            p0.reportDraw(0, nextCard);
            p1.reportDraw(0, null);
            
            //player 0 discards
            Card discardedCardFrom0 = p1.getDiscard();
            p0.reportDiscard(0, discardedCardFrom0);
            p1.reportDiscard(0, discardedCardFrom0);

            //now the card is buried
            assertEquals(StateTracker.OPP_BURIED_DISCARD, p0.getMatrix()[discardedCard.getSuit()][discardedCard.getRank()][StateTracker.STATE]);
            assertEquals(StateTracker.KNOW_WHERE_IT_IS, p0.getMatrix()[discardedCard.getSuit()][discardedCard.getRank()][StateTracker.PROB_IN_STOCK]);
        }
    }

    @Test
    public void testFlowProbability1(){
        for(int t = 0; t < NUM_TESTS; t++){
            int ourPlayer = 0;
            ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer player0 = new StateTrackerPlayer(params);
            OurSimpleGinRummyPlayer player1 = new OurSimpleGinRummyPlayer();
            
            GinRummyPlayer[] players = new GinRummyPlayer[] {player0, player1};
            ArrayList<ArrayList<Card>> hands = new ArrayList<ArrayList<Card>>();
            hands.add(new ArrayList<Card>());
            hands.add(new ArrayList<Card>());
            int startingPlayer = ourPlayer;
            
            int currentPlayer = startingPlayer;
            int opponent = (currentPlayer == 0) ? 1 : 0;
            Stack<Card> deck = Card.getShuffle(random.nextInt());
            hands.get(0).clear();
			hands.get(1).clear();
			for (int i = 0; i < 2 * HAND_SIZE; i++)
				hands.get(i % 2).add(deck.pop());
            for(int i = 0; i < 2; i++){
                Card[] handArr = new Card[HAND_SIZE];
				hands.get(i).toArray(handArr);
				players[i].startGame(i, startingPlayer, handArr); 
            }
            Stack<Card> discards = new Stack<Card>();
            discards.push(deck.pop());
            Card firstFaceUpCard = discards.peek();

            int turnsTaken = 0;
            int testTurns = 30;
            
            for(int ct = 0; ct < testTurns; ct++){
                
                //store information of our player at the beginnig of each turn
                StateTrackerPlayer ourPlayerTracker = (StateTrackerPlayer)players[ourPlayer];
                double[][][] preMatrix = new double[Card.NUM_SUITS][Card.NUM_RANKS][StateTracker.DIMENSION];
                for(int i = 0; i < Card.NUM_SUITS; i++){
                    for(int j = 0; j < Card.NUM_RANKS; j++){
                        preMatrix[i][j][StateTracker.PROB_IN_STOCK] = ourPlayerTracker.getMatrix()[i][j][StateTracker.PROB_IN_STOCK];
                        preMatrix[i][j][StateTracker.STATE] = ourPlayerTracker.getMatrix()[i][j][StateTracker.STATE];
                    }
                }
                int preNumDeckCards = ourPlayerTracker.getNumDeckCards();
                int preNumOppCardsUnknown = ourPlayerTracker.getNumOppCardsUnknown();


                // normal flow of a game
                boolean drawFaceUp = false;
                Card faceUpCard = discards.peek();
                if (!(turnsTaken == 3 && faceUpCard == firstFaceUpCard)) { 
                    // both players declined and 1st player must draw face down
                    drawFaceUp = players[currentPlayer].willDrawFaceUpCard(faceUpCard); //will current player draw the 1st card?
                    
                }
                if (!(!drawFaceUp && turnsTaken < 2 && faceUpCard == firstFaceUpCard)) {
                    Card drawCard = drawFaceUp ? discards.pop() : deck.pop();
                    for (int i = 0; i < 2; i++) 
                        players[i].reportDraw(currentPlayer, (i == currentPlayer || drawFaceUp) ? drawCard : null);

                    Card discardCard = players[currentPlayer].getDiscard();
                    for (int i = 0; i < 2; i++) 
                        players[i].reportDiscard(currentPlayer, discardCard);
                    discards.push(discardCard);
                    

                    //store information of the state of the player at the end of each turn 
                    ourPlayerTracker = (StateTrackerPlayer)players[ourPlayer];
                    double[][][] curMatrix = new double[Card.NUM_SUITS][Card.NUM_RANKS][StateTracker.DIMENSION];
                    for(int i = 0; i < Card.NUM_SUITS; i++){
                        for(int j = 0; j < Card.NUM_RANKS; j++){
                            curMatrix[i][j][StateTracker.PROB_IN_STOCK] = ourPlayerTracker.getMatrix()[i][j][StateTracker.PROB_IN_STOCK];
                            curMatrix[i][j][StateTracker.STATE] = ourPlayerTracker.getMatrix()[i][j][StateTracker.STATE];
                        }
                    }
                    int curNumDeckCards = ourPlayerTracker.getNumDeckCards();
                    int curNumOppCardsUnknown = ourPlayerTracker.getNumOppCardsUnknown();
                    String msg = "turnsTaken: " + turnsTaken + ", currentPlayer is: " + currentPlayer + ", deck size is: " + deck.size() + ", discards size is: " + discards.size();
                            
                    if(turnsTaken > 2){// here we focus on normal rounds in the middle of the game

                        if(currentPlayer == ourPlayer){ // our player's turn
                            // case (i)
                            if(drawFaceUp == true){ // our player drew from the discard pile
                                // no probability has been changed
                                assertEquals(preNumDeckCards, curNumDeckCards);
                                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown);
                                for(int i = 0; i < Card.NUM_SUITS; i++){
                                    for(int j = 0; j < Card.NUM_RANKS; j++){
                                        assertEquals(preMatrix[i][j][StateTracker.PROB_IN_STOCK], curMatrix[i][j][StateTracker.PROB_IN_STOCK], msg);
                                    }
                                }
                            }
                            // case (ii)
                            else{ // our player drew from deck
                                // numDeckCards -> numDeckCards - 1
                                // numOppCardsUnknown -> numOppCardsUnKnown
                                // delta is NOT zero
                                assertEquals(preNumDeckCards - 1, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown);
                                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                                double delta = p_cur - p_pre;
                                msg += ", p_pre: " + p_pre + ", p_cur: " + p_cur + ", delta: " + delta;
                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            if(suit != drawCard.getSuit() || rank != drawCard.getRank()){
                                                double expected = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] + delta;
                                                assertEquals(expected, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                            }
                                            else{
                                                assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK]);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else{ //opponent's turn 
                            // check if our player knew the discardCard was in the opponent's hand or not
                            boolean isKnownCard = (preMatrix[discardCard.getSuit()][discardCard.getRank()][StateTracker.PROB_IN_STOCK] == StateTracker.KNOW_WHERE_IT_IS);
                            
                            //case (iii)
                            if(drawFaceUp == true && isKnownCard){ //opponent drew from the discard pile and discarded a card that a player knew was in the opponent's hand
                                // numDeckCards -> numDeck
                                // numOppCardsUnknown -> numOppCardsUnKnown
                                // delta is zero
                                // probability of cards neighboring the drawCard are changed
                                assertEquals(preNumDeckCards, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown);
                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                            String msg_new = msg + ", p_pre: " + preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                            // update on cards neighboring the drawCard
                                            if(suit == drawCard.getSuit() && (Math.abs(rank - drawCard.getRank()) == 2)){
                                                p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY);
                                                msg_new += ", a";
                                            }
                                            else if(suit == drawCard.getSuit() && (Math.abs(rank - drawCard.getRank()) == 1)){
                                                p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY);
                                                msg_new += ", b";
                                            }
                                            else if(rank == drawCard.getRank()){
                                                p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK);
                                                msg_new += ", c";
                                            }

                                            // update on cards neighboring the discardCard
                                            if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                                msg_new += ", d";
                                            }
                                            else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                                msg_new += ", e";
                                            }
                                            else if(rank == discardCard.getRank()){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                                msg_new += ", f";
                                            }
                                            assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                                        }
                                        else{
                                            assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                        }

                                    }
                                }
                            }
                            //case (iv)
                            else if(drawFaceUp == true && !isKnownCard){ //opponent drew from the discard pile and discarded a card that a player did NOT know was in the opponent's hand
                                // numDeckCards -> numDeck
                                // numOppCardsUnknown -> numOppCardsUnKnown - 1
                                // delta is NOT zero
                                // probability of cards neighboring the drawCard are changed
                                assertEquals(preNumDeckCards, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown - 1, curNumOppCardsUnknown);
                                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                                double delta = p_cur - p_pre;

                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            if(suit != discardCard.getSuit() || rank != discardCard.getRank()){
                                                double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                                String msg_new = msg + ", p_pre: " + preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] + ", delta: " + delta;
                                                // update on cards neighboring the drawCard
                                                if(suit == drawCard.getSuit() && (Math.abs(rank - drawCard.getRank()) == 2)){
                                                    p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY);
                                                    msg_new += ", a";
                                                }
                                                else if(suit == drawCard.getSuit() && (Math.abs(rank - drawCard.getRank()) == 1)){
                                                    p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY);
                                                    msg_new += ", b";
                                                }
                                                else if(rank == drawCard.getRank()){
                                                    p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK);
                                                    msg_new += ", c";
                                                }
    
                                                // update on cards neighboring the discardCard
                                                if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                                    msg_new += ", d";
                                                }
                                                else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                                    msg_new += ", e";
                                                }
                                                else if(rank == discardCard.getRank()){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                                    msg_new += ", f";
                                                }
                                                p += delta;
                                                assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);                                
                                            }
                                            else{
                                                assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                            }
                                        }
                                        
                                    }
                                }
                            }
                            // case (v)
                            else if(drawFaceUp == false && isKnownCard){ //opponent drew from the discard pile and discarded a card that a player knew was in the opponent's hand
                                // numDeckCards -> numDeck - 1
                                // numOppCardsUnknown -> numOppCardsUnKnown + 1
                                // delta is NOT zero
                                // probability of cards neighboring the face-up card (that wasn't drawn) are changed
                                assertEquals(preNumDeckCards - 1, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown + 1, curNumOppCardsUnknown, msg);
                                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                                double delta = p_cur - p_pre;
                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                            String msg_new = msg + ", p_pre: " + preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] + ", delta: " + delta;
                                            // update on cards neighboring the not-drawCard
                                            if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 2)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY);
                                                msg_new += ", a";
                                            }
                                            else if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 1)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY);
                                                msg_new += ", b";
                                            }
                                            else if(rank == faceUpCard.getRank()){
                                                p = p + (1 - p)* params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK);
                                                msg_new += ", c";
                                            }

                                            // update on cards neighboring the discardCard
                                            if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                                msg_new += ", d";
                                            }
                                            else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                                msg_new += ", e";
                                            }
                                            else if(rank == discardCard.getRank()){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                                msg_new += ", f";
                                            }
                                            p += delta;
                                            assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                                        }
                                    }
                                }                    
                            }
                            // case (vi)
                            else if(drawFaceUp == false && !isKnownCard){ //opponent drew from the discard pile and discarded a card that a player did NOT know was in the opponent's hand
                                // numDeckCards -> numDeck - 1
                                // numOppCardsUnknown -> numOppCardsUnKnown
                                // delta is NOT zero
                                // probability of cards neighboring the face-up card (that wasn't drawn) are changed
                                assertEquals(preNumDeckCards - 1, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown, msg);
                                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                                double delta = p_cur - p_pre;
                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            if(suit != discardCard.getSuit() || rank != discardCard.getRank()){
                                                double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                                String msg_new = msg + ", p_pre: " + preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] + ", delta: " + delta;
                                                // update on cards neighboring the not-drawCard
                                                if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 2)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY);
                                                    msg_new += ", a";
                                                }
                                                else if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 1)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY);
                                                    msg_new += ", b";
                                                }
                                                else if(rank == faceUpCard.getRank()){
                                                    p = p + (1 - p)* params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK);
                                                    msg_new += ", c";
                                                }
    
                                                // update on cards neighboring the discardCard
                                                if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                                    msg_new += ", d";
                                                }
                                                else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                                    msg_new += ", e";
                                                }
                                                else if(rank == discardCard.getRank()){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                                    msg_new += ", f";
                                                }
                                                p += delta;
                                                assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                                            }
                                            else{
                                                assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                            }
                                        }
                                    }
                                }             
                            }
                        }
                    }
                }
                turnsTaken++;
                currentPlayer = (currentPlayer == 0) ? 1 : 0;
                opponent = (currentPlayer == 0) ? 1 : 0;
            }
        }
    }

    @Test
    public void testFlowProbability2(){
        for(int t = 0; t < NUM_TESTS; t++){
            int ourPlayer = 1;
            ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer player1 = new StateTrackerPlayer(params);
            OurSimpleGinRummyPlayer player0 = new OurSimpleGinRummyPlayer();
            
            GinRummyPlayer[] players = new GinRummyPlayer[] {player0, player1};
            ArrayList<ArrayList<Card>> hands = new ArrayList<ArrayList<Card>>();
            hands.add(new ArrayList<Card>());
            hands.add(new ArrayList<Card>());
            int startingPlayer = ourPlayer;
            
            int currentPlayer = startingPlayer;
            int opponent = (currentPlayer == 0) ? 1 : 0;
            Stack<Card> deck = Card.getShuffle(random.nextInt());
            hands.get(0).clear();
			hands.get(1).clear();
			for (int i = 0; i < 2 * HAND_SIZE; i++)
				hands.get(i % 2).add(deck.pop());
            for(int i = 0; i < 2; i++){
                Card[] handArr = new Card[HAND_SIZE];
				hands.get(i).toArray(handArr);
				players[i].startGame(i, startingPlayer, handArr); 
            }
            Stack<Card> discards = new Stack<Card>();
            discards.push(deck.pop());
            Card firstFaceUpCard = discards.peek();

            int turnsTaken = 0;
            int testTurns = 30;
            
            for(int ct = 0; ct < testTurns; ct++){
                
                //store information of our player at the beginnig of each turn
                StateTrackerPlayer ourPlayerTracker = (StateTrackerPlayer)players[ourPlayer];
                double[][][] preMatrix = new double[Card.NUM_SUITS][Card.NUM_RANKS][StateTracker.DIMENSION];
                for(int i = 0; i < Card.NUM_SUITS; i++){
                    for(int j = 0; j < Card.NUM_RANKS; j++){
                        preMatrix[i][j][StateTracker.PROB_IN_STOCK] = ourPlayerTracker.getMatrix()[i][j][StateTracker.PROB_IN_STOCK];
                        preMatrix[i][j][StateTracker.STATE] = ourPlayerTracker.getMatrix()[i][j][StateTracker.STATE];
                    }
                }
                int preNumDeckCards = ourPlayerTracker.getNumDeckCards();
                int preNumOppCardsUnknown = ourPlayerTracker.getNumOppCardsUnknown();


                // normal flow of a game
                boolean drawFaceUp = false;
                Card faceUpCard = discards.peek();
                if (!(turnsTaken == 3 && faceUpCard == firstFaceUpCard)) { 
                    // both players declined and 1st player must draw face down
                    drawFaceUp = players[currentPlayer].willDrawFaceUpCard(faceUpCard); //will current player draw the 1st card?
                    
                }
                if (!(!drawFaceUp && turnsTaken < 2 && faceUpCard == firstFaceUpCard)) {
                    Card drawCard = drawFaceUp ? discards.pop() : deck.pop();
                    for (int i = 0; i < 2; i++) 
                        players[i].reportDraw(currentPlayer, (i == currentPlayer || drawFaceUp) ? drawCard : null);

                    Card discardCard = players[currentPlayer].getDiscard();
                    for (int i = 0; i < 2; i++) 
                        players[i].reportDiscard(currentPlayer, discardCard);
                    discards.push(discardCard);
                    

                    //store information of the state of the player at the end of each turn 
                    ourPlayerTracker = (StateTrackerPlayer)players[ourPlayer];
                    double[][][] curMatrix = new double[Card.NUM_SUITS][Card.NUM_RANKS][StateTracker.DIMENSION];
                    for(int i = 0; i < Card.NUM_SUITS; i++){
                        for(int j = 0; j < Card.NUM_RANKS; j++){
                            curMatrix[i][j][StateTracker.PROB_IN_STOCK] = ourPlayerTracker.getMatrix()[i][j][StateTracker.PROB_IN_STOCK];
                            curMatrix[i][j][StateTracker.STATE] = ourPlayerTracker.getMatrix()[i][j][StateTracker.STATE];
                        }
                    }
                    int curNumDeckCards = ourPlayerTracker.getNumDeckCards();
                    int curNumOppCardsUnknown = ourPlayerTracker.getNumOppCardsUnknown();
                    String msg = "turnsTaken: " + turnsTaken + ", currentPlayer is: " + currentPlayer + ", deck size is: " + deck.size() + ", discards size is: " + discards.size();
                            
                    if(turnsTaken > 2){// here we focus on normal rounds in the middle of the game

                        if(currentPlayer == ourPlayer){ // our player's turn
                            // case (i)
                            if(drawFaceUp == true){ // our player drew from the discard pile
                                // no probability has been changed
                                assertEquals(preNumDeckCards, curNumDeckCards);
                                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown);
                                for(int i = 0; i < Card.NUM_SUITS; i++){
                                    for(int j = 0; j < Card.NUM_RANKS; j++){
                                        assertEquals(preMatrix[i][j][StateTracker.PROB_IN_STOCK], curMatrix[i][j][StateTracker.PROB_IN_STOCK], msg);
                                    }
                                }
                            }
                            // case (ii)
                            else{ // our player drew from deck
                                // numDeckCards -> numDeckCards - 1
                                // numOppCardsUnknown -> numOppCardsUnKnown
                                // delta is NOT zero
                                assertEquals(preNumDeckCards - 1, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown);
                                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                                double delta = p_cur - p_pre;
                                msg += ", p_pre: " + p_pre + ", p_cur: " + p_cur + ", delta: " + delta;
                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            if(suit != drawCard.getSuit() || rank != drawCard.getRank()){
                                                double expected = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] + delta;
                                                assertEquals(expected, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                            }
                                            else{
                                                assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK]);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else{ //opponent's turn 
                            // check if our player knew the discardCard was in the opponent's hand or not
                            boolean isKnownCard = (preMatrix[discardCard.getSuit()][discardCard.getRank()][StateTracker.PROB_IN_STOCK] == StateTracker.KNOW_WHERE_IT_IS);
                            
                            //case (iii)
                            if(drawFaceUp == true && isKnownCard){ //opponent drew from the discard pile and discarded a card that a player knew was in the opponent's hand
                                // numDeckCards -> numDeck
                                // numOppCardsUnknown -> numOppCardsUnKnown
                                // delta is zero
                                // probability of cards neighboring the drawCard are changed
                                assertEquals(preNumDeckCards, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown);
                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                            String msg_new = msg + ", p_pre: " + preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                            // update on cards neighboring the drawCard
                                            if(suit == drawCard.getSuit() && (Math.abs(rank - drawCard.getRank()) == 2)){
                                                p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY);
                                                msg_new += ", a";
                                            }
                                            else if(suit == drawCard.getSuit() && (Math.abs(rank - drawCard.getRank()) == 1)){
                                                p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY);
                                                msg_new += ", b";
                                            }
                                            else if(rank == drawCard.getRank()){
                                                p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK);
                                                msg_new += ", c";
                                            }

                                            // update on cards neighboring the discardCard
                                            if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                                msg_new += ", d";
                                            }
                                            else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                                msg_new += ", e";
                                            }
                                            else if(rank == discardCard.getRank()){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                                msg_new += ", f";
                                            }
                                            assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                                        }
                                        else{
                                            assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                        }

                                    }
                                }
                            }
                            //case (iv)
                            else if(drawFaceUp == true && !isKnownCard){ //opponent drew from the discard pile and discarded a card that a player did NOT know was in the opponent's hand
                                // numDeckCards -> numDeck
                                // numOppCardsUnknown -> numOppCardsUnKnown - 1
                                // delta is NOT zero
                                // probability of cards neighboring the drawCard are changed
                                assertEquals(preNumDeckCards, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown - 1, curNumOppCardsUnknown);
                                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                                double delta = p_cur - p_pre;

                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            if(suit != discardCard.getSuit() || rank != discardCard.getRank()){
                                                double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                                String msg_new = msg + ", p_pre: " + preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] + ", delta: " + delta;
                                                // update on cards neighboring the drawCard
                                                if(suit == drawCard.getSuit() && (Math.abs(rank - drawCard.getRank()) == 2)){
                                                    p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY);
                                                    msg_new += ", a";
                                                }
                                                else if(suit == drawCard.getSuit() && (Math.abs(rank - drawCard.getRank()) == 1)){
                                                    p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY);
                                                    msg_new += ", b";
                                                }
                                                else if(rank == drawCard.getRank()){
                                                    p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK);
                                                    msg_new += ", c";
                                                }
    
                                                // update on cards neighboring the discardCard
                                                if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                                    msg_new += ", d";
                                                }
                                                else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                                    msg_new += ", e";
                                                }
                                                else if(rank == discardCard.getRank()){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                                    msg_new += ", f";
                                                }
                                                p += delta;
                                                assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);                                
                                            }
                                            else{
                                                assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                            }
                                        }
                                        
                                    }
                                }
                            }
                            // case (v)
                            else if(drawFaceUp == false && isKnownCard){ //opponent drew from the discard pile and discarded a card that a player knew was in the opponent's hand
                                // numDeckCards -> numDeck - 1
                                // numOppCardsUnknown -> numOppCardsUnKnown + 1
                                // delta is NOT zero
                                // probability of cards neighboring the face-up card (that wasn't drawn) are changed
                                assertEquals(preNumDeckCards - 1, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown + 1, curNumOppCardsUnknown, msg);
                                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                                double delta = p_cur - p_pre;
                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                            String msg_new = msg + ", p_pre: " + preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] + ", delta: " + delta;
                                            // update on cards neighboring the not-drawCard
                                            if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 2)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY);
                                                msg_new += ", a";
                                            }
                                            else if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 1)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY);
                                                msg_new += ", b";
                                            }
                                            else if(rank == faceUpCard.getRank()){
                                                p = p + (1 - p)* params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK);
                                                msg_new += ", c";
                                            }

                                                // update on cards neighboring the discardCard
                                            if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                                msg_new += ", d";
                                            }
                                            else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                                msg_new += ", e";
                                            }
                                            else if(rank == discardCard.getRank()){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                                msg_new += ", f";
                                            }
                                            p += delta;
                                            assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                                        }
                                    }
                                }                    
                            }
                            // case (vi)
                            else if(drawFaceUp == false && !isKnownCard){ //opponent drew from the discard pile and discarded a card that a player did NOT know was in the opponent's hand
                                // numDeckCards -> numDeck - 1
                                // numOppCardsUnknown -> numOppCardsUnKnown
                                // delta is NOT zero
                                // probability of cards neighboring the face-up card (that wasn't drawn) are changed
                                assertEquals(preNumDeckCards - 1, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown, msg);
                                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                                double delta = p_cur - p_pre;
                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            if(suit != discardCard.getSuit() || rank != discardCard.getRank()){
                                                double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                                String msg_new = msg + ", p_pre: " + preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] + ", delta: " + delta;
                                                // update on cards neighboring the not-drawCard
                                                if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 2)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY);
                                                    msg_new += ", a";
                                                }
                                                else if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 1)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY);
                                                    msg_new += ", b";
                                                }
                                                else if(rank == faceUpCard.getRank()){
                                                    p = p + (1 - p)* params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK);
                                                    msg_new += ", c";
                                                }

                                                    // update on cards neighboring the discardCard
                                                if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                                    msg_new += ", d";
                                                }
                                                else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                                    msg_new += ", e";
                                                }
                                                else if(rank == discardCard.getRank()){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                                    msg_new += ", f";
                                                }
                                                p += delta;
                                                assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                                            }
                                            else{
                                                assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                            }
                                        }
                                    }
                                }             
                            }
                        }
                    }
                }
                turnsTaken++;
                currentPlayer = (currentPlayer == 0) ? 1 : 0;
                opponent = (currentPlayer == 0) ? 1 : 0;
            }
        }
    }


    @Test
    public void testOpening1Probability(){
        for(int t = 0; t < NUM_TESTS; t++){
            int ourPlayer = 1;
            ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer player1 = new StateTrackerPlayer(params);
            OurSimpleGinRummyPlayer player0 = new OurSimpleGinRummyPlayer();
            GinRummyPlayer[] players = new GinRummyPlayer[] {player0, player1};
            ArrayList<ArrayList<Card>> hands = new ArrayList<ArrayList<Card>>();
            hands.add(new ArrayList<Card>());
            hands.add(new ArrayList<Card>());
            int startingPlayer = ourPlayer;
            
            int currentPlayer = startingPlayer;
            int opponent = (currentPlayer == 0) ? 1 : 0;
            Stack<Card> deck = Card.getShuffle(random.nextInt());
            hands.get(0).clear();
			hands.get(1).clear();
			for (int i = 0; i < 2 * HAND_SIZE; i++)
				hands.get(i % 2).add(deck.pop());
            for(int i = 0; i < 2; i++){
                Card[] handArr = new Card[HAND_SIZE];
				hands.get(i).toArray(handArr);
				players[i].startGame(i, startingPlayer, handArr); 
            }
            Stack<Card> discards = new Stack<Card>();
            discards.push(deck.pop());
            Card firstFaceUpCard = discards.peek();

            int turnsTaken = 0;
            int testTurns = 30;
            
            for(int ct = 0; ct < testTurns; ct++){
                
                //store information of our player at the beginnig of each turn
                StateTrackerPlayer ourPlayerTracker = (StateTrackerPlayer)players[ourPlayer];
                double[][][] preMatrix = new double[Card.NUM_SUITS][Card.NUM_RANKS][StateTracker.DIMENSION];
                for(int i = 0; i < Card.NUM_SUITS; i++){
                    for(int j = 0; j < Card.NUM_RANKS; j++){
                        preMatrix[i][j][StateTracker.PROB_IN_STOCK] = ourPlayerTracker.getMatrix()[i][j][StateTracker.PROB_IN_STOCK];
                        preMatrix[i][j][StateTracker.STATE] = ourPlayerTracker.getMatrix()[i][j][StateTracker.STATE];
                    }
                }
                int preNumDeckCards = ourPlayerTracker.getNumDeckCards();
                int preNumOppCardsUnknown = ourPlayerTracker.getNumOppCardsUnknown();


                // normal flow of a game
                boolean drawFaceUp = false;
                Card faceUpCard = discards.peek();
                if (!(turnsTaken == 3 && faceUpCard == firstFaceUpCard)) { 
                    // both players declined and 1st player must draw face down
                    drawFaceUp = players[currentPlayer].willDrawFaceUpCard(faceUpCard); //will current player draw the 1st card?
                    
                }
                if (!(!drawFaceUp && turnsTaken < 2 && faceUpCard == firstFaceUpCard)) {
                    Card drawCard = drawFaceUp ? discards.pop() : deck.pop();
                    for (int i = 0; i < 2; i++) 
                        players[i].reportDraw(currentPlayer, (i == currentPlayer || drawFaceUp) ? drawCard : null);

                    Card discardCard = players[currentPlayer].getDiscard();
                    for (int i = 0; i < 2; i++) 
                        players[i].reportDiscard(currentPlayer, discardCard);
                    discards.push(discardCard);
                    

                    //store information of the state of the player at the end of each turn 
                    ourPlayerTracker = (StateTrackerPlayer)players[ourPlayer];
                    double[][][] curMatrix = new double[Card.NUM_SUITS][Card.NUM_RANKS][StateTracker.DIMENSION];
                    for(int i = 0; i < Card.NUM_SUITS; i++){
                        for(int j = 0; j < Card.NUM_RANKS; j++){
                            curMatrix[i][j][StateTracker.PROB_IN_STOCK] = ourPlayerTracker.getMatrix()[i][j][StateTracker.PROB_IN_STOCK];
                            curMatrix[i][j][StateTracker.STATE] = ourPlayerTracker.getMatrix()[i][j][StateTracker.STATE];
                        }
                    }
                    int curNumDeckCards = ourPlayerTracker.getNumDeckCards();
                    int curNumOppCardsUnknown = ourPlayerTracker.getNumOppCardsUnknown();
                    String msg = "turnsTaken: " + turnsTaken + ", currentPlayer is: " + currentPlayer + ", deck size is: " + deck.size() + ", discards size is: " + discards.size();
                            
                    if(turnsTaken > 2){// here we focus on normal rounds in the middle of the game

                        if(currentPlayer == ourPlayer){ // our player's turn
                            if(drawFaceUp == true){ // our player drew from the discard pile
                                // no probability has been changed
                                assertEquals(preNumDeckCards, curNumDeckCards);
                                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown);
                                for(int i = 0; i < Card.NUM_SUITS; i++){
                                    for(int j = 0; j < Card.NUM_RANKS; j++){
                                        assertEquals(preMatrix[i][j][StateTracker.PROB_IN_STOCK], curMatrix[i][j][StateTracker.PROB_IN_STOCK], msg);
                                    }
                                }
                            }
                            else{ // our player drew from deck
                                // numDeckCards -> numDeckCards - 1
                                // numOppCardsUnknown -> numOppCardsUnKnown
                                // delta is NOT zero
                                assertEquals(preNumDeckCards - 1, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown);
                                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                                double delta = p_cur - p_pre;
                                msg += ", p_pre: " + p_pre + ", p_cur: " + p_cur + ", delta: " + delta;
                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            if(suit != drawCard.getSuit() || rank != drawCard.getRank()){
                                                double expected = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] + delta;
                                                assertEquals(expected, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                            }
                                            else{
                                                assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else{ //opponent's turn 
                            // check if our player knew the discardCard was in the opponent's hand or not
                            boolean isKnownCard = (preMatrix[discardCard.getSuit()][discardCard.getRank()][StateTracker.PROB_IN_STOCK] == StateTracker.KNOW_WHERE_IT_IS);
                            
                            if(drawFaceUp == true && isKnownCard){ //opponent drew from the discard pile and discarded a card that a player knew was in the opponent's hand
                                // numDeckCards -> numDeck
                                // numOppCardsUnknown -> numOppCardsUnKnown
                                // delta is zero
                                // probability of cards neighboring the drawCard are changed
                                assertEquals(preNumDeckCards, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown);
                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                            String msg_new = msg + ", p_pre: " + preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                            // update on cards neighboring the drawCard
                                            if(suit == drawCard.getSuit() && (Math.abs(rank - drawCard.getRank()) == 2)){
                                                p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY);
                                                msg_new += ", a";
                                            }
                                            else if(suit == drawCard.getSuit() && (Math.abs(rank - drawCard.getRank()) == 1)){
                                                p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY);
                                                msg_new += ", b";
                                            }
                                            else if(rank == drawCard.getRank()){
                                                p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK);
                                                msg_new += ", c";
                                            }

                                            // update on cards neighboring the discardCard
                                            if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                                msg_new += ", d";
                                            }
                                            else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                                msg_new += ", e";
                                            }
                                            else if(rank == discardCard.getRank()){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                                msg_new += ", f";
                                            }
                                            assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                                        }
                                        else{
                                            assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                        }

                                    }
                                }
                            }
                            else if(drawFaceUp == true && !isKnownCard){ //opponent drew from the discard pile and discarded a card that a player did NOT know was in the opponent's hand
                                // numDeckCards -> numDeck
                                // numOppCardsUnknown -> numOppCardsUnKnown - 1
                                // delta is NOT zero
                                // probability of cards neighboring the drawCard are changed
                                assertEquals(preNumDeckCards, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown - 1, curNumOppCardsUnknown);
                                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                                double delta = p_cur - p_pre;

                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            if(suit != discardCard.getSuit() || rank != discardCard.getRank()){
                                                double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                                String msg_new = msg + ", p_pre: " + preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] + ", delta: " + delta;
                                                // update on cards neighboring the drawCard
                                                if(suit == drawCard.getSuit() && (Math.abs(rank - drawCard.getRank()) == 2)){
                                                    p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY);
                                                    msg_new += ", a";
                                                }
                                                else if(suit == drawCard.getSuit() && (Math.abs(rank - drawCard.getRank()) == 1)){
                                                    p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY);
                                                    msg_new += ", b";
                                                }
                                                else if(rank == drawCard.getRank()){
                                                    p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK);
                                                    msg_new += ", c";
                                                }
    
                                                // update on cards neighboring the discardCard
                                                if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                                    msg_new += ", d";
                                                }
                                                else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                                    msg_new += ", e";
                                                }
                                                else if(rank == discardCard.getRank()){
                                                    p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                                    msg_new += ", f";
                                                }
                                                p += delta;
                                                assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                                            }
                                            else{
                                                assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                            }
                                        }
                                    }
                                }
                            }
                            else if(drawFaceUp == false && isKnownCard){ //opponent drew from the discard pile and discarded a card that a player knew was in the opponent's hand
                                // numDeckCards -> numDeck - 1
                                // numOppCardsUnknown -> numOppCardsUnKnown + 1
                                // delta is NOT zero
                                // probability of cards neighboring the face-up card (that wasn't drawn) are changed
                                assertEquals(preNumDeckCards - 1, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown + 1, curNumOppCardsUnknown, msg);
                                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                                double delta = p_cur - p_pre;
                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(curMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                            String msg_new = msg + ", p_pre: " + preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] + ", delta: " + delta;
                                            // update on cards neighboring the not-drawCard
                                            if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 2)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY);
                                                msg_new += ", a";
                                            }
                                            else if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 1)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY);
                                                msg_new += ", b";
                                            }
                                            else if(rank == faceUpCard.getRank()){
                                                p = p + (1 - p)* params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK);
                                                msg_new += ", c";
                                            }

                                            // update on cards neighboring the discardCard
                                            if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                                msg_new += ", d";
                                            }
                                            else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                                msg_new += ", e";
                                            }
                                            else if(rank == discardCard.getRank()){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                                msg_new += ", f";
                                            }
                                            p += delta;
                                            assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                                        }
                                        else{
                                            assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                        }
                                    }
                                }                    
                            }
                            else if(drawFaceUp == false && !isKnownCard){ //opponent drew from stock and discarded a card that a player did NOT know was in the opponent's hand
                                // numDeckCards -> numDeck - 1
                                // numOppCardsUnknown -> numOppCardsUnKnown
                                // delta is NOT zero
                                // probability of cards neighboring the face-up card (that wasn't drawn) are changed
                                assertEquals(preNumDeckCards - 1, curNumDeckCards, msg);
                                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown, msg);
                                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                                double delta = p_cur - p_pre;
                                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                                        if(curMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                                            double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                                            String msg_new = msg + ", p_pre: " + preMatrix[suit][rank][StateTracker.PROB_IN_STOCK] + ", delta: " + delta;
                                            // update on cards neighboring the not-drawCard
                                            if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 2)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY);
                                                msg_new += ", a";
                                            }
                                            else if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 1)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY);
                                                msg_new += ", b";
                                            }
                                            else if(rank == faceUpCard.getRank()){
                                                p = p + (1 - p)* params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK);
                                                msg_new += ", c";
                                            }

                                                // update on cards neighboring the discardCard
                                            if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                                msg_new += ", d";
                                            }
                                            else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                                msg_new += ", e";
                                            }
                                            else if(rank == discardCard.getRank()){
                                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                                msg_new += ", f";
                                            }
                                            p += delta;
                                            assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                                        }
                                        else{
                                            assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg);
                                        }
                                    }
                                }             
                            }
                        }
                    }
                }
                turnsTaken++;
                currentPlayer = (currentPlayer == 0) ? 1 : 0;
                opponent = (currentPlayer == 0) ? 1 : 0;
            }
        }
    }

    @Test
    public void testOpening2Probability(){
        for(int t = 0; t < NUM_TESTS; t++){
            ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer player0 = new StateTrackerPlayer(params);
            OurSimpleGinRummyPlayer player1 = new OurSimpleGinRummyPlayer();
            Stack<Card> shuffledCards = Card.getShuffle(random.nextInt());
            Card[] Cards0 = new Card[HAND_SIZE];
            Card[] Cards1 = new Card[HAND_SIZE];
            for(int i = 0; i < HAND_SIZE; i++){
                Cards0[i] = shuffledCards.pop();
                Cards1[i] = shuffledCards.pop();
            }
            Card faceUpCard = shuffledCards.pop();
            Card drawCard = null;
            Card discardCard = null;

            player0.startGame(0, 1, Cards0);
            player1.startGame(1, 1, Cards1);

            double[][][] preMatrix = new double[Card.NUM_SUITS][Card.NUM_RANKS][StateTracker.DIMENSION];
            for(int i = 0; i < Card.NUM_SUITS; i++){
                for(int j = 0; j < Card.NUM_RANKS; j++){
                    preMatrix[i][j][StateTracker.PROB_IN_STOCK] = player0.getMatrix()[i][j][StateTracker.PROB_IN_STOCK];
                    preMatrix[i][j][StateTracker.STATE] = player0.getMatrix()[i][j][StateTracker.STATE];
                }
            }
            int preNumDeckCards = player0.getNumDeckCards();
            int preNumOppCardsUnknown = player0.getNumOppCardsUnknown();

            boolean willDraw1 = player1.willDrawFaceUpCard(faceUpCard);
            boolean willDraw0 = false;

            // the opponent takes the initial face-up card
            if(willDraw1){
                player0.reportDraw(1, faceUpCard);
                player1.reportDraw(1, faceUpCard);
                discardCard = player1.getDiscard();
                player0.reportDiscard(1, discardCard);
                player1.reportDiscard(1, discardCard);
            }
            // the opponent rejects the initial face-up card
            else {
                willDraw0 = player0.willDrawFaceUpCard(faceUpCard);
                // our player takes the inital face-up card
                if(willDraw0){
                    player0.reportDraw(0, faceUpCard);
                    player1.reportDraw(0, faceUpCard);    
                    discardCard = player0.getDiscard();
                    player0.reportDiscard(0, discardCard);
                    player1.reportDiscard(0, discardCard);
                }
                // our player also rejects the initial face=up card so the opponent draws a card from deck
                else{
                    drawCard = shuffledCards.pop();
                    player0.reportDraw(1, null);
                    player1.reportDraw(1, drawCard);
                    discardCard = player1.getDiscard();
                    player0.reportDiscard(1, discardCard);
                    player1.reportDiscard(1, discardCard);
                }
            }
            
            double[][][] curMatrix = new double[Card.NUM_SUITS][Card.NUM_RANKS][StateTracker.DIMENSION];
            for(int i = 0; i < Card.NUM_SUITS; i++){
                for(int j = 0; j < Card.NUM_RANKS; j++){
                    curMatrix[i][j][StateTracker.PROB_IN_STOCK] = player0.getMatrix()[i][j][StateTracker.PROB_IN_STOCK];
                    curMatrix[i][j][StateTracker.STATE] = player0.getMatrix()[i][j][StateTracker.STATE];
                }
            }
            int curNumDeckCards = player0.getNumDeckCards();
            int curNumOppCardsUnknown = player0.getNumOppCardsUnknown();

            //case(iv)
            if(willDraw1){
                assertEquals(preNumDeckCards, curNumDeckCards);
                assertEquals(preNumOppCardsUnknown - 1, curNumOppCardsUnknown);
                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                double delta = p_cur - p_pre;

                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                        if(curMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                            double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                            String msg_new = "case(iv): " + "delta: " + delta;
                            // update on cards neighboring the drawCard
                            if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 2)){
                                p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY);
                                msg_new += ", a";
                            }
                            else if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 1)){
                                p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY);
                                msg_new += ", b";
                            }
                            else if(rank == faceUpCard.getRank()){
                                p = p - p * params.get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK);
                                msg_new += ", c";
                            }

                            // update on cards neighboring the discardCard
                            if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                msg_new += ", d";
                            }
                            else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                msg_new += ", e";
                            }
                            else if(rank == discardCard.getRank()){
                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                msg_new += ", f";
                            }
                            p += delta;
                            assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                        }
                        else{
                            assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK]);
                        }
                    }
                }
            }
            //case(v)
            else if(willDraw0){
                assertEquals(preNumDeckCards, curNumDeckCards);
                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown);
                String msg = "case(v)";
                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                        if(curMatrix[suit][rank][StateTracker.STATE] == StateTracker.UNKNOWN){
                            double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                            String msg_new = msg;
                            // update on cards neighboring the not-drawCard
                            if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 2)){
                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY);
                                msg_new += ", a";
                            }
                            else if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 1)){
                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY);
                                msg_new += ", b";
                            }
                            else if(rank == faceUpCard.getRank()){
                                p = p + (1 - p)* params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK);
                                msg_new += ", c";
                            }
                            assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                        }
                    }
                }
            }
            //case(vi)
            else{
                assertEquals(preNumDeckCards - 1, curNumDeckCards);
                assertEquals(preNumOppCardsUnknown, curNumOppCardsUnknown);
                double p_pre = (double)preNumDeckCards/(preNumDeckCards + preNumOppCardsUnknown);
                double p_cur = (double)curNumDeckCards/(curNumDeckCards + curNumOppCardsUnknown);
                double delta = p_cur - p_pre;
                for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                    for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                        if(curMatrix[suit][rank][StateTracker.PROB_IN_STOCK] != StateTracker.KNOW_WHERE_IT_IS){
                            double p = preMatrix[suit][rank][StateTracker.PROB_IN_STOCK];
                            String msg_new = "case (vi): " + ", delta: " + delta;
                            // update on cards neighboring the not-drawCard
                            if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 2)){
                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY);
                                msg_new += ", a";
                            }
                            else if(suit == faceUpCard.getSuit() && (Math.abs(rank - faceUpCard.getRank()) == 1)){
                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY);
                                msg_new += ", b";
                            }
                            else if(rank == faceUpCard.getRank()){
                                p = p + (1 - p)* params.get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK);
                                msg_new += ", c";
                            }

                            // update on cards neighboring the discardCard
                            if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 2)){
                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
                                msg_new += ", d";
                            }
                            else if(suit == discardCard.getSuit() && (Math.abs(rank - discardCard.getRank()) == 1)){
                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY);
                                msg_new += ", e";
                            }
                            else if(rank == discardCard.getRank()){
                                p = p + (1 - p) * params.get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK);
                                msg_new += ", f";
                            }
                            p += delta;
                            assertEquals(p, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK], msg_new);
                        }
                        else{
                            assertEquals(StateTracker.KNOW_WHERE_IT_IS, curMatrix[suit][rank][StateTracker.PROB_IN_STOCK]);
                        }
                    }
                }
            }
        }        
    }

    @Test
    public void testOpeningTurnsTaken1(){
        for(int t = 0; t < NUM_TESTS; t++){
            ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer player0 = new StateTrackerPlayer(params);
            OurSimpleGinRummyPlayer player1 = new OurSimpleGinRummyPlayer();
            Stack<Card> shuffledCards = Card.getShuffle(random.nextInt());
            Card[] Cards0 = new Card[HAND_SIZE];
            Card[] Cards1 = new Card[HAND_SIZE];
            for(int i = 0; i < HAND_SIZE; i++){
                Cards0[i] = shuffledCards.pop();
                Cards1[i] = shuffledCards.pop();
            }
            Card faceUpCard = shuffledCards.pop();
            Card drawCard = null;
            
            player0.startGame(0, 0, Cards0);
            player1.startGame(1, 0, Cards1);
            boolean willDraw0 = player0.willDrawFaceUpCard(faceUpCard);
            boolean willDraw1 = false;
            
            //our player takes the initial face-up card
            if(willDraw0){
                player0.reportDraw(0, faceUpCard);
                player1.reportDraw(0, faceUpCard);
            }
            //our player rejects the initial face-up card
            else{
                willDraw1 = player1.willDrawFaceUpCard(faceUpCard);
                // the opponent takes the initial face-up card
                if(willDraw1){
                    player0.reportDraw(1, faceUpCard);
                    player1.reportDraw(1, faceUpCard);
                }
                // the opponent also rejects the initial face-up card so our player draws a card from deck
                else{
                    drawCard = shuffledCards.pop();
                    player0.reportDraw(0, drawCard);
                    player1.reportDraw(0, null);
                }

            }
            
            double turnsTaken = player0.getTurnsTaken();
            double numSelfTurnsTaken = player0.getNumSelfTurnsTaken();

            if(willDraw0){
                assertEquals(1, turnsTaken);
                assertEquals(1, numSelfTurnsTaken);
            }
            else if(willDraw1){
                assertEquals(2, turnsTaken);
                assertEquals(1, numSelfTurnsTaken);
            }
            else{
                assertEquals(3, turnsTaken);
                assertEquals(2, numSelfTurnsTaken);            
            }
        }        
    }

    @Test
    public void testOpeningTurnsTaken2(){
        for(int t = 0; t < NUM_TESTS; t++){
            ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
            StateTrackerPlayer player0 = new StateTrackerPlayer(params);
            OurSimpleGinRummyPlayer player1 = new OurSimpleGinRummyPlayer();
            Stack<Card> shuffledCards = Card.getShuffle(random.nextInt());
            Card[] Cards0 = new Card[HAND_SIZE];
            Card[] Cards1 = new Card[HAND_SIZE];
            for(int i = 0; i < HAND_SIZE; i++){
                Cards0[i] = shuffledCards.pop();
                Cards1[i] = shuffledCards.pop();
            }
            Card faceUpCard = shuffledCards.pop();
            Card drawCard = null;
            
            player0.startGame(0, 1, Cards0);
            player1.startGame(1, 1, Cards1);
            boolean willDraw1 = player1.willDrawFaceUpCard(faceUpCard);
            boolean willDraw0 = false;

            // the opponent takes the initial face-up card
            if(willDraw1){
                player0.reportDraw(1, faceUpCard);
                player1.reportDraw(1, faceUpCard);
            }
            // the opponent rejects the initial face-up card
            else {
                willDraw0 = player0.willDrawFaceUpCard(faceUpCard);
                // our player takes the inital face-up card
                if(willDraw0){
                    player0.reportDraw(0, faceUpCard);
                    player1.reportDraw(0, faceUpCard);    
                }
                // our player also rejects the initial face=up card so the opponent draws a card from deck
                else{
                    drawCard = shuffledCards.pop();
                    player0.reportDraw(1, null);
                    player1.reportDraw(1, drawCard);
                }
            }
            
            double turnsTaken = player0.getTurnsTaken();
            double numSelfTurnsTaken = player0.getNumSelfTurnsTaken();

            if(willDraw1){
                assertEquals(1, turnsTaken);
                assertEquals(0, numSelfTurnsTaken);
            }
            else if(willDraw0){
                assertEquals(2, turnsTaken);
                assertEquals(1, numSelfTurnsTaken);
            }
            else{
                assertEquals(3, turnsTaken);
                assertEquals(1, numSelfTurnsTaken);            
            }
        }        
    }

}