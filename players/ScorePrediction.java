package players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import players.StateTracker;
import players.StateTrackerPlayer;
import ginrummy.Card;
import ginrummy.GinRummyUtil;
import util.OurUtil;
import java.util.Random;;

public class ScorePrediction {

    protected static boolean TESTING = false;
    

    // VERIFIED 6/17
    /**
     * oneOppHandScore computes the score that self would get if it knocks, while opp has a single hand.  
     */ 
    // public static double oneOppHandScore(StateTracker myTracker, ArrayList<Card> selfHand){
    //     if(TESTING) System.out.println("==========oneOppHandScore is called to predict score=========");
    //     Random random = new Random();
    //     ArrayList<Card> oppHand = new ArrayList<Card>();
    //     ArrayList<Card> oppCardsKnown = myTracker.getOppCardsKnown(); //this is for readability so can delete this for efficiency
    //     oppHand.addAll(oppCardsKnown);//adding the cards that we alread know are in the opponent's hand: card from discard/first initial face up card
        
    //     int numOfKnownCards = oppHand.size();
    //     int numOfUnknownCards = StateTracker.HAND_SIZE - numOfKnownCards;
    //     Map<Card, Double> mapOfCardsWithHighestProb = myTracker.getCardToProbMapWithHighestProbInOppHand(numOfUnknownCards); //finding the "unknown" cards that have the highest probability of being in the opponent's hands. 
    //     oppHand.addAll(mapOfCardsWithHighestProb.keySet()); //adding the known opponent hand's cards with the unknown cards

    //     ArrayList<ArrayList<ArrayList<Card>>> selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(selfHand); //random.nextInt(selfBestMeldSets.size()
    //     ArrayList<ArrayList<Card>> selfMeldSet = selfBestMeldSets.isEmpty() ? new ArrayList<ArrayList<Card>>() : selfBestMeldSets.get(0);
    //     int selfDeadwood = GinRummyUtil.getDeadwoodPoints(selfMeldSet, selfHand); //getting self's deadwood to calculate the score when the self knocks
    //     if(TESTING) System.out.println("Self Deadwood is: " + selfDeadwood);
    //     if(TESTING) System.out.println("Opp estimated hand is: " + oppHand.toString());
    //     double score  = OurUtil.computeScoreIfKnock(selfBestMeldSets.get(0), selfDeadwood, oppHand);//this gives us the predicted score that self would get if it knocks
    //     return score;
    // }

    // UNVERIFIED 6/19 (Made a change regarding the number of cards to pick up to form combinations)
    // VERIFIED 6/17
    /**
     * multiOppHandScore gives us the score that self would get, while we generate many potential hands
     * Define an int n > u. So it’s more than the number of cards we need to complete the hand.
     * Determine the n cards with the highest (1-PROB_IN_STOCK) values.
     * Form every possible set of u cards from these n cards. (nCu)
     * Combining a set with the k cards forms one possible hand.
     * Each hand could then be dealt with probabilistically (expected value).
     */
    public static double multiOppHandScore(StateTracker myTracker, ArrayList<Card> selfHand, int numAdditionalCards){
        if(TESTING) System.out.println("\n========== multiOppHandScore is called to predict score =========");

        // guess opp's hand
        ArrayList<Card> oppCardsKnown = myTracker.getOppCardsKnown(); 
        int numOfCardsUnknown = StateTracker.HAND_SIZE - oppCardsKnown.size();
        ArrayList<ArrayList<Card>> combOfOppHandsGuessed = OurUtil.multiOppHand(myTracker, numAdditionalCards, numOfCardsUnknown);

        // evaluate self's hand
        ArrayList<ArrayList<ArrayList<Card>>> selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(selfHand);//getiing the self melds
        // System.out.println("self melds: " + selfBestMeldSets);
        int selfDeadwood = -1;
        if(selfBestMeldSets.isEmpty()){
            selfDeadwood = GinRummyUtil.getDeadwoodPoints(selfHand);
        }
        else
            selfDeadwood = GinRummyUtil.getDeadwoodPoints(selfBestMeldSets.get(0), selfHand);//getting the self deadwood
        if(TESTING) System.out.println("Self Deadwood Points are: " + selfDeadwood + "\n");
        
        if(selfDeadwood > GinRummyUtil.MAX_DEADWOOD){
            if(TESTING) System.out.println("Cannot knock with deadwood: " + selfDeadwood);
            return 0;
        }

        // predict opp's hand score
        double sumOfScores = 0;
        double sumOfHandProbs = 0;
        for(ArrayList<Card> oppHandGuessed: combOfOppHandsGuessed){
        
            double handProb = OurUtil.calcOppHandProbability(oppHandGuessed, myTracker);
            
            if(TESTING) {
                System.out.print("----------------------\nA potential opp Hand is: ");
                System.out.println(Arrays.toString(oppHandGuessed.toArray()));
                System.out.println("The Probability of the hand is: " + handProb);
            }
            double score = OurUtil.computeScoreIfKnock(selfBestMeldSets.get(0), selfDeadwood, oppHandGuessed);// returns the score of a single hand
            if(TESTING)
                System.out.println("The score of this hand is: " + score + "\n");
            
            sumOfScores += score * handProb; // multiOppHandScore where the sum of all  the scores of all potential hands are being added
            sumOfHandProbs += handProb;
        }

        if(TESTING) System.out.println("\nThe sum of hand probabilites is " + sumOfHandProbs);
        double expectedValueNormalized = sumOfScores / sumOfHandProbs;
        if(TESTING) System.out.println("The expected score by knocking is: " + expectedValueNormalized);

        return expectedValueNormalized;
    }

    public static void setTesting(boolean testing){
        TESTING = testing;
    }

    // test method for getCombinationOfCards
    public static void combinationTest(){
        System.out.println("=========Test for forming every possible set of unknown cards=========");
        ArrayList<Card> cards = OurUtil.makeHand(new String[] {"AC", "AH", "AD", "2S", "3C"});
        System.out.println("The total cards are: " + cards);
        ArrayList<Card[]> setOfCards = OurUtil.getCombinationOfCards(cards, 3);
        for(Card[] ArrayOfCards : setOfCards){
            System.out.print("{ ");
            for(int i = 0; i<ArrayOfCards.length; i++){
                System.out.print(ArrayOfCards[i].toString()+ " ");
            }
            System.out.print("}");
            System.out.println();
        }
        
        /**
         *  Combination Test outcome for  nHighestProbability cards : {"AC", "AH", "AD", "2S", "3C"}
         *  and we want every possible set of 3 cards from these cards:
         *  =========Test for forming every possible set of unknown cards=========
                The total cards are: [AC, AH, AD, 2S, 3C]
                { AC AH AD }
                { AC AH 2S }
                { AC AH 3C }
                { AC AD 2S }
                { AC AD 3C }
                { AC 2S 3C }
                { AH AD 2S }
                { AH AD 3C }
                { AH 2S 3C }
                { AD 2S 3C }
         */
    }

    public static void oppHandScoreTest1(){
        System.out.println("\n=================Test1 for multiOppHandScore=================\n");
        setTesting(true);
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        StateTrackerPlayer p0 = new StateTrackerPlayer(params);
        
        ArrayList<Card> selfHand = p0.myTracker.getSelfHandForHardcodedStateTracker1(); // OurUtil.makeHand(new String[] {"2S", "3S", "4S", "5S", "6S", "9C", "9H", "9S", "4C", "AD"});
        System.out.println("Our player has " + selfHand.toString());
        ArrayList<Card> oppHand = p0.myTracker.getOppHandForHardcodedStateTracker1(); // OurUtil.makeHand(new String[] {"8D", "9D", "TD", "AS", "AH", "5C", "5D", "3D", "6H", "2D"});
        System.out.println("Opp player has " + oppHand.toString());

        p0.myTracker.setToHardcodedStateTracker1();
        p0.myTracker.displayFields();
        p0.myTracker.displaySTATES();
        p0.myTracker.displayPROBS();
        int numOfAdditionalCards  = 1; //(int)params.get(ParamList.SP_NUM_OF_ADDITIONAL_CARDS);
        System.out.println("The number of additional cards to form combinations: " + numOfAdditionalCards );

        OurUtil.setTesting(true); //this is to supress print statements by computeScoreIfKnock

        // double score = oneOppHandScore(p0.myTracker, selfHand);
        double score = multiOppHandScore(p0.myTracker, selfHand, numOfAdditionalCards);
    }
    
    public static void oppHandScoreTest2(){
        //Player 0 has [[2S, 3S, 4S, 5S, 6S], [9C, 9H, 9S], [4C, QH]]
        //Player 0 has [[2S, 3S, 4S, 5S, 6S], [9C, 9H, 9S], [4C, QH]] with 14 deadwood.
        //Player 1 has [[8D, 9D, TD], [8C, AS, AH, 5C, 5D, 3D, 6H]]
        System.out.println("\n=================Test2 for multiOppHandScore=================\n");
        setTesting(true);
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        StateTrackerPlayer p0 = new StateTrackerPlayer(params);
        
        ArrayList<Card> selfHand = p0.myTracker.getSelfHandForHardcodedStateTracker2(); // OurUtil.makeHand(new String[] {"2S", "3S", "4S", "5S", "6S", "9C", "9H", "9S", "4C", "QH"});
        System.out.println("Our player has " + selfHand.toString());
        ArrayList<Card> oppHand = p0.myTracker.getOppHandForHardcodedStateTracker2(); // OurUtil.makeHand(new String[] {"8D", "9D", "TD", "AS", "AH", "5C", "5D", "3D", "6H", "8C"});
        System.out.println("Opp player has " + oppHand.toString());

        p0.myTracker.setToHardcodedStateTracker2();
        p0.myTracker.displayFields();
        p0.myTracker.displaySTATES();
        p0.myTracker.displayPROBS();
        int numOfAdditionalCards  = 2;
        System.out.println("The number of additional cards to form combinations is: " + numOfAdditionalCards );
        
        OurUtil.setTesting(false); //this is to supress print statements by computeScoreIfKnock

        // double score = oneOppHandScore(p0.myTracker, selfHand);
        double score = multiOppHandScore(p0.myTracker, selfHand, numOfAdditionalCards );
    }

    public static void oppHandScoreTest3(){
        // our player has {"2H", "3H", "4H", "AS", "2S", "3S", "7C", "7S", "7D", "4C"}
        // opp has {"6H", "7H", "8H", "9C", "9H", "9S", "9D", "AH", "AD", "2D"}
        System.out.println("\n=================Test3 for multiOppHandScore=================\n");
        setTesting(true);
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        StateTrackerPlayer p0 = new StateTrackerPlayer(params);
        
        ArrayList<Card> selfHand = p0.myTracker.getSelfHandForHardcodedStateTracker3(); 
        System.out.println("Our player has " + selfHand.toString());
        ArrayList<Card> oppHand = p0.myTracker.getOppHandForHardcodedStateTracker3(); 
        System.out.println("Opp player has " + oppHand.toString());

        p0.myTracker.setToHardcodedStateTracker3();
        p0.myTracker.displayFields();
        p0.myTracker.displaySTATES();
        p0.myTracker.displayPROBS();
        int numOfAdditionalCards  = 0;
        System.out.println("The number of additional cards to form combinations: " + numOfAdditionalCards );
        
        OurUtil.setTesting(false); //this is to supress print statements by computeScoreIfKnock

        //double score = oneOppHandScore(p0.myTracker, selfHand);
        double score = multiOppHandScore(p0.myTracker, selfHand, numOfAdditionalCards );
    }    
    
    public static void main(String []args){
        combinationTest();
        // oppHandScoreTest1();
        // oppHandScoreTest2();
        // oppHandScoreTest3();
    }
        
}
