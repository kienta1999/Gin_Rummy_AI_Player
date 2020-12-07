package players.handeval; 
import java.util.ArrayList;
import ginrummy.Card;
import util.OurUtil;
import players.StateTracker;
import players.ParamList;
import players.SimpleFakeGinRummyPlayer;
import games.TestingGame;

public class ConvHandEvaluator implements HandEvaluator {

    private ParamList params;
    boolean shouldNormalize;
    public static double maxEvalHand;
    public static final double MAX_CONV_EVAL_HAND = 25.5;

    public ConvHandEvaluator(ParamList params) {
        this.params = params;
        shouldNormalize = false;
    }

    public String getGraphicalRepresentation() {
        String result = "ConvHandEval:{";
        // result += sameRankValue + ", " + oneAwayRankSameSuitValue + ", " + twoAwayRankSameSuitValue + "}{";

        double[] genesTimes10 = new double[]{params.get(ParamList.CH_SAMERANK)*10, params.get(ParamList.CH_ONEAWAY)*10, params.get(ParamList.CH_TWOAWAY)*10};
        char[] symbols = new char[]{'r', '1', '2'};

        for(int geneID = 0; geneID < genesTimes10.length; geneID++) {
            for(int symbolCounter = 1; symbolCounter < genesTimes10[geneID]; symbolCounter++)
                result += symbols[geneID];
            for(int spaceCounter = 0; spaceCounter < 10-genesTimes10[geneID]; spaceCounter++)
                result += " ";
        }

        return result + "}";
    }

    public String toString() {
        return "ConvHandEval:{" + params.get(ParamList.CH_SAMERANK) + ", " + params.get(ParamList.CH_ONEAWAY) + ", " + params.get(ParamList.CH_TWOAWAY) + "}";
    }

    @Override
    public void setParamList(ParamList params){
        this.params = params;
    }

    @Override
    public boolean hasDifferentParamList(ParamList otherParams) {
        return this.params != otherParams;
    }

    /**
     * Normalizes the fields, and enforces the ordering requirement.
     */
    public void finalizeChanges() {
        normalize();
        enforceOrdering();
    }

    // VERIFIED 6/17
    /*
     * Every time that oneAwayRankSameSuitValue or twoAwayRankSameSuitValue changes, check that oneAwayRankSameSuitValue > twoAway…
     * If not, swap the values.
     * Should be called after calling any set method.
     */
    private void enforceOrdering() {
        if(params.get(ParamList.CH_ONEAWAY) < params.get(ParamList.CH_TWOAWAY)){
           double swap = params.get(ParamList.CH_ONEAWAY);
           params.set(ParamList.CH_ONEAWAY, params.get(ParamList.CH_TWOAWAY));
           params.set(ParamList.CH_TWOAWAY, swap);
        }
    }
    
    public static void enforceOrderingAndNormalizeTest(){
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        ConvHandEvaluator tester = new ConvHandEvaluator(params);
        params.set(ParamList.CH_SAMERANK, 1.0);
        params.set(ParamList.CH_ONEAWAY, 0.5);
        params.set(ParamList.CH_TWOAWAY, 1.5);

        System.out.println("(Before enforcement) The individual: " );
        System.out.println("The individual's gene for same rank is: " + tester.params.get(ParamList.CH_SAMERANK));
        System.out.println("The individual's gene for same suit, one rank away value is: " + params.get(ParamList.CH_ONEAWAY));
        System.out.println("The individual's gene for same suit, two rank away value is: " + params.get(ParamList.CH_TWOAWAY));

        tester.enforceOrdering();
        System.out.println("(After enforcement) The individual: " );
        System.out.println("The individual's gene for same rank is: " + tester.params.get(ParamList.CH_SAMERANK));
        System.out.println("The individual's gene for same suit, one rank away value is: " + params.get(ParamList.CH_ONEAWAY));
        System.out.println("The individual's gene for same suit, two rank away value is: " + params.get(ParamList.CH_TWOAWAY));

        tester.normalize();
        System.out.println("After normalizing:");
        System.out.println("The individual's gene for same rank is: " + tester.params.get(ParamList.CH_SAMERANK));
        System.out.println("The individual's gene for same suit, one rank away value is: " + params.get(ParamList.CH_ONEAWAY));
        System.out.println("The individual's gene for same suit, two rank away value is: " + params.get(ParamList.CH_TWOAWAY));
    }

    // VERIFIED 6/17
    private void normalize() {
        double sum = params.get(ParamList.CH_SAMERANK) + params.get(ParamList.CH_ONEAWAY) + params.get(ParamList.CH_TWOAWAY);
        double normSameRankValue = params.get(ParamList.CH_SAMERANK)/sum;
        double normOneAwayRankSameSuitValue = params.get(ParamList.CH_ONEAWAY)/sum;
        double normTwoAwayRankSameSuitValue = params.get(ParamList.CH_TWOAWAY)/sum;
        params.set(ParamList.CH_SAMERANK, normSameRankValue);
        params.set(ParamList.CH_ONEAWAY, normOneAwayRankSameSuitValue);
        params.set(ParamList.CH_TWOAWAY, normTwoAwayRankSameSuitValue);
    }

    // VERIFIED 6/17
    @Override
    public double evalHand(ArrayList<Card> cards, StateTracker myTracker, Card excludedCard){    
        double handEvaluationValue = 0;
        double [] cardValue = new double[cards.size()];
        for(int a = 0; a<cardValue.length; a++){  //initializing all card values
            cardValue[a] = 0;
        }
        for(int i = 0; i<cards.size()-1; i++){
            for(int j = i+1; j<cards.size(); j++){
                if(cards.get(i).rank == cards.get(j).rank && cards.get(i).suit != cards.get(j).suit){
                    cardValue[i] = cardValue[i] + params.get(ParamList.CH_SAMERANK);
                    cardValue[j] = cardValue[j] + params.get(ParamList.CH_SAMERANK);
                }
                else if(cards.get(i).suit == cards.get(j).suit && (cards.get(i).rank == cards.get(j).rank + 1 || cards.get(i).rank == cards.get(j).rank-1 )){
                    cardValue[i]+=params.get(ParamList.CH_ONEAWAY); 
                    cardValue[j]+=params.get(ParamList.CH_ONEAWAY);
                }
                else if(cards.get(i).suit == cards.get(j).suit && (cards.get(i).rank == cards.get(j).rank + 2 || cards.get(i).rank == cards.get(j).rank-2 )){
                    cardValue[i]+=params.get(ParamList.CH_TWOAWAY); 
                    cardValue[j]+=params.get(ParamList.CH_TWOAWAY);
                }
            }
        }
        for(double eachCardValue : cardValue){ 
            handEvaluationValue+= eachCardValue;
        }
        maxEvalHand = maxEvalHand < handEvaluationValue ? handEvaluationValue : maxEvalHand;
        //System.out.println("The handEval Value is: " + handEvaluationValue);
        if(shouldNormalize)
            return handEvaluationValue / MAX_CONV_EVAL_HAND;
        else
            return handEvaluationValue;
    }
    
    public void setShouldNormalize(boolean shouldNormalize){
        this.shouldNormalize = shouldNormalize; 
    }
    
    public static void test1(){
        System.out.println("\n=====================/ Hand Evaluation of cards with no match /=====================\n");
        ArrayList<Card> cards = OurUtil.makeHand(new String[] {"AC", "5C", "9C"});
        StateTracker myTracker = null;
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        ConvHandEvaluator test = new ConvHandEvaluator(params);
        params.set(ParamList.CH_SAMERANK, 1.5);
        params.set(ParamList.CH_ONEAWAY, 2.0);
        params.set(ParamList.CH_TWOAWAY, 1.0);
        double handEvaluation = test.evalHand(cards, myTracker, null);
        System.out.println("The cards are: " +  cards);
        System.out.println("Hand Evaluation of cards with no match is: " + handEvaluation);

    }

    public static void test2(){
        System.out.println("\n==================/ Hand Evaluation of Cards with One Away Rank, Same Suit /================\n");
        ArrayList<Card> cards = OurUtil.makeHand(new String[] {"AC", "2C", "9C"});
        StateTracker myTracker = null;
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        ConvHandEvaluator test = new ConvHandEvaluator(params);
        params.set(ParamList.CH_SAMERANK, 1.5);
        params.set(ParamList.CH_ONEAWAY, 2.0);
        params.set(ParamList.CH_TWOAWAY, 1.0);
        double handEvaluation = test.evalHand(cards, myTracker, null);
        System.out.println("The cards are: " +  cards);
        System.out.println("Hand Evaluation of cards with one away rank, same suit is: " + handEvaluation);

    }

    public static void test3(){
        System.out.println("\n==================/ Hand Evaluation of Cards with Two Away Rank, Same Suit /================\n");
        ArrayList<Card> cards = OurUtil.makeHand(new String[] {"AC", "3C", "9C"});
        StateTracker myTracker = null;
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        ConvHandEvaluator test = new ConvHandEvaluator(params);
        params.set(ParamList.CH_SAMERANK, 1.5);
        params.set(ParamList.CH_ONEAWAY, 2.0);
        params.set(ParamList.CH_TWOAWAY, 1.0);
        double handEvaluation = test.evalHand(cards, myTracker, null);
        System.out.println("The cards are: " +  cards);
        System.out.println("Hand Evaluation of cards with two away rank, same suit is: " + handEvaluation);
       
    }

    public static void test4(){
        System.out.println("\n====================/ Hand Evaluation of Cards with Same Ranks /=====================\n");
        ArrayList<Card> cards = OurUtil.makeHand(new String[] {"AC", "AD", "9H"});
        StateTracker myTracker = null;
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        ConvHandEvaluator test = new ConvHandEvaluator(params);
        params.set(ParamList.CH_SAMERANK, 1.5);
        params.set(ParamList.CH_ONEAWAY, 2.0);
        params.set(ParamList.CH_TWOAWAY, 1.0);
        double handEvaluation = test.evalHand(cards, myTracker, null);
        System.out.println("The cards are: " +  cards);
        System.out.println("Hand Evaluation of cards with same ranks is: " + handEvaluation);     
    }

    public static void test5(){
        System.out.println("\n====================/ Hand Evaluation of Card Triangle /=====================\n");
        ArrayList<Card> cards = OurUtil.makeHand(new String[] {"AC", "AD", "2D"});
        StateTracker myTracker = null;
        ParamList params = new ParamList(new double[]{}); // don't need any ensemble weights for this test
        ConvHandEvaluator test = new ConvHandEvaluator(params);
        params.set(ParamList.CH_SAMERANK, 1.5);
        params.set(ParamList.CH_ONEAWAY, 2.0);
        params.set(ParamList.CH_TWOAWAY, 1.0);
        double handEvaluation = test.evalHand(cards, myTracker, null);

        System.out.println("The cards are: " +  cards);
        System.out.println("Hand Evaluation of cards with same ranks is: " + handEvaluation);
        System.out.println("Using field values:\none away: " + params.get(ParamList.CH_ONEAWAY) + "\ntwo away: " + params.get(ParamList.CH_TWOAWAY) + "\nsame rank: " + params.get(ParamList.CH_SAMERANK));
        System.out.println("The calculation is:\nAC:\n  1.5 same rank with AD\nAD:\n  1.5 same rank with AC\n  2.0 one away from 2D\n2D:\n  2.0 one away from AD");
    }

    public static double play10000GameToGetMaxValue(){
        SimpleFakeGinRummyPlayer p1 = new SimpleFakeGinRummyPlayer(ParamList.getRandomParamList(0));

        for(int i = 0; i < 10000; i++){
            ParamList param0 = ParamList.getRandomParamList(1);
            EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(param0, new ConvHandEvaluator(param0));

            TestingGame game = new TestingGame(p0, p1);
            game.play();
        }
        return maxEvalHand;
    }

    public static void main(String[] args) {
        //test1();
       // test2();
       // test3();
        //test4();
        //test5();
        // enforceOrderingAndNormalizeTest();
        System.out.println("The max value for evalHand of MeldabilityHandEvaluator: " + play10000GameToGetMaxValue());
    }

}

/*
The cards are: [AC, AD, 2D]
Hand Evaluation of cards with same ranks is: 7.0
Using field values:
one away: 2.0
two away: 1.0
same rank: 1.5
The calculation is:
AC:
  1.5 same rank with AD
AD:
  1.5 same rank with AC
  2.0 one away from 2D
2D:
  2.0 one away from AD
*/
