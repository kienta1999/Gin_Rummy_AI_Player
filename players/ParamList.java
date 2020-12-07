package players;

import java.util.Random;


// import ga.GATuner;
// import games.TestingGame;
// import players.handeval.AceTwoBonusHandEvaluator;
// import players.handeval.ConvHandEvaluator;
// import players.handeval.DeadwoodDrawDecider;
// import players.handeval.DeadwoodHandEvaluator;
// import players.handeval.EnsembleHandEvalPlayer;
// import players.handeval.LinearDeadwoodPenaltyHandEvaluator;
// import players.handeval.MeldabilityHandEvaluator;
// import players.handeval.OppCardsKnownDeadwoodPenaltyHandEvaluator;
// import players.handeval.TwoStageKnockDecider;

import java.io.Serializable;
import java.util.ArrayList;
// import java.util.Arrays;


/**
 * @author Steven Bogaerts
 */
public class ParamList implements Serializable {

    private static final long serialVersionUID = 1L;

    public static boolean TESTING = false;

    private ArrayList<Double> paramAL;

    private static final ArrayList<Double> defaultParamAL = getDefaultParamAL();

    private static ArrayList<Double> fixedValues = new ArrayList<>();

    public static boolean SHOULD_CHECK_ORDER = true;

    /////////////////////////////////////////////////////////////////////
    // Non-ensemble-related hyperparameters

    // ConvHandEvaluator hyperparameters
    public static final int CH_SAMERANK = 0;
    public static final int CH_ONEAWAY = 1;
    public static final int CH_TWOAWAY = 2;

    // MeldabilityCalculator hyperparameters
    public static final int MC_SELF_LOW_OBTAINABILITY = 3;
    public static final int MC_SELF_RATIO_FOR_UNKNOWN = 4;
    public static final int MC_SELF_WRANK = 5;
    public static final int MC_SELF_WRUN = 6;
    public static final int MC_OPP_LOW_OBTAINABILITY = 7;
    public static final int MC_OPP_RATIO_FOR_UNKNOWN = 8;
    public static final int MC_OPP_WRANK = 9;
    public static final int MC_OPP_WRUN = 10;

    // StateTracker hyperparameters
    public static final int ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY = 11;
    public static final int ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY = 12;
    public static final int ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK = 13;

    public static final int ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY = 14;
    public static final int ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY = 15;
    public static final int ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK = 16;

    public static final int ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY = 17;
    public static final int ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY = 18;
    public static final int ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK = 19;

    // Used to find out at what number parameters for StateTracker start and end
    public static final int ST_START = ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY;
    public static final int ST_END = ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK;

    //ScorePrediction hyyperparametrs
    public static final int SP_NUM_OF_ADDITIONAL_CARDS = 20; 
    
    //MultiOppHandMeldabilityEvaluator
    public static final int OM_NUM_OF_ADDITIONAL_CARDS = 21;

    //LinearDeadWoodPeanltyHandEvaluator
    public static final int LD_PENALTY_SLOPE = 22;
    public static final int LD_PENALTY_EXPONENT = 23;

    //OppCardsKnownDeadwoodPenaltyHandEvaluator
    public static final int OD_PENALTY_SLOPE = 24;
    public static final int OD_PENALTY_EXPONENT = 25;

    //TwoStagedKnockDecider
    public static final int TS_KNOCK_THRESHOLD_EARLY = 26;
    public static final int TS_KNOCK_THRESHOLD_LATE = 27;
    public static final int TS_KNOCK_MIDDLE = 28;

    //OneStagedKnockDecider
    public static final int OS_KNOCK_THRESHOLD = 29;

    //TwoStageDrawDecider
    public static final int TS_DRAW_MIDDLE = 30;

    public static final int NUM_NONENSEMBLE_PARAMS = 31;

    /////////////////////////////////////////////////////////////////////
    // Ensemble weights

    private static final int ENSEMBLE_STARTING_ID = NUM_NONENSEMBLE_PARAMS;

    private int numEnsembleWeights;

    /*
    setUpSelf: 
    p31: MeldabilityHandEvaluator 
    p32: DeadwoodHandEvaluator 
    p33: AceTwoBonusHandEvaluator
    p34: ConvHandEvaluator 
    //MultiOppHandMeldabilityEvaluator
    p35: linearHe
    p36: oppCardsHe
    */
    /////////////////////////////////////////////////////////////////////

    // public static final ArrayList<String> PARAM_NAMES = new ArrayList<String>(Arrays.asList("CH_SAMERANK", "CH_ONEAWAY", "CH_TWOAWAY", "MC_SELF_LOW_OBTAINABILITY", "MC_SELF_RATIO_FOR_UNKNOWN", "MC_SELF_WRANK", "MC_SELF_WRUN", "ST_PARAMETER", "ST_SMALLPARAMETER", "ST_SMALLESTPARAMETER", "SP_NUM_OF_ADDITIONAL_CARDS", "LD_PENALTY_SCALE", "OD_PENALTY_SCALE", "weight1", "weight2", "weight3"));

    /**
     * copy constructor
     */
    static{
        for(int i = 0; i < NUM_NONENSEMBLE_PARAMS + 10; i++){
            fixedValues.add(-1.0);
        }
    }

    public static void setFixedValue(int id, double value){
        fixedValues.set(id, value);
    }

    public ParamList(ParamList other){
        ArrayList<Double> copiedParamsAL = new ArrayList<>();
        double[] ensembleWeights = new double[other.numEnsembleWeights];
        for(int i = 0; i < NUM_NONENSEMBLE_PARAMS; i++){
            copiedParamsAL.add(other.get(i));
        }
        for(int i = 0; i < ensembleWeights.length; i++){
            ensembleWeights[i] = other.getEnsembleWeight(i);
        }

        setUpParamList(copiedParamsAL, ensembleWeights);
    }

    /**
     * This *must* remain private. For outside use, call the version below with double[], 
     * passing it the double array of weights.
     */
    private ParamList(int numEnsembleWeights) {
        double[] ensembleWeights = new double[numEnsembleWeights];
        for(int i = 0; i < ensembleWeights.length; i++)
            ensembleWeights[i] = 0.0;

        setUpParamList(defaultParamAL, ensembleWeights);
    }

    /**
     * If you want a ParamList with no ensemble members, call this constructor and pass
     * an empty array.    new double[] {}
     * @param ensembleWeights
     */
    public ParamList(double[] ensembleWeights) {
        setUpParamList(defaultParamAL, ensembleWeights);
    }

    /**
     * If you have an array of all params (included ensemble weights), it can be passed here.
     */
    public ParamList(double[] paramsAndWeights, int numEnsembleWeights){
        ArrayList<Double> newParamAL = new ArrayList<>();
        int lengthOfParamAL = paramsAndWeights.length - numEnsembleWeights;
        for(int i = 0; i < lengthOfParamAL; i++){
            newParamAL.add(paramsAndWeights[i]);
        }
        double[] ensembleWeights = new double[numEnsembleWeights];
        for(int i = 0; i < numEnsembleWeights; i++){
            ensembleWeights[i] = paramsAndWeights[i + lengthOfParamAL];
        }
        setUpParamList(newParamAL, ensembleWeights);
    }

    public ParamList(ArrayList<Double> paramAL, double[] ensembleWeights) {
        setUpParamList(paramAL, ensembleWeights);
    }

    private void setUpParamList(ArrayList<Double> newParamAL, double[] ensembleWeights) {
        this.paramAL = new ArrayList<Double>(newParamAL);
        this.numEnsembleWeights = ensembleWeights.length;
        for(int i = 0; i < ensembleWeights.length; i++)
            this.paramAL.add(ensembleWeights[i]);
        enforceRestrictions();
    }

    // public static ParamList getRandomParamList(int numEnsembleWeights){
    //     Random r = new Random();

    //     double[] ensembleWeights = new double[numEnsembleWeights];
    //     for(int ensembleID = 0; ensembleID < numEnsembleWeights; ensembleID++)
    //         ensembleWeights[ensembleID] = r.nextDouble(); // individual.set(ensembleID + ENSEMBLE_STARTING_ID, r.nextDouble());

    //     ParamList individual = new ParamList(ensembleWeights);

    //     individual.set(CH_SAMERANK, r.nextDouble());
    //     individual.set(CH_ONEAWAY, r.nextDouble());
    //     individual.set(CH_TWOAWAY, r.nextDouble());
    //     individual.set(MC_SELF_LOW_OBTAINABILITY, r.nextDouble()/2);
    //     individual.set(MC_SELF_RATIO_FOR_UNKNOWN, r.nextDouble());
    //     individual.set(MC_SELF_WRANK, r.nextDouble());
    //     individual.set(MC_SELF_WRUN, r.nextDouble());
    //     individual.set(MC_OPP_LOW_OBTAINABILITY, r.nextDouble()/2);
    //     individual.set(MC_OPP_RATIO_FOR_UNKNOWN, r.nextDouble());
    //     individual.set(MC_OPP_WRANK, r.nextDouble());
    //     individual.set(MC_OPP_WRUN, r.nextDouble());
    //     individual.set(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY , r.nextDouble());
    //     individual.set(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, r.nextDouble());
    //     individual.set(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, r.nextDouble());
    //     individual.set(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, r.nextDouble());
    //     individual.set(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, r.nextDouble());
    //     individual.set(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, r.nextDouble());
    //     individual.set(ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, r.nextDouble());
    //     individual.set(ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, r.nextDouble());
    //     individual.set(ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, r.nextDouble()); 
    //     individual.set(SP_NUM_OF_ADDITIONAL_CARDS, r.nextInt(10));
    //     individual.set(OM_NUM_OF_ADDITIONAL_CARDS, r.nextInt(10));
    //     individual.set(LD_PENALTY_SLOPE, r.nextDouble());
    //     individual.set(LD_PENALTY_EXPONENT, r.nextDouble() + r.nextInt(4));
    //     individual.set(OD_PENALTY_SLOPE, r.nextDouble());
    //     individual.set(OD_PENALTY_EXPONENT, r.nextDouble() + r.nextInt(4));
    //     individual.set(TS_KNOCK_THRESHOLD_EARLY, r.nextInt(11));
    //     individual.set(TS_KNOCK_THRESHOLD_LATE, r.nextInt(11));
    //     individual.set(TS_KNOCK_MIDDLE, 5 + r.nextInt(15));
    //     individual.set(OS_KNOCK_THRESHOLD, r.nextInt(11));
        
    //     individual.enforceRestrictions();

    //     return individual;
    // }
    
    public static ParamList getRandomParamList(int numOfEvaluators){
        
        Random r = new Random();

        double[] ensembleWeights = new double[numOfEvaluators];
        for(int ensembleID = 0; ensembleID < numOfEvaluators; ensembleID++){
            if(fixedValues.get(ParamList.NUM_NONENSEMBLE_PARAMS + ensembleID)==-1){
                ensembleWeights[ensembleID] = r.nextDouble();
            }
            else{
                ensembleWeights[ensembleID] = fixedValues.get(ParamList.NUM_NONENSEMBLE_PARAMS + ensembleID);
            }
        }
        ParamList individual = new ParamList(ensembleWeights);
        double paramValue = -1; 
        for(int paramIndex = 0; paramIndex < ParamList.NUM_NONENSEMBLE_PARAMS; paramIndex++){
            switch(paramIndex){
                case ParamList.CH_SAMERANK: case ParamList.CH_ONEAWAY: case ParamList.CH_TWOAWAY: 
                case ParamList.MC_SELF_RATIO_FOR_UNKNOWN: case ParamList.MC_SELF_WRANK: case ParamList.MC_SELF_WRUN: 
                case ParamList.MC_OPP_RATIO_FOR_UNKNOWN: case ParamList.MC_OPP_WRANK: case ParamList.MC_OPP_WRUN: 
                case ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY: case ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY: case ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK: 
                case ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY: case ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY: case ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK: 
                case ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY: case ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY: case ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK: 
                case ParamList.LD_PENALTY_SLOPE: case ParamList.OD_PENALTY_SLOPE: 
                    if(fixedValues.get(paramIndex)==-1){
                        paramValue = r.nextDouble();
                    }
                    else{
                        paramValue = fixedValues.get(paramIndex);
                    }
                    break;
                case ParamList.MC_SELF_LOW_OBTAINABILITY: case ParamList.MC_OPP_LOW_OBTAINABILITY: 
                    if(fixedValues.get(paramIndex)==-1){
                        paramValue = (r.nextDouble()/2);
                    }
                    else{
                        paramValue = fixedValues.get(paramIndex);
                    }
                    break;
                case ParamList.SP_NUM_OF_ADDITIONAL_CARDS: case ParamList.OM_NUM_OF_ADDITIONAL_CARDS:
                    if(fixedValues.get(paramIndex)==-1){
                        paramValue =  r.nextInt(10);
                    }
                    else{
                        paramValue = fixedValues.get(paramIndex);
                    }
                    break;
                case ParamList.LD_PENALTY_EXPONENT: case ParamList.OD_PENALTY_EXPONENT:
                    if(fixedValues.get(paramIndex)==-1){
                        paramValue =  r.nextDouble() + r.nextInt(4);
                    }
                    else{
                        paramValue = fixedValues.get(paramIndex);
                    }
                    break;
                case ParamList.TS_KNOCK_THRESHOLD_EARLY: case ParamList.TS_KNOCK_THRESHOLD_LATE: case ParamList.OS_KNOCK_THRESHOLD:
                    if(fixedValues.get(paramIndex)==-1){
                        paramValue =  r.nextInt(11);
                    }
                    else{
                        paramValue = fixedValues.get(paramIndex);
                    }
                    break;
                case ParamList.TS_KNOCK_MIDDLE:
                case ParamList.TS_DRAW_MIDDLE:
                    if(fixedValues.get(paramIndex)==-1){
                        paramValue = 5 + r.nextInt(15);
                    }
                    else{
                        paramValue = fixedValues.get(paramIndex);
                    }
                    break;
                
            }
            if(paramValue != -1){
                individual.set(paramIndex, paramValue);
            }
            else
                System.out.println("Value was not set in the ParamList");
            
        }
        individual.enforceRestrictions();
        return individual;
    }
    public int getNumEnsembleWeights() {
        return numEnsembleWeights;
    }

    /**
     * Returns an ArrayList<Double> with "reasonable" values. 
     * For MeldabilityCalculator and StateTracker, these are the values we previously had hardcoded 
     * as static final variables at the top of the classes.
     * 
     * This does not include the ensemble weights, which get added in setUpParamList.
     */
    private static ArrayList<Double> getDefaultParamAL() {
        ArrayList<Double> defaults = new ArrayList<Double>();

        // 4) from ex2-1.txt
        defaults.add(CH_SAMERANK, 0.5);
        defaults.add(CH_ONEAWAY, 0.4);
        defaults.add(CH_TWOAWAY, 0.1);

        // 3) from ex1-1.txt
        defaults.add(MC_SELF_LOW_OBTAINABILITY, 0.1);
        defaults.add(MC_SELF_RATIO_FOR_UNKNOWN, 0.9);
        defaults.add(MC_SELF_WRANK, 0.4);
        defaults.add(MC_SELF_WRUN, 0.6);

        // MultiOpp - not used
        defaults.add(MC_OPP_LOW_OBTAINABILITY, 0.05);
        defaults.add(MC_OPP_RATIO_FOR_UNKNOWN, 0.5);
        defaults.add(MC_OPP_WRANK, 0.4);
        defaults.add(MC_OPP_WRUN, 0.6);

        // 2) Set A (7/19 meeting agenda, special non-game experiment)
        defaults.add(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, 0.8380616650330837);
        defaults.add(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, 0.06152068390417398);
        defaults.add(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, 0.38258062302368523);
        defaults.add(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, 0.39810496473742085);
        defaults.add(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, 0.10844666639491507);
        defaults.add(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, 0.37763392278384067);
        defaults.add(ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, 0.2706576839587317);
        defaults.add(ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, 0.2077890824329678);
        defaults.add(ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, 0.47922119839098565); 

        // ScorePredictionKnockDecider - not used
        defaults.add(SP_NUM_OF_ADDITIONAL_CARDS, 1.0);

        // MultiOpp - not used
        defaults.add(OM_NUM_OF_ADDITIONAL_CARDS, 1.0);

        // 5) ex3-1.txt
        defaults.add(LD_PENALTY_SLOPE, 0.0); // Hmm, best is to not change the weights at all...
        defaults.add(LD_PENALTY_EXPONENT, 2.75);

        // 6) ex4-1.txt
        defaults.add(OD_PENALTY_SLOPE, 0.0); // Again, best is to not change the weights at all...
        defaults.add(OD_PENALTY_EXPONENT, 0.25);

        // 1) These values are set based on the exhaustive knock decider experiments (special non-game experiment)
        defaults.add(TS_KNOCK_THRESHOLD_EARLY, 10.0);
        defaults.add(TS_KNOCK_THRESHOLD_LATE, 0.0);
        defaults.add(TS_KNOCK_MIDDLE, 6.0);

        // OneStageKnockDecider - not used
        defaults.add(OS_KNOCK_THRESHOLD, 8.0);

        // TwoStageDrawDecider - set based on discussion in 7/20 meeting (special non-game experiment)
        defaults.add(TS_DRAW_MIDDLE, 14.0);

        return defaults;
    }

    /**
     * Returns the hyperparameter with the given id.
     * For the id, use one of the constants defined in the ParamList class.
     */
    public double get(int id) {
        return paramAL.get(id); 
    }

    /**
     * Sets the hyperparameter with the given id to the given value.
     * For the id, use one of the constants defined in the ParamList class.
     */
    public void set(int id, double value) {
        paramAL.set(id, value);
    }

    /**
     * @return the index at which the weight was added
     */
    /*
    public int addEnsembleWeight(double value) {
        paramAL.add(value);
        numEnsembleWeights++;
        return paramAL.size()-1;
    }
    */

    /**
     * @param ensembleMemberID The ensemble member ID (so, 0 for the first HandEvaluator, 1 for the next, etc.)
     */
    public double getEnsembleWeight(int ensembleMemberID) {
        return get(ensembleMemberID + ENSEMBLE_STARTING_ID);
    }

    public int getSize(){
        return paramAL.size();
    }

    public void printSpecifiedParams(int start, int stop){
        for(int i = start; i <= stop; i++){
            if(i != stop)
                System.out.print(this.get(i) + ", ");
            else
                System.out.println(this.get(i));
        }
    }

    /**
     * @param ensembleMemberID The ensemble member ID (so, 0 for the first HandEvaluator, 1 for the next, etc.)
     */
    /*
    public void setEnsembleWeight(int ensembleMemberID, double value) {
        set(ensembleMemberID + ENSEMBLE_STARTING_ID, value);
    }
    */

    /**
     * Returns paramAL
     * Created to store information on ParamList object when serializing StateTracker
     */
    // public ArrayList<Double> getParamAL() {
    //     return paramAL;
    // }

    /**
     * This method should be called after one or more hyperparameter values
     * are mutated, to ensure that restrictions on values are maintained.
     */
    public void enforceRestrictions() {
        // ConvHandEvaluator
        checkOrder(CH_ONEAWAY, CH_TWOAWAY);
        convHandNormalizer(); //CH_ONEAWAY, CH_TWOAWAY, CH_SAMERANK

        // Meldability Calculator
        checkBounds(MC_SELF_LOW_OBTAINABILITY, 0, 1);
        checkBounds(MC_SELF_RATIO_FOR_UNKNOWN, 0, 1);
        checkBounds(MC_OPP_LOW_OBTAINABILITY, 0, 1);
        checkBounds(MC_OPP_RATIO_FOR_UNKNOWN, 0, 1);
        meldabilityWeightNormalizer();  //MC_SELF_WRANK, MC_SELF_WRUN, MC_OPP_WRANK, MC_OPP_WRUN

        // StateTracker
        checkOrder(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY);
        checkOrder(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY);
        checkOrder(ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY);
        
        checkBounds(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, 0, 1);
        checkBounds(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, 0, 1);
        checkBounds(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, 0, 1);

        checkBounds(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, 0, 1);
        checkBounds(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, 0, 1);
        checkBounds(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, 0, 1);

        checkBounds(ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, 0, 1);
        checkBounds(ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, 0, 1);
        checkBounds(ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, 0, 1);

        // ScorePrediction
        checkBounds(SP_NUM_OF_ADDITIONAL_CARDS, 0, 10);

        checkBounds(LD_PENALTY_SLOPE, 0, 1);
        checkBounds(LD_PENALTY_EXPONENT, 0, 4);

        checkBounds(OD_PENALTY_SLOPE, 0, 1);
        checkBounds(OD_PENALTY_EXPONENT, 0, 4);

        //multiOppHandMeldability
        checkBounds(OM_NUM_OF_ADDITIONAL_CARDS, 0, 10);

        //TwoStagedKnockDecider
        checkOrder(TS_KNOCK_THRESHOLD_EARLY, TS_KNOCK_THRESHOLD_LATE);
        checkBounds(TS_KNOCK_MIDDLE, 5, 19);

        checkBounds(OS_KNOCK_THRESHOLD, 0, 10);

        checkBounds(TS_DRAW_MIDDLE, 0, 25);

        ensembleWeightNormalizer();
    }

    public void checkBounds(int id, double min, double max){
        if(this.get(id) < min)
            this.set(id, min);
        else if(this.get(id) > max)
            this.set(id, max);
    }

    public void checkOrder(int largerId, int smallerId){
        if (SHOULD_CHECK_ORDER) {
            if(this.get(largerId) < this.get(smallerId))
                swapParams(largerId, smallerId);
        }
    }
    
    public void swapParams(int id1, int id2){
        double swap = paramAL.get(id1);
        this.set(id1, paramAL.get(id2));
        this.set(id2, swap);
    }
    
    private void convHandNormalizer(){
        double sumOfConvHandEvalParams = paramAL.get(CH_ONEAWAY) + paramAL.get(CH_TWOAWAY) + paramAL.get(CH_SAMERANK);
        this.set(CH_ONEAWAY, this.get(CH_ONEAWAY)/sumOfConvHandEvalParams);
        this.set(CH_TWOAWAY, this.get(CH_TWOAWAY)/sumOfConvHandEvalParams);
        this.set(CH_SAMERANK, this.get(CH_SAMERANK)/sumOfConvHandEvalParams);
    }

    private void meldabilityWeightNormalizer(){
        double sum = paramAL.get(MC_SELF_WRANK) + paramAL.get(MC_SELF_WRUN);
        this.set(MC_SELF_WRANK, this.get(MC_SELF_WRANK)/sum);
        this.set(MC_SELF_WRUN, this.get(MC_SELF_WRUN)/sum);
        
        sum = paramAL.get(MC_OPP_WRANK) + paramAL.get(MC_OPP_WRUN);
        this.set(MC_OPP_WRANK, this.get(MC_OPP_WRANK)/sum);
        this.set(MC_OPP_WRUN, this.get(MC_OPP_WRUN)/sum);
    }

    private void ensembleWeightNormalizer() {
        double sum = 0;
        for(int wtID = 0; wtID < numEnsembleWeights; wtID++)
            sum += getEnsembleWeight(wtID);

        if (sum != 0) {
            for(int wtID = 0; wtID < numEnsembleWeights; wtID++)
                set(wtID + ENSEMBLE_STARTING_ID, getEnsembleWeight(wtID)/sum); //  setEnsembleWeight(wtID, getEnsembleWeight(wtID)/sum);
        }
    }

    /*
    public void mutate(double mutationChance){
        Random r = new Random();
        for(int idOfGeneMutated = 0; idOfGeneMutated < paramAL.size(); idOfGeneMutated++){
            double randomChance = r.nextDouble();
            if(randomChance < mutationChance){
                // int idOfGeneMutated = r.nextInt(paramAL.size()); 
                double rangeMin;
                double rangeMax;
                double mutationValue = -1;
                switch(idOfGeneMutated){
                    case CH_SAMERANK: case CH_ONEAWAY: case CH_TWOAWAY:
                        mutationValue = r.nextDouble();
                        break;
                    case MC_SELF_LOW_OBTAINABILITY :
                        mutationValue = r.nextDouble()/2;
                        break;
                    case MC_SELF_RATIO_FOR_UNKNOWN: 
                        mutationValue = r.nextDouble();
                        break;
                    case MC_SELF_WRANK: case MC_SELF_WRUN:
                        mutationValue = r.nextDouble();
                        break;
                    case MC_OPP_LOW_OBTAINABILITY :
                        mutationValue = r.nextDouble()/2;
                        break;
                    case MC_OPP_RATIO_FOR_UNKNOWN: 
                        mutationValue = r.nextDouble();
                        break;
                    case MC_OPP_WRANK: case MC_OPP_WRUN:
                        mutationValue = r.nextDouble();
                        break;
                    case ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY:
                    case ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY:
                    case ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK:
                    case ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY:
                    case ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY:
                    case ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK:
                    case ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY:
                    case ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY:
                    case ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK:
                        mutationValue = r.nextDouble();
                        break;       
                    case SP_NUM_OF_ADDITIONAL_CARDS: case OM_NUM_OF_ADDITIONAL_CARDS:
                        mutationValue = r.nextInt(10); // we need to consider the upper bound
                        break;
                    case LD_PENALTY_SLOPE: case OD_PENALTY_SLOPE:
                        mutationValue = r.nextDouble();
                        break;
                    case LD_PENALTY_EXPONENT: case OD_PENALTY_EXPONENT:
                        mutationValue = r.nextDouble() + r.nextInt(4);
                        break;
                    case TS_KNOCK_THRESHOLD_EARLY: case TS_KNOCK_THRESHOLD_LATE:
                        mutationValue = r.nextInt(11);
                        break;
                    case TS_KNOCK_MIDDLE: 
                        mutationValue = 5 + r.nextInt(15);
                        break;
                    case OS_KNOCK_THRESHOLD:
                        mutationValue = r.nextInt(11);
                        break;
                    default: // an ensemble weight
                        mutationValue = r.nextDouble();
                        break;
                }
                if(mutationValue != -1){
                    this.set(idOfGeneMutated, mutationValue);
                    this.enforceRestrictions();
                }
                else
                    System.out.println("Error: Mutation wasn't successfully done");
            }
        }
    }*/

    public void mutate(double mutationChance){
        Random r = new Random();
        for(int idOfGeneMutated = 0; idOfGeneMutated < (this.numEnsembleWeights + NUM_NONENSEMBLE_PARAMS) ; idOfGeneMutated++){
            if(fixedValues.get(idOfGeneMutated) != -1){
                continue;
            }
            double randomChance = r.nextDouble();
            if(randomChance < mutationChance){
                // int idOfGeneMutated = r.nextInt(paramAL.size()); 
                // double rangeMin;
                // double rangeMax;
                double mutationValue = -1;
                switch(idOfGeneMutated){
                    case ParamList.CH_SAMERANK: case ParamList.CH_ONEAWAY: case ParamList.CH_TWOAWAY:
                        mutationValue = r.nextDouble();
                        break;
                    case ParamList.MC_SELF_LOW_OBTAINABILITY :
                        mutationValue = r.nextDouble()/2;
                        break;
                    case ParamList.MC_SELF_RATIO_FOR_UNKNOWN: 
                        mutationValue = r.nextDouble();
                        break;
                    case ParamList.MC_SELF_WRANK: case ParamList.MC_SELF_WRUN:
                        mutationValue = r.nextDouble();
                        break;
                    case ParamList.MC_OPP_LOW_OBTAINABILITY :
                        mutationValue = r.nextDouble()/2;
                        break;
                    case ParamList.MC_OPP_RATIO_FOR_UNKNOWN: 
                        mutationValue = r.nextDouble();
                        break;
                    case ParamList.MC_OPP_WRANK: case ParamList.MC_OPP_WRUN:
                        mutationValue = r.nextDouble();
                        break;
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
                    case ParamList.SP_NUM_OF_ADDITIONAL_CARDS: case ParamList.OM_NUM_OF_ADDITIONAL_CARDS:
                        mutationValue = r.nextInt(10); // we need to consider the upper bound
                        break;
                    case ParamList.LD_PENALTY_SLOPE: case ParamList.OD_PENALTY_SLOPE:
                        mutationValue = r.nextDouble();
                        break;
                    case ParamList.LD_PENALTY_EXPONENT: case ParamList.OD_PENALTY_EXPONENT:
                        mutationValue = r.nextDouble() + r.nextInt(4);
                        break;
                    case ParamList.TS_KNOCK_THRESHOLD_EARLY: case ParamList.TS_KNOCK_THRESHOLD_LATE:
                        mutationValue = r.nextInt(11);
                        break;
                    case ParamList.TS_KNOCK_MIDDLE: 
                    case ParamList.TS_DRAW_MIDDLE:
                        mutationValue = 5 + r.nextInt(15);
                        break;
                    case ParamList.OS_KNOCK_THRESHOLD:
                        mutationValue = r.nextInt(11);
                        break;
                    default: // an ensemble weight
                        mutationValue = r.nextDouble();
                        break;
                }
                if(mutationValue != -1){
                    this.set(idOfGeneMutated, mutationValue);
                    this.enforceRestrictions();
                }
                else
                    System.out.println("Error: Mutation wasn't successfully done");
            }
        }
    }
    
    // a1 | b1 c1
    // a2 | b2 c2
    // a1 b2 c2
    
    // single point and double point crossover - we need to decide this 
    //randomly select two points in the indivdual to swap
    //ParamList1 = a1 b1 | c1 d1 e1 f1 | g1 h1 i1 j1
    //ParamList2 = a2 b2 | c2 d2 e2 f2 | g2 h2 i2 j2
    //two points between b&c and f&g (how do we code this to select random points?)
    //child 1 = a1 b1 c2 d2 e2 f2 g1 h1 i1 j1
    //child 2 = a2 b2 c1 d1 e1 f1 g2 h2 i2 j2

    public ParamList[] twoPointsCrossover(ParamList otherParent, int numEnsembleWeights){
        Random r = new Random();
        int start = r.nextInt(paramAL.size());
        int stop = r.nextInt(paramAL.size());
        if(TESTING) System.out.println("Crossover from: " + start + " to: " + stop);
        
        ParamList child1 = new ParamList(numEnsembleWeights);
        ParamList child2 = new ParamList(numEnsembleWeights);

        for(int i = 0; i < paramAL.size(); i++){
            if((start <= stop && (i >= start && i <= stop)) || (start > stop && (i <=stop || i >= start))){
                child1.set(i, otherParent.get(i));
                child2.set(i, this.get(i));
            }
            else{
                child1.set(i, this.get(i));
                child2.set(i, otherParent.get(i));
            }
        }
        child1.enforceRestrictions();
        child2.enforceRestrictions();
        ParamList[] children = {child1, child2};
        return children;
    }

    public ParamList[] onePointCrossOver(ParamList otherParent, int numEnsembleWeights){
        Random r = new Random();
        int first = r.nextInt(paramAL.size());
        int second = r.nextInt(paramAL.size());
        if(TESTING) System.out.println("Crossover : " + first + " and: " + second);

        ParamList child1 = new ParamList(numEnsembleWeights);
        ParamList child2 = new ParamList(numEnsembleWeights);
        
        for(int i = 0; i < paramAL.size(); i++){
            if(i != first && i != second){
                child1.set(i, this.get(i));
                child2.set(i, otherParent.get(i));
            }
            else{
                child1.set(i, otherParent.get(i));
                child2.set(i, this.get(i));    
            }
        }
        child1.enforceRestrictions();
        child2.enforceRestrictions();
        ParamList[] children = {child1, child2};
        return children;
    }

    public static void testCrossover(int numOfEvaluators){
        System.out.println("============== testCrossover ==============");
        ParamList parent1 = new ParamList(numOfEvaluators);
        ParamList parent2 = new ParamList(numOfEvaluators);
        for(int indexOfParam = 0; indexOfParam<parent1.paramAL.size(); indexOfParam++){
            parent1.set(indexOfParam, indexOfParam/10.0);
            parent2.set(indexOfParam, indexOfParam/20.0);
        }
        ParamList[] newParamLists = parent1.twoPointsCrossover(parent2, numOfEvaluators);
        System.out.println("Parent 1: " + parent1);
        System.out.println("Parent 2: " + parent2);
        System.out.println("Child 1: " + newParamLists[0]);
        System.out.println("Child 2: " + newParamLists[1]);
    }

    public static void testEnforceRestrictions(){
        System.out.println("============== testEnforceRestrictions ==============");
        double [] ensembleWeights = {1,2,3};//new double[]
        ParamList tester = new ParamList(ensembleWeights);
        //ConvHand restrictions
        tester.set(CH_ONEAWAY, 0.6);
        tester.set(CH_TWOAWAY, 0.9);
        //Medability restrictions
        tester.set(MC_SELF_LOW_OBTAINABILITY, 1.7); 
        tester.set(MC_SELF_RATIO_FOR_UNKNOWN, -1.1);
        tester.set(MC_SELF_WRANK, 0.9);
        tester.set(MC_SELF_WRUN, 1.8);
        tester.set(MC_OPP_LOW_OBTAINABILITY, 1.7); 
        tester.set(MC_OPP_RATIO_FOR_UNKNOWN, -1.1);
        tester.set(MC_OPP_WRANK, 0.9);
        tester.set(MC_OPP_WRUN, 1.8);
        //State Tracker
        tester.set(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, 0.07);
        tester.set(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, 0.14);
        tester.set(ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, 0.8);
        tester.set(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, 0.05);
        tester.set(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, 0.15);
        tester.set(ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, 0.20);
        tester.set(ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, 0.1);
        tester.set(ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, 0.05);
        tester.set(ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, 0.07);
        //ScorePrediction
        tester.set(SP_NUM_OF_ADDITIONAL_CARDS, 15);
        //multiOppHandMeldabilityEvaluator
        tester.set(OM_NUM_OF_ADDITIONAL_CARDS, 15);
        //LinearDeadWoodPeanltyHandEvaluator
        tester.set(LD_PENALTY_SLOPE, -2.7);
        tester.set(LD_PENALTY_EXPONENT, 6);
        //OppCardsKnownDeadwoodPenaltyHandEvaluator
        tester.set(OD_PENALTY_SLOPE, 3);
        tester.set(OD_PENALTY_EXPONENT, -1);
        //TwoStagedKnockDecider
        tester.set(TS_KNOCK_THRESHOLD_EARLY, 6.0);
        tester.set(TS_KNOCK_THRESHOLD_LATE, 9.0);
        tester.set(TS_KNOCK_MIDDLE, 20);
        //OneStagedKnockDecider
        tester.set(OS_KNOCK_THRESHOLD, -1);
        //TwoStageDrawDecider
        tester.set(TS_DRAW_MIDDLE, 10);

        System.out.println("======================================================");
        System.out.println("The ParamList before enforceRestriction is: " + tester);
        System.out.println();
        tester.enforceRestrictions();
        System.out.println("The ParamList after enforceRestriction is: " + tester);
    }
    public static void testMutation(){
        ParamList param = new ParamList(4);
        for(int i = 0; i < 10; i++){
            System.out.println(param.paramAL); 
            param.mutate(0.1);
        }
        // System.out.println(param.toString());
        System.out.println(param.paramAL);
    }

    public static void testCopyConstructor(){
        double[] ensembleWeights = new double[] {0.15, 0.70, 0.10, 0.10, /*0.10,*/ 0.10, 0.10};
        ParamList params = new ParamList(ensembleWeights);
        params.set(ParamList.TS_KNOCK_THRESHOLD_EARLY, 10.0); // default is 9.0
        params.set(ParamList.TS_KNOCK_THRESHOLD_LATE, 0.0); // default is 6.0
        params.set(ParamList.TS_KNOCK_MIDDLE, 4.0); // default is 6.0
        params.enforceRestrictions();

        ParamList copied = new ParamList(params);
        System.out.println("Original ParamList: " + params.getSize());
        System.out.println("Copied ParamList: " + copied.getSize());
    }

    public static void setTesting(boolean testing){
        TESTING = testing;
    }
    
    @Override
    public String toString(){
        return paramAL.toString();
    }

    public String toVerboseString() {
        String str = "Array rep: " + toString() + "\nTable rep: {\n";
        for(int i = 0; i < paramAL.size(); i++){
            str += "  " + String.format("%2d, ", i) + String.format("%9.6f", get(i)) + "\n";
        }
        str += "}";
        return str;
    }

    public ArrayList<Double> paramListToArrayList(){
        return new ArrayList<Double>(paramAL);
    }


    public static void testMutateSelectiveParamList(){
        //ArrayList<Double> myFixedValues = new ArrayList<>();
        
        int numOfEvaluators = 6;
        double mutationChance = 0.2;
        //for(int i = 0; i < ParamList.NUM_NONENSEMBLE_PARAMS + numOfEvaluators; i++){
           // myFixedValues.add(-1.0);
        //}

        ParamList.setFixedValue(ParamList.CH_SAMERANK, 0.4);
        ParamList.setFixedValue(ParamList.CH_ONEAWAY, 0.5);
        ParamList.setFixedValue(ParamList.CH_TWOAWAY, 0.1);
        System.out.println("fixed value:" + fixedValues);
        //ParamList.fixedValues = myFixedValues;
        ParamList params = ParamList.getRandomParamList(numOfEvaluators);
        System.out.println("Param before mutated: " + params);
        System.out.println();
        
        params.mutate(mutationChance);
        System.out.println("ParamList after mutattion: " + params);
    }

    public static void testSelectiveRandomIndividuals(int numOfEvaluators){
        // ParamList tester = new ParamList(new double[]{-1, -1, -1});
        // ParamList
        ArrayList<Double> myFixedValues = new ArrayList<>();
        for(int i = 0; i < ParamList.NUM_NONENSEMBLE_PARAMS + numOfEvaluators; i++){
            myFixedValues.add(-1.0);
        }

        myFixedValues.set(ParamList.CH_SAMERANK, 0.4);
        myFixedValues.set(ParamList.CH_ONEAWAY, 0.5);
        myFixedValues.set(ParamList.CH_TWOAWAY, 0.1);
        ParamList.fixedValues = myFixedValues;
        // tester.setFixValue(ParamList.CH_TWOAWAY, 0.35);
        ParamList randomIndividual1 = ParamList.getRandomParamList(numOfEvaluators);
        ParamList randomIndividual2 = ParamList.getRandomParamList(numOfEvaluators);
        // System.out.println("Old paramlist: " + tester);
        System.out.println("New paramlist 1: " + randomIndividual1);
        System.out.println("New paramlist 2: " + randomIndividual2);
    }
    
    public static void testParamListConstructor(){
        double[] paramsAndWeights = {0.29208164794833336, 0.4933043961984872, 0.2146139558531794, 0.039082238420405646, 0.4097065894571512, 0.34316571780522437, 0.6568342821947756, 0.17000003329459223, 0.9565683621930221, 0.4008540338225604, 0.5991459661774395, 0.8164209844981123, 0.33923897237621314, 0.9686458628844807, 0.8985860747919318, 0.7824084985947584, 0.3507563695181819, 0.9451028368829781, 0.3677917221207484, 0.21958246619065547, 1.0, 4.0, 0.8115237600349762, 3.9962362572044015, 0.030086775107394215, 3.834926979775834, 2.0, 1.0, 16.0, 6.0, 0.05411308380226888, 0.08453569036529046, 0.2157851074819329, 0.15142572449844588, 0.01187131896265894, 0.482269074889403};
        int numEnsembleWeights = 6;
        ParamList params = new ParamList(paramsAndWeights, numEnsembleWeights);
        System.out.println(params);

        // {0.29208164794833336, 0.4933043961984872, 0.2146139558531794, 0.039082238420405646, 0.4097065894571512, 0.34316571780522437, 0.6568342821947756, 0.17000003329459223, 0.9565683621930221, 0.4008540338225604, 0.5991459661774395, 0.8164209844981123, 0.33923897237621314, 0.9686458628844807, 0.8985860747919318, 0.7824084985947584, 0.3507563695181819, 0.9451028368829781, 0.3677917221207484, 0.21958246619065547, 1.0, 4.0, 0.8115237600349762, 3.9962362572044015, 0.030086775107394215, 3.834926979775834, 2.0, 1.0, 16.0, 6.0, 0.05411308380226888, 0.08453569036529046, 0.2157851074819329, 0.15142572449844588, 0.01187131896265894, 0.482269074889403};
        // [0.29208164794833336, 0.4933043961984872, 0.2146139558531794, 0.039082238420405646, 0.4097065894571512, 0.34316571780522437, 0.6568342821947756, 0.17000003329459223, 0.9565683621930221, 0.4008540338225604, 0.5991459661774395, 0.8164209844981123, 0.33923897237621314, 0.9686458628844807, 0.8985860747919318, 0.7824084985947584, 0.3507563695181819, 0.9451028368829781, 0.3677917221207484, 0.21958246619065547, 1.0, 4.0, 0.8115237600349762, 3.9962362572044015, 0.030086775107394215, 3.834926979775834, 2.0, 1.0, 16.0, 6.0, 0.05411308380226888, 0.08453569036529046, 0.2157851074819329, 0.15142572449844588, 0.01187131896265894, 0.482269074889403]
    }

    
    public static void main(String[] args){
        setTesting(true);
        
        // testCrossover();
        // testEnforceRestrictions();
        // testMutation();
        // testCopyConstructor();
        // testSelectiveRandomIndividuals(3);
        // testMutateSelectiveParamList();
        // testParamListConstructor();

        
    }
   
}