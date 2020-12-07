package games;


import experiment.ExperimentalRuns;
import players.ParamList;
import players.SimpleFakeGinRummyPlayer;

import players.handeval.AceTwoBonusHandEvaluator;
import players.handeval.Choose10From11DrawDecider;
import players.handeval.ConvHandEvaluator;
import players.handeval.DeadwoodDrawDecider;
import players.handeval.DeadwoodHandEvaluator;
import players.handeval.EnsembleHandEvalPlayer;
import players.handeval.IndexEnsembleHandEvalPlayer;
import players.handeval.MeldabilityHandEvaluator;
import players.handeval.OneStageKnockDecider;
import players.handeval.LinearDeadwoodPenaltyHandEvaluator;
import players.handeval.OppCardsKnownDeadwoodPenaltyHandEvaluator;
import players.handeval.TwoStageKnockDecider;
import players.handeval.ScorePredictionKnockDecider;
import players.handeval.TwoStageDrawDecider;
import players.handeval.KnockOnGinKnockDecider;

import players.handeval.MeldOnlyDrawDecider;

public class Tournament {
    SimpleFakeGinRummyPlayer simplePlayer;
    EnsembleHandEvalPlayer knockOnGinPlayer;
    EnsembleHandEvalPlayer tunedTwoStagePlayer;

    int numGamePerPair;

    double resultSimpleVsKnockGin; //win % of Simple (against KnockGin)
    double resultKnockGinVsTwoStage; //win % of KnockGin (against TwoStage)
    double resultTwoStageVsSimple; //win % of TwoStage (against Simple)

    public Tournament(){
        simplePlayer = new SimpleFakeGinRummyPlayer(new ParamList(new double[]{}));

        knockOnGinPlayer = new EnsembleHandEvalPlayer(ParamList.getRandomParamList(1), new DeadwoodHandEvaluator());
        knockOnGinPlayer.setKnockDecider(new KnockOnGinKnockDecider() );
        knockOnGinPlayer.setDrawDecider(new MeldOnlyDrawDecider());

        ExperimentalRuns.fixTwoStageKnockDecider101();
        // paramOfTunedTwoStagePlayer.set(ParamList.TS_KNOCK_THRESHOLD_EARLY, 0);
        // paramOfTunedTwoStagePlayer.set(ParamList.TS_KNOCK_THRESHOLD_LATE, 0);
        // paramOfTunedTwoStagePlayer.set(ParamList.TS_KNOCK_MIDDLE, 6);
        ParamList.setFixedValue(ParamList.TS_DRAW_MIDDLE, 14);
        ParamList paramOfTunedTwoStagePlayer = ParamList.getRandomParamList(1);
        System.out.println(paramOfTunedTwoStagePlayer);
        tunedTwoStagePlayer = new IndexEnsembleHandEvalPlayer(paramOfTunedTwoStagePlayer, new DeadwoodHandEvaluator());
        tunedTwoStagePlayer.setKnockDecider(new TwoStageKnockDecider(paramOfTunedTwoStagePlayer) );
        tunedTwoStagePlayer.setDrawDecider(new MeldOnlyDrawDecider());
        tunedTwoStagePlayer.setDrawDecider(new TwoStageDrawDecider(paramOfTunedTwoStagePlayer));

        resultSimpleVsKnockGin = 0;
        resultKnockGinVsTwoStage = 0;
        resultTwoStageVsSimple = 0;

        numGamePerPair = 2000;

    }
    
    public Tournament(int numGamePerPair){
        this();
        this.numGamePerPair = numGamePerPair; 
    }

    public void playManyGames(){
        
        TestingGame gameSimpleVsKnockGin = new TestingGame(simplePlayer, knockOnGinPlayer);
        TestingGame gameKnockGinVsTwoStage = new TestingGame(knockOnGinPlayer, tunedTwoStagePlayer);
        TestingGame gameTwoStageVsSimple = new TestingGame(tunedTwoStagePlayer, simplePlayer);
        

        for(int i = 0; i < numGamePerPair; i++){
            resultSimpleVsKnockGin += gameSimpleVsKnockGin.play() == 0 ? 1 : 0;
            resultKnockGinVsTwoStage += gameKnockGinVsTwoStage.play() == 0 ? 1 : 0;
            resultTwoStageVsSimple += gameTwoStageVsSimple.play() == 0 ? 1 : 0;
        }
        resultSimpleVsKnockGin /= numGamePerPair;
        resultKnockGinVsTwoStage /= numGamePerPair;
        resultTwoStageVsSimple /= numGamePerPair;

    }

    public String toString(){
        String s = "";
        s += "Simple vs KnockOnGin: Simple win percentage: " + resultSimpleVsKnockGin + System.lineSeparator();
        s += "Simple vs KnockOnGin: KnockOnGin win percentage: " + (1 - resultSimpleVsKnockGin) + System.lineSeparator();

        s += "KnockOnGin vs tunedTwoStage: KnockOnGin win percentage: " + resultKnockGinVsTwoStage + System.lineSeparator();
        s += "KnockOnGin vs tunedTwoStage: tunedTwoStage win percentage: " + (1 - resultKnockGinVsTwoStage) + System.lineSeparator();

        s += "tunedTwoStage vs Simple: tunedTwoStage win percentage: " + resultTwoStageVsSimple + System.lineSeparator();
        s += "tunedTwoStage vs Simple: Simple win percentage: " + (1 - resultTwoStageVsSimple) + System.lineSeparator();

        return s;
    }

    public static void main(String[] args){
        int numGame = 2000;
        Tournament t = new Tournament(numGame);
        t.playManyGames();
        System.out.println(t);
    }

}