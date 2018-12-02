package MasterMind.AI;

import MasterMind.Game;
import java.util.*;
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
    }
    public void addRule(Rule rule){
        rules.add(rule);
        removeInvalid(rule);
    }

    void removeInvalid(Rule rule) {
        //allPotentialPlays.parallelStream().forEach(x-> rule.followsRules(x.getPlayAsArray()));
        allPotentialPlays = allPotentialPlays.stream().filter(x-> rule.followsRules(x.getPlayAsArray())).collect(Collectors.toList());
    }

    private AI(Game game, int start, int end, int depth){
        this(game);
        this.start = start;
        this.end = end;
        this.depth = depth;
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

    private SinglePlay split() {
        int split = end - start;
        split = split/2;
        AI ai1 = new AI(game,start,start+split,depth-1);
        AI ai2 = new AI(game,start+split,end,depth-1);
        ai1.fork();

        SinglePlay ai2Result = ai2.compute();
        SinglePlay ai1Result = ai1.join();

        if(score(ai1Result)>score(ai2Result)){
            return ai1Result;
        }
        else{
            return ai2Result;
        }
    }
    private SinglePlay doWork() {
        SinglePlay currentWinner = new SinglePlay(-1,game.getPegs());
        int highScore = -1;

        for(int i = start; i < end; i++) {
            //get the rules for scoring this round.
            //System.out.println(i + " valid");
            //score them all. //TODO add depth searching by getting iterating through all valid options.  Award points for each different peg this possibility allows.
            allPotentialPlays.get(i).determineScore();

            //compare to current leader...smaller one wins
            if(highScore < allPotentialPlays.get(i).getScore()){
                currentWinner = allPotentialPlays.get(i);
                highScore = currentWinner.getScore();
            }
        }
        //return smallest
        return currentWinner;
    }

    private static double score(SinglePlay entry){
        //for matching all possibilities/colors^correct
        //for right color result/pegs^almost
        return 0;
    }

    @Override
    public int compare(SinglePlay o1, SinglePlay o2) {
        return Integer.compare(o1.getScore(),o2.getScore());
    }
}
