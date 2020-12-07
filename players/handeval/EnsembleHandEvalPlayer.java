package players.handeval;

import ginrummy.Card;
import players.TunableStateTracker;
import java.util.ArrayList;
import players.ParamList;
import util.OurUtil;

/**
 * This player implements choose10From11Cards using a weighted sum of 
 * ensemble members' hand eval scores.
 */
public class EnsembleHandEvalPlayer extends AbstractHandEvalPlayer {

    private static boolean EVALTESTING = false;
    private static boolean TESTING = false;

    protected ArrayList<HEWeightPair> handEvaluators;
    // protected ArrayList<Double> weights;

    protected static class HEWeightPair {
        public HandEvaluator he;
        public double weight;
        public HEWeightPair(HandEvaluator he, double weight) {
            this.he = he;
            this.weight = weight;
        }
    }

    public EnsembleHandEvalPlayer(ParamList params, HandEvaluator ... hes) {
        super(params);

        handEvaluators = new ArrayList<HEWeightPair>();
        for(int i = 0; i < hes.length; i++) {
            handEvaluators.add(new HEWeightPair(hes[i], params.getEnsembleWeight(i)));
        }

        assertConditions(hes);
    }

    public EnsembleHandEvalPlayer(ParamList params, TunableStateTracker tunableStateTracker, HandEvaluator ... hes){
        this(params, hes);
        this.myStateTracker = tunableStateTracker;
    }

    /**
     * Call this from constructor, but maybe also, to be safe, for now, from choose10From11Cards. Might be great safety with trickiness of GA.
     * (Would need to call from IndexEnsemble's choose10From11Cards too, since overrides and doesn't call super.)
     */
    protected void assertConditions(HandEvaluator[] hes) {
        OurUtil.assertCondition(params.getNumEnsembleWeights() != 0, "Can't have an ensemble with 0 members.");

        OurUtil.assertCondition(params.getNumEnsembleWeights() == hes.length, "Number of weights and HandEvaluators must match.");

        double sum = 0;
        for(int i = 0; i < hes.length; i++) {
            sum += params.getEnsembleWeight(i);
        }
        OurUtil.assertCondition(sum != 0, "Ensemble weights must not be all 0 - need to use a ParamList constructor that passes an array of doubles.");

        for(int i = 0; i < hes.length; i++) {
            // OurUtil.assertCondition(handEvaluators.get(i).he.getParamList() == this.params, "Each ensemble member must have the same ParamList as the Ensemble player, but ensemble member " + i + " does not.");
            // but getParamList doesn't exist! Not sure we'd want to break encapsulation anyway. Fearful of misuse.

            OurUtil.assertCondition(!handEvaluators.get(i).he.hasDifferentParamList(this.params), "Each ensemble member must have the same ParamList as the ensemble player, but ensemble member " + i + " does not.");
        }
        
        if (knockDecider != null)
            OurUtil.assertCondition(!knockDecider.hasDifferentParamList(this.params), "The knock decider must have the same ParamList as the ensemble player.");

        OurUtil.assertCondition(!myStateTracker.hasDifferentParamList(this.params), "The state tracker must have the same ParamList as the ensemble player.");
    }
    /*
    public void addHandEvaluator(HandEvaluator he, double weight) {
        handEvaluators.add(new HEWeightPair(he, weight));
    }
    */

    @Override
    public void setParamList(ParamList newParams) {
        super.setParamList(newParams); // updates this.params

        // Update ParamList for each ensemble member
        // Also save the ensemble weights locally, for use in calculations and to be updated throughout the game by some hand evaluators
        HandEvaluator[] hes = new HandEvaluator[getNumOfEvals()]; // for passing to assertConditions
        for(int i = 0; i < getNumOfEvals(); i++) {
            handEvaluators.get(i).he.setParamList(this.params);
            handEvaluators.get(i).weight = newParams.getEnsembleWeight(i);
            hes[i] = handEvaluators.get(i).he;
        }
        knockDecider.setParamList(newParams);
        assertConditions(hes);
    }

    public double getWeight(int handEvaluatorIndex) {
        return handEvaluators.get(handEvaluatorIndex).weight;
        // return params.getEnsembleWeight(handEvaluatorIndex);
    }

    public void setWeight(int handEvaluatorIndex, double newWeight) {
        handEvaluators.get(handEvaluatorIndex).weight = newWeight;
        normalizeWeights();
    }

    public void setAllWeights(double[] weights) {
        OurUtil.assertCondition(weights.length == handEvaluators.size(), "setAllWeights must be given enough weights for every hand evaluator.");
        for(int heID = 0; heID < weights.length; heID++) {
            handEvaluators.get(heID).weight = weights[heID];
        }
        normalizeWeights();
    }

    public int getNumOfEvals(){
        return this.handEvaluators.size();
    }

    public int getHandEvaluatorIndex(HandEvaluator he){
        int index = -1;
        for(int i = 0; i < handEvaluators.size(); i++){
            if(handEvaluators.get(i).he == he)
                index = i;
        }
        return index;
    }

    public static void setTesting(boolean testing){
        TESTING = testing;
    }

    public static void setEvalTesting(boolean testing){
        EVALTESTING = testing;
    }

    // VERIFIED 6/16
    private void normalizeWeights() {
        double sum = 0;
        for(int weightID = 0; weightID < handEvaluators.size(); weightID++) {
            sum += handEvaluators.get(weightID).weight;
        }

        for(int weightID = 0; weightID < handEvaluators.size(); weightID++)
            handEvaluators.get(weightID).weight /= sum;
    }

	@Override
	public void startGame(int playerNum, int startingPlayerNum, Card[] cards) {
        super.startGame(playerNum, startingPlayerNum, cards);

        // Reset the local ensemble weights to the original ones saved in the ParamList
        // (in case they were adjusted by some kind of penalty he)
        HandEvaluator[] hes = new HandEvaluator[getNumOfEvals()]; // for passing to assertConditions
        for(int heID = 0; heID < handEvaluators.size(); heID++) {
            handEvaluators.get(heID).weight = params.getEnsembleWeight(heID);
            hes[heID] = handEvaluators.get(heID).he;
        }
        assertConditions(hes);
    }
    
    // VERIFIED 6/17
    /**
     * Same as SingleHandEvalPlayer, except uses getEnsembleEvaluation to get a weighted
     * sum of the individual evaluations.
     */
    @Override
    protected Card choose10From11Cards(ArrayList<Card> handOf11) {
        if (TESTING) System.out.println("-------------------------------\nchoose10From11Cards begins");

        double[] allEvalValues = new double[11];
        for(int i = 0; i < allEvalValues.length; i++){
            ArrayList<Card> handOf10 = new ArrayList<Card>(handOf11);
            Card cardToDrop = handOf11.get(i);
            handOf10.remove(cardToDrop);
            allEvalValues[i] = getEnsembleEvaluation(handOf10, cardToDrop);
            if (TESTING) System.out.println("---------------\nhand of 10: " + handOf10 + " by removing: " + cardToDrop + "\neval: " + allEvalValues[i] + "\n---------------");
        }

        double maxEval = allEvalValues[0];
        int indexOfCardDiscarded = 0;
        int loopLimit = drewFaceUpCard ? 10 : 11; // if drew FaceUpCard, then don't consider dropping it
        for(int j = 1; j < loopLimit; j++) {
            if(allEvalValues[j]>maxEval){
                maxEval = allEvalValues[j];
                indexOfCardDiscarded = j;
            }       
        }

        if (TESTING) System.out.println("maxEval: " + maxEval + ", indexOfCardDiscarded: " + indexOfCardDiscarded);
            
        return handOf11.get(indexOfCardDiscarded);
    }

    // VERIFIED 6/17
    /**
     * Computes a weighted sum of the hand evaluations from each HandEvaluator.
     */
    protected double getEnsembleEvaluation(ArrayList<Card> handOf10, Card excludedCard) {
        if (EVALTESTING) System.out.println("----- getEnsembleEvaluation\nhand: " + handOf10);
        double sum = 0;
        for(int heID = 0; heID < handEvaluators.size(); heID++) {
            double evalScore = handEvaluators.get(heID).he.evalHand(handOf10, this.myStateTracker, excludedCard);
            // params.enforceRestrictions();
            if (EVALTESTING) System.out.println("Evaluator " + heID + " with weight " + getWeight(heID) + " evals: " + evalScore);
            sum += getWeight(heID) * evalScore;
        }

        return sum;
    }

    public static void testChoose10From11() {
        System.out.println("=============== testChoose10From11 ===============");
        double[] ensembleWeights = new double[] {3, 2};
        ParamList params = new ParamList(ensembleWeights);
        params.set(ParamList.CH_SAMERANK, 1.5);
        params.set(ParamList.CH_ONEAWAY, 2);
        params.set(ParamList.CH_TWOAWAY, 1);
        params.enforceRestrictions();
        EnsembleHandEvalPlayer p = new EnsembleHandEvalPlayer(params, new ConvHandEvaluator(params), new MeldabilityHandEvaluator(params));

        p.getStateTracker().setToHardcodedStateTracker1();
        ArrayList<Card> hand = p.getStateTracker().getSelfHandForHardcodedStateTracker1();
        System.out.println("hand: " + hand.toString());
        // don't need to set cards for the player though

        hand.add(Card.strCardMap.get("7S")); // adds on to the run meld
        System.out.println("**********How to update the StateTracker from here, to handle this card addition?");

        Card result = p.choose10From11Cards(hand);
        System.out.println("choose10From11Cards returned: " + result);
    }

    public static void testEnsembleEvaluation() {
        System.out.println("=============== testEnsembleEvaluation ===============");
        double[] ensembleWeights = new double[] {3, 2};
        ParamList params = new ParamList(ensembleWeights);
        params.set(ParamList.CH_SAMERANK, 1.5);
        params.set(ParamList.CH_ONEAWAY, 2);
        params.set(ParamList.CH_TWOAWAY, 1);
        params.enforceRestrictions();
        EnsembleHandEvalPlayer p = new EnsembleHandEvalPlayer(params, new ConvHandEvaluator(params), new MeldabilityHandEvaluator(params));

        p.getStateTracker().setToHardcodedStateTracker1();
        ArrayList<Card> hand = p.getStateTracker().getSelfHandForHardcodedStateTracker1();
        // don't need to set cards for the player though

        System.out.println("Ensemble evaluation: " + p.getEnsembleEvaluation(hand, null));

        /*
        =============== testEnsembleEvaluation ===============
        ----- getEnsembleEvaluation
        hand: [2S, 3S, 4S, 5S, 6S, 9C, 9H, 9S, 4C, AD]
        Evaluator 0 with weight 0.16666666666666666 evals: 22.0
        Evaluator 1 with weight 0.5 evals: 34.0
        Evaluator 2 with weight 0.3333333333333333 evals: 421.35880725000004          // this includes the new meldability (obtainability) calculations
        Ensemble evaluation: 161.11960241666665
        */
    }
    public static void testAssertConditions(){
        double[] ensembleWeights = new double[] {0.15, 0.70, 0.10, 0.10, /*0.10,*/ 0.10, 0.10};
        ParamList params = new ParamList(ensembleWeights);
        params.set(ParamList.TS_KNOCK_THRESHOLD_EARLY, 10.0); // default is 9.0
        params.set(ParamList.TS_KNOCK_THRESHOLD_LATE, 0.0); // default is 6.0
        params.set(ParamList.TS_KNOCK_MIDDLE, 4.0); // default is 6.0
        params.enforceRestrictions();
        LinearDeadwoodPenaltyHandEvaluator linearHe = new LinearDeadwoodPenaltyHandEvaluator(params);
        OppCardsKnownDeadwoodPenaltyHandEvaluator oppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params);
        EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(params, new MeldabilityHandEvaluator(params), 
                                                                                 new DeadwoodHandEvaluator(), 
                                                                                 new AceTwoBonusHandEvaluator(), 
                                                                                 new ConvHandEvaluator(params), 
                                                                                 //new MultiOppHandMeldabilityEvaluator(params),
                                                                                 linearHe,
                                                                                 oppCardsHe);
        linearHe.setEnsemblePlayer(p0);
        oppCardsHe.setEnsemblePlayer(p0);

        // ParamList.getRandomParamList(ensembleWeights.length)
        p0.setKnockDecider(new TwoStageKnockDecider(params));
    }
    public static void main(String[] args) {
        System.out.println("New version 3!");
    //    testEnsembleEvaluation(); // set EVALTESTING to true
       // testChoose10From11();     // probably set EVALTESTING to false and TESTING to true
       testAssertConditions();
    }

}