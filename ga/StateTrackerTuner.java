package ga;
import players.ParamList;
// import players.StateTracker;
import players.TunableStateTracker;
import players.SimpleFakeGinRummyPlayer;
import players.handeval.EnsembleHandEvalPlayer;
import java.util.Random;
import java.util.LinkedList;
import java.util.ArrayList;
import games.TestingGame;
import ginrummy.Card;
import players.handeval.KnockOnGinKnockDecider;
import players.handeval.MeldOnlyDrawDecider;
import players.handeval.DeadwoodHandEvaluator;

public class StateTrackerTuner {

    private static boolean TESTING = false;
    private static boolean GATESTING = false;

    // private static final double SELECT_LOSER_CHANCE = 0.01;

    // //Opp's Players
    // //default: for any other value, opp is simple player
    // public static final int OPP_IS_SIMPLE_PLAYER = 0;
    // public static final int OPP_IS_KNOCK_ON_GIN_PLAYER = 1;
    // public static final int OPP_IS_ENSEMBLE_PLAYER = 2;
    // public static final int OPP_IS_INDEX_PLAYER = 3;

    // //Opp's DrawDeciders
    // public static final int OPP_DEADWOOD_DRAW_DECIDER = 0;
    // public static final int OPP_MELD_ONLY_DRAW_DECIDER = 1;
    // public static final int OPP_CHOOSE_10FROM11_DRAW_DECIDER = 2;

    // //Opp's KnockDeciders
    // public static final int OPP_ONE_STAGE_KNOCK_DECIDER = 0;
    // public static final int OPP_TWO_STAGE_KNOCK_DECIDER = 1;
    // public static final int OPP_SCORE_PREDICTION_KNOCK_DECIDER = 2;
    // public static final int OPP_KNOCK_ON_GIN_KNOCK_DECIDER = 3;

    public static ParamList paramListForOpp;

    public ParamList[] createRandomIndividuals(int popSize){
        Random r = new Random();
        ParamList[] population = new ParamList[popSize];
        for(int i = 0; i<popSize; i++){
           population[i] = new ParamList(new double[]{1});
           population[i].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, r.nextDouble());
           population[i].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, r.nextDouble());
           population[i].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, r.nextDouble());
           population[i].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, r.nextDouble());
           population[i].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, r.nextDouble());
           population[i].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, r.nextDouble());
           population[i].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, r.nextDouble());
           population[i].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, r.nextDouble());
           population[i].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, r.nextDouble());
           population[i].enforceRestrictions();
        }
        return population;
    }

    public double[] calcFitnessErrorLoop(ParamList[] paramLists, int gamesPerIndividual, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        double[] fitnessValue = new double[paramLists.length];
        
        for(int i = 0; i < paramLists.length; i++){
            // SimpleFakeGinRummyPlayer p0 = new SimpleFakeGinRummyPlayer(new ParamList(new double[0]));
            SimpleFakeGinRummyPlayer p1 = GATuner.setupOpp(opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
            
            ParamList params = paramLists[i]; 
            params.enforceRestrictions(); 
            TunableStateTracker st0 = new TunableStateTracker(params, p1);
            EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(params, st0, new DeadwoodHandEvaluator());
            p0.setKnockDecider(new KnockOnGinKnockDecider());
            p0.setDrawDecider(new MeldOnlyDrawDecider());

            TestingGame gameManager = new TestingGame(p0, p1);
            TestingGame.setPlayVerbose(false);
            
            if(TESTING) System.out.println("Individual " + i + " is: " + params);
            
            for(int j = 0; j < gamesPerIndividual; j++){
                gameManager.play();
            }
            fitnessValue[i] = st0.getFitnessError();
        }
        return fitnessValue;
    }

    public double[] calcFitnessCountLoop(ParamList[] paramLists, int gamesPerIndividual, int numAdditionalCards, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        double[] fitnessValue = new double[paramLists.length];
        
        
        for(int i = 0; i < paramLists.length; i++){
            // SimpleFakeGinRummyPlayer p0 = new SimpleFakeGinRummyPlayer(new ParamList(new double[0]));
            SimpleFakeGinRummyPlayer p1 = GATuner.setupOpp(opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
                        
            ParamList params = paramLists[i]; 
            params.enforceRestrictions(); 
            TunableStateTracker st0 = new TunableStateTracker(params, p1, numAdditionalCards);
            EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(params, st0, new DeadwoodHandEvaluator());
            p0.setKnockDecider(new KnockOnGinKnockDecider());
            p0.setDrawDecider(new MeldOnlyDrawDecider());

            TestingGame gameManager = new TestingGame(p0, p1);
            TestingGame.setPlayVerbose(false);
            
            if(TESTING) System.out.println("Individual " + i + " is: " + params);
            
            for(int j = 0; j < gamesPerIndividual; j++){
                gameManager.play();
            }
            fitnessValue[i] = st0.getFitnessCount();
        }
        return fitnessValue;
    }

    private static int genCount = 0;
    private static java.util.concurrent.atomic.AtomicInteger indivCount = new java.util.concurrent.atomic.AtomicInteger(0);

    public double[] calcFitnessCountStream(ParamList[] paramLists, int gamesPerIndividual, int numAdditionalCards, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        ArrayList<ParamList> allParamList = new ArrayList<>();
        for(int i = 0; i < paramLists.length; i++){
            allParamList.add(paramLists[i]);
        }
        double start, elapsed;
        double[] fitnessValue = new double[paramLists.length];
        start = System.currentTimeMillis();                 
        fitnessValue = allParamList.stream().parallel().map((x) -> {return playManyGamesCount(x, gamesPerIndividual, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);}).mapToDouble(x -> {return x;}).toArray();
        elapsed = System.currentTimeMillis() - start;
        // System.out.println("Elapsed time in parallel: " + elapsed);
        indivCount.set(0); // reset the individual count now that calc fitness is done
        genCount++; // Increase for the next generation
        return fitnessValue;
    }

    public static double playManyGamesCount(ParamList params, int gamesPerIndividual, int numAdditionalCards, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
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
        
        double fitnessValue = myTracker.getFitnessCount();
        return fitnessValue;
    }

    public double[] calcFitnessErrorStream(ParamList[] paramLists, int gamesPerIndividual, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        ArrayList<ParamList> allParamList = new ArrayList<>();
        for(int i = 0; i < paramLists.length; i++){
            allParamList.add(paramLists[i]);
        }
        double start, elapsed;
        double[] fitnessValue = new double[paramLists.length];
        start = System.currentTimeMillis();                 
        fitnessValue = allParamList.stream().parallel().map((x) -> {return playManyGamesError(x, gamesPerIndividual, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);}).mapToDouble(x -> {return x;}).toArray();
        elapsed = System.currentTimeMillis() - start;
        // System.out.println("Elapsed time in parallel: " + elapsed);
        indivCount.set(0); // reset the individual count now that calc fitness is done
        genCount++; // Increase for the next generation
        return fitnessValue;
    }

    public static double playManyGamesError(ParamList params, int gamesPerIndividual, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
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
        
        double fitnessValue = myTracker.getFitnessError();
        return fitnessValue;
    }

    public ParamList[] select(ParamList[] population, double[] fitnesses, int tournamentSize){
        // Random r = new Random();
        // ParamList[] SelectedIndividuals = new ParamList[tournamentSize]; 
        ArrayList<Integer> populationIndex = new ArrayList<>();
        for(int i = 0; i < population.length; i++){
            populationIndex.add(i);
        }
        int index1 = GATuner.pickNRandomAndReturnBest(populationIndex, fitnesses, tournamentSize);
        populationIndex.remove(index1);
        // System.out.println("Remaining indices: " + populationIndex);
        int index2 = GATuner.pickNRandomAndReturnBest(populationIndex, fitnesses, tournamentSize);

        if(TESTING){
            System.out.println("The chosen individual: " + index1 + " and " + index2);
            System.out.println("Fitness of index1 : " + fitnesses[index1]);
            System.out.println("Fitness of index2 : " + fitnesses[index2]);
        }

        ParamList parent1 = population[index1];
        ParamList parent2 = population[index2];


        return new ParamList[]{parent1, parent2};
    }

    public ParamList[] onePointCrossover(ParamList parent1, ParamList parent2){
        Random r = new Random();
        ParamList child1 = new ParamList(parent1);
        ParamList child2 = new ParamList(parent2);
        int num = ParamList.ST_END - ParamList.ST_START + 1;
        int index = ParamList.ST_START + r.nextInt(num);
        if(TESTING) System.out.println("Crossover at " + index + "(" + (index - ParamList.ST_START) + ")");
        double temp = child1.get(index);
        child1.set(index, child2.get(index));
        child2.set(index, temp);
        child1.enforceRestrictions();
        child2.enforceRestrictions();
        return new ParamList[]{child1, child2};
    }

    public ParamList[] twoPointsCrossover(ParamList parent1, ParamList parent2){
        Random r = new Random();
        int num = ParamList.ST_END - ParamList.ST_START + 1;
        int start = r.nextInt(num) + ParamList.ST_START;
        int stop = r.nextInt(num) + ParamList.ST_START;
        if(TESTING) System.out.println("Crossover from " + start + "(" + (start - ParamList.ST_START) + ")" + " to " + stop + "(" + (stop - ParamList.ST_START) + ")");

        ParamList child1 = new ParamList(parent1);
        ParamList child2 = new ParamList(parent2);
        
        for(int i = ParamList.ST_START; i <= ParamList.ST_END; i++){
            if((start <= stop && (i >= start && i <= stop)) || (start > stop && (i <=stop || i >= start))){
                child1.set(i, parent2.get(i));
                child2.set(i, parent1.get(i));
            }
        }
        child1.enforceRestrictions();
        child2.enforceRestrictions();
        ParamList[] children = {child1, child2};
        return children;
    }

    public void mutate(ParamList[] population, double mutationChance){
        for(int individualIndex = 0; individualIndex<population.length; individualIndex++){
            Random r = new Random();
            for(int idOfGeneMutated = ParamList.ST_START; idOfGeneMutated <= ParamList.ST_END; idOfGeneMutated++){
                double randomChance = r.nextDouble();
                if(randomChance < mutationChance){
                    // int idOfGeneMutated = r.nextInt(paramAL.size()); 
                    double mutationValue = -1;
                    switch(idOfGeneMutated){
                        case ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY:
                        case ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY:
                        case ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK: 
                        case ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY: 
                        case ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY: 
                        case ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK: 
                        case ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY: 
                        case ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY: 
                        case ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK: 
                            mutationValue = r.nextDouble();
                            break;                      
                        default: // an ensemble weight
                            mutationValue = r.nextDouble();
                            break;
                    }
                    if(mutationValue != -1){
                        population[individualIndex].set(idOfGeneMutated, mutationValue);
                        population[individualIndex].enforceRestrictions();
                    }
                    else
                        System.out.println("Error: Mutation wasn't successfully done");
                }
            }
        }
    }

    public static ParamList geneticAlgorithmError(int popSize, int numGenerations, int gamesPerIndividual, int tournamentSize, double mutationChance, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        ParamList bestParamList = null;
        double bestFitnessAcrossGeneration = 0;

        if (GATESTING) System.out.println("=========================\n====GATESTING=========\n=========================\n");
        double averageFitnessThisGen;
        ArrayList<Double> averageFitnessEachGen = new ArrayList<Double>(numGenerations);
        ArrayList<Double> bestFitnessEachGen = new ArrayList<Double>(numGenerations);
        StateTrackerTuner ga = new StateTrackerTuner();
        
        if (GATESTING) System.out.println("-------------------\nCalling createRandomIndividuals");
        ParamList[] population = ga.createRandomIndividuals(popSize); // need to review for the second parameter
        
        if (GATESTING) System.out.println("-------------------\nCalling calcFitness");
        // double[] fitness = ga.calcFitnessErrorLoop(population, gamesPerIndividual, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        double[] fitness = ga.calcFitnessErrorStream(population, gamesPerIndividual, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
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
        System.out.println("-------------------\nThe best individual of generation " + generationID + " is: " 
                           + population[indexOfBest].get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY) + ", "
                           + population[indexOfBest].get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY) + ", "
                           + population[indexOfBest].get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK) + ", "
                           + population[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY) + ", "
                           + population[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY) + ", "
                           + population[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK) + ", "
                           + population[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY) + ", "
                           + population[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY) + ", "
                           + population[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK));
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

                ParamList[] newIndividuals = ga.twoPointsCrossover(selectedIndividuals[0], selectedIndividuals[1]);
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
            double [] dummyEnsembleWeights = new double[0];
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
            // fitness = ga.calcFitnessErrorLoop(nextGen, gamesPerIndividual, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
            fitness = ga.calcFitnessErrorStream(nextGen, gamesPerIndividual, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
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
            if(bestFitnessAcrossGeneration < bestFitness){
                bestFitnessAcrossGeneration = bestFitness;
                bestParamList = eliteIndividual;
            }
           // if (GATESTING) System.out.println("eliteIndividual: " + eliteIndividual + ", " + eliteIndividual.getGraphicalRepresentation());

            System.out.println("-------------------\nThe best individual of generation " + generationID + " is: " 
                                + nextGen[indexOfBest].get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK));
            System.out.println("The best individual's fitness value is: " + bestFitness);
            System.out.println("The average fitness this generation is: " + averageFitnessThisGen);
            System.out.print("Across generations, averageFitness: ");
            for(double n: averageFitnessEachGen) System.out.printf("%.3f, ", n);
            System.out.println("\nAcross generations, bestFitness: " + bestFitnessEachGen);
            //for(double n: bestFitnessEachGen) System.out.printf("%.3f, ", n);
            System.out.println();
            generationID++;
        }
        return bestParamList;
    }


    public static ParamList geneticAlgorithmCount(int popSize, int numGenerations, int gamesPerIndividual, int tournamentSize, double mutationChance, int numAdditionalCards, int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag){
        ParamList bestParamList = null;
        double bestFitnessAcrossGeneration = 0;
        
        if (GATESTING) System.out.println("=========================\n====GATESTING=========\n=========================\n");
        double averageFitnessThisGen;
        ArrayList<Double> averageFitnessEachGen = new ArrayList<Double>(numGenerations);
        ArrayList<Double> bestFitnessEachGen = new ArrayList<Double>(numGenerations);
        StateTrackerTuner ga = new StateTrackerTuner();
        
        if (GATESTING) System.out.println("-------------------\nCalling createRandomIndividuals");
        ParamList[] population = ga.createRandomIndividuals(popSize); // need to review for the second parameter
        
        if (GATESTING) System.out.println("-------------------\nCalling calcFitness");
        // double[] fitness = ga.calcFitnessCountLoop(population, gamesPerIndividual, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        double[] fitness = ga.calcFitnessCountStream(population, gamesPerIndividual, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
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
        System.out.println("-------------------\nThe best individual of generation " + generationID + " is: " 
                           + population[indexOfBest].get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY) + ", "
                           + population[indexOfBest].get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY) + ", "
                           + population[indexOfBest].get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK) + ", "
                           + population[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY) + ", "
                           + population[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY) + ", "
                           + population[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK) + ", "
                           + population[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY) + ", "
                           + population[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY) + ", "
                           + population[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK));
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

                ParamList[] newIndividuals = ga.twoPointsCrossover(selectedIndividuals[0], selectedIndividuals[1]);
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
            double [] dummyEnsembleWeights = new double[0];
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
            // fitness = ga.calcFitnessCountLoop(nextGen, gamesPerIndividual, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
            fitness = ga.calcFitnessCountStream(nextGen, gamesPerIndividual, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
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
           if(bestFitnessAcrossGeneration < bestFitness){
                bestFitnessAcrossGeneration = bestFitness;
                bestParamList = eliteIndividual;
            }
            System.out.println("-------------------\nThe best individual of generation " + generationID + " is: " 
                                + nextGen[indexOfBest].get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY) + ", "
                                + nextGen[indexOfBest].get(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK));
            System.out.println("The best individual's fitness value is: " + bestFitness);
            System.out.println("The average fitness this generation is: " + averageFitnessThisGen);
            System.out.print("Across generations, averageFitness: ");
            for(double n: averageFitnessEachGen) System.out.printf("%.3f, ", n);
            System.out.println("\nAcross generations, bestFitness: " + bestFitnessEachGen);
            //for(double n: bestFitnessEachGen) System.out.printf("%.3f, ", n);
            System.out.println();
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

    public static void setTesting(boolean testing){
        TESTING = testing;
    }

    public static void calcFitnessErrorTesting(){
        System.out.println("========== calcFitnessErrorTesting ==========");
        StateTrackerTuner tester = new StateTrackerTuner();
        Random r = new Random();     
        int gamesPerIndividual = 100;  
        ParamList[] population = new ParamList[2];
        population[0] = new ParamList(new double[]{1});;//new ParamList(new double[]{0.7, 0.1, 0.19, 0, 0, 0});
        population[0].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, r.nextDouble());
        population[0].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, r.nextDouble());
        population[0].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, r.nextDouble());
        population[0].enforceRestrictions();
        // System.out.println("Individual 0: " + population[0]);

        population[1] = new ParamList(new double[]{1});;//new ParamList(new double[]{0.15, 0.75, 0.09, 0, 0, 0});
        population[1].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, r.nextDouble());
        population[1].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, r.nextDouble());
        population[1].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, r.nextDouble());
        population[1].enforceRestrictions();
        // System.out.println("Individual 1: " + population[1]);

        int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_ERROR);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.EVERY_TURN);
    //    double[] fitnessValue = tester.calcFitnessErrorLoop(population, gamesPerIndividual, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
       double[] fitnessValue = tester.calcFitnessErrorStream(population, gamesPerIndividual, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
       System.out.println("The fitness of Player with individual 0 is: " + fitnessValue[0]);
       System.out.println("The fitness of Player with individual 1 is: " + fitnessValue[1]);
    }

    public static void calcFitnessCountTesting(){
        System.out.println("========== calcFitnessCountTesting ==========");
        StateTrackerTuner tester = new StateTrackerTuner();
        Random r = new Random();     
        int gamesPerIndividual = 100;  
        ParamList[] population = new ParamList[2];
        population[0] = new ParamList(new double[]{1});;//new ParamList(new double[]{0.7, 0.1, 0.19, 0, 0, 0});
        population[0].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, r.nextDouble());
        population[0].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, r.nextDouble());
        population[0].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, r.nextDouble());
        population[0].enforceRestrictions();
        // System.out.println("Individual 0: " + population[0]);

        population[1] = new ParamList(new double[]{1});;//new ParamList(new double[]{0.15, 0.75, 0.09, 0, 0, 0});
        population[1].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, r.nextDouble());
        population[1].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, r.nextDouble());
        population[1].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, r.nextDouble());
        population[1].enforceRestrictions();
        // System.out.println("Individual 1: " + population[1]);

       int opponentFlag = GATuner.OPP_IS_KNOCK_ON_GIN_PLAYER;
       int oppKnockDeciderFlag = -1;
       int oppDrawDeciderFlag = GATuner.OPP_MELD_ONLY_DRAW_DECIDER;
       int numAdditionalCards = 3;
       TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_COUNT);
       TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.EVERY_TURN);
    //    double[] fitnessValue = tester.calcFitnessCountLoop(population, gamesPerIndividual, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
       double[] fitnessValue = tester.calcFitnessCountStream(population, gamesPerIndividual, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
       System.out.println("The fitness of Player with individual 0 is: " + fitnessValue[0]);
       System.out.println("The fitness of Player with individual 1 is: " + fitnessValue[1]);
    }

    public static void testCrossover(){
        System.out.println("============== testCrossover ==============");
        StateTrackerTuner tester = new StateTrackerTuner();
        ParamList[] population = new ParamList[2];
        Random r = new Random();
        population[0] = new ParamList(new double[0]);;//new ParamList(new double[]{0.7, 0.1, 0.19, 0, 0, 0});
        population[0].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, r.nextDouble());
        population[0].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, r.nextDouble());
        population[0].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, r.nextDouble());
        population[0].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, r.nextDouble());
        population[0].enforceRestrictions();
        // System.out.println("Individual 0: " + population[0]);

        population[1] = new ParamList(new double[0]);;//new ParamList(new double[]{0.15, 0.75, 0.09, 0, 0, 0});
        population[1].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, r.nextDouble());
        population[1].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, r.nextDouble());
        population[1].set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, r.nextDouble());
        population[1].set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, r.nextDouble());
        population[1].enforceRestrictions();
        
        setTesting(true);

        ParamList[] newParamListsOne = tester.onePointCrossover(population[0], population[1]);
        System.out.print("Parent 1: ");
        population[0].printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        System.out.print("Parent 2: ");
        population[1].printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        System.out.print("Child 1:  ");
        newParamListsOne[0].printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        System.out.print("Child 2:  ");
        newParamListsOne[1].printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);

        ParamList[] newParamListsTwo = tester.twoPointsCrossover(population[0], population[1]);
        System.out.println("Two points crossover: ");
        System.out.print("Parent 1: ");
        population[0].printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        System.out.print("Parent 2: ");
        population[1].printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        System.out.print("Child 1:  ");
        newParamListsTwo[0].printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
        System.out.print("Child 2:  ");
        newParamListsTwo[1].printSpecifiedParams(ParamList.ST_START, ParamList.ST_END);
    }


    public static void main(String[] args){
        // calcFitnessErrorTesting();
        calcFitnessCountTesting();
        // testCrossover();

        int gamesPerIndividual = 10;
        int popSize = 10;
        int numGenerations = 5;
        int tournamentSize = 15;
        double mutationChance = 0.02;
        
        int numAdditionalCards = 3;
        int opponentFlag = GATuner.OPP_IS_INDEX_PLAYER;
        int oppKnockDeciderFlag = GATuner.OPP_KNOCK_ON_GIN_KNOCK_DECIDER;
        int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;

        // TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_ERROR);
        // TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.AT_ONE_TURN);
        // TunableStateTracker.setTurnToCheck(10);
        // geneticAlgorithmError(popSize, numGenerations, gamesPerIndividual, tournamentSize, mutationChance, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        
        // TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_COUNT);
        // TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.AT_ONE_TURN);
        // TunableStateTracker.setTurnToCheck(10);
        // geneticAlgorithmCount(popSize, numGenerations, gamesPerIndividual, tournamentSize, mutationChance, numAdditionalCards, opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
    }
}