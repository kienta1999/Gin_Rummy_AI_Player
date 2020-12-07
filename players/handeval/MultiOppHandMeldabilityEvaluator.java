package players.handeval;

import java.util.ArrayList;

import games.TestingGame;
import ginrummy.Card;
import ginrummy.GinRummyPlayer;
import ginrummy.GinRummyUtil;
import players.MeldabilityCalculator;
import players.OurSimpleGinRummyPlayer;
import players.ParamList;
import players.SimpleFakeGinRummyPlayer;
import players.StateTracker;
import util.OurUtil;

public class MultiOppHandMeldabilityEvaluator implements HandEvaluator{

    private ParamList params;
    private MeldabilityCalculator meldCalc;
    private int numCalled;
    private ArrayList<ArrayList<Card>> combOfOppHandGuessed;
    private ArrayList<Card> cardsInChosenMelds; 
    private boolean shouldNormalize;

    private static ArrayList<Double> lsOfMeldability = new ArrayList<>();

    public static final double MAX_OPP_MELDABILTY = 1000;
    private static boolean TESTING = false;
    private static boolean TIMETESTING = false;

    public MultiOppHandMeldabilityEvaluator(ParamList params){
        this.params = params;
        this.meldCalc = new MeldabilityCalculator(params);
        this.numCalled = 0;
        this.combOfOppHandGuessed = null;
        this.cardsInChosenMelds = null;
    }

    @Override
    public double evalHand(ArrayList<Card> cards, StateTracker myTracker, Card excludedCard){
        double start, elapsed;
        start = System.currentTimeMillis();

        this.numCalled++; // increament the number of times the method is called
        if(this.numCalled == 1){
            if(TESTING) System.out.println("The first call: generate combOfOppHandGuessed");
            int numOfCardsUnknown = StateTracker.HAND_SIZE - myTracker.getNumOppCardsKnown();
            this.combOfOppHandGuessed = OurUtil.multiOppHand(myTracker, (int)params.get(ParamList.OM_NUM_OF_ADDITIONAL_CARDS), numOfCardsUnknown);    
            
            if(TESTING) System.out.println("generate self's best meld set");
            ArrayList<Card> selfHand = (ArrayList<Card>)cards.clone();
            selfHand.add(excludedCard);
            ArrayList<ArrayList<ArrayList<Card>>> lsOfLsOfSelfMelds = GinRummyUtil.cardsToBestMeldSets(selfHand);
            ArrayList<ArrayList<Card>> lsOfChosenSelfMelds = !lsOfLsOfSelfMelds.isEmpty() ? lsOfLsOfSelfMelds.get(0) : new ArrayList<ArrayList<Card>>();
            this.cardsInChosenMelds = new ArrayList<Card>();
            lsOfChosenSelfMelds.forEach(this.cardsInChosenMelds::addAll);
        }

        // check if excludedCard is in self's meld
        // if so we don't have to evaluate opp's meldability with the card because we will not discard it
        if(cardsInChosenMelds.contains(excludedCard)){
            if(TESTING) System.out.println("ExcludedCard " + excludedCard + " is in self's meld");
            if(this.numCalled == 11){
                if(TESTING) System.out.println("The 11th call: reset the variables");
                this.numCalled = 0;
                this.combOfOppHandGuessed = null;
                this.cardsInChosenMelds = null;
            }
            elapsed = System.currentTimeMillis() - start;
            if(TIMETESTING) System.out.println("Elapsed time (ms): " + elapsed + " with " + excludedCard); // /1000 converts from ms to s    
            return 0;
        }

        double sumOfExpectedMeldability = java.util.stream.DoubleStream.iterate(0, i -> i + 1)
                                                                       .limit(this.combOfOppHandGuessed.size())
                                                                       .parallel()
                                                                       .map(h -> calcExpectedMeldability(myTracker, combOfOppHandGuessed.get((int)h), excludedCard))
                                                                       .sum();

        // double sumOfExpectedMeldability = 0;
        // for(ArrayList<Card> oppHandGuessed: combOfOppHandGuessed){
        //     double handProb = OurUtil.calcOppHandProbability(oppHandGuessed, myTracker);
        //     elapsed = System.currentTimeMillis() - start;
        //     ArrayList<Card> oppHandGuessedClone = (ArrayList<Card>)oppHandGuessed.clone();
        //     oppHandGuessedClone.add(excludedCard);
        //     if(TESTING) System.out.println("One possible hand is: " + oppHandGuessedClone);
        //     double handMeldability = meldCalc.oppHandMeldability(oppHandGuessedClone, myTracker);
        //     if(TESTING) {
        //         System.out.println("The Probability of the hand is: " + handProb);
        //         System.out.println("The Meldability of the hand is: " + handMeldability);
        //         System.out.println();
        //     }
        //     sumOfExpectedMeldability += handMeldability * handProb; 
        // }

        if(numCalled == 11){
            if(TESTING) System.out.println("The 11th call: reset the variables");
            this.numCalled = 0;
            this.combOfOppHandGuessed = null;
            this.cardsInChosenMelds = null;
        }

        if(TESTING) System.out.println("Sum of Expected Meldability: " + sumOfExpectedMeldability);
        
        elapsed = System.currentTimeMillis() - start;
        if(TIMETESTING) System.out.println("Elapsed time (ms): " + elapsed + " with " + excludedCard); 

        lsOfMeldability.add(sumOfExpectedMeldability);

        if(shouldNormalize)
            return 1 - sumOfExpectedMeldability/MAX_OPP_MELDABILTY;
        else
            return Double.MAX_VALUE - sumOfExpectedMeldability;
    }

    public double calcExpectedMeldability(StateTracker myTracker, ArrayList<Card> oppHandGuessed, Card excludedCard){
        double handProb = OurUtil.calcOppHandProbability(oppHandGuessed, myTracker);
        ArrayList<Card> oppHandGuessedClone = (ArrayList<Card>)oppHandGuessed.clone();
        oppHandGuessedClone.add(excludedCard);
        if(TESTING) System.out.println("One possible hand is: " + oppHandGuessedClone);
        double handMeldability = meldCalc.oppHandMeldability(oppHandGuessedClone, myTracker);
        if(TESTING) {
            System.out.println("The Probability of the hand is: " + handProb);
            System.out.println("The Meldability of the hand is: " + handMeldability);
            System.out.println();
        }
        return handMeldability * handProb;
    }

    @Override
    public void setParamList(ParamList params){
        this.params = params;
        meldCalc.setParamList(params);
    }

    @Override
    public boolean hasDifferentParamList(ParamList otherParams) {
        return (this.params != otherParams) || (meldCalc.getParamList() != otherParams);
    }

    public void setShouldNormalize(boolean shouldNormalize){
        this.shouldNormalize = shouldNormalize;
    }

    public static void setTesting(boolean testing){
        TESTING = testing;
    }

    public static void setTimeTesting(boolean testing){
        TIMETESTING = testing;
    }

    public static void testChoose10From11(){
        EnsembleHandEvalPlayer p;
        ArrayList<Card> hand;
        Card result;

        EnsembleHandEvalPlayer.setTesting(true);
        EnsembleHandEvalPlayer.setEvalTesting(false);
        IndexEnsembleHandEvalPlayer.setTesting(true);

        System.out.println("=============== testChoose10From11 ===============");
        double[] ensembleWeights = new double[] {3};
        ParamList params = new ParamList(ensembleWeights);
        params.set(ParamList.OM_NUM_OF_ADDITIONAL_CARDS, 1);
        params.enforceRestrictions();

        // p = new IndexEnsembleHandEvalPlayer(params, new MultiOppHandMeldabilityEvaluator(params));
        p = new EnsembleHandEvalPlayer(params, new MultiOppHandMeldabilityEvaluator(params));

        System.out.println("---------- Trial 1 ----------");
        p.getStateTracker().setToHardcodedStateTracker1();
        hand = p.getStateTracker().getSelfHandForHardcodedStateTracker1();
        // our player has {"2S", "3S", "4S", "5S", "6S", "9C", "9H", "9S", "4C", "AD"}
        // opp has {"8D", "9D", "TD", "AS", "AH", "5C", "5D", "3D", "6H", "2D"}
        
        Card card = Card.strCardMap.get("7S");
        hand.add(card); // adds on to the run meld
        p.getStateTracker().getMatrix()[card.getSuit()][card.getRank()][StateTracker.STATE] = StateTracker.SELF_FROM_STOCK;
        p.getStateTracker().getMatrix()[card.getSuit()][card.getRank()][StateTracker.PROB_IN_STOCK] = StateTracker.KNOW_WHERE_IT_IS;

        setTesting(true);
        System.out.println("********** To verify the combinatin of opp's hand, make setTesting below true ********* ");
        OurUtil.setTesting(true);
        result = p.choose10From11Cards(hand);
        System.out.println("choose10From11Cards returned: " + result);

        System.out.println();
        System.out.println("---------- Trial 2 ----------");
        p.getStateTracker().setToHardcodedStateTracker3();
        hand = p.getStateTracker().getSelfHandForHardcodedStateTracker3();
        // our player has {"2H", "3H", "4H", "AS", "2S", "3S", "7C", "7S", "7D", "4C"}
        // opp has {"6H", "7H", "8H", "9C", "9H", "9S", "9D", "AH", "AD", "2D"}
        
        card = Card.strCardMap.get("AC");
        hand.add(card);
        p.getStateTracker().getMatrix()[card.getSuit()][card.getRank()][StateTracker.STATE] = StateTracker.SELF_FROM_STOCK;
        p.getStateTracker().getMatrix()[card.getSuit()][card.getRank()][StateTracker.PROB_IN_STOCK] = StateTracker.KNOW_WHERE_IT_IS;
        setTesting(true);
        System.out.println("********** To verify the combinatin of opp's hand, make setTesting below true ********* ");
        OurUtil.setTesting(true);
        result = p.choose10From11Cards(hand);
        System.out.println("choose10From11Cards returned: " + result);
    }

    public static void testEfficiency(){
        EnsembleHandEvalPlayer p;
        ArrayList<Card> hand;
        Card result;

        setTesting(false);
        EnsembleHandEvalPlayer.setTesting(false);
        EnsembleHandEvalPlayer.setEvalTesting(false);
        IndexEnsembleHandEvalPlayer.setTesting(false);

        System.out.println("=============== testChoose10From11 ===============");
        double[] ensembleWeights = new double[] {3};
        ParamList params = new ParamList(ensembleWeights);
        params.set(ParamList.OM_NUM_OF_ADDITIONAL_CARDS, 3);
        params.enforceRestrictions();

        // p = new IndexEnsembleHandEvalPlayer(params, new MultiOppHandMeldabilityEvaluator(params));
        p = new EnsembleHandEvalPlayer(params, new MultiOppHandMeldabilityEvaluator(params));

        // self has no melds so computation cost should be the largest
        ArrayList<Card> selfHand = OurUtil.makeHand(new String[] {"8C", "5C", "7C", "4C", "8S", "8D", "JD", "QS", "KD", "KS"});
        Card[] cards = new Card[10];
        for(int i = 0; i < cards.length; i++) cards[i] = selfHand.get(i);
        p.startGame(0, 0, cards);
        System.out.println(p.getStateTracker());
        
        // this doesn't form melds either
        Card faceUp = Card.strCardMap.get("2H");
        selfHand.add(faceUp);
        p.getStateTracker().updateFromWillDrawFaceUpCard(faceUp, true);
        p.reportDraw(0, faceUp);

        int num = 3;
        Card discardCard;
        double start, elapsed;
        start = System.currentTimeMillis();
        for(int i = 0; i < num; i++)
            discardCard = p.getDiscard();
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Total elapsed time (s): " + elapsed/1000);
    }

    public static double playNGamesLoop(int n){
        System.out.println("In playNGames: " + n);
        double start, elapsed;
        start = System.currentTimeMillis();
        GinRummyPlayer p0 = new OurSimpleGinRummyPlayer();
        ParamList dummy = ParamList.getRandomParamList(1);
        EnsembleHandEvalPlayer p1 = new EnsembleHandEvalPlayer(dummy, new MultiOppHandMeldabilityEvaluator(dummy));
        TestingGame game = new TestingGame(p0, p1);

        int currWins = 0;

        for(int i = 0; i < n; i++){
            // ParamList params = ParamList.getRandomParamList(1);
            // p1.setParamList(params);
            int winner = game.play();
            if(winner == 1)
                currWins++;
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Elapsed time (s) to run " + n + " games: " + elapsed/1000);

        double sum = 0;                                              
        for(Double e: lsOfMeldability)
            sum += e;
        double mean = sum/lsOfMeldability.size();

        System.out.println(lsOfMeldability);
        System.out.println("Size: " + lsOfMeldability.size());

        double variance = 0;
        for(Double e: lsOfMeldability)
            variance += (e - mean) * (e - mean);
        variance /= lsOfMeldability.size();
        double std = Math.sqrt(variance);

        System.out.println("Mean: " + mean);
        System.out.println("Variance: " + variance);
        System.out.println("Standard deviation: " + std);

        return currWins / ((double) n);
    }

    public static void main (String[] args){
        // testChoose10From11();

        // MultiOppHandMeldabilityEvaluator.setTimeTesting(true);
        // OurUtil.setTimeTesting(true);
        // testEfficiency();

        // playNGamesLoop(100);
    }
    
}