package players.handeval;
import ginrummy.Card;
import java.util.ArrayList;

import games.TestingGame;
import players.StateTracker;
import players.MeldabilityCalculator;
import players.ParamList;
import util.OurUtil;

import players.SimpleFakeGinRummyPlayer;

public class MeldabilityHandEvaluator implements HandEvaluator {

    private MeldabilityCalculator meldCalc;
    public static double maxEvalHand;
    public static final double MAX_MELDABILITY_EVAL_HAND = 1000;
    boolean shouldNormalize;
 
    public MeldabilityHandEvaluator(ParamList params){
        meldCalc = new MeldabilityCalculator(params);
        maxEvalHand = 0;
        shouldNormalize = false;
    }
    
    @Override
    public double evalHand(ArrayList<Card> cards, StateTracker myTracker, Card excludedCard){  
        double handMeldability = meldCalc.selfHandMeldability(cards, myTracker);
        maxEvalHand = maxEvalHand < handMeldability ? handMeldability : maxEvalHand;
        if(shouldNormalize)
            return handMeldability / MAX_MELDABILITY_EVAL_HAND;
        else
            return handMeldability;
    }

    @Override
    public void setParamList(ParamList params){
        meldCalc.setParamList(params);
    }

    @Override
    public boolean hasDifferentParamList(ParamList otherParams) {
        return meldCalc.getParamList() != otherParams;
    }
    public void setShouldNormalize(boolean shouldNormalize){
        this.shouldNormalize = shouldNormalize; 
    }
    
    public static double play10000GameToGetMaxValue(){
        SimpleFakeGinRummyPlayer p1 = new SimpleFakeGinRummyPlayer(ParamList.getRandomParamList(0));

        for(int i = 0; i < 10000; i++){
            ParamList param0 = ParamList.getRandomParamList(1);
            EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(param0, new MeldabilityHandEvaluator(param0));

            TestingGame game = new TestingGame(p0, p1);
            game.play();
        }
        return maxEvalHand;
    }
    public static void main(String[] args){
        System.out.println("The max value for evalHand of MeldabilityHandEvaluator: " + play10000GameToGetMaxValue());
    }
}