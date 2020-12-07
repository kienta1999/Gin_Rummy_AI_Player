package ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import games.TestingGame;
// import players.KnockOnGinPlayer;
//import games.GATestingGame;
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
import players.handeval.MultiOppHandMeldabilityEvaluator;
import players.handeval.OneStageKnockDecider;
import players.handeval.LinearDeadwoodPenaltyHandEvaluator;
import players.handeval.OppCardsKnownDeadwoodPenaltyHandEvaluator;
import players.handeval.TwoStageKnockDecider;
import players.handeval.ScorePredictionKnockDecider;
import players.handeval.TwoStageDrawDecider;
import players.handeval.KnockOnGinKnockDecider;

import players.handeval.MeldOnlyDrawDecider;

import java.util.stream.DoubleStream;


public class GATuner {

    private static boolean TESTING = false;
    private static boolean GATESTING = false;
    
    private static final double SELECT_LOSER_CHANCE = 0.01;

    //Self's Players
    //default: for any other value, self is index player
    public static final int SELF_IS_INDEX_PLAYER = 0;
    public static final int SELF_IS_ENSEMBLE_PLAYER = 1;

    //Self's DrawDeciders
    public static final int SELF_DEADWOOD_DRAW_DECIDER = 10;
    public static final int SELF_MELD_ONLY_DRAW_DECIDER = 11;
    public static final int SELF_CHOOSE_10FROM11_DRAW_DECIDER = 12;
    public static final int SELF_TWO_STAGE_DRAW_DECIDER = 13;

    //Self's KnockDeciders
    //default: for any other value, self is using knock on gin strategy
    public static final int SELF_ONE_STAGE_KNOCK_DECIDER = 20;
    public static final int SELF_TWO_STAGE_KNOCK_DECIDER = 21;
    public static final int SELF_SCORE_PREDICTION_KNOCK_DECIDER = 22;
    public static final int SELF_KNOCK_ON_GIN_KNOCK_DECIDER = 23;

    //Opp's Players
    //default: for any other value, opp is simple player
    public static final int OPP_IS_SIMPLE_PLAYER = 30;
    public static final int OPP_IS_KNOCK_ON_GIN_PLAYER = 31;
    public static final int OPP_IS_ENSEMBLE_PLAYER = 32;
    public static final int OPP_IS_INDEX_PLAYER = 33;

    //Opp's DrawDeciders
    public static final int OPP_DEADWOOD_DRAW_DECIDER = 40;
    public static final int OPP_MELD_ONLY_DRAW_DECIDER = 41;
    public static final int OPP_CHOOSE_10FROM11_DRAW_DECIDER = 42;
    public static final int OPP_TWO_STAGE_DRAW_DECIDER = 43;

    //Opp's KnockDeciders
    public static final int OPP_ONE_STAGE_KNOCK_DECIDER = 50;
    public static final int OPP_TWO_STAGE_KNOCK_DECIDER = 51;
    public static final int OPP_SCORE_PREDICTION_KNOCK_DECIDER = 52;
    public static final int OPP_KNOCK_ON_GIN_KNOCK_DECIDER = 53;

    // public static final boolean WANT_CROSSOVER = ;

    public static ParamList paramListForOppEnsemble;
    public static ParamList paramListForOppIndex;

    static{
        // paramListForOppEnsemble = ParamList.getRandomParamList(6);
        // paramListForOppIndex = ParamList.getRandomParamList(6);
       // ParamList.setFixedValue(id, value);
        
        double[] paramsAndWeightsEnsemble = {0.29208164794833336, 0.4933043961984872, 0.2146139558531794, 0.039082238420405646, 0.4097065894571512, 0.34316571780522437, 0.6568342821947756, 0.17000003329459223, 0.9565683621930221, 0.4008540338225604, 0.5991459661774395, 0.8164209844981123, 0.33923897237621314, 0.9686458628844807, 0.8985860747919318, 0.7824084985947584, 0.3507563695181819, 0.9451028368829781, 0.3677917221207484, 0.21958246619065547, 1.0, 4.0, 0.8115237600349762, 3.9962362572044015, 0.030086775107394215, 3.834926979775834, 2.0, 1.0, 16.0, 6.0, 10.0, 0.05411308380226888, 0.08453569036529046, 0.2157851074819329, 0.15142572449844588, 0.01187131896265894, 0.482269074889403};
        double[] paramsAndWeightsIndex = {0.297846634228479, 0.5307736296104318, 0.1713797361610891, 0.29255272829711787, 0.4264842655420431, 0.005900959676350176, 0.9940990403236498, 0.14726847481837535, 0.6413552516589862, 0.6253135922746896, 0.3746864077253105, 0.8205109244378551, 0.7537877191694062, 0.2636731048778447, 0.5997579860260097, 0.3014951713495917, 0.8613875037443127, 0.9860845921055696, 0.3779128308601424, 0.273733622598187, 5.0, 2.0, 0.4299428886711003, 2.682439409466846, 0.7753331058928833, 1.5889333631616411, 7.0, 2.0, 19.0, 6.0, 10.0, 0.01842709733747154, 0.1170731249086796, 0.5623925451979919, 0.1117587456460728, 0.1046406428044201, 0.085707844105364};
        int numEnsembleWeights = 6;
        paramListForOppEnsemble = new ParamList(paramsAndWeightsEnsemble, numEnsembleWeights);
        paramListForOppIndex = new ParamList(paramsAndWeightsIndex, numEnsembleWeights);
    }
    
    public static void setParamListForOppEnsemble(ParamList other){
        paramListForOppEnsemble = other;
    }
    
    public static void setParamListForOppIndex(ParamList other){
        paramListForOppIndex = other;
    }
    
    public ParamList[] createRandomIndividuals(int popSize, int numOfEvaluators){
        Random r = new Random();
        ParamList[] population = new ParamList[popSize];
        for(int i = 0; i<popSize; i++){
           population[i] = ParamList.getRandomParamList(numOfEvaluators);
        }
        return population;
    }
  
    private static int genCount = 0;
    private static java.util.concurrent.atomic.AtomicInteger indivCount = new java.util.concurrent.atomic.AtomicInteger(0);

    public static EnsembleHandEvalPlayer setupSelf(ParamList params, int selfKnockDeciderFlag, int selfFlag, int selfDrawDeciderFlag){
        
        //create the player
        EnsembleHandEvalPlayer p0 = null;
        LinearDeadwoodPenaltyHandEvaluator linearHe = new LinearDeadwoodPenaltyHandEvaluator(params);
        OppCardsKnownDeadwoodPenaltyHandEvaluator oppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params);
        if(selfFlag == SELF_IS_INDEX_PLAYER){
            p0 = new IndexEnsembleHandEvalPlayer(params, new MeldabilityHandEvaluator(params), 
                                                            new DeadwoodHandEvaluator(), 
                                                            new AceTwoBonusHandEvaluator(), 
                                                            new ConvHandEvaluator(params), 
                                                            linearHe,
                                                            oppCardsHe,
                                                            new MultiOppHandMeldabilityEvaluator(params)
                                                            );
            linearHe.setEnsemblePlayer(p0);
            oppCardsHe.setEnsemblePlayer(p0);
        }
        else if(selfFlag == SELF_IS_ENSEMBLE_PLAYER){
            MeldabilityHandEvaluator mhe = new MeldabilityHandEvaluator(params);
            ConvHandEvaluator che = new ConvHandEvaluator(params);
            MultiOppHandMeldabilityEvaluator multiOppHE = new MultiOppHandMeldabilityEvaluator(params);
            mhe.setShouldNormalize(true);
            che.setShouldNormalize(true);
            multiOppHE.setShouldNormalize(true);
            p0 = new EnsembleHandEvalPlayer(params, mhe, 
                                                    new DeadwoodHandEvaluator(), 
                                                    new AceTwoBonusHandEvaluator(), 
                                                    che, 
                                                    linearHe, 
                                                    oppCardsHe,
                                                    multiOppHE
                                                    );

            linearHe.setEnsemblePlayer(p0);
            oppCardsHe.setEnsemblePlayer(p0);
        }
        else{
            throw new RuntimeException("Self flag not available");
        }

        //Knock Decider
        if(selfKnockDeciderFlag == SELF_ONE_STAGE_KNOCK_DECIDER){
            p0.setKnockDecider(new OneStageKnockDecider(params));
        }
        else if(selfKnockDeciderFlag == SELF_TWO_STAGE_KNOCK_DECIDER){
            p0.setKnockDecider(new TwoStageKnockDecider(params));
        }
        else if(selfKnockDeciderFlag == SELF_SCORE_PREDICTION_KNOCK_DECIDER){
            p0.setKnockDecider(new ScorePredictionKnockDecider(params));
        }
        else if(selfKnockDeciderFlag == SELF_KNOCK_ON_GIN_KNOCK_DECIDER){
            p0.setKnockDecider(new KnockOnGinKnockDecider());
        }
        else{
            throw new RuntimeException("Knock decider flag for self not available");
        }
        //draw decider
        if(selfDrawDeciderFlag == SELF_DEADWOOD_DRAW_DECIDER){
            p0.setDrawDecider(new DeadwoodDrawDecider());
        }
        else if(selfDrawDeciderFlag == SELF_CHOOSE_10FROM11_DRAW_DECIDER){
            p0.setDrawDecider(new Choose10From11DrawDecider(p0));
        }
        else if(selfDrawDeciderFlag == SELF_MELD_ONLY_DRAW_DECIDER){
            p0.setDrawDecider(new MeldOnlyDrawDecider());
        }
        else if(selfDrawDeciderFlag == SELF_TWO_STAGE_DRAW_DECIDER){
            p0.setDrawDecider(new TwoStageDrawDecider(params));
        }
        else{
            throw new RuntimeException("DrawDecider flag for self not available: " + selfDrawDeciderFlag);
        }

        return p0;
    }
    
    public static SimpleFakeGinRummyPlayer setupOpp(int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        SimpleFakeGinRummyPlayer p1 = null;
        ParamList oppParams = null;
        if(opponentFlag == OPP_IS_SIMPLE_PLAYER){
            p1 = new SimpleFakeGinRummyPlayer(ParamList.getRandomParamList(0));
        }
        else if (opponentFlag == OPP_IS_KNOCK_ON_GIN_PLAYER){
            p1 = new EnsembleHandEvalPlayer(new ParamList(new double[]{1}), new DeadwoodHandEvaluator());
            ((EnsembleHandEvalPlayer)p1).setKnockDecider(new KnockOnGinKnockDecider());
        }
        else if(opponentFlag == OPP_IS_ENSEMBLE_PLAYER){
            oppParams = paramListForOppEnsemble;
            LinearDeadwoodPenaltyHandEvaluator oppLinearHe = new LinearDeadwoodPenaltyHandEvaluator(oppParams);
            OppCardsKnownDeadwoodPenaltyHandEvaluator oppOppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(oppParams);
            MeldabilityHandEvaluator oppMhe = new MeldabilityHandEvaluator(oppParams);
            ConvHandEvaluator oppChe = new ConvHandEvaluator(oppParams);
            oppMhe.setShouldNormalize(true);
            oppChe.setShouldNormalize(true);
            p1 = new EnsembleHandEvalPlayer(oppParams, oppMhe, new DeadwoodHandEvaluator(),new AceTwoBonusHandEvaluator(), 
                                            oppChe, //new MultiOppHandMeldabilityEvaluator(oppParams),
                                            oppLinearHe, oppOppCardsHe);

            oppLinearHe.setEnsemblePlayer((EnsembleHandEvalPlayer)p1);
            oppOppCardsHe.setEnsemblePlayer((EnsembleHandEvalPlayer)p1);
        }
        else if(opponentFlag == OPP_IS_INDEX_PLAYER){
            oppParams = paramListForOppIndex;
            LinearDeadwoodPenaltyHandEvaluator oppLinearHe = new LinearDeadwoodPenaltyHandEvaluator(oppParams);
            OppCardsKnownDeadwoodPenaltyHandEvaluator oppOppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(oppParams);
            MeldabilityHandEvaluator oppMhe = new MeldabilityHandEvaluator(oppParams);
            ConvHandEvaluator oppChe = new ConvHandEvaluator(oppParams);
            p1 = new IndexEnsembleHandEvalPlayer(oppParams, oppMhe, new DeadwoodHandEvaluator(),new AceTwoBonusHandEvaluator(), 
                                            oppChe, //new MultiOppHandMeldabilityEvaluator(oppParams),
                                            oppLinearHe, oppOppCardsHe);
                                            
            oppLinearHe.setEnsemblePlayer((EnsembleHandEvalPlayer)p1);
            oppOppCardsHe.setEnsemblePlayer((EnsembleHandEvalPlayer)p1);
           
            // ((EnsembleHandEvalPlayer)p1).setDrawDecider(new MeldOnlyDrawDecider());
            // ((EnsembleHandEvalPlayer)p1).setKnockDecider(new KnockOnGinKnockDecider());
        }
        else{
            throw new RuntimeException("Opp flag not available");
        }

        if(opponentFlag == OPP_IS_ENSEMBLE_PLAYER || opponentFlag == OPP_IS_INDEX_PLAYER || opponentFlag == OPP_IS_KNOCK_ON_GIN_PLAYER){
            if(opponentFlag!=OPP_IS_KNOCK_ON_GIN_PLAYER){
                if(oppKnockDeciderFlag == OPP_ONE_STAGE_KNOCK_DECIDER){
                        ((EnsembleHandEvalPlayer)p1).setKnockDecider(new OneStageKnockDecider(oppParams));
                }
                else if(oppKnockDeciderFlag == OPP_TWO_STAGE_KNOCK_DECIDER){
                        ((EnsembleHandEvalPlayer)p1).setKnockDecider(new TwoStageKnockDecider(oppParams));
                }
                else if(oppKnockDeciderFlag == OPP_SCORE_PREDICTION_KNOCK_DECIDER){
                        ((EnsembleHandEvalPlayer)p1).setKnockDecider(new ScorePredictionKnockDecider(oppParams));
                }
                else if(oppKnockDeciderFlag == OPP_KNOCK_ON_GIN_KNOCK_DECIDER){
                        ((EnsembleHandEvalPlayer)p1).setKnockDecider(new KnockOnGinKnockDecider());
                }
                else{
                    throw new RuntimeException("Knock decider for opp flag not available");
                }
            }
           
            if(oppDrawDeciderFlag == OPP_DEADWOOD_DRAW_DECIDER){
                ((EnsembleHandEvalPlayer)p1).setDrawDecider(new DeadwoodDrawDecider());
            }
            else if(oppDrawDeciderFlag == OPP_CHOOSE_10FROM11_DRAW_DECIDER){
                ((EnsembleHandEvalPlayer)p1).setDrawDecider(new Choose10From11DrawDecider((EnsembleHandEvalPlayer)p1));
            }
            else if(oppDrawDeciderFlag == OPP_MELD_ONLY_DRAW_DECIDER){
                ((EnsembleHandEvalPlayer)p1).setDrawDecider(new MeldOnlyDrawDecider());
            }
            else if(oppDrawDeciderFlag == OPP_TWO_STAGE_DRAW_DECIDER){
                ((EnsembleHandEvalPlayer)p1).setDrawDecider(new TwoStageDrawDecider(oppParams));
            }
            else{
                throw new RuntimeException("DrawDecider flag for opp not available");
            }
        }
        return p1;
    }
    
    public static double playManyGames(ParamList params, int selfKnockDeciderFlag, int opponentFlag, int selfFlag, int gamesPerIndividual, int selfDrawDeciderFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        // ParamList param = new ParamList(paramAL);
        EnsembleHandEvalPlayer p0 = setupSelf(params, selfKnockDeciderFlag, selfFlag, selfDrawDeciderFlag);
        
        // ParamList oppParams = ParamList.getRandomParamList(6);
        SimpleFakeGinRummyPlayer p1 = setupOpp(opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        int p0Wins;

        TestingGame gameManager = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(false);
        p0Wins = 0;
        params.enforceRestrictions(); //player0.normalizeWeights();
        //p0.setParamList(params);

        System.out.print("(" + genCount + ":" + indivCount.incrementAndGet() + ")");
        for(int j = 0; j < gamesPerIndividual; j++){
            int winner = gameManager.play();
            if(winner == 0)
                p0Wins += 1; 
        }

        return (double)p0Wins / gamesPerIndividual;
    }

    public double[] calcFitnessStream(ParamList[] paramLists, int gamesPerIndividual, int selfFlag, int selfKnockDeciderFlag,  int selfDrawDeciderFlag, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        ArrayList<ParamList> allParamList = new ArrayList<>();
        for(int i = 0; i < paramLists.length; i++){
            allParamList.add(paramLists[i]);
        }
        double start, elapsed;
        double[] fitnessValue = new double[paramLists.length];
        start = System.currentTimeMillis();                 
        fitnessValue = allParamList.stream().parallel().map((x) -> {return playManyGames(x, selfKnockDeciderFlag, opponentFlag, selfFlag, gamesPerIndividual, selfDrawDeciderFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);}).mapToDouble(x -> {return x;}).toArray();
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time in parallel: " + elapsed);
        indivCount.set(0); // reset the individual count now that calc fitness is done
        genCount++; // Increase for the next generation
        return fitnessValue;
    }

    public double[] calcFitnessLoop(ParamList[] paramLists, int numOfEvaluators, int selfKnockDeciderFlag, int opponentFlag, int selfFlag, int gamesPerIndividual, int selfDrawDeciderFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        // double start, elapsed;
       
        double[] fitnessValue = new double[paramLists.length];
        for(int s = 0; s < fitnessValue.length; s++) {
            fitnessValue[s] = 0;
        }
        
        ParamList dummyParams = ParamList.getRandomParamList(numOfEvaluators);
        EnsembleHandEvalPlayer p0 = setupSelf(dummyParams, selfKnockDeciderFlag, selfFlag, selfDrawDeciderFlag);
        SimpleFakeGinRummyPlayer p1 = setupOpp(opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        
        int p0Wins;

        TestingGame gameManager = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(false);

        
        // int weight = 0;
       // player0.addHandEvaluator(new DeadwoodHandEvaluator(), weight);
       // player0.addHandEvaluator(new ConvHandEvaluator(dummyParams), weight);
       // player0.addHandEvaluator(new MeldabilityHandEvaluator(dummyParams), weight);
       // player0.addHandEvaluator(new AceTwoBonusHandEvaluator(), weight);
        //start = System.currentTimeMillis();
        for(int i = 0; i < paramLists.length; i++){
            p0Wins = 0;
            ParamList params = paramLists[i]; 
            params.enforceRestrictions(); //player0.normalizeWeights();
            p0.setParamList(params); //hopefully, not adding more weights but chnaging the dummy place holder's weights
            if(TESTING) System.out.println("Individual " + i + " is: " + params);
            
            for(int j = 0; j < gamesPerIndividual; j++){
                int winner = gameManager.play();
                if(winner == 0)
                    p0Wins += 1; 
            }
            double fitness = p0Wins;
            fitnessValue[i] += fitness;
        }
        //elapsed = System.currentTimeMillis() - start;
        //System.out.println("Elapsed time in parallel: " + elapsed);

        if(TESTING){
            for(int k = 0; k < paramLists.length; k++)
                System.out.println("\nThe number of wins for individual " + k + " is " + fitnessValue[k]); 
        }
        
        for(int l = 0; l < fitnessValue.length; l++)
            fitnessValue[l] = (double) fitnessValue[l]/gamesPerIndividual;

        return fitnessValue;
    }

    public static int pickNRandomAndReturnBest(ArrayList<Integer> populationIndex, double[] fitnesses, int tournamentSize){
        if(tournamentSize > populationIndex.size()){
            throw new RuntimeException("tournament size should be smaller or equal to population size");
        }
        Random r = new Random();
        ArrayList <Integer> chosenIndividualIndices = new ArrayList<>();
        ArrayList<Integer> populationIndexCopy = new ArrayList<>(populationIndex);
        for(int i = 0; i < tournamentSize; i++){
            int index = r.nextInt(populationIndexCopy.size());
            chosenIndividualIndices.add(populationIndexCopy.get(index));
            populationIndexCopy.remove(index);
        }
        int bestIndex = chosenIndividualIndices.get(0);
        for(int i = 1; i < tournamentSize; i++){
            if(fitnesses[bestIndex] < fitnesses[chosenIndividualIndices.get(i)]){
                bestIndex = chosenIndividualIndices.get(i);
            }
        }

        if(TESTING){
            System.out.println("All fitness: " + Arrays.toString(fitnesses));
            System.out.println("Chosen tournament: " + chosenIndividualIndices);
            System.out.print("Fitness of chosen tournament: ");
            for(int i = 0; i < chosenIndividualIndices.size(); i++){
                System.out.print(fitnesses[chosenIndividualIndices.get(i)] + " ");
            }
            System.out.println();
            System.out.println("Best in the tournament: " + bestIndex);
        }

        return bestIndex;
    }

    public ParamList[] select(ParamList[] population, double[] fitnesses, int tournamentSize){
        // Random r = new Random();
        // ParamList[] SelectedIndividuals = new ParamList[tournamentSize]; 
        ArrayList<Integer> populationIndex = new ArrayList<>();
        for(int i = 0; i < population.length; i++){
            populationIndex.add(i);
        }
        int index1 = pickNRandomAndReturnBest(populationIndex, fitnesses, tournamentSize);
        populationIndex.remove(index1);
        // System.out.println("Remaining indices: " + populationIndex);
        int index2 = pickNRandomAndReturnBest(populationIndex, fitnesses, tournamentSize);

        if(TESTING){
            System.out.println("The chosen individual: " + index1 + " and " + index2);
            System.out.println("Fitness of index1 : " + fitnesses[index1]);
            System.out.println("Fitness of index2 : " + fitnesses[index2]);
        }

        ParamList parent1 = population[index1];
        ParamList parent2 = population[index2];


        return new ParamList[]{parent1, parent2};
    }

    public static void testSelect(){
        int popSize = 30;
        ParamList[] population = new ParamList[popSize];
        int tournamentSize = 15;
        double[] fitnesses = new double[population.length];
        for(int i = 0; i < popSize; i++){
            fitnesses[i] = (int)(Math.random() * 100);
        }
        GATuner ga = new GATuner();
        ga.select(population, fitnesses, tournamentSize);
        
    }
        /*
        if your population has 20: 0, 1, 2, .. 19
        tournamentSize == 5             ^
        pick 2 tournament0, 5, 8, 17, 19 () and (7, 9, 6, 10, 11)
        choose 19 and 11 if 19 and 11 have best fitness 
        
        pick one tournament (0, 5, 8, 17, 19)
        choose 5
        remove 5
        pick another tournament (from the rest...)
        remove x
        



        
        */
        
        /*ParamList[] twoSelectedIndividuals = new ParamList[2]; 

        // Calculate the sum of all the fitnesses
        double sumOfFitnessWithLoserChance = 0;
        for(int i = 0; i < fitnesses.length; i++)
            sumOfFitnessWithLoserChance += fitnesses[i] + SELECT_LOSER_CHANCE; // adds a small chance for every fitness, so even 0 fitness individuals can be selected
            
        // Calculate selection probabilities based on fitnesses
        LinkedList<Double> selectionProb = new LinkedList<Double>();
        for(int i = 0; i < fitnesses.length; i++)
            selectionProb.add((fitnesses[i] + SELECT_LOSER_CHANCE)/sumOfFitnessWithLoserChance);
        
        if(TESTING) { // print selection probabilities
            for(int i = 0; i<selectionProb.size(); i++)
                System.out.println("Selection probability of individual " + i  + " with fitness value: " + fitnesses[i]  + " is " + selectionProb.get(i));
        }

        // Make ArrayList of individuals, to select from
        ArrayList<ParamList> eligibleIndividuals = new ArrayList<ParamList>(population.length);
        for(int i = 0; i < population.length; i++)
            eligibleIndividuals.add(population[i]);
        
        Random r = new Random();
        //choose 2 individuals
        for(int choiceID = 0; choiceID < 2; choiceID++){
            double randomValue = r.nextDouble();
            
            if (TESTING) {
                System.out.println("RandomValue: " + randomValue);
                double t = 0;
                for (int j = 0; j < eligibleIndividuals.size(); j++) {
                    t += selectionProb.get(j);
                    System.out.println("Individual " + j + " has range [" + (t-selectionProb.get(j)) + ", " + t + "]");
                }
            }
            
            // Find the probability range corresponding to randomValue
            int selectedID = -1;
            double sum = 0;
            while(randomValue>=sum){
                selectedID++;
                sum+=selectionProb.get(selectedID);
            }
            
            if(TESTING) System.out.println("Selected individual is: " + selectedID);

            // Select individual at index selectedID
            twoSelectedIndividuals[choiceID] = eligibleIndividuals.get(selectedID);

            // Remove this individual from the structures, so it isn't chosen a second time
            eligibleIndividuals.remove(selectedID);
            selectionProb.remove(selectedID);

            // Renormalize the probabilities, since we removed one
            double normsum = 0;
            for(int s = 0; s<selectionProb.size(); s++)
                 normsum+= selectionProb.get(s);
            for(int m = 0; m<selectionProb.size(); m++)
                selectionProb.set(m , selectionProb.get(m)/normsum);
        }

        return twoSelectedIndividuals;*/
    

    public ParamList[] crossover(ParamList parent1, ParamList parent2, int numOfEvaluators){
        ParamList[] children = parent1.twoPointsCrossover(parent2, numOfEvaluators); //we might consider changing this or randomising between single point and two points
        return children;
    }

    public void mutate(ParamList[] population, double mutationChance){
        for(int individualIndex = 0; individualIndex<population.length; individualIndex++){
            population[individualIndex].mutate(mutationChance);
        }
    }
    
    public static ParamList geneticAlgorithm(int popSize, int numGenerations, int gamesPerIndividual, int numOfEvaluators, double mutationChance, int selfFlag, int selfDrawDeciderFlag, int selfKnockDeciderFlag, int opponentFlag,  int oppDrawDeciderFlag, int oppKnockDeciderFlag, boolean wantCrossover, int tournamentSize){
        ParamList bestParamList = null;
        double bestFitnessAcrossGeneration = 0;

        if (GATESTING) System.out.println("=========================\n====GATESTING=========\n=========================\n");
        double averageFitnessThisGen;
        ArrayList<Double> averageFitnessEachGen = new ArrayList<Double>(numGenerations);
        ArrayList<Double> bestFitnessEachGen = new ArrayList<Double>(numGenerations);
        GATuner ga = new GATuner();
        
        if (GATESTING) System.out.println("-------------------\nCalling createRandomIndividuals");
        ParamList[] population = ga.createRandomIndividuals(popSize, numOfEvaluators); // need to review for the second parameter
        
        if (GATESTING) System.out.println("-------------------\nCalling calcFitness");
        double[] fitness = ga.calcFitnessStream(population, gamesPerIndividual, selfFlag, selfKnockDeciderFlag,  selfDrawDeciderFlag, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        //double[] fitness = ga.calcFitnessLoop(population, numOfEvaluators, selfKnockDeciderFlag, opponentFlag, selfFlag, gamesPerIndividual, selfDrawDeciderFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        int generationID = 0;
        int nextGenTracker; 
        if (GATESTING) System.out.println("Fitnesses:\n" + java.util.Arrays.toString(fitness));

        // find individual with best fitness, and log results
        if (GATESTING) System.out.println("-------------------\nFinding best individual");
        double[] results = findFitnessSumAndBestIndex(fitness);
        double sum = results[0];
        int indexOfBest = (int) results[1];
        double bestFitness = fitness[indexOfBest];
        averageFitnessThisGen = sum / population.length;
        averageFitnessEachGen.add(averageFitnessThisGen);
        bestFitnessEachGen.add(bestFitness);
        System.out.println("-------------------\nThe best individual of generation " + generationID + " is: " + population[indexOfBest]);
        System.out.println("The best individual's fitness value is: " + bestFitness);
        System.out.println("The average fitness this generation is: " + averageFitnessThisGen);
        System.out.println("Across generations, averageFitness: " + averageFitnessEachGen);
        System.out.println("Across generations, bestFitness: " + bestFitnessEachGen);
        ParamList eliteIndividual = new ParamList(population[indexOfBest]);
        //if (GATESTING) System.out.println("eliteIndividual: " + eliteIndividual + ", " + eliteIndividual.getGraphicalRepresentation());
        if(bestFitnessAcrossGeneration < bestFitness){
            bestFitnessAcrossGeneration = bestFitness;
            bestParamList = eliteIndividual;
        }
        while(generationID < numGenerations){
            if (GATESTING) {
                System.out.println("===========================================");
                System.out.println("Generation: " + generationID);
                System.out.println("Population: ");
                //System.out.println(java.util.Arrays.toString(population));                
                //for(int indivID = 0; indivID < population.length; indivID++){}
                    //System.out.println(population[indivID].getGraphicalRepresentation());
            }

            // Make the next generation
            if (GATESTING) System.out.println("-------------------\nMaking next generation");
            nextGenTracker = 0;
            ParamList[] nextGen = new ParamList[population.length];
            while(nextGenTracker < population.length-1){ // -1 so that we save space for the eliteIndividual
                if (GATESTING) System.out.println("-----\nnextGenTracker: " + nextGenTracker);
                ParamList[] selectedIndividuals = ga.select(population, fitness, tournamentSize);

                if (GATESTING) {
                    System.out.println("--\nselectedIndividuals:");
                    for(int indivID = 0; indivID < selectedIndividuals.length; indivID++){}
                        //System.out.println(selectedIndividuals[indivID].getGraphicalRepresentation());
                }
                ParamList[] newIndividuals = null;
                if(wantCrossover)
                {
                    newIndividuals = ga.crossover(selectedIndividuals[0], selectedIndividuals[1], numOfEvaluators);
                }
                else{
                    newIndividuals = selectedIndividuals;
                }
                if (GATESTING) {
                    System.out.println("--\nnewIndividuals:");
                    //for(int indivID = 0; indivID < newIndividuals.length; indivID++){}
                        //System.out.println(newIndividuals[indivID].getGraphicalRepresentation());
                }

                nextGen[nextGenTracker] = newIndividuals[0];           
                //if (GATESTING) System.out.println("Next gen now contains child 1: " + nextGen[nextGenTracker].getGraphicalRepresentation());
                nextGenTracker++;
                if(nextGenTracker < population.length-1) {// -1 so that we save space for the eliteIndividual
                    nextGen[nextGenTracker] = newIndividuals[1];
                    //if (GATESTING) System.out.println("Next gen now contains child 2: " + nextGen[nextGenTracker].getGraphicalRepresentation());
                    nextGenTracker++;
                }
            }
            double [] dummyEnsembleWeights = new double[numOfEvaluators];
            nextGen[nextGen.length-1] = new ParamList(dummyEnsembleWeights); // dummy placeholder for mutation. Elite individual will go here

            // apply mutation
            if (GATESTING) System.out.println("-------------------\nMutating");
            ga.mutate(nextGen, mutationChance);
            if (GATESTING) System.out.println("Results of mutation:\n" + java.util.Arrays.toString(nextGen));

            // Add the eliteIndividual
            if (GATESTING) System.out.println("-------------------\nAdding elite individual");
            nextGen[nextGen.length-1] = eliteIndividual;
            if (GATESTING) {
                System.out.println("Now, next gen:\n");
                for(int indivID = 0; indivID < nextGen.length; indivID++){}
                    //System.out.println(nextGen[indivID].getGraphicalRepresentation());
            }
    
            population = nextGen;

            // calculate the fitness
            if (GATESTING) System.out.println("-------------------\nCalling calcFitness");
            fitness = ga.calcFitnessStream(population, gamesPerIndividual, selfFlag, selfKnockDeciderFlag, selfDrawDeciderFlag, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
            // fitness = ga.calcFitnessLoop(population, numOfEvaluators, selfKnockDeciderFlag, opponentFlag, selfFlag, gamesPerIndividual, selfDrawDeciderFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
            
            if (GATESTING) System.out.println("Fitnesses:\n" + java.util.Arrays.toString(fitness));

            // find individual with best fitness
            if (GATESTING) System.out.println("-------------------\nFinding best individual");
            results = findFitnessSumAndBestIndex(fitness);
            sum = results[0];
            indexOfBest = (int) results[1];
            bestFitness = fitness[indexOfBest];
            averageFitnessThisGen = sum / nextGen.length;
            averageFitnessEachGen.add(averageFitnessThisGen);
            bestFitnessEachGen.add(bestFitness);
            eliteIndividual = new ParamList(nextGen[indexOfBest]);
           // if (GATESTING) System.out.println("eliteIndividual: " + eliteIndividual + ", " + eliteIndividual.getGraphicalRepresentation());

            System.out.println("-------------------\nThe best individual of generation " + generationID + " is: " + nextGen[indexOfBest]);
            System.out.println("The best individual's fitness value is: " + bestFitness);
            System.out.println("The average fitness this generation is: " + averageFitnessThisGen);
            System.out.print("Across generations, averageFitness: ");
            for(double n: averageFitnessEachGen) System.out.printf("%.3f, ", n);
            System.out.println("\nAcross generations, bestFitness: " + bestFitnessEachGen);
            //for(double n: bestFitnessEachGen) System.out.printf("%.3f, ", n);
            System.out.println();
            if(bestFitnessAcrossGeneration < bestFitness){
                bestFitnessAcrossGeneration = bestFitness;
                bestParamList = eliteIndividual;
            }
            generationID++;
        }
        return bestParamList;   
    }

    protected static double[] findFitnessSumAndBestIndex(double[] fitness) { //I just copied this
        double bestFitness = fitness[0];
        int indexOfBest = 0;
        double sum = 0;
        for(int r = 0; r<fitness.length; r++){
            sum += fitness[r];
            if(fitness[r]>bestFitness){
                bestFitness = fitness[r];
                indexOfBest = r;
            }
        }

        return new double[] {sum, indexOfBest};
    }
 
    public void setTesting(boolean testing){
        TESTING = testing;
    }
    public static void testPickNRandomAndReturnBest(){
        int popSize = 30;
        int tournamentSize = 7;
        ArrayList<Integer> populationIndex = new ArrayList<>();
        double[] fitnesses = new double[popSize];
        for(int i = 0; i < popSize; i++){
            populationIndex.add(i);
            fitnesses[i] = (int)(Math.random() * 100);
        }
        
        pickNRandomAndReturnBest(populationIndex, fitnesses, tournamentSize);
    } 
    public static void randomIndividualsTesting() {
        System.out.println("========== randomIndividualsTesting ==========\n");
        System.out.println("Making two sets of random individuals:");
        ParamList pl1 = ParamList.getRandomParamList(0);
        ParamList pl2 = ParamList.getRandomParamList(0);
        System.out.println("First individual: " + pl1);
        System.out.println("Second individual: " + pl2);
    }

    public static void calcFitnessTesting(int numOfEvaluators, int selfKnockDeciderFlag, int opponentFlag, int selfFlag, int gamesPerIndividual, int selfDrawDeciderFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        System.out.println("========== calcFitnessTesting ==========");
        GATuner tester = new GATuner();
        ParamList[] population = new ParamList[2];
        population[0] = ParamList.getRandomParamList(numOfEvaluators);//new ParamList(new double[]{0.7, 0.1, 0.19, 0, 0, 0});
        
        population[1] = ParamList.getRandomParamList(numOfEvaluators);//new ParamList(new double[]{0.15, 0.75, 0.09, 0, 0, 0});
        

       double[] fitnessValue = tester.calcFitnessLoop(population, numOfEvaluators, selfKnockDeciderFlag, opponentFlag, selfFlag, gamesPerIndividual, selfDrawDeciderFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
       System.out.println("The fitness of Player with individual 0 is: " + fitnessValue[0]);
       System.out.println("The fitness of Player with individual 1 is: " + fitnessValue[1]);

    }



    
    public static void main(String[] args){
        // testPickNRandomAndReturnBest();
        testSelect();
        // int popSize = 10;
        // int numGenerations = 2; 
        // int gamesPerIndividual = 100;
        // int numOfEvaluators = 6;
        // double mutationChance = 0.02;
        // int knockDeciderFlag = SELF_TWO_STAGE_KNOCK_DECIDER;
        // int opponentFlag = OPP_IS_SIMPLE_PLAYER;
        // int selfFlag = SELF_IS_INDEX_PLAYER;
        // int drawDeciderFlag = SELF_DEADWOOD_DRAW_DECIDER;
        // ParamList.setFixedValue(ParamList.SP_NUM_OF_ADDITIONAL_CARDS, 1);
        
       // geneticAlgorithm(popSize, numGenerations, gamesPerIndividual,
       // numOfEvaluators, mutationChance, knockDeciderFlag, opponentFlag, selfFlag, drawDeciderFlag);
        // geneticAlgorithm(popSize, numGenerations, gamesPerIndividual, numOfEvaluators, mutationChance, selfFlag, selfDrawDeciderFlag, selfKnockDeciderFlag, opponentFlag, oppDrawDeciderFlag, oppKnockDeciderFlag)
    }
}