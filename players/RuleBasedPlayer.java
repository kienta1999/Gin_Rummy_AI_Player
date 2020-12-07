package players;

import java.util.ArrayList;

import ginrummy.Card;
import ginrummy.GinRummyUtil;

// import java.util.Random;
import java.util.*;
import java.lang.Math;



/**
 * Implements a RuleBasedPlyaer that follows human rule strategy
 * 
 * @author Kien and Moon
 * @version 1.0
 */

public class RuleBasedPlayer extends OurSimpleGinRummyPlayer {

	//count the number of cards drawn
    private int numDraw; 
    private int numDrawnAll;
	
	//get all the cards discarded by opponent
	ArrayList<Card> cardsDiscardedByOpponent = new ArrayList<>();
	
	//get all the cards discarded by this player
	ArrayList<Card> cardsDiscardedByPlayer = new ArrayList<>();

	@Override
	public void startGame(int playerNum, int startingPlayerNum, Card[] cards) {
        super.startGame(playerNum, startingPlayerNum, cards);
        numDraw = 0;
        numDrawnAll = 0;
	}

	@Override
	public boolean willDrawFaceUpCard(Card card){
		cardsDiscardedByOpponent.add(card);
		return super.willDrawFaceUpCard(card);
	}

	@Override
	public void reportDraw(int playerNum, Card drawnCard) {
		// Ignore other player draws.  Add to cards if playerNum is this player.
		super.reportDraw(playerNum, drawnCard);
		if (playerNum == this.playerNum) {
            numDraw++;
        }
        numDrawnAll++;
	}
	
    public static boolean potentialMeld(Card c1, Card c2){
        return (c1.getRank() == c2.getRank()) || 
        ((c1.getSuit() == c2.getSuit() && 
        Math.abs(c1.getRank() - c2.getRank()) <= 2));
    }

    //return the Cards that can form triangle - high chance to form meld
	//example 7S 7D 8D, 10C 10D 8D
	public Set<Card> getcardsInMeld(){
		//return all the cards that are in meld
		Set<Card> cardsInMeld = new HashSet<>();
		if(GinRummyUtil.cardsToBestMeldSets(cards).size() > 0){
            for(ArrayList<Card> card: GinRummyUtil.cardsToBestMeldSets(cards).get(0)){
                for(Card c: card){
                    cardsInMeld.add(c);
                }
            }
        }
		return cardsInMeld;
    }
    
    public Set<Card> getCardTriangles(){
        //public ArrayList<Card> cards = new ArrayList<Card>();
        Set<Card> triangle = new HashSet<>();
        Set<Card> cardsInMeld = getcardsInMeld();
		
        //The remainingCards that are not in meld - potential to form triangle
        ArrayList<Card> remainingCards = (ArrayList<Card>) cards.clone();
        remainingCards.removeAll(cardsInMeld);
        //find triangle in these cards
        if(remainingCards.size() < 3)
			return triangle;
        for(int i = 0; i < remainingCards.size(); i++){
            for(int j = i + 1; j < remainingCards.size(); j++){
                for(int k = j + 1; k < remainingCards.size(); k++){
                    Card c1 = remainingCards.get(i);
                    Card c2 = remainingCards.get(j);
                    Card c3 = remainingCards.get(k);
                    if( (potentialMeld(c1, c2) && potentialMeld(c1, c3)) || 
                    (potentialMeld(c1, c2) && potentialMeld(c2, c3)) ||
                    (potentialMeld(c1, c3) && potentialMeld(c2, c3)) ){
                        triangle.add(c1);
                        triangle.add(c2);
                        triangle.add(c3);
                    }
                }
            }
        }
        return triangle;
    }
    
    public static boolean isHighCard(Card c){
        return c.getRank() >= 9; //10 J Q K 
    }

    //Keep high card that can be meld in the first rows, ex 10C 10D since opponent may discard them
    public Set<Card> getHighCardWithPotentialBecomeMeld(){
        Set<Card> highCard = new HashSet<>();
		Set<Card> cardsInMeld = getcardsInMeld();
		
        //The remainingCards that are not in meld - potential to form high cards
		ArrayList<Card> remainingCards = (ArrayList<Card>) cards.clone();
		remainingCards.removeAll(cardsInMeld);
        for(int i = 0; i < remainingCards.size(); i++){
            for(int j = i + 1; j < remainingCards.size(); j++){
                Card c1 = remainingCards.get(i);
                Card c2 = remainingCards.get(j);
                if(isHighCard(c1) && isHighCard(c2)){
                    if(potentialMeld(c1, c2)){
                        highCard.add(c1);
                        highCard.add(c2);
                    }
                }
            }
        }
        return highCard;
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public Card getDiscard() {
        // Discard a random card (not just drawn face up) leaving minimal deadwood points.
        //Keep the high cards with potential to become melds and triangles
		int minDeadwood = Integer.MAX_VALUE;
		ArrayList<Card> candidateCards = new ArrayList<Card>();
		for (Card card : cards) {
			// Cannot draw and discard face up card.
			if (card == drawnCard && drawnCard == faceUpCard)
				continue;
			// Disallow repeat of draw and discard.
			ArrayList<Card> drawDiscard = new ArrayList<Card>();
			drawDiscard.add(drawnCard);
			drawDiscard.add(card);
			if (drawDiscardBitstrings.contains(GinRummyUtil.cardsToBitstring(drawDiscard)))
				continue;
			
			ArrayList<Card> remainingCards = (ArrayList<Card>) cards.clone();
            remainingCards.remove(card);
            //8 is an arbitary number - if player played less than 8 rounds then keep the high cards and triangles 
            if(this.numDraw <= 8){
                //Keep the high card with pairs + triangle in the begining 
                remainingCards.removeAll(getHighCardWithPotentialBecomeMeld());
                remainingCards.removeAll(getCardTriangles());
                if(remainingCards.isEmpty()){
                    remainingCards.addAll(getHighCardWithPotentialBecomeMeld());
                }
                if(remainingCards.isEmpty()){
                    remainingCards.addAll(getCardTriangles());
                }
            }
			ArrayList<ArrayList<ArrayList<Card>>> bestMeldSets = GinRummyUtil.cardsToBestMeldSets(remainingCards);
			int deadwood = bestMeldSets.isEmpty() ? GinRummyUtil.getDeadwoodPoints(remainingCards) : GinRummyUtil.getDeadwoodPoints(bestMeldSets.get(0), remainingCards);
			if (deadwood <= minDeadwood) {
				if (deadwood < minDeadwood) {
					minDeadwood = deadwood;
					candidateCards.clear();
				}
				candidateCards.add(card);
			}
		}
		// System.out.println("candidateCards to remove: " + candidateCards);
		Card discard = candidateCards.get(random.nextInt(candidateCards.size()));
		// Prevent future repeat of draw, discard pair.
		ArrayList<Card> drawDiscard = new ArrayList<Card>();
		drawDiscard.add(drawnCard);
		drawDiscard.add(discard);
		drawDiscardBitstrings.add(GinRummyUtil.cardsToBitstring(drawDiscard));
		return discard;
	}
	
	@Override
	public ArrayList<ArrayList<Card>> getFinalMelds() {
        // need a parameter to weigh our hands based on deckNum
		int threthold;
        int deckNum = getDeckNum(this.numDrawnAll);
        // System.out.println("Deck Num is " + deckNum);
        if (deckNum >= 32/2){
            threthold = GinRummyUtil.MAX_DEADWOOD;
        }
        else{
            threthold = GinRummyUtil.MAX_DEADWOOD - (deckNum/2 - 7);
        }
		// Check if deadwood of maximal meld is low enough to go out. 
		ArrayList<ArrayList<ArrayList<Card>>> bestMeldSets = GinRummyUtil.cardsToBestMeldSets(cards);
		if (!opponentKnocked && (bestMeldSets.isEmpty() || GinRummyUtil.getDeadwoodPoints(bestMeldSets.get(0), cards) > Math.min(GinRummyUtil.MAX_DEADWOOD, threthold)))
			return null;
		return bestMeldSets.isEmpty() ? new ArrayList<ArrayList<Card>>() : bestMeldSets.get(random.nextInt(bestMeldSets.size()));
    }	
    
    /**
	 * Get the number of cards left in the draw pile
	 * @param numDrawnAll the number of cards drawn by both of the players
	 * @return the number of cards left in the draw pile
	 */
    public int getDeckNum(int numDrawnAll){
        int init_deckNum = Card.NUM_CARDS - 10 * 2;
        int deckNum = init_deckNum - numDrawnAll;
        return deckNum;
    }

}