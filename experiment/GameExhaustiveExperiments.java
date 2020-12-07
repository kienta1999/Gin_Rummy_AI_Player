package experiment;

import players.ParamList;
import players.SimpleFakeGinRummyPlayer;
import players.handeval.AceTwoBonusHandEvaluator;
import players.handeval.ConvHandEvaluator;
import players.handeval.DeadwoodHandEvaluator;
import players.handeval.EnsembleHandEvalPlayer;
import players.handeval.LinearDeadwoodPenaltyHandEvaluator;
import players.handeval.MeldabilityHandEvaluator;
import players.handeval.OppCardsKnownDeadwoodPenaltyHandEvaluator;

import java.util.ArrayList;

import ga.GATuner;
import games.TestingGame;

public class GameExhaustiveExperiments {

    private static boolean TESTING = false;

    private static void setTesting(boolean val) {
        TESTING = val;
    }

    private static java.util.concurrent.atomic.AtomicInteger indivCount = new java.util.concurrent.atomic.AtomicInteger(-1);

    public static double playManyGames(ParamList params, int gamesPerIndividual, 
                                       int selfFlag, int selfKnockDeciderFlag,  int selfDrawDeciderFlag, 
                                       int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag) {
        EnsembleHandEvalPlayer p0 = GATuner.setupSelf(params, selfKnockDeciderFlag, selfFlag, selfDrawDeciderFlag);
        // if (TESTING) System.out.println("Player type: " + p0.getClass());

        SimpleFakeGinRummyPlayer p1 = GATuner.setupOpp(opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        int p0Wins;

        TestingGame gameManager = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(false);
        p0Wins = 0;
        params.enforceRestrictions();

        for(int j = 0; j < gamesPerIndividual; j++){
            int winner = gameManager.play();
            if (winner == 0)
                p0Wins += 1; 
        }

        double winRate = (double) p0Wins / gamesPerIndividual;
        System.out.print(indivCount.incrementAndGet() + " "); // Note that this number does not necessarily match the order of the param list
        return winRate;
    }

    /*
    public static double[] calcWinRatesStream(ArrayList<ParamList> allParamLists, int gamesPerIndividual, 
                                              int selfFlag, int selfKnockDeciderFlag,  int selfDrawDeciderFlag, 
                                              int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag) {
        double[] winRates = new double[allParamLists.size()];
        System.out.println("----------------------------------\nProgress:");
        winRates = allParamLists.stream()
                                .parallel()
                                .map((x) -> {return playManyGames(x, gamesPerIndividual,
                                                                  selfFlag, selfKnockDeciderFlag, selfDrawDeciderFlag, 
                                                                  opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);})
                                .mapToDouble(x -> {return x;})
                                .toArray();
        System.out.println();
        indivCount.set(0); // reset the individual count now that the test is done
        return winRates;
    }
    */

    /**
     * Same as calcWinRatesStream, except splits the task up into smaller pieces, so that intermediate results can be printed (in case of
     * code crashing).
     * @param startingIndex The index in allParamLists to start at (in case we're restarting code due to the job being killed previously).
     */
    public static double[] calcWinRatesStreamSplit(int globalStartingIndex, ArrayList<ParamList> allParamLists, int gamesPerIndividual, 
                                              int selfFlag, int selfKnockDeciderFlag,  int selfDrawDeciderFlag, 
                                              int opponentFlag, int oppKnockDeciderFlag, int oppDrawDeciderFlag, int[] paramIDs) {
        double[] globalWinRates = new double[allParamLists.size()];

        int subsetMaxSize = 101; // 300; // Make ArrayList<ParamList> of size targetSize (or less)

        int currGlobalID = globalStartingIndex; // For new experiments, this is 0. For crashed experiments, this is the point of restarting.

        for(int subsetID = globalStartingIndex/subsetMaxSize; subsetID * subsetMaxSize < allParamLists.size(); subsetID++) {
            System.out.println("========== Subset " + subsetID + " ==========");
            ArrayList<ParamList> currSubset = new ArrayList<ParamList>(subsetMaxSize); // the current subset of ParamLists, to be considered in parallel
            int globalIndexOfSubsetStart = currGlobalID; // the global index that this subset starts at

            // Add targetSize ParamLists to currSubset (or fewer, if nearing end of allParamLists)
            for(int i = 0; i < subsetMaxSize && currGlobalID < allParamLists.size(); i++, currGlobalID++)
                currSubset.add(allParamLists.get(currGlobalID));

            if (TESTING) {
                System.out.print("First paramlist (row " + globalIndexOfSubsetStart + "): ");
                for(int pID = 0; pID < paramIDs.length; pID++)
                    System.out.print("p" + paramIDs[pID] + ": " + currSubset.get(0).get(paramIDs[pID]) + "  ");
                System.out.println();

                System.out.print("Last paramlist (row " + (currGlobalID-1) + "): ");
                for(int pID = 0; pID < paramIDs.length; pID++)
                    System.out.print("p" + paramIDs[pID] + ": " + currSubset.get(currSubset.size()-1).get(paramIDs[pID]) + "  ");
                System.out.println();

                System.out.println("Size: " + currSubset.size());
            }

            double[] currSubsetWinRates = new double[currSubset.size()]; // Will contain the win rates for this subset
            // Test the curr subset of all ParamLists, in parallel
            currSubsetWinRates = currSubset.stream().parallel().map((x) -> {return playManyGames(x, gamesPerIndividual,
                                                                                                 selfFlag, selfKnockDeciderFlag, selfDrawDeciderFlag, 
                                                                                                 opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);})
                                        .mapToDouble(x -> {return x;})
                                        .toArray();

            System.out.println();
            System.out.println("Subset " + subsetID + " results:");
            showTableOfResults(currSubset, paramIDs, currSubsetWinRates, globalIndexOfSubsetStart); // capture these partial results, in case there's a crash later

            // copy results into main winRates array
            for(int pID = 0; pID < currSubset.size(); pID++)
                globalWinRates[pID + globalIndexOfSubsetStart] = currSubsetWinRates[pID];
        }

        System.out.println();
        indivCount.set(0); // reset the individual count now that the test is done
        return globalWinRates;
    }

    private static void printParamRange(ParamList params, int from, int to) {
        for(int i = from; i < to; i++) {
            // System.out.print(params.get(i) + ", ");
            System.out.printf("%.3f, ", params.get(i));
        }
        // System.out.println(params.get(to));
        System.out.printf("%.3f\n", params.get(to));
    }

    /**
     * Makes an ArrayList of ParamList objects.
     * Each ParamList uses a copy of ParamList's defaultParamAL, with hardcoded ensemble weights.
     */
    private static ArrayList<ParamList> initializeParamLists(int numIndividuals, int numOfEvaluators, int selfFlag) {
        ParamList throwAway = new ParamList(getBaseParamsAndWeights(selfFlag), numOfEvaluators);
        System.out.println("Using these parameters as a base (some values will change in the exhaustive search):\n" + throwAway.toString()); // throwAway.toVerboseString());

        // Initialize the ArrayList of ParamLists
        ArrayList<ParamList> allParamLists = new ArrayList<ParamList>(numIndividuals);
        for(int paramListID = 0; paramListID < numIndividuals; paramListID++) {
            // ParamList currParams = new ParamList(ensembleWeights); // uses the defaultParamAL, and hardcoded ensemble weights
            ParamList currParams = new ParamList(getBaseParamsAndWeights(selfFlag), numOfEvaluators);

            // No need to work with the fixedValues ArrayList, since that's only used in mutation and generating a random ParamList.

            allParamLists.add(currParams);
        }

        return allParamLists;
    }

    public static double computeStandardDeviation(double[] winRates, double ... av) {
        double average;
        if (av.length == 0) {
            double sum = 0;
            for(int i = 0; i < winRates.length; i++)
                sum += winRates[i];

            average = sum / winRates.length;
        }
        else if (av.length == 1)
            average = av[0];
        else
            throw new RuntimeException("Too many arguments passed to computeStandardDeviation: " + winRates + ", " + av);

        // Compute standard deviation
        double sum = 0;
        for(int i = 0; i < winRates.length; i++) {
            double diff = (winRates[i] - average);
            sum += diff*diff;
        }
        double stdDev = Math.sqrt(sum / winRates.length);
        return stdDev;
    }

    /**
     * 
     * @param allParamLists
     * @param winRates
     * @return The best ParamList
     */
    public static ParamList analyzeWinRates(ArrayList<ParamList> allParamLists, double[] winRates) {
        double maxWinRate = winRates[0];
        int maxParamsID = 0;

        double minWinRate = winRates[0];
        int minParamsID = 0;

        double sum = 0;

        for(int i = 1; i < winRates.length; i++) {
            sum += winRates[i];
            if (maxWinRate < winRates[i]) {
                maxWinRate = winRates[i];
                maxParamsID = i;
            }
            else if (winRates[i] < minWinRate) {
                minWinRate = winRates[i];
                minParamsID = i;
            }
        }

        System.out.println("----------\nMax win rate: " + maxWinRate);
        System.out.println("Corresponding max params at index " + maxParamsID + ":\n" + allParamLists.get(maxParamsID).toString()); // allParamLists.get(maxParamsID).toVerboseString());

        System.out.println("----------\nMin win rate: " + minWinRate);
        System.out.println("Corresponding min params at index " + minParamsID + ":\n" + allParamLists.get(minParamsID).toString()); // allParamLists.get(minParamsID).toVerboseString());

        double average = sum / winRates.length;
        System.out.println("----------\nAverage win rate: " + average);

        // Compute standard deviation
        double stdDev = computeStandardDeviation(winRates, average);

        /*
        sum = 0;
        for(int i = 0; i < winRates.length; i++) {
            double diff = (winRates[i] - average);
            sum += diff*diff;
        }
        double stdDev = Math.sqrt(sum / winRates.length);
        */

        System.out.println("Standard deviation of win rates: " + stdDev);

        return allParamLists.get(maxParamsID);
    }

    /**
     * @return The array of win rates.
     */
    public static double[] runExhaustiveTest(int selfFlag, int globalStartingIndex, ArrayList<ParamList> allParamLists, int gamesPerIndividual, int[] paramIDs) {
        System.out.println("=============== runExhaustiveTest ===============");
        System.out.println("gamesPerIndividual: " + gamesPerIndividual);

        // Make the players
        // int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER; // gets passed in instead
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_TWO_STAGE_DRAW_DECIDER; // GATuner.SELF_DEADWOOD_DRAW_DECIDER;
        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;

        // Run the test
        double start, elapsed;
        start = System.currentTimeMillis();

        /*
        double[] winRates = calcWinRatesStream(allParamLists, gamesPerIndividual, 
                                               selfFlag, selfKnockDeciderFlag, selfDrawDeciderFlag, 
                                               opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
                                               */
        double[] winRates = calcWinRatesStreamSplit(globalStartingIndex, allParamLists, gamesPerIndividual, 
                                                    selfFlag, selfKnockDeciderFlag, selfDrawDeciderFlag, 
                                                    opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag, paramIDs);

        if (TESTING) {
            System.out.println("winRates as an array: " + java.util.Arrays.toString(winRates));
            System.out.println("winRates as a table: ");
            for(int i = 0; i < winRates.length; i++)
                System.out.println(i + ", " + winRates[i]);
        }

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (m): " + elapsed/1000/60);

        return winRates;
    }

    public static double runSinglePlayerTest(int selfFlag, ParamList params, int numGames) {
        // Make the players
        // int selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER; // gets passed in instead
        int selfKnockDeciderFlag = GATuner.SELF_TWO_STAGE_KNOCK_DECIDER;
        int selfDrawDeciderFlag = GATuner.SELF_TWO_STAGE_DRAW_DECIDER; // GATuner.SELF_DEADWOOD_DRAW_DECIDER;
        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;

        double winRate = playManyGames(params, numGames, 
                                       selfFlag, selfKnockDeciderFlag, selfDrawDeciderFlag, 
                                       opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);
        return winRate;
    }

    // VERIFIED 7/17
    private static ArrayList<ParamList> makeParamListsForMeldabilityHE(int numOfEvaluators, int selfFlag) {
        // Set up the max for each parameter (min is assumed to be 0)
        double lowMax = 0.5;
        double ratioMax = 1.0;
        double rankMax = 1.0;
        // run not shown, since it's just 1-rankVal

        // Set up the step size for each parameter
        double lowStep = .05;
        double ratioStep = .10;
        double rankStep = .10;
        // run not shown, since it's just 1-rankVal

        int numIndividuals = (int) ((lowMax / lowStep + 1) * (ratioMax / ratioStep + 1) * (rankMax / rankStep + 1)); // +1 for each because loop is <=, not <

        ArrayList<ParamList> allParamLists = initializeParamLists(numIndividuals, numOfEvaluators, selfFlag);

        // Systematically set the param values we want to consider
        // Ranges are based on the possible ranges in getRandomParamList.
        System.out.println("----------------------------------\nparams:");
        int paramListID = 0;
        for(double lowVal = 0; lowVal <= lowMax; lowVal = round(lowVal + lowStep)) {
            for(double ratioVal = 0; ratioVal <= ratioMax; ratioVal = round(ratioVal + ratioStep)) {
                for(double rankVal = 0; rankVal <= rankMax; rankVal = round(rankVal + rankStep)) {
                    double runVal = 1 - rankVal;
                    runVal = round(runVal);
                    allParamLists.get(paramListID).set(ParamList.MC_SELF_LOW_OBTAINABILITY, lowVal);
                    allParamLists.get(paramListID).set(ParamList.MC_SELF_RATIO_FOR_UNKNOWN, ratioVal);
                    allParamLists.get(paramListID).set(ParamList.MC_SELF_WRANK, rankVal);
                    allParamLists.get(paramListID).set(ParamList.MC_SELF_WRUN, runVal);
                    if (TESTING) { 
                        System.out.printf("%7d, ", paramListID);
                        printParamRange(allParamLists.get(paramListID), ParamList.MC_SELF_LOW_OBTAINABILITY, ParamList.MC_SELF_WRUN);
                    }
                    paramListID++;
                }
            }
        }
        System.out.println("MeldabilityHE ParamLists are ready.");
        System.out.println("numIndividuals: " + numIndividuals + ". Correct number is: " + paramListID);
        util.OurUtil.assertCondition(numIndividuals == paramListID, "Mismatch between numIndividuals and paramListID.");

        return allParamLists;
    }

    // VERIFIED 7/17
    private static ArrayList<ParamList> makeParamListsForConvHE(int numOfEvaluators, int selfFlag) {
        // same, one, and two should add up to 1.0

        // Set up the max for each parameter (min is assumed to be 0)
        double sameMax = 1.0;
        double oneMax = 1.0;
        // two not shown, since it's just 1-(same+one)

        // Set up the step size for each parameter
        double sameStep = .10;
        double oneStep = .10;
        // two not shown, since it's just 1-(same+one)

        int numIndividuals = 34; // hard to calculate, so just run this code, check what paramListID value is printed after the loop, and set it here

        ArrayList<ParamList> allParamLists = initializeParamLists(numIndividuals, numOfEvaluators, selfFlag);

        // Systematically set the param values we want to consider
        // Ranges are based on the possible ranges in getRandomParamList.
        int paramListID = 0;
        for(double sameVal = 0; sameVal <= sameMax; sameVal = round(sameVal + sameStep)) {
            for(double oneVal = 0; oneVal <= (oneMax-sameVal); oneVal = round(oneVal + oneStep)) {
                double twoVal = 1 - (sameVal + oneVal);
                twoVal = round(twoVal);
                if (oneVal >= twoVal) {
                    allParamLists.get(paramListID).set(ParamList.CH_SAMERANK, sameVal);
                    allParamLists.get(paramListID).set(ParamList.CH_ONEAWAY, oneVal);
                    allParamLists.get(paramListID).set(ParamList.CH_TWOAWAY, twoVal);
                    if (TESTING) {
                        System.out.printf("%7d, ", paramListID);
                        printParamRange(allParamLists.get(paramListID), ParamList.CH_SAMERANK, ParamList.CH_TWOAWAY);
                    }
                    paramListID++;
                }
                // else skip this one, it doesn't follow the ordering requirement
            }
        }
        System.out.println("ConvHE ParamLists are ready.");
        System.out.println("numIndividuals: " + numIndividuals + ". Correct number is: " + paramListID);
        util.OurUtil.assertCondition(numIndividuals == paramListID, "Mismatch between numIndividuals and paramListID.");

        return allParamLists;
    }

    // VERIFIED 7/17
    private static ArrayList<ParamList> makeParamListsForLinearDeadwoodPenaltyHE(int numOfEvaluators, int selfFlag) {
        // Set up the max for each parameter (min is assumed to be 0)
        double slopeMax = 1.0;
        double expMax = 4.0; // this is what's used in ParamList's check bounds

        // Set up the step size for each parameter
        double slopeStep = .10;
        double expStep = .25;

        int numIndividuals = (int) ((slopeMax / slopeStep + 1) * (expMax / expStep + 1));

        ArrayList<ParamList> allParamLists = initializeParamLists(numIndividuals, numOfEvaluators, selfFlag);

        // Systematically set the param values we want to consider
        // Ranges are based on the possible ranges in getRandomParamList.
        int paramListID = 0;
        for(double slopeVal = 0; slopeVal <= slopeMax; slopeVal = round(slopeVal + slopeStep)) {
            for(double expVal = 0; expVal <= expMax; expVal = round(expVal + expStep)) {
                allParamLists.get(paramListID).set(ParamList.LD_PENALTY_SLOPE, slopeVal);
                allParamLists.get(paramListID).set(ParamList.LD_PENALTY_EXPONENT, expVal);
                if (TESTING) {
                    System.out.printf("%7d, ", paramListID);
                    printParamRange(allParamLists.get(paramListID), ParamList.LD_PENALTY_SLOPE, ParamList.LD_PENALTY_EXPONENT);
                }
                paramListID++;
            }
        }
        System.out.println("LinearDeadwoodPenaltyHE ParamLists are ready.");
        System.out.println("numIndividuals: " + numIndividuals + ". Correct number is: " + paramListID);
        util.OurUtil.assertCondition(numIndividuals == paramListID, "Mismatch between numIndividuals and paramListID.");

        return allParamLists;
    }

    // VERIFIED 7/17
    private static ArrayList<ParamList> makeParamListsForOppCardsKnownDeadwoodPenaltyHE(int numOfEvaluators, int selfFlag) {
        // Set up the max for each parameter (min is assumed to be 0)
        double slopeMax = 1.0;
        double expMax = 4.0; // this is what's used in ParamList's check bounds

        // Set up the step size for each parameter
        double slopeStep = .10;
        double expStep = .25;

        int numIndividuals = (int) ((slopeMax / slopeStep + 1) * (expMax / expStep + 1));

        ArrayList<ParamList> allParamLists = initializeParamLists(numIndividuals, numOfEvaluators, selfFlag);

        // Systematically set the param values we want to consider
        // Ranges are based on the possible ranges in getRandomParamList.
        int paramListID = 0;
        for(double slopeVal = 0; slopeVal <= slopeMax; slopeVal = round(slopeVal + slopeStep)) {
            for(double expVal = 0; expVal <= expMax; expVal = round(expVal + expStep)) {
                expVal = round(expVal);
                allParamLists.get(paramListID).set(ParamList.OD_PENALTY_SLOPE, slopeVal);
                allParamLists.get(paramListID).set(ParamList.OD_PENALTY_EXPONENT, expVal);
                if (TESTING) {
                    System.out.printf("%7d, ", paramListID);
                    printParamRange(allParamLists.get(paramListID), ParamList.OD_PENALTY_SLOPE, ParamList.OD_PENALTY_EXPONENT);
                }
                paramListID++;
            }
        }
        System.out.println("OppCardsKnownDeadwoodPenaltyHE ParamLists are ready.");
        System.out.println("numIndividuals: " + numIndividuals + ". Correct number is: " + paramListID);
        util.OurUtil.assertCondition(numIndividuals == paramListID, "Mismatch between numIndividuals and paramListID.");

        return allParamLists;
    }

    // This will be a very expensive test
    // VERIFIED 7/17
    private static ArrayList<ParamList> makeParamListsForMultiOppHE(int numOfEvaluators, int selfFlag) {
        // if (true) throw new RuntimeException("Make sure you add MultiOpp to the ensemble members, and change numOfEvaluators. Then, comment out this error message.");

        // Set up the max for each parameter (min is assumed to be 0)
        double numMin = 1;
        double numMax = 1; // 2; // 10; // will be expensive!

        double lowMax = 0.5;
        double ratioMax = 1.0;
        double rankMax = 1.0;
        // run not shown, since it's just 1-rankVal

        // Set up the step size for each parameter
        double numStep = 1.0;
        double lowStep = .10;
        double ratioStep = .20;
        double rankStep = .20;
        // run not shown, since it's just 1-rankVal

        int numIndividuals = (int) (((numMax-numMin) / numStep + 1) * (lowMax / lowStep + 1) * (ratioMax / ratioStep + 1) * (rankMax / rankStep + 1)); // +1 for each because loop is <=, not <

        ArrayList<ParamList> allParamLists = initializeParamLists(numIndividuals, numOfEvaluators, selfFlag);

        // Systematically set the param values we want to consider
        // Ranges are based on the possible ranges in getRandomParamList.
        int paramListID = 0;
        for(double numVal = numMin; numVal <= numMax; numVal = round(numVal + numStep)) {
            for(double lowVal = 0; lowVal <= lowMax; lowVal = round(lowVal + lowStep)) {
                for(double ratioVal = 0; ratioVal <= ratioMax; ratioVal = round(ratioVal + ratioStep)) {
                    for(double rankVal = 0; rankVal <= rankMax; rankVal = round(rankVal + rankStep)) {
                        double runVal = 1 - rankVal;
                        runVal = round(runVal);
                        allParamLists.get(paramListID).set(ParamList.OM_NUM_OF_ADDITIONAL_CARDS, numVal);
                        allParamLists.get(paramListID).set(ParamList.MC_OPP_LOW_OBTAINABILITY, lowVal);
                        allParamLists.get(paramListID).set(ParamList.MC_OPP_RATIO_FOR_UNKNOWN, ratioVal);
                        allParamLists.get(paramListID).set(ParamList.MC_OPP_WRANK, rankVal);
                        allParamLists.get(paramListID).set(ParamList.MC_OPP_WRUN, runVal);
                        if (TESTING) {
                            System.out.printf("%7d, ", paramListID);
                            ParamList params = allParamLists.get(paramListID);
                            System.out.printf("%.2f, ", params.get(ParamList.OM_NUM_OF_ADDITIONAL_CARDS));
                            int from = ParamList.MC_OPP_LOW_OBTAINABILITY;
                            int to = ParamList.MC_OPP_WRUN;
                            for(int i = from; i < to; i++) {
                                System.out.printf("%.2f, ", params.get(i));
                            }
                            System.out.printf("%.2f\n", params.get(to));
                        }
                        paramListID++;
                    }
                }
            }
        }
        System.out.println("MultiOppHE ParamLists are ready.");
        System.out.println("numIndividuals: " + numIndividuals + ". Correct number is: " + paramListID);
        util.OurUtil.assertCondition(numIndividuals == paramListID, "Mismatch between numIndividuals and paramListID.");

        return allParamLists;
    }

    public static double round(double n) {
        int places = 100000;
        return ((double) Math.round(n*places)) / places;
    }

    private static ArrayList<ParamList> makeParamListsForSixEnsembleWeights(int numOfEvaluators, int selfFlag) {
        util.OurUtil.assertCondition(numOfEvaluators == 6, "This ensemble weight exhaustive search code requires that numOfEvaluators=6.");

        // All weights must add up to 1.0.
        // Values range from 0 to 1
        double step = 0.10;

        double[] weights = new double[numOfEvaluators];
        double[] startingAmountLeft = new double[numOfEvaluators];

        int numIndividuals = 3003;  // hard to calculate, so just run this code, check what paramListID value is printed after the loop, and set it here

        ArrayList<ParamList> allParamLists = new ArrayList<ParamList>(numIndividuals); // initializeParamLists(numIndividuals, numOfEvaluators);

        int paramListID = 0;
        startingAmountLeft[0] = 1.0;
        for(weights[0] = 0; weights[0] <= startingAmountLeft[0]; weights[0] = round(weights[0] + step)) {

            startingAmountLeft[1] = round(startingAmountLeft[1-1] - weights[1-1]);
            for(weights[1] = 0; weights[1] <= startingAmountLeft[1]; weights[1] = round(weights[1] + step)) {

                startingAmountLeft[2] = round(startingAmountLeft[2-1] - weights[2-1]);
                for(weights[2] = 0; weights[2] <= startingAmountLeft[2]; weights[2] = round(weights[2] + step)) {

                    startingAmountLeft[3] = round(startingAmountLeft[3-1] - weights[3-1]);
                    for(weights[3] = 0; weights[3] <= startingAmountLeft[3]; weights[3] = round(weights[3] + step)) {
    
                        startingAmountLeft[4] = round(startingAmountLeft[4-1] - weights[4-1]);
                        for(weights[4] = 0; weights[4] <= startingAmountLeft[4]; weights[4] = round(weights[4] + step)) {
                            weights[5] = round(1 - (weights[0] + weights[1] + weights[2] + weights[3] + weights[4]));

                            // Make the ParamList with base values
                            ParamList currParamList = new ParamList(getBaseParamsAndWeights(selfFlag), numOfEvaluators);
                            // Now, change the ensemble weights to be what's in the weights array
                            for(int memberID = 0; memberID < numOfEvaluators; memberID++)
                                currParamList.set(ParamList.NUM_NONENSEMBLE_PARAMS + memberID, weights[memberID]);

                            if (TESTING) {
                                System.out.printf("%7d, ", paramListID);
                                printParamRange(currParamList, ParamList.NUM_NONENSEMBLE_PARAMS, ParamList.NUM_NONENSEMBLE_PARAMS + currParamList.getNumEnsembleWeights() - 1);
                            }
                            allParamLists.add(currParamList);
                            paramListID++;
                        }
                    }
                }
            }
        }
        System.out.println("Ensemble weight ParamLists are ready.");
        System.out.println("numIndividuals: " + numIndividuals + ". Correct number is: " + paramListID);
        util.OurUtil.assertCondition(numIndividuals == paramListID, "Mismatch between numIndividuals and paramListID.");

        return allParamLists;
    }

    /**
     * Yikes, this is an expensive test.
     */
    private static ArrayList<ParamList> makeParamListsForSevenEnsembleWeights(int numOfEvaluators, int selfFlag) {
        // if (true) throw new RuntimeException("This method doesn't work yet, because GATuner's setupSelf needs to include MultiOpp first.");

        util.OurUtil.assertCondition(numOfEvaluators == 7, "This ensemble weight exhaustive search code requires that numOfEvaluators=7.");

        // All weights must add up to 1.0.
        // Values range from 0 to 1
        double step = 0.10;

        double[] weights = new double[numOfEvaluators];
        double[] startingAmountLeft = new double[numOfEvaluators];

        int numIndividuals = 8008;  // hard to calculate, so just run this code, check what paramListID value is printed after the loop, and set it here

        ArrayList<ParamList> allParamLists = new ArrayList<ParamList>(numIndividuals); // initializeParamLists(numIndividuals, numOfEvaluators);

        int paramListID = 0;
        startingAmountLeft[0] = 1.0;
        for(weights[0] = 0; weights[0] <= startingAmountLeft[0]; weights[0] = round(weights[0] + step)) {

            startingAmountLeft[1] = round(startingAmountLeft[1-1] - weights[1-1]);
            for(weights[1] = 0; weights[1] <= startingAmountLeft[1]; weights[1] = round(weights[1] + step)) {

                startingAmountLeft[2] = round(startingAmountLeft[2-1] - weights[2-1]);
                for(weights[2] = 0; weights[2] <= startingAmountLeft[2]; weights[2] = round(weights[2] + step)) {

                    startingAmountLeft[3] = round(startingAmountLeft[3-1] - weights[3-1]);
                    for(weights[3] = 0; weights[3] <= startingAmountLeft[3]; weights[3] = round(weights[3] + step)) {
    
                        startingAmountLeft[4] = round(startingAmountLeft[4-1] - weights[4-1]);
                        for(weights[4] = 0; weights[4] <= startingAmountLeft[4]; weights[4] = round(weights[4] + step)) {

                            startingAmountLeft[5] = round(startingAmountLeft[5-1] - weights[5-1]);
                            for(weights[5] = 0; weights[5] <= startingAmountLeft[5]; weights[5] = round(weights[5] + step)) {
    
                                weights[6] = round(1 - (weights[0] + weights[1] + weights[2] + weights[3] + weights[4] + weights[5]));

                                // Make the ParamList with base values
                                ParamList currParamList = new ParamList(getBaseParamsAndWeights(selfFlag), numOfEvaluators);
                                // Now, change the ensemble weights to be what's in the weights array
                                for(int memberID = 0; memberID < numOfEvaluators; memberID++)
                                    currParamList.set(ParamList.NUM_NONENSEMBLE_PARAMS + memberID, weights[memberID]);

                                if (TESTING) {
                                    System.out.printf("%7d, ", paramListID);
                                    printParamRange(currParamList, ParamList.NUM_NONENSEMBLE_PARAMS, ParamList.NUM_NONENSEMBLE_PARAMS + currParamList.getNumEnsembleWeights() - 1);
                                }
                                allParamLists.add(currParamList);
                                paramListID++;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Ensemble weight ParamLists are ready.");
        System.out.println("numIndividuals: " + numIndividuals + ". Correct number is: " + paramListID);
        util.OurUtil.assertCondition(numIndividuals == paramListID, "Mismatch between numIndividuals and paramListID.");

        return allParamLists;
    }
    
    public static void showTableOfResults(ArrayList<ParamList> paramLists, int[] paramIDs, double[] winRates, int startingID) {
        System.out.println("----------------------------------------\nTable of results: ");
        System.out.print("ID, ");
        for(int p = 0; p < paramIDs.length; p++)
            System.out.print("p" + paramIDs[p] + ", ");
        System.out.println("winRate");

        for(int i = 0; i < paramLists.size(); i++) {
            System.out.print((i + startingID) + ", ");
            for (int p = 0; p < paramIDs.length; p++) {
                System.out.printf("%.6f, ", paramLists.get(i).get(paramIDs[p]));
            }
            System.out.println(winRates[i]);
        }
    }

    public static void displayResults(ArrayList<ParamList> allParamLists, int[] paramIDs, double[] winRates) {
        ParamList bestParams = analyzeWinRates(allParamLists, winRates);

        showTableOfResults(allParamLists, paramIDs, winRates, 0);

        System.out.println("----------------------------------------\nLearned parameters: ");
        for (int i = 0; i < paramIDs.length; i++)
            System.out.println(paramIDs[i] + ": " + bestParams.get(paramIDs[i]));
        System.out.println("All parameters:\n" + bestParams.toString()); // bestParams.toVerboseString());
    }

    public static void runStdDevCheckExperiment(int selfFlag, int numOfEvaluators) {
        ParamList params = new ParamList(getBaseParamsAndWeights(selfFlag), numOfEvaluators);

        int numTrials = 1000;
        int[] gameCounts = new int[] {100, 500, 1000, 2000, 4000, 6000, 8000, 10000};
        // int[] gameCounts = new int[] {10, 50, 100, 500};
        double[] stdDevs = new double[gameCounts.length];
        for(int gameCountID = 0; gameCountID < gameCounts.length; gameCountID++) {
            final int gameCount = gameCounts[gameCountID];

            double[] winRates = new double[numTrials];
            winRates = java.util.stream.DoubleStream.iterate(0, i -> i+1)
                                                    .limit(numTrials)
                                                    .parallel()
                                                    .map((trialID) -> {return runSinglePlayerTest(selfFlag, new ParamList(params), gameCount);})
                                                    .toArray();

            /*
            double[] winRates = new double[numTrials];
            for(int i = 0; i < numTrials; i++)
                winRates[i] = runSinglePlayerTest(selfFlag, params, gameCount);
            */

            double stdDev = computeStandardDeviation(winRates);
            System.out.println("\nStd dev for " + numTrials + " trials of " + gameCount + " games: " + stdDev);
            stdDevs[gameCountID] = stdDev;
        }
        System.out.println("Over " + numTrials + " trials each\ngameCount, stdDev");
        for(int i = 0; i < stdDevs.length; i++) {
            System.out.printf("%8d, %.6f\n", gameCounts[i], stdDevs[i]);
        }
    }

    /**
     * Get the hardcoded params and weights that will serve as the base values 
     * (before some parameters are searched exhaustively).
     * This replaces how we were previously getting the base params - by encoding them in
     * in ParamList's getDefaultParamAL.
     * 
     * selfFlag is used because that determines which set of params to use - the ones developed
     * in experiments for Ensemble player, or the ones developed in experiments for Index player.
     */
    public static double[] getBaseParamsAndWeights(int selfFlag) {
        // The ordering of ensemble weights is determined by GATuner's setupSelf:
        // MeldabilityHandEvaluator, DeadwoodHandEvaluator, AceTwoBonusHandEvaluator, ConvHandEvaluator, LinearDeadwoodPenaltyHandEvaluator, OppCardsKnownDeadwoodPenaltyHandEvaluator, (MultiOpp)

        // originalGuesses is the hardcoded values we used for weeks
        // double[] originalGuesses = new double[] {0.3333333333333333, 0.4444444444444444, 0.2222222222222222, 0.05, 0.5, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.01, 1.0, 0.01, 2.0, 10.0, 0.0, 6.0, 8.0, 14.0, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666};

        // array names use the same convention as the file names: it<iteration number>_<experiment number><E or I>
        // They reflect the best parameters at the end of that experiment (accumulating parameters from prior experiments).
        if (selfFlag == GATuner.SELF_IS_ENSEMBLE_PLAYER) {
            // Ensemble, after ex1 - ex5, iteration 1
            // double[] pE1To5iter1 = new double[] {0.5, 0.4, 0.1, 0.1, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 0.25, 10.0, 0.0, 6.0, 8.0, 14.0, 0.16666666666666669, 0.16666666666666669, 0.16666666666666669, 0.16666666666666669, 0.16666666666666669, 0.16666666666666669};
            // double[] pE1To6iter1 = new double[] {0.5, 0.4, 0.1, 0.1, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 0.25, 10.0, 0.0, 6.0, 8.0, 14.0, 0.19999999999999998, 0.5, 0.09999999999999999, 0.09999999999999999, 0.09999999999999999, 0.0};
            // double[] p_it2_1E = new double[] {0.5, 0.4, 0.1, 0.35, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 0.25, 10.0, 0.0, 6.0, 8.0, 14.0, 0.19999999999999996, 0.5, 0.09999999999999998, 0.09999999999999998, 0.09999999999999998, 0.0};
            // double[] p_it2_2E = new double[] {0.4, 0.4, 0.2, 0.35, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 0.25, 10.0, 0.0, 6.0, 8.0, 14.0, 0.19999999999999998, 0.5000000000000001, 0.09999999999999999, 0.09999999999999999, 0.09999999999999999, 0.0};
            // double[] p_it2_3E = new double[] {0.4, 0.4, 0.2, 0.35, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 0.25, 10.0, 0.0, 6.0, 8.0, 14.0, 0.19999999999999998, 0.5000000000000001, 0.09999999999999999, 0.09999999999999999, 0.09999999999999999, 0.0};
            // double[] p_it2_4E = new double[] {0.4, 0.4, 0.2, 0.35, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 2.0, 10.0, 0.0, 6.0, 8.0, 14.0, 0.19999999999999998, 0.5000000000000001, 0.09999999999999999, 0.09999999999999999, 0.09999999999999999, 0.0};
            // double[] p_it2_6E = new double[] {0.4, 0.4, 0.2, 0.35, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 2.0, 10.0, 0.0, 6.0, 8.0, 14.0, 0.2, 0.1, 0.0, 0.1, 0.5, 0.1};

            // double[] p_it3_1E = new double[] {0.4, 0.4, 0.2, 0.45, 0.7, 0.5, 0.5, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 2.0, 10.0, 0.0, 6.0, 8.0, 14.0, 0.2, 0.1, 0.0, 0.1, 0.5, 0.1};
            // double[] p_it3_2E = new double[] {0.7, 0.3, 0.0, 0.45, 0.7, 0.5, 0.5, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 2.0, 10.0, 0.0, 6.0, 8.0, 14.0, 0.2, 0.1, 0.0, 0.1, 0.5, 0.1};
            // double[] p_it3_3E = new double[] {0.7, 0.3, 0.0, 0.45, 0.7, 0.5, 0.5, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 3.5, 0.0, 2.0, 10.0, 0.0, 6.0, 8.0, 14.0, 0.2, 0.1, 0.0, 0.1, 0.5, 0.1};
            // double[] p_it3_4E = new double[] {0.7, 0.3, 0.0, 0.45, 0.7, 0.5, 0.5, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 3.5, 0.0, 1.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.2, 0.1, 0.0, 0.1, 0.5, 0.1};
            // double[] p_it3_6E = new double[] {0.7, 0.3, 0.0, 0.45, 0.7, 0.5, 0.5, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 3.5, 0.0, 1.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.1, 0.0, 0.1, 0.0, 0.6, 0.2};
               //                                0    1    2    3     4    5    6    7     8    9    10   11                  12                   13                   14                   15                   16                   17                  18                  19                   20   21   22   23    24   25   26    27   28   29   30    31                   32                  33                   34                   35                   36

            // double[] includeMultiOpp = new double[] {0.7, 0.3, 0.0, 0.45, 0.7, 0.5, 0.5, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 3.5, 0.0, 1.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.1, 0.0, 0.1, 0.0, 0.6, 0.2, 0.1}; // added 0.1 for MultiOpp, so sum is 1.1, will be normalized

            double[] withBestMultiOppFromNum0Or1 = new double[] {0.7, 0.3, 0.0, 0.45, 0.7, 0.5, 0.5, 0.1, 1.0, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 0.0, 0.0, 3.5, 0.0, 1.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.09090909090909091, 0.0, 0.09090909090909091, 0.0, 0.5454545454545454, 0.18181818181818182, 0.09090909090909091};

            return withBestMultiOppFromNum0Or1;
        }
        else if (selfFlag == GATuner.SELF_IS_INDEX_PLAYER) {
            // Starting with it1 ensemble results
            // double[] guessSameAsEnsembleIter1 = new double[] {0.5, 0.4, 0.1, 0.1, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 0.25, 10.0, 0.0, 6.0, 8.0, 14.0, 0.19999999999999998, 0.5, 0.09999999999999999, 0.09999999999999999, 0.09999999999999999, 0.0};
            // double[] p_it1_1I = new double[] {0.5, 0.4, 0.1, 0.4, 0.0, 0.1, 0.9, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 0.25, 10.0, 0.0, 6.0, 8.0, 14.0, 0.19999999999999996, 0.5, 0.09999999999999998, 0.09999999999999998, 0.09999999999999998, 0.0};
            // double[] p_it1_2I = new double[] {0.4, 0.3, 0.3, 0.4, 0.0, 0.1, 0.9, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 0.25, 10.0, 0.0, 6.0, 8.0, 14.0, 0.19999999999999998, 0.5000000000000001, 0.09999999999999999, 0.09999999999999999, 0.09999999999999999, 0.0};
            // double[] p_it1_3I = new double[] {0.4, 0.3, 0.3, 0.4, 0.0, 0.1, 0.9, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.7, 1.5, 0.0, 0.25, 10.0, 0.0, 6.0, 8.0, 14.0, 0.19999999999999998, 0.5000000000000001, 0.09999999999999999, 0.09999999999999999, 0.09999999999999999, 0.0};
            // double[] p_it1_4I = new double[] {0.4, 0.3, 0.3, 0.4, 0.0, 0.1, 0.9, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.7, 1.5, 0.3, 0.0, 10.0, 0.0, 6.0, 8.0, 14.0, 0.19999999999999998, 0.5000000000000001, 0.09999999999999999, 0.09999999999999999, 0.09999999999999999, 0.0};
            // double[] p_it1_6I = new double[] {0.4, 0.3, 0.3, 0.4, 0.0, 0.1, 0.9, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.7, 1.5, 0.3, 0.0, 10.0, 0.0, 6.0, 8.0, 14.0, 0.1, 0.1, 0.2, 0.6, 0.0, 0.0};

            // double[] p_it1_1I = new double[] {0.3333333333333333, 0.4444444444444444, 0.2222222222222222, 0.35, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.01, 1.0, 0.01, 2.0, 10.0, 0.0, 6.0, 8.0, 14.0, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666};
            // double[] p_it1_2I = new double[] {0.4, 0.3, 0.3, 0.35, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.01, 1.0, 0.01, 2.0, 10.0, 0.0, 6.0, 8.0, 14.0, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666};
            // double[] p_it1_3I = new double[] {0.4, 0.3, 0.3, 0.35, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.3, 2.0, 0.01, 2.0, 10.0, 0.0, 6.0, 8.0, 14.0, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666};
            // double[] p_it1_4I = new double[] {0.4, 0.3, 0.3, 0.35, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.3, 2.0, 1.0, 1.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666, 0.16666666666666666};
            // double[] p_it1_6I = new double[] {0.4, 0.3, 0.3, 0.35, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.3, 2.0, 1.0, 1.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.1, 0.0, 0.3, 0.3, 0.3, 0.0};

            // double[] p_it2_1I = new double[] {0.4, 0.3, 0.3, 0.45, 0.7, 0.7, 0.3, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.3, 2.0, 1.0, 1.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.1, 0.0, 0.3, 0.3, 0.3, 0.0};
            // double[] p_it2_2I = new double[] {0.3, 0.4, 0.3, 0.45, 0.7, 0.7, 0.3, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.3, 2.0, 1.0, 1.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.1, 0.0, 0.3, 0.3, 0.3, 0.0};
            // double[] p_it2_3I = new double[] {0.3, 0.4, 0.3, 0.45, 0.7, 0.7, 0.3, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.4, 0.5, 1.0, 1.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.1, 0.0, 0.3, 0.3, 0.3, 0.0};
            // double[] p_it2_4I = new double[] {0.3, 0.4, 0.3, 0.45, 0.7, 0.7, 0.3, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.4, 0.5, 0.5, 2.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.1, 0.0, 0.3, 0.3, 0.3, 0.0};
            // double[] p_it2_6I = new double[] {0.3, 0.4, 0.3, 0.45, 0.7, 0.7, 0.3, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.4, 0.5, 0.5, 2.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.10000000000000002, 0.0, 0.6000000000000001, 0.20000000000000004, 0.10000000000000002, 0.0};

            // double[] p_it3_1I = new double[] {0.3, 0.4, 0.3, 0.4, 0.2, 1.0, 0.0, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.4, 0.5, 0.5, 2.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.09999999999999999, 0.0, 0.6000000000000001, 0.19999999999999998, 0.09999999999999999, 0.0};
            // double[] p_it3_2I = new double[] {0.6, 0.3, 0.1, 0.4, 0.2, 1.0, 0.0, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.4, 0.5, 0.5, 2.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.09999999999999999, 0.0, 0.6000000000000001, 0.19999999999999998, 0.09999999999999999, 0.0};
            // double[] p_it3_3I = new double[] {0.6, 0.3, 0.1, 0.4, 0.2, 1.0, 0.0, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 1.0, 3.5, 0.5, 2.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.09999999999999999, 0.0, 0.6000000000000001, 0.19999999999999998, 0.09999999999999999, 0.0};
            // double[] p_it3_4I = new double[] {0.6, 0.3, 0.1, 0.4, 0.2, 1.0, 0.0, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 1.0, 3.5, 0.2, 2.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.09999999999999999, 0.0, 0.6000000000000001, 0.19999999999999998, 0.09999999999999999, 0.0};
            double[] p_it3_6I = new double[] {0.6, 0.3, 0.1, 0.4, 0.2, 1.0, 0.0, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 1.0, 3.5, 0.2, 2.5, 10.0, 0.0, 6.0, 8.0, 14.0, 0.1, 0.1, 0.2, 0.3, 0.0, 0.3};

               //                                0    1    2    3     4    5    6    7     8    9    10   11                  12                   13                   14                   15                   16                   17                  18                  19                   20   21   22   23    24   25   26    27   28   29   30    31                   32                  33                   34                   35                   36

            return p_it3_6I;
        }
        else
            throw new RuntimeException("Unrecognized selfFlag: " + selfFlag);
    }

    /**
     * Pass two command-line arguments.
     * First is an int representing the test to perform.
     * Second is I for self Index player, or E for self Ensemble player.
     */
    public static void main(String[] args) {
        setTesting(false);

        int numOfEvaluators = 7; // number of ensemble members (note that the ensemble weight search is hardcoded for 6 ensemble members)
        int gamesPerIndividual = 5000;

        if (args.length >= 2) {
            System.out.println("Using command-line arguments: " + args[0] + ", " + args[1]);

            ArrayList<ParamList> allParamLists = null;

            // Determine the self player type
            final int selfFlag;
            if (args[1].equals("E")) { // use an Ensemble player
                selfFlag = GATuner.SELF_IS_ENSEMBLE_PLAYER;
                System.out.println("Self: Ensemble player");
            }
            else if (args[1].equals("I")) { // use an Index player
                selfFlag = GATuner.SELF_IS_INDEX_PLAYER;
                System.out.println("Self: Index player");
            }
            else
                throw new RuntimeException("Unrecognized second command-line parameter: " + args[1]);

            int[] paramIDs = null;

            // Determine the experiment to run, and setup the ParamLists accordingly.
            if (args[0].equals("1")) {
                allParamLists = makeParamListsForMeldabilityHE(numOfEvaluators, selfFlag);
                paramIDs = new int[] {ParamList.MC_SELF_LOW_OBTAINABILITY, ParamList.MC_SELF_RATIO_FOR_UNKNOWN, ParamList.MC_SELF_WRANK, ParamList.MC_SELF_WRUN};
            }
            else if (args[0].equals("2")) {
                allParamLists = makeParamListsForConvHE(numOfEvaluators, selfFlag);
                paramIDs = new int[] {ParamList.CH_SAMERANK, ParamList.CH_ONEAWAY, ParamList.CH_TWOAWAY};
            }
            else if (args[0].equals("3")) {
                allParamLists = makeParamListsForLinearDeadwoodPenaltyHE(numOfEvaluators, selfFlag);
                paramIDs = new int[] {ParamList.LD_PENALTY_SLOPE, ParamList.LD_PENALTY_EXPONENT};
            }
            else if (args[0].equals("4")) {
                allParamLists = makeParamListsForOppCardsKnownDeadwoodPenaltyHE(numOfEvaluators, selfFlag);
                paramIDs = new int[] {ParamList.OD_PENALTY_SLOPE, ParamList.OD_PENALTY_EXPONENT};
            }
            else if (args[0].equals("5")) {
                allParamLists = makeParamListsForMultiOppHE(numOfEvaluators, selfFlag);
                paramIDs = new int[] {ParamList.OM_NUM_OF_ADDITIONAL_CARDS, ParamList.MC_OPP_LOW_OBTAINABILITY, ParamList.MC_OPP_RATIO_FOR_UNKNOWN, ParamList.MC_OPP_WRANK, ParamList.MC_OPP_WRUN};
            }
            else if (args[0].equals("6")) {
                allParamLists = makeParamListsForSixEnsembleWeights(numOfEvaluators, selfFlag);
                paramIDs = new int[numOfEvaluators];
                for(int i = 0; i < paramIDs.length; i++)
                    paramIDs[i] = ParamList.NUM_NONENSEMBLE_PARAMS + i;
            }
            else if (args[0].equals("7")) {
                allParamLists = makeParamListsForSevenEnsembleWeights(numOfEvaluators, selfFlag);
                paramIDs = new int[numOfEvaluators];
                for(int i = 0; i < paramIDs.length; i++)
                    paramIDs[i] = ParamList.NUM_NONENSEMBLE_PARAMS + i;
            }
            else if (args[0].equals("98")) { // Run a single player test on base parameters, multiple times, checking for standard deviation of win rate
                runStdDevCheckExperiment(selfFlag, numOfEvaluators);
                System.exit(0);
            }
            else if (args[0].equals("99")) { // Run a single player test on the base parameters
                ParamList params = new ParamList(getBaseParamsAndWeights(selfFlag), numOfEvaluators);
                double winRate = runSinglePlayerTest(selfFlag, params, gamesPerIndividual);
                System.out.println("winRate: " + winRate);
                System.exit(0);
            }
            else
                throw new RuntimeException("Unrecognized first command-line parameter: " + args[0]);
            
            // Run the experiment
            int globalStartingIndex;
            if (args.length == 3)
                globalStartingIndex = Integer.parseInt(args[2]); // start at the ParamList ID indicated as the third argument - this is an experiment that was killed for some reason
            else
                globalStartingIndex = 0; // start at ParamList 0 - this is a new experiment, not a broken interrupted one

            double[] winRates = runExhaustiveTest(selfFlag, globalStartingIndex, allParamLists, gamesPerIndividual, paramIDs);
            System.out.println("All tests complete. Results follow.");
            displayResults(allParamLists, paramIDs, winRates);
        }
        else {
            throw new RuntimeException("You must provide command-line arguments.");
            /*
            System.out.println("NOT using command-line arguments.");
            ArrayList<ParamList> allParamLists = null;

            // Uncomment one of the following
            // allParamLists = makeParamListsForMeldabilityHE(numOfEvaluators);
            // allParamLists = makeParamListsForConvHE(numOfEvaluators);
            // allParamLists = makeParamListsForLinearDeadwoodPenaltyHE(numOfEvaluators);
            // allParamLists = makeParamListsForOppCardsKnownDeadwoodPenaltyHE(numOfEvaluators);
            // allParamLists = makeParamListsForMultiOppHE(numOfEvaluators);
            // allParamLists = makeParamListsForSixEnsembleWeights(numOfEvaluators);
            
            util.OurUtil.assertCondition(allParamLists != null, "Make sure to either uncomment the allParamLists initializer you want to use, or provide a command-line argument.");

            runExhaustiveTest(allParamLists, numOfEvaluators, gamesPerIndividual);
            */
        }

    }

}
