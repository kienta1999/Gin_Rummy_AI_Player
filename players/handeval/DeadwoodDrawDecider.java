package players.handeval;

import java.util.ArrayList;
import ginrummy.Card;
import players.StateTracker;
import players.ParamList;
import ginrummy.GinRummyUtil;
import util.OurUtil;


public class DeadwoodDrawDecider implements DrawDecider{

   public static final boolean TESTING = false;
    // public

    public boolean hasDifferentParamList(ParamList otherParams){
        return false;
    }

    public boolean shouldDraw(ArrayList<Card> hand, Card faceUpCard, StateTracker myTracker){
        boolean willDraw;
        ArrayList<Card> candidateCards = new ArrayList<Card>(hand);
        candidateCards.add(faceUpCard);
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
        if(candidateCards.get(indexOfCardMinDeadwoodValue) == faceUpCard){
            willDraw = false;
        }
        else{
            willDraw = true;
        }
        
        return willDraw;
    }

    public static void test(){
        DeadwoodDrawDecider dwDecider = new DeadwoodDrawDecider();

        System.out.println("------------------------------Player will draw the card------------------------------");
        ArrayList<Card> hand = OurUtil.makeHand(new String[]{"2C", "3C", "4C", "5S", "6S", "6D", "6C", "6H", "JH", "QH"});
        Card faceUpCard = OurUtil.getCard(8, 0);
        StateTracker myTracker = null;

        ArrayList<ArrayList<ArrayList<Card>>> selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(hand);
        ArrayList<ArrayList<Card>> selfMeldSet = selfBestMeldSets.isEmpty() ? new ArrayList<ArrayList<Card>>() : selfBestMeldSets.get(0);
        int deadwood = GinRummyUtil.getDeadwoodPoints(selfMeldSet, hand);
        System.out.println("Deadwood before drawing the card: " + deadwood);
        System.out.println("Player's meld set: " + selfMeldSet);
        System.out.println("Unmelded card: " + OurUtil.getMeldedAndUnmeldedCards(hand)[1]);
        System.out.println("Face up card: " + faceUpCard);

        boolean willDarw = dwDecider.shouldDraw(hand, faceUpCard, myTracker);
        System.out.println("Should the player draw the card? " + willDarw);


        System.out.println("------------------------------Player will not draw the card------------------------------");
        hand = OurUtil.makeHand(new String[]{"2C", "3C", "4C", "5S", "6S", "6D", "6C", "6H", "7H", "8H"});
        faceUpCard = OurUtil.getCard(8, 0);
        myTracker = null;

        selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(hand);
        selfMeldSet = selfBestMeldSets.isEmpty() ? new ArrayList<ArrayList<Card>>() : selfBestMeldSets.get(0);
        deadwood = GinRummyUtil.getDeadwoodPoints(selfMeldSet, hand);
        System.out.println("Deadwood before drawing the card: " + deadwood);
        System.out.println("Player's meld set: " + selfMeldSet);
        System.out.println("Unmelded card: " + OurUtil.getMeldedAndUnmeldedCards(hand)[1]);
        System.out.println("Face up card: " + faceUpCard);

        willDarw = dwDecider.shouldDraw(hand, faceUpCard, myTracker);
        System.out.println("Should the player draw the card? " + willDarw);
    }
    public static void main(String[] args){
        test();
    }
}