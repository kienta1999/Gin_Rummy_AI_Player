package players.handeval;

import java.util.ArrayList;

import ginrummy.Card;
import players.ParamList;
import players.StateTracker;
import players.SimpleFakeGinRummyPlayer;
import games.TestingGame;

public class LinearDeadwoodPenaltyHandEvaluator implements HandEvaluator{

    private DeadwoodHandEvaluator deadwoodHandEval;

    private ParamList params;

    private EnsembleHandEvalPlayer ensemblePlayer;
    private int indexInEnsemble;
    private int turnNumberWhenWeightsUpdated;

    private static boolean TESTING = false;

    public LinearDeadwoodPenaltyHandEvaluator(ParamList params){
        this.deadwoodHandEval = new DeadwoodHandEvaluator();
        this.params = params;
        this.turnNumberWhenWeightsUpdated = -1;
    }

    @Override
    public void setParamList(ParamList params){
        this.params = params;
    }

    @Override
    public boolean hasDifferentParamList(ParamList otherParams) {
        return this.params != otherParams;
    }

    @Override
    public double evalHand(ArrayList<Card> cards, StateTracker myTracker, Card excludedCard){
        int numDrawnSelf = myTracker.getNumDrawnSelf();
        double deadwoodHandEvalScore = deadwoodHandEval.evalHand(cards, myTracker, excludedCard);

        if(this.turnNumberWhenWeightsUpdated != numDrawnSelf) {
            double newWeight = params.getEnsembleWeight(indexInEnsemble) + params.get(ParamList.LD_PENALTY_SLOPE) * Math.pow(numDrawnSelf - 1, params.get(ParamList.LD_PENALTY_EXPONENT));
            ensemblePlayer.setWeight(indexInEnsemble, newWeight);
            this.turnNumberWhenWeightsUpdated = numDrawnSelf;
            if (TESTING) System.out.println("numDrawnSelf: " + numDrawnSelf + ", slope: " + params.get(ParamList.LD_PENALTY_SLOPE) + ", exponent: " + params.get(ParamList.LD_PENALTY_EXPONENT) + ", new weight: " + newWeight);
        }

        return deadwoodHandEvalScore;
    }      

    public void setEnsemblePlayer(EnsembleHandEvalPlayer ensemblePlayer){
        this.ensemblePlayer = ensemblePlayer;
        this.indexInEnsemble = ensemblePlayer.getHandEvaluatorIndex(this);
        
        if (TESTING) System.out.println("IndexInEnsemble: " + indexInEnsemble + ", startingEnsembleWeight: " + params.getEnsembleWeight(indexInEnsemble));
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
        params.set(ParamList.CH_SAMERANK, 1);
        params.set(ParamList.CH_ONEAWAY, 1);
        params.set(ParamList.CH_TWOAWAY, 1);
        params.set(ParamList.LD_PENALTY_SLOPE, 0.01);
        params.set(ParamList.LD_PENALTY_EXPONENT, 1.0);
        params.enforceRestrictions();

        LinearDeadwoodPenaltyHandEvaluator he = new LinearDeadwoodPenaltyHandEvaluator(params);
        // p = new IndexEnsembleHandEvalPlayer(params, ......);
        p = new EnsembleHandEvalPlayer(params, he, new ConvHandEvaluator(params), new MeldabilityHandEvaluator(params));
        he.setEnsemblePlayer(p);

        p.getStateTracker().setToHardcodedStateTracker2();
        hand = p.getStateTracker().getSelfHandForHardcodedStateTracker2();
        
        hand.add(Card.strCardMap.get("7S")); // adds on to the run meld
        // System.out.println("**********How to update the StateTracker from here, to handle this card addition?");

        double slope = params.get(ParamList.LD_PENALTY_SLOPE);
        System.out.println("Slope: " + slope);
        double exponent = params.get(ParamList.LD_PENALTY_EXPONENT);
        System.out.println("Exponent: " + exponent);
        int numDrawnSelf = p.getStateTracker().getNumDrawnSelf();
        System.out.println("NumDrawnSelf-1: " + (numDrawnSelf - 1));
        double paramsEnsembleWeight = params.getEnsembleWeight(0);
        double newWeight = paramsEnsembleWeight + slope * Math.pow(numDrawnSelf-1, exponent);
        System.out.println("New Weight = startinEnsembleWeight(" + paramsEnsembleWeight + ") + slope(" + slope +") * numDrawnSelf-1(" + (numDrawnSelf-1) + ") ^ exponent(" + exponent + ") = " + newWeight);
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
        params2.set(ParamList.CH_SAMERANK, 1);
        params2.set(ParamList.CH_ONEAWAY, 1);
        params2.set(ParamList.CH_TWOAWAY, 1);
        params2.set(ParamList.LD_PENALTY_SLOPE, 0.01);
        params2.set(ParamList.LD_PENALTY_EXPONENT, 1.0);
        params2.enforceRestrictions();

        LinearDeadwoodPenaltyHandEvaluator he2 = new LinearDeadwoodPenaltyHandEvaluator(params2);
        // p = new IndexEnsembleHandEvalPlayer(params2, ......);
        p = new EnsembleHandEvalPlayer(params2, he2, new ConvHandEvaluator(params2), new MeldabilityHandEvaluator(params2));
        he2.setEnsemblePlayer(p);

        p.getStateTracker().setToHardcodedStateTracker1();
        hand = p.getStateTracker().getSelfHandForHardcodedStateTracker1();
        
        hand.add(Card.strCardMap.get("7S")); // adds on to the run meld
        // System.out.println("**********How to update the StateTracker from here, to handle this card addition?");

        slope = params2.get(ParamList.LD_PENALTY_SLOPE);
        System.out.println("Slope: " + slope);
        exponent = params2.get(ParamList.LD_PENALTY_EXPONENT);
        System.out.println("Exponent: " + exponent);
        numDrawnSelf = p.getStateTracker().getNumDrawnSelf();
        System.out.println("NumDrawnSelf-1: " + (numDrawnSelf - 1));
        paramsEnsembleWeight = params2.getEnsembleWeight(0);
        newWeight = paramsEnsembleWeight + slope * Math.pow(numDrawnSelf-1, exponent);
        System.out.println("New Weight = startinEnsembleWeight(" + paramsEnsembleWeight + ") + slope(" + slope +") * numDrawnSelf-1(" + (numDrawnSelf-1) + ") ^ exponent(" + exponent + ") = " + newWeight);
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