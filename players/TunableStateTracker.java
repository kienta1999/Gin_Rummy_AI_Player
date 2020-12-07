package players;

import ginrummy.Card;
import util.OurUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import experiment.ExperimentalRuns;
import games.TestingGame;

public class TunableStateTracker extends StateTracker{
    protected FakePlayer opp;
    protected ArrayList<Double> allFitnessError; //fitness of errors every round
    protected ArrayList<Double> allFitnessCount; //fitness of num of cards  StateTracker predicted correctly
    protected int numAdditionalCards;
    
    protected ArrayList<ArrayList<Double>> errorPerIndex;
    protected ArrayList<ArrayList<Double>> countPerIndex;
    public int numGames;
    public ArrayList<Double> numTurns;

    public static boolean PER_TURN = false;
    public static boolean PER_OPP_CARDS_KNOWN = false;

    // fitness and frequency settings
    public static int FITNESS_METHOD;
    public static final int FITNESS_ERROR = 3;
    public static final int FITNESS_COUNT = 4;
    public static final int NO_FITNESS = 5;

    public static int FREQUENCY_OF_CHECKING;
    public static int TURN_TO_CHECK;
    public static final int EVERY_TURN = 1;
    public static final int AT_ONE_TURN = 2;

    private static final long serialVersionUID = 1L;

    private TunableStateTracker(ParamList params) {
        super(params);
        allFitnessError = new ArrayList<Double>();
        allFitnessCount = new ArrayList<Double>();
        errorPerIndex = new ArrayList<ArrayList<Double>>();
        countPerIndex = new ArrayList<ArrayList<Double>>();
        this.numGames = 0;
        numTurns = new ArrayList<Double>();
    }

    public TunableStateTracker(ParamList params, FakePlayer opp){
        this(params);
        this.opp = opp;
        this.numAdditionalCards = 0;
    }

    public TunableStateTracker(ParamList params, FakePlayer opp, int numAdditionalCards){
        this(params);
        this.opp = opp;
        this.numAdditionalCards = numAdditionalCards;
    }

    public static void setFitnessMethod(int method){
        if(method == FITNESS_ERROR || method == FITNESS_COUNT || method == NO_FITNESS)
            FITNESS_METHOD = method;
        else
            System.out.println("Inappropriate input for fitness method: " + method);
    }

    public static void setFrequencyOfChecking(int frequency){
        if(frequency == EVERY_TURN || frequency == AT_ONE_TURN)
            FREQUENCY_OF_CHECKING = frequency;
        else
            System.out.println("Inappropriate input for frequncy: " + frequency);
    }

    public static void setTurnToCheck(int turn){
        TURN_TO_CHECK = turn;
    }

    public static void setPerTurn(boolean perTurn){
        PER_TURN = perTurn;
    }

    public static void setPerOppCardsKnown(boolean perKnown){
        PER_OPP_CARDS_KNOWN = perKnown;
    }

    @Override
    public void updateFromStartGame(int playerNum, int startingPlayerNum, Card[] cards) {
        if(PER_TURN && numGames != 0){
            if(TunableStateTracker.FITNESS_METHOD == TunableStateTracker.FITNESS_ERROR){
                this.errorPerIndex.add(allFitnessError);
                allFitnessError = new ArrayList<Double>();
            }
            else if(TunableStateTracker.FITNESS_METHOD == TunableStateTracker.FITNESS_COUNT){
                this.countPerIndex.add(allFitnessCount);
                allFitnessCount = new ArrayList<Double>();
            }
        }
        else if(PER_OPP_CARDS_KNOWN && numGames != 0){
            if(TunableStateTracker.FITNESS_METHOD == TunableStateTracker.FITNESS_ERROR){
                this.errorPerIndex.add(getListPerOppCardsUnknownAVG(allFitnessError, this.numOppCardsUnknown_t));
                allFitnessError = new ArrayList<Double>();
            }
            else if(TunableStateTracker.FITNESS_METHOD == TunableStateTracker.FITNESS_COUNT){
                this.countPerIndex.add(getListPerOppCardsUnknownAVG(allFitnessCount, this.numOppCardsUnknown_t));
                allFitnessCount = new ArrayList<Double>();
            }
        }
        numGames++;
        numTurns.add((double)this.turnsTaken);
        super.updateFromStartGame(playerNum, startingPlayerNum, cards);
    }

    @Override
    public void updateFromReportDiscard(int playerNum, Card discardCard){
        super.updateFromReportDiscard(playerNum, discardCard);

        //Set of all opponent's card
        int numTurnsTaken = getTurnsTaken();

        if(TunableStateTracker.FITNESS_METHOD == TunableStateTracker.FITNESS_ERROR){
            if(TunableStateTracker.FREQUENCY_OF_CHECKING == TunableStateTracker.EVERY_TURN
               || (TunableStateTracker.FREQUENCY_OF_CHECKING == TunableStateTracker.AT_ONE_TURN 
                   && TunableStateTracker.TURN_TO_CHECK == numTurnsTaken && numTurnsTaken != 0)){
                    
                    double fitnessError = calcErrorFitness();
                    allFitnessError.add(fitnessError);
               }
        }
        else if(TunableStateTracker.FITNESS_METHOD == TunableStateTracker.FITNESS_COUNT){
            if(TunableStateTracker.FREQUENCY_OF_CHECKING == TunableStateTracker.EVERY_TURN
               || (TunableStateTracker.FREQUENCY_OF_CHECKING == TunableStateTracker.AT_ONE_TURN 
                   && TunableStateTracker.TURN_TO_CHECK == numTurnsTaken && numTurnsTaken != 0)){
                    
                    double fitnessCount = calcCountFitness();
                    allFitnessCount.add(fitnessCount);
            }
        }
        else if(TunableStateTracker.FITNESS_METHOD != TunableStateTracker.NO_FITNESS){
            System.out.println("Error: Must Set Fitness");
        }
    }

    private double calcErrorFitness(){
        HashSet<Card> oppHand = new HashSet<>(opp.showCards());
        double fitnessError = 0;
        double error = 0;
        double w = (double)(this.numDeckCards)/this.numOppCardsUnknown;

        for(int suit = 0; suit < Card.NUM_SUITS; suit++){
            for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                if(this.matrix[suit][rank][StateTracker.STATE] == StateTracker.UNKNOWN){
                    Card card = OurUtil.getCard(rank, suit);
                    if(oppHand.contains(card)){
                        //the card is in opponent's hand - probability should be close to 0 here
                        error += w * this.matrix[suit][rank][StateTracker.PROB_IN_STOCK];
                    }
                    else{
                        //the card is in stock - probability should be close to 1 here
                        error += 1 - this.matrix[suit][rank][StateTracker.PROB_IN_STOCK];
                    }
                }
            }
        }
        //normalize the error
        int maxError = 2 * this.numDeckCards; // = numDeckCards + w * numOppCardsUnknown
        error /= maxError; // the more accurate predictions are, the smaller the error is
        fitnessError = 1 - error; // the smaller the error is, the bigger the fitness is
        return fitnessError;
    }

    private double calcCountFitness(){
        if(this.numOppCardsUnknown != 0){
            HashSet<Card> oppHand = new HashSet<>(opp.showCards());
            int count = 0;
            Map<Card, Double> oppCardsGuessed = getCardToProbMapWithHighestProbInOppHand(this.numOppCardsUnknown + this.numAdditionalCards);
            for(Card card: oppCardsGuessed.keySet()){
                if(oppHand.contains(card)){
                    count++;       
                }
            }
            double countRate = (double)count/this.numOppCardsUnknown;// the more accurate predictions are, the bigger the count is
            // System.out.println(oppHand);
            // System.out.println(oppCardsGuessed.keySet());
            // System.out.println("countRate: " + countRate + ", count: " + count + ", numOppCardsUnknown: " + this.numOppCardsUnknown);
            return countRate;
        }
        else //we know every card in opp's hand
            return 1.0; // let's handle this as if we predicted every card
    }

    public ArrayList<Double> getNumTurns(){
        return this.numTurns;
    }

    public double getFitnessError(){
        double overallFitness = 0;
        for(Double fitness: allFitnessError){
                overallFitness += fitness;
        }

        if(allFitnessError.size() != 0)
            return overallFitness / allFitnessError.size();
        else{
            System.out.println("No fitness was computed. TURN_TO_CHECK might be too large or there is a bug");
            return -1;
        }
             
    }

    public double getFitnessCount(){
        double overAllFitness = 0;
        for(Double fitness: allFitnessCount){
            overAllFitness += fitness;
        }
        
        if(allFitnessCount.size() != 0)
            return overAllFitness / allFitnessCount.size();
        else{
            System.out.println("No fitness was computed. TURN_TO_CHECK might be too large or there is a bug");
            return -1;
        }
    }

    public ArrayList<Double> getErrorPerIndexAVG(){
        ArrayList<Double> errorPerIndexAVG = getListPerIndexAVG(this.errorPerIndex);
        return errorPerIndexAVG;
    }

    public ArrayList<Double> getCountPerIndexAVG(){
        ArrayList<Double> countPerIndexAVG = getListPerIndexAVG(this.countPerIndex);
        return countPerIndexAVG;
    }

    public static ArrayList<Double> getListPerIndexAVG(ArrayList<ArrayList<Double>> data){
        ArrayList<Double> listPerTurnAVG = new ArrayList<>();
        int longest = getLongestGame(data);
        for(int i = 0; i < longest; i++){//loop through indices
            int num = 0;
            double sum = 0;
            for(int j = 0; j < data.size(); j++){//loop through data
                if(i < data.get(j).size()){
                    sum += data.get(j).get(i);
                    num++;
                }
            }
            double avg = sum/num;
            listPerTurnAVG.add(avg);
        }
        return listPerTurnAVG;
    }
    
    public static int getLongestGame(ArrayList<ArrayList<Double>> data){
        int max = -1;
        for(int i = 0; i < data.size(); i++){
            if(max < data.get(i).size())
                max = data.get(i).size();
        }
        return max;
    }

    public static ArrayList<Double> getListPerOppCardsUnknownAVG(ArrayList<Double> allFitness, ArrayList<Integer> numOppCardsUnknown_t){
        // System.out.println("Size of fitness: " + allFitness.size() + ", Size of nummOppCardsUnknown: " + numOppCardsUnknown_t.size());
        ArrayList<Double> listPerNumUnknown = new ArrayList<Double>();
        double[] lsSum = new double[10];
        int[] num = new int[10];
        for(int i = 1; i < numOppCardsUnknown_t.size(); i++){  //numOppCardsUnknonw_t has one more value than allFitness because it adds a value in updateFromStartGame
            int index = 10 - numOppCardsUnknown_t.get(i);
            lsSum[index] += allFitness.get(i-1); //because of the reason above, the index must be i-1
            num[index]++;
        }
        for(int i = 0; i < num.length; i++){
            double avg = num[i] != 0 ? lsSum[i]/num[i] : -1;
            if(avg != -1)
                listPerNumUnknown.add(avg);
        }
        // System.out.println(listPerNumUnknown);
        return listPerNumUnknown;
    }

    public static void testCalcCountFitness(){
        
    }
 
    public static void main (String[] args){
        SimpleFakeGinRummyPlayer p1 = new SimpleFakeGinRummyPlayer(new ParamList(new double[0]));

        ParamList params = new ParamList(new double[0]);
        int numAdditionalCards = 0;
        TunableStateTracker st0 = new TunableStateTracker(params, p1, numAdditionalCards);
        // System.out.println("Used parameters: ");
        double[] parameters = ExperimentalRuns.getStateTrackerParams014();
        for(int i = ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY; i <= ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK; i++)
            params.set(i, parameters[i - ParamList.ST_START]);
        System.out.println();
        SimpleFakeGinRummyPlayer p0 = new SimpleFakeGinRummyPlayer(params, st0);
        
        TestingGame gameManager = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(false);

        TunableStateTracker.setFitnessMethod(TunableStateTracker.FITNESS_COUNT);
        TunableStateTracker.setFrequencyOfChecking(TunableStateTracker.EVERY_TURN);
        setPerOppCardsKnown(true);
        for(int i = 0; i < 1; i++)
            gameManager.play();

        // System.out.println(((TunableStateTracker)p0.getStateTracker()).getFitnessCount());
    }
}