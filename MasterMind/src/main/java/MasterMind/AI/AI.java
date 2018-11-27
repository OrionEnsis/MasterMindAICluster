package MasterMind.AI;
import MasterMind.Game;

import java.util.concurrent.RecursiveTask;


public class AI extends RecursiveTask<int[]> {
    private Game game;
    private final double totalRemainingPossibilities;
    private static final int THRESHHOLD = 5000;
    private double startPoint;
    public AI(Game game){
        super();
        this.game = game;
        totalRemainingPossibilities = Math.pow((double) game.getColors(), (double) game.getPegs());
        startPoint = -1;
    }

    private AI(Game game, double calculatedAmount){//, int[] mustUse,int[] shouldMove){
        this(game);
        startPoint = calculatedAmount;
    }

    @Override
    protected int[] compute() {
        int result[];
        if(calcStart() < THRESHHOLD)
            result = doWork();
        else
            result = split();
        return result;
    }

    private int[] split() {
        AI ai1 = new AI(game,calcStart()/2);
        AI ai2 = new AI(game,calcStart()/2);
        ai1.fork();

        int[] ai2Result = ai2.compute();
        int[] ai1Result = ai1.join();

        if(score(ai1Result)>score(ai2Result)){
            return ai1Result;
        }
        else{
            return ai2Result;
        }
    }

    private int[] doWork() {
        //TODO find method to ensure uniqueness.
        //for every value
        //calculate minimal possibilities removed.
        //compare to current leader
        //smaller one wins
        //return smallest
        return null;
    }

    private int calcStart() {
        if(startPoint >= 0){
            return (int)startPoint;
        }else{
            //TODO look at past ones to determine score somehow
            return 0;
        }
    }

    private static double score(int[] entry){
        //for matching all possibilities/colors^correct
        //for right color result/pegs^almost
        return 0;
    }
}
