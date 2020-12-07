package players;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import games.TestingGame;
import ginrummy.Card;
import ginrummy.GinRummyUtil;
import util.OurUtil;
import players.ParamList;

public class MeldabilityCalculator {

    private ParamList params;

    public static boolean TESTING = false;

    public MeldabilityCalculator(ParamList params) {
        this.params = params;
    }

    public ParamList getParamList() {
        return params;
    }

    // VERIFIED 6/17
    /**
     * This is the original version, without the meld competition adjustment (a card in a meld has 0 obtainability).
     * @param card The card whose obtainability we want to calculate
     * @param myTracker A StateTracker object with the most current game state information
     * @param cardsInChosenMelds cards who are in chosenMelds
     * @return An obtainability measure based on the Obtainability section of Main Ideas document
     */
    public double calcSelfObtainability(Card card, StateTracker myTracker, ArrayList<Card> cardsInChosenMelds){ //should return the obtainability score, and at what turn the card could be obtained?
        double obtainability = 0;
        double[][][] matrix = myTracker.getMatrix();
        if(matrix[card.getSuit()][card.getRank()][StateTracker.PROB_IN_STOCK] == StateTracker.KNOW_WHERE_IT_IS){
            if(matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_FROM_STOCK
               || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_FROM_DISCARD
               || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_FROM_START){
                if(cardsInChosenMelds.contains(card))
                    obtainability = 0;
                else
                    obtainability = 1;
            }
            else if(matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_BURIED_DISCARD
                    || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.OPP_BURIED_DISCARD){
                        obtainability = 0;
            }
            else if(matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.OPP_FROM_DISCARD
                    || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.OPP_FROM_START
                    || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_TOP_DISCARD){
                        obtainability = params.get(ParamList.MC_SELF_LOW_OBTAINABILITY);
            }
            else if(matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.OPP_TOP_DISCARD){
                obtainability = 0.99;
            }
        }
        else{
            obtainability = matrix[card.getSuit()][card.getRank()][StateTracker.PROB_IN_STOCK] * params.get(ParamList.MC_SELF_RATIO_FOR_UNKNOWN);
        }
        
        return obtainability;
    }

    /**
     * @param card The card whose obtainability we want to calculate
     * @param myTracker A StateTracker object with the most current game state information
     * @param cardsInChosenMelds cards who are in chosenMelds
     * @return An obtainability measure
     */
    public double calcOppObtainability(Card card, StateTracker myTracker, ArrayList<Card> cardsInChosenMelds){ //should return the obtainability score, and at what turn the card could be obtained?
        double obtainability = 0;
        double[][][] matrix = myTracker.getMatrix();
        if(matrix[card.getSuit()][card.getRank()][StateTracker.PROB_IN_STOCK] == StateTracker.KNOW_WHERE_IT_IS){
            if(matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.OPP_FROM_DISCARD
               || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.OPP_FROM_START){
                if(cardsInChosenMelds.contains(card))
                    obtainability = 0;
                else
                    obtainability = 1;
            }
            else if(matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_BURIED_DISCARD
                    || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.OPP_BURIED_DISCARD
                    || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.OPP_TOP_DISCARD){
                        obtainability = 0;
            }
            else if(matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_FROM_STOCK
                    || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_FROM_DISCARD
                    || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_FROM_START){
                        obtainability = params.get(ParamList.MC_OPP_LOW_OBTAINABILITY);
            }
        }
        else{
            obtainability = (1 - matrix[card.getSuit()][card.getRank()][StateTracker.PROB_IN_STOCK]) * params.get(ParamList.MC_OPP_RATIO_FOR_UNKNOWN);
        }
        
        return obtainability;
    }
    

    // VERIFIED 6/17
    public double selfCardMeldability(Card card, StateTracker myTracker, ArrayList<ArrayList<Card>> lsOfChosenSelfMelds){
        double rankMeldability = selfRankMeldability(card, myTracker, lsOfChosenSelfMelds);
        double runMeldability = selfRunMeldability(card, myTracker, lsOfChosenSelfMelds);
        return params.get(ParamList.MC_SELF_WRANK) * rankMeldability + params.get(ParamList.MC_SELF_WRUN) * runMeldability;
    }

    public double oppCardMeldability(Card card, StateTracker myTracker, ArrayList<ArrayList<Card>> lsOfChosenOppMelds){
        double rankMeldability = oppRankMeldability(card, myTracker, lsOfChosenOppMelds);
        double runMeldability = oppRunMeldability(card, myTracker, lsOfChosenOppMelds);
        return params.get(ParamList.MC_OPP_WRANK) * rankMeldability + params.get(ParamList.MC_OPP_WRUN) * runMeldability;
    }

    // VERIFIED 6/17
    public double selfRankMeldability(Card card, StateTracker myTracker, ArrayList<ArrayList<Card>> lsOfChosenSelfMelds){
        if(TESTING) System.out.println("-------------selfRankMeldibility-------------");
        double rankMeldability = 0;

        // Determine if card is already in a meld
        for(ArrayList<Card> chosenMeld : lsOfChosenSelfMelds){
            if(chosenMeld.contains(card)){
                // Determine if the meld is a 'run meld' by checking the cards' ranks
                if(chosenMeld.get(0).getRank() == chosenMeld.get(1).getRank()){
                    rankMeldability = 100;
                }
                break;
            }
        }

        // If not in a meld ...
        if(rankMeldability == 0){
            ArrayList<Double> obOfotherCards = new ArrayList<Double>();
            ArrayList<Card> cardsInChosenMelds = new ArrayList<Card>();
            lsOfChosenSelfMelds.forEach(cardsInChosenMelds::addAll);

            for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                if(suit != card.getSuit()){
                    Card other = getCard(card.getRank(), suit);
                    double obtainability = calcSelfObtainability(other, myTracker, cardsInChosenMelds);
                    obOfotherCards.add(obtainability);
                    if(TESTING) System.out.println("Self Obtainability of card " + other + " is " + obtainability);
                }
            }
            rankMeldability = obOfotherCards.get(0) * obOfotherCards.get(1) 
                            + obOfotherCards.get(1) * obOfotherCards.get(2) 
                            + obOfotherCards.get(2) * obOfotherCards.get(0); 
        }
                               
        if(TESTING) System.out.println("The self rank Meldability of card " + card + " is " + rankMeldability);
        return rankMeldability;
    }

    public double oppRankMeldability(Card card, StateTracker myTracker, ArrayList<ArrayList<Card>> lsOfChosenOppMelds){
        if(TESTING) System.out.println("-------------oppRankMeldibility-------------");
        double rankMeldability = 0;

        // Determine if card is already in one of opp's melds
        for(ArrayList<Card> chosenMeld : lsOfChosenOppMelds){
            if(chosenMeld.contains(card)){
                // Determine if the meld is a 'run meld' by checking the cards' ranks
                if(chosenMeld.get(0).getRank() == chosenMeld.get(1).getRank()){
                    rankMeldability = 100;
                }
                break;
            }
        }

        // If not in a meld ...
        if(rankMeldability == 0){
            ArrayList<Double> obOfotherCards = new ArrayList<Double>();
            ArrayList<Card> cardsInChosenMelds = new ArrayList<Card>();
            lsOfChosenOppMelds.forEach(cardsInChosenMelds::addAll);

            for(int suit = 0; suit < Card.NUM_SUITS; suit++){
                if(suit != card.getSuit()){
                    Card other = getCard(card.getRank(), suit);
                    double obtainability = calcOppObtainability(other, myTracker, cardsInChosenMelds);
                    obOfotherCards.add(obtainability);
                    if(TESTING) System.out.println("Opp Obtainability of card " + other + " is " + obtainability);
                }
            }
            rankMeldability = obOfotherCards.get(0) * obOfotherCards.get(1) 
                            + obOfotherCards.get(1) * obOfotherCards.get(2) 
                            + obOfotherCards.get(2) * obOfotherCards.get(0); 
        }
                               
        if(TESTING) System.out.println("The opp rank Meldability of card " + card + " is " + rankMeldability);
        return rankMeldability;
    }

    // VERIFIED 6/17
    public double selfRunMeldability(Card card, StateTracker myTracker, ArrayList<ArrayList<Card>> lsOfChosenSelfMelds){
        if(TESTING) System.out.println("-------------selfRunMeldability-------------");
        double runMeldability = 0;

        // Determine if card is already in a meld
        for(ArrayList<Card> chosenMeld : lsOfChosenSelfMelds){
            if(chosenMeld.contains(card)){
                // Determine if the meld is a 'rank meld' by checking the cards' suits
                if(chosenMeld.get(0).getSuit() == chosenMeld.get(1).getSuit()){
                    runMeldability = 100;
                }
                break;
            }
        }

        // If not in a meld ...
        if(runMeldability == 0){
            double[] obOfOtherCards = new double[5];
            ArrayList<Card> cardsInChosenMelds = new ArrayList<Card>();
            lsOfChosenSelfMelds.forEach(cardsInChosenMelds::addAll);
            for(int rank = Math.max(card.getRank() - 2, 0); Math.abs(rank - card.getRank()) <= 2 && rank < Card.NUM_RANKS; rank++){
               if(rank != card.getRank()){
                   Card other = getCard(rank, card.getSuit());
                   double obtainability = calcSelfObtainability(other, myTracker, cardsInChosenMelds); 
                   obOfOtherCards[rank - card.getRank() + 2] = obtainability;
                   if(TESTING) System.out.println("Self Obtainability of card " + other + " is " + obtainability);
               }
            }
            runMeldability = obOfOtherCards[0] * obOfOtherCards[1]
                           + obOfOtherCards[1] * obOfOtherCards[3]
                           + obOfOtherCards[3] * obOfOtherCards[4];
        }

        if(TESTING) System.out.println("The self run Meldability of card " + card + " is " + runMeldability);
        return runMeldability;
    }

    public double oppRunMeldability(Card card, StateTracker myTracker, ArrayList<ArrayList<Card>> lsOfChosenOppMelds){
        if(TESTING) System.out.println("-------------oppRunMeldability-------------");
        double runMeldability = 0;

        // Determine if card is already in one of opp's melds
        for(ArrayList<Card> chosenMeld : lsOfChosenOppMelds){
            if(chosenMeld.contains(card)){
                // Determine if the meld is a 'rank meld' by checking the cards' suits
                if(chosenMeld.get(0).getSuit() == chosenMeld.get(1).getSuit()){
                    runMeldability = 100;
                }
                break;
            }
        }

        // If not in a meld ...
        if(runMeldability == 0){
            double[] obOfOtherCards = new double[5];
            ArrayList<Card> cardsInChosenMelds = new ArrayList<Card>();
            lsOfChosenOppMelds.forEach(cardsInChosenMelds::addAll);
            for(int rank = Math.max(card.getRank() - 2, 0); Math.abs(rank - card.getRank()) <= 2 && rank < Card.NUM_RANKS; rank++){
               if(rank != card.getRank()){
                   Card other = getCard(rank, card.getSuit());
                   double obtainability = calcOppObtainability(other, myTracker, cardsInChosenMelds); 
                   obOfOtherCards[rank - card.getRank() + 2] = obtainability;
                   if(TESTING) System.out.println("Opp Obtainability of card " + other + " is " + obtainability);
               }
            }
            runMeldability = obOfOtherCards[0] * obOfOtherCards[1]
                           + obOfOtherCards[1] * obOfOtherCards[3]
                           + obOfOtherCards[3] * obOfOtherCards[4];
        }

        if(TESTING) System.out.println("The opp run Meldability of card " + card + " is " + runMeldability);
        return runMeldability;
    }

    // public static double handMeldability(ArrayList<Card> hand, StateTracker myTracker){
    //     double handMeldability = 0;
    //     ArrayList<ArrayList<Card>> lsOflsOfMelds = GinRummyUtil.cardsToAllMelds(hand);
    //     for(int i = 0; i < hand.size(); i++){
    //         handMeldability += selfCardMeldability(hand.get(i), myTracker);
    //         for(int j = 0; j < lsOflsOfMelds.size(); j++){
    //             handMeldability += lsOflsOfMelds.get(j).contains(hand.get(i)) ? 100 : 0;
    //         }
    //     }
    //     return handMeldability;
    // } 

    // VERIFIED 6/17
    public double selfHandMeldability(ArrayList<Card> hand, StateTracker myTracker){
        double handMeldability = 0;
        ArrayList<ArrayList<ArrayList<Card>>> lsOfLsOfSelfMelds = GinRummyUtil.cardsToBestMeldSets(hand);
        ArrayList<ArrayList<Card>> lsOfChosenSelfMelds = !lsOfLsOfSelfMelds.isEmpty() ? lsOfLsOfSelfMelds.get(0) : new ArrayList<ArrayList<Card>>();
        for(int i = 0; i < hand.size(); i++){
            handMeldability += selfCardMeldability(hand.get(i), myTracker, lsOfChosenSelfMelds);
        }
        return handMeldability;
    } 

    public double oppHandMeldability(ArrayList<Card> oppHand, StateTracker myTracker){
        ArrayList<ArrayList<ArrayList<Card>>> lsOfLsOfSOppMelds = GinRummyUtil.cardsToBestMeldSets(oppHand);
        ArrayList<ArrayList<Card>> lsOfChosenOppMelds = !lsOfLsOfSOppMelds.isEmpty() ? lsOfLsOfSOppMelds.get(0) : new ArrayList<ArrayList<Card>>();
        double handMeldability = 0;
        for(int i = 0; i < oppHand.size(); i++){
            handMeldability += oppCardMeldability(oppHand.get(i), myTracker, lsOfChosenOppMelds);
        }
        return handMeldability;
    } 

    // VERIFIED 6/17
    public double[][] getMatrixOfSelfOb(StateTracker myTracker, ArrayList<Card> cardsInChosenMelds){
        double[][] matrix = new double[Card.NUM_SUITS][Card.NUM_RANKS];
        for(int suit = 0; suit < Card.NUM_SUITS; suit++){
            for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                double obtainability = calcSelfObtainability(getCard(rank, suit), myTracker, cardsInChosenMelds);
                matrix[suit][rank] = obtainability;
            }
        }
        return matrix;
    }

    public double[][] getMatrixOfOppOb(StateTracker myTracker, ArrayList<Card> cardsInChosenMelds){
        double[][] matrix = new double[Card.NUM_SUITS][Card.NUM_RANKS];
        for(int suit = 0; suit < Card.NUM_SUITS; suit++){
            for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                double obtainability = calcOppObtainability(getCard(rank, suit), myTracker, cardsInChosenMelds);
                matrix[suit][rank] = obtainability;
            }
        }
        return matrix;
    }

    // VERIFIED 6/17
    public double[][] getMatrixOfSelfMeld(StateTracker myTracker, ArrayList<ArrayList<Card>> lsOfChosenSelfMelds){
        double [][] matrix = new double[Card.NUM_SUITS][Card.NUM_RANKS];
        for(int suit = 0; suit<Card.NUM_SUITS; suit++){
            for(int rank = 0; rank<Card.NUM_RANKS; rank++){
                double meldability = selfCardMeldability(getCard(rank, suit), myTracker, lsOfChosenSelfMelds);
                matrix[suit][rank] = meldability;
            }
        }
        return matrix;
    }

    public double[][] getMatrixOfOppMeld(StateTracker myTracker, ArrayList<ArrayList<Card>> lsOfChosenOppMelds){
        double [][] matrix = new double[Card.NUM_SUITS][Card.NUM_RANKS];
        for(int suit = 0; suit<Card.NUM_SUITS; suit++){
            for(int rank = 0; rank<Card.NUM_RANKS; rank++){
                double meldability = oppCardMeldability(getCard(rank, suit), myTracker, lsOfChosenOppMelds);
                matrix[suit][rank] = meldability;
            }
        }
        return matrix;
    }
    
    public static void setTesting(boolean testing){
        TESTING = testing;
    }

    public void setParamList(ParamList params){
        this.params = params;
    }

    public Card getCard(int rank, int suit){
        return Card.allCards[suit * Card.NUM_RANKS + rank];
    }

    /**
     * Print out the matrix that stores information of probality that cards are in stock 
     */
    public void displayMatrix(double[][] matrix){
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
                    System.out.printf("%.3f ", matrix[j][k-1]);
                }
            }
            System.out.println();
        }
    }

    public static void test(){
        System.out.println("==================== Test ====================");
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        StateTrackerPlayer player = new StateTrackerPlayer(params);
        SimpleFakeGinRummyPlayer opponent = new SimpleFakeGinRummyPlayer(params);

        MeldabilityCalculator meldCalc = new MeldabilityCalculator(params);

        ArrayList<Card> myCards = OurUtil.makeHand(new String[] {"AC", "AH", "2S", "4H", "5D", "6D", "7S", "8D", "8S", "8H"});
        ArrayList<Card> oppCards = OurUtil.makeHand(new String[] {"AS", "2C", "2H", "2D", "3S", "6C", "5H", "6S", "7D", "8C"});
        Card[] cardsOfPlayer0 = new Card[10]; 
        Card[] cardsOfPlayer1 = new Card[10];
        for(int i = 0; i < 10; i++){
            cardsOfPlayer1[i] = oppCards.get(i);
            cardsOfPlayer0[i] = myCards.get(i);
        }

        System.out.println("==================== Self Starts a Game ====================");
        player.startGame(0, 0, cardsOfPlayer0);
        opponent.startGame(1, 0, cardsOfPlayer1);
        System.out.println("Player 0 is dealt " + Arrays.toString(cardsOfPlayer0));
        System.out.println("Player 1 is dealt " + Arrays.toString(cardsOfPlayer1));
        ArrayList<ArrayList<Card>> lsOfChosenMelds = GinRummyUtil.cardsToBestMeldSets(myCards).get(0);
        ArrayList<Card> cardsInChosenMelds = new ArrayList<Card>();
        lsOfChosenMelds.forEach(cardsInChosenMelds::addAll);
        double[][] matrixOfOb = meldCalc.getMatrixOfSelfOb(player.myTracker, cardsInChosenMelds);
        double[][] matrixOfMeld = meldCalc.getMatrixOfSelfMeld(player.myTracker, lsOfChosenMelds);
        System.out.println("---------- Matrix Of Probability ----------");
        player.myTracker.displayPROBS();
        System.out.println("---------- Matrix Of Obtainability ----------");
        meldCalc.displayMatrix(matrixOfOb);
        System.out.println("---------- Matrix Of Meldability ----------");
        meldCalc.displayMatrix(matrixOfMeld);

        MeldabilityCalculator.setTesting(true);
        double obtainability = meldCalc.calcSelfObtainability(Card.strCardMap.get("AD"), player.myTracker, cardsInChosenMelds);
        System.out.println("Obtainability of card AD: " + obtainability);

        Card card = Card.strCardMap.get("7S");
        double meldability = meldCalc.selfCardMeldability(card, player.myTracker, lsOfChosenMelds);
        System.out.println("Meldability of card " + card + ": " + meldability);

        card = Card.strCardMap.get("8S");
        System.out.println("Case where the initial face-up card is: " + card);
        meldability = meldCalc.selfCardMeldability(card, player.myTracker, lsOfChosenMelds);
        System.out.println("Meldability of card " + card + ": " + meldability);

        System.out.println("==================== handMeldability ====================");
        MeldabilityCalculator.setTesting(false);
        double handMeldability = meldCalc.selfHandMeldability(player.cards, player.myTracker);
        System.out.println("HandMeldabilty for the hand is: " + handMeldability);
    }

    /**
     * In this test, we consider this issue:
     * “Even when a card is in a meld, the obtainability of the card is 1 (because it’s in our hand) and it could lead 
     * to getting another card that tries to make a meld with the card already in the meld.” - maybe need to look at availability to meld
     * 5C 6C 7C 7S and then we value 7D because it makes a meld, but 7C is already in a meld.
     * 5C 6C 7C and then we value 7D could meld with 7C, but 7C is already in a meld.
     * Can apply with either a run or a suit meld
     */
    public static void testMeldCompetition() {
        System.out.println("============== testMeldCompetition ==============");
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        MeldabilityCalculator meldCalc = new MeldabilityCalculator(params);
        StateTracker tracker = new StateTracker(params);
        tracker.setToHardcodedStateTracker1();

        ArrayList<Card> selfHand = tracker.getSelfHandForHardcodedStateTracker1(); // "2S", "3S", "4S", "5S", "6S", "9C", "9H", "9S", "4C", "AD"
        System.out.println("selfHand: " + selfHand);

        ArrayList<ArrayList<Card>> lsOfChosenMelds = GinRummyUtil.cardsToBestMeldSets(selfHand).get(0);

        System.out.println("-------------------------------");
        System.out.println("AD rank meldability is low");
        System.out.println("rank meldability: " + meldCalc.selfRankMeldability(Card.strCardMap.get("AD"), tracker, lsOfChosenMelds));

        System.out.println("-------------------------------");
        System.out.println("4C rank meldability should be low too - could meld with 4S, but 4S is already in a different meld");
        System.out.println("rank meldability: " + meldCalc.selfRankMeldability(Card.strCardMap.get("4C"), tracker, lsOfChosenMelds));
    }

    public static void testCardMeldability() {
        System.out.println("============== testCardMeldability ==============");
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        MeldabilityCalculator meldCalc = new MeldabilityCalculator(params);
        StateTracker tracker = new StateTracker(params);
        tracker.setToHardcodedStateTracker1();

        ArrayList<Card> selfHand = tracker.getSelfHandForHardcodedStateTracker1(); // "2S", "3S", "4S", "5S", "6S", "9C", "9H", "9S", "4C", "AD"
        System.out.println("selfHand: " + selfHand);
        ArrayList<ArrayList<Card>> lsOfChosenMelds = GinRummyUtil.cardsToBestMeldSets(selfHand).get(0);
        System.out.println("2S is in a run meld, so should have runMeldability 100.");
        System.out.println("runMeldability: " + meldCalc.selfRunMeldability(Card.strCardMap.get("2S"), tracker, lsOfChosenMelds));
        System.out.println("rankMeldability: " + meldCalc.selfRankMeldability(Card.strCardMap.get("2S"), tracker, lsOfChosenMelds));
        System.out.println("selfCardMeldability: " + meldCalc.selfCardMeldability(Card.strCardMap.get("2S"), tracker, lsOfChosenMelds));
    }

    public static void testOpp(){
        System.out.println("==================== Test Opp ====================");
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        StateTrackerPlayer player = new StateTrackerPlayer(params);
        // SimpleFakeGinRummyPlayer opponent = new SimpleFakeGinRummyPlayer(params);

        MeldabilityCalculator meldCalc = new MeldabilityCalculator(params);

        player.myTracker.setToHardcodedStateTracker1();
        ArrayList<Card> myCards = player.myTracker.getSelfHandForHardcodedStateTracker1();
        ArrayList<Card> oppCards = player.myTracker.getOppHandForHardcodedStateTracker1();
        System.out.println("---------- Game Info ----------");
        System.out.println(player.myTracker);
        System.out.println("---------- Each Players' hand ----------");
        System.out.println("Self Hand: " + myCards);
        System.out.println("Opp Hand: " + oppCards);
        System.out.println("---------- OppHandGuessed when there are no additional cards ----------");
        ArrayList<Card> oppCardsKnown = player.myTracker.getOppCardsKnown();
        int numOfCardsUnknown = StateTracker.HAND_SIZE - oppCardsKnown.size();
        ArrayList<ArrayList<Card>> combOfOppHandGuessed = OurUtil.multiOppHand(player.myTracker, 0, numOfCardsUnknown);
        ArrayList<Card> oppHandGuessed = combOfOppHandGuessed.get(0);
        System.out.println(oppHandGuessed);

        ArrayList<ArrayList<ArrayList<Card>>> lsOfLsOfSOppMelds = GinRummyUtil.cardsToBestMeldSets(oppHandGuessed);
        ArrayList<ArrayList<Card>> lsOfChosenOppMelds = !lsOfLsOfSOppMelds.isEmpty() ? lsOfLsOfSOppMelds.get(0) : new ArrayList<ArrayList<Card>>();
        ArrayList<Card> cardsInChosenOppMelds = new ArrayList<Card>();
        lsOfChosenOppMelds.forEach(cardsInChosenOppMelds::addAll);
        double[][] matrixOfOppOb = meldCalc.getMatrixOfOppOb(player.myTracker, cardsInChosenOppMelds);
        double[][] matrixOfOppMeld = meldCalc.getMatrixOfOppMeld(player.myTracker, lsOfChosenOppMelds);
        System.out.println("---------- Matrix Of Opp Obtainability ----------");
        meldCalc.displayMatrix(matrixOfOppOb);
        System.out.println("---------- Matrix Of Meldability ----------");
        meldCalc.displayMatrix(matrixOfOppMeld);
        System.out.println();

        MeldabilityCalculator.setTesting(true);
        System.out.println("----- This is the case where self is debating which card to discard, AD or 4C -----");
        Card card = Card.strCardMap.get("AD");
        double meldability = meldCalc.oppCardMeldability(card, player.myTracker, lsOfChosenOppMelds);
        System.out.println("Meldability of card " + card + ": " + meldability);
        System.out.println("********** To verify the calculation of oppHandMeldability make setTesting true **********");
        MeldabilityCalculator.setTesting(false); // change this
        ArrayList<Card> case1 = (ArrayList<Card>)(oppHandGuessed.clone());
        case1.add(card);
        double handMeldability = meldCalc.selfHandMeldability(case1, player.myTracker);
        System.out.println("HandMeldabilty for oppGueseedHand that " + card + " is added to: " + handMeldability);
        System.out.println();

        MeldabilityCalculator.setTesting(true);
        card = Card.strCardMap.get("4C");
        meldability = meldCalc.oppCardMeldability(card, player.myTracker, lsOfChosenOppMelds);
        System.out.println("Meldability of card " + card + ": " + meldability);
        System.out.println("********** To verify the calculation of oppHandMeldability make setTesting true **********");
        MeldabilityCalculator.setTesting(false); // change this
        ArrayList<Card> case2 = (ArrayList<Card>)(oppHandGuessed.clone());
        case2.add(card);
        handMeldability = meldCalc.selfHandMeldability(case2, player.myTracker);
        System.out.println("HandMeldabilty for oppGueseedHand that " + card + " is added to: " + handMeldability);
        System.out.println();
    }

    public static void main(String[] args) {
        // test();
        // testMeldCompetition();
        // testCardMeldability();
        testOpp();
    }

}

