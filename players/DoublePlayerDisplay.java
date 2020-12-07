package players;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import util.OurUtil;
import ginrummy.Card;
import ginrummy.GinRummyUtil;
import players.MeldabilityCalculator;
import players.ParamList;

public class DoublePlayerDisplay {
    public static void display(String filepath){
        try{
            File file = new File(filepath);
            Scanner scan = new Scanner(file);
            int count = 0;
            if(filepath.equals("players\\doubleplayer.txt") )
                scan.nextLine(); //skip the first line
            while(scan.hasNextLine()){
                count++;
                String[] data = scan.nextLine().split(",");
                String cardSimple = data[0];
                String cardOther = data[1];
                String[] myHand = data[2].split(" ");
                String[] opponentHand = data[3].split(" ");
                String serializedStateTracker = data[4];
                StateTracker myTracker = StateTracker.deserializeStateTrackerFromString(serializedStateTracker);
                System.out.println("---------------------------------------------------------------------------------------");
                System.out.println("Count senario: " + count);
                System.out.println("State Tracker: " + myTracker);
                System.out.println("The card simple player choose to discard: " + cardSimple);
                System.out.println("The card ensemble player choose to discard: " + cardOther);
                
                
                System.out.println("Player's hand: " + Arrays.toString(myHand));
                ArrayList<ArrayList<ArrayList<Card>>> playerBestMeldSets = GinRummyUtil.cardsToBestMeldSets(OurUtil.makeHand(myHand));
                if(!playerBestMeldSets.isEmpty())
                    System.out.println("Player's meld: " + playerBestMeldSets.get(0) );
                else{
                    System.out.println("Player doesnt have meld");
                }


                System.out.println("Opponent's hand: " + Arrays.toString(opponentHand));
                ArrayList<ArrayList<ArrayList<Card>>> oppBestMeldSets = GinRummyUtil.cardsToBestMeldSets(OurUtil.makeHand(opponentHand));
                if(!oppBestMeldSets.isEmpty())
                    System.out.println("Opponent's meld: " + oppBestMeldSets.get(0) );
                else{
                    System.out.println("Opp doesnt have meld");
                }

                
                
                


                ParamList params = new ParamList(new ParamList(new double[]{}));
                params.set(ParamList.CH_ONEAWAY, 0.374);
                params.set(ParamList.CH_TWOAWAY, 0.149);
                params.set(ParamList.CH_SAMERANK, 0.477);
                params.set(ParamList.MC_SELF_LOW_OBTAINABILITY, 0.086);
                params.set(ParamList.MC_SELF_RATIO_FOR_UNKNOWN, 0.885);
                params.set(ParamList.MC_SELF_WRANK, 0.565);
                params.set(ParamList.MC_SELF_WRUN, 0.435);
                params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_ONEAWAY, 0.9);
                params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_TWOAWAY, 0.25);
                params.set(ParamList.ST_DECREASE_PROB_OPP_DRAWS_FACEUP_SAMERANK, 0.9);
                params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_ONEAWAY, 0.25);
                params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_TWOAWAY, 0.18);
                params.set(ParamList.ST_INCREASE_PROB_OPP_DECLINES_FACEUP_SAMERANK, 0.25);
                params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_ONEAWAY, 0.9);
                params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_TWOAWAY, 0.25);
                params.set(ParamList.ST_INCREASE_PROB_OPP_DISCARDED_SAMERANK, 0.9);
                MeldabilityCalculator calculator = new MeldabilityCalculator(params);
                System.out.println("Meldability matrix: ");
                if(!playerBestMeldSets.isEmpty())
                    calculator.displayMatrix(calculator.getMatrixOfSelfMeld(myTracker, playerBestMeldSets.get(0) ) );
              
                else{ 
                    calculator.displayMatrix(calculator.getMatrixOfSelfMeld(myTracker, new ArrayList<ArrayList<Card>>() ) );
                }
                

            }
            scan.close();
        }
        catch (Exception e){
            System.out.println("File not found");
        }
    }
    public static void main(String[] args){
        display("players\\doubleplayer.txt");
        
    }    
}