package ga;
import java.util.ArrayList;
import ginrummy.Card;
import ginrummy.GinRummyGame;
//import games.GATestingGame;
import java.util.Random;
import java.util.LinkedList;
import players.OurSimpleGinRummyPlayer;
import players.SimpleFakeGinRummyPlayer;

import players.handeval.ConvHandEvaluator;
//import players.handeval.HOSHandEvalPlayer;

//import games.GATestingGame;

public class HandEvalGA {

    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // IMPORTANT NOTE!!!!!
    // I've commented out this entire class, pending changes using the full ParamList.
    // To start, remove all the /* and the one */ at the end of the file THAT ARE NOT INDENTED AT ALL.
    // Do not remove the /* and */ that are indented - those represent code that was previously commented out,
    // before the ParamList changes.
    // You can also search on the phrase "Remove this comment-out" to see the comment symbols that should be removed
    // when you're starting this work.

/*Remove this comment-out

    private static final boolean TESTING = false;
    private static final boolean GATESTING = false;

    private static final double MUTATION_CHANCE = 0.06;
    private static final double SELECT_LOSER_CHANCE = 0.01;

    public HandEvalGA(){}

    // VERIFIED 6/5
    public ConvHandEvaluator[] createRandomIndividuals(int n){
        Random r = new Random();
        ConvHandEvaluator[] randomIndividuals = new ConvHandEvaluator[n];  
        for(int i = 0; i<n; i++){
            ConvHandEvaluator individual = new ConvHandEvaluator();
            individual.setOneAwayRankSameSuitValue(r.nextDouble());
            individual.setSameRankValue(r.nextDouble());
            individual.setTwoAwayRankSameSuitValue(r.nextDouble());
            individual.finalizeChanges();
            randomIndividuals[i] = individual;

        }
        return randomIndividuals;
    }

    // VERIFIED 6/6
    public double[] calcFitness(ConvHandEvaluator[] pop, int gamesPerIndividual){
        // Set up fitnessValue array
        double [] fitnessValue = new double[pop.length];
        for(int s = 0; s<fitnessValue.length; s++)
            fitnessValue[s] = 0;

        HOSHandEvalPlayer player0 = new HOSHandEvalPlayer();
        SimpleFakeGinRummyPlayer player1 = new SimpleFakeGinRummyPlayer();
        int countOfPlayer0Wins;

        GATestingGame gameManager = new GATestingGame(player0, player1);
        GATestingGame.setPlayVerbose(false);

        for(int i = 0; i< pop.length; i++) { 
            countOfPlayer0Wins = 0;
            player0.setHandEvaluator(pop[i]);
            for(int k = 0; k < gamesPerIndividual; k++) {
                int winner = gameManager.play();// // for testing of fitness, you could just randomly choose a winner
                if(winner == 0)
                    countOfPlayer0Wins += 1;
            }
            fitnessValue[i] += countOfPlayer0Wins;
        }

        if(TESTING){
            for(int n = 0; n<pop.length; n++)
                System.out.println("\nThe number of wins for individual " + n + " is " + fitnessValue[n]);
        }

        for(int m = 0; m < fitnessValue.length; m++)
            fitnessValue[m] = (double) fitnessValue[m]/gamesPerIndividual;

        return fitnessValue;
    }

    /*
    //unverified
    public double[] calcFitness(ConvHandEvaluator[] pop){
        Random r = new Random();
        int numberOfGamesWithOneIndividual = 5;
        double [] fitnessValue = new double[pop.length];
        for(int s = 0; s<fitnessValue.length; s++){
            fitnessValue[s] = 0;
        }
        HandEvalPlayer player0 = new HandEvalPlayer();
        HandEvalPlayer player1 = new HandEvalPlayer();
        int countOfPlayer0Wins = 0;
        int countOfPlayer1Wins = 0;
        //int countOfWinsByLastIndividual = 0;
        GATestingGame gameManager = new GATestingGame(player0, player1);
        gameManager.setPlayVerbose(false);
        for(int i = 0; i< pop.length; i++){ 
             //length-1?
            for(int j = i+1; j<pop.length; j++){
                player0.setHandEvaluator(pop[i]);
                player1.setHandEvaluator(pop[j]);
                for(int k = 0; k<numberOfGamesWithOneIndividual; k++){
                    //int winner = r.nextInt(2);
                    System.out.println(i + " vs. " + j + ", game " + k);
                    int winner = gameManager.play();// // for testing of fitness, you could just randomly choose a winner
                    //if (TESTING) System.out.println("Winner: " + winner);
                    if(winner == 0){
                        countOfPlayer0Wins+=1;
                    }
                    else{
                        countOfPlayer1Wins+=1;
                    }
                }
                fitnessValue[j] += countOfPlayer1Wins; // player1
                countOfPlayer1Wins = 0;
            }
            fitnessValue[i] += countOfPlayer0Wins;
            //if(TESTING)  // player0
            countOfPlayer0Wins = 0;
        }
        //if(TESTING) System.out.println("The number of wins for individual " + (pop.length-1) + " is " + countOfWinsByLastIndividual);
        if(TESTING){
            for(int n = 0; n<pop.length; n++){
                System.out.println("The number of wins for individual " + n + " is " + fitnessValue[n]);
            }
        }
        for(int m = 0; m < fitnessValue.length; m++)
            fitnessValue[m] = (double) fitnessValue[m]/((pop.length-1) * numberOfGamesWithOneIndividual);

        return fitnessValue;

    }*/
/*Remove this comment-out    
    // VERIFIED 6/8
    public ConvHandEvaluator[] select(ConvHandEvaluator[] population, double[] fitnesses){
        ConvHandEvaluator[] twoSelectedIndividuals = new ConvHandEvaluator[2];

        // Calculate the sum of all the fitnesses
        double sumOfFitnessesWithLoserChance = fitnesses.length * SELECT_LOSER_CHANCE; // adds a small chance for every fitness, so even 0 fitness individuals can be selected      
        for(int i = 0; i<fitnesses.length; i++)
            sumOfFitnessesWithLoserChance+=fitnesses[i];
        
        // Calculate selection probabilities based on fitnesses
        LinkedList<Double> selectionProb = new LinkedList<Double>();
        for(int i = 0; i<fitnesses.length; i++)
            selectionProb.add((fitnesses[i] + SELECT_LOSER_CHANCE)/sumOfFitnessesWithLoserChance);

        if(TESTING) { // print selection probabilities
            for(int i = 0; i<selectionProb.size(); i++)
                System.out.println("Selection probability of individual " + i  + " with fitness value: " + fitnesses[i]  + " is " + selectionProb.get(i));
        }

        // Make ArrayList of individuals, to select from
        ArrayList<ConvHandEvaluator> eligibleIndividuals = new ArrayList<ConvHandEvaluator>(population.length);
        for(int i = 0; i<population.length; i++)
            eligibleIndividuals.add(population[i]);
            
        Random r = new Random();
        // Choose 2 individuals
        for(int choiceID = 0; choiceID<2; choiceID++) { 
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
        
        return twoSelectedIndividuals;
    }

    // VERIFIED 6/5
    public ConvHandEvaluator[] crossover(ConvHandEvaluator x, ConvHandEvaluator y){
       ConvHandEvaluator[] newIndividuals = new ConvHandEvaluator[2];
        Random r = new Random();
        int random = r.nextInt(2);
        //int random = 0;
        ConvHandEvaluator child1 = new ConvHandEvaluator();
        ConvHandEvaluator child2 = new ConvHandEvaluator();
        if(random == 0){
            child1.setSameRankValue(x.getSameRankValue());  //between a-b
            child1.setOneAwayRankSameSuitValue(y.getOneAwayRankSameSuitValue());
            child1.setTwoAwayRankSameSuitValue(y.getTwoAwayRankSameSuitValue());
            child2.setSameRankValue(y.getSameRankValue()) ; 
            child2.setOneAwayRankSameSuitValue(x.getOneAwayRankSameSuitValue());
            child2.setTwoAwayRankSameSuitValue(x.getTwoAwayRankSameSuitValue());   
        }
        else{
            child1.setSameRankValue(x.getSameRankValue()) ;//between b-c
            child1.setOneAwayRankSameSuitValue(x.getOneAwayRankSameSuitValue());
            child1.setTwoAwayRankSameSuitValue(y.getTwoAwayRankSameSuitValue());
            child2.setSameRankValue(y.getSameRankValue()) ; 
            child2.setOneAwayRankSameSuitValue(y.getOneAwayRankSameSuitValue());
            child2.setTwoAwayRankSameSuitValue(x.getTwoAwayRankSameSuitValue());  
        }
        newIndividuals[0] = child1;
        newIndividuals[1] = child2;            

        if (TESTING) {
            System.out.println("New individuals before finalizeChanges:");
            System.out.println(java.util.Arrays.toString(newIndividuals));
        }
    
        newIndividuals[0].finalizeChanges();
        newIndividuals[1].finalizeChanges(); 
                    
        return newIndividuals;
    }

    // VERIFIED 6/6
    public void mutation(ConvHandEvaluator[] population, double mutationChance){
        Random r = new Random();
        double randomChance;
        for(int i = 0; i<population.length; i++){
            randomChance = r.nextDouble();
            if(randomChance < mutationChance){
                int randomMutator = r.nextInt(3);
                switch(randomMutator){
                    case 0: 
                        population[i].setSameRankValue(r.nextDouble());
                        break;
                    case 1:
                        population[i].setOneAwayRankSameSuitValue(r.nextDouble());
                        break;
                    case 2:
                        population[i].setTwoAwayRankSameSuitValue(r.nextDouble());
                        break;
                }
                population[i].finalizeChanges();
            }
        }
    }

    // VERIFIED 6/8
    public static void geneticAlgorithm(int popSize, int numGenerations, int gamesPerIndividual){
        if (GATESTING) System.out.println("=========================\n====GATESTING=========\n=========================\n");
        double averageFitnessThisGen;
        ArrayList<Double> averageFitnessEachGen = new ArrayList<Double>(numGenerations);
        ArrayList<Double> bestFitnessEachGen = new ArrayList<Double>(numGenerations);

        HandEvalGA ga = new HandEvalGA();

        if (GATESTING) System.out.println("-------------------\nCalling createRandomIndividuals");
        ConvHandEvaluator[] population = ga.createRandomIndividuals(popSize);
        //if (GATESTING) System.out.println("Individuals created:\n" + java.util.Arrays.toString(population));
        if (GATESTING) {
            for(int indivID = 0; indivID < population.length; indivID++)
                System.out.println(population[indivID].getGraphicalRepresentation());
        }

        if (GATESTING) System.out.println("-------------------\nCalling calcFitness");
        double[] fitness = ga.calcFitness(population, gamesPerIndividual);
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
        ConvHandEvaluator eliteIndividual = new ConvHandEvaluator(population[indexOfBest]);
        if (GATESTING) System.out.println("eliteIndividual: " + eliteIndividual + ", " + eliteIndividual.getGraphicalRepresentation());

        while(generationID < numGenerations){
            if (GATESTING) {
                System.out.println("===========================================");
                System.out.println("Generation: " + generationID);
                System.out.println("Population: ");
                //System.out.println(java.util.Arrays.toString(population));                
                for(int indivID = 0; indivID < population.length; indivID++)
                    System.out.println(population[indivID].getGraphicalRepresentation());
            }

            // Make the next generation
            if (GATESTING) System.out.println("-------------------\nMaking next generation");
            nextGenTracker = 0;
            ConvHandEvaluator[] nextGen = new ConvHandEvaluator[population.length];
            while(nextGenTracker < population.length-1){ // -1 so that we save space for the eliteIndividual
                if (GATESTING) System.out.println("-----\nnextGenTracker: " + nextGenTracker);
                ConvHandEvaluator[] selectedIndividuals = ga.select(population, fitness);

                if (GATESTING) {
                    System.out.println("--\nselectedIndividuals:");
                    for(int indivID = 0; indivID < selectedIndividuals.length; indivID++)
                        System.out.println(selectedIndividuals[indivID].getGraphicalRepresentation());
                }

                // ConvHandEvaluator[] newIndividuals = ga.crossover(selectedIndividuals[0], selectedIndividuals[1]);
                ConvHandEvaluator[] newIndividuals = selectedIndividuals;
                
                if (GATESTING) {
                    System.out.println("--\nnewIndividuals:");
                    for(int indivID = 0; indivID < newIndividuals.length; indivID++)
                        System.out.println(newIndividuals[indivID].getGraphicalRepresentation());
                }

                nextGen[nextGenTracker] = newIndividuals[0];           
                if (GATESTING) System.out.println("Next gen now contains child 1: " + nextGen[nextGenTracker].getGraphicalRepresentation());
                nextGenTracker++;
                if(nextGenTracker < population.length-1) {// -1 so that we save space for the eliteIndividual
                    nextGen[nextGenTracker] = newIndividuals[1];
                    if (GATESTING) System.out.println("Next gen now contains child 2: " + nextGen[nextGenTracker].getGraphicalRepresentation());
                    nextGenTracker++;
                }
            }

            nextGen[nextGen.length-1] = new ConvHandEvaluator(-1, -1, -1); // dummy placeholder for mutation. Elite individual will go here

            // apply mutation
            if (GATESTING) System.out.println("-------------------\nMutating");
            ga.mutation(nextGen, MUTATION_CHANCE);
            if (GATESTING) System.out.println("Results of mutation:\n" + java.util.Arrays.toString(nextGen));

            // Add the eliteIndividual
            if (GATESTING) System.out.println("-------------------\nAdding elite individual");
            nextGen[nextGen.length-1] = eliteIndividual;
            if (GATESTING) {
                System.out.println("Now, next gen:\n");
                for(int indivID = 0; indivID < nextGen.length; indivID++)
                    System.out.println(nextGen[indivID].getGraphicalRepresentation());
            }
    
            // calculate the fitness
            if (GATESTING) System.out.println("-------------------\nCalling calcFitness");
            fitness = ga.calcFitness(nextGen, gamesPerIndividual);
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
            eliteIndividual = new ConvHandEvaluator(nextGen[indexOfBest]);
            if (GATESTING) System.out.println("eliteIndividual: " + eliteIndividual + ", " + eliteIndividual.getGraphicalRepresentation());

            System.out.println("-------------------\nThe best individual of generation " + generationID + " is: " + nextGen[indexOfBest]);
            System.out.println("The best individual's fitness value is: " + bestFitness);
            System.out.println("The average fitness this generation is: " + averageFitnessThisGen);
            System.out.println("Across generations, averageFitness: " + averageFitnessEachGen);
            System.out.println("Across generations, bestFitness: " + bestFitnessEachGen);
            population = nextGen;
            generationID++;
        }
    }

    /* // This version uses crossover
    // VERIFIED 6/8
    public static void geneticAlgorithm(int popSize, int numGenerations, int gamesPerIndividual){
        if (GATESTING) System.out.println("=========================\n====GATESTING=========\n=========================\n");
        double averageFitnessThisGen;
        ArrayList<Double> averageFitnessEachGen = new ArrayList<Double>(numGenerations);
        ArrayList<Double> bestFitnessEachGen = new ArrayList<Double>(numGenerations);

        HandEvalGA ga = new HandEvalGA();

        if (GATESTING) System.out.println("-------------------\nCalling createRandomIndividuals");
        ConvHandEvaluator[] population = ga.createRandomIndividuals(popSize);
        //if (GATESTING) System.out.println("Individuals created:\n" + java.util.Arrays.toString(population));
        if (GATESTING) {
            for(int indivID = 0; indivID < population.length; indivID++)
                System.out.println(population[indivID].getGraphicalRepresentation());
        }

        if (GATESTING) System.out.println("-------------------\nCalling calcFitness");
        double[] fitness = ga.calcFitness(population, gamesPerIndividual);
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
        ConvHandEvaluator eliteIndividual = new ConvHandEvaluator(population[indexOfBest]);
        if (GATESTING) System.out.println("eliteIndividual: " + eliteIndividual + ", " + eliteIndividual.getGraphicalRepresentation());

        while(generationID < numGenerations){
            if (GATESTING) {
                System.out.println("===========================================");
                System.out.println("Generation: " + generationID);
                System.out.println("Population: ");
                //System.out.println(java.util.Arrays.toString(population));                
                for(int indivID = 0; indivID < population.length; indivID++)
                    System.out.println(population[indivID].getGraphicalRepresentation());
            }

            // Make the next generation
            if (GATESTING) System.out.println("-------------------\nMaking next generation");
            nextGenTracker = 0;
            ConvHandEvaluator[] nextGen = new ConvHandEvaluator[population.length];
            while(nextGenTracker < population.length-1){ // -1 so that we save space for the eliteIndividual
                if (GATESTING) System.out.println("-----\nnextGenTracker: " + nextGenTracker);
                ConvHandEvaluator[] selectedIndividuals = ga.select(population, fitness);

                if (GATESTING) {
                    System.out.println("--\nselectedIndividuals:");
                    for(int indivID = 0; indivID < selectedIndividuals.length; indivID++)
                        System.out.println(selectedIndividuals[indivID].getGraphicalRepresentation());
                }

                ConvHandEvaluator[] newIndividuals = ga.crossover(selectedIndividuals[0], selectedIndividuals[1]);
                if (GATESTING) {
                    System.out.println("--\nnewIndividuals:");
                    for(int indivID = 0; indivID < newIndividuals.length; indivID++)
                        System.out.println(newIndividuals[indivID].getGraphicalRepresentation());
                }

                nextGen[nextGenTracker] = newIndividuals[0];           
                if (GATESTING) System.out.println("Next gen now contains child 1: " + nextGen[nextGenTracker].getGraphicalRepresentation());
                nextGenTracker++;
                if(nextGenTracker < population.length-1) {// -1 so that we save space for the eliteIndividual
                    nextGen[nextGenTracker] = newIndividuals[1];
                    if (GATESTING) System.out.println("Next gen now contains child 2: " + nextGen[nextGenTracker].getGraphicalRepresentation());
                    nextGenTracker++;
                }
            }

            nextGen[nextGen.length-1] = new ConvHandEvaluator(-1, -1, -1); // dummy placeholder for mutation. Elite individual will go here

            // apply mutation
            if (GATESTING) System.out.println("-------------------\nMutating");
            ga.mutation(nextGen, MUTATION_CHANCE);
            if (GATESTING) System.out.println("Results of mutation:\n" + java.util.Arrays.toString(nextGen));

            // Add the eliteIndividual
            if (GATESTING) System.out.println("-------------------\nAdding elite individual");
            nextGen[nextGen.length-1] = eliteIndividual;
            if (GATESTING) {
                System.out.println("Now, next gen:\n");
                for(int indivID = 0; indivID < nextGen.length; indivID++)
                    System.out.println(nextGen[indivID].getGraphicalRepresentation());
            }
    
            // calculate the fitness
            if (GATESTING) System.out.println("-------------------\nCalling calcFitness");
            fitness = ga.calcFitness(nextGen, gamesPerIndividual);
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
            eliteIndividual = new ConvHandEvaluator(nextGen[indexOfBest]);
            if (GATESTING) System.out.println("eliteIndividual: " + eliteIndividual + ", " + eliteIndividual.getGraphicalRepresentation());

            System.out.println("-------------------\nThe best individual of generation " + generationID + " is: " + nextGen[indexOfBest]);
            System.out.println("The best individual's fitness value is: " + bestFitness);
            System.out.println("The average fitness this generation is: " + averageFitnessThisGen);
            System.out.println("Across generations, averageFitness: " + averageFitnessEachGen);
            System.out.println("Across generations, bestFitness: " + bestFitnessEachGen);
            population = nextGen;
            generationID++;
        }
    }*/


/*Remove this comment-out


    private static double[] findFitnessSumAndBestIndex(double[] fitness) {
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

    public static void geneticAlgorithmTesting1(int popSize, int numGenerations, int gamesPerIndividual){
        double averageFitnessThisGen;
        ArrayList<Double> averageFitnessEachGen = new ArrayList<Double>(numGenerations);
        ArrayList<Double> bestFitnessEachGen = new ArrayList<Double>(numGenerations);
        HandEvalGA tester = new HandEvalGA();
        ConvHandEvaluator[] population = new ConvHandEvaluator[popSize];
        for(int individualNum = 0; individualNum<population.length; individualNum++){
            ConvHandEvaluator individual = new ConvHandEvaluator();
            population[individualNum] = individual;
            population[individualNum].setSameRankValue(1.5);
            population[individualNum].setOneAwayRankSameSuitValue(2.0);
            population[individualNum].setTwoAwayRankSameSuitValue(1.0);
        }
        /*ConvHandEvaluator he1 = new ConvHandEvaluator();
        he1.setSameRankValue(1.5);
        he1.setOneAwayRankSameSuitValue(0.8);
        he1.setTwoAwayRankSameSuitValue(1.6);
        he1.finalizeChanges();
        
        ConvHandEvaluator he2 = new ConvHandEvaluator();
        he2.setSameRankValue(1.9);
        he2.setOneAwayRankSameSuitValue(0.8);
        he2.setTwoAwayRankSameSuitValue(1.6);
        he2.finalizeChanges();
       
        ConvHandEvaluator he3 = new ConvHandEvaluator();
        he3.setSameRankValue(2.0);
        he3.setOneAwayRankSameSuitValue(3.0);
        he3.setTwoAwayRankSameSuitValue(2.0);
        he3.finalizeChanges();

        ConvHandEvaluator he4 = new ConvHandEvaluator();
        he4.setSameRankValue(2.1);
        he4.setOneAwayRankSameSuitValue(3);
        he4.setTwoAwayRankSameSuitValue(1.5);
        he4.finalizeChanges();
        
        population[0] = he1;
        population[1] = he2;
        population[2] = he3;
        population[3] = he4;*/

/*Remove this comment-out
        System.out.println("Calling fitness ");
        double fitness[] = new double[population.length];
        for(int fitnessNum = 0; fitnessNum<fitness.length; fitnessNum++){
            fitness[fitnessNum] = 0.5;
        }
        for(int i = 0; i<fitness.length; i++){
            System.out.println("Fitness of individual " + i + " is " + fitness[i]);
        }

        int generationID = 0;
        int nextGenTracker; 
        while(generationID<numGenerations){
            System.out.println("----------------------------------");
            System.out.println("Generation: " + generationID);
            System.out.println("Population: ");
            for(int indivID = 0; indivID < population.length; indivID++)
                System.out.println(population[indivID].getGraphicalRepresentation());

                if (GATESTING) System.out.println("Making next generation");
                nextGenTracker = 0;
                ConvHandEvaluator[] nextGen = new ConvHandEvaluator[population.length];
                while(nextGenTracker<population.length){
                    ConvHandEvaluator[] selectedIndividuals = tester.select(population, fitness);
                    ConvHandEvaluator[] newIndividuals = tester.crossover(selectedIndividuals[0], selectedIndividuals[1]);
    
                    nextGen[nextGenTracker] = newIndividuals[0];           
                    nextGenTracker++;
                    if(nextGenTracker < population.length) {
                        nextGen[nextGenTracker] = newIndividuals[1];
                        nextGenTracker++;
                    }
                }
                
                // apply mutation
                System.out.println("Mutating");
                tester.mutation(nextGen, 0.0);

                

                 //calculate the fitness
                System.out.println("Calling calcFitness");
                fitness = tester.calcFitness(nextGen, gamesPerIndividual);
                for(int i = 0; i<fitness.length; i++){
                    System.out.println("Fitness of individual " + i + " is " + fitness[i]);
                }
        
                // find individual with best fitness
                /*System.out.println("Finding best individual");
                double bestFitness = fitness[0];
                int individualNum = 0;
                averageFitnessThisGen = 0;
                for(int r = 0; r<nextGen.length; r++){
                    averageFitnessThisGen += fitness[r];
                    if(fitness[r]>bestFitness){
                        bestFitness = fitness[r];
                        individualNum = r;
                    }
                }
                averageFitnessThisGen = averageFitnessThisGen / nextGen.length;
                averageFitnessEachGen.add(averageFitnessThisGen);
                bestFitnessEachGen.add(bestFitness);
                System.out.println("\nThe best individual of generation " + generationID + " is: " + nextGen[individualNum]);
                System.out.println("The best individual's fitness value is: " + bestFitness);
                System.out.println("The average fitness this generation is: " + averageFitnessThisGen);
                System.out.println("Across generations, averageFitness: " + averageFitnessEachGen);
                System.out.println("Across generations, bestFitness: " + bestFitnessEachGen);
                System.out.println("--------------------------------------------------");*/
/*Remove this comment-out                population = nextGen;
                for(int indivID = 0; indivID < population.length; indivID++)
                System.out.println(population[indivID].getGraphicalRepresentation());
                generationID++;
            }
    
        }

        
       /* System.out.println("Making next generation");
            nextGenTracker = 0;
            ConvHandEvaluator[] nextGen = new ConvHandEvaluator[population.length];
            while(nextGenTracker<population.length){
                ConvHandEvaluator[] selectedIndividuals = tester.select(population, fitness);
                for(int selectIndex = 0; selectIndex<selectedIndividuals.length; selectIndex++){
                    System.out.println("The selected indiividual's first gene is: " + selectedIndividuals[selectIndex].getSameRankValue());
                }
                System.out.println();
                ConvHandEvaluator[] newIndividuals = tester.crossover(selectedIndividuals[0], selectedIndividuals[1]);
                for(int crossoverIndex = 0; crossoverIndex<newIndividuals.length; crossoverIndex++){
                    System.out.println("The new indiividual is: " );
                    System.out.println("The individual's gene for same rank is: " + newIndividuals[crossoverIndex].getSameRankValue());
                    System.out.println("The individual's gene for same suit, one rank away value is: " + newIndividuals[crossoverIndex].getOneAwayRankSameSuitValue());
                    System.out.println("The individual's gene for same suit, two rank away value is: " + newIndividuals[crossoverIndex].getTwoAwayRankSameSuitValue());
                    System.out.println();
                }
                
                nextGen[nextGenTracker] = newIndividuals[0];           
                nextGenTracker++;
                if(nextGenTracker < population.length) {
                    nextGen[nextGenTracker] = newIndividuals[1];
                    nextGenTracker++;
                }
                System.out.println("Mutating");
                tester.mutation(nextGen, 0.0);
                System.out.println("Calling calcFitness");
                fitness = tester.calcFitness(nextGen, gamesPerIndividual);
                for(int i = 0; i<fitness.length; i++){
                    System.out.println("Fitness of individual " + i + " is " + fitness[i]);
                }
            // find individual with best fitness
                System.out.println("Finding best individual");
                double bestFitness = fitness[0];
                int individualNum = 0;
                averageFitnessThisGen = 0;
                for(int r = 0; r<nextGen.length; r++){
                    averageFitnessThisGen += fitness[r];
                    if(fitness[r]>bestFitness){
                        bestFitness = fitness[r];
                        individualNum = r;
                    }
                }
                averageFitnessThisGen = averageFitnessThisGen / nextGen.length;
                averageFitnessEachGen.add(averageFitnessThisGen);
                bestFitnessEachGen.add(bestFitness);
                System.out.println("\nThe best individual of generation " + generationID + " is: " + nextGen[individualNum]);
                System.out.println("The best individual's fitness value is: " + bestFitness);
                System.out.println("The average fitness this generation is: " + averageFitnessThisGen);
                System.out.println("Across generations, averageFitness: " + averageFitnessEachGen);
                System.out.println("Across generations, bestFitness: " + bestFitnessEachGen);
                System.out.println("--------------------------------------------------");
                population = nextGen;
                generationID++;
            }*/
        
/*Remove this comment-out
    public static void randomIndividualsTesting() {
        System.out.println("========== randomIndividualsTesting ==========\n");
        HandEvalGA tester = new HandEvalGA();

        System.out.println("Making two sets of random individuals:");

        ConvHandEvaluator[] set1 = tester.createRandomIndividuals(4);
        System.out.println(java.util.Arrays.toString(set1));

        ConvHandEvaluator[] set2 = tester.createRandomIndividuals(4);
        System.out.println(java.util.Arrays.toString(set2));
    }

    public static void calcFitnessTesting(){
        System.out.println("========== calcFitnessTesting ==========");
        HandEvalGA tester = new HandEvalGA();
        /*ConvHandEvaluator[] population = new ConvHandEvaluator[4];
            population = tester.createRandomIndividuals(population.length);
            for(int i = 0; i<population.length; i++){
                System.out.println("The individual number: " + i);
                System.out.println("The individual's gene for same rank is: " + population[i].getSameRankValue());
                System.out.println("The individual's gene for same suit, one rank away value is: " + population[i].getOneAwayRankSameSuitValue());
                System.out.println("The individual's gene for same suit, two rank away value is: " + population[i].getTwoAwayRankSameSuitValue());
                System.out.println("The sum of the individuals' genes is:" +  "" + (population[i].getSameRankValue() + population[i].getOneAwayRankSameSuitValue() + population[i].getTwoAwayRankSameSuitValue()) );
            }*/
/*Remove this comment-out        ConvHandEvaluator[] population = new ConvHandEvaluator[4];
        ConvHandEvaluator he1 = new ConvHandEvaluator();
        he1.setSameRankValue(1.5);
        he1.setOneAwayRankSameSuitValue(2.0);
        he1.setTwoAwayRankSameSuitValue(1.0);
        he1.finalizeChanges();
        
        ConvHandEvaluator he2 = new ConvHandEvaluator();
        he2.setSameRankValue(2.0);
        he2.setOneAwayRankSameSuitValue(2.7);
        he2.setTwoAwayRankSameSuitValue(1.4);
        he2.finalizeChanges();
       
        ConvHandEvaluator he3 = new ConvHandEvaluator();
        he3.setSameRankValue(1.4);
        he3.setOneAwayRankSameSuitValue(1.9);
        he3.setTwoAwayRankSameSuitValue(1.3);
        he3.finalizeChanges();

        ConvHandEvaluator he4 = new ConvHandEvaluator();
        he4.setSameRankValue(.5);
        he4.setOneAwayRankSameSuitValue(.489);
        he4.setTwoAwayRankSameSuitValue(1);
        he4.finalizeChanges();
        
        population[0] = he1;
        population[1] = he2;
        population[2] = he3;
        population[3] = he4;
        double [] fitness = tester.calcFitness(population, 10);

        if(TESTING){
            for(int i = 0; i<fitness.length; i++)
                System.out.println("The fitness value for individual " + i + " is " + fitness[i]);
        }
    }

    public static void selectTesting(){
        System.out.println("========== selectTesting ==========\n");

        HandEvalGA tester = new HandEvalGA();
        ConvHandEvaluator[] population = new ConvHandEvaluator[4];
        ConvHandEvaluator he1 = new ConvHandEvaluator();
        he1.setSameRankValue(1.5);
        he1.setOneAwayRankSameSuitValue(2.0);
        he1.setTwoAwayRankSameSuitValue(1.0);
        //he1.finalizeChanges()

        ConvHandEvaluator he2 = new ConvHandEvaluator();
        he2.setSameRankValue(1.6);
        he2.setOneAwayRankSameSuitValue(1.8);
        he2.setTwoAwayRankSameSuitValue(0.9);
        //he2.finalizeChanges()

        ConvHandEvaluator he3 = new ConvHandEvaluator();
        he3.setSameRankValue(1.7);
        he3.setOneAwayRankSameSuitValue(2.2);
        he3.setTwoAwayRankSameSuitValue(0.675);
        //he3.finalizeChanges()

        ConvHandEvaluator he4 = new ConvHandEvaluator();
        he4.setSameRankValue(1.34);
        he4.setOneAwayRankSameSuitValue(1.97);
        he4.setTwoAwayRankSameSuitValue(1.13);
        //he4.finalizeChanges()

        population[0] = he1;
        population[1] = he2;
        population[2] = he3;
        population[3] = he4;

        //double [] fitness =  tester.calcFitness(population);
        double[] fitness = new double[] {0.4, 0.9, 0.8, 0.1};

        for(int i = 0; i < population.length; i++)
            System.out.println("Individual " + i + ": " + population[i]);

        for(int i = 0; i<population.length; i++)
            System.out.println("The fitness value of individual: " + i + " is " + fitness[i]);

        ConvHandEvaluator[] selectedIndividuals = tester.select(population, fitness);
        for(int i = 0; i<selectedIndividuals.length; i++)
            System.out.println("Selected individual: " + selectedIndividuals[i]);
    }

    public static void selectTestingWithLosers(){
        System.out.println("========== selectTestingWithLosers ==========\n");

        HandEvalGA tester = new HandEvalGA();
        ConvHandEvaluator[] population = new ConvHandEvaluator[4];
        ConvHandEvaluator he1 = new ConvHandEvaluator();
        he1.setSameRankValue(1.5);
        he1.setOneAwayRankSameSuitValue(2.0);
        he1.setTwoAwayRankSameSuitValue(1.0);
        //he1.finalizeChanges()

        ConvHandEvaluator he2 = new ConvHandEvaluator();
        he2.setSameRankValue(1.6);
        he2.setOneAwayRankSameSuitValue(1.8);
        he2.setTwoAwayRankSameSuitValue(0.9);
        //he2.finalizeChanges()

        ConvHandEvaluator he3 = new ConvHandEvaluator();
        he3.setSameRankValue(1.7);
        he3.setOneAwayRankSameSuitValue(2.2);
        he3.setTwoAwayRankSameSuitValue(0.675);
        //he3.finalizeChanges()

        ConvHandEvaluator he4 = new ConvHandEvaluator();
        he4.setSameRankValue(1.34);
        he4.setOneAwayRankSameSuitValue(1.97);
        he4.setTwoAwayRankSameSuitValue(1.13);
        //he4.finalizeChanges()

        population[0] = he1;
        population[1] = he2;
        population[2] = he3;
        population[3] = he4;

        //double [] fitness =  tester.calcFitness(population);
        double[] fitness = new double[] {0.1, 0.0, 0.0, 0.0};

        for(int i = 0; i < population.length; i++)
            System.out.println("Individual " + i + ": " + population[i]);

        for(int i = 0; i<population.length; i++)
            System.out.println("The fitness value of individual: " + i + " is " + fitness[i]);

        ConvHandEvaluator[] selectedIndividuals = tester.select(population, fitness);
        for(int i = 0; i<selectedIndividuals.length; i++)
            System.out.println("Selected individual: " + selectedIndividuals[i]);
    }

    public static void crossoverTesting(){
        System.out.println("========== crossoverTesting ==========\n");

        HandEvalGA tester = new HandEvalGA();
        //ConvHandEvaluator[] population = new ConvHandEvaluator[2];
        ConvHandEvaluator he1 = new ConvHandEvaluator();
        he1.setSameRankValue(1);
        he1.setOneAwayRankSameSuitValue(2);
        he1.setTwoAwayRankSameSuitValue(3);
        he1.finalizeChanges();

        ConvHandEvaluator he2 = new ConvHandEvaluator();
        he2.setSameRankValue(4);
        he2.setOneAwayRankSameSuitValue(5);
        he2.setTwoAwayRankSameSuitValue(6);
        he2.finalizeChanges();

        System.out.println("The parent Individual he1 is: ");
        System.out.println("The individual's gene for same rank is: " + he1.getSameRankValue());
        System.out.println("The individual's gene for same suit, one rank away value is: " + he1.getOneAwayRankSameSuitValue());
        System.out.println("The individual's gene for same suit, two rank away value is: " + he1.getTwoAwayRankSameSuitValue());
        System.out.println();
        System.out.println("The parent Individual he2 is: ");
        System.out.println("The individual's gene for same rank is: " + he2.getSameRankValue());
        System.out.println("The individual's gene for same suit, one rank away value is: " + he2.getOneAwayRankSameSuitValue());
        System.out.println("The individual's gene for same suit, two rank away value is: " + he2.getTwoAwayRankSameSuitValue());
        System.out.println();

        ConvHandEvaluator[] children =  tester.crossover(he1, he2);

        for(int i = 0; i<children.length; i++){
            System.out.println("The new Individual is: ");
            System.out.println("The individual's gene for same rank is: " + children[i].getSameRankValue());
            System.out.println("The individual's gene for same suit, one rank away value is: " + children[i].getOneAwayRankSameSuitValue());
            System.out.println("The individual's gene for same suit, two rank away value is: " + children[i].getTwoAwayRankSameSuitValue());
            System.out.println();
        }
        System.out.println();
    }

    public static void mutationTesting(){
        System.out.println("========== mutationTesting ==========\n");
        HandEvalGA tester = new HandEvalGA();
        ConvHandEvaluator[] population = new ConvHandEvaluator[4];
        ConvHandEvaluator he1 = new ConvHandEvaluator();
        he1.setSameRankValue(1);
        he1.setOneAwayRankSameSuitValue(1);
        he1.setTwoAwayRankSameSuitValue(1);
        he1.finalizeChanges();

        ConvHandEvaluator he2 = new ConvHandEvaluator();
        he2.setSameRankValue(2);
        he2.setOneAwayRankSameSuitValue(2);
        he2.setTwoAwayRankSameSuitValue(2);
        he2.finalizeChanges();

        ConvHandEvaluator he3 = new ConvHandEvaluator();
        he3.setSameRankValue(3);
        he3.setOneAwayRankSameSuitValue(3);
        he3.setTwoAwayRankSameSuitValue(3);
        he3.finalizeChanges();

        ConvHandEvaluator he4 = new ConvHandEvaluator();
        he4.setSameRankValue(4);
        he4.setOneAwayRankSameSuitValue(4);
        he4.setTwoAwayRankSameSuitValue(4);
        he4.finalizeChanges();

        population[0] = he1;
        population[1] = he2;
        population[2] = he3;
        population[3] = he4;
              
        System.out.println("One of the genes of the individuals is getting mutated and then finalizeChanges() is called");
        System.out.println("The mutation chance for testing is 1.0, but eventually MUTATION_CHANCE (a field) will determine by what chance an individual will be mutatedr\n");

        System.out.println("================Individuals Before Mutation================\n");
        for(int i = 0; i<population.length; i++){
            System.out.println("The unmutated individual number: " + i);
            System.out.println("The individual's gene for same rank is: " + population[i].getSameRankValue());
            System.out.println("The individual's gene for same suit, one rank away value is: " + population[i].getOneAwayRankSameSuitValue());
            System.out.println("The individual's gene for same suit, two rank away value is: " + population[i].getTwoAwayRankSameSuitValue());
            System.out.println();
        } 
       
        System.out.println("================Individuals After Mutation================\n");
            
        tester.mutation(population, 1.0);
        for(int i = 0; i<population.length; i++){
            System.out.println("The mutated individual number: " + i);
            System.out.println("The individual's gene for same rank is: " + population[i].getSameRankValue());
            System.out.println("The individual's gene for same suit, one rank away value is: " + population[i].getOneAwayRankSameSuitValue());
            System.out.println("The individual's gene for same suit, two rank away value is: " + population[i].getTwoAwayRankSameSuitValue());
            System.out.println();
        }
    }

    public static void main(String[] args){
        //geneticAlgorithm(6, 3, 10);
        geneticAlgorithm(100, 100, 100);
        //geneticAlgorithmTesting1(50, 1, 100); // popSize, numGenerations, gamesPerIndividual
        //geneticAlgorithmTesting(1, 100);
        // randomIndividualsTesting();
        // mutationTesting();
        // crossoverTesting();
        //selectTesting();
        //selectTestingWithLosers();
        // calcFitnessTesting();
    }

*///Remove this comment-out
}