package players;

import ginrummy.Card;
import players.handeval.IndexEnsembleHandEvalPlayer;

import players.handeval.DeadwoodHandEvaluator;
import players.handeval.EnsembleHandEvalPlayer;
import players.handeval.ConvHandEvaluator;
import players.handeval.MeldabilityHandEvaluator;
import players.handeval.LinearDeadwoodPenaltyHandEvaluator;
import players.handeval.OppCardsKnownDeadwoodPenaltyHandEvaluator;
import players.handeval.AceTwoBonusHandEvaluator;

import players.handeval.TwoStageKnockDecider;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import ga.GATuner;
import games.TestingGame;

public class DoublePlayer extends SimpleFakeGinRummyPlayer{

    protected static final boolean TESTING = true;

    ////set the variable to true to write data to a file
    protected static final boolean WRITE_DATA_TO_FILE = true;

    private static int NUM_OF_EVALUATORS = 6;

    // ParamList params;

    SimpleFakeGinRummyPlayer pSimple;
    IndexEnsembleHandEvalPlayer pOther;
    // EnsembleHandEvalPlayer pOther = new EnsembleHandPlayer(3, params)

    SimpleFakeGinRummyPlayer opponent;
    
    private int countSameDiscardChoice;
    private int countDiffDiscardChoice;

    //contain the data of discard - to write data to file
    private ArrayList<String> allStateTracker;
    private ArrayList<Card> allCardSimple;
    private ArrayList<Card> allCardOther;
    private ArrayList<ArrayList<Card>> allPlayerHands;
    private ArrayList<ArrayList<Card>> allOppHands;
    

    // public DoublePlayer(SimpleFakeGinRummyPlayer opponent){
    //     this(opponent, new ParamList(GATuner.NUM_OF_EVALUATORS));
    // }

    public DoublePlayer(SimpleFakeGinRummyPlayer opponent, ParamList params) {
        super(params);

        this.opponent = opponent;
        pSimple = new SimpleFakeGinRummyPlayer(new ParamList(new double[]{}));

        // double[] ensembleWeights = new double[] {params.getEns

        LinearDeadwoodPenaltyHandEvaluator linearHe = new LinearDeadwoodPenaltyHandEvaluator(params);
        OppCardsKnownDeadwoodPenaltyHandEvaluator oppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params);
        pOther = new IndexEnsembleHandEvalPlayer(params, new MeldabilityHandEvaluator(params), 
                                                                                 new DeadwoodHandEvaluator(), 
                                                                                 new AceTwoBonusHandEvaluator(), 
                                                                                 new ConvHandEvaluator(params), 
                                                                                //  new MultiOppHandMeldabilityEvaluator(params),
                                                                                 linearHe,
                                                                                 oppCardsHe);
        linearHe.setEnsemblePlayer(pOther);
        oppCardsHe.setEnsemblePlayer(pOther);

        // p0.setKnockDecider(new OneStageKnockDecider(params));
        pOther.setKnockDecider(new TwoStageKnockDecider(params));

        allStateTracker = new ArrayList<>();
        allCardSimple = new ArrayList<>();
        allCardOther = new ArrayList<>();
        allPlayerHands = new ArrayList<>(); 
        allOppHands = new ArrayList<>();

        // pOther.addHandEvaluator(new DeadwoodHandEvaluator(), params.getEnsembleWeight(0));
        // pOther.addHandEvaluator(new ConvHandEvaluator(params), params.getEnsembleWeight(1));
        // pOther.addHandEvaluator(new MeldabilityHandEvaluator(params), params.getEnsembleWeight(2));
    }

    @Override
    public void startGame(int playerNum, int startingPlayerNum, Card[] cards){
        // System.out.println("Num hand eval: " + pOther.getNumOfEvals());
        super.startGame(playerNum, startingPlayerNum, cards);
        
        pSimple.startGame(playerNum, startingPlayerNum, cards);
        pOther.startGame(playerNum, startingPlayerNum, cards);
        //update for both player

        // pOther.getStateTracker().updateFromStartGame(playerNum, startingPlayerNum, cards);
        countSameDiscardChoice = 0;
        countDiffDiscardChoice = 0;
        
        if(TESTING){
            System.out.println("We are player: " + playerNum);
        }
    }

    @Override
    public boolean willDrawFaceUpCard(Card card) {
        boolean willDraw = pOther.willDrawFaceUpCard(card);

        // Now, tell pSimple and super what decision pOhter made, so that their state trackers can be updated correctly
        // super.willDrawFaceUpCard(card); // can't just do this, because we need super's state tracker to be updated with the correct willdraw
        // pSimple.willDrawFaceUpCard(card); // can't do this, because we need pSimple's state tracker to be updated with the correct willdraw

        this.myStateTracker.updateFromWillDrawFaceUpCard(card, willDraw); // updating the DoublePlayer ("super")
        pSimple.getStateTracker().updateFromWillDrawFaceUpCard(card, willDraw);

        return willDraw;
    }

    public static ArrayList<Card> cloneArrayListOfCard(ArrayList<Card> list){
        ArrayList<Card> newList = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            newList.add(list.get(i));
        }
        return newList;
    }

    @Override
    public Card getDiscard(){
        Card cardSimple = pSimple.getDiscard();
        Card cardOther = pOther.getDiscard();

        if(cardSimple != cardOther){
            countDiffDiscardChoice++;
            if(TESTING){
                System.out.println("Discard choice of Simple Player: " + cardSimple);
                System.out.println("Discard chocie of Index Ensemble Player: " + cardOther);
            }
            if(WRITE_DATA_TO_FILE){
                try{
                    allStateTracker.add(StateTracker.serializeStateTrackerToString(pOther.getStateTracker()) );
                    allCardSimple.add(cardSimple);
                    allCardOther.add(cardOther);
                    allPlayerHands.add(cloneArrayListOfCard(this.showCards()));
                    allOppHands.add(cloneArrayListOfCard(opponent.showCards()));
                }
                catch(Exception e){
                    System.out.println("State Tracker not available");
                }
                
                
            }
        }
        else
            countSameDiscardChoice++;
        if(TESTING){
            System.out.println("Number of diffrent discard choice: " + countDiffDiscardChoice);
            System.out.println("Number of same discard choice: " + countSameDiscardChoice);
        }
        return cardOther;
    }

    @Override
    public void reportDraw(int playerNum, Card drawnCard){
        //update the game for both player
        pSimple.reportDraw(playerNum, drawnCard);
        pOther.reportDraw(playerNum, drawnCard);

        super.reportDraw(playerNum, drawnCard);
    }

    @Override
    public void reportDiscard(int playerNum, Card discardedCard){
        super.reportDiscard(playerNum, discardedCard);

        //update for both player
        pSimple.reportDiscard(playerNum, discardedCard);
        pOther.reportDiscard(playerNum, discardedCard);
        // pOther.getStateTracker().updateFromReportDiscard(playerNum, discardedCard);
    }

    public String toString(){
        return pOther.getStateTracker().toString();
    }

    public int getCountSameDiscardChoice(){
        return countSameDiscardChoice;
    }

    public int getCountDiffDiscardChoice(){
        return countDiffDiscardChoice;
    }

      //export data to a filepath
    public void exportData(String filepath){
        try{
            FileWriter file = new FileWriter(filepath);
            BufferedWriter writer = new BufferedWriter(file);
            writer.write("Card_Simple_Player, Card_Ensemble_Player, All_player_card, All_opponent_card, State_Tracker");
            writer.newLine();
            for(int i = 0; i < allCardSimple.size(); i++){
                writer.write(allCardSimple.get(i) + ",");
                writer.write(allCardOther.get(i) + ",");
                for(int j = 0; j < allPlayerHands.get(i).size(); j++){
                    writer.write(allPlayerHands.get(i).get(j).toString() + " ");
                }
                writer.write(",");
                for(int j = 0; j < allOppHands.get(i).size(); j++){
                    writer.write(allOppHands.get(i).get(j).toString() + " ");
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

    //let the DoublePlayer plays with SimplePlayer to generate the needed data
    public static void playGame(int numGame, boolean playVerbose, ParamList params){
        SimpleFakeGinRummyPlayer simplePlayer = new SimpleFakeGinRummyPlayer(params);
        DoublePlayer doublePlayer = new DoublePlayer(simplePlayer, params);
        TestingGame game = new TestingGame(simplePlayer, doublePlayer);
        TestingGame.setPlayVerbose(true);
        int numWin = 0;
        for(int i = 0; i < numGame; i++){
            numWin += game.play();
        }
        System.out.println("Win " + numWin + " game out of " + numGame + " ratio: " + (double)numWin / numGame);
        if(WRITE_DATA_TO_FILE){
            doublePlayer.exportData("doubleplayer.txt");
        }
    }

    public static void main(String[] args){
        ParamList params = new ParamList(new double[] {0.15, 0.70, 0.10, 0.10, 0.10, 0.10});
        params.set(ParamList.CH_ONEAWAY, 0.374);
        params.set(ParamList.CH_TWOAWAY, 0.149);
        params.set(ParamList.CH_SAMERANK, 0.477);
        params.set(ParamList.MC_SELF_LOW_OBTAINABILITY, 0.086);
        params.set(ParamList.MC_SELF_RATIO_FOR_UNKNOWN, 0.885);
        params.set(ParamList.MC_SELF_WRANK, 0.565);
        params.set(ParamList.MC_SELF_WRUN, 0.435);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, 0.9);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, 0.25);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, 0.9);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, 0.25);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, 0.18);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, 0.25);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, 0.9);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, 0.25);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, 0.9);
       // weight for MeldabilityHandEvaluator
        DoublePlayer.playGame(1, true, params);
    }
}