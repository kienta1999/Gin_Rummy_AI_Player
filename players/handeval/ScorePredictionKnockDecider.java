package players.handeval;

import java.util.ArrayList;

import ginrummy.Card;
import ginrummy.GinRummyUtil;

import players.ParamList;
import players.StateTracker;
import players.ScorePrediction;

public class ScorePredictionKnockDecider implements KnockDecider {

    protected ParamList params;
    public static final boolean TESTING = false;
    private int threshold = 0;

    public ScorePredictionKnockDecider(ParamList params){
        this.params = params;
    }

    public ArrayList<ArrayList<Card>> shouldKnock(ArrayList<Card> hand, StateTracker myTracker){
        double score = ScorePrediction.multiOppHandScore(myTracker, hand, (int)params.get(ParamList.SP_NUM_OF_ADDITIONAL_CARDS) );
        if(TESTING){
            System.out.println("My state tracker: " + myTracker);
            System.out.println("My hand: " + hand);
            System.out.println("Predicted score: " + score);
        }
        if(score > threshold){
            ArrayList<ArrayList<Card>> selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(hand).get(0);
            if(TESTING){
                System.out.println("Self knocks. Best meld set: " + selfBestMeldSets);
                System.out.println("Self deadwood: " + GinRummyUtil.getDeadwoodPoints(selfBestMeldSets, hand));
            }
            return selfBestMeldSets;
        }   
        else{
            if (TESTING) System.out.println("Self doesnt knock");
            return null;
        }
            
    }

    public boolean hasDifferentParamList(ParamList otherParams){
        return this.params != otherParams;
    }
    public void setParamList(ParamList newParams){
        params = newParams;
    }
    public static void testSelfKnock() {
        System.out.println("=============== Test ===============");
        ParamList params = new ParamList(new double[] {});
        StateTracker stateTracker = new StateTracker(params);
        stateTracker.setToHardcodedStateTracker1();

        System.out.println("StateTracker: " + stateTracker);

        KnockDecider ts = new ScorePredictionKnockDecider(params);

        ArrayList<Card> selfHand = stateTracker.getSelfHandForHardcodedStateTracker1();
        
        // int deadwood = GinRummyUtil.getDeadwoodPoints(selfMeldSet, selfHand);

       // System.out.println("Self's meld: " + selfMeldSet);
       // System.out.println("Self's deadwood: " + deadwood);

        params.set(ParamList.SP_NUM_OF_ADDITIONAL_CARDS, 2);
       
        ArrayList<ArrayList<Card>> knockResults = ts.shouldKnock(selfHand, stateTracker);

        System.out.println("Knock result: " + knockResults);
    }

    public static void testSelfNotKnock() {
        System.out.println("=============== Test ===============");
        ParamList params = new ParamList(new double[] {});
        StateTracker stateTracker = new StateTracker(params);
        stateTracker.setToHardcodedStateTracker1();

        System.out.println("StateTracker: " + stateTracker);

        KnockDecider ts = new ScorePredictionKnockDecider(params);

        ArrayList<Card> selfHand = stateTracker.getSelfHandForHardcodedStateTracker2();

        params.set(ParamList.SP_NUM_OF_ADDITIONAL_CARDS, 2);
       
        ArrayList<ArrayList<Card>> knockResults = ts.shouldKnock(selfHand, stateTracker);
        System.out.println("Knock result: " + knockResults);
    }

    public static void main(String[] args){
        //set TESTING to true
        testSelfKnock();
        testSelfNotKnock();
    }
}
