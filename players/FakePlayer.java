package players;

import ginrummy.GinRummyPlayer;
import ginrummy.Card;
import java.util.ArrayList;

/**
 * This interface specifies some additional methods useful for testing, that wouldn't be allowed
 * in a real Gin Rummy player.
 * 
 * If you're building a player to be an eligible competitor, DO NOT use any methods in this interface.
 * Rather, this interface and its implementing classes are for testing and development only.
 * 
 * @author Steven Bogaerts
 */
public interface FakePlayer extends GinRummyPlayer {

	/**
	 * This method allows a player to show its cards to anyone with access to the object.
     * It is implemented in OurSimpleGinRummyPlayer to enable the KnockTrackingPlayer to cheat.
	 * DO NOT use this method unless you intend for your player to cheat by looking at the opponent's cards!
	 * Obviously we won't be able to do this in the actual tournament, since this interface won't be implemented
     * by any real opponents.
     * 
	 * @return The cards field
	 */
	public ArrayList<Card> showCards();

	/**
	 * Change the cards that this player has. Useful for testing a single play.
	 * Obviously can't be used in a real game competition.
	 * 
	 * @param The new list of cards for this player.
	 */
	public void setCards(ArrayList<Card> newCards);

	public int getPlayerNum();

	public void setPlayerNum(int playerNum);
    
}
