package experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.text.DefaultStyledDocument.ElementSpec;

import ga.GATuner;
import ga.StateTrackerTuner;
import games.TestingGame;
import players.ParamList;
import players.SimpleFakeGinRummyPlayer;
import players.TunableStateTracker;
import players.handeval.DeadwoodHandEvaluator;
import players.handeval.EnsembleHandEvalPlayer;
import players.handeval.KnockOnGinKnockDecider;
import players.handeval.MeldOnlyDrawDecider;
import players.handeval.TwoStageKnockDecider;

public class StateTrackerAnalyzer {

    double[] parameters;
    private static int genCount = 0;
    private static java.util.concurrent.atomic.AtomicInteger indivCount = new java.util.concurrent.atomic.AtomicInteger(0);


    StateTrackerAnalyzer(double[] parameters) throws Exception {
        if(parameters.length == 9){
            this.parameters = parameters;
        }
        else{
            throw new Exception("Parameters must consist of 9 parameters: " + parameters.length);
        }
    }
    
    public ParamList[] createSameIndividuals(int popSize){
        ParamList[] population = new ParamList[popSize];
        for(int i = 0; i < popSize; i++){
            population[i] = new ParamList(new double[]{1});
            population[i].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, this.parameters[0]);
            population[i].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, this.parameters[1]);
            population[i].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, this.parameters[2]);
            population[i].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, this.parameters[3]);
            population[i].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, this.parameters[4]);
            population[i].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, this.parameters[5]);
            population[i].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, this.parameters[6]);
            population[i].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, this.parameters[7]);
            population[i].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, this.parameters[8]);
            population[i].enforceRestrictions();
        }
        return population;
    }

    public static ArrayList<Double> calcErrorPerIndexStream(ParamList[] paramLists, int gamesPerIndividual, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        ArrayList<ParamList> allParamList = new ArrayList<>();
        for(int i = 0; i < paramLists.length; i++){
            allParamList.add(paramLists[i]);
        }
        double start, elapsed;
        List<ArrayList<Double>> errros = new ArrayList<ArrayList<Double>>();
        start = System.currentTimeMillis();
        errros = allParamList.stream().parallel().map((x) -> {return playManyGamesError(x, gamesPerIndividual, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);}).collect(Collectors.toList());
        elapsed = System.currentTimeMillis() - start;
        // System.out.println("Elapsed time in parallel: " + elapsed);
        indivCount.set(0); // reset the individual count now that calc fitness is done
        genCount++; // Increase for the next generation
        ArrayList<Double> errorPerTurnAVG = TunableStateTracker.getListPerIndexAVG((ArrayList<ArrayList<Double>>)errros);
        return errorPerTurnAVG;
    }

    public static ArrayList<Double> calcCountPerIndexStream(ParamList[] paramLists, int gamesPerIndividual, int numAdditionalCards, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        ArrayList<ParamList> allParamList = new ArrayList<>();
        for(int i = 0; i < paramLists.length; i++){
            allParamList.add(paramLists[i]);
        }
        double start, elapsed;
        List<ArrayList<Double>> counts = new ArrayList<ArrayList<Double>>();
        start = System.currentTimeMillis();
        counts = allParamList.stream().parallel().map((x) -> {return playManyGamesCount(x, gamesPerIndividual, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);}).collect(Collectors.toList());
        elapsed = System.currentTimeMillis() - start;
        // System.out.println("Elapsed time in parallel: " + elapsed);
        indivCount.set(0); // reset the individual count now that calc fitness is done
        genCount++; // Increase for the next generation
        ArrayList<Double> countPerTurnAVG = TunableStateTracker.getListPerIndexAVG((ArrayList<ArrayList<Double>>)counts);
        return countPerTurnAVG;
    }

    public static ArrayList<Double> playManyGamesError(ParamList params, int gamesPerIndividual, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        SimpleFakeGinRummyPlayer opp = GATuner.setupOpp(opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);

        TunableStateTracker myTracker = new TunableStateTracker(params, opp);
        EnsembleHandEvalPlayer self = new EnsembleHandEvalPlayer(params, myTracker, new DeadwoodHandEvaluator());
        self.setKnockDecider(new KnockOnGinKnockDecider());
        self.setDrawDecider(new MeldOnlyDrawDecider());

        TestingGame gameManager = new TestingGame(self, opp);
        TestingGame.setPlayVerbose(false);

        System.out.print("(" + genCount + ";" + indivCount.incrementAndGet() + ")");
        for(int i = 0; i < gamesPerIndividual; i++){
            gameManager.play();
        }
        
        ArrayList<Double> errorPerTurnAVG = myTracker.getErrorPerIndexAVG();
        return errorPerTurnAVG;
    }

    public static ArrayList<Double> playManyGamesCount(ParamList params, int gamesPerIndividual, int numAdditionalCards, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        SimpleFakeGinRummyPlayer opp = GATuner.setupOpp(opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);

        TunableStateTracker myTracker = new TunableStateTracker(params, opp, numAdditionalCards);
        EnsembleHandEvalPlayer self = new EnsembleHandEvalPlayer(params, myTracker, new DeadwoodHandEvaluator());
        self.setKnockDecider(new KnockOnGinKnockDecider());
        self.setDrawDecider(new MeldOnlyDrawDecider());

        TestingGame gameManager = new TestingGame(self, opp);
        TestingGame.setPlayVerbose(false);

        System.out.print("(" + genCount + ";" + indivCount.incrementAndGet() + ")");
        for(int i = 0; i < gamesPerIndividual; i++){
            gameManager.play();
        }
        
        ArrayList<Double> countPerTurnAVG = myTracker.getCountPerIndexAVG();
        return countPerTurnAVG;
    }

    public static ArrayList<Double> avgErrorsPerTurn(double[] parameters, int totalGames, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag) throws Exception{
        int numParallel = 12;
        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_ERROR);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.EVERY_TURN);
        TunableStateTracker.setPerTurn(true);
        StateTrackerAnalyzer stAnalyzer = new StateTrackerAnalyzer(parameters);
        int gamesPerIndividual = totalGames/numParallel + 1;
        ParamList[] population = stAnalyzer.createSameIndividuals(numParallel);
        ArrayList<Double> errorPerTurnAVG = calcErrorPerIndexStream(population, gamesPerIndividual, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("\nThe longest turn was: " + errorPerTurnAVG.size());
        System.out.println(errorPerTurnAVG);
        return errorPerTurnAVG;
    }

    public static ArrayList<Double> avgCountsPerTurn(double[] parameters, int totalGames, int numAdditionalCards, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag) throws Exception{
        int numParallel = 12;
        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_COUNT);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.EVERY_TURN);
        TunableStateTracker.setPerTurn(true);
        StateTrackerAnalyzer stAnalyzer = new StateTrackerAnalyzer(parameters);
        int gamesPerIndividual = totalGames/numParallel + 1;
        ParamList[] population = stAnalyzer.createSameIndividuals(numParallel);
        ArrayList<Double> countPerTurnAVG = calcCountPerIndexStream(population, gamesPerIndividual, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("\nThe longest turn was: " + countPerTurnAVG.size());
        System.out.println(countPerTurnAVG);
        return countPerTurnAVG;
    }

    public static ArrayList<Double> avgErrorsPerOppCardsUnKnown(double[] parameters, int totalGames, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag) throws Exception{
        int numParallel = 12;
        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_ERROR);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.EVERY_TURN);
        TunableStateTracker.setPerOppCardsKnown(true);
        StateTrackerAnalyzer stAnalyzer = new StateTrackerAnalyzer(parameters);
        int gamesPerIndividual = totalGames/numParallel + 1;
        ParamList[] population = stAnalyzer.createSameIndividuals(numParallel);
        ArrayList<Double> errorPerTurnAVG = calcErrorPerIndexStream(population, gamesPerIndividual, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("\nThe size is: " + errorPerTurnAVG.size());
        System.out.println(errorPerTurnAVG);
        return errorPerTurnAVG;
    }

    public static ArrayList<Double> avgCountsPerOppCardsUnKnown(double[] parameters, int totalGames, int numAdditionalCards, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag) throws Exception{
        int numParallel = 12;
        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_COUNT);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.EVERY_TURN);
        TunableStateTracker.setPerOppCardsKnown(true);
        StateTrackerAnalyzer stAnalyzer = new StateTrackerAnalyzer(parameters);
        int gamesPerIndividual = totalGames/numParallel + 1;
        ParamList[] population = stAnalyzer.createSameIndividuals(numParallel);
        ArrayList<Double> countPerTurnAVG = calcCountPerIndexStream(population, gamesPerIndividual, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        System.out.println("\nThe size is: " + countPerTurnAVG.size());
        System.out.println(countPerTurnAVG);
        return countPerTurnAVG;
    }

    // analyze game length 
    // self uses TwoStageKnockDecider (10, 0, 6)
    public static ArrayList<Double> analyzeGameLength(ParamList params, int totalGames, 
                                        int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        
        SimpleFakeGinRummyPlayer p1 = GATuner.setupOpp(opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        
        TunableStateTracker st0 = new TunableStateTracker(params, p1);
        EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(params, st0, new DeadwoodHandEvaluator());
        p0.setKnockDecider(new TwoStageKnockDecider(params));
        p0.setDrawDecider(new MeldOnlyDrawDecider());
    
        ExperimentalRuns.fixTwoStageKnockDecider101();

        TestingGame gameManager = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(false);
        TunableStateTracker.setFitnessMethod(TunableStateTracker.NO_FITNESS);
        params.enforceRestrictions();

        for(int j = 0; j < totalGames; j++){
            gameManager.play();
        }

        ArrayList<Double> numSelfTurns = st0.getNumTurns();
        Collections.sort(numSelfTurns);
        double mean = ExperimentalRuns.mean(numSelfTurns);
        double sd = ExperimentalRuns.sd(numSelfTurns);
        double median = ExperimentalRuns.median(numSelfTurns);
        double min = numSelfTurns.get(0);
        double max = numSelfTurns.get(numSelfTurns.size()-1);
        System.out.println("Number of samples: " + numSelfTurns.size());
        System.out.println("mean: " + mean + "\nsd: " + sd + "\nmedian: " + median + "\nmin: " + min + "\nmax: " + max);
        return numSelfTurns;
    }

    public static void main(String[] args) throws Exception {
        double[] parameters = new double[]{0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565};
        int totalGames = 48;
        int numAdditionalCards = 0;
        // int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        // int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        // int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        // avgCountsPerTurn(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        // avgErrorsPerTurn(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);

        // avgErrorsPerOppCardsUnKnown(parameters, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        // avgCountsPerOppCardsUnKnown(parameters, totalGames, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        ParamList params = new ParamList(new double[]{1});
        analyzeGameLength(params, totalGames, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
    }
}