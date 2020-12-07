package players;

import ga.GATuner;
import games.TestingGame;
import ginrummy.SimpleGinRummyPlayer;
import players.handeval.AceTwoBonusHandEvaluator;
import players.handeval.ConvHandEvaluator;
import players.handeval.DeadwoodHandEvaluator;
import players.handeval.EnsembleHandEvalPlayer;
import players.handeval.IndexEnsembleHandEvalPlayer;
import players.handeval.LinearDeadwoodPenaltyHandEvaluator;
import players.handeval.MeldabilityHandEvaluator;
import players.handeval.MultiOppHandMeldabilityEvaluator;
import players.handeval.OppCardsKnownDeadwoodPenaltyHandEvaluator;
import players.handeval.TwoStageDrawDecider;
import players.handeval.TwoStageKnockDecider;

public class DePauwIndexPlayer extends IndexEnsembleHandEvalPlayer {

    private static LinearDeadwoodPenaltyHandEvaluator linearHe;
    private static OppCardsKnownDeadwoodPenaltyHandEvaluator oppCardsHe; 
    private static MeldabilityHandEvaluator mhe;
    private static ConvHandEvaluator che;
    private static ParamList params;

    static {
        double[] p_it2_6E = new double[] {0.4, 0.4, 0.2, 0.35, 0.9, 0.4, 0.6, 0.05, 0.5, 0.4, 0.6, 0.8380616650330837, 0.06152068390417398, 0.38258062302368523, 0.39810496473742085, 0.10844666639491507, 0.37763392278384067, 0.2706576839587317, 0.2077890824329678, 0.47922119839098565, 1.0, 1.0, 0.0, 2.75, 0.0, 2.0, 10.0, 0.0, 6.0, 8.0, 14.0, 0.2, 0.1, 0.0, 0.1, 0.5, 0.1};
        params = new ParamList(p_it2_6E, 6);

        linearHe = new LinearDeadwoodPenaltyHandEvaluator(params);
        oppCardsHe = new OppCardsKnownDeadwoodPenaltyHandEvaluator(params);

        mhe = new MeldabilityHandEvaluator(params);
        che = new ConvHandEvaluator(params);
        //mhe.setShouldNormalize(true);
        //che.setShouldNormalize(true);
    }

    public DePauwIndexPlayer() {
        super(params, 
                mhe, 
                new DeadwoodHandEvaluator(), 
                new AceTwoBonusHandEvaluator(), 
                che, 
                linearHe, 
                oppCardsHe);

        linearHe.setEnsemblePlayer(this);
        oppCardsHe.setEnsemblePlayer(this);
        this.setKnockDecider(new TwoStageKnockDecider(params));
        this.setDrawDecider(new TwoStageDrawDecider(params));
    }
    
    public static void main(String[] args) {
        DePauwIndexPlayer p0 = new DePauwIndexPlayer();

        int opponentFlag = GATuner.OPP_IS_SIMPLE_PLAYER;
        int oppKnockDeciderFlag = -1;
        int oppDrawDeciderFlag = -1;
        SimpleFakeGinRummyPlayer p1 = GATuner.setupOpp(opponentFlag, oppKnockDeciderFlag, oppDrawDeciderFlag);

        TestingGame gameManager = new TestingGame(p0, p1);
        TestingGame.setPlayVerbose(false);
        int p0Wins = 0;
        int gamesPerIndividual = 5000;

        for(int j = 0; j < gamesPerIndividual; j++){
            int winner = gameManager.play();
            if (winner == 0)
                p0Wins += 1; 
        }

        double winRate = (double) p0Wins / gamesPerIndividual;
        System.out.println("winRate: " + winRate);
    }
    
}
