package experiment;
// import java.util.ArrayList;

import players.ParamList;
import players.SimpleFakeGinRummyPlayer;
import players.StateTracker;
import players.TunableStateTracker;
import players.handeval.ConvHandEvaluator;
import players.handeval.EnsembleHandEvalPlayer;
import players.handeval.IndexEnsembleHandEvalPlayer;
import players.handeval.KnockOnGinKnockDecider;
import players.handeval.LinearDeadwoodPenaltyHandEvaluator;
import players.handeval.MeldOnlyDrawDecider;
import players.handeval.MeldabilityHandEvaluator;
import players.handeval.OneStageKnockDecider;
import players.handeval.OppCardsKnownDeadwoodPenaltyHandEvaluator;
import players.handeval.TwoStageDrawDecider;
import players.handeval.DeadwoodHandEvaluator;
import players.handeval.AceTwoBonusHandEvaluator;
import players.handeval.TwoStageKnockDecider;
import players.handeval.DeadwoodDrawDecider;
import java.util.stream.Collectors;
import java.util.Collections;

import ga.StateTrackerTuner;
import games.TestingGame;

import java.util.Arrays;
import java.util.ArrayList;

import ga.GATuner;

public class ExperimentalRuns {

    // use this as a template or test run 
    public static void template(){
        System.out.println("=============== run### ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 20;
        int numGenerations = 7;
        int gamesPerIndividual = 500;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_INDEX_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        int tournamentSize = popSize* 3 / 100;

        boolean wantCrossover = true;

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
                                    mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
                                    oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }
    
    // Self: EnsemblePlayer (KnockOnGin, DeadwoodDraw)
    // Opp: SimplePlayer (none, none)
    public static void run001(){
        System.out.println("=============== run001 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        
        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_KNOCK_ON_GIN_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;

        boolean wantCrossover = true;
        int tournamentSize = popSize* 3 / 100;


        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);

    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Self: EnsemblePlayer (KnockOnGin, DeadwoodDraw)
    // Opp: SimplePlayer (none, none)
    public static void run001Big(){
        System.out.println("=============== run001Big ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        
        int popSize = 500;
        int numGenerations = 20;
        int gamesPerIndividual = 2000;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_KNOCK_ON_GIN_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;

        boolean wantCrossover = true;
        int tournamentSize = popSize* 3 / 100;

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    public static void run001A(){
        System.out.println("=============== run001A ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        
        int popSize = 20;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        int tournamentSize = popSize* 3 / 100;
        
        boolean wantCrossover = true;
        

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);

    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    public static void run001B(){
        System.out.println("=============== run001B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        
        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        int tournamentSize = popSize* 3 / 100;


        boolean wantCrossover = true;

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);

    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Self: IndexPlayer (KnockOnGin, DeadwoodDraw)
    // Opp: SimplePlayer (none, none)
    public static void run002(){
        System.out.println("=============== run002 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_INDEX_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_KNOCK_ON_GIN_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        int tournamentSize = popSize* 3 / 100;

        
        boolean wantCrossover = true;

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Self: IndexPlayer (KnockOnGin, DeadwoodDraw)
    // Opp: SimplePlayer (none, none)
    public static void run002Big(){
        System.out.println("=============== run002Big ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 500;
        int numGenerations = 20;
        int gamesPerIndividual = 2000;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_INDEX_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_KNOCK_ON_GIN_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        int tournamentSize = popSize* 3 / 100;

        
        boolean wantCrossover = true;

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Self: IndexPlayer (KnockOnGin, DeadwoodDraw)
    // Opp: SimplePlayer (none, none)
    public static void run002A(){
        System.out.println("=============== run002A ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_INDEX_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        int tournamentSize = popSize* 3 / 100;


        boolean wantCrossover = true;

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Test for KnockDeciders
    // Self: EnsemblePlayer (OneStage, DeadwoodDraw)
    // Opp: KnockOnGin (none, MeldOnly) --- basically the same as KnockOnGinPlayer
    public static void run003(){
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;
        
        boolean wantCrossover = true;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_ONE_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        int tournamentSize = popSize* 3 / 100;


        fixStateTracker010();

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Test for KnockDeciders
    // Self: IndexEnsemblePlayer (OneStage, DeadwoodDraw)
    // Opp: KnockOnGin(none, MeldOnly) --- basically the same as KnockOnGinPlayer
    public static void run004(){
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_INDEX_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_ONE_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;

        boolean wantCrossover = true;
        int tournamentSize = popSize* 3 / 100;

        
        fixStateTracker010();

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Test for KnockDeciders
    // Self: EnsemblePlayer (TwoStage, DeadwoodDraw)
    // Opp: KnockOnGin(none, MeldOnly) --- basically the same as KnockOnGinPlayer
    public static void run005(){
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;
        boolean wantCrossover = true;
        int tournamentSize = popSize * 3 / 100;


        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        
        fixStateTracker010();

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Test for KnockDeciders
    // Self: IndexEnsemblePlayer (TwoStage, DeadwoodDraw)
    // Opp: KnockOnGin(none, MeldOnly) --- basically the same as KnockOnGinPlayer
    public static void run006(){
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;
        boolean wantCrossover = true;
        int tournamentSize = popSize * 3 / 100;


        int selfFlag = GATuner.SELF_IS_INDEX_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        
        fixStateTracker010();

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Test for KnockDeciders
    // Self: EnsemblePlayer (ScorePrediction, DeadwoodDraw)
    // Opp: KnockOnGin(none, MeldOnly) --- basically the same as KnockOnGinPlayer
    public static void run007(){
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;
        boolean wantCrossover = true;
        int tournamentSize = popSize * 3 / 100;


        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_SCORE_PREDICTION_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        
        fixStateTracker010();

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Test for KnockDeciders
    // Self: IndexEnsemblePlayer (ScorePrediction, DeadwoodDraw)
    // Opp: KnockOnGin(none, MeldOnly) --- basically the same as KnockOnGinPlayer
    public static void run008(){
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;
        boolean wantCrossover = true;
        int tournamentSize = popSize * 3 / 100;


        int selfFlag = GATuner.SELF_IS_INDEX_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_SCORE_PREDICTION_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;
    
        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        
        fixStateTracker010();

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual,
        numOfEvaluators, mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Test for IndexEnsemblePlayer
    // Self: IndexEnsemblePlayer (KnockOnGinKnockDecider, DeadwoodDraw)
    // Opp: IndexEnsemblePlayer(KnockOnGinKnockDecider, DeadwoodDraw)
    public static void run009(){
        double start, elapsed;
        start = System.currentTimeMillis();
        
        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;
        boolean wantCrossover = true;
        int tournamentSize = popSize * 3 / 100;


        int selfFlag = GATuner.SELF_IS_INDEX_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_KNOCK_ON_GIN_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;
    
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual,
        numOfEvaluators, mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTracker Tuner
    // Uses Error method and checks at the end of EVERY turn
    // Opp: KnockOnGin (none, MeldOnlyDraw) 
    public static void run010(){
        System.out.println("=============== run010 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int tournamentSize = popSize* 3 / 100;
        double mutationChance = 0.02;

        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;

        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_ERROR);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.EVERY_TURN);

        ParamList bestParams = StateTrackerTuner.geneticAlgorithmError(popSize, numGenerations, gamesPerIndividual, tournamentSize, mutationChance, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        System.out.print("Print only parameters related to StateTracker: ");
        bestParams.printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // fix parameters for StateTracker
    // values come from run010
    public static void fixStateTracker010(){
        double[] parameters = getStateTrackerParams010();
        for(int i = ParamList.ST_START; i <= ParamList.ST_END; i++){
            ParamList.setFixedValue(i, parameters[i-ParamList.ST_START]);
        }
    }

    public static double[] getStateTrackerParams010(){
        return new double[]{0.45248472747997903, 0.098306198595558, 0.948208493865991, 0.20539377959960048, 0.025488707655334286, 0.8766671223274256, 0.2550980350633101, 0.0390763429567218, 0.6062397813343741};
    }

    // StateTracker Tuner
    // Uses Count method and checks at the end of EVERY turn
    // Opp: KnockOnGin (none, MeldOnlyDraw) 
    public static void run011(){
        System.out.println("=============== run011 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int tournamentSize = popSize* 3 / 100;
        double mutationChance = 0.02;


        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;

        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_COUNT);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.EVERY_TURN);

        ParamList bestParams = StateTrackerTuner.geneticAlgorithmCount(popSize, numGenerations, gamesPerIndividual, tournamentSize, mutationChance, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        System.out.print("Print only parameters related to StateTracker: ");
        bestParams.printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // fix parameters for StateTracker
    // values come from run011
    public static void fixStateTracker011(){
        double[] parameters = getStateTrackerParams011();
        for(int i = ParamList.ST_START; i <= ParamList.ST_END; i++){
            ParamList.setFixedValue(i, parameters[i-ParamList.ST_START]);
        }
        // 0.45248472747997903, 0.098306198595558, 0.948208493865991
        // 0.20539377959960048, 0.025488707655334286, 0.8766671223274256
        // 0.2550980350633101, 0.0390763429567218, 0.6062397813343741
        
        // ParamList.setFixedValue(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, 0.45248472747997903);
        // ParamList.setFixedValue(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, 0.098306198595558);
        // ParamList.setFixedValue(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, 0.948208493865991);
        // ParamList.setFixedValue(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, 0.20539377959960048);
        // ParamList.setFixedValue(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, 0.025488707655334286);
        // ParamList.setFixedValue(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, 0.8766671223274256);
        // ParamList.setFixedValue(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, 0.2550980350633101);
        // ParamList.setFixedValue(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, 0.0390763429567218);
        // ParamList.setFixedValue(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, 0.6062397813343741);
    }

    public static double[] getStateTrackerParams011(){
        return new double[]{0.45248472747997903, 0.098306198595558, 0.948208493865991, 0.20539377959960048, 0.025488707655334286, 0.8766671223274256, 0.2550980350633101, 0.0390763429567218, 0.6062397813343741};
    }


    // Test for EnsemblePlayer - one hand evaluator
    // Self: EnsemblePlayer (KnockOnGinKnockDecider, DeadwoodDraw)
    // Opp: SimplePlayer
    public static void run012(){
        double start, elapsed;
        start = System.currentTimeMillis();
        
        int popSize = 30;
        int numGenerations = 5;
        int gamesPerIndividual = 1000;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;
        boolean wantCrossover = true;
        int tournamentSize = popSize* 3 / 100;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;
        for(int i = 0; i < numOfEvaluators; i++){
            ParamList.setFixedValue(ParamList.NUM_NONENSEMBLE_PARAMS + i, 0);
        }
        ParamList.setFixedValue(ParamList.NUM_NONENSEMBLE_PARAMS, 1);
        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        
        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual,
        numOfEvaluators, mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTracker Tuner
    // Uses Error method and checks at the end of turn 10
    // Opp: KnockOnGin (none, MeldOnlyDraw) 
    public static void run013(){
        System.out.println("=============== run013 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 2000;
        int tournamentSize = popSize* 3 / 100;
        double mutationChance = 0.02;

        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;

        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_ERROR);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.AT_ONE_TURN);
        TunableStateTracker.setTurnToCheck(10);

        ParamList bestParams = StateTrackerTuner.geneticAlgorithmError(popSize, numGenerations, gamesPerIndividual, tournamentSize, mutationChance, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        System.out.print("Print only parameters related to StateTracker: ");
        bestParams.printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // fix parameters for StateTracker
    // values come from run013
    public static void fixStateTracker013(){
        double[] parameters = getStateTrackerParams013();
        for(int i = ParamList.ST_START; i <= ParamList.ST_END; i++){
            ParamList.setFixedValue(i, parameters[i-ParamList.ST_START]);
        }
    }

    public static double[] getStateTrackerParams013(){
        return new double[]{0.9725055205494261, 0.7662127382944128, 0.8995356512946784, 0.747589405876202, 0.3702202323496384, 0.12045202240233688, 0.9663655782051961, 0.3973603321389466, 0.09871222578386218};
    }

    public static double[] getStateTrackerParams013B(){
        return new double[]{0.9389203666870006, 0.64179423649239, 0.9850498048185996, 0.13149268999224983, 0.08266513959017774, 0.9793123126805426, 0.17791827265643056, 0.017778724745283747, 0.9985026930234445};
    }

    // StateTracker Tuner
    // Uses Count method and checks at the end of turn 10
    // Opp: KnockOnGin (none, MeldOnlyDraw) 
    public static void run014(){
        System.out.println("=============== run014 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 2000;
        int tournamentSize = popSize* 3 / 100;
        double mutationChance = 0.02;

        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;

        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_COUNT);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.AT_ONE_TURN);
        TunableStateTracker.setTurnToCheck(10);

        ParamList bestParams = StateTrackerTuner.geneticAlgorithmCount(popSize, numGenerations, gamesPerIndividual, tournamentSize, mutationChance, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        System.out.print("Print only parameters related to StateTracker: ");
        bestParams.printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // fix parameters for StateTracker
    // values come from run014
    public static void fixStateTracker014(){
        double[] parameters = getStateTrackerParams014();
        for(int i = ParamList.ST_START; i <= ParamList.ST_END; i++){
            ParamList.setFixedValue(i, parameters[i-ParamList.ST_START]);
        }
    }

    public static double[] getStateTrackerParams014(){
        return new double[]{0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565};
    }

    public static double[] getStateTrackerParams014B(){
        return new double[]{0.4288488397453174, 0.15642855643005749, 0.9549581787332029, 0.7081388644550592, 0.05794316594829063, 0.2148350420335675, 0.6550672733467983, 0.11444679870333441, 0.26418962368951127};
    }

    public static void fixNonEnsembleWeights015(){
        double[] paramsNonEnsemble = {0.29208164794833336, 0.4933043961984872, 0.2146139558531794, 0.039082238420405646, 0.4097065894571512, 
            0.34316571780522437, 0.6568342821947756, 0.17000003329459223, 0.9565683621930221, 0.4008540338225604, 0.5991459661774395, 
            0.8164209844981123, 0.33923897237621314, 0.9686458628844807, 0.8985860747919318, 0.7824084985947584, 0.3507563695181819, 
            0.9451028368829781, 0.3677917221207484, 0.21958246619065547, 1.0, 4.0, 0.8115237600349762, 3.9962362572044015, 0.030086775107394215,
             3.834926979775834, 2.0, 1.0, 16.0, 6.0};
        for(int i = 0; i < ParamList.NUM_NONENSEMBLE_PARAMS; i++){
            ParamList.setFixedValue(i, paramsNonEnsemble[i]);
        }
    }

    //Fix all values, only tune the weights of hand evaluator
    //Self: EnsemblePlayer, TwoStage
    public static void run015(){
        double start, elapsed;
        start = System.currentTimeMillis();
        
        int popSize = 40;
        int numGenerations = 10;
        int gamesPerIndividual = 2000;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;
        boolean wantCrossover = true;
        int tournamentSize = popSize* 3 / 100;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;
        //fix the Params

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;

        fixNonEnsembleWeights015();
        
        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual,
        numOfEvaluators, mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    
    }

    //Fix all values, only tune the weights of hand evaluator
    //Self: EnsemblePlayer, TwoStage
    public static void run016(){
        double start, elapsed;
        start = System.currentTimeMillis();
        
        int popSize = 30;
        int numGenerations = 5;
        int gamesPerIndividual = 100;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;
        boolean wantCrossover = true;
        int tournamentSize = popSize* 3 / 100;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;
        
        //fix the Params

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;

        ParamList.setFixedValue(ParamList.TS_KNOCK_THRESHOLD_EARLY, 0.0);
        ParamList.setFixedValue(ParamList.TS_KNOCK_THRESHOLD_LATE, 0.0);
        
        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual,
        numOfEvaluators, mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    
    }

    // Exhaustive Search for OneStageKnockDecider
    // Self: Ensemble (OneStage, MeldOnlyDraw, DeadwoodHandEval)
    // Opp: Ensemble (OneStage, MeldOnlyDraw, DeadwoodHandEval)
    public static void run100(){
        System.out.println("=============== run100 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int numGamesPerCase = 2000;

        double[] winnings = ExhaustiveExperiments.OneStageVSOneStage(numGamesPerCase);

        System.out.println(Arrays.toString(winnings));

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Exhaustive Search for TwoStageKnockDecider
    // Self: Ensemble (TwoStage, MeldOnlyDraw, DeadwoodHandEval)
    // Opp: Ensemble (OneStage(0), MeldOnlyDraw, DeadwoodHandEval)
    public static void run101(){
        System.out.println("=============== run101 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int numGamesPerCase = 2000;
        int osThreshold = 0;

        double[] winnings = ExhaustiveExperiments.TwoStageVSOneStage(numGamesPerCase, osThreshold);

        System.out.println(Arrays.toString(winnings));

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Exhaustive Search for TwoStageKnockDecider
    // Self: Ensemble (TwoStage, MeldOnlyDraw, DeadwoodHandEval)
    // Opp: Ensemble (OneStage(2), MeldOnlyDraw, DeadwoodHandEval)
    public static void run102(){
        System.out.println("=============== run102 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int numGamesPerCase = 2000;
        int osThreshold = 2;

        double[] winnings = ExhaustiveExperiments.TwoStageVSOneStage(numGamesPerCase, osThreshold);

        System.out.println(Arrays.toString(winnings));

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Exhaustive Search for TwoStageKnockDecider
    // Self: Ensemble (TwoStage, MeldOnlyDraw, DeadwoodHandEval)
    // Opp: Ensemble (OneStage(4), MeldOnlyDraw, DeadwoodHandEval)
    public static void run103(){
        System.out.println("=============== run103 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int numGamesPerCase = 2000;
        int osThreshold = 4;

        double[] winnings = ExhaustiveExperiments.TwoStageVSOneStage(numGamesPerCase, osThreshold);

        System.out.println(Arrays.toString(winnings));

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Exhaustive Search for TwoStageKnockDecider
    // Self: Ensemble (TwoStage, MeldOnlyDraw, DeadwoodHandEval)
    // Opp: Ensemble (OneStage(6), MeldOnlyDraw, DeadwoodHandEval)
    public static void run104(){
        System.out.println("=============== run104 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int numGamesPerCase = 2000;
        int osThreshold = 6;

        double[] winnings = ExhaustiveExperiments.TwoStageVSOneStage(numGamesPerCase, osThreshold);

        System.out.println(Arrays.toString(winnings));

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Exhaustive Search for TwoStageKnockDecider
    // Self: Ensemble (TwoStage, MeldOnlyDraw, DeadwoodHandEval)
    // Opp: Ensemble (OneStage(8), MeldOnlyDraw, DeadwoodHandEval)
    public static void run105(){
        System.out.println("=============== run105 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int numGamesPerCase = 2000;
        int osThreshold = 8;

        double[] winnings = ExhaustiveExperiments.TwoStageVSOneStage(numGamesPerCase, osThreshold);

        System.out.println(Arrays.toString(winnings));

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // fix parameters for TwoStageKnockDecider
    // (E, L, M) = (10, 0, 6)
    // The values come from the results 101-105
    public static void fixTwoStageKnockDecider101(){
        ParamList.setFixedValue(ParamList.TS_KNOCK_THRESHOLD_EARLY, 10);
        ParamList.setFixedValue(ParamList.TS_KNOCK_THRESHOLD_LATE, 0);
        ParamList.setFixedValue(ParamList.TS_KNOCK_MIDDLE, 6);
    }

    public static void run106(){
        System.out.println("=============== run106 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int numGamesPerCase = 2000;

        double[] winnings = ExhaustiveExperiments.TwoStageVSScorePrediction(numGamesPerCase);

        System.out.println(Arrays.toString(winnings));

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // TwoStageDrawDecider
    // self: ensemble{TSKnock}
    public static void run200(){
        System.out.println("=============== run200 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int numGamesPerCase = 10000;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;

        double[] winnings = ExhaustiveExperiments.searchTwoStageDrawDecider(numGamesPerCase, oppDrawDeciderFlag);

        System.out.println(Arrays.toString(winnings));

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    public static void run201(){
        System.out.println("=============== run201 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int numGamesPerCase = 10000;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;

        double[] winnings = ExhaustiveExperiments.searchTwoStageDrawDecider(numGamesPerCase, oppDrawDeciderFlag);

        System.out.println(Arrays.toString(winnings));

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer per turn
    // Fitness: Error
    // Uses parameters from run013 
    public static void run110() throws Exception {
        System.out.println("=============== run110 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams010();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerTurn(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);

    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count 
    // Uses parameters from run014
    public static void run111() throws Exception {
        System.out.println("=============== run111 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams011();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);

    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Error
    // Uses parameters from run013 
    public static void run113() throws Exception {
        System.out.println("=============== run113 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams013();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerTurn(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Error
    // Uses parameters from run013 
    public static void run113B() throws Exception {
        System.out.println("=============== run113B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams013B();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerTurn(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Error
    // Uses parameters from run013 
    public static void run113M() throws Exception {
        System.out.println("=============== run113M ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams013B();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerTurn(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses parameters from run014
    public static void run114() throws Exception {
        System.out.println("=============== run114 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams014();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses parameters from run014
    public static void run114B() throws Exception {
        System.out.println("=============== run114B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams014B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses parameters from run014
    public static void run114M() throws Exception {
        System.out.println("=============== run114M ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams014B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Error
    // Uses parameters from run010 
    public static void run115() throws Exception {
        System.out.println("=============== run115 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams010();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerOppCardsUnKnown(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);

    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run011
    public static void run116() throws Exception {
        System.out.println("=============== run116 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams011();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);

    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Error
    // Uses parameters from run013 
    public static void run117() throws Exception {
        System.out.println("=============== run117 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams013();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerOppCardsUnKnown(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Error
    // Uses parameters from run013 
    public static void run117B() throws Exception {
        System.out.println("=============== run117B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams013B();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerOppCardsUnKnown(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run014
    public static void run118() throws Exception {
        System.out.println("=============== run118 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams014();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run014
    public static void run118B() throws Exception {
        System.out.println("=============== run118B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams014B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }


    // Analyze game length 
    // self: Ensemble(TwoStageKnock(10, 0, 6), MeldOnlyDraw, DeadwoodEval)
    // opp: SimplePlayer
    public static void run120(){
        System.out.println("=============== run120 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        int totalGames = 10000;
        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        ParamList dummy = new ParamList(new double[]{1});
        StateTrackerAnalyzer.analyzeGameLength(dummy, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // Analyze game length 
    // self: Ensemble(TwoStageKnock(10, 0, 6), MeldOnlyDraw, DeadwoodEval)
    // opp: KnockOnGin (none, MeldOnlyDraw)
    public static void run121(){
        System.out.println("=============== run121 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        int totalGames = 10000;
        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        ParamList dummy = new ParamList(new double[]{1});
        StateTrackerAnalyzer.analyzeGameLength(dummy, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTracker Tuner
    // Uses Error method and checks at the end of turn 15
    // Opp: KnockOnGin (none, MeldOnlyDraw) 
    public static void run123(){
        System.out.println("=============== run123 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 2000;
        int tournamentSize = popSize* 3 / 100;
        double mutationChance = 0.02;

        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;

        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_ERROR);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.AT_ONE_TURN);
        TunableStateTracker.setTurnToCheck(15);

        ParamList bestParams = StateTrackerTuner.geneticAlgorithmError(popSize, numGenerations, gamesPerIndividual, tournamentSize, mutationChance, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        System.out.print("Print only parameters related to StateTracker: ");
        bestParams.printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    public static double[] getStateTrackerParams123(){
        return new double[]{0.985974070534608, 0.5860528560495246, 0.9877069784901421, 0.22402243441905456, 0.21531573160140394, 0.9417343969748954, 0.2869149173956985, 0.25145162812070254, 0.7807864300521876};
    }

    public static double[] getStateTrackerParams123B(){
        return new double[]{0.9470188850259347, 0.9086174717708582, 0.9647394578477269, 0.10995622196511778, 0.011318194405964266, 0.9571390858650956, 0.29171950310985184, 0.059223439668070754, 0.9464854925872137};
    }

    // StateTracker Tuner
    // Uses Count method and checks at the end of turn 10
    // numAdditionalCards = 0
    // Opp: KnockOnGin (none, MeldOnlyDraw) 
    public static void run124(){
        System.out.println("=============== run124 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 2000;
        int tournamentSize = popSize* 3 / 100;
        double mutationChance = 0.02;

        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;

        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_COUNT);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.AT_ONE_TURN);
        TunableStateTracker.setTurnToCheck(10);

        ParamList bestParams = StateTrackerTuner.geneticAlgorithmCount(popSize, numGenerations, gamesPerIndividual, tournamentSize, mutationChance, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        System.out.print("Print only parameters related to StateTracker: ");
        bestParams.printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    public static double[] getStateTrackerParams124(){
        return new double[]{0.434288603119409, 0.4277246087769463, 0.836422970733855, 0.1251747339102558, 0.12174996757592116, 0.8482570933971513, 0.9724975576771724, 0.4319563867941698, 0.23309366785338126};
    }

    public static double[] getStateTrackerParams124B(){
        return new double[]{0.8258447412619491, 0.18576106962561767, 0.5730751504539557, 0.4228556739497471, 0.3412624674111574, 0.5917277031971161, 0.3472294138086014, 0.29893074067469927, 0.6204097475212305};    
    }

    // StateTracker Tuner
    // Uses Count method and checks at the end of turn 10
    // numAdditionalCards = 3
    // Opp: KnockOnGin (none, MeldOnlyDraw) 
    public static void run125(){
        System.out.println("=============== run125 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 2000;
        int tournamentSize = popSize* 3 / 100;
        double mutationChance = 0.02;

        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;

        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_COUNT);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.AT_ONE_TURN);
        TunableStateTracker.setTurnToCheck(10);

        ParamList bestParams = StateTrackerTuner.geneticAlgorithmCount(popSize, numGenerations, gamesPerIndividual, tournamentSize, mutationChance, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        System.out.print("Print only parameters related to StateTracker: ");
        bestParams.printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    public static double[] getStateTrackerParams125(){
        return new double[]{0.8428168493450475, 0.19072056555825156, 0.5343795148814426, 0.8669004695582703, 0.45570375869887003, 0.5942145560144757, 0.9227693223621712, 0.35141474481721524, 0.4144657623949608};
    }

    public static double[] getStateTrackerParams125B(){
        return new double[]{0.7150801809108848, 0.21643559537414936, 0.7950272243650457, 0.6626076072394504, 0.11104867429780219, 0.22763694251811017, 0.6350968089644013, 0.07237543820162051, 0.2791548805781182};
    }

    // StateTracker Tuner
    // Uses Error method and checks at the end of EVERY turn
    // Opp: KnockOnGin (none, MeldOnlyDraw) 
    public static void run126(){
        System.out.println("=============== run126 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 2000;
        int tournamentSize = popSize* 3 / 100;
        double mutationChance = 0.02;

        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;

        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_ERROR);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.EVERY_TURN);

        ParamList bestParams = StateTrackerTuner.geneticAlgorithmError(popSize, numGenerations, gamesPerIndividual, tournamentSize, mutationChance, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        System.out.print("Print only parameters related to StateTracker: ");
        bestParams.printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    public static double[] getStateTrackerParams126(){
        return new double[]{0.9080900937014048, 0.8616855097805642, 0.9758854749243581, 0.6788734737595091, 0.11081784098738878, 0.5468244472075339, 0.4314035401653684, 0.30557842076930886, 0.8598241544633731};
    }

    public static double[] getStateTrackerParams126B(){
        return new double[]{0.9428942488901159, 0.9367412604470984, 0.9697365416885357, 0.05464032122598006, 0.010331882768072287, 0.9408674241543815, 0.01250435639200742, 0.01250435639200742, 0.9134536055724352};
    }

    // StateTracker Tuner
    // Uses Count method and checks at the end of EVERY turn
    // numAdditionalCards = 0
    // Opp: KnockOnGin (none, MeldOnlyDraw) 
    public static void run127(){
        System.out.println("=============== run127 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 2000;
        int tournamentSize = popSize* 3 / 100;
        double mutationChance = 0.02;


        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;

        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_COUNT);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.EVERY_TURN);

        ParamList bestParams = StateTrackerTuner.geneticAlgorithmCount(popSize, numGenerations, gamesPerIndividual, tournamentSize, mutationChance, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        System.out.print("Print only parameters related to StateTracker: ");
        bestParams.printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    public static double[] getStateTrackerParams127(){
        return new double[]{0.14003034313386564, 0.07769530051551632, 0.757065508345762, 0.2973537251837025, 0.00678109589710707, 0.30617695923578137, 0.33449428009408744, 0.08484074083081705, 0.15486103154953623};
    }

    public static double[] getStateTrackerParams127B(){
        return new double[]{0.3711818620610956, 0.2541668832688089, 0.3678004521950814, 0.3265168807868527, 0.2465072897079097, 0.36060781594421487, 0.41574251622231106, 0.1675523707061891, 0.39141122476832924};
    }

    // StateTracker Tuner
    // Uses Count method and checks at the end of EVERY turn
    // numAdditionalCards = 3
    // Opp: KnockOnGin (none, MeldOnlyDraw) 
    public static void run128(){
        System.out.println("=============== run128 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 2000;
        int tournamentSize = popSize* 3 / 100;
        double mutationChance = 0.02;


        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;

        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_COUNT);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.EVERY_TURN);

        ParamList bestParams = StateTrackerTuner.geneticAlgorithmCount(popSize, numGenerations, gamesPerIndividual, tournamentSize, mutationChance, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
        System.out.print("Print only parameters related to StateTracker: ");
        bestParams.printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    public static double[] getStateTrackerParams128(){
        return new double[]{0.36757581222128655, 0.24072595638422134, 0.10913854522585409, 0.28695995325881574, 0.28622271995287984, 0.3037946535696997, 0.32440237969777097, 0.24020653595095975, 0.28401862620617924};
    }

    public static double[] getStateTrackerParams128B(){
        return new double[]{0.6630371527744499, 0.1217697871528205, 0.3264101591147599, 0.29569207631075933, 0.20402524225665863, 0.4663172409048555, 0.3373632020537469, 0.2256831575342324, 0.5218022381679938};
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Error
    // Uses parameters from run123 
    public static void run133() throws Exception {
        System.out.println("=============== run133 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams123();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerTurn(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Error
    // Uses parameters from run123 
    public static void run133B() throws Exception {
        System.out.println("=============== run133B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams123B();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerTurn(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }
   
    // StateTrackerAnalyzer Per Turn
    // Fitness: Error
    // Uses parameters from run123 
    public static void run133M() throws Exception {
        System.out.println("=============== run133M ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams123B();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerTurn(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }    

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count(0)
    // Uses parameters from run124
    public static void run134() throws Exception {
        System.out.println("=============== run134 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams124();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count(0)
    // Uses parameters from run124
    public static void run134B() throws Exception {
        System.out.println("=============== run134B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams124B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count(0)
    // Uses parameters from run124
    public static void run134M() throws Exception {
        System.out.println("=============== run134M ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams124B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }    

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count(3)
    // Uses parameters from run125
    public static void run135() throws Exception {
        System.out.println("=============== run135 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams125();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count(3)
    // Uses parameters from run125
    public static void run135B() throws Exception {
        System.out.println("=============== run135B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams125B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count(3)
    // Uses parameters from run125
    public static void run135M() throws Exception {
        System.out.println("=============== run135M ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams125B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }    

    // StateTrackerAnalyzer Per Turn
    // Fitness: Error
    // Uses parameters from run126 
    public static void run136() throws Exception {
        System.out.println("=============== run136 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams126();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerTurn(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Error
    // Uses parameters from run126 
    public static void run136B() throws Exception {
        System.out.println("=============== run136B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams126B();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerTurn(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Error
    // Uses parameters from run126 
    public static void run136M() throws Exception {
        System.out.println("=============== run136M ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams126B();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerTurn(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }    

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count(0)
    // Uses parameters from run127
    public static void run137() throws Exception {
        System.out.println("=============== run137 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams127();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count(0)
    // Uses parameters from run127
    public static void run137B() throws Exception {
        System.out.println("=============== run137B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams127B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count(0)
    // Uses parameters from run127
    public static void run137M() throws Exception {
        System.out.println("=============== run137M ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams127B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count(3)
    // Uses parameters from run128
    public static void run138() throws Exception {
        System.out.println("=============== run138 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams128();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count(3)
    // Uses parameters from run128
    public static void run138B() throws Exception {
        System.out.println("=============== run138B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams128B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count(3)
    // Uses parameters from run128
    public static void run138M() throws Exception {
        System.out.println("=============== run138M ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams128B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }
    // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses parameters from run014
    public static void run139() throws Exception {
        System.out.println("=============== run139 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams014();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses parameters from run014
    public static void run140D0() throws Exception {
        System.out.println("=============== run140D0 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams014();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses parameters from run014
    public static void run140M0() throws Exception {
        System.out.println("=============== run140M0 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams014();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses parameters from run014
    public static void run140D3() throws Exception {
        System.out.println("=============== run140D3 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams014();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses parameters from run014
    public static void run140M3() throws Exception {
        System.out.println("=============== run140M3 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams014();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses 0 for every parameter
    public static void run141C0M() throws Exception {
        System.out.println("=============== run141C0M ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses 0 for every parameter
    public static void run141C3M() throws Exception {
        System.out.println("=============== run141C3M ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

        // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses 0 for every parameter
    public static void run141C0D() throws Exception {
        System.out.println("=============== run141C0D ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses 0 for every parameter
    public static void run141C3D() throws Exception {
        System.out.println("=============== run141C3D ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per Turn
    // Fitness: Count
    // Uses 0 for every parameter
    public static void run141EM() throws Exception {
        System.out.println("=============== run141EM ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerTurn(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }      

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Error
    // Uses parameters from run123 
    public static void run143() throws Exception {
        System.out.println("=============== run143 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams123();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerOppCardsUnKnown(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Error
    // Uses parameters from run123 
    public static void run143B() throws Exception {
        System.out.println("=============== run143B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams123B();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerOppCardsUnKnown(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run124
    public static void run144() throws Exception {
        System.out.println("=============== run144 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams124();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run124
    public static void run144B() throws Exception {
        System.out.println("=============== run144 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams124B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run125
    public static void run145() throws Exception {
        System.out.println("=============== run145 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams125();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run125
    public static void run145B() throws Exception {
        System.out.println("=============== run145B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams125B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Error
    // Uses parameters from run126 
    public static void run146() throws Exception {
        System.out.println("=============== run146 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams126();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerOppCardsUnKnown(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Error
    // Uses parameters from run126 
    public static void run146B() throws Exception {
        System.out.println("=============== run146B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams126B();
        int totalGames = 2000 * 12;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgErrorsPerOppCardsUnKnown(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run127
    public static void run147() throws Exception {
        System.out.println("=============== run147 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams127();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run127
    public static void run147B() throws Exception {
        System.out.println("=============== run147B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams127B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run128
    public static void run148() throws Exception {
        System.out.println("=============== run148 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams128();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run128
    public static void run148B() throws Exception {
        System.out.println("=============== run148B ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams128B();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run011
    public static void run149C3() throws Exception {
        System.out.println("=============== run149C3 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams014();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    // StateTrackerAnalyzer Per OppCardsUnknown
    // Fitness: Count
    // Uses parameters from run011
    public static void run149C0() throws Exception {
        System.out.println("=============== run149C0 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        double[] parameters = getStateTrackerParams014();
        int totalGames = 2000 * 12;
        int numAdditionalCards = 0;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        StateTrackerAnalyzer.avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    /*
    self: Ensemble Player (Two Stage, deadwood)
    opp: Simple Player
    crossover: yes
    Target: test new tournament selection     
    */
    public static void run022(){
        System.out.println("=============== run022 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 2000;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        int tournamentSize = popSize* 3 / 100;

        boolean wantCrossover = true;

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
                                    mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
                                    oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    /*
    self: Ensemble Player (Two Stage, deadwood)
    opp: Simple Player
    crossover: No
    Target: test new tournament selection
    */
    public static void run023(){
        System.out.println("=============== run023 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 2000;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        int tournamentSize = popSize* 3 / 100;

        boolean wantCrossover = false;

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
                                    mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
                                    oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);

        elapsed = System.currentTimeMillis() - start;
     
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    /*
    Best player of run022 vs 
    */
    public static void run024(){
        double[] paramsArray = {0.3477115421523146, 0.3758783707359571, 0.27641008711172843, 0.005216252835084145, 0.7182151039155418, 0.5743255098614893, 0.4256744901385106, 0.2846376733701423, 0.4831965483595829, 0.8167175864262103, 0.1832824135737897, 0.6891615990704024, 0.5363657768525255, 0.016946176422851678, 0.3784085366908435, 0.06170329943121833, 0.09758519319996917, 0.5683623853791561, 0.5403604780349225, 0.8455049745888533, 7.0, 0.0, 0.08476275628354568, 1.7923812406751636, 0.23623204246484064, 2.508441031740568, 8.0, 7.0, 19.0, 2.0, 0.1994567126090956, 0.029979474997941746, 0.12864512562000022, 0.13876577516898048, 0.27630444982210745, 0.22684846178187457};
        //= {0.5194467060537098, 0.4507542557263633, 0.0297990382199269, 0.39934953566821885, 0.6688301565469988, 0.20238682627992888, 0.7976131737200711, 0.4372249532903665, 0.841672624178657, 0.8575915835300302, 0.14240841646996977, 0.8105577415754656, 0.7382349867689809, 0.7059333380160969, 0.8660246607098339, 0.4758751813394866, 0.4259236304453111, 0.8276863154154702, 0.030354739383426255, 0.9035144543720394, 9.0, 6.0, 0.8383562608515353, 3.208055774837532, 0.013280210108335355, 3.0576829854693126, 7.0, 0.0, 17.0, 7.0, 0.0791126554058838, 0.07306467053017213, 0.08386615221969014, 0.40038910508298464, 0.3539898515399171, 0.009577565221352115};     
       ParamList params  = new ParamList(paramsArray, 6);
        int gamesPerIndividual = 2000;
        LinearDeadwoodPenaltyHandEvaluator oppLinearHe = new LinearDeadwoodPenaltyHandEvaluator(params);
        OppCardsKnownDeadwoodPenaltyHandEvaluator oppOppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params);
        MeldabilityHandEvaluator oppMhe = new MeldabilityHandEvaluator(params);
        ConvHandEvaluator oppChe = new ConvHandEvaluator(params);
        oppMhe.setShouldNormalize(true);
        oppChe.setShouldNormalize(true);
        EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(params, oppMhe, new DeadwoodHandEvaluator(),new AceTwoBonusHandEvaluator(), 
                                        oppChe, //new MultiOppHandMeldabilityEvaluator(oppParams),
                                        oppLinearHe, oppOppCardsHe);

        oppLinearHe.setEnsemblePlayer((EnsembleHandEvalPlayer)p0);
        oppOppCardsHe.setEnsemblePlayer((EnsembleHandEvalPlayer)p0);
        ((EnsembleHandEvalPlayer)p0).setKnockDecider(new TwoStageKnockDecider(params));
        ((EnsembleHandEvalPlayer)p0).setDrawDecider(new DeadwoodDrawDecider());
        SimpleFakeGinRummyPlayer p1 = new SimpleFakeGinRummyPlayer(new ParamList(new double[0]));
        
        TestingGame gameManager = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(false);
        int p0Wins = 0;
        for(int j = 0; j < gamesPerIndividual; j++){
            int winner = gameManager.play();
            if(winner == 0)
                p0Wins += 1; 
        }
        System.out.println("winrate:" + (double)p0Wins/gamesPerIndividual);
    }
    
    public static void run025(){
        double[] paramsArray = {0.3525562186988447, 0.5017162976336551, 0.14572748366750016, 0.08188176307256967, 0.22228731614994335, 0.6145744183543337, 0.38542558164566637, 0.4391065428724263, 0.4497301737056283, 0.6354613163056313, 0.36453868369436865, 0.45248472747997903, 0.098306198595558, 0.948208493865991, 0.20539377959960048, 0.025488707655334286, 0.8766671223274256, 0.2550980350633101, 0.0390763429567218, 0.6062397813343741, 4.0, 5.0, 0.693123939297376, 3.0741304022866744, 0.3691328723617989, 0.25584411630016446, 4.0, 2.0, 9.0, 7.0, 0.11232885290660373, 0.4898351322473466, 0.04524781363231897, 0.08759206265654497, 0.24421885617231223, 0.02077728238487343};
        //{0.3396084737374551, 0.640909246808708, 0.01948227945383694, 0.023946291441857226, 0.6688301565469988, 0.15243227165318912, 0.8475677283468108, 0.3527786170190347, 0.841672624178657, 0.5189571993530402, 0.48104280064695976, 0.8105577415754656, 0.7382349867689809, 0.370102040113808, 0.3544458274701848, 0.09434098740750374, 0.5582204812112749, 0.47917978726376964, 0.3088326168944864, 0.941770045655481, 7.0, 4.0, 0.3710347712011922, 0.15979452962859464, 0.5107804922208957, 3.0576829854693126, 7.0, 7.0, 17.0, 7.0, 0.058667000445152474, 0.4821250490705148, 0.25107443406171237, 0.02316070762893088, 0.18010001475680923, 0.004872794036880208};
       //                        0.3396084737374551, 0.640909246808708, 0.01948227945383694, 0.023946291441857226, 0.6688301565469988, 0.15243227165318912, 0.8475677283468108, 0.3527786170190347, 0.841672624178657, 0.5189571993530402, 0.48104280064695976, 0.8105577415754656, 0.7382349867689809, 0.370102040113808, 0.3544458274701848, 0.09434098740750374, 0.5582204812112749, 0.47917978726376964, 0.3088326168944864, 0.941770045655481, 7.0, 4.0, 0.3710347712011922, 0.15979452962859464, 0.5107804922208957, 3.0576829854693126, 7.0, 7.0, 17.0, 7.0, 0.058667000445152474, 0.4821250490705148, 0.25107443406171237, 0.02316070762893088, 0.18010001475680923, 0.004872794036880208        
       ParamList params  = new ParamList(paramsArray, 6);
        int gamesPerIndividual = 2000;
        LinearDeadwoodPenaltyHandEvaluator oppLinearHe = new LinearDeadwoodPenaltyHandEvaluator(params);
        OppCardsKnownDeadwoodPenaltyHandEvaluator oppOppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params);
        MeldabilityHandEvaluator oppMhe = new MeldabilityHandEvaluator(params);
        ConvHandEvaluator oppChe = new ConvHandEvaluator(params);
        oppMhe.setShouldNormalize(true);
        oppChe.setShouldNormalize(true);
        EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(params, oppMhe, new DeadwoodHandEvaluator(),new AceTwoBonusHandEvaluator(), 
                                        oppChe, //new MultiOppHandMeldabilityEvaluator(oppParams),
                                        oppLinearHe, oppOppCardsHe);

        oppLinearHe.setEnsemblePlayer((EnsembleHandEvalPlayer)p0);
        oppOppCardsHe.setEnsemblePlayer((EnsembleHandEvalPlayer)p0);
        p0.setKnockDecider(new OneStageKnockDecider(params));
        p0.setDrawDecider(new DeadwoodDrawDecider());
        
        ParamList oppParam = new ParamList(new double[]{1});
        EnsembleHandEvalPlayer p1 = new EnsembleHandEvalPlayer(oppParam, new DeadwoodHandEvaluator());
        p1.setKnockDecider(new KnockOnGinKnockDecider());
        p1.setDrawDecider(new MeldOnlyDrawDecider());

        TestingGame gameManager = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(false);
        int p0Wins = 0;
        for(int j = 0; j < gamesPerIndividual; j++){
            int winner = gameManager.play();
            if(winner == 0)
                p0Wins += 1; 
        }
        System.out.println("winrate:" + (double)p0Wins/gamesPerIndividual);
    }

    public static double mean (ArrayList<Double> table)
    {
        double total = 0;

        for ( int i= 0;i < table.size(); i++)
        {
            double currentNum = table.get(i);
            total+= currentNum;
        }
        return total/table.size();
    }

    public static double sd (ArrayList<Double> table)
    {
        // Step 1: 
        double mean = mean(table);
        double temp = 0;
    
        for (int i = 0; i < table.size(); i++)
        {
            double val = table.get(i);
    
            // Step 2:
            double squrDiffToMean = Math.pow(val - mean, 2);
    
            // Step 3:
            temp += squrDiffToMean;
        }
    
        // Step 4:
        double meanOfDiffs = (double) temp / (double) (table.size());
    
        // Step 5:
        return Math.sqrt(meanOfDiffs);
    }

    public static double median(ArrayList<Double> table) {
        // Collections.sort(table); no need since we sort it anyway
        int middle = table.size() / 2;
        middle = middle > 0 && middle % 2 == 0 ? middle - 1 : middle;
        return table.get(middle);
    }

    private static java.util.concurrent.atomic.AtomicInteger indivCount = new java.util.concurrent.atomic.AtomicInteger(0);

    public static double playManyGame(ParamList params, int gamesPerIndividual, int selfFlag){
        System.out.print(indivCount.getAndIncrement() + " ");
        EnsembleHandEvalPlayer p0 = null;
        
        LinearDeadwoodPenaltyHandEvaluator linearHe = new LinearDeadwoodPenaltyHandEvaluator(params);
        OppCardsKnownDeadwoodPenaltyHandEvaluator oppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params);
        // liner
        if(selfFlag == GATuner.SELF_IS_ENSEMBLE_PLAYER){
            MeldabilityHandEvaluator mhe = new MeldabilityHandEvaluator(params);
            ConvHandEvaluator che = new ConvHandEvaluator(params);
            mhe.setShouldNormalize(true);
            che.setShouldNormalize(true);
            p0 = new EnsembleHandEvalPlayer(params, mhe, new DeadwoodHandEvaluator(), 
                                                                        new AceTwoBonusHandEvaluator(), 
                                                                        che, 
                                                                        //new MultiOppHandMeldabilityEvaluator(params),
                                                                        linearHe,
                                                                        oppCardsHe);
            linearHe.setEnsemblePlayer(p0);
            oppCardsHe.setEnsemblePlayer(p0);
        }
        else if(selfFlag == GATuner.SELF_IS_INDEX_PLAYER){
            MeldabilityHandEvaluator mhe = new MeldabilityHandEvaluator(params);
            ConvHandEvaluator che = new ConvHandEvaluator(params);
            p0 = new IndexEnsembleHandEvalPlayer(params, mhe, new DeadwoodHandEvaluator(), 
                                                                        new AceTwoBonusHandEvaluator(), 
                                                                        che, 
                                                                        //new MultiOppHandMeldabilityEvaluator(params),
                                                                        linearHe,
                                                                        oppCardsHe);
            linearHe.setEnsemblePlayer(p0);
            oppCardsHe.setEnsemblePlayer(p0);
        }
        else{
            throw new RuntimeException("Self flag not available");
        }
        

        // p0.setKnockDecider(new OneStageKnockDecider(params));
        p0.setKnockDecider(new TwoStageKnockDecider(params));
        p0.setDrawDecider(new DeadwoodDrawDecider());


        SimpleFakeGinRummyPlayer p1 = new SimpleFakeGinRummyPlayer(new ParamList(new double[0]));
        int p0Wins = 0;

        TestingGame gameManager = new TestingGame(p0, p1);
        for(int j = 0; j < gamesPerIndividual; j++){
            // System.out.println("Game " + j + " is playing ...");
            int winner = gameManager.play();
            if(winner == 0)
                p0Wins += 1; 
        }
        return (double) p0Wins / gamesPerIndividual;
    }

    public static void experimentWithEnforceRestrictions(boolean shouldCheckOrder, int selfFlag){
        System.out.println("======================\nshouldCheckOrder: " + shouldCheckOrder);
        ParamList.SHOULD_CHECK_ORDER = shouldCheckOrder;

        ArrayList<Double> allWinRate = new ArrayList<>();
        int numRandomIndividual = 10000;
        int gamesPerIndividual = 2000;
        
        ArrayList<ParamList> randomParamLists = new ArrayList<>();
        for(int i = 0; i < numRandomIndividual; i++){
            randomParamLists.add(ParamList.getRandomParamList(6)); 
        }
        
        allWinRate = randomParamLists.stream().parallel().map(x -> {return playManyGame(x, gamesPerIndividual, selfFlag);} ).collect(Collectors.toCollection(ArrayList::new));
        Collections.sort(allWinRate);
        double mean = mean(allWinRate);
        double median = median(allWinRate);
        double std = sd(allWinRate);
        double min = allWinRate.get(0);
        double max = allWinRate.get(allWinRate.size() - 1);

        System.out.println("All win rate: " + allWinRate);
        System.out.println("mean: " + mean + ", median: " + median + ", std: " + std + ", min: " + min + ", max: " + max);
    }

    public static void run001WithoutCrossover(){
        System.out.println("=============== run001WithoutCrossover ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        
        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_KNOCK_ON_GIN_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;

        boolean wantCrossover = false;
        int tournamentSize = popSize* 3 / 100;


        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);

    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    public static void run001WithoutCrossoverBig(){
        System.out.println("=============== run001WithoutCrossoverBig ===============");
        double start, elapsed;
        start = System.currentTimeMillis();
        
        int popSize = 500;
        int numGenerations = 20;
        int gamesPerIndividual = 2000;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_KNOCK_ON_GIN_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;

        boolean wantCrossover = false;
        int tournamentSize = popSize* 3 / 100;


        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);

    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    public static void run002WithoutCrossover(){
        System.out.println("=============== run002WithoutCrossover ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 100;
        int numGenerations = 15;
        int gamesPerIndividual = 200;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_INDEX_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_KNOCK_ON_GIN_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        int tournamentSize = popSize* 3 / 100;

        
        boolean wantCrossover = false;

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    public static void run002WithoutCrossoverBig(){
        System.out.println("=============== run002WithoutCrossoverBig ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 500;
        int numGenerations = 20;
        int gamesPerIndividual = 2000;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_INDEX_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_KNOCK_ON_GIN_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        int tournamentSize = popSize* 3 / 100;

        
        boolean wantCrossover = false;

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    //One GA run with 1000 individuals, tournament, no crossover
    public static void run026(){
        System.out.println("=============== run026 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 1000;
        int numGenerations = 15;
        int gamesPerIndividual = 1000;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;
        
        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        int tournamentSize = popSize* 3 / 100;

        fixTwoStageKnockDecider101();
        fixStateTracker014();
        
        boolean wantCrossover = false;

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);

    }
    //One GA run with 1000 individuals, tournament, with crossover
    public static void run027(){
        System.out.println("=============== run027 ===============");
        double start, elapsed;
        start = System.currentTimeMillis();

        int popSize = 1000;
        int numGenerations = 15;
        int gamesPerIndividual = 1000;
        int numOfEvaluators = 6;
        double mutationChance = 0.02;

        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        
        int tournamentSize = popSize* 3 / 100;

        
        boolean wantCrossover = true;

        fixStateTracker014();
        fixTwoStageKnockDecider101();

        ParamList bestParams = GATuner.geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, 
        mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, 
        oppDrawDeciderFlag, oppKnockDeciderFlag, wantCrossover, tournamentSize);
        System.out.println("ParamList of the best fitness across generations: " + bestParams);
    
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s): " + elapsed/1000);
    }

    /*
    Generate a random ParamList (e.g., win rate = 70%)
    Plug those random values into a ParamList
    ST | MD | …. | Weights
    Example: Lock everything but ST genes with the random values … hit it with mutation 2000 times … evaluate what the mutated ParamList’s fitness is … we care about the genes that impact Index Player’s fitness the most
    (This is not a GA run.)

    */

    public static void fixCH(ParamList params){
        ParamList.setFixedValue(ParamList.CH_SAMERANK, params.get(ParamList.CH_SAMERANK));
        ParamList.setFixedValue(ParamList.CH_ONEAWAY, params.get(ParamList.CH_ONEAWAY));
        ParamList.setFixedValue(ParamList.CH_TWOAWAY, params.get(ParamList.CH_TWOAWAY));
    }

    public static void fixMC(ParamList params){
        ParamList.setFixedValue(ParamList.MC_SELF_LOW_OBTAINABILITY, params.get(ParamList.MC_OPP_LOW_OBTAINABILITY));
        ParamList.setFixedValue(ParamList.MC_SELF_RATIO_FOR_UNKNOWN, params.get(ParamList.MC_SELF_RATIO_FOR_UNKNOWN));
        ParamList.setFixedValue(ParamList.MC_SELF_WRANK, params.get(ParamList.MC_SELF_WRANK));
        ParamList.setFixedValue(ParamList.MC_SELF_WRUN, params.get(ParamList.MC_SELF_WRUN));        
        ParamList.setFixedValue(ParamList.MC_OPP_LOW_OBTAINABILITY, params.get(ParamList.MC_OPP_LOW_OBTAINABILITY));
        ParamList.setFixedValue(ParamList.MC_OPP_RATIO_FOR_UNKNOWN, params.get(ParamList.MC_OPP_RATIO_FOR_UNKNOWN));
        ParamList.setFixedValue(ParamList.MC_OPP_WRANK, params.get(ParamList.MC_OPP_WRANK));
        ParamList.setFixedValue(ParamList.MC_OPP_WRUN, params.get(ParamList.MC_OPP_WRUN));
    }

    public static void fixST(ParamList params){
        for(int i = ParamList.ST_START; i <= ParamList.ST_END; i++){
            ParamList.setFixedValue(i, params.get(i));
        }
    }

    public static void fixLD(ParamList params){
        ParamList.setFixedValue(ParamList.LD_PENALTY_SLOPE, params.get(ParamList.LD_PENALTY_SLOPE));
        ParamList.setFixedValue(ParamList.LD_PENALTY_EXPONENT, params.get(ParamList.LD_PENALTY_EXPONENT));
    }

    public static void fixOD(ParamList params){
        ParamList.setFixedValue(ParamList.OD_PENALTY_SLOPE, params.get(ParamList.OD_PENALTY_SLOPE));
        ParamList.setFixedValue(ParamList.OD_PENALTY_EXPONENT, params.get(ParamList.OD_PENALTY_EXPONENT));
    }

    public static void fixTheRest(ParamList params){
        ParamList.setFixedValue(ParamList.SP_NUM_OF_ADDITIONAL_CARDS, params.get(ParamList.SP_NUM_OF_ADDITIONAL_CARDS));
        ParamList.setFixedValue(ParamList.OM_NUM_OF_ADDITIONAL_CARDS, params.get(ParamList.OM_NUM_OF_ADDITIONAL_CARDS));

       //fix weights
       for(int i = ParamList.NUM_NONENSEMBLE_PARAMS; i < ParamList.NUM_NONENSEMBLE_PARAMS + 6; i++){
            ParamList.setFixedValue(i, params.get(i));
       }
    }

    private static final int NOT_FIX_CH = 0;
    private static final int NOT_FIX_MC = 1;
    private static final int NOT_FIX_ST = 2;
    private static final int NOT_FIX_LD = 3; 
    private static final int NOT_FIX_OD = 4;

    public static void experimentFixSetOfGenes(int notLockedParamflag){
        // StateTracker.setFixedValue()
        //0 - StateTracker, 1 - ConvHandEval
        
        ParamList.setFixedValue(ParamList.TS_KNOCK_THRESHOLD_EARLY, 10);
        ParamList.setFixedValue(ParamList.TS_KNOCK_THRESHOLD_LATE, 0);
        ParamList.setFixedValue(ParamList.TS_KNOCK_MIDDLE, 6);

        ParamList params = ParamList.getRandomParamList(6);
        if(notLockedParamflag < NOT_FIX_CH || notLockedParamflag > NOT_FIX_OD){
            throw new RuntimeException("notLockedParamflag does not exist");
        }
        
        if(notLockedParamflag != NOT_FIX_CH)
            fixCH(params);
        if(notLockedParamflag != NOT_FIX_MC)
            fixMC(params);
        if(notLockedParamflag != NOT_FIX_ST)
            fixST(params);
        if(notLockedParamflag != NOT_FIX_LD)
            fixLD(params);
        if(notLockedParamflag != NOT_FIX_OD)
            fixOD(params);

        fixTheRest(params);
        
        double mutationChance = 0.5;
        int numMutatedParam = 2000;
        ParamList[] mutatedParams = new ParamList[numMutatedParam];
        for(int i = 0; i < numMutatedParam; i++){
            ParamList copiedParam = new ParamList(params);
            mutatedParams[i] = copiedParam;
            params.mutate(mutationChance);
            // System.out.println("Param number " + i + ": " + mutatedParams[i]);
        }
        
        int gamesPerIndividual = 1000;
    
        int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_DEADWOOD_DRAW_DECIDER;

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;

        GATuner ga = new GATuner();
        double[] fitnesses = ga.calcFitnessStream(mutatedParams, gamesPerIndividual, selfFlag, selfKnockDeciderFlag, selfDrawDeciderFlag, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);

        for(int i = 0; i < numMutatedParam; i++){
            System.out.println("Param with index: " + i + " has fitness " + fitnesses[i]);
            //" + mutatedParams[i] + "
        }
        // System.out.println(Arrays.toString(fitnesses) );
    } 
    
    public static void experimentNotFixSetOfCH(){
        experimentFixSetOfGenes(NOT_FIX_CH);
    }

    public static void experimentNotFixSetOfMC(){
        experimentFixSetOfGenes(NOT_FIX_MC);
    }

    public static void experimentNotFixSetOfST(){
        experimentFixSetOfGenes(NOT_FIX_ST);
    }
    
    public static void experimentNotFixSetOfLD(){
        experimentFixSetOfGenes(NOT_FIX_LD);
    }

    public static void experimentNotFixSetOfOD(){
        experimentFixSetOfGenes(NOT_FIX_OD);
    }

    public static void testTwoStageDrawDecider(int TSmiddle){
        System.out.println("===========================Test for TwoStageDrawParam == " + TSmiddle +" ==========================");
        ParamList defaulPL = new ParamList(new double[]{1});
        defaulPL.set(ParamList.TS_DRAW_MIDDLE, TSmiddle);
        
        EnsembleHandEvalPlayer MeldDrawPlayer = new EnsembleHandEvalPlayer(defaulPL, new DeadwoodHandEvaluator());
        MeldDrawPlayer.setDrawDecider(new MeldOnlyDrawDecider());
        MeldDrawPlayer.setKnockDecider(new TwoStageKnockDecider(defaulPL));

        EnsembleHandEvalPlayer DeadwoodDrawPlayer = new EnsembleHandEvalPlayer(defaulPL, new DeadwoodHandEvaluator());
        DeadwoodDrawPlayer.setDrawDecider(new DeadwoodDrawDecider());
        DeadwoodDrawPlayer.setKnockDecider(new TwoStageKnockDecider(defaulPL));

        EnsembleHandEvalPlayer TwoStageDrawPlayer = new EnsembleHandEvalPlayer(defaulPL, new DeadwoodHandEvaluator());
        TwoStageDrawPlayer.setDrawDecider(new TwoStageDrawDecider(defaulPL));
        TwoStageDrawPlayer.setKnockDecider(new TwoStageKnockDecider(defaulPL));

        int numGame = 2000;

        // System.out.println("Matches against each other");

        // TestingGame gameTSvsDD = new TestingGame(TwoStageDrawPlayer, DeadwoodDrawPlayer);
        // TestingGame gameTSvsMD = new TestingGame(TwoStageDrawPlayer, MeldDrawPlayer);
        // TestingGame gameMDvsDD = new TestingGame(MeldDrawPlayer, DeadwoodDrawPlayer);
        
        // int numTSwinDD = 0;
        // int numTSwinMD = 0;
        // int numMDwinDD = 0;

        // for(int i = 0; i < numGame; i++){
        //     numTSwinDD += gameTSvsDD.play() == 0 ? 1 : 0;
        //     numTSwinMD += gameTSvsMD.play() == 0 ? 1 : 0;
        //     numMDwinDD += gameMDvsDD.play() == 0 ? 1 : 0;
        // }
        // System.out.println("Num win of TwoStage Draw against Deadwood Draw: " + (double)numTSwinDD / numGame);
        // System.out.println("Num win of TwoStage Draw against Meld Draw: " + (double)numTSwinMD / numGame);
        // System.out.println("Num win of Meld Draw against Deadwood Draw: " + (double)numMDwinDD / numGame);
        

        System.out.println("Matches against Simple Player");
        SimpleFakeGinRummyPlayer simplePlayer = new SimpleFakeGinRummyPlayer(new ParamList(new double[0]));
        TestingGame gameTSvsSimple = new TestingGame(TwoStageDrawPlayer, simplePlayer);
        TestingGame gameDDvsSimple = new TestingGame(DeadwoodDrawPlayer, simplePlayer);
        TestingGame gameMDvsSimple = new TestingGame(MeldDrawPlayer, simplePlayer);
    
        int numTSwinSimple = 0;
        int numDDwinSimple = 0;
        int numMDwinSimple = 0;

        for(int i = 0; i < numGame; i++){
            numTSwinSimple += gameTSvsSimple.play() == 0 ? 1 : 0;
            numDDwinSimple += gameDDvsSimple.play() == 0 ? 1 : 0;
            numMDwinSimple += gameMDvsSimple.play() == 0 ? 1 : 0;
        }
        System.out.println("Num win of TwoStage Draw against Simple: " + (double)numTSwinSimple / numGame);
        System.out.println("Num win of Deadwood Draw against Simple: " + (double)numDDwinSimple / numGame);
        System.out.println("Num win of Meld Draw against Simple: " + (double)numMDwinSimple / numGame);
    }
    
    public static void main(String[] args) throws Exception {
        
        //just run 1 at a time
        // experimentNotFixSetOfCH();
        // experimentNotFixSetOfMC();
        // experimentNotFixSetOfST();
        // experimentNotFixSetOfLD();
        // experimentNotFixSetOfOD();

        // testTwoStageDrawDecider(5);
        // testTwoStageDrawDecider(14);
        // testTwoStageDrawDecider(22);

        run149C0();
        run149C3();
    }


}
