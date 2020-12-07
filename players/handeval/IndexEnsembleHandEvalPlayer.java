package players.handeval;

import ginrummy.Card;
import ginrummy.GinRummyPlayer;
//import given.GinRummyGame;
import games.TestingGame;
// import players.KnockOnGinPlayer;
import players.OurSimpleGinRummyPlayer;
import players.ParamList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Arrays;
import players.SimpleFakeGinRummyPlayer;

import ga.GATuner;

/**
 * This class is the same as EnsembleHandEvalPlayer, except that the ensemble members
 * are compared based on their indices, rather than their actual evalHand scores.
 * That is, each ensemble member computes an ordered list of the 11 hands based on its
 * evalHand method, and this player chooses the hand corresponding to the best indices
 * in these ordered lists.
 * This makes it easy to compare various ensembles that output values with different magnitudes.
 */
public class IndexEnsembleHandEvalPlayer extends EnsembleHandEvalPlayer {

    private static boolean TESTING = false;

    public IndexEnsembleHandEvalPlayer(ParamList params, HandEvaluator ... hes) {
        super(params, hes);
    }

    // VERIFIED 6/17
    /**
     * Finds the evalIndex for each card according to each HandEvaluator in this ensemble.
     * Then, for each card, computes a weighted sum of evalIndex values (weighted by HandEvaluator weight).
     * The card with the highest weighted sum is returned. This is the card that should be removed
     * to give the highest-scoring handOf10.
     * 
     * I'm not sure how to intentially make the StateTracker have a different ParamList, but there is a check for this
     * as well.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Card choose10From11Cards(ArrayList<Card> handOf11) {
        if (TESTING) System.out.println("-------------------------------\nchoose10From11Cards begins.\nhandOf11: " + handOf11);

        // For each HandEvaluator, get the corresponding cardToEvalIndexMap
        HashMap<Card, Integer>[] cardToEvalIndexMaps = (HashMap<Card, Integer>[]) new HashMap[handEvaluators.size()];
        for(int heID = 0; heID < handEvaluators.size(); heID++) {
            if (TESTING) System.out.println("----------\nStarting cardToEvalIndexMaps[" + heID + "] process.");
            cardToEvalIndexMaps[heID] = getCardToEvalIndexMap(handOf11, handEvaluators.get(heID).he);
            if (TESTING) System.out.println("Resulting cardToEvalIndexMaps[" + heID + "]: " + cardToEvalIndexMaps[heID]);
        }

        /*
        So, for example, suppose map=cardToEvalIndexMaps[0] is the cardToEvalIndexMap for handEvaluators.get(0).
        So for a Card c in handOf11, map.get(c) is the index for c.
        More specifically, the question:
        "If c were removed from handOf11, what evalHand score would result?"
        is asked for every card in handOf11. The resulting evalHand scores are sorted from lowest to highest.
        The card at the end of the list is most beneficial to drop.

        Since this is an ensemble, every HandEvaluator "he" makes its own cardToEvalIndexMap, stored in cardToEvalIndexMaps[heID].
        The indices of all these are combined via a weighted sum, and the card corresponding to the highest weighted sum is returned.
        */

        if(TESTING) {
            for(int i = 0; i < handEvaluators.size(); i++)
                System.out.println("Weight for HandEvaluator[" + i + "]: " + getWeight(i));
        }

        double[] allWeightedSums = new double[11];
        for(int cardID = 0; cardID < allWeightedSums.length; cardID++){ // for each card, we need a weighted sum of the eval indices
            // Get the hand of 10 corresponding to removing card i
            ArrayList<Card> handOf10 = new ArrayList<Card>(handOf11);
            Card removedCard = handOf11.get(cardID);
            handOf10.remove(removedCard);

            // Find the weighted sum for the current card
            allWeightedSums[cardID] = 0;
            for(int heID = 0; heID < handEvaluators.size(); heID++) { // for each hand evaluator
                int indexOfHandEvalIfCardRemoved = cardToEvalIndexMaps[heID].get(removedCard); // get the eval index saved in the corresponding map
                allWeightedSums[cardID] += indexOfHandEvalIfCardRemoved * getWeight(heID); // add the eval index to the weighted sum
            }
            
            if (TESTING) System.out.println("---------------\nhand of 10: " + handOf10 + " by removing: " + handOf11.get(cardID) + "\neval: " + allWeightedSums[cardID]);
        }

        // Get the maximum weighted sum - that's the one that would give the maximum score ***if it is removed***
        // So that's the card we want to discard
        double maxEval = allWeightedSums[0];
        int indexOfCardDiscarded = 0;
        int loopLimit = drewFaceUpCard ? 10 : 11; // if drewFaceUpCard, then don't consider dropping it
        for(int j = 1; j < loopLimit; j++) {
            if(allWeightedSums[j]>maxEval){
                maxEval = allWeightedSums[j];
                indexOfCardDiscarded = j;
            }       
        }

        if (TESTING) System.out.println("maxEval: " + maxEval + ", indexOfCardDiscarded: " + indexOfCardDiscarded);
        
        return handOf11.get(indexOfCardDiscarded);
    }
    
    // VERIFIED 6/9
    /**
     * Computes a map from card to index ordered by evalHand (the hand resulting from leaving that card out).
     * The map is ordered from lowest handEval value to highest.
     * So the last card is the one we want to drop, since that corresponds to the highest handEval value
     * (the value obtained by dropping that card is highest).
     * 
     * Also accounts for ties by assigning the same rank to tied cards,
     * and then skipping subsequent ranks for the next card.
     * 
     * @param handOf11 A list of 11 cards under consideration
     * @param he The HandEvaluator on which to base this map.
     * @return A map from Card to evalIndex value
     */
    protected HashMap<Card, Integer> getCardToEvalIndexMap(ArrayList<Card> handOf11, HandEvaluator he) {
        double[] allEvalValues = new double[11];
        HashMap<Card, Double> unsortedCardToEval = new HashMap<Card, Double>(handOf11.size());
        for(int i = 0; i < allEvalValues.length; i++) {
            ArrayList<Card> handOf10 = new ArrayList<Card>(handOf11);
            Card cardToDrop = handOf11.get(i);
            handOf10.remove(cardToDrop);
            unsortedCardToEval.put(cardToDrop, he.evalHand(handOf10, this.myStateTracker, cardToDrop));
        }

        // Sort in order from lowest value to highest, 
        // so the last card is the one we want to drop, since it leads to the highest handEval for the remaining 10 cards.
        // https://howtodoinjava.com/sort/java-sort-map-by-values/
        LinkedHashMap<Card, Double> sortedCardToEval = new LinkedHashMap<Card, Double>(unsortedCardToEval.size());
        unsortedCardToEval.entrySet().stream()
                                    .sorted(Map.Entry.comparingByValue())
                                    .forEachOrdered(x -> sortedCardToEval.put(x.getKey(), x.getValue()));

        if (TESTING) System.out.println("sortedCardToEval: " + sortedCardToEval);

        // Add the cards in sorted order, while watching for ties (in eval score)
        HashMap<Card, Integer> cardToEvalIndex = new HashMap<Card, Integer>(handOf11.size());
        double previousEval = -1; // dummy initial value
        int rankInList = -1; // will be incremented first time in loop
        int tieCount = 0;
        for(Map.Entry<Card, Double> entry : sortedCardToEval.entrySet()) {
            Card currCard = entry.getKey();
            double currEval = entry.getValue();

            if (currEval != previousEval) { // no tie this time
                rankInList += tieCount + 1; // Advance at least 1, plus the length of the previous sequence of ties
                tieCount = 0; // reset the tie count
            }
            else // there's a tie, so rank stays the same
                tieCount++;

            cardToEvalIndex.put(currCard, rankInList);
            previousEval = currEval; // track this value, to check for a tie with the next card
        }

        return cardToEvalIndex;
    }

    public static void setTesting(boolean testing){
        TESTING = testing;
    }

    /**
     * This method is mapped to the stream elements.
     * 
     * @return Win percentage
     */
    private static double testWeight(double currWeight, double numGames) {
        double[] ensembleWeights = new double[] {1, 1};
        ParamList params = new ParamList(ensembleWeights);
        IndexEnsembleHandEvalPlayer p0 = new IndexEnsembleHandEvalPlayer(params, new MeldabilityHandEvaluator(params), new DeadwoodHandEvaluator());

        GinRummyPlayer p1 = new OurSimpleGinRummyPlayer();
        TestingGame game = new TestingGame(p0, p1);
        int currHosWins = 0;

        p0.setAllWeights(new double[] {currWeight, 1-currWeight});
        for(int gameID = 0; gameID < numGames; gameID++) {
            int winner = game.play();
            if (winner == 0)
                currHosWins++;
        }
        System.out.println(currWeight + ": " + currHosWins / numGames);
        
        return currHosWins / numGames;
    }

    public static void testLikeHOSHandEvalPlayerStream() {
        System.out.println("=============== testLikeHOSHandEvalPlayerStream ===============");
        int numTests = 51;
        double stepSize = 1.0 / (numTests-1);

        double[] winPercentages;
        double start, elapsed;
        int numGames = 1000;

        // Time it in parallel
        start = System.currentTimeMillis();
        winPercentages = java.util.stream.DoubleStream.iterate(0, i -> i + stepSize)
                                                      .limit(numTests)
                                                      .parallel()
                                                      .map(w -> testWeight(w, numGames))
                                                      .toArray();
        // the arbitrary ordering of execution (above) does not harm the ordering in the resulting array                                                               
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time in parallel: " + elapsed);

        System.out.println("In order of weight, the winPercentages are:\n" + java.util.Arrays.toString(winPercentages));

        double max = -1;
        int maxID = -1;
        for (int i = 0; i < winPercentages.length; i++) {
            if (winPercentages[i] > max) {
                max = winPercentages[i];
                maxID = i;
            }
        }
        System.out.println("Maximum is: " + max + " for id " + maxID + ", which is weight " + (maxID*0.02));
    }

    public static void testLikeHOSHandEvalPlayerLoop() {
        System.out.println("=============== testLikeHOSHandEvalPlayerLoop ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        double[] ensembleWeights = new double[] {1, 1};
        ParamList params = new ParamList(ensembleWeights);
        IndexEnsembleHandEvalPlayer p0 = new IndexEnsembleHandEvalPlayer(params, new MeldabilityHandEvaluator(params), new DeadwoodHandEvaluator());
        
        GinRummyPlayer p1 = new OurSimpleGinRummyPlayer();

        TestingGame game = new TestingGame(p0, p1);

        int numGames = 1000;
        int currHosWins;
        int maxWins = -1;
        double maxWinsWeight = 0.0;

        for(double currWeight = 0.0; currWeight < 1.0; currWeight+=0.02) {
            currHosWins = 0;
            p0.setAllWeights(new double[] {currWeight, 1-currWeight});
            for(int gameID = 0; gameID < numGames; gameID++) {
                int winner = game.play();
                if (winner == 0)
                    currHosWins++;
            }
            System.out.println(currWeight + ": " + ((double) currHosWins)/numGames);
            if (currHosWins > maxWins) {
                maxWins = currHosWins;
                maxWinsWeight = currWeight;
            }
        }

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time in parallel: " + elapsed);

        System.out.println("Max wins percentage: " + ((double) maxWins)/numGames + ", best eval weight: " + maxWinsWeight + ", best point weight: " + (1-maxWinsWeight));
    }

    public static void testChoose10From11() {
        EnsembleHandEvalPlayer p;
        ArrayList<Card> hand;
        Card result;

        System.out.println("=============== testChoose10From11, trial 1 ===============");
        double[] ensembleWeights = new double[] {0.3, 0.7};
        ParamList params = new ParamList(ensembleWeights);
        params.set(ParamList.CH_SAMERANK, 0);
        params.set(ParamList.CH_ONEAWAY, 0);
        params.set(ParamList.CH_TWOAWAY, 1);
        params.enforceRestrictions();
        p = new IndexEnsembleHandEvalPlayer(params, new MeldabilityHandEvaluator(params), new ConvHandEvaluator(params));

        p.getStateTracker().setToHardcodedStateTracker1();
        hand = p.getStateTracker().getSelfHandForHardcodedStateTracker1();
        // don't need to set cards for the player though

        hand.add(Card.strCardMap.get("7S")); // adds on to the run meld
        System.out.println("**********How to update the StateTracker from here, to handle this card addition?");

        result = p.choose10From11Cards(hand);
        System.out.println("choose10From11Cards returned: " + result);

        System.out.println("4C by-hand calculation: 9*0.3 + 6*0.7 = " + (9*0.3 + 6*0.7));
        System.out.println("AD by-hand calculation: 10*0.3 + 6*0.7 = " + (10*0.3 + 6*0.7));

        System.out.println("=============== testChoose10From11, trial 2 ===============");
        double[] moreEnsembleWeights = new double[] {0.005, 0.995};
        ParamList moreParams = new ParamList(moreEnsembleWeights);
        moreParams.set(ParamList.CH_SAMERANK, 0);
        moreParams.set(ParamList.CH_ONEAWAY, 0);
        moreParams.set(ParamList.CH_TWOAWAY, 1);
        moreParams.enforceRestrictions();
        p = new IndexEnsembleHandEvalPlayer(moreParams, new MeldabilityHandEvaluator(moreParams), new ConvHandEvaluator(moreParams));

        p.getStateTracker().setToHardcodedStateTracker1();
        hand = p.getStateTracker().getSelfHandForHardcodedStateTracker1();
        // don't need to set cards for the player though

        hand.add(Card.strCardMap.get("7S")); // adds on to the run meld
        System.out.println("**********How to update the StateTracker from here, to handle this card addition?");

        result = p.choose10From11Cards(hand);
        System.out.println("choose10From11Cards returned: " + result);

        System.out.println("4C by-hand calculation: 9*0.005 + 6*0.995 = " + (9*0.005 + 6*0.995));
        System.out.println("AD by-hand calculation: 10*0.005 + 6*0.995 = " + (10*0.005 + 6*0.995));
    }

    /**
     * Use this method to test for various assertions.
     * To cause a ParamList error, change one of the params to params2.
     * One could also pass 0 members to the ensemble, or give a weight array length that mismatches,
     * or set all ensembleWeights to 0.
     */
    public static void testAssertions() {
        double[] ensembleWeights = new double[] {0.15, 0.70, 0.10, 0.10, 0.10, 0.10, 0.10};
        double[] allZeros = new double[]        {0,    0,    0,    0,    0,    0,    0};
        double[] tooShort = new double[]        {0.15}; // too short will cause IndexOutOfBoundsException
        double[] tooLong = new double[]         {0.15, 1,    1,    1,    1,    1,    1,    1}; // too long will lead to an assertion error

        ParamList params = new ParamList(ensembleWeights);
        params.set(ParamList.TS_KNOCK_THRESHOLD_EARLY, 10.0); // default is 9.0
        params.set(ParamList.TS_KNOCK_THRESHOLD_LATE, 0.0); // default is 6.0
        params.set(ParamList.TS_KNOCK_MIDDLE, 4.0); // default is 6.0
        params.enforceRestrictions();

        ParamList params2 = new ParamList(ensembleWeights); // Object looks the same, but is not the same
        params2.set(ParamList.TS_KNOCK_THRESHOLD_EARLY, 10.0); // default is 9.0
        params2.set(ParamList.TS_KNOCK_THRESHOLD_LATE, 0.0); // default is 6.0
        params2.set(ParamList.TS_KNOCK_MIDDLE, 4.0); // default is 6.0
        params2.enforceRestrictions();

        LinearDeadwoodPenaltyHandEvaluator linearHe = new LinearDeadwoodPenaltyHandEvaluator(params);
        OppCardsKnownDeadwoodPenaltyHandEvaluator oppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params);
        IndexEnsembleHandEvalPlayer p0 = new IndexEnsembleHandEvalPlayer(params, new MeldabilityHandEvaluator(params), 
                                                                                 new DeadwoodHandEvaluator(), 
                                                                                 new AceTwoBonusHandEvaluator(), 
                                                                                 new ConvHandEvaluator(params), 
                                                                                 new MultiOppHandMeldabilityEvaluator(params),
                                                                                 linearHe,
                                                                                 oppCardsHe);
        linearHe.setEnsemblePlayer(p0);
        oppCardsHe.setEnsemblePlayer(p0);

        // p0.setKnockDecider(new OneStageKnockDecider(params));
        p0.setKnockDecider(new TwoStageKnockDecider(params));

        System.out.println("If this message shows up, then no assertion errors were detected!");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Makes a certain number of threads, each of which being responsible for playing n / numThreads games.
     * @param n The total number of games to be played.
     * @return The average win rate for the total number of games.
     */
    public static double playNGamesStream(int n) {
        int numThreads = 4;
        int gamesPerThread = n / numThreads;
        /*
        double[] winRates = java.util.stream.DoubleStream.iterate(0, i -> i + 1)
                                                .limit(numThreads)
                                                .parallel()
                                                .map(threadID -> playNGames(gamesPerThread))
                                                .toArray();
        System.out.println("winRates: " + java.util.Arrays.toString(winRates));
        */

        double winRate = java.util.stream.DoubleStream.iterate(0, i -> i + 1)
                                                      .limit(numThreads)
                                                      .parallel()
                                                      .map(threadID -> playNGamesLoop(gamesPerThread))
                                                      .average().getAsDouble();

        return winRate;
    }

    /**
     * @param n The number of games to be played.
     */
    public static double playNGamesLoop(int n) {
        System.out.println("In playNGames: " + n);
        IndexEnsembleHandEvalPlayer p0 = makeAllEvaluatorsEnsemble();
        GinRummyPlayer p1 = new OurSimpleGinRummyPlayer();
        TestingGame game = new TestingGame(p0, p1);

        int currWins = 0;

        for(int gameID = 0; gameID < n; gameID++) {
            int winner = game.play();
            if (winner == 0)
                currWins++;
        }

        return currWins / ((double) n);
    }

    private static ParamList getParamsBy4GAMix() {
        double[] ensembleWeights = new double[]    {0.15,  0.70,  0.10,  0.10, /*0.10,*/ 0.10,  0.10};
        // double[] ensembleWeights = new double[]    {0.372, 0.030, 0.185, 0.180,          0.038, 0.195}; // gen 4
        // double[] ensembleWeights = new double[]    {0.309, 0.033, 0.054, 0.455,          0.115, 0.034}; // gen 10
                                                    //  Meld   Dead   Ace2   Conv    Multi   LinD   OppC
        // double[] ensembleWeights = new double[]    {0.35,  0.03,  0.10,  0.30, /*0.10,*/ 0.05,  0.10}; // guessing
        ParamList params = new ParamList(ensembleWeights); 
        // Parameters based on 7/4 GA run
        params.set(ParamList.CH_SAMERANK, 0.424); // from Gen 10
        params.set(ParamList.CH_ONEAWAY, 0.305);
        params.set(ParamList.CH_TWOAWAY, 0.271);

        params.set(ParamList.MC_SELF_LOW_OBTAINABILITY, 0.201); // from Gen 4
        params.set(ParamList.MC_SELF_RATIO_FOR_UNKNOWN, 0.634);
        params.set(ParamList.MC_SELF_WRANK, 0.397);
        params.set(ParamList.MC_SELF_WRUN, 0.603);

        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, 0.9);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, 0.25);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, 0.9);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, 0.25);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, 0.18);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, 0.25);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, 0.9);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, 0.25);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, 0.9);

        params.set(ParamList.LD_PENALTY_SLOPE, 0.394); // Gen 10
        params.set(ParamList.LD_PENALTY_EXPONENT, 3.966);

        params.set(ParamList.OD_PENALTY_SLOPE, 0.418); // Gen 4
        params.set(ParamList.OD_PENALTY_EXPONENT, 2.521);

        return params;
    }

    private static ParamList getParamsBy4GAGen10() {
                                                //  Meld   Dead   Ace2   Conv    Multi   LinD   OppC
        double[] ensembleWeights = new double[]    {0.309, 0.033, 0.054, 0.455,  /*0.10,*/   0.115, 0.034}; // gen 10
        ParamList params = new ParamList(ensembleWeights);

        // Parameters based on 7/4 GA run
        params.set(ParamList.CH_SAMERANK, 0.424);
        params.set(ParamList.CH_ONEAWAY, 0.305);
        params.set(ParamList.CH_TWOAWAY, 0.271);

        params.set(ParamList.MC_SELF_LOW_OBTAINABILITY, 0.194);
        params.set(ParamList.MC_SELF_RATIO_FOR_UNKNOWN, 0.730);
        params.set(ParamList.MC_SELF_WRANK, 0.378);
        params.set(ParamList.MC_SELF_WRUN, 0.622);

        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, 0.9);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, 0.376);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, 0.9);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, 0.376);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, 0.304);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, 0.376);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, 0.9);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, 0.376);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, 0.9);

        params.set(ParamList.LD_PENALTY_SLOPE, 0.394);
        params.set(ParamList.LD_PENALTY_EXPONENT, 3.966);

        params.set(ParamList.OD_PENALTY_SLOPE, 0.399);
        params.set(ParamList.OD_PENALTY_EXPONENT, 1.450);

        return params;
    }

    public static IndexEnsembleHandEvalPlayer makeAllEvaluatorsEnsemble() {
        ParamList params;
        // params = getParamsBy4GAMix();
        params = getParamsBy4GAGen10();

        params.set(ParamList.TS_KNOCK_THRESHOLD_EARLY, 10.0); // default is 9.0
        params.set(ParamList.TS_KNOCK_THRESHOLD_LATE, 0.0); // default is 6.0
        params.set(ParamList.TS_KNOCK_MIDDLE, 4.0); // default is 6.0
        params.set(ParamList.OM_NUM_OF_ADDITIONAL_CARDS, 1.0); // default is 1.0
        params.enforceRestrictions();
        LinearDeadwoodPenaltyHandEvaluator linearHe = new LinearDeadwoodPenaltyHandEvaluator(params);
        OppCardsKnownDeadwoodPenaltyHandEvaluator oppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params);
        IndexEnsembleHandEvalPlayer p0 = new IndexEnsembleHandEvalPlayer(params, new MeldabilityHandEvaluator(params), 
                                                                                 new DeadwoodHandEvaluator(), 
                                                                                 new AceTwoBonusHandEvaluator(), 
                                                                                 new ConvHandEvaluator(params), 
                                                                                 // new MultiOppHandMeldabilityEvaluator(params),
                                                                                 linearHe,
                                                                                 oppCardsHe);
        linearHe.setEnsemblePlayer(p0);
        oppCardsHe.setEnsemblePlayer(p0);

        // p0.setKnockDecider(new OneStageKnockDecider(params));
        // p0.setKnockDecider(new TwoStageKnockDecider(params));
        p0.setKnockDecider(new ScorePredictionKnockDecider(params));

        return p0;
    }

    public static void demoAllEvaluatorsEnsemble() {
        System.out.println("=============== demoAllEvaluatorsEnsemble ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int numGames = 1000;
        double winRate = playNGamesStream(numGames);

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000); // /1000 converts from ms to s

        System.out.println("Win rate: " + winRate);
    }
    
    public static void demoGARandom(){
        GATuner tester = new GATuner();
        int popSize = 2;
        int numOfEvaluators = 6; 
        ParamList[] params = tester.createRandomIndividuals(popSize, numOfEvaluators);
        
        double[] fitness = tester.calcFitnessLoop(params, 6, GATuner.SELF_KNOCK_ON_GIN_KNOCK_DECIDER, GATuner.OPP_IS_SIMPLE_PLAYER, GATuner.SELF_IS_INDEX_PLAYER, 100, GATuner.SELF_MELD_ONLY_DRAW_DECIDER, -1, -1);

        System.out.println("individual number 0: " + params[0] + " fitness: " + fitness[0]);
        System.out.println("individual number 1: " + params[1] + " fitness: " + fitness[1]);
        
        System.out.println("All fitness: " + Arrays.toString(fitness));
        
        double sumFitness = 0;
        int indexOfBestFitness = 0;
        
        for(int i = 0; i < popSize; i++){
            if(fitness[i] > fitness[indexOfBestFitness]){
                indexOfBestFitness = i;
            }
            sumFitness += fitness[i];
        }
        System.out.println("Average fitness: " + sumFitness / popSize);
        System.out.println("The best fitness: " + fitness[indexOfBestFitness]);
        System.out.println("The best param: " + params[indexOfBestFitness]);
    
    }

    public static void demoRandomIndexVsKnockOnGin(){
        ParamList params = ParamList.getRandomParamList(6);
        LinearDeadwoodPenaltyHandEvaluator linearHe = new LinearDeadwoodPenaltyHandEvaluator(params);
        OppCardsKnownDeadwoodPenaltyHandEvaluator oppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params);
        IndexEnsembleHandEvalPlayer p0 = new IndexEnsembleHandEvalPlayer(params, new MeldabilityHandEvaluator(params), 
                                                                                 new DeadwoodHandEvaluator(), 
                                                                                 new AceTwoBonusHandEvaluator(), 
                                                                                 new ConvHandEvaluator(params), 
                                                                                 //new MultiOppHandMeldabilityEvaluator(params),
                                                                                 linearHe,
                                                                                 oppCardsHe);
        linearHe.setEnsemblePlayer(p0);
        oppCardsHe.setEnsemblePlayer(p0);

        // p0.setKnockDecider(new OneStageKnockDecider(params));
        p0.setKnockDecider(new TwoStageKnockDecider(params));
        p0.setDrawDecider(new DeadwoodDrawDecider());

        EnsembleHandEvalPlayer p1 = new EnsembleHandEvalPlayer(new ParamList(new double[]{1}), new DeadwoodHandEvaluator());
        p1.setKnockDecider(new KnockOnGinKnockDecider());
        p1.setDrawDecider(new DeadwoodDrawDecider());
        
        TestingGame game = new TestingGame(p0, p1);
        int p0Win = 0;
        int numGame= 2;
        for(int i = 0; i < numGame; i++){
            if(game.play() == 0)
                p0Win++;
        }
        System.out.println("Num index player win: " + p0Win);
        System.out.println("Win rate: " + (double)p0Win / numGame);
        System.out.println("Paramlist: " + params);
    }

    public static void main(String[] args) {
        // set TESTING to true
       // testChoose10From11();
        //makeAllEvaluatorsEnsemble();

        // set TESTING to false
        // demoAllEvaluatorsEnsemble();
        //testLikeHOSHandEvalPlayerLoop();
        //testLikeHOSHandEvalPlayerStream();

        // testAssertions();

    //    demoGARandom();
        demoRandomIndexVsKnockOnGin();
    }

}