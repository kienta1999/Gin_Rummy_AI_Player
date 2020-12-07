package players.handeval;

import java.util.ArrayList;

import games.TestingGame;
import ginrummy.Card;
import players.ParamList;
import players.SimpleFakeGinRummyPlayer;
import players.StateTracker;

public class OppCardsKnownDeadwoodPenaltyHandEvaluator implements HandEvaluator{

    private DeadwoodHandEvaluator deadwoodHandEvaluator = new DeadwoodHandEvaluator();

    private ParamList params;

    private EnsembleHandEvalPlayer ensemblePlayer;
    private int indexInEnsemble;
    private int numCardsWhenWeightsUpdated;

    private static boolean TESTING = false;

    public OppCardsKnownDeadwoodPenaltyHandEvaluator(ParamList params){
        this.params = params;
        this.numCardsWhenWeightsUpdated = 0;
    }

    @Override
    public double evalHand(ArrayList<Card> cards, StateTracker myTracker, Card excludedCard){
        int numOppCardsKnown = myTracker.getNumOppCardsKnown();
        double deadwoodHandEvalScore = deadwoodHandEvaluator.evalHand(cards, myTracker, null);

        if(this.numCardsWhenWeightsUpdated != numOppCardsKnown){
            double newWeight = params.getEnsembleWeight(indexInEnsemble) + params.get(ParamList.OD_PENALTY_SLOPE) * Math.pow(numOppCardsKnown, params.get(ParamList.OD_PENALTY_EXPONENT));
            ensemblePlayer.setWeight(indexInEnsemble, newWeight);
            this.numCardsWhenWeightsUpdated = numOppCardsKnown;
            if(TESTING) System.out.println("deadwood score: " + deadwoodHandEvalScore + ", numOppCardsKnown: " + numOppCardsKnown + ", slope: " + params.get(ParamList.OD_PENALTY_SLOPE) + ", exponent: " + params.get(ParamList.OD_PENALTY_EXPONENT) + ", new weight (unnormalized): " + newWeight);
        }

        return deadwoodHandEvalScore;
    }

    public void setEnsemblePlayer(EnsembleHandEvalPlayer ensemblePlayer){
        this.ensemblePlayer = ensemblePlayer; 
        this.indexInEnsemble = ensemblePlayer.getHandEvaluatorIndex(this);

        if(TESTING) System.out.println("IndexInEnsemble: " + indexInEnsemble + ", startingEnsembleWeight: " + params.getEnsembleWeight(indexInEnsemble));
    }

    @Override
    public void setParamList(ParamList params){
        this.params = params;
    }

    @Override
    public boolean hasDifferentParamList(ParamList otherParams) {
        return this.params != otherParams;
    }
    
    public static void setTesting(boolean testing){
        TESTING = testing;
    }

    public static void testChoose10From11(){
        EnsembleHandEvalPlayer p;
        ArrayList<Card> hand;
        Card result;

        setTesting(true);
        EnsembleHandEvalPlayer.setTesting(true);
        EnsembleHandEvalPlayer.setEvalTesting(true);
        IndexEnsembleHandEvalPlayer.setTesting(true);

        System.out.println("=============== testChoose10From11 trial1===============");

        double[] ensembleWeights = new double[] {7, 1, 2};
        ParamList params = new ParamList(ensembleWeights);

        params.set(ParamList.OD_PENALTY_SLOPE, 0.01);
        params.set(ParamList.OD_PENALTY_EXPONENT, 2.0);
        params.set(ParamList.CH_SAMERANK, 1);
        params.set(ParamList.CH_ONEAWAY, 1);
        params.set(ParamList.CH_TWOAWAY, 1);
        params.enforceRestrictions();

        OppCardsKnownDeadwoodPenaltyHandEvaluator he = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params);
        // p = new IndexEnsembleHandEvalPlayer(params, ...);
        p = new EnsembleHandEvalPlayer(params, he, new ConvHandEvaluator(params), new MeldabilityHandEvaluator(params));

        he.setEnsemblePlayer(p);

        p.getStateTracker().setToHardcodedStateTracker1();
        hand = p.getStateTracker().getSelfHandForHardcodedStateTracker1();
        
        hand.add(Card.strCardMap.get("7S")); // adds on to the run meld
        // System.out.println("**********How to update the StateTracker from here, to handle this card addition?");

        double slope = params.get(ParamList.OD_PENALTY_SLOPE);
        System.out.println("Slope: " + slope);
        double exponent = params.get(ParamList.OD_PENALTY_EXPONENT);
        System.out.println("Exponent: " + exponent);
        int numOppCardsKnown = p.getStateTracker().getNumOppCardsKnown();
        System.out.println("OppCardsKnown: " + numOppCardsKnown);
        double startingEnsembleWeight = params.getEnsembleWeight(0);
        double newWeight = startingEnsembleWeight + slope * Math.pow(numOppCardsKnown, exponent);
        System.out.println("New Weight = startinEnsembleWeight(" + startingEnsembleWeight + ") + slope(" + slope +") * numOppCardsKnown(" + numOppCardsKnown + ") ^ exponent(" + exponent + ") = " + newWeight);
        double weight1 = params.getEnsembleWeight(1);
        double weight2 = params.getEnsembleWeight(2);
        double totalWeight = newWeight + weight1 + weight2;
        System.out.println("new weight0: " + newWeight + ", weight1: " + weight1 + ", weight2: " + weight2 + ", Total Weight: " + totalWeight);
        System.out.println("----- Normalize weights by dividing each weight by totalWeight ------");
        System.out.println("normalized weight0: " + newWeight/totalWeight + ", normalized weight1: " + weight1/totalWeight + ", normalized weight2: " + weight2/totalWeight);

        result = p.choose10From11Cards(hand);

        System.out.println();
        System.out.println("=============== testChoose10From11 trial2===============");

        double[] ensembleWeights2 = new double[] {0.980, 0.015, 0.005};
        ParamList params2 = new ParamList(ensembleWeights2);

        params2.set(ParamList.OD_PENALTY_SLOPE, 0.01);
        params2.set(ParamList.OD_PENALTY_EXPONENT, 2.0);
        params2.set(ParamList.CH_SAMERANK, 1);
        params2.set(ParamList.CH_ONEAWAY, 1);
        params2.set(ParamList.CH_TWOAWAY, 1);
        params2.enforceRestrictions();

        OppCardsKnownDeadwoodPenaltyHandEvaluator he2 = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params2);
        // p = new IndexEnsembleHandEvalPlayer(params, ...);
        p = new EnsembleHandEvalPlayer(params2, he2, new ConvHandEvaluator(params2), new MeldabilityHandEvaluator(params2));

        he2.setEnsemblePlayer(p);

        p.getStateTracker().setToHardcodedStateTracker3();
        hand = p.getStateTracker().getSelfHandForHardcodedStateTracker3();
        
        hand.add(Card.strCardMap.get("7S")); // adds on to the run meld
        // System.out.println("**********How to update the StateTracker from here, to handle this card addition?");

        slope = params2.get(ParamList.OD_PENALTY_SLOPE);
        System.out.println("Slope: " + slope);
        exponent = params2.get(ParamList.OD_PENALTY_EXPONENT);
        System.out.println("Exponent: " + exponent);
        numOppCardsKnown = p.getStateTracker().getNumOppCardsKnown();
        System.out.println("numOppCardsKnown: " + numOppCardsKnown);
        startingEnsembleWeight = params2.getEnsembleWeight(0);
        newWeight = startingEnsembleWeight + slope * Math.pow(numOppCardsKnown, exponent);
        System.out.println("New Weight = startinEnsembleWeight(" + startingEnsembleWeight + ") + slope(" + slope +") * numOppCardsKnown(" + numOppCardsKnown + ") ^ exponent(" + exponent + ") = " + newWeight);
        weight1 = params2.getEnsembleWeight(1);
        weight2 = params2.getEnsembleWeight(2);
        totalWeight = newWeight + weight1 + weight2;
        System.out.println("new weight0: " + newWeight + ", weight1: " + weight1 + ", weight2: " + weight2 + ", Total Weight: " + totalWeight);
        System.out.println("----- Normalize weights by dividing each weight by totalWeight ------");
        System.out.println("normalized weight0: " + newWeight/totalWeight + ", normalized weight1: " + weight1/totalWeight + ", normalized weight2: " + weight2/totalWeight);

        result = p.choose10From11Cards(hand);
        
        // System.out.println("choose10From11Cards returned: " + result);
        // System.out.println("4C by-hand calculation:  9*0.09433962264150944 +  9*0.18867924528301888 + 10*0.7169811320754716 = " + (9*0.09433962264150944 +  9*0.18867924528301888 + 10*0.7169811320754716));
        // System.out.println("AD by-hand calculation: 10*0.09433962264150944 + 10*0.18867924528301888 +  9*0.7169811320754716 = " + (10*0.09433962264150944 +  10*0.18867924528301888 +  9*0.7169811320754716));
    }

    public static void main(String[] args){
        testChoose10From11();
    }
}