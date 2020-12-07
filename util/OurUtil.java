package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ginrummy.GinRummyUtil;
import players.StateTracker;
import ginrummy.Card;

/**
 * Wrapper for additional utilities for the Card class.
 * 
 * @author Steven Bogaerts
 */
public class OurUtil {
    protected static boolean TESTING = false; 
    protected static boolean TIMETESTING = false;

    public static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }

    /**
     * @param cards The card Strings to be in the hand.
     * @return ArrayList of Card objects from the strings passed in
     */
    public static ArrayList<Card> makeHand(String[] cards) {
        ArrayList<Card> hand = new ArrayList<Card>(cards.length);
        for(String cardStr : cards)
            hand.add(Card.strCardMap.get(cardStr));

        return hand;
    }

    // VERIFIED 6/9
    /**
     * @param fullHand The hand of 10 cards (but will also work for 11-card "candidate hands")
     * @return An ArrayList of Cards representing the unmelded cards
     */
    public static ArrayList<Card> getUnmeldedCards(ArrayList<Card> fullHand) {
        ArrayList<ArrayList<ArrayList<Card>>> bestMeldSets = GinRummyUtil.cardsToBestMeldSets(fullHand);

        if (bestMeldSets.isEmpty())
            return new ArrayList<Card>(fullHand); // make a copy in case caller intends to mutate
        else {
            ArrayList<ArrayList<Card>> meldSet = bestMeldSets.get(0); // just arbitrarily take the first meld set
            ArrayList<Card> unmeldedCards = getUnmeldedGivenMelded(meldSet, fullHand);
            return unmeldedCards;
        }
    }

    // VERIFIED 6/9
    /**
     * This is based on code in the GinRummyGame playGame method.
     */
    private static ArrayList<Card> getUnmeldedGivenMelded(ArrayList<ArrayList<Card>> meldSet, ArrayList<Card> fullHand) {
        long unmeldedBitstring = GinRummyUtil.cardsToBitstring(fullHand); // will remove melded cards from this bitstring in loop below
        for (ArrayList<Card> meld : meldSet) {
            long meldBitstring = GinRummyUtil.cardsToBitstring(meld);
            unmeldedBitstring &= ~meldBitstring; // remove melded cards from unmeldedBitstring
        }

        ArrayList<Card> unmeldedCards = GinRummyUtil.bitstringToCards(unmeldedBitstring);

        return unmeldedCards;
    }

    // VERIFIED 6/9
    /**
     * Note that this returns a flat list of melded cards, not actual melds.
     * @param fullHand
     * @return An array of two ArrayList<Card> objects. The first element is the melded cards (in a flat ArrayList). 
     * The second element is the unmelded cards (in a flat ArrayList).
     */
    public static ArrayList<Card>[] getMeldedAndUnmeldedCards(ArrayList<Card> fullHand) {
        final int MELDED_ID = 0;
        final int UNMELDED_ID = 1;
        ArrayList<Card>[] meldedUnmelded = (ArrayList<Card>[]) new ArrayList[2];

        ArrayList<ArrayList<ArrayList<Card>>> bestMeldSets = GinRummyUtil.cardsToBestMeldSets(fullHand);
        boolean areMelds = !bestMeldSets.isEmpty();

        if (!areMelds) {
            meldedUnmelded[MELDED_ID] = new ArrayList<Card>();
            meldedUnmelded[UNMELDED_ID] = new ArrayList<Card>(fullHand); // Making a copy, just in case the caller will mutate
            return meldedUnmelded;
        }
        else {
            ArrayList<ArrayList<Card>> meldSet = bestMeldSets.get(0); // just arbitrarily take the first meld set
            // System.out.println("Melds: " + meldSet);

            // "flatten" the ArrayList<ArrayList<Card>> meld set
            ArrayList<Card> flatMeldedCards = new ArrayList<Card>(10); // may have size less than 10
            for(ArrayList<Card> meld : meldSet) {
                for(Card c: meld)
                    flatMeldedCards.add(c);
            }

            meldedUnmelded[MELDED_ID] = flatMeldedCards;
            meldedUnmelded[UNMELDED_ID] = getUnmeldedGivenMelded(meldSet, fullHand);
        }

        return meldedUnmelded;
    }


    /**
     * @param meldSet A list of all the player's melds
     * @param cards A list of cards to test if they can be laid off (i.e., fit into melds) 
     * @return An ArrayList of Cards representing the cards that were successfully able to fit into melds
     */
    public static ArrayList<Card> getLayoffGivenMelded(ArrayList<ArrayList<Card>> meldSet, ArrayList<Card> potentialLayoffCards)
    {
        ArrayList <Card> finalLayoffCards = new ArrayList<Card>(potentialLayoffCards);
        ArrayList<ArrayList<Card>> meldSetCopy = new ArrayList<ArrayList<Card>>();
        for (ArrayList<Card> meld : meldSet)
            meldSetCopy.add(new ArrayList<Card>(meld));
    
        // lay off on knocking meld (if not gin)
        boolean cardWasLaidOff;
        do { // attempt to lay each card off
            cardWasLaidOff = false;
            Card layOffCard = null;
            ArrayList<Card> layOffMeld = null;
            for (Card card : potentialLayoffCards) {
                for (ArrayList<Card> meld : meldSetCopy) {
                    ArrayList<Card> newMeld = (ArrayList<Card>) meld.clone();
                    newMeld.add(card);
                    long newMeldBitstring = GinRummyUtil.cardsToBitstring(newMeld);
                    if (GinRummyUtil.getAllMeldBitstrings().contains(newMeldBitstring)) {
                        layOffCard = card;
                        layOffMeld = meld;
                        break;
                    }
                }
                if (layOffCard != null) {
                    if (TESTING) System.out.printf("Opponent lays off %s on %s.\n", layOffCard, layOffMeld);
                    finalLayoffCards.remove(layOffCard);
                    layOffMeld.add(layOffCard);
                    cardWasLaidOff = true;
                    break;
                }
            }
        } while (cardWasLaidOff);
        
        return finalLayoffCards;
    }

    /**
     * @return positive score if self gets points, negative score if opp
     *         undercuts (and gets points), and 0 if self cann't knock.
     */
    public static double computeScoreIfKnock(ArrayList<ArrayList<Card>> selfMeldSet, int selfDeadwoodPoints, ArrayList<Card> oppHand){
        if (selfDeadwoodPoints > GinRummyUtil.MAX_DEADWOOD)  { // can't knock
            if (TESTING) System.out.println("Self can't knock.");
            return 0;
        }
        else {
            ArrayList <Card> oppUnmeldedCards = OurUtil.getUnmeldedCards(oppHand);
            if (TESTING) System.out.println("oppUnmeldedCards: " + oppUnmeldedCards);
    
            // -------------------------------------
            // Opponent tries to lay off unmelded cards
    
            // Don't need to check if selfDeadwoodPoints == 0. Gin is not tracked, since we
            // should always "knock" on gin.
    
            // System.out.println("Before laying off, selfMeldSet: " + selfMeldSet);
    
            // Copy selfMeldSet, by copying each sub-ArrayList. It will be used to determine layoffs.
            ArrayList<ArrayList<Card>> selfMeldSetCopy = new ArrayList<ArrayList<Card>>();
            for (ArrayList<Card> meld : selfMeldSet)
                selfMeldSetCopy.add(new ArrayList<Card>(meld));
    
            // lay off on knocking meld (if not gin)
            boolean cardWasLaidOff;
            do { // attempt to lay each card off
                cardWasLaidOff = false;
                Card layOffCard = null;
                ArrayList<Card> layOffMeld = null;
                for (Card card : oppUnmeldedCards) {
                    for (ArrayList<Card> meld : selfMeldSetCopy) {
                        ArrayList<Card> newMeld = (ArrayList<Card>) meld.clone();
                        newMeld.add(card);
                        long newMeldBitstring = GinRummyUtil.cardsToBitstring(newMeld);
                        if (GinRummyUtil.getAllMeldBitstrings().contains(newMeldBitstring)) {
                            layOffCard = card;
                            layOffMeld = meld;
                            break;
                        }
                    }
                    if (layOffCard != null) {
                        if (TESTING) System.out.printf("Opponent lays off %s on %s.\n", layOffCard, layOffMeld);
                        oppUnmeldedCards.remove(layOffCard);
                        layOffMeld.add(layOffCard);
                        cardWasLaidOff = true;
                        break;
                    }
    
                }
            } while (cardWasLaidOff);
    
            // Now, oppUnmeldedCards only contains cards that can't be laid off
    
            // ------------------------------------
            // Compute oppDeadwoodPoints
    
            int oppDeadwoodPoints = 0;
            for (final Card card : oppUnmeldedCards)
                oppDeadwoodPoints += GinRummyUtil.getDeadwoodPoints(card);
            if (TESTING) System.out.printf("Opponent has %d deadwood with %s\n", oppDeadwoodPoints, oppUnmeldedCards);
    
            // compare deadwood and compute new scores
            // Gin won't happen in this code - self will always knock on Gin, thus it's not
            // tracked here
    
            double score;
            if(selfDeadwoodPoints == 0){
                if(TESTING) System.out.printf("Self can win by gin");
                return GinRummyUtil.GIN_BONUS + oppDeadwoodPoints;
            }
            else if (selfDeadwoodPoints < oppDeadwoodPoints) { // non-gin round win
                score = oppDeadwoodPoints - selfDeadwoodPoints;
                if (TESTING) System.out.printf("Self scores the deadwood difference of %d.\n", oppDeadwoodPoints - selfDeadwoodPoints);
            } 
            else { // undercut win for opponent - register as a negative score for self
                score = -1 * (GinRummyUtil.UNDERCUT_BONUS + selfDeadwoodPoints - oppDeadwoodPoints);
                if (TESTING) System.out.printf(
                        "Opponent undercuts and scores the undercut bonus of %d plus deadwood difference of %d for %d total points.\n",
                        GinRummyUtil.UNDERCUT_BONUS, selfDeadwoodPoints - oppDeadwoodPoints,
                        GinRummyUtil.UNDERCUT_BONUS + selfDeadwoodPoints - oppDeadwoodPoints);
            }
            return score;
        }
    }

    /**
     * @return list of list of cards
     * @param myTracker
     * @param numAdditionalCards the number of additional cards to get to form combinations of cards. When this equals to 0, there is only one combination of cards
     * @param numCardsToChoose the number of cards with which you want to form combinations. Most of the time, this should be equal to the number of cards unknown in the opp's hand
     */
    public static ArrayList<ArrayList<Card>> multiOppHand(StateTracker myTracker, int numAdditionalCards, int numCardsToChoose){
        double start, elapsed; 

        ArrayList<Card> oppCardsKnown = (ArrayList<Card>)myTracker.getOppCardsKnown().clone(); 
        // int numOfCardsKnown = oppCardsKnown.size();
        // int numOfCardsUnknown = StateTracker.HAND_SIZE - numOfCardsKnown;

        start = System.currentTimeMillis();
        Map<Card, Double> mapOfCardsWithHighestProb = myTracker.getCardToProbMapWithHighestProbInOppHand(numCardsToChoose + numAdditionalCards);//shoukd be > than numOfCardsUnknown: cards that have the highest prob of being in the opp hand but are more than we need to complete a hand
        elapsed = System.currentTimeMillis() - start;
        if(TIMETESTING) System.out.println("Elapsed time (ms): " + elapsed + " while picking up " + (numCardsToChoose + numAdditionalCards) + " cards");
        
        if(TESTING){
            //System.out.println("The number of cards with highest probability are: " + numElements);
            System.out.println("The set of cards with highest probability are: ");
            for(Map.Entry<Card, Double> entry: mapOfCardsWithHighestProb.entrySet()){
                System.out.println("[Card: " + entry.getKey().toString() + ", Probability: " + entry.getValue().toString() + "] ");
            }
            System.out.println();
        }

        ArrayList<Card> oppCardsGuessed = new ArrayList<Card>();
        for(Card card: mapOfCardsWithHighestProb.keySet()){
            oppCardsGuessed.add(card);// n (as mentioned above the method) number of cards with highest probability
        }

        start = System.currentTimeMillis();
        ArrayList<Card[]> combOfOppCardsGuessed = OurUtil.getCombinationOfCards(oppCardsGuessed, numCardsToChoose);//this will return a list of every possible set of numOfCardsUnknown from nhighestprobabilityunknown cards
        elapsed = System.currentTimeMillis() - start;
        if(TIMETESTING) System.out.println("Elapsed time (ms): " + elapsed + " while making " + (numCardsToChoose + numAdditionalCards) + "C" + numCardsToChoose);

        if(TESTING){
            System.out.println("The number of cards to choose is: " + numCardsToChoose);
            System.out.println("The number of additional cards is: " + numAdditionalCards);
            System.out.println("Each set of possible unknown cards from n Highest probability cards are: ");
            for(Card[] ArrayOfCards : combOfOppCardsGuessed){
                System.out.print("{ ");
                for(int i = 0; i<ArrayOfCards.length; i++){
                    System.out.print(ArrayOfCards[i].toString()+ " ");
                }
                System.out.print("}");
                System.out.println();
            } 
            System.out.println();         
        }

        ArrayList<ArrayList<Card>> combOfOppHands = new ArrayList<>();
        for(int i = 0; i < combOfOppCardsGuessed.size(); i++){
            ArrayList<Card> oneOppHandGuessed = new ArrayList<>();
            oneOppHandGuessed.addAll(oppCardsKnown);
            oneOppHandGuessed.addAll(Arrays.asList(combOfOppCardsGuessed.get(i)));
            combOfOppHands.add(oneOppHandGuessed);
        }

        if(TESTING){
            System.out.println("Combination of opponent's hand guessed");
            for(int i = 0; i < combOfOppCardsGuessed.size(); i++){
                System.out.println(combOfOppHands.get(i));
            }
        }

        return combOfOppHands;
    }

    public static double calcOppHandProbability(ArrayList<Card> hand, StateTracker myTracker){
        double handProb = 1;
        double[][][] matrix = myTracker.getMatrix();
        for(Card card: hand){
            if(matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.KNOW_WHERE_IT_IS){
                if(matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.OPP_FROM_DISCARD
                    || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.OPP_FROM_START
                    || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_TOP_DISCARD){
                        handProb *= 1;
                }
                else if(matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_FROM_STOCK
                        || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_FROM_DISCARD
                        || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_FROM_START
                        || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.SELF_BURIED_DISCARD
                        || matrix[card.getSuit()][card.getRank()][StateTracker.STATE] == StateTracker.OPP_BURIED_DISCARD){
                        handProb *= 0;
                }
            }
            else
                handProb *= (1 - matrix[card.getSuit()][card.getRank()][StateTracker.PROB_IN_STOCK]);
        }
        return handProb;
    }

    // VERIFIED 6/17
    /**
     * @return a set of every possible set of u unknown cards from n number of Highest probbaility cards
     */
    public static ArrayList<Card[]> getCombinationOfCards(ArrayList<Card> cards, int numOfCardsToTake){
        int numOfHighestProbCards = cards.size();
        ArrayList<Card[]> combinations = new ArrayList<Card[]>();
        Card[] combination = new Card[numOfCardsToTake];
        
        for(int i = 0; i < numOfCardsToTake; i++){
            combination[i] = cards.get(i);
        }

        outerloop:
        while(cards.indexOf(combination[numOfCardsToTake - 1]) < numOfHighestProbCards ){
            combinations.add(combination.clone());
            int t = numOfCardsToTake - 1;
            while(t != 0 && (cards.indexOf(combination[t]) == numOfHighestProbCards  - numOfCardsToTake + t)){
                t--;
            }

            int current = cards.indexOf(combination[t]);
            if(current < cards.size() - 1)
                combination[t] = cards.get(current + 1);
            else
                break;

            for (int i = t + 1; i < numOfCardsToTake; i++){
                int index = cards.indexOf(combination[i-1]);
                if(index < cards.size() - 1)
                    combination[i] = cards.get(index + 1);
                else
                    break outerloop;
            }
        }
        return combinations;
    }

    public static Card getCard(int rank, int suit){
        return Card.allCards[suit * Card.NUM_RANKS + rank];
    }

    public static void setTesting(boolean testing){
        TESTING = testing;
    }

    public static void setTimeTesting(boolean testing){
        TIMETESTING = testing;
    }

    /*
    public static int getScoreIfSelfKnocks(selfMelds, ArrayList<Card> oppHand) {
        // find melds for self
        // layoff for opp
        // return negative score if opp undercuts
    }
    */

    private static void testGetUnmeldedCards() {
        System.out.println("==================== testGetUnmeldedCards ====================");
        System.out.println("--------------------------");
        ArrayList<Card> hand;
        hand = makeHand(new String[]{"AD", "2D", "3D", "4D", "6C", "6D", "6H", "5C", "4H", "JH"});
        System.out.println("Full hand: " + hand);
        System.out.println("Unmelded: " + getUnmeldedCards(hand));

        System.out.println("--------------------------");
        hand = makeHand(new String[]{"AD", "2D", "3D", "4D", "6C", "6D", "6H", "5C", "4H", "JH", "JD"});
        System.out.println("Full hand (11 cards): " + hand);
        System.out.println("Unmelded: " + getUnmeldedCards(hand));

        System.out.println("--------------------------");
        hand = makeHand(new String[]{"AD", "2D", "3D", "4D", "6C", "6D", "6H", "5C", "4H", "JH", "6S"});
        System.out.println("Full hand (11 cards): " + hand);
        System.out.println("Unmelded: " + getUnmeldedCards(hand));

        System.out.println("--------------------------");
        hand = makeHand(new String[]{"AD", "2D", "3D", "4D", "6C", "6D", "6H", "6S", "JC", "JD", "JH"});
        System.out.println("Full hand (11 cards): " + hand);
        System.out.println("Unmelded: " + getUnmeldedCards(hand));

        /*
        Full hand: [AD, 2D, 3D, 4D, 6C, 6D, 6H, 5C, 4H, JH]
        Unmelded: [5C, 4H, JH]
        Full hand (11 cards): [AD, 2D, 3D, 4D, 6C, 6D, 6H, 5C, 4H, JH, JD]
        Unmelded: [5C, 4H, JH, JD]
        Full hand (11 cards): [AD, 2D, 3D, 4D, 6C, 6D, 6H, 5C, 4H, JH, 6S]
        Unmelded: [5C, 4H, JH]
        Full hand (11 cards): [AD, 2D, 3D, 4D, 6C, 6D, 6H, 6S, JC, JD, JH]
        Unmelded: []
        */
    }

    private static void testGetMeldedAndUnmeldedCards() {
        System.out.println("==================== testGetMeldedAndUnmeldedCards ====================");
        ArrayList<Card> hand;
        ArrayList<Card>[] meldedUnmelded;

        System.out.println("--------------------------");
        hand = makeHand(new String[]{"AD", "2S", "3D", "4S", "5D", "6S", "7D", "8S", "9D", "TS"});
        System.out.println("Full hand: " + hand);
        meldedUnmelded = getMeldedAndUnmeldedCards(hand);
        System.out.println("Melded: " + meldedUnmelded[0]);
        System.out.println("Unmelded: " + meldedUnmelded[1]);

        System.out.println("--------------------------");
        hand = makeHand(new String[]{"AD", "2D", "3D", "4D", "6C", "6D", "6H", "5C", "4H", "JH"});
        System.out.println("Full hand: " + hand);
        meldedUnmelded = getMeldedAndUnmeldedCards(hand);
        System.out.println("Melded: " + meldedUnmelded[0]);
        System.out.println("Unmelded: " + meldedUnmelded[1]);

        System.out.println("--------------------------");
        hand = makeHand(new String[]{"AD", "2D", "3D", "4D", "6C", "6D", "6H", "5C", "4H", "JH", "JD"});
        System.out.println("Full hand (11 cards): " + hand);
        meldedUnmelded = getMeldedAndUnmeldedCards(hand);
        System.out.println("Melded: " + meldedUnmelded[0]);
        System.out.println("Unmelded: " + meldedUnmelded[1]);

        System.out.println("--------------------------");
        hand = makeHand(new String[]{"AD", "2D", "3D", "4D", "6C", "6D", "6H", "5C", "4H", "JH", "6S"});
        System.out.println("Full hand (11 cards): " + hand);
        meldedUnmelded = getMeldedAndUnmeldedCards(hand);
        System.out.println("Melded: " + meldedUnmelded[0]);
        System.out.println("Unmelded: " + meldedUnmelded[1]);

        System.out.println("--------------------------");
        hand = makeHand(new String[]{"AD", "2D", "3D", "4D", "6C", "6D", "6H", "6S", "JC", "JD", "JH"});
        System.out.println("Full hand (11 cards): " + hand);
        meldedUnmelded = getMeldedAndUnmeldedCards(hand);
        System.out.println("Melded: " + meldedUnmelded[0]);
        System.out.println("Unmelded: " + meldedUnmelded[1]);

        /*
        --------------------------
        Full hand: [AD, 2S, 3D, 4S, 5D, 6S, 7D, 8S, 9D, TS]
        Melded: []
        Unmelded: [AD, 2S, 3D, 4S, 5D, 6S, 7D, 8S, 9D, TS]
        --------------------------
        Full hand: [AD, 2D, 3D, 4D, 6C, 6D, 6H, 5C, 4H, JH]
        Melds: [[AD, 2D, 3D, 4D], [6C, 6H, 6D]]
        Melded: [AD, 2D, 3D, 4D, 6C, 6H, 6D]
        Unmelded: [5C, 4H, JH]
        --------------------------
        Full hand: [AD, 2D, 3D, 4D, 6C, 6D, 6H, 5C, 4H, JH, JD]
        Melds: [[AD, 2D, 3D, 4D], [6C, 6H, 6D]]
        Melded: [AD, 2D, 3D, 4D, 6C, 6H, 6D]
        Unmelded: [5C, 4H, JH, JD]
        --------------------------
        Full hand: [AD, 2D, 3D, 4D, 6C, 6D, 6H, 5C, 4H, JH, 6S]
        Melds: [[AD, 2D, 3D, 4D], [6C, 6H, 6S, 6D]]
        Melded: [AD, 2D, 3D, 4D, 6C, 6H, 6S, 6D]
        Unmelded: [5C, 4H, JH]
        --------------------------
        Full hand: [AD, 2D, 3D, 4D, 6C, 6D, 6H, 6S, JC, JD, JH]
        Melds: [[JC, JH, JD], [AD, 2D, 3D, 4D], [6C, 6H, 6S, 6D]]
        Melded: [JC, JH, JD, AD, 2D, 3D, 4D, 6C, 6H, 6S, 6D]
        Unmelded: []
        */
    }

    public static void main(String[] args) {
        testGetUnmeldedCards();
        testGetMeldedAndUnmeldedCards();
    }

}