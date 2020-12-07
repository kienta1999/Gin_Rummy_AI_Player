package players;

import java.util.ArrayList;

import experiment.StateTrackerAnalyzer;
import players.OurSimpleGinRummyPlayer;
import util.OurUtil;
import ginrummy.Card;

/**
 * This class is just an OurSimpleGinRummyPlayer that implements the FakePlayer interface.
 * This gives it some additional functionality for "cheating" for testing purposes.
 * 
 * @author Steven Bogaerts
 */
public class SimpleFakeGinRummyPlayer extends OurSimpleGinRummyPlayer implements FakePlayer {
    
	/**
	 * This method allows a player to show its cards to anyone with access to the object.
     * It is implemented in OurSimpleGinRummyPlayer to enable the KnockTrackingPlayer to cheat.
	 * DO NOT use this method unless you intend for your player to cheat by looking at the opponent's cards!
	 * Obviously we won't be able to do this in the actual tournament, since this interface won't be implemented
     * by any real opponents.
     * 
	 * @return The cards field
	 */
	
	protected StateTracker myStateTracker;
	protected ParamList params;
	public PerformanceTracker myPerformanceTracker;

	/*
	public SimpleFakeGinRummyPlayer() {
		super();
		params = ParamList.getRandomParamList(0);
		myStateTracker = new  StateTracker(params);
		myPerformanceTracker = new PerformanceTracker();
	}
	*/

	public SimpleFakeGinRummyPlayer(ParamList params) {
		// this();
		this.params = params;
		myStateTracker = new StateTracker(params);
		myPerformanceTracker = new PerformanceTracker();
	}

	public SimpleFakeGinRummyPlayer(ParamList params, TunableStateTracker tunableStateTracker){
		this(params);
		myStateTracker = tunableStateTracker;
		OurUtil.assertCondition(myStateTracker.params == params, "TunableStateTracker’s ParamList is not the same ParamList that’s passed in ");
	}
	
    public StateTracker getStateTracker() {
        return this.myStateTracker;
	}
	
    public void setParamList(ParamList newParams) {
        this.params = newParams;
        myStateTracker.setParamList(this.params);
	}
	
	@Override
	public ArrayList<Card> showCards() {
		return cards;
	}

	/**
	 * Change the cards that this player has. Useful for testing a single play.
	 * Obviously can't be used in a real game competition.
	 * 
	 * @param The new list of cards for this player.
	 */
	@Override
	public void setCards(ArrayList<Card> newCards) {
		this.cards = newCards;
	}

	public int getPlayerNum() {
		return this.playerNum;
	}

	public void setPlayerNum(int playerNum) {
		this.playerNum = playerNum;
	}

	@Override
	public void startGame(int playerNum, int startingPlayerNum, Card[] cards){
		super.startGame(playerNum, startingPlayerNum, cards);
		myStateTracker.updateFromStartGame(playerNum, startingPlayerNum, cards);
		myPerformanceTracker.updateFromStartGame(myStateTracker, this.playerNum, this.cards);
	}

	@Override
	public boolean willDrawFaceUpCard(Card card) {
		boolean willDraw = super.willDrawFaceUpCard(card);
		myStateTracker.updateFromWillDrawFaceUpCard(card, willDraw);
		return willDraw;
		
	}
	@Override
	public void reportDraw(int playerNum, Card drawnCard) {
		super.reportDraw(playerNum, drawnCard);
		myStateTracker.updateFromReportDraw(playerNum, drawnCard);
	}

	@Override
	public Card getDiscard() {
		Card discard = super.getDiscard();
		return discard;
	}
	
	@Override
	public void reportDiscard(int playerNum, Card discardedCard){
		super.reportDiscard(playerNum, discardedCard);
		myStateTracker.updateFromReportDiscard(playerNum, discardedCard);
	}

	@Override
	public ArrayList<ArrayList<Card>> getFinalMelds(){
		ArrayList<ArrayList<Card>> finalMeld = super.getFinalMelds();
		myPerformanceTracker.updateFromGetFinalMelds(finalMeld);
		return finalMeld;
	}

	@Override
	public void reportFinalMelds(int playerNum, ArrayList<ArrayList<Card>> melds){
		super.reportFinalMelds(playerNum, melds);
		//if(playerNum != this.playerNum){
			//opponentKnocked = true;
		//}
		myPerformanceTracker.updateFromReportFinalMelds(playerNum, melds);
	}
	
	@Override
	public void reportScores(int[] scores) {
		// Ignored by simple player, but could affect strategy of more complex player.
		super.reportScores(scores);
		myPerformanceTracker.updateFromReportScore(scores);
	}

	@Override
	public void reportLayoff(int playerNum, Card layoffCard, ArrayList<Card> opponentMeld) {
		// Ignored by simple player, but could affect strategy of more complex player.
		super.reportLayoff(playerNum, layoffCard, opponentMeld);
		myPerformanceTracker.updateFromReportLayoff(playerNum);
	}

	@Override
	public void reportFinalHand(int playerNum, ArrayList<Card> hand) {
		// Ignored by simple player, but could affect strategy of more complex player.
		super.reportFinalHand(playerNum, hand);
	}
}