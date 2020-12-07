package players;

import ginrummy.Card;
// import games.TestingGame;
import ginrummy.GinRummyGame;
import util.OurUtil;
import java.util.ArrayList;

public class PerformanceTracker {
    //Variables reset at the beginning of game
    public int numRounds;
    public int numSelfKnocks;
    public int numOppKnocks;
    
    public int numSelfUndercuts;
    public int numOppUndercuts;

    public int numSelfGins;

    public int numSelfCardsLaidOff;
    public int numOppCardsLaidOff;

    public ArrayList<Integer> myScores;
    public ArrayList<Integer> oppScores;

    public ArrayList<Integer> listNumSelfTurnsToKnock;
    public ArrayList<Integer> listNumOppTurnsToKnock;

    public ArrayList<Integer> listNumSelfFaceUpCardsDrawn;
    public ArrayList<Integer> listNumOppFaceUpCardsDrawn;

    public ArrayList<Integer> listNumSelfMeldsAtKnock;
    public ArrayList<Integer> listNumSelfRunMeldsAtKnock;
    public ArrayList<Integer> listNumSelfRankMeldsAtKnock;

    public ArrayList<Integer> listNumOppMeldsAtKnock;
    public ArrayList<Integer> listNumOppRunMeldsAtKnock;
    public ArrayList<Integer> listNumOppRankMeldsAtKnock;





    //Variables reset at the beginning of round
    public int numSelfTurnsToKnock;
    public int numOppTurnsToKnock;
    
    public int numSelfFaceUpCardsDrawn;
    public int numOppFaceUpCardsDrawn;
    
    public int numSelfMeldsAtKnock;
    public int numSelfRunMeldsAtKnock;
    public int numSelfRankMeldsAtKnock;
    
    public int numOppMeldsAtKnock;
    public int numOppRunMeldsAtKnock;
    public int numOppRankMeldsAtKnock;

   

   

    public int numPlayerKnock;

    public StateTracker myStateTracker;
    

    //boolean opponentKnocked;
    //boolean selfKnocked;
    int playerNum;
    
    ArrayList<ArrayList<Card>> selfMeld;
    ArrayList<ArrayList<Card>> oppMeld;

    ArrayList<Card> myCard;
    int[] scoreOfPrevGame = new int[2];
    //save the score of previous game - to reset the parameter of new game

    public PerformanceTracker(){
        numRounds = 0; //done
        numSelfKnocks = 0; //done
        numOppKnocks = 0; //done
        
        numSelfUndercuts = 0; //done
        numOppUndercuts = 0; //done
        
        numSelfGins = 0; //done
        myScores = new ArrayList<>(); //done
        oppScores = new ArrayList<>(); //done

        
        numSelfCardsLaidOff = 0; //done
        numOppCardsLaidOff = 0; //done

        listNumSelfTurnsToKnock = new ArrayList<>();
        listNumOppTurnsToKnock = new ArrayList<>();
        
        listNumSelfFaceUpCardsDrawn = new ArrayList<>();
        listNumOppFaceUpCardsDrawn = new ArrayList<>();

        listNumSelfMeldsAtKnock = new ArrayList<>();
        listNumSelfRunMeldsAtKnock = new ArrayList<>();
        listNumSelfRankMeldsAtKnock = new ArrayList<>();

        listNumOppMeldsAtKnock = new ArrayList<>();
        listNumOppRunMeldsAtKnock = new ArrayList<>();
        listNumOppRankMeldsAtKnock = new ArrayList<>();
    }
    public void updateFromStartGame(StateTracker tracker, int playerNum, ArrayList<Card> cards){

        numSelfTurnsToKnock = 0; //done
        numOppTurnsToKnock = 0; //done

        numSelfFaceUpCardsDrawn = 0; //done
        numOppFaceUpCardsDrawn = 0; //done

        myStateTracker = tracker;
        numRounds++;
        this.playerNum = playerNum;
        

        numSelfMeldsAtKnock = 0; //done
        numSelfRunMeldsAtKnock = 0; //done
        numSelfRankMeldsAtKnock = 0; //done
    
        numOppMeldsAtKnock = 0; //done
        numOppRunMeldsAtKnock = 0; //done
        numOppRankMeldsAtKnock = 0; //done

        numPlayerKnock = -1;

        myCard = cards;

        if(scoreOfPrevGame[0] >= 100 || scoreOfPrevGame[1] >= 100){
            resetParameter();
        }
    }
    public void updateFromGetFinalMelds(ArrayList<ArrayList<Card>> selfMeld){
        
            
    }
    public boolean isRun(ArrayList<Card> meld){
        if(meld == null)
            return false;
        return meld.get(0).getSuit() == meld.get(1).getSuit();
    }
    public boolean isRank(ArrayList<Card> meld){
        if(meld == null)
            return false;
        return meld.get(0).getRank() == meld.get(1).getRank();
    }
    public void updateFromReportFinalMelds(int playerNum, ArrayList<ArrayList<Card>> melds){
        if(this.numPlayerKnock != 0 && this.numPlayerKnock != 1){
            this.numPlayerKnock = playerNum;
        }
        // System.out.println("playerNum who knocks: " + this.numPlayerKnock + " the player's melds: " + melds);
        // if(melds != null){
        //     numPlayerKnock = playerNum;
        // }
        if(playerNum == this.playerNum){
            selfMeld = melds;
            numSelfMeldsAtKnock = selfMeld.size();
            for(int i = 0; i < numSelfMeldsAtKnock; i++){
                if(isRun(selfMeld.get(i)))
                    numSelfRunMeldsAtKnock++;
                else
                    numSelfRankMeldsAtKnock++;   
            }
        }
        else{
            oppMeld = melds;
            numOppMeldsAtKnock = oppMeld.size();
            for(int i = 0; i < numOppMeldsAtKnock; i++){
                if(isRun(oppMeld.get(i)))
                    numOppRunMeldsAtKnock++;
                else
                    numOppRankMeldsAtKnock++;   
            }
        }
    }
    public void updateFromReportLayoff(int playerNum){
        
        if(playerNum == this.playerNum){
            numSelfCardsLaidOff++;
        }
        else{
            numOppCardsLaidOff++;
        }

    }
    public void updateFromReportScore(int[] scores){
        for(int suit = 0; suit < Card.NUM_SUITS; suit++){
            for(int rank = 0; rank < Card.NUM_RANKS; rank++){
                double state = myStateTracker.getMatrix()[suit][rank][StateTracker.STATE];
                if(state == StateTracker.SELF_FROM_DISCARD || state == StateTracker.SELF_FROM_START){
                    numSelfFaceUpCardsDrawn++;
                }
                if(state == StateTracker.OPP_FROM_DISCARD || state == StateTracker.OPP_FROM_START){
                    numOppFaceUpCardsDrawn++;
                }
            }
        }

        int myCurrentScore = -1;
        int oppCurrentScore = -1;

        if(myScores.size()>=1){
            myCurrentScore = scores[playerNum] - myScores.get(myScores.size() - 1);
            oppCurrentScore = scores[(playerNum == 0) ? 1 : 0] - oppScores.get(oppScores.size() - 1);              
        }
        else{
            myCurrentScore = scores[playerNum];
            oppCurrentScore = scores[(playerNum == 0) ? 1 : 0];
        }

        if(numPlayerKnock == this.playerNum){
            //player knocks
            numSelfTurnsToKnock = myStateTracker.getNumSelfTurnsTaken(); 
            numSelfKnocks++;
        if(myCurrentScore == 0 && oppCurrentScore > 0){
            numOppUndercuts++;
            }
        }
        else{
            //opponent knocks
            numOppKnocks++;
            numOppTurnsToKnock = myStateTracker.getTurnsTaken() - myStateTracker.getNumSelfTurnsTaken();
            if(oppCurrentScore == 0 && myCurrentScore > 0){
                numSelfUndercuts++;
            }
        }
        

        if(OurUtil.getUnmeldedCards(myCard).isEmpty()){
            numSelfGins++;
        }
        myScores.add(scores[playerNum]);
        oppScores.add(scores[(playerNum == 0) ? 1 : 0]);

        scoreOfPrevGame = scores;

        listNumSelfTurnsToKnock.add(numSelfTurnsToKnock);
        listNumOppTurnsToKnock.add(numOppTurnsToKnock);

        listNumSelfFaceUpCardsDrawn.add(numSelfFaceUpCardsDrawn);
        listNumOppFaceUpCardsDrawn.add(numOppFaceUpCardsDrawn);

        listNumSelfMeldsAtKnock.add(numSelfMeldsAtKnock);
        listNumSelfRunMeldsAtKnock.add(numSelfRunMeldsAtKnock);
        listNumSelfRankMeldsAtKnock.add(numSelfRankMeldsAtKnock);

        listNumOppMeldsAtKnock.add(numOppMeldsAtKnock);
        listNumOppRunMeldsAtKnock.add(numOppRunMeldsAtKnock);
        listNumOppRankMeldsAtKnock.add(numOppRankMeldsAtKnock);
    }
    public void resetParameter(){
        numRounds = 0;
        numSelfKnocks = 0;
        numOppKnocks = 0;
        
        numSelfUndercuts = 0;
        numOppUndercuts = 0;
        
        numSelfGins = 0; 
        myScores = new ArrayList<>(); 
        oppScores = new ArrayList<>(); 

        
        numSelfCardsLaidOff = 0; 
        numOppCardsLaidOff = 0; 


        listNumSelfTurnsToKnock = new ArrayList<>();
        listNumOppTurnsToKnock = new ArrayList<>();
        
        listNumSelfFaceUpCardsDrawn = new ArrayList<>();
        listNumOppFaceUpCardsDrawn = new ArrayList<>();

        listNumSelfMeldsAtKnock = new ArrayList<>();
        listNumSelfRunMeldsAtKnock = new ArrayList<>();
        listNumSelfRankMeldsAtKnock = new ArrayList<>();

        listNumOppMeldsAtKnock = new ArrayList<>();
        listNumOppRunMeldsAtKnock = new ArrayList<>();
        listNumOppRankMeldsAtKnock = new ArrayList<>();
    }
    public static void performanceTrackerTest(){
        ParamList params = new ParamList(new double[] {});

        SimpleFakeGinRummyPlayer p0 = new SimpleFakeGinRummyPlayer(params);
        SimpleFakeGinRummyPlayer p1 = new SimpleFakeGinRummyPlayer(params);

        GinRummyGame game = new GinRummyGame(p0, p1);
        GinRummyGame.setPlayVerbose(true);
        game.play();
        // game.play();
        
        System.out.println("------------------------- Parameters updated for every game (initiate in constructor)-------------------------");
        System.out.println("num round: " + p0.myPerformanceTracker.numRounds);

        System.out.println("numSelfKnocks: " + p0.myPerformanceTracker.numSelfKnocks);
        System.out.println("numOppKnocks: " + p0.myPerformanceTracker.numOppKnocks);

        System.out.println("numSelfUndercuts: " + p0.myPerformanceTracker.numSelfUndercuts);
        System.out.println("numOppUndercuts: " + p0.myPerformanceTracker.numOppUndercuts);

        System.out.println("numSelfGins: " + p0.myPerformanceTracker.numSelfGins);

        System.out.println("numSelfCardsLaidOff: " + p0.myPerformanceTracker.numSelfCardsLaidOff);
        System.out.println("numOppCardsLaidOff: " + p0.myPerformanceTracker.numOppCardsLaidOff);

        

        System.out.println();

        System.out.println("listNumSelfTurnsToKnock: " + p0.myPerformanceTracker.listNumSelfTurnsToKnock);
        System.out.println("listNumOppTurnsToKnock: " + p0.myPerformanceTracker.listNumOppTurnsToKnock);

        System.out.println("listNumSelfFaceUpCardsDrawn: " + p0.myPerformanceTracker.listNumSelfFaceUpCardsDrawn);
        System.out.println("listNumOppFaceUpCardsDrawn: " + p0.myPerformanceTracker.listNumOppFaceUpCardsDrawn);

        System.out.println("listNumSelfMeldsAtKnock: " + p0.myPerformanceTracker.listNumSelfMeldsAtKnock);
        System.out.println("listNumSelfRunMeldsAtKnock: " + p0.myPerformanceTracker.listNumSelfRunMeldsAtKnock);
        System.out.println("listNumSelfRankMeldsAtKnock: " + p0.myPerformanceTracker.listNumSelfRankMeldsAtKnock);
        
        System.out.println("listNumOppMeldsAtKnock: " + p0.myPerformanceTracker.listNumOppMeldsAtKnock);
        System.out.println("listNumOppRunMeldsAtKnock: " + p0.myPerformanceTracker.listNumOppRunMeldsAtKnock);
        System.out.println("listNumOppRankMeldsAtKnock: " + p0.myPerformanceTracker.listNumOppRankMeldsAtKnock);

        System.out.println();


        System.out.println("------------------------- Parameters updated for every round (initiate in start game)-------------------------");
        System.out.println("numSelfTurnsToKnock: " + p0.myPerformanceTracker.numSelfTurnsToKnock);
        System.out.println("numOppTurnsToKnock: " + p0.myPerformanceTracker.numOppTurnsToKnock);

        System.out.println("numOppFaceUpCardsDrawn: " + p0.myPerformanceTracker.numOppFaceUpCardsDrawn);
        System.out.println("numSelfFaceUpCardsDrawn: " + p0.myPerformanceTracker.numSelfFaceUpCardsDrawn);

        System.out.println("numSelfMeldsAtKnock: " + p0.myPerformanceTracker.numSelfMeldsAtKnock);
        System.out.println("numSelfRunMeldsAtKnock: " + p0.myPerformanceTracker.numSelfRunMeldsAtKnock);
        System.out.println("numSelfRankMeldsAtKnock: " + p0.myPerformanceTracker.numSelfRankMeldsAtKnock);
    
        System.out.println("numOppMeldsAtKnock: " + p0.myPerformanceTracker.numOppMeldsAtKnock);
        System.out.println("numOppRunMeldsAtKnock: " + p0.myPerformanceTracker.numOppRunMeldsAtKnock);
        System.out.println("numOppRankMeldsAtKnock: " + p0.myPerformanceTracker.numOppRankMeldsAtKnock);
    
    }
    public static void main(String[] args){
        performanceTrackerTest();
    }

}