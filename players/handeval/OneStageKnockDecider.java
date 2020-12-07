package players.handeval;

import java.util.ArrayList;

import ginrummy.Card;
import ginrummy.GinRummyUtil;
import players.ParamList;
import players.StateTracker;

public class OneStageKnockDecider implements KnockDecider{

    protected ParamList params;

    public OneStageKnockDecider(ParamList params){
        this.params = params;
    }

    @Override
    public boolean hasDifferentParamList(ParamList otherParams) {
        return this.params != otherParams;
    }
    
    public ArrayList<ArrayList<Card>> shouldKnock(ArrayList<Card> hand, StateTracker myTracker){
        ArrayList<ArrayList<ArrayList<Card>>> selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(hand); //random.nextInt(selfBestMeldSets.size()
        ArrayList<ArrayList<Card>> selfMeldSet = selfBestMeldSets.isEmpty() ? new ArrayList<ArrayList<Card>>() : selfBestMeldSets.get(0);
        int deadwood = GinRummyUtil.getDeadwoodPoints(selfMeldSet, hand); //getting self's deadwood to calculate the score when the self knocks

        if(deadwood <= params.get(ParamList.OS_KNOCK_THRESHOLD))
            return selfMeldSet;
        else
            return null;
    }
    public void setParamList(ParamList newParams){
        params = newParams;
    }
    public static void test(){
        System.out.println("=============== Test ===============");
        ParamList params = new ParamList(new double[] {});
        StateTracker stateTracker = new StateTracker(params);
        stateTracker.setToHardcodedStateTracker1();

        KnockDecider ts = new OneStageKnockDecider(params);

        ArrayList<Card> selfHand = stateTracker.getSelfHandForHardcodedStateTracker1();
        ArrayList<ArrayList<ArrayList<Card>>> selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(selfHand);
        ArrayList<ArrayList<Card>> selfMeldSet = selfBestMeldSets.isEmpty() ? new ArrayList<ArrayList<Card>>() : selfBestMeldSets.get(0);
        int deadwood = GinRummyUtil.getDeadwoodPoints(selfMeldSet, selfHand);
        System.out.println("Self's deadwood: " + deadwood);

        System.out.println("---------- Case 1 ----------");
        params.set(ParamList.OS_KNOCK_THRESHOLD, 10);
        System.out.println("threshold is 10");
        ArrayList<ArrayList<Card>> knockResults = ts.shouldKnock(selfHand, stateTracker);
        System.out.println("shouldKnock: " + knockResults);
        
        System.out.println("---------- Case 2 ----------");
        params.set(ParamList.OS_KNOCK_THRESHOLD, 4);
        System.out.println("threshold is 4");
        knockResults = ts.shouldKnock(selfHand, stateTracker);
        System.out.println("shouldKnock: " + knockResults);
    }

    public static void main(String[] args){
        test();
    }

    
}