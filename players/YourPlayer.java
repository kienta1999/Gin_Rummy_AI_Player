package players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import ginrummy.Card;
import ginrummy.GinRummyPlayer;
import ginrummy.GinRummyUtil;

public class YourPlayer extends OurSimpleGinRummyPlayer {

    private StateTracker myTracker;
    private Scanner scanner;

    private boolean displayMatrix = true;

    public YourPlayer(ParamList params){
        this.myTracker = new StateTracker(params);
    }

    @Override
    public void startGame(int playerNum, int startingPlayerNum, Card[] cards){
        super.startGame(playerNum, startingPlayerNum, cards);
        myTracker.updateFromStartGame(playerNum, startingPlayerNum, cards);
        scanner = new Scanner(System.in);
        System.out.println("Do you want to display StateTracker matrix during the game? [y/n]");
        String answer = scanner.nextLine().trim().toLowerCase();
        while(!answer.equals("y") && !answer.equals("n")){
            System.out.println("Enter 'y' for yes or 'n' for n");
            answer = scanner.nextLine().trim().toLowerCase();
        }
        boolean displayMatrix = answer.equals("y") ? true : false;
        setDisplayMatrix(displayMatrix);
        System.out.println("You are dealt " + Arrays.asList(cards));
    }

    @Override
    public boolean willDrawFaceUpCard(Card card){
        // super.willDrawFaceUpCard(card);
        
        if(displayMatrix) System.out.println(myTracker);
        System.out.println("Do you draw the face-up card " + card + "? [y/n]");
        String answer = scanner.nextLine().trim().toLowerCase();
        while(!answer.equals("y") && !answer.equals("n")){
            System.out.println("Enter 'y' for yes or 'n' for n");
            answer = scanner.nextLine().trim().toLowerCase();
        }
        boolean willDraw = answer.equals("y") ? true : false;

        myTracker.updateFromWillDrawFaceUpCard(card, willDraw);
        return willDraw;
    }

    @Override
    public void reportDraw(int playerNum, Card drawnCard){
        super.reportDraw(playerNum, drawnCard);
        myTracker.updateFromReportDraw(playerNum, drawnCard);
        if(playerNum == this.playerNum){
            System.out.println("You drew " + drawnCard);
        }
        else{
            if(drawnCard == null){
                System.out.println("Opponent drew from deck");
            }
            else{
                System.out.println("Opponent drew the face-up card " + drawnCard);
            }
        }
    }

    @Override
    public Card getDiscard(){
        // super.getDiscard();
        ArrayList<String> strCards = new ArrayList<String>();
        for(Card card: this.cards){
            strCards.add(card.toString());
        }

        System.out.println("Which card do you want to discard?");
        System.out.println("Choose one card from your hand: " + this.cards);
        ArrayList<ArrayList<ArrayList<Card>>> bestMeldSets = GinRummyUtil.cardsToBestMeldSets(cards);
        if(!bestMeldSets.isEmpty()) System.out.println("Best Meld set is : " + bestMeldSets.get(0));
        String str = scanner.nextLine().trim().toUpperCase();
        while(!strCards.contains(str)){
            System.out.println("Card " + str + " doesn't exist in your hand. Choose again");
            str = scanner.nextLine().trim().toUpperCase();
        }

        Card discard = Card.strCardMap.get(str);
        return discard;
    }

    @Override
    public void reportDiscard(int playerNum, Card discardCard){
        super.reportDiscard(playerNum, discardCard);
        myTracker.updateFromReportDiscard(playerNum, discardCard);
        if(playerNum == this.playerNum){
            // System.out.println("You discarded " + discardCard);
        }
        else{
            // System.out.println("Opponent discarded " + discardCard);
        }
    }

    @Override
    public ArrayList<ArrayList<Card>> getFinalMelds(){
        ArrayList<ArrayList<ArrayList<Card>>> bestMeldSets = GinRummyUtil.cardsToBestMeldSets(cards);
        if (!opponentKnocked && (bestMeldSets.isEmpty() || GinRummyUtil.getDeadwoodPoints(bestMeldSets.get(0), cards) > GinRummyUtil.MAX_DEADWOOD))
            return null;
        else if(bestMeldSets.isEmpty()){
            return new ArrayList<ArrayList<Card>>();
        }
        else if(opponentKnocked){
            System.out.println("Opponent knocked");
            int index = 0;
            if(bestMeldSets.size() > 1){
                int max = bestMeldSets.size();
                System.out.println("Which set do you want to use? [1-" + max + "]");
                for(int i = 0; i < max; i++){
                    System.out.println(i + ": " + bestMeldSets.get(i));
                }

                boolean loop = true;
                while(loop){
                    try{
                        index = scanner.nextInt();
                        if(index >=0 && index < max)
                            loop = false;
                        else
                            System.out.println("Invalid index");
                    }
                    catch(InputMismatchException e){
                        System.out.println("Invalid value!");
                        scanner.next();
                    }
                }
            }
            return bestMeldSets.get(index);
        }
        else{
            System.out.println("Do you want to knock now? [y/n]");
            String answer = scanner.nextLine().trim().toLowerCase();
            while(!answer.equals("y") && !answer.equals("n")){
                System.out.println("Enter 'y' for yes or 'n' for n");
                answer = scanner.nextLine().trim().toLowerCase();
            }
            if(answer.equals("y")){
                int index = 0;
                if(bestMeldSets.size() > 1){
                    int max = bestMeldSets.size();
                    System.out.println("Which set do you want to use? [1-" + max + "]");
                    for(int i = 0; i < max; i++){
                        System.out.println(i + ": " + bestMeldSets.get(i));
                    }

                    boolean loop = true;
                    while(loop){
                        try{
                            index = scanner.nextInt();
                            if(index >=0 && index < max)
                                loop = false;
                            else
                                System.out.println("Invalid index");
                        }
                        catch(InputMismatchException e){
                            System.out.println("Invalid value!");
                            scanner.next();
                        }
                    }
                }
                return bestMeldSets.get(index);
            }
            else
                return null;
        }
    }

    public void closeScanner(){
        scanner.close();
    }

    public void setDisplayMatrix(boolean displayMatrix){
        this.displayMatrix = displayMatrix;
    }
}