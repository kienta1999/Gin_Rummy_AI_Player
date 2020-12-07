package players.handeval;

import java.util.ArrayList;

import ginrummy.Card;
import players.ParamList;
import players.StateTracker;
import util.OurUtil;

public class TwoStageDrawDecider implements DrawDecider {
    
    protected DeadwoodDrawDecider deadwoodDD;
    protected MeldOnlyDrawDecider meldOnlyDD;

    protected ParamList params;

    public TwoStageDrawDecider(ParamList params){
        this.params = params;
        deadwoodDD = new DeadwoodDrawDecider();
        meldOnlyDD = new MeldOnlyDrawDecider();
    }

    public boolean shouldDraw(ArrayList<Card> hand, Card faceUpCard, StateTracker myTracker){
        int numDrawnSelf = myTracker.getNumDrawnSelf();
        if(numDrawnSelf < params.get(ParamList.TS_DRAW_MIDDLE)){
            return meldOnlyDD.shouldDraw(hand, faceUpCard, myTracker);
        }
        else{
            return deadwoodDD.shouldDraw(hand, faceUpCard, myTracker);
        }
    }

    public boolean hasDifferentParamList(ParamList otherParams){
        return this.params != otherParams;
    }

    public void setParamList(ParamList newParams){
        params = newParams;
    }

    public static void testTwoStageDrawDecider(){
        ParamList defaultPL = new ParamList(new double[0]);
        TwoStageDrawDecider ts = new TwoStageDrawDecider(defaultPL);
        System.out.println("Default params for draw middle is 10");
        ArrayList<Card> hand = OurUtil.makeHand(new String[]{"AS", "2S", "3S", "4C", "5C", "6C", "7H", "7C", "TS", "KC"});
        Card faceUpCard = OurUtil.getCard(0, 0); // AC
        StateTracker myTracker = new StateTracker(defaultPL);

        System.out.println("Player's hand: " + hand);
        System.out.println("Face up card: " + faceUpCard);
        
        //Case 1: numDrawnSelf < TS_DRAW_MIDDLE(10) 
        System.out.println("======================Case 1: numDrawnSelf < 10 (default draw middle)===============");
        System.out.println("Meld Only Draw Decider using");
        myTracker.setNumDrawnSelf(8);
        System.out.println("numDrawnSelf: " + myTracker.getNumDrawnSelf());
        System.out.println("Should the player draw the card? " + ts.shouldDraw(hand, faceUpCard, myTracker));

        //Case 2: numDrawnSelf > TS_DRAW_MIDDLE(10) 
        System.out.println("======================Case 2: numDrawnSelf >= 10 (default draw middle)===============");
        System.out.println("Deadwood Draw Decider using");
        myTracker.setNumDrawnSelf(12);
        System.out.println("numDrawnSelf: " + myTracker.getNumDrawnSelf());
        System.out.println("Should the player draw the card? " + ts.shouldDraw(hand, faceUpCard, myTracker));

        
    }
    public static void main(String[] args){
        testTwoStageDrawDecider();
    }

}