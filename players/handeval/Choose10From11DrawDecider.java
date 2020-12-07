package players.handeval;

import java.util.ArrayList;
import ginrummy.Card;
import players.StateTracker;
import players.ParamList;
import util.OurUtil;

public class Choose10From11DrawDecider implements DrawDecider{

    private EnsembleHandEvalPlayer player;

    private static boolean TESTING = false;

    public Choose10From11DrawDecider(EnsembleHandEvalPlayer p){
        this.player = p; 
    }

    @Override
    public boolean shouldDraw(ArrayList<Card> hand, Card faceUpCard, StateTracker myTracker){
        boolean willDraw;
        ArrayList<Card> candidateCards = new ArrayList<Card>(hand);
        candidateCards.add(faceUpCard);
        if (TESTING) System.out.println("Candidate cards are: " + candidateCards);
        Card discardChoice = player.choose10From11Cards(candidateCards);
        if(discardChoice == faceUpCard){
            if (TESTING) System.out.println("The face up card is not drawn");
            willDraw = false;
        }
        else{
            if (TESTING) System.out.println("The face up card is drawn");
            willDraw = true;
        }
        return willDraw;
    }

    @Override
    public boolean hasDifferentParamList(ParamList otherParams) {
        return false;
    }

    public static void test(){
        
        System.out.println("---------- All HandEvaluators -----------");
        double[] ensembleWeights = new double[] {0.15, 0.70, 0.10, 0.10, 0.10, 0.10};
        ParamList params = new ParamList(ensembleWeights);
        params.enforceRestrictions();
        LinearDeadwoodPenaltyHandEvaluator linearHe = new LinearDeadwoodPenaltyHandEvaluator(params);
        OppCardsKnownDeadwoodPenaltyHandEvaluator oppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params);
        EnsembleHandEvalPlayer p = new IndexEnsembleHandEvalPlayer(params, new MeldabilityHandEvaluator(params), 
                                                                            new DeadwoodHandEvaluator(), 
                                                                            new AceTwoBonusHandEvaluator(), 
                                                                            new ConvHandEvaluator(params), 
                                                                            linearHe,
                                                                            oppCardsHe);
        linearHe.setEnsemblePlayer(p);
        oppCardsHe.setEnsemblePlayer(p);
        p.getStateTracker().setToHardcodedStateTracker1();
        Choose10From11DrawDecider dd = new Choose10From11DrawDecider(p);
        ArrayList<Card> hand = p.getStateTracker().getSelfHandForHardcodedStateTracker1();
        Card faceUp = Card.strCardMap.get("7S");
        System.out.println("Hand: " + hand + "\nFaceUp: " + faceUp);
        boolean willDraw = dd.shouldDraw(hand, faceUp, null);
        System.out.println("willDraw: " + willDraw);

        System.out.println("---------- Only Deadwood Evaluator ---------");
        params = new ParamList(new double[]{1});
        params.enforceRestrictions();
        p = new EnsembleHandEvalPlayer(params, new DeadwoodHandEvaluator());
        p.getStateTracker().setToHardcodedStateTracker2();
        dd = new Choose10From11DrawDecider(p);
        hand = p.getStateTracker().getSelfHandForHardcodedStateTracker2();
        faceUp = Card.strCardMap.get("8C");
        System.out.println("Hand: " + hand + "\n FaceUp: " + faceUp);
        willDraw = dd.shouldDraw(hand, faceUp, null);
        System.out.println("willDraw: " + willDraw);
    }

    public static void main(String[] args){
        test();
    }
}