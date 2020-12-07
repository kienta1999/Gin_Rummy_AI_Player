package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import util.OurUtil;
import ginrummy.Card;
import ginrummy.GinRummyUtil;
import players.MeldabilityCalculator;
import players.ParamList;

import players.StateTracker;

public class GameSimulatorReader {
    
    public static void display(String filepath, ParamList params){
        try{
            System.out.println("-------------- Start Recovering Data ---------------");
            File file = new File(filepath);
            Scanner scan = new Scanner(file);
            int count = 0;
            scan.nextLine(); //skip the first line
            while(scan.hasNextLine()){
                count++;
                String[] data = scan.nextLine().split(",");
                String discardEnsemble = data[0];
                String[] ensembleHands = data[1].split(" ");
                String[] yourHands= data[2].split(" ");
                String serializedStateTracker = data[3];
                StateTracker myTracker = StateTracker.deserializeStateTrackerFromString(serializedStateTracker);
                System.out.println("---------------------------------------------------------------------------------------");
                System.out.println("selfTurnsTaken: " + count);
                System.out.println("State Tracker: " + myTracker);
                System.out.println("The card ensemble player choose to discard: " + discardEnsemble);
                
                System.out.println("Player's hand: " + Arrays.toString(ensembleHands));
                ArrayList<ArrayList<ArrayList<Card>>> playerBestMeldSets = GinRummyUtil.cardsToBestMeldSets(OurUtil.makeHand(ensembleHands));
                if(!playerBestMeldSets.isEmpty())
                    System.out.println("Ensemble Player's meld: " + playerBestMeldSets.get(0) );
                else{
                    System.out.println("Ensemble Player doesn't have any melds");
                }
                System.out.println("Your hand: " + Arrays.toString(yourHands));
                ArrayList<ArrayList<ArrayList<Card>>> oppBestMeldSets = GinRummyUtil.cardsToBestMeldSets(OurUtil.makeHand(yourHands));
                if(!oppBestMeldSets.isEmpty())
                    System.out.println("Your melds: " + oppBestMeldSets.get(0) );
                else{
                    System.out.println("You don't have any melds");
                }
                MeldabilityCalculator calculator = new MeldabilityCalculator(params);
                System.out.println("---------- Meldability matrix -----------");
                if(!oppBestMeldSets.isEmpty())
                    calculator.displayMatrix(calculator.getMatrixOfSelfMeld(myTracker, playerBestMeldSets.get(0) ) );
              
                else{ 
                    calculator.displayMatrix(calculator.getMatrixOfSelfMeld(myTracker, new ArrayList<ArrayList<Card>>() ) );
                }
                

            }
            scan.close();
            System.out.println("-------------- End ---------------");
        }
        catch (Exception e){
            System.out.println("File not found");
        }
    }

    public static void main(String[] args){
        double[] ensembleWeights = new double[]{1, 1, 1};
        ParamList params = new ParamList(ensembleWeights);
        params.set(ParamList.CH_ONEAWAY, 0.374);
        params.set(ParamList.CH_TWOAWAY, 0.149);
        params.set(ParamList.CH_SAMERANK, 0.477);
        params.set(ParamList.MC_SELF_LOW_OBTAINABILITY, 0.086);
        params.set(ParamList.MC_SELF_RATIO_FOR_UNKNOWN, 0.885);
        params.set(ParamList.MC_SELF_WRANK, 0.565);
        params.set(ParamList.MC_SELF_WRUN, 0.435);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, 0.9);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, 0.376);
        params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, 0.9);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, 0.376);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, 0.304);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, 0.376);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, 0.9);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, 0.376);
        params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, 0.9);

        display("GameSimulator.txt", params);
    }
}