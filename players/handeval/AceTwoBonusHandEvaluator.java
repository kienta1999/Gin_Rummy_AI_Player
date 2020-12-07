package players.handeval;

import ginrummy.Card;
import players.StateTracker;
import players.ParamList;
import java.util.ArrayList;

public class AceTwoBonusHandEvaluator implements HandEvaluator {
    
    @Override
    public double evalHand(ArrayList<Card> cards, StateTracker myTracker, Card card){
        double evalScore = 0;
        for(Card card0: cards){
            if(card0.getRank() == 0 || card0.getRank() == 1)
                evalScore+=1;
        }
        return (evalScore/8.0);
    }

    @Override
    public void setParamList(ParamList params) {
    }

    @Override
    public boolean hasDifferentParamList(ParamList otherParams) {
        return false;
    }

    public static void evalHandTest(){
        ParamList param = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        StateTracker tracker = new StateTracker(param);
        tracker.setToHardcodedStateTracker1();
        AceTwoBonusHandEvaluator tester = new AceTwoBonusHandEvaluator();
        ArrayList<Card> selfHand = tracker.getSelfHandForHardcodedStateTracker1(); // "2S", "3S", "4S", "5S", "6S", "9C", "9H", "9S", "4C", "AD"
        System.out.println("selfHand: " + selfHand);
        double evalScore = tester.evalHand(selfHand, tracker, null);
        System.out.println("The evalScore is: " + evalScore);
    }

    public static void main(String[] args){
        evalHandTest();
    }
}