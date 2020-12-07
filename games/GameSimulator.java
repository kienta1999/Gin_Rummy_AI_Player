package games;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import ga.GATuner;
import ginrummy.Card;
import ginrummy.GinRummyPlayer;
import ginrummy.GinRummyUtil;
import players.DoublePlayer;
import players.OurSimpleGinRummyPlayer;
import players.ParamList;
import players.StateTracker;
import players.YourPlayer;
import players.handeval.ConvHandEvaluator;
import players.handeval.DeadwoodHandEvaluator;
import players.handeval.EnsembleHandEvalPlayer;
import players.handeval.IndexEnsembleHandEvalPlayer;
import players.handeval.MeldabilityHandEvaluator;
import weka.core.pmml.jaxbbindings.SetReference;

public class GameSimulator {
    	/**
	 * Random number generator
	 */
	private static final Random RANDOM = new Random();
	
	/**
	 * Hand size (before and after turn). After draw and before discard there is one extra card.
	 */
	private static final int HAND_SIZE = 10;
	
	/**
	 * Whether or not to print information during game play
	 */
    private static boolean playVerbose = false;
    
    private static boolean playRealGame = true;

    private static final boolean WRITE_DATA_TO_FILE = true;

	/**
	 * Two Gin Rummy players numbered according to their array index.
	 */
    private GinRummyPlayer[] players;
    
    private String gameInfo;
    private ArrayList<String> gameInfoAL;
    private Scanner scanner; 

    //contain the data of discard - to write data to file
    private ArrayList<String> allStateTracker;
    private ArrayList<Card> allOppDiscards;
    private ArrayList<ArrayList<Card>> allPlayerHands;
    private ArrayList<ArrayList<Card>> allOppHands;
	
	/**
	 * Set whether or not there is to be printed output during gameplay.
	 * @param playVerbose whether or not there is to be printed output during gameplay
	 */
	public static void setPlayVerbose(boolean playVerbose) {
		GameSimulator.playVerbose = playVerbose;
    }
    
    public static void setPlayRealGame(boolean playRealGame){
        GameSimulator.playRealGame = playRealGame;
    }
	
	/**
	 * Create a GinRummyGame with two given players
	 * @param player0 Player 0
	 * @param player1 Player 1
	 */
	public GameSimulator(YourPlayer player0, EnsembleHandEvalPlayer player1) {
        players = new GinRummyPlayer[] {player0, player1};
        gameInfoAL = new ArrayList<String>();
        gameInfo = "";

        allStateTracker = new ArrayList<>();
        allOppDiscards = new ArrayList<>();
        allPlayerHands = new ArrayList<>(); 
        allOppHands = new ArrayList<>();
	}

	/**
	 * Play a game of Gin Rummy and return the winning player number 0 or 1.
	 * @return the winning player number 0 or 1
	 */
	@SuppressWarnings("unchecked")
	public int play() {
		int[] scores = new int[2];
		ArrayList<ArrayList<Card>> hands = new ArrayList<ArrayList<Card>>();
		hands.add(new ArrayList<Card>());
		hands.add(new ArrayList<Card>());
		int startingPlayer = RANDOM.nextInt(2);
		
		while (scores[0] < GinRummyUtil.GOAL_SCORE && scores[1] < GinRummyUtil.GOAL_SCORE) { // while game not over
			int currentPlayer = startingPlayer;
			int opponent = (currentPlayer == 0) ? 1 : 0;
            
			// get shuffled deck and deal cards
			Stack<Card> deck = Card.getShuffle(RANDOM.nextInt());
			hands.get(0).clear();
			hands.get(1).clear();
			for (int i = 0; i < 2 * HAND_SIZE; i++)
				hands.get(i % 2).add(deck.pop());
			for (int i = 0; i < 2; i++) {
				Card[] handArr = new Card[HAND_SIZE];
				hands.get(i).toArray(handArr);
				players[i].startGame(i, startingPlayer, handArr); 
				if (playVerbose)
                    System.out.printf("Player %d is dealt %s.\n", i, hands.get(i));
                gameInfo += String.format("Player %d is dealt %s.\n", i, hands.get(i)) ;
			}
			if (playVerbose || playRealGame)
                System.out.printf("Player %d starts.\n", startingPlayer);
            gameInfo += String.format("Player %d starts.\n", startingPlayer);

			Stack<Card> discards = new Stack<Card>();
			discards.push(deck.pop());
			if (playVerbose || playRealGame)
                System.out.printf("The initial face up card is %s.\n", discards.peek());
            gameInfo += String.format("The initial face up card is %s.\n", discards.peek());

			Card firstFaceUpCard = discards.peek();
			int turnsTaken = 0;
            ArrayList<ArrayList<Card>> knockMelds = null;
            

			while (deck.size() > 2) { // while the deck has more than two cards remaining, play round
				// DRAW
				boolean drawFaceUp = false;
				Card faceUpCard = discards.peek();
				// offer draw face-up iff not 3rd turn with first face up card (decline automatically in that case) 
				if (!(turnsTaken == 2 && faceUpCard == firstFaceUpCard)) { // both players declined and 1st player must draw face down
					drawFaceUp = players[currentPlayer].willDrawFaceUpCard(faceUpCard);
					if (!drawFaceUp && faceUpCard == firstFaceUpCard && turnsTaken < 2){
                        if(playVerbose || playRealGame)
                            System.out.printf("Player %d declines %s.\n", currentPlayer, firstFaceUpCard);
                        gameInfo += String.format("Player %d declines %s.\n", currentPlayer, firstFaceUpCard);
                    }
				}
				if (!(!drawFaceUp && turnsTaken < 2 && faceUpCard == firstFaceUpCard)) { // continue with turn if not initial declined option
					Card drawCard = drawFaceUp ? discards.pop() : deck.pop();
					for (int i = 0; i < 2; i++) 
						players[i].reportDraw(currentPlayer, (i == currentPlayer || drawFaceUp) ? drawCard : null);
					if (playVerbose)
                        System.out.printf("Player %d draws %s.\n", currentPlayer, drawCard);
                    gameInfo += String.format("Player %d draws %s.\n", currentPlayer, drawCard);
					hands.get(currentPlayer).add(drawCard);

					// DISCARD
					Card discardCard = players[currentPlayer].getDiscard();
					if (!hands.get(currentPlayer).contains(discardCard) || discardCard == faceUpCard) {
                        if (playVerbose || playRealGame)
                            System.out.printf("Player %d discards %s illegally and forfeits.\n", currentPlayer, discardCard);
                        gameInfo += String.format("Player %d discards %s illegally and forfeits.\n", currentPlayer, discardCard);
						return opponent;
					}
					hands.get(currentPlayer).remove(discardCard);
					for (int i = 0; i < 2; i++) 
						players[i].reportDiscard(currentPlayer, discardCard);
					if (playVerbose || playRealGame)
                        System.out.printf("Player %d discards %s.\n", currentPlayer, discardCard);
                    gameInfo += String.format("Player %d discards %s.\n", currentPlayer, discardCard);
                    discards.push(discardCard);
                    
                    if(currentPlayer == 1 && WRITE_DATA_TO_FILE){
                        try{
                            allStateTracker.add(StateTracker.serializeStateTrackerToString(((EnsembleHandEvalPlayer)players[1]).getStateTracker()));
                        }
                        catch(Exception e){
                            System.out.println("State Tracker not available");
                        }

                        allOppDiscards.add(discardCard);
                        allPlayerHands.add(DoublePlayer.cloneArrayListOfCard(hands.get(opponent)));
                        allOppHands.add(DoublePlayer.cloneArrayListOfCard(hands.get(currentPlayer)));
                    }
					
						ArrayList<Card> unmeldedCards = (ArrayList<Card>) hands.get(currentPlayer).clone();
						ArrayList<ArrayList<ArrayList<Card>>> bestMelds = GinRummyUtil.cardsToBestMeldSets(unmeldedCards);
						if (bestMelds.isEmpty()) {
                            if(playVerbose) System.out.printf("Player %d has %s with %d deadwood.\n", currentPlayer, unmeldedCards, GinRummyUtil.getDeadwoodPoints(unmeldedCards));
                            gameInfo += String.format("Player %d has %s with %d deadwood.\n", currentPlayer, unmeldedCards, GinRummyUtil.getDeadwoodPoints(unmeldedCards));
                        }
						else {
							ArrayList<ArrayList<Card>> melds = bestMelds.get(0);
							for (ArrayList<Card> meld : melds)
								for (Card card : meld)
									unmeldedCards.remove(card);
							melds.add(unmeldedCards);
                            if(playVerbose) System.out.printf("Player %d has %s with %d deadwood.\n", currentPlayer, melds, GinRummyUtil.getDeadwoodPoints(unmeldedCards));
                            gameInfo += String.format("Player %d has %s with %d deadwood.\n", currentPlayer, melds, GinRummyUtil.getDeadwoodPoints(unmeldedCards));
                        }
                        
						
					// CHECK FOR KNOCK 
					knockMelds = players[currentPlayer].getFinalMelds();
					if (knockMelds != null)
						break; // player knocked; end of round
				}

				turnsTaken++;
				currentPlayer = (currentPlayer == 0) ? 1 : 0;
				opponent = (currentPlayer == 0) ? 1 : 0;
			}
			
			if (knockMelds != null) { // round didn't end due to non-knocking and 2 cards remaining in draw pile
				// check legality of knocking meld
				long handBitstring = GinRummyUtil.cardsToBitstring(hands.get(currentPlayer));
				long unmelded = handBitstring;
				for (ArrayList<Card> meld : knockMelds) {
					long meldBitstring = GinRummyUtil.cardsToBitstring(meld);
					if (!GinRummyUtil.getAllMeldBitstrings().contains(meldBitstring) // non-meld ...
							|| (meldBitstring & unmelded) != meldBitstring) { // ... or meld not in hand
						if (playVerbose || playRealGame)
                            System.out.printf("Player %d melds %s illegally and forfeits.\n", currentPlayer, knockMelds);
                        gameInfo += String.format("Player %d melds %s illegally and forfeits.\n", currentPlayer, knockMelds);
						return opponent;
					}
					unmelded &= ~meldBitstring; // remove successfully melded cards from 
				}
				// compute knocking deadwood
				int knockingDeadwood = GinRummyUtil.getDeadwoodPoints(knockMelds, hands.get(currentPlayer));
				if (knockingDeadwood > GinRummyUtil.MAX_DEADWOOD) {
					if (playVerbose || playRealGame)
                        System.out.printf("Player %d melds %s with greater than %d deadwood and forfeits.\n", currentPlayer, knockMelds, knockingDeadwood);				
                    gameInfo += String.format("Player %d melds %s with greater than %d deadwood and forfeits.\n", currentPlayer, knockMelds, knockingDeadwood);
					return opponent;
				}
				
				ArrayList<ArrayList<Card>> meldsCopy = new ArrayList<ArrayList<Card>>();
				for (ArrayList<Card> meld : knockMelds)
					meldsCopy.add((ArrayList<Card>) meld.clone());
				for (int i = 0; i < 2; i++) 
                    players[i].reportFinalMelds(currentPlayer, meldsCopy);
                    
					if (knockingDeadwood > 0) {
                        if(playVerbose || playRealGame) System.out.printf("Player %d melds %s with %d deadwood from %s.\n", currentPlayer, knockMelds, knockingDeadwood, GinRummyUtil.bitstringToCards(unmelded));
                        gameInfo += String.format("Player %d melds %s with %d deadwood from %s.\n", currentPlayer, knockMelds, knockingDeadwood, GinRummyUtil.bitstringToCards(unmelded));
                    }
					else{
                        if(playVerbose || playRealGame) System.out.printf("Player %d goes gin with melds %s.\n", currentPlayer, knockMelds);
                        gameInfo += String.format("Player %d goes gin with melds %s.\n", currentPlayer, knockMelds);
                    }
				// get opponent meld
				ArrayList<ArrayList<Card>> opponentMelds = players[opponent].getFinalMelds();
				for (ArrayList<Card> meld : opponentMelds)
					meldsCopy.add((ArrayList<Card>) meld.clone());
				meldsCopy = new ArrayList<ArrayList<Card>>();
				for (int i = 0; i < 2; i++) 
					players[i].reportFinalMelds(opponent, meldsCopy);
				
				// check legality of opponent meld
				long opponentHandBitstring = GinRummyUtil.cardsToBitstring(hands.get(opponent));
				long opponentUnmelded = opponentHandBitstring;
				for (ArrayList<Card> meld : opponentMelds) {
					long meldBitstring = GinRummyUtil.cardsToBitstring(meld);
					if (!GinRummyUtil.getAllMeldBitstrings().contains(meldBitstring) // non-meld ...
							|| (meldBitstring & opponentUnmelded) != meldBitstring) { // ... or meld not in hand
						if (playVerbose || playRealGame)
                            System.out.printf("Player %d melds %s illegally and forfeits.\n", opponent, opponentMelds);
                            gameInfo += String.format("Player %d melds %s illegally and forfeits.\n", opponent, opponentMelds);
						return currentPlayer;
					}
					opponentUnmelded &= ~meldBitstring; // remove successfully melded cards from 
				}
				if (playVerbose || playRealGame)
                    System.out.printf("Player %d melds %s.\n", opponent, opponentMelds);
                gameInfo += String.format("Player %d melds %s.\n", opponent, opponentMelds);

				// lay off on knocking meld (if not gin)
				ArrayList<Card> unmeldedCards = GinRummyUtil.bitstringToCards(opponentUnmelded);
				if (knockingDeadwood > 0) { // knocking player didn't go gin
					boolean cardWasLaidOff;
					do { // attempt to lay each card off
						cardWasLaidOff = false;
						Card layOffCard = null;
						ArrayList<Card> layOffMeld = null;
						for (Card card : unmeldedCards) {
							for (ArrayList<Card> meld : knockMelds) {
								ArrayList<Card> newMeld = (ArrayList<Card>) meld.clone();
								newMeld.add(card);
								long newMeldBitstring = GinRummyUtil.cardsToBitstring(newMeld);
								if (GinRummyUtil.getAllMeldBitstrings().contains(newMeldBitstring)) {
									layOffCard = card;
									layOffMeld = meld;
									break;
								}
							}
							if (layOffCard != null) {
								if (playVerbose || playRealGame)
                                    System.out.printf("Player %d lays off %s on %s.\n", opponent, layOffCard, layOffMeld);
                                gameInfo += String.format("Player %d lays off %s on %s.\n", opponent, layOffCard, layOffMeld);
								unmeldedCards.remove(layOffCard);
								layOffMeld.add(layOffCard);
								cardWasLaidOff = true;
								break;
							}
								
						}
					} while (cardWasLaidOff);
				}
				int opponentDeadwood = 0;
				for (Card card : unmeldedCards)
					opponentDeadwood += GinRummyUtil.getDeadwoodPoints(card);
				if (playVerbose || playRealGame)
                    System.out.printf("Player %d has %d deadwood with %s\n", opponent, opponentDeadwood, unmeldedCards); 
                gameInfo += String.format("Player %d has %d deadwood with %s\n", opponent, opponentDeadwood, unmeldedCards);
				// compare deadwood and compute new scores
				if (knockingDeadwood == 0) { // gin round win
					scores[currentPlayer] += GinRummyUtil.GIN_BONUS + opponentDeadwood;
					if (playVerbose || playRealGame)
                        System.out.printf("Player %d scores the gin bonus of %d plus opponent deadwood %d for %d total points.\n", currentPlayer, GinRummyUtil.GIN_BONUS, opponentDeadwood, GinRummyUtil.GIN_BONUS + opponentDeadwood); 
                    gameInfo += String.format("Player %d scores the gin bonus of %d plus opponent deadwood %d for %d total points.\n", currentPlayer, GinRummyUtil.GIN_BONUS, opponentDeadwood, GinRummyUtil.GIN_BONUS + opponentDeadwood);
				}
				else if (knockingDeadwood < opponentDeadwood) { // non-gin round win
					scores[currentPlayer] += opponentDeadwood - knockingDeadwood;
					if (playVerbose || playRealGame)
                        System.out.printf("Player %d scores the deadwood difference of %d.\n", currentPlayer, opponentDeadwood - knockingDeadwood); 
                    gameInfo += String.format("Player %d scores the deadwood difference of %d.\n", currentPlayer, opponentDeadwood - knockingDeadwood);
				}
				else { // undercut win for opponent
					scores[opponent] += GinRummyUtil.UNDERCUT_BONUS + knockingDeadwood - opponentDeadwood;
					if (playVerbose || playRealGame)
						System.out.printf("Player %d undercuts and scores the undercut bonus of %d plus deadwood difference of %d for %d total points.\n", opponent, GinRummyUtil.UNDERCUT_BONUS, knockingDeadwood - opponentDeadwood, GinRummyUtil.UNDERCUT_BONUS + knockingDeadwood - opponentDeadwood); 
                    gameInfo += String.format("Player %d undercuts and scores the undercut bonus of %d plus deadwood difference of %d for %d total points.\n", opponent, GinRummyUtil.UNDERCUT_BONUS, knockingDeadwood - opponentDeadwood, GinRummyUtil.UNDERCUT_BONUS + knockingDeadwood - opponentDeadwood);
                }
				startingPlayer = (startingPlayer == 0) ? 1 : 0; // starting player alternates
			}
			else { // If the round ends due to a two card draw pile with no knocking, the round is cancelled.
				if (playVerbose || playRealGame)
                    System.out.println("The draw pile was reduced to two cards without knocking, so the hand is cancelled.");
                gameInfo += String.format("The draw pile was reduced to two cards without knocking, so the hand is cancelled.");
            }
            
			// score reporting
			if (playVerbose || playRealGame) 
                System.out.printf("Player\tScore\n0\t%d\n1\t%d\n", scores[0], scores[1]);
            gameInfo += String.format("Player\tScore\n0\t%d\n1\t%d\n", scores[0], scores[1]);
			for (int i = 0; i < 2; i++) 
                players[i].reportScores(scores.clone());
            //((YourPlayer)players[0]).closeScanner();

            exportData("GameSimulator.txt");
            if(playRealGame){
                scanner = new Scanner(System.in);
                System.out.println("Do you want to get gameInfo for this game? [y/n]");
                String answer = "";
                if(scanner.hasNextLine()) answer = scanner.nextLine().trim().toLowerCase();
                while(!answer.equals("y") && !answer.equals("n")){
                    System.out.println("Enter 'y' for yes or 'n' for n");
                    answer = scanner.nextLine().trim().toLowerCase();
                }
                if(answer.equals("y")){
                    System.out.println("---------- Game Info ----------");
                    System.out.println(gameInfo);
                    System.out.println("---------- End ----------");
                }
            }
            gameInfoAL.add(gameInfo);
            gameInfo = "";
            
		}
		if (playVerbose || playRealGame)
            System.out.printf("Player %s wins.\n", scores[0] > scores[1] ? 0 : 1);
        gameInfo += String.format("Player %s wins.\n", scores[0] > scores[1] ? 0 : 1);
        gameInfoAL.add(gameInfo);

        System.out.println("Do you want to get gameInfo for the whole games? [y/n]");
        scanner = new Scanner(System.in);
        String answer = scanner.nextLine().trim().toLowerCase();
        while(!answer.equals("y") && !answer.equals("n")){
            System.out.println("Enter 'y' for yes or 'n' for n");
            answer = scanner.nextLine().trim().toLowerCase();
        }
        if(answer.equals("y")){
            for(String str: gameInfoAL){
                System.out.println(str);
            }
        }
        scanner.close();
		return scores[0] >= GinRummyUtil.GOAL_SCORE ? 0 : 1;
    }
    
    public void exportData(String filepath){
        try{
            FileWriter file = new FileWriter(filepath);
            BufferedWriter writer = new BufferedWriter(file);
            writer.write("Card_Ensemble_Player, Ensemble_Player_Hands, Your_Hands, State_Tracker");
            writer.newLine();
            for(int i = 0; i < allOppDiscards.size(); i++){
                writer.write(allOppDiscards.get(i) + ",");
                for(int j = 0; j < allOppHands.get(i).size(); j++){
                    writer.write(allOppHands.get(i).get(j).toString() + " ");
                }
                writer.write(",");
                for(int j = 0; j < allPlayerHands.get(i).size(); j++){
                    writer.write(allPlayerHands.get(i).get(j).toString() + " ");
                }
                writer.write(",");
                writer.write(allStateTracker.get(i) + "");
                writer.newLine();
            }
            writer.close();
        }
        catch (Exception e){
            System.out.println("Illegal file name");
        }
    }

    public static void main(String[] args){

        ParamList defaultParams = new ParamList(new double[]{}); // don't need any ensemble weights for YourPlayer
        YourPlayer player0 = new YourPlayer(defaultParams);
		
		double[] ensembleWeights = new double[]{1, 1, 1};
        ParamList params = new ParamList(ensembleWeights);
        params.set(ParamList.CH_ONEAWAY, 0.374);
        params.set(ParamList.CH_TWOAWAY, 0.149);
        params.set(ParamList.CH_SAMERANK, 0.477);
        params.set(ParamList.MC_SELF_LOW_OBTAINABILITY, 0.086);
        params.set(ParamList.MC_SELF_RATIO_FOR_UNKNOWN, 0.885);
        params.set(ParamList.MC_SELF_WRANK, 0.565);
        params.set(ParamList.MC_SELF_WRUN, 0.435);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, 0.9);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, 0.376);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, 0.9);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, 0.376);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, 0.304);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, 0.376);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, 0.9);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, 0.376);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, 0.9);
        params.enforceRestrictions();
        IndexEnsembleHandEvalPlayer player1 = new IndexEnsembleHandEvalPlayer(params, new DeadwoodHandEvaluator(), new ConvHandEvaluator(params), new MeldabilityHandEvaluator(params));
		// OurSimpleGinRummyPlayer player1 = new OurSimpleGinRummyPlayer();


        GameSimulator game = new GameSimulator(player0, player1);
        setPlayVerbose(false);
        setPlayRealGame(true);
        game.play();
    }
}