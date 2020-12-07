package players.handeval;

import java.util.ArrayList;

import ginrummy.Card;
import ginrummy.GinRummyUtil;
import players.ParamList;
import players.StateTracker;

public class TwoStageKnockDecider implements KnockDecider{

    protected ParamList params;

    public TwoStageKnockDecider(ParamList params){
        this.params = params;
    }

    @Override
    public boolean hasDifferentParamList(ParamList otherParams) {
        return this.params != otherParams;
    }
    public void setParamList(ParamList newParams){
        params = newParams;
    }
    /**
     * Returns final melds, or null if the player should not knock.
     */
    public ArrayList<ArrayList<Card>> shouldKnock(ArrayList<Card> hand, StateTracker myTracker){
        int numDrawnSelf = myTracker.getNumDrawnSelf();
        ArrayList<ArrayList<ArrayList<Card>>> selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(hand); //random.nextInt(selfBestMeldSets.size()
        ArrayList<ArrayList<Card>> selfMeldSet = selfBestMeldSets.isEmpty() ? new ArrayList<ArrayList<Card>>() : selfBestMeldSets.get(0);
        int deadwood = GinRummyUtil.getDeadwoodPoints(selfMeldSet, hand); //getting self's deadwood to calculate the score when the self knocks
        if(numDrawnSelf < params.get(ParamList.TS_KNOCK_MIDDLE)){
            if(deadwood <= params.get(ParamList.TS_KNOCK_THRESHOLD_EARLY))
                return selfMeldSet;
        }
        else{
            if(deadwood <= params.get(ParamList.TS_KNOCK_THRESHOLD_LATE))
                return selfMeldSet;
        }
        return null;
    }

    public static void test() {
        System.out.println("=============== Test ===============");
        ParamList params = new ParamList(new double[] {});
        StateTracker stateTracker = new StateTracker(params);
        stateTracker.setToHardcodedStateTracker1();

        KnockDecider ts = new TwoStageKnockDecider(params);

        ArrayList<Card> selfHand = stateTracker.getSelfHandForHardcodedStateTracker1();
        ArrayList<ArrayList<ArrayList<Card>>> selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(selfHand);
        ArrayList<ArrayList<Card>> selfMeldSet = selfBestMeldSets.isEmpty() ? new ArrayList<ArrayList<Card>>() : selfBestMeldSets.get(0);
        int deadwood = GinRummyUtil.getDeadwoodPoints(selfMeldSet, selfHand);
        System.out.println("Self's deadwood: " + deadwood);
        int numDrawnSelf = stateTracker.getNumDrawnSelf();
        System.out.println("NumDrawnSelf: " + numDrawnSelf);

        System.out.println("---------- Case 1 ----------");
        params.set(ParamList.TS_KNOCK_THRESHOLD_EARLY, 10);
        params.set(ParamList.TS_KNOCK_MIDDLE, 5);
        params.set(ParamList.TS_KNOCK_THRESHOLD_LATE, 3);
        System.out.println("threshold early is 10, threshold late is 3, and the boundary between early and late is 5");
        ArrayList<ArrayList<Card>> knockResults = ts.shouldKnock(selfHand, stateTracker);
        System.out.println("shouldKnock: " + knockResults);
        
        System.out.println("---------- Case 2 ----------");
        params.set(ParamList.TS_KNOCK_THRESHOLD_EARLY, 10);
        params.set(ParamList.TS_KNOCK_MIDDLE, 5);
        params.set(ParamList.TS_KNOCK_THRESHOLD_LATE, 5);
        System.out.println("threshold early is 10, threshold late is 5, and the boundary between early and late is 5");
        knockResults = ts.shouldKnock(selfHand, stateTracker);
        System.out.println("shouldKnock: " + knockResults);

        System.out.println("---------- Case 3 ----------");
        params.set(ParamList.TS_KNOCK_THRESHOLD_EARLY, 10);
        params.set(ParamList.TS_KNOCK_MIDDLE, 10);
        params.set(ParamList.TS_KNOCK_THRESHOLD_LATE, 3);
        System.out.println("threshold early is 10, threshold late is 3, and the boundary between early and late is 10");
        knockResults = ts.shouldKnock(selfHand, stateTracker);
        System.out.println("shouldKnock: " + knockResults);

        System.out.println("---------- Case 4 ----------");
        params.set(ParamList.TS_KNOCK_THRESHOLD_EARLY, 3);
        params.set(ParamList.TS_KNOCK_MIDDLE, 10);
        params.set(ParamList.TS_KNOCK_THRESHOLD_LATE, 1);
        System.out.println("threshold early is 3, threshold late is 1, and the boundary between early and late is 10");
        knockResults = ts.shouldKnock(selfHand, stateTracker);
        System.out.println("shouldKnock: " + knockResults);
    }

    public static void main(String[] args){
        test();
    }
    
}