package players.handeval;


import java.util.ArrayList;

import ginrummy.Card;
import ginrummy.GinRummyUtil;
import players.StateTracker;
import util.OurUtil;
import players.ParamList;

public class KnockOnGinKnockDecider implements KnockDecider{
    
    public ArrayList<ArrayList<Card>> shouldKnock(ArrayList<Card> hand, StateTracker myTracker){
        ArrayList<ArrayList<ArrayList<Card>>> selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(hand); //random.nextInt(selfBestMeldSets.size()
        ArrayList<ArrayList<Card>> selfMeldSet = selfBestMeldSets.isEmpty() ? new ArrayList<ArrayList<Card>>() : selfBestMeldSets.get(0);
        int deadwood = GinRummyUtil.getDeadwoodPoints(selfMeldSet, hand); //getting self's deadwood to calculate the score when the self knocks
        if(deadwood == 0)
            return selfMeldSet;
        else
            return null;        
    }
    
    public boolean hasDifferentParamList(ParamList otherParams) {
        return false;
    }
    public void setParamList(ParamList newParams){
        
    }
    public static void test(){
        System.out.println("=============== Test ===============");
        StateTracker stateTracker = null;
        KnockDecider ts = new KnockOnGinKnockDecider();
        
        System.out.println("---------- Case 1 ----------");
        System.out.println("Gin");
        ArrayList <Card> selfHand = OurUtil.makeHand(new String[] {"2C", "3C", "4C", "6S", "6D", "6C", "6H", "JH", "QH", "KH"});     
        ArrayList<ArrayList<ArrayList<Card>>> selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(selfHand);
        ArrayList<ArrayList<Card>> selfMeldSet = selfBestMeldSets.isEmpty() ? new ArrayList<ArrayList<Card>>() : selfBestMeldSets.get(0);
        int deadwood = GinRummyUtil.getDeadwoodPoints(selfMeldSet, selfHand);
        System.out.println("Self's deadwood: " + deadwood);
        ArrayList<ArrayList<Card>> knockResults = ts.shouldKnock(selfHand, stateTracker);
        System.out.println("shouldKnock: " + knockResults);

        System.out.println("---------- Case 2 ----------");
        System.out.println("Not Gin");
        selfHand = OurUtil.makeHand(new String[] {"2C", "2S", "AC", "6S", "6D", "6C", "6H", "JH", "QH", "KH"});    
        selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(selfHand);
        selfMeldSet = selfBestMeldSets.isEmpty() ? new ArrayList<ArrayList<Card>>() : selfBestMeldSets.get(0);
        deadwood = GinRummyUtil.getDeadwoodPoints(selfMeldSet, selfHand);
        System.out.println("Self's deadwood: " + deadwood);
        knockResults = ts.shouldKnock(selfHand, stateTracker);
        System.out.println("shouldKnock: " + knockResults);

        

    }
    public static void main(String[] args){
        test();
    }
}