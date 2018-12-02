package MasterMind.AI;

import MasterMind.Game;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

//TODO thought: for double depth testing have them be judged by the metric of the sum of the 2 members with bonus based on how different they are.
//TODO set up for Single play?
public class AI extends RecursiveTask<SinglePlay> implements Comparator<SinglePlay> {

    public  List<SinglePlay> allPotentialPlays = new LinkedList<>();
    private Game game;
    private static final int THRESHOLD = 5000;
    private int start;
    private int end;
    private int depth;
    private List<Rule> rules = new ArrayList<>();

    public AI(Game game){
        super();
        this.game = game;
        int totalRemainingPossibilities = (int)Math.pow((double) game.getColors(),(double) game.getPegs());
        start = 0;
        for (int i = 0; i < totalRemainingPossibilities; i++) {
            allPotentialPlays.add(new SinglePlay(i,game.getPegs()));
        }
        for (int i = 0; i < game.pastResults.size(); i++) {
            addRule(new Rule(game.pastGuesses.get(i),game.pastResults.get(i)));

        }
        end = allPotentialPlays.size();
        depth = 2;
    }


    private AI(Game game, int start, int end){
        this(game);
        this.start = start;
        this.end = end;
        this.depth = depth;
    }

    public AI(Game game, int i) {
        this(game);
        depth = i;
    }

    @Override
    protected SinglePlay compute() {
        SinglePlay result;
        if((end-start) < THRESHOLD) {
            System.out.println("Doing Work " + (end -start));
            result = doWork();
        }else {
            System.out.println("Splitting " + (end -start));
            result = split();
        }
        return result;
    }

    public void addRule(Rule rule){
        rules.add(rule);
        removeInvalid(rule);
    }

    void removeInvalid(Rule rule) {
        //allPotentialPlays.parallelStream().forEach(x-> rule.followsRules(x.getPlayAsArray()));
        allPotentialPlays = allPotentialPlays.stream().filter(x-> rule.followsRules(x.getPlayAsArray())).collect(Collectors.toList());
    }

    private SinglePlay split() {
        int split = end - start;
        split = split/2;
        AI ai1 = new AI(game,start,start+split);
        AI ai2 = new AI(game,start+split,end);
        ai1.fork();

        SinglePlay ai2Result = ai2.compute();
        SinglePlay ai1Result = ai1.join();

        if(ai1Result.getScore() > ai2Result.getScore()){
            return ai1Result;
        }
        else{
            return ai2Result;
        }
    }
    private SinglePlay doWork() {
        SinglePlay currentWinner = new SinglePlay(-1,game.getPegs());
        int highScore = Integer.MAX_VALUE;

        for(int i = start; i < end; i++) {
            //get the rules for scoring this round.
            //System.out.println(i + " valid");
            //score them all. //TODO add depth searching by getting iterating through all valid options.  Award points for each different peg this possibility allows.
            determineScore(allPotentialPlays.get(i));

            //compare to current leader...smaller one wins
            if(highScore > allPotentialPlays.get(i).getScore()){
                currentWinner = allPotentialPlays.get(i);
                highScore = currentWinner.getScore();
            }
        }
        //return smallest
        return currentWinner;
    }

    private void determineScore(SinglePlay singlePlay) {
        if(depth != 0){
            int sum = 0;
             //ForkJoinPool forkJoinPool = new ForkJoinPool();
            //for every value of win and maybe add as new rule.
            for (int i = 0; i < game.getPegs(); i++) {
                for (int j = 0; j < game.getPegs() - i; j++) {
                    System.out.println("new Pool");
                    AI ai = new AI(game,depth-1);
                    ai.addRule(new Rule(singlePlay.getPlayAsArray(),i,j));
                    sum += ai.compute().getScore();
                    //ai.quietlyJoin();

                }
            }
            //get the sum
            singlePlay.score = allPotentialPlays.stream().mapToInt(x->x.getScore()).sum();
        }
        else{
            singlePlay.score = allPotentialPlays.size();
        }
    }

    @Override
    public int compare(SinglePlay o1, SinglePlay o2) {
        return Integer.compare(o1.getScore(),o2.getScore());
    }
}
