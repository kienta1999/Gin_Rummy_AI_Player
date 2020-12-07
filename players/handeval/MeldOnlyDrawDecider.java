package players.handeval;
import java.util.ArrayList;
import ginrummy.Card;
import players.StateTracker;
import players.ParamList;
import ginrummy.GinRummyUtil;
import util.OurUtil;
public class MeldOnlyDrawDecider implements DrawDecider{

    public static boolean TESTING = false;
    public boolean shouldDraw(ArrayList<Card> hand, Card faceUpCard, StateTracker myTracker){
		ArrayList<Card> newCards = (ArrayList<Card>)hand.clone();
        newCards.add(faceUpCard);
        ArrayList<ArrayList<ArrayList<Card>>> selfBestMeldSets = GinRummyUtil.cardsToBestMeldSets(newCards);
        if(selfBestMeldSets == null || selfBestMeldSets.isEmpty()){
            return false;
        }

        ArrayList<ArrayList<Card>> selfMeldSet = selfBestMeldSets.get(0);
        if(TESTING) System.out.println("melds: " + selfMeldSet);
        for(ArrayList<Card> meld: selfMeldSet){
			if (meld.contains(faceUpCard))
                return true;
        }   
        return false;   	
    }
    
    @Override
    public boolean hasDifferentParamList(ParamList otherParams) {
        return false;
    }

    public static void meldOnlyDrawDeciderTest1(){
       System.out.println("=======Test when player picks up the faceUp card======="); 
       ArrayList<Card> cards = OurUtil.makeHand(new String[] {"2C", "2S", "AC", "6S", "6D", "6C", "6H", "JH", "QH", "KH"});
       DrawDecider tester = new MeldOnlyDrawDecider();
       Card faceUp = Card.strCardMap.get("2D");
       boolean draw = tester.shouldDraw(cards, faceUp, null);
       System.out.println("Self Hand: " + cards);
       System.out.println("The face up card is "  + faceUp);
       System.out.println("Should the player draw the face up card?: " + draw);
    }

    public static void meldOnlyDrawDeciderTest2(){
        System.out.println("=======Test when player picks up the faceUp card======="); 
        ArrayList<Card> cards = OurUtil.makeHand(new String[] {"2C", "2S", "AC", "6S", "6D", "6C", "6H", "JH", "QH", "KH"});
        DrawDecider tester = new MeldOnlyDrawDecider();
        Card faceUp = Card.strCardMap.get("4C");
        boolean draw = tester.shouldDraw(cards, faceUp, null);
        System.out.println("Self Hand: " + cards);
        System.out.println("The face up card is "  + faceUp);
        System.out.println("Should the player draw the face up card?: " + draw);
    }

    public static void main(String[] args){
        meldOnlyDrawDeciderTest2();
    }
}
