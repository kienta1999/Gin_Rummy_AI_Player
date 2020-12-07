package experiment;

import java.util.ArrayList;
import java.util.Arrays;

import ga.GATuner;
import games.TestingGame;
import players.ParamList;
import players.SimpleFakeGinRummyPlayer;
import players.handeval.DeadwoodDrawDecider;
import players.handeval.DeadwoodHandEvaluator;
import players.handeval.EnsembleHandEvalPlayer;
import players.handeval.MeldOnlyDrawDecider;
import players.handeval.OneStageKnockDecider;
import players.handeval.ScorePredictionKnockDecider;
import players.handeval.TwoStageDrawDecider;
import players.handeval.TwoStageKnockDecider;

public class ExhaustiveExperiments {

    private static int genCount = 0;
    private static java.util.concurrent.atomic.AtomicInteger indivCount = new java.util.concurrent.atomic.AtomicInteger(0);

    protected static class Thresholds{
        public int threshold0;
        public int threshold1;
        public int thresholdEarly;
        public int thresholdLate;
        public int thresholdMiddle;
        public Thresholds(int t0, int t1){
            threshold0 = t0;
            threshold1 = t1;
        }
        public Thresholds(int tE, int tL, int tM){
            thresholdEarly = tE;
            thresholdLate = tL;
            thresholdMiddle = tM;
        }
    }

    public static double[] OneStageVSOneStage(int numGamesPerCase){
        int numTotalCases = 121;
        // Thresholds[] thresholds = new Thresholds[numTotalCases]; 
        // for(int i = 0; i <= 10; i++){
        //     for(int j = 0; j <= 10; j++){
        //         thresholds[i+j] = new Thresholds(i, j); 
        //     }
        // }
        ArrayList<Thresholds> thresholds = new ArrayList<Thresholds>();
        for(int i = 0; i <= 10; i++){
            for(int j = 0; j <= 10; j++){
                thresholds.add(new Thresholds(i,j));
            }
        }
        double[] winningRatios = new double[numTotalCases];
        winningRatios = thresholds.stream().parallel().map((x) -> {return playManyGamesOneStage(x, numGamesPerCase);}).mapToDouble(x -> {return x;}).toArray();
        
        System.out.println();
        System.out.print("  O ");
        for(int i = 0; i < 11; i++) System.out.print("  " + i + "   ");
        
        for(int i = 0; i < (11+1); i++){
            if(i == 0)
                System.out.println("\nS");
            else{
                for(int j = 0; j < (11 + 1); j++){
                    if(j == 0)
                        System.out.print((i-1) + "   ");
                    else
                        System.out.printf("%.3f ", winningRatios[(i-1)*11 + (j-1)]);
                }
                System.out.println();
            }
        }

        int bestIndex = findBestWinIndex(winningRatios);
        System.out.println("\nBest winning Ratio is " + winningRatios[bestIndex] + " when self uses: " + bestIndex/11 + ", opp uses: " + bestIndex%11);
        return winningRatios;
    }

    public static double playManyGamesOneStage(Thresholds thresholds, int gamesPerIndividual){
        ParamList params0 = new ParamList(new double[]{1});
        params0.set(ParamList.OS_KNOCK_THRESHOLD, thresholds.threshold0);
        EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(params0, new DeadwoodHandEvaluator());
        p0.setDrawDecider(new MeldOnlyDrawDecider());
        p0.setKnockDecider(new OneStageKnockDecider(params0));

        ParamList params1 = new ParamList(new double[]{1});
        params1.set(ParamList.OS_KNOCK_THRESHOLD, thresholds.threshold1);
        EnsembleHandEvalPlayer p1 = new EnsembleHandEvalPlayer(params1, new DeadwoodHandEvaluator());
        p1.setDrawDecider(new MeldOnlyDrawDecider());
        p1.setKnockDecider(new OneStageKnockDecider(params1));

        int p0Wins;

        TestingGame gameManager = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(false);
        p0Wins = 0;
        params0.enforceRestrictions(); 
        params1.enforceRestrictions();

        // System.out.print("(" + genCount + ":" + indivCount.incrementAndGet() + ")");
        System.out.print("(" + thresholds.threshold0 + ":" + thresholds.threshold1 + ")");
        for(int j = 0; j < gamesPerIndividual; j++){
            int winner = gameManager.play();
            if(winner == 0)
                p0Wins += 1; 
        }
        return (double)p0Wins / gamesPerIndividual;
    }

    public static double[] TwoStageVSOneStage(int numGamesPerCase, int osThreshold){
        int numTotalCases = 55 * 15;
        System.out.println("ExhaustiveSearch TwoStage: " + numTotalCases);

        ArrayList<Thresholds> thresholds = new ArrayList<>();
        for(int m = 5; m < 20; m++){
            for(int e = 1; e <= 10; e++){
                for(int l = 0; l < e; l++){
                    thresholds.add(new Thresholds(e, l, m));
                }
            }
        }

        double[] winningRatios = new double[numTotalCases];
        winningRatios = thresholds.stream().parallel().map((x) -> {return playManyGamesTwoStage(x, numGamesPerCase, osThreshold);}).mapToDouble(x -> {return x;}).toArray();
        
        int bestIndex = findBestWinIndex(winningRatios);
        int[] bestIndices = computeELMfromIndex(bestIndex);
        System.out.println();
        displayTwoStageResults(winningRatios);    
        System.out.println("\nBest winning Ratio is " + winningRatios[bestIndex] + " when (e, l, m) = (" + bestIndices[0] + ", " + bestIndices[1] + ", " + bestIndices[2] + ")");
        return winningRatios;
    }

    public static double playManyGamesTwoStage(Thresholds thresholds, int gamesPerIndividual, int osThreshold){
        ParamList params0 = new ParamList(new double[]{1});
        params0.set(ParamList.TS_KNOCK_THRESHOLD_EARLY, thresholds.thresholdEarly);
        params0.set(ParamList.TS_KNOCK_THRESHOLD_LATE, thresholds.thresholdLate);
        params0.set(ParamList.TS_KNOCK_MIDDLE, thresholds.thresholdMiddle);
        EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(params0, new DeadwoodHandEvaluator());
        p0.setDrawDecider(new MeldOnlyDrawDecider());
        p0.setKnockDecider(new TwoStageKnockDecider(params0));

        ParamList params1 = new ParamList(new double[]{1});
        params1.set(ParamList.OS_KNOCK_THRESHOLD, osThreshold);
        EnsembleHandEvalPlayer p1 = new EnsembleHandEvalPlayer(params1, new DeadwoodHandEvaluator());
        p1.setDrawDecider(new MeldOnlyDrawDecider());
        p1.setKnockDecider(new OneStageKnockDecider(params1));

        int p0Wins;

        TestingGame gameManager = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(false);
        p0Wins = 0;
        params0.enforceRestrictions(); 
        params1.enforceRestrictions();

        System.out.print("(" + indivCount.incrementAndGet() + ")");
        // System.out.print("(" + thresholds.thresholdEarly + ":" + thresholds.thresholdLate + ":" + thresholds.thresholdMiddle+ ")");
        for(int j = 0; j < gamesPerIndividual; j++){
            int winner = gameManager.play();
            if(winner == 0)
                p0Wins += 1; 
        }
        return (double)p0Wins / gamesPerIndividual;
    }

    public static int[] computeELMfromIndex(int index){
        int m = (index+1) / 55 + 5;
        int k = (index+1) % 55;
        double temp = (-1 + Math.sqrt(1 + 8*k))/2;
        int e = temp % 1 == 0 ? (int)temp : (int)temp + 1;
        int l = k - (e-1)*(e)/2-1;
        int[] indices = new int[]{e, l, m};
        return indices;
    }

    public static int findBestWinIndex(double[] winningRatios){
        int index = 0;
        for(int i = 1; i < winningRatios.length; i++){
            if(winningRatios[index] < winningRatios[i])
                index = i;
        }
        return index;
    }

    public static void displayTwoStageResults(double[] winningRates){
        for(int m = 0; m < 15; m++){
            System.out.println("---------- Middle is " + (m+5) + "---------"); 
            System.out.print("  L ");
            for(int k = 0; k < 10; k++) System.out.print("  " + k + "   ");
            for(int e = 0; e < (10 + 1); e++){
                if(e == 0)
                    System.out.println("\nE");
                else{
                    for(int l = -1; l < e; l++){
                        if(l == -1){
                            System.out.print(e + "   ");
                        }
                        else{
                            int index = m*55 + e*(e-1)/2 + l;
                            System.out.printf("%.3f ", winningRates[index]);
                        }
                    }
                    System.out.println();
                }
            }
            System.out.println();
        }
    }

    public static void testDisplay(){
        int numTotalCases = 55 * 15;
        ArrayList<Thresholds> thresholds = new ArrayList<Thresholds>();
        for(int m = 5; m < 20; m++){
            for(int e = 1; e <= 10; e++){
                for(int l = 0; l < e; l++){
                    thresholds.add(new Thresholds(e, l, m));
                }
            }
        }
        double[] winningRates = new double[numTotalCases];
        for(int i = 0; i < numTotalCases; i++){
            Thresholds t = thresholds.get(i);
            double val = t.thresholdEarly * 0.1 + t.thresholdLate * 0.01 + t.thresholdMiddle * 0.001;
            winningRates[i] = val;
        }
        displayTwoStageResults(winningRates);        
    }

    public static double[] TwoStageVSScorePrediction(int numGamesPerCase){
        ArrayList<Double> numAdditionalCards = new ArrayList<Double>();
        for(int i = 0; i <= 7; i++){
            numAdditionalCards.add((double)i);
        }
        double [] winningRates = new double[11];
        winningRates = numAdditionalCards.stream().parallel().map((x) -> {return playManyGamesSP(x, numGamesPerCase);}).mapToDouble(x -> {return x;}).toArray();

        int bestIndex = findBestWinIndex(winningRates);
        System.out.println("The best win rate is: " + winningRates[bestIndex] + " when numAdditionalCards is: " + (bestIndex));
        return winningRates;

    }

    public static double playManyGamesSP(double numAdditionalCards, int gamesPerIndividual){
        ParamList params0 = new ParamList(new double[]{1});
        params0.set(ParamList.SP_NUM_OF_ADDITIONAL_CARDS, numAdditionalCards);
        EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(params0, new DeadwoodHandEvaluator());
        p0.setDrawDecider(new MeldOnlyDrawDecider());
        p0.setKnockDecider(new ScorePredictionKnockDecider(params0));

        ParamList params1 = new ParamList(new double[]{1});
        EnsembleHandEvalPlayer p1 = new EnsembleHandEvalPlayer(params1, new DeadwoodHandEvaluator());
        p1.setDrawDecider(new MeldOnlyDrawDecider());
        p1.setKnockDecider(new TwoStageKnockDecider(params1));

        int p0Wins;

        TestingGame gameManager = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(false);
        p0Wins = 0;
        params0.enforceRestrictions(); 
        params1.enforceRestrictions();

        System.out.print("(" + indivCount.incrementAndGet() + ")");
        // System.out.print("(" + thresholds.thresholdEarly + ":" + thresholds.thresholdLate + ":" + thresholds.thresholdMiddle+ ")");
        for(int j = 0; j < gamesPerIndividual; j++){
            int winner = gameManager.play();
            if(winner == 0)
                p0Wins += 1; 
        }
        return (double)p0Wins / gamesPerIndividual;
    }

    public static double[] searchTwoStageDrawDecider(int numGamesPerCase, int oppDrawDeciderFlag){
        ArrayList<Double> middle = new ArrayList<Double>();
        for(int i = 0; i < 26; i++){
            middle.add((double)i);
        }
        double [] winningRates = new double[15];
        winningRates = middle.stream().parallel().map((x) -> {return playManyGamesDD(x, numGamesPerCase, oppDrawDeciderFlag);}).mapToDouble(x -> {return x;}).toArray();

        int bestIndex = findBestWinIndex(winningRates);
        System.out.println("The best win rate is: " + winningRates[bestIndex] + " when middle is: " + (bestIndex));
        return winningRates;
    }

    public static double playManyGamesDD(double middle, int gamesPerIndividual, int oppDrawDeciderFlag){
        ParamList params0 = new ParamList(new double[]{1});
        params0.set(ParamList.TS_DRAW_MIDDLE, middle);
        EnsembleHandEvalPlayer p0 = new EnsembleHandEvalPlayer(params0, new DeadwoodHandEvaluator());
        p0.setDrawDecider(new TwoStageDrawDecider(params0));
        p0.setKnockDecider(new TwoStageKnockDecider(params0)); // 10, 0, 6

        ParamList params1 = new ParamList(new double[]{1});
        EnsembleHandEvalPlayer p1 = new EnsembleHandEvalPlayer(params1, new DeadwoodHandEvaluator());
        p1.setKnockDecider(new TwoStageKnockDecider(params1)); // 10, 0, 6

        if(oppDrawDeciderFlag == GATuner.OPP_DEADWOOD_DRAW_DECIDER){
            p1.setDrawDecider(new DeadwoodDrawDecider());
        }
        else if(oppDrawDeciderFlag == GATuner.OPP_MELD_ONLY_DRAW_DECIDER){
            p1.setDrawDecider(new MeldOnlyDrawDecider());
        }
        else{
            throw new RuntimeException("DrawDecider flag for opp not available");
        }

        int p0Wins;

        TestingGame gameManager = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(false);
        p0Wins = 0;
        params0.enforceRestrictions(); 
        params1.enforceRestrictions();

        System.out.print("(" + indivCount.incrementAndGet() + ")");
        // System.out.print("(" + thresholds.thresholdEarly + ":" + thresholds.thresholdLate + ":" + thresholds.thresholdMiddle+ ")");
        for(int j = 0; j < gamesPerIndividual; j++){
            int winner = gameManager.play();
            if(winner == 0)
                p0Wins += 1; 
        }
        return (double)p0Wins / gamesPerIndividual;
    }

    public static void main(String[] args){
        int numGamesPerCase = 5;
        // OneStageVSOneStage(numGamesPerCase);

        // int osThreshold = 0;
        // double[] winningRates = TwoStageVSOneStage(numGamesPerCase, osThreshold);
        // System.out.println(Arrays.toString(winningRates));

        double[] winningRates = new double[]{0.4865, 0.492, 0.4475, 0.5165, 0.4575, 0.3915, 0.508, 0.465, 0.422, 0.384, 0.505, 0.463, 0.42, 0.4125, 0.347, 0.496, 0.463, 0.4095, 0.4005, 0.3425, 0.3425, 0.5225, 0.453, 0.435, 0.424, 0.3705, 0.3355, 0.3365, 0.532, 0.4875, 0.447, 0.4325, 0.4035, 0.338, 0.342, 0.3215, 0.5275, 0.488, 0.4655, 0.4515, 0.3985, 0.345, 0.3545, 0.346, 0.3095, 0.552, 0.513, 0.4545, 0.457, 0.4225, 0.374, 0.367, 0.331, 0.321, 0.319, 0.479, 0.498, 0.4475, 0.5065, 0.4605, 0.426, 0.502, 0.451, 0.403, 0.403, 0.5005, 0.4735, 0.406, 0.401, 0.3565, 0.5215, 0.4505, 0.431, 0.39, 0.373, 0.3295, 0.508, 0.4915, 0.4415, 0.4395, 0.4, 0.3475, 0.3255, 0.5435, 0.4985, 0.45, 0.429, 0.394, 0.339, 0.333, 0.319, 0.543, 0.5265, 0.474, 0.4495, 0.412, 0.3725, 0.358, 0.3385, 0.3145, 0.573, 0.5285, 0.5035, 0.48, 0.4275, 0.39, 0.3745, 0.339, 0.3255, 0.32, 0.4995, 0.5015, 0.47, 0.5125, 0.4545, 0.405, 0.488, 0.4505, 0.4155, 0.407, 0.4925, 0.451, 0.4245, 0.418, 0.356, 0.518, 0.465, 0.4355, 0.4145, 0.3605, 0.299, 0.5225, 0.4705, 0.457, 0.4615, 0.393, 0.3395, 0.3325, 0.523, 0.522, 0.454, 0.4425, 0.4085, 0.3555, 0.3505, 0.3275, 0.5395, 0.504, 0.479, 0.485, 0.4115, 0.3805, 0.358, 0.3165, 0.3315, 0.555, 0.536, 0.5015, 0.479, 0.4305, 0.4005, 0.3685, 0.365, 0.323, 0.3325, 0.493, 0.509, 0.4315, 0.4895, 0.4605, 0.402, 0.495, 0.4495, 0.41, 0.401, 0.4895, 0.4555, 0.425, 0.406, 0.361, 0.5235, 0.462, 0.4575, 0.4455, 0.372, 0.3365, 0.53, 0.4795, 0.4525, 0.432, 0.3775, 0.346, 0.3175, 0.5445, 0.51, 0.4555, 0.4515, 0.4105, 0.3695, 0.3455, 0.3415, 0.5325, 0.499, 0.477, 0.442, 0.4235, 0.3665, 0.36, 0.318, 0.2965, 0.549, 0.501, 0.4755, 0.4695, 0.456, 0.3745, 0.3875, 0.363, 0.36, 0.3355, 0.484, 0.4855, 0.431, 0.468, 0.429, 0.4165, 0.4775, 0.46, 0.404, 0.41, 0.4755, 0.445, 0.402, 0.4245, 0.356, 0.497, 0.4395, 0.4315, 0.4315, 0.3795, 0.3135, 0.504, 0.441, 0.4335, 0.4145, 0.385, 0.3315, 0.322, 0.5045, 0.46, 0.439, 0.451, 0.3795, 0.3495, 0.354, 0.3395, 0.5145, 0.484, 0.448, 0.4195, 0.385, 0.385, 0.3435, 0.3535, 0.3095, 0.5035, 0.483, 0.449, 0.4315, 0.397, 0.387, 0.376, 0.347, 0.3095, 0.329, 0.4895, 0.464, 0.438, 0.461, 0.45, 0.4075, 0.463, 0.435, 0.4125, 0.399, 0.4755, 0.4365, 0.379, 0.3905, 0.346, 0.488, 0.4395, 0.413, 0.406, 0.3605, 0.3185, 0.4555, 0.4225, 0.427, 0.395, 0.376, 0.345, 0.323, 0.4625, 0.4255, 0.3985, 0.406, 0.367, 0.3685, 0.329, 0.3145, 0.4605, 0.457, 0.425, 0.4215, 0.3905, 0.3705, 0.3395, 0.335, 0.3205, 0.47, 0.4525, 0.4255, 0.426, 0.3855, 0.3525, 0.364, 0.3445, 0.327, 0.322, 0.4805, 0.4745, 0.436, 0.4885, 0.405, 0.4235, 0.4515, 0.423, 0.395, 0.407, 0.4375, 0.4455, 0.3845, 0.378, 0.349, 0.4415, 0.437, 0.4075, 0.386, 0.37, 0.3185, 0.424, 0.4045, 0.3895, 0.3715, 0.359, 0.3015, 0.3195, 0.4165, 0.416, 0.4, 0.3955, 0.362, 0.323, 0.34, 0.319, 0.4195, 0.421, 0.3795, 0.3765, 0.37, 0.342, 0.352, 0.3365, 0.308, 0.405, 0.392, 0.389, 0.39, 0.347, 0.3415, 0.3455, 0.3265, 0.3175, 0.3265, 0.468, 0.4615, 0.419, 0.4425, 0.4215, 0.368, 0.428, 0.3845, 0.3785, 0.3925, 0.409, 0.373, 0.3535, 0.352, 0.345, 0.4135, 0.3965, 0.375, 0.3765, 0.349, 0.318, 0.391, 0.376, 0.377, 0.3385, 0.339, 0.3305, 0.3335, 0.3775, 0.3595, 0.336, 0.361, 0.3605, 0.3225, 0.305, 0.3145, 0.3835, 0.387, 0.346, 0.3505, 0.3345, 0.324, 0.3245, 0.303, 0.2885, 0.3915, 0.362, 0.3585, 0.342, 0.3355, 0.337, 0.334, 0.339, 0.303, 0.3165, 0.461, 0.443, 0.4205, 0.427, 0.419, 0.4015, 0.4185, 0.395, 0.399, 0.3655, 0.3725, 0.393, 0.343, 0.3345, 0.324, 0.367, 0.3705, 0.3465, 0.3345, 0.3415, 0.325, 0.354, 0.3525, 0.325, 0.3405, 0.3365, 0.3195, 0.323, 0.331, 0.366, 0.321, 0.3375, 0.314, 0.3075, 0.3235, 0.31, 0.353, 0.342, 0.334, 0.343, 0.3125, 0.315, 0.311, 0.3, 0.299, 0.3435, 0.3245, 0.3365, 0.329, 0.3395, 0.3295, 0.315, 0.3105, 0.326, 0.309, 0.4505, 0.4335, 0.4175, 0.4045, 0.4205, 0.3845, 0.3755, 0.3675, 0.366, 0.356, 0.36, 0.3405, 0.341, 0.3455, 0.335, 0.33, 0.3415, 0.3535, 0.3265, 0.3345, 0.334, 0.332, 0.33, 0.3325, 0.33, 0.3275, 0.299, 0.291, 0.336, 0.302, 0.3345, 0.3305, 0.3015, 0.309, 0.294, 0.3055, 0.3215, 0.3265, 0.3245, 0.3115, 0.321, 0.3245, 0.296, 0.324, 0.3035, 0.3395, 0.3435, 0.328, 0.304, 0.3135, 0.31, 0.3195, 0.316, 0.313, 0.321, 0.4375, 0.427, 0.415, 0.41, 0.4285, 0.393, 0.3745, 0.3615, 0.365, 0.3585, 0.31, 0.3475, 0.331, 0.3225, 0.3125, 0.323, 0.3205, 0.324, 0.3265, 0.329, 0.3155, 0.3095, 0.2975, 0.2925, 0.308, 0.3155, 0.3045, 0.332, 0.318, 0.3285, 0.329, 0.32, 0.2955, 0.318, 0.285, 0.2975, 0.327, 0.325, 0.316, 0.3125, 0.3095, 0.3025, 0.3145, 0.311, 0.317, 0.3235, 0.337, 0.315, 0.332, 0.326, 0.322, 0.312, 0.33, 0.305, 0.304, 0.4505, 0.3835, 0.4285, 0.3955, 0.4025, 0.4035, 0.379, 0.3745, 0.348, 0.3455, 0.305, 0.306, 0.3215, 0.3105, 0.318, 0.307, 0.316, 0.3165, 0.3085, 0.3195, 0.3365, 0.3095, 0.319, 0.307, 0.3265, 0.3315, 0.3, 0.313, 0.295, 0.318, 0.3115, 0.2985, 0.288, 0.3185, 0.3125, 0.304, 0.3145, 0.2995, 0.3055, 0.299, 0.307, 0.2935, 0.3155, 0.318, 0.2955, 0.3175, 0.3215, 0.29, 0.305, 0.3135, 0.321, 0.3365, 0.3, 0.3, 0.3045, 0.429, 0.4, 0.409, 0.4035, 0.3965, 0.401, 0.356, 0.3535, 0.3495, 0.361, 0.3115, 0.319, 0.3155, 0.3205, 0.298, 0.333, 0.32, 0.3045, 0.3255, 0.3255, 0.3065, 0.315, 0.2965, 0.3035, 0.302, 0.3095, 0.2995, 0.313, 0.3015, 0.291, 0.2955, 0.315, 0.309, 0.3115, 0.308, 0.3025, 0.314, 0.3225, 0.312, 0.3085, 0.318, 0.3205, 0.324, 0.303, 0.3045, 0.315, 0.317, 0.3075, 0.3115, 0.3295, 0.2965, 0.3195, 0.303, 0.3235, 0.324, 0.4365, 0.4025, 0.409, 0.392, 0.3975, 0.4105, 0.342, 0.359, 0.357, 0.3435, 0.3205, 0.326, 0.3225, 0.312, 0.3075, 0.3205, 0.3225, 0.3055, 0.32, 0.294, 0.3065, 0.289, 0.292, 0.305, 0.2965, 0.3095, 0.3065, 0.3125, 0.3075, 0.318, 0.305, 0.289, 0.295, 0.298, 0.287, 0.3105, 0.315, 0.299, 0.295, 0.312, 0.294, 0.312, 0.306, 0.3275, 0.321, 0.287, 0.3125, 0.3195, 0.3005, 0.3255, 0.326, 0.2945, 0.292, 0.32, 0.3055, 0.4465, 0.397, 0.4245, 0.432, 0.397, 0.3945, 0.3715, 0.3745, 0.357, 0.352, 0.3165, 0.2935, 0.331, 0.311, 0.316, 0.3255, 0.33, 0.327, 0.3, 0.3355, 0.2995, 0.318, 0.292, 0.325, 0.309, 0.3075, 0.297, 0.304, 0.29, 0.289, 0.301, 0.294, 0.295, 0.3065, 0.299, 0.3085, 0.302, 0.3145, 0.3115, 0.29, 0.2935, 0.292, 0.285, 0.2955, 0.2965, 0.316, 0.3125, 0.311, 0.3125, 0.3195, 0.294, 0.3035, 0.311, 0.304, 0.308};
        // displayTwoStageResults(winningRates);

        // testDisplay();

        // int oppDrawDeciderFlag = GATuner.OPP_DEADWOOD_DRAW_DECIDER;
        // searchTwoStageDrawDecider(numGamesPerCase, oppDrawDeciderFlag);

        TwoStageVSScorePrediction(numGamesPerCase);
    }


    
}