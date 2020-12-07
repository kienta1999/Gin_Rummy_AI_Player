package players;

import games.TestingGame;
import ginrummy.*;

/**
 * This class is simply for testing / illustrating the TestingGame class,
 * for crashing the code on bad player actions.
 * 
 * @author Steven Bogaerts
 */
public class BadActionPlayer extends OurSimpleGinRummyPlayer {
    
    /**
     * Just returns AC, regardless of whether it has it or not.
     */
	@Override
	public Card getDiscard() {
        return Card.strCardMap.get("AC");
    }

    public static void main(String[] args) {
        OurSimpleGinRummyPlayer a = new OurSimpleGinRummyPlayer();
        OurSimpleGinRummyPlayer b = new OurSimpleGinRummyPlayer();
        BadActionPlayer c = new BadActionPlayer();

        TestingGame.setPlayVerbose(false);

        TestingGame game;
        int winner;

        System.out.println("---------------------------- Ordinary, non-erroneous game: ");
        game = new TestingGame(a, b);
        winner = game.play();
        System.out.println("Winner: " + winner);

        System.out.println("---------------------------- Erroneous game: ");
        game = new TestingGame(a, c);
        winner = game.play();
        System.out.println("Winner: " + winner);
    }

}