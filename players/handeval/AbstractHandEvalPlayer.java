package players.handeval;
import ginrummy.Card;
import ginrummy.GinRummyUtil;
import players.SimpleFakeGinRummyPlayer;
import players.StateTracker;
import players.TunableStateTracker;
import players.ParamList;
import java.util.ArrayList;

import games.TestingGame;
import players.handeval.EnsembleHandEvalPlayer;

/**
 * This abstract class defines getDiscard to call choose10From11, to choose
 * which card to drop.
 * 
 * choose10From11 is an abstract method. Subclasses must provide
 * an implementation to determine how to choose the card to drop.
 * 
 * The class also handles the StateTracker maintenance for extending players.
 * 
 * Otherwise, this class just does what the simple player does:
 * - Knock as soon as possible
 * - Never take a face-up card unless it makes a meld
 */
public abstract class AbstractHandEvalPlayer extends SimpleFakeGinRummyPlayer {

    protected static final boolean TESTING = false;
    protected boolean drewFaceUpCard;

    protected Card toDiscard;

    protected KnockDecider knockDecider;
    protected DrawDecider drawDecider;

    public AbstractHandEvalPlayer(ParamList params) {
        super(params);
        this.knockDecider = null;
    }

    /*
    public StateTracker getStateTracker(){
        return this.myTracker;
    }
    */

    /*
    public void setParamList(ParamList params){
        this.params = params;
        myTracker.setParamList(params);
    }
    */

    /**
     * If this method is not called, then the default knock deciding mechanism will be used,
     * inherited from KnockOnGinPlayer.
     */
    public void setKnockDecider(KnockDecider kd) {
        util.OurUtil.assertCondition(!kd.hasDifferentParamList(this.params), "The knock decider must have the same ParamList as the ensemble player.");
        this.knockDecider = kd;
    }

    public void setDrawDecider(DrawDecider dd){
        // util.OurUtil.assertCondition(!dd.hasDifferentParamList(this.params), "The draw decider must have the same ParamList as the ensemble player.");
        this.drawDecider = dd;
    }

	@Override
	public void startGame(int playerNum, int startingPlayerNum, Card[] cards) {
        super.startGame(playerNum, startingPlayerNum, cards);

        if (TESTING) System.out.println("================\n===================\n===============\nStarting game");

        //this.toDiscard = null;
        //myTracker.updateFromStartGame(playerNum, startingPlayerNum, cards);
    }

    // the previous willDrawFaceUp method is in the end
    @Override
    public boolean willDrawFaceUpCard(Card faceUp) {
        //If self has no drawDecider, the program crashes
        boolean willDraw = drawDecider.shouldDraw(cards, faceUp, myStateTracker);
        drewFaceUpCard = willDraw;
        myStateTracker.updateFromWillDrawFaceUpCard(faceUp, willDraw); 
        return willDraw;
    }

    @Override
	public void reportDraw(int playerNum, Card drawnCard) {
        super.reportDraw(playerNum, drawnCard);  
        // myStateTracker.updateFromReportDraw(playerNum, drawnCard);
        if (TESTING) {
            if (this.playerNum == playerNum)
                System.out.println("Cards of Player " + playerNum + " after drawn card is reported: " + cards);
        }
    }

    // 
    @Override
	public Card getDiscard() {
        // super.getDiscard();
        return choose10From11Cards(cards);

        /*
        // cards has 11 cards now, thanks to the Simple player's reportDraw method.
        if (drewFaceUpCard) {
            if (TESTING) System.out.println("getDiscard, we did draw face up card");
            Card toReturn = toDiscard;
            toDiscard = null;
            drewFaceUpCard = false;
            return toReturn;
        }
        else {
            if (TESTING) System.out.println("getDiscard, we did NOT draw face up card");
            return choose10From11Cards(cards); 
        }*/
    }

    /**
     * Override this method to determine how to choose the best hand of 10 cards
     * from a hand of 11 cards.
     * @return the Card to be discarded, that leads to the best hand of 10
     */
    protected abstract Card choose10From11Cards(ArrayList<Card> handOf11);

    @Override
    public void reportDiscard(int playerNum, Card discardedCard) {
        super.reportDiscard(playerNum, discardedCard);
        //myStateTracker.updateFromReportDiscard(playerNum, discardedCard);

        if (TESTING) {
            if (this.playerNum == playerNum)
                System.out.println("Cards of Player " + playerNum + " after reportDiscard is called:  " + cards);
        }
    }
    
    @Override
	public ArrayList<ArrayList<Card>> getFinalMelds() {
        ArrayList<ArrayList<Card>> result;

        // If self has no knock decider, the program crahses.
        // If the opponent knocked, we have no decisions to make about knocking,
        // so use the super getFinalMelds.
        if (opponentKnocked)
            result = super.getFinalMelds();
        else { // opponent didn't knock, so self decides if it wants to or not.
            result = knockDecider.shouldKnock(cards, myStateTracker);
            myPerformanceTracker.updateFromGetFinalMelds(result);
        }

        if (TESTING) System.out.println("getFinalMelds ends: " + cards);
        return result;
    }
    
    public static void testKnockDecider(){
        EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(ParamList.getRandomParamList(1), new DeadwoodHandEvaluator());
        SimpleFakeGinRummyPlayer p1 = new SimpleFakeGinRummyPlayer(ParamList.getRandomParamList(0));
        //p0.setKnockDecider(new KnockOnGinKnockDecider() );
        TestingGame game = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(true);
        game.play();
    }
    
    public static void main(String[] args){
        testKnockDecider();
    }

}










        // super.willDrawFaceUpCard(faceUp); // No - call it below using willDraw
        /*boolean willDraw;
        ArrayList<Card> candidateCards = new ArrayList<Card>(cards);
        candidateCards.add(faceUp);
        double[] deadwoodPoints = new double[11]; 
        for(int indexOfDiscardedChoice = 0; indexOfDiscardedChoice<candidateCards.size(); indexOfDiscardedChoice++) {
            ArrayList<Card> candidateCards_copy = new ArrayList<Card>(candidateCards);
            candidateCards_copy.remove(indexOfDiscardedChoice);
            ArrayList<ArrayList<ArrayList<Card>>> lsOfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(candidateCards_copy);
            if (lsOfBestMeldSets.isEmpty()) {
                deadwoodPoints[indexOfDiscardedChoice] = GinRummyUtil.getDeadwoodPoints(candidateCards_copy);
            }
            else {
                ArrayList<ArrayList<Card>> bestMeldSet = lsOfBestMeldSets.get(0);
                deadwoodPoints[indexOfDiscardedChoice] = GinRummyUtil.getDeadwoodPoints(bestMeldSet, candidateCards_copy); 
            }        
        }

        if (TESTING) System.out.println("deadwoodPoints: " + java.util.Arrays.toString(deadwoodPoints));

        int indexOfCardMinDeadwoodValue = 0;
        double deadwoodMinValue = deadwoodPoints[indexOfCardMinDeadwoodValue];
        for(int i = 0; i < deadwoodPoints.length; i++) {
            if(deadwoodMinValue >= deadwoodPoints[i]) { // if a tie, update, so that if faceup is tied, that's what we choose to drop (that is, we choose not to take it in the first place)
                indexOfCardMinDeadwoodValue = i;
                deadwoodMinValue = deadwoodPoints[indexOfCardMinDeadwoodValue];
                if (TESTING) System.out.println("New min at index " + indexOfCardMinDeadwoodValue + " with value " + deadwoodMinValue);
            }
        }
        if(candidateCards.get(indexOfCardMinDeadwoodValue)==faceUp){
            willDraw = false;
        }
        else{
            willDraw = true;
        }
        drewFaceUpCard = willDraw;
        myStateTracker.updateFromWillDrawFaceUpCard(faceUp, willDraw); 
        
        return willDraw;*/
        
        //Unverified
        /*
        if (TESTING) System.out.println("\ncards in hand: " + cards);
        boolean willDraw = false;
        ArrayList<Card> candidateCards = new ArrayList<Card>(cards);
        candidateCards.add(faceUp);
        if (TESTING) System.out.println("Candidate cards are:  "+ candidateCards);
        Card discardChoice = choose10From11Cards(candidateCards);  
        if(discardChoice == faceUp) {
            if (TESTING) System.out.println("The face up card is not drawn");
            if (TESTING) System.out.println("cards: " + cards + "\n");
            toDiscard = null;
            willDraw = false;  
        }
        else {
            toDiscard = discardChoice;   
            if (TESTING) System.out.println("The face up card is drawn, will discard: " + toDiscard);
            willDraw = true;    
        }
        drewFaceUpCard = willDraw;
        if (TESTING) System.out.println("willDraw: " + willDraw);
        myTracker.updateFromWillDrawFaceUpCard(faceUp, willDraw); 

        return willDraw; */