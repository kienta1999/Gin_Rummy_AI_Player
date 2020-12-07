package knock;

import java.util.ArrayList;

import ginrummy.Card;

/**
 * Not used yet.
 * 
 * Classes that implement this interface use some means to predict the score
 * a player (or its opponent) will earn if it knocks.
 * 
 * @author Steven Bogaerts
 */
public interface ScorePredictor {

    /**
     * Attempts to predict the score that would result if self knocks.
     * A positive score means self gets those points.
     * A negative score means opp gets those points.
     * 
     * Ultimately, we'd want to develop this interface to take in much more information:
     * a data structure about what is known about the game.
     * 
     * @param selfHand the hand self currently has
     * @param numMoves the number of moves in the game so far
     * 
     * @return the predicted score
     */
    public int predictScoreIfKnock(ArrayList<Card> selfHand, int numMoves);
    // maybe also pass in number of times opponent drew face-up card, number of times self drew face-up card, etc.
    // Eventually, will pass in lots of data we're tracking
}