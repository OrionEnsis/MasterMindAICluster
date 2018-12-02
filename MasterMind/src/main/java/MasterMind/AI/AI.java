package MasterMind.AI;

import MasterMind.Game;
import java.util.*;
import java.util.concurrent.RecursiveTask;

//TODO thought: for double depth testing have them be judged by the metric of the sum of the 2 members with bonus based on how different they are.
//TODO set up for Single play?
public class AI extends RecursiveTask<int[]> implements Comparator<SinglePlay> {

    private List<SinglePlay> allPotentialPlays = new LinkedList<>();
    private Game game;
    private static final int THRESHOLD = 5000;
    private int start;
    private int end;
    private int depth;
    private List<Rule> rules = new ArrayList<>();

    public AI(Game game){
        super();
        this.game = game;
        int totalRemainingPossibilities = (int)Math.pow((double) game.getColors(), (double) game.getPegs());
        start = 0;
        end = totalRemainingPossibilities;
        for (int i = 0; i < totalRemainingPossibilities; i++) {
            allPotentialPlays.add(new SinglePlay(i,game.getPegs()));
        }
        for (int i = 0; i < game.pastResults.size(); i++) {
            rules.add(new Rule(game.pastGuesses.get(i),game.pastResults.get(i)));
        }

    }

    private AI(Game game, int start, int end, int depth){
        this(game);
        this.start = start;
        this.end = end;
        this.depth = depth;
    }

    @Override
    protected int[] compute() {
        int result[];
        if((end-start) < THRESHOLD) {
            System.out.println("Doing Work" + (end -start));
            result = doWork();
        }else {
            System.out.println("Splitting" + (end -start));
            result = split();
        }
        return result;
    }

    private int[] split() {
        AI ai1 = new AI(game,start,end/2,depth-1);
        AI ai2 = new AI(game,end/2,end,depth-1);
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
        SinglePlay currentWinner = new SinglePlay(-1,game.getPegs());
        int highScore = -1;
        if(game.pastGuesses.size()>rules.size()){
            for (int i =0; i < game.pastGuesses.size(); i++) {
                if(i >= rules.size())
                    rules.add(new Rule(game.pastGuesses.get(i),game.pastResults.get(i)));
            }
        }
        outer: for(int i = start; i < end; i++) {
            //get the rules for scoring this round.
            for (Rule r : rules) {
                if (!r.followsRules(allPotentialPlays.get(i).getPlayAsArray())){
                    System.out.println("invalid member found");
                    continue outer;
                }
            }

            //score them all. //TODO add depth searching by getting iterating through all valid options.  Award points for each different peg this possibility allows.
            allPotentialPlays.get(i).determineScore();

            //compare to current leader...smaller one wins
            if(highScore < allPotentialPlays.get(i).getScore()){
                currentWinner = allPotentialPlays.get(i);
                highScore = currentWinner.getScore();}
//            }else if(highScore == allPotentialPlays.get(i).getScore() && Math.random() %2 == 0 ){
//                currentWinner = allPotentialPlays.get(i);
//            }
        }
        //return smallest
        return currentWinner.getPlayAsArray();
    }

    private static double score(int[] entry){
        //for matching all possibilities/colors^correct
        //for right color result/pegs^almost
        return 0;
    }

    @Override
    public int compare(SinglePlay o1, SinglePlay o2) {
        return Integer.compare(o1.getScore(),o2.getScore());
    }
}
