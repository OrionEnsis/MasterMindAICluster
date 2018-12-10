package MasterMind.AI;

import MasterMind.Clustering.Connection;
import MasterMind.Game;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * This class is a recursive Task that will iterate over a collection of possibilities in MasterMind.  It looks all
 * future possibilities and returns the move that will remove the most amount of elements possible.
 * @author Jim Spagnola III
 */
public class AI extends RecursiveTask<SinglePlay> implements Comparator<SinglePlay> {

    private List<SinglePlay> allPotentialPlays = new LinkedList<>();
    private Game game;
    private static final int THRESHOLD = 5000;
    private int start;
    private int end;
    private int depth;
    private boolean useNetwork = true;
    private List<Rule> rules = new ArrayList<>(); //we don't need this, but keeping it around just in case we need it.

    /**
     * The constructor.  It will look at all previous turns to add rules that remove possibilities from the list.
     * @param game the game of mastermind currently being played
     */
    public AI(Game game, boolean useNetwork){
        super();
        this.useNetwork = useNetwork;
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
        depth = 1;
    }

    private AI(Game game, int start, int end, List<SinglePlay> list){
        this.allPotentialPlays = list;
        this.start = start;
        this.end = end;
        this.game = game;
        depth = 1;
    }

    private AI(Game game,List<SinglePlay> list, int i) {
        this.game = game;
        this.allPotentialPlays = list;
        start = 0;
        end = list.size();
        depth = i;
        useNetwork = false;
    }

    @Override
    protected SinglePlay compute() {
        SinglePlay result;
        if((end-start) < THRESHOLD) {
            result = doWork();
        }else {
            result = split();
        }
        return result;
    }

    private void addRule(Rule rule){
        rules.add(rule);
        removeInvalid(rule);
    }

    private void removeInvalid(Rule rule) {
        allPotentialPlays = allPotentialPlays.parallelStream().filter(x-> rule.followsRules(x.getPlayAsArray())).collect(Collectors.toList());
        end = allPotentialPlays.size();
    }

    private SinglePlay split() {
        int split = end - start;
        split = split/2;
        AI ai1 = new AI(game,start,start+split,allPotentialPlays);
        AI ai2 = new AI(game,start+split,end,allPotentialPlays);
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
            //score them all.
            if(allPotentialPlays.get(i) == null){
                System.out.println(i);
            }

            //off load work if we can.  If not, do it ourselves.
            networkCheck(allPotentialPlays.get(i));

            //compare to current leader...smaller one wins
            if(highScore > allPotentialPlays.get(i).getScore()){
                currentWinner = allPotentialPlays.get(i);
                highScore = currentWinner.getScore();
            }
        }
        //return smallest
        return currentWinner;
    }

    //intellij thinks this should be private, but we're going to use this for network stuff in a bit.
    public void determineScore(SinglePlay singlePlay) {
        if(depth != 0){
            int sum = 0;

            ArrayList<AI> ais = new ArrayList<>();
            for (int i = 0; i < game.getPegs(); i++) {

                Rule rule = new Rule(singlePlay.getPlayAsArray(),i,0);
                List<SinglePlay> play = allPotentialPlays.parallelStream().filter(x-> rule.followsRules(x.getPlayAsArray())).collect(Collectors.toList());
                AI task = new AI(game,play,depth-1);
                ais.add(task);
                task.fork();
            }
            for (AI a :
                    ais) {
                sum += a.join().getScore();
            }
            //get the sum
            singlePlay.setScore(sum);
        }
        else{
            singlePlay.setScore(1);
        }
    }

    private void networkCheck(SinglePlay singlePlay){
        Connection connection = Connection.queue.poll();
        if(useNetwork && connection != null){
            connection.sendPlay(singlePlay);
            Connection.queue.offer(connection);
        }
        else{
            determineScore(singlePlay);
        }
    }
    @Override
    public int compare(SinglePlay o1, SinglePlay o2) {
        return Integer.compare(o1.getScore(),o2.getScore());
    }
}
