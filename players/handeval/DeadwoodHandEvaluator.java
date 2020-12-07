package players.handeval;

import java.util.ArrayList;
import ginrummy.Card;
import players.StateTracker;
import util.OurUtil;
import ginrummy.GinRummyUtil;
import players.ParamList;
/**
 * This HandEvaluator evaluates a hand highly if it has little (or no) deadwood.
 */
public class DeadwoodHandEvaluator implements HandEvaluator {

    private static final boolean TESTING = false;

    private static final int HIGHEST_DEADWOOD_POINTS = 98; // corresponding to (for example) TS, JC, QS, KC, TH, JD, QH, KD, 9C, 9D

    // VERIFIED 6/16
    /**
     * A score of 1 means no deadwood at all.
     * A score of 0 means the maximum possible deadwood (98).
     * So higher is better, as with other HandEvaluators.
     */
    @Override
    public double evalHand(ArrayList<Card> cards, StateTracker myTracker, Card excludedCard) {
        ArrayList<Card>[] meldedUnmelded = OurUtil.getMeldedAndUnmeldedCards(cards);
        // ArrayList<Card> meldedCards = meldedUnmelded[0];
        ArrayList<Card> unmeldedCards = meldedUnmelded[1];

        if (TESTING) System.out.println("unmeldedCards: " + unmeldedCards);

        double deadwood0To1 = ((double) GinRummyUtil.getDeadwoodPoints(unmeldedCards)) / HIGHEST_DEADWOOD_POINTS; // near 1 means lots of deadwood. 0 means none.

        double noDeadwoodScore = 1-deadwood0To1; // near 1 means no deadwood. near 0 means lots of deadwood.
        // So higher noDeadwoodScore is better.

        return noDeadwoodScore;
    }

    @Override
    public void setParamList(ParamList params) {
    }

    @Override
    public boolean hasDifferentParamList(ParamList otherParams) {
        return false;
    }
    
    public static void main(String[] args) {
        System.out.println("=============== testEnsembleEvaluation ===============");
        HandEvaluator he = new DeadwoodHandEvaluator();

        StateTracker tracker = null; // not used in this code
        ArrayList<Card> handMaxDeadwood = OurUtil.makeHand(new String[]    {"TS", "JC", "QS", "KC", "TH", "JD", "QH", "KD", "9C", "9D"}); // deadwood: 98
        ArrayList<Card> handMoreDeadwood = OurUtil.makeHand(new String[]   {"AC", "2C", "3C", "4C", "5C", "6C", "TD", "JH", "QD", "KH"}); // deadwood: 40
        ArrayList<Card> handLittleDeadwood = OurUtil.makeHand(new String[] {"AC", "2C", "3C", "4C", "5C", "6C", "8S", "8C", "8D", "KH"}); // deadwood: 10
        ArrayList<Card> handNoDeadwood = OurUtil.makeHand(new String[]     {"AC", "2C", "3C", "4C", "5C", "6C", "7S", "7C", "7D", "7H"}); // deadwood: 0

        System.out.println("Deadwood evaluation (worst to best): " + he.evalHand(handMaxDeadwood, null, null) + ", " 
                                                                   + he.evalHand(handMoreDeadwood, null, null) + ", " 
                                                                   + he.evalHand(handLittleDeadwood, null, null) + ", " 
                                                                   + he.evalHand(handNoDeadwood, null, null));

        System.out.println("Right answers: " + (1-98/98.0) + ", " + (1-40/98.0) + ", " + (1-10/98.0) + ", " + (1-0/98.0));
    }

}
