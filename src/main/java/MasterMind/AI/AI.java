package MasterMind.AI;

import MasterMind.Clustering.Connection;
import MasterMind.Game;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * This class is a recursive Task that will iterate over a collection of possibilities in MasterMind.  It looks all
 * future possibilities and returns the move that will remove the most amount of elements possible.  It also has the ability
 * to off load sections of its work to a cluster of machines.  The Play that it returns will be the play most like to
 * remove the largest number of other possible solutions when played.
 * @author Jim Spagnola III
 */
public class AI extends RecursiveTask<SinglePlay> {

    private List<SinglePlay> allPotentialPlays = new LinkedList<>();
    private Game game;
    private static final int THRESHOLD = 1000;
    private static final Semaphore lock = new Semaphore(1);
    private int start;
    private int end;
    private int depth;
    private boolean useNetwork;

    /**
     * The constructor.  It will look at all previous turns to add rules that remove possibilities from the list.
     * @param game the game of mastermind currently being played
     * @param useNetwork whether or not we will be using clustering.
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
            removeInvalid(new Rule(game.pastGuesses.get(i),game.pastResults.get(i)));
        }
        end = allPotentialPlays.size();
        depth = 1;
    }

    /**
     * A constructor sent to recursive fork members including clustered machines.
     * @param game the game of mastermind currently being played
     * @param start the starting index to evaluate
     * @param end the last index to evaluate
     * @param useNetwork whether or not we will be using clustering.
     */
    public AI(Game game,int start, int end, boolean useNetwork){
        this(game,useNetwork);
        this.start = start;
        this.end = end;
        depth = 1;
    }

    /**
     * a constructor used to create smaller forks.
     *
     * @param game the game of mastermind currently being played
     * @param start the starting index to evaluate
     * @param end the last index to evaluate
     * @param depth how far ahead to look
     * @param list the smaller set of possible moves to use.
     * @param useNetwork whether or not we will be using clustering.
     */
    private AI(Game game, int start, int end,int depth, List<SinglePlay> list,boolean useNetwork){
        this.allPotentialPlays = list;
        this.start = start;
        this.end = end;
        this.game = game;
        this.depth = depth;
        this.useNetwork = useNetwork;

    }

    /**
     * a constructor used by forks that look forward for moves.
     * @param game the game of mastermind currently being played
     * @param list the smaller set of possible moves to use.
     * @param depth how far ahead to look
     */
    private AI(Game game,List<SinglePlay> list, int depth) {
        this.game = game;
        this.allPotentialPlays = list;
        start = 0;
        end = list.size();
        this.depth = depth;
        useNetwork = false;
    }

    /**
     * This is called when AI is invoked by a forkjoinpool.  It will recurse to divide and conquer to find an ideal solution
     *
     * @return the best play to make
     */
    @Override
    protected SinglePlay compute() {
        SinglePlay result;
        if ((end - start) < THRESHOLD) {
            result = doWork();
        } else {
            result = split();
        }
        return result;
    }

    /**
     * This method removes completely invalid members from the solution set in order to reduce redundant calculations
     * @param rule the metric to judge the remaining set by
     */
    private void removeInvalid(Rule rule) {
        allPotentialPlays = allPotentialPlays.parallelStream().filter(x-> rule.followsRules(x.getPlayAsArray())).collect(Collectors.toList());
        end = allPotentialPlays.size();
    }

    /**
     * This method creates forks of the remaining set of data into smaller sections for better parallelism.  If using the
     * network is enabled it will attempt to send its data to other cluster members.  When it is done in this manner it will
     * divide the work done among the total available cores.
     *
     * @return the best play discovered thus far
     */
    private SinglePlay split() {
        int split = end - start;
        split = split/2;
        AI ai1;
        AI ai2 = new AI(game,start+split,end,depth,allPotentialPlays,useNetwork);
        SinglePlay ai2Result;
        SinglePlay ai1Result;

        //we want to perform a different sort of split for load balancing if the network is involved.
        if(useNetwork && lock.tryAcquire() && (end - start) > Runtime.getRuntime().availableProcessors()){
            System.out.println("Network split");
            int cores = Connection.queue.stream().mapToInt(Connection::getCores).sum();
            cores += Runtime.getRuntime().availableProcessors();
            int workPerCore = (end-start)/cores;
            int coreSplit = start;

            //we need a way to run, store the FutureTasks and their results
            ExecutorService ex = null;
            if( Connection.queue.size() >0)
                 ex = Executors.newFixedThreadPool(Connection.queue.size());
            ArrayList<SinglePlay> bestPlays = new ArrayList<>();
            ArrayList<FutureTask<SinglePlay>> futureTasks = new ArrayList<>();

            //distribute work between all cores.
            for (int i = 0; i < Connection.queue.size(); i++) {
                Connection c = Connection.queue.get(i);
                int tempStart = coreSplit;
                int endTemp = tempStart + workPerCore*c.getCores();
                futureTasks.add(new FutureTask<>(() -> c.sendPlays(tempStart, endTemp)));
                coreSplit = endTemp;
                System.out.println(c.getName() + " has " + (endTemp-tempStart) + " tasks.");
            }

            //run the futures
            if( ex != null)
                for (FutureTask<SinglePlay> ft : futureTasks) {
                    ex.execute(ft);
                }

            //run ours.  PS we want to do the most proportional work on the server machine because there is less info to send
            ai1 = new AI(game,coreSplit,end,depth,allPotentialPlays,useNetwork);
            ai1Result = ai1.compute();

            //collect the futures
            for (FutureTask<SinglePlay> bestPlay : futureTasks) {
                try {
                    bestPlays.add(bestPlay.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            //get the smallest of the two
            SinglePlay play = new SinglePlay(0, allPotentialPlays.get(0).getPegs());
            play.setScore(Integer.MAX_VALUE);
            for (SinglePlay bestPlay : bestPlays) {
                if (play.getScore() == Integer.MAX_VALUE || play.getScore() < bestPlay.getScore()) {
                    play = bestPlay;
                }
            }
            ai2Result = play;
            System.out.println("Network out");
            lock.release();
        }
        //if we aren't using a network we want to keep things local splitting and joining.
        else {
            ai1 = new AI(game, start, start + split, depth, allPotentialPlays, useNetwork);
            ai1.fork();
            ai2Result = ai2.compute();
            ai1Result = ai1.join();
        }

        //regardless we still want the smallest possible set of answers
        if(ai1Result.getScore() < ai2Result.getScore()){
            return ai1Result;
        }else{
            return ai2Result;
        }
    }

    /**
     * this method calculates the optimal play iteratively through start and end positions.
     * @return the best member found in the set
     */
    private SinglePlay doWork() {
        SinglePlay currentWinner = new SinglePlay(-1,game.getPegs());
        currentWinner.setScore(Integer.MAX_VALUE);
        int highScore = Integer.MAX_VALUE;

        //score them all.
        for(int i = start; i < end; i++) {

            if(allPotentialPlays.get(i) == null){
                System.out.println(i);
            }

            //off load work if we can.  If not, do it ourselves.
            determineScore(allPotentialPlays.get(i));

            //todo check this metric for accuracy
            //compare to current leader...smaller one wins
            if(highScore > allPotentialPlays.get(i).getScore()){
                currentWinner = allPotentialPlays.get(i);
                highScore = currentWinner.getScore();
            }
        }
        if(start == end)
            System.out.println("Start equals end");
        if(currentWinner.getScore() == Integer.MAX_VALUE)
            System.out.println("SOMETHING WENT HORRIBLY WRONG");
        //return smallest
        return currentWinner;
    }

    /**
     * This method scores the play that it is given by looking at all possible results and recurse until depth is 0.  Once
     * depth is 0 it will count the remaining members at the depth.
     *
     * @param singlePlay the play to be evaluated
     */
    private void determineScore(SinglePlay singlePlay) {
        if(depth != 0){
            int sum = 0;
            ArrayList<AI> ais = new ArrayList<>();

            //for every possible branch //TODO(this needs the mays added back to it)
            for (int i = 0; i < game.getPegs(); i++) {

                //look ahead with the give result.
                Rule rule = new Rule(singlePlay.getPlayAsArray(),i,0);
                List<SinglePlay> play = allPotentialPlays.parallelStream().filter(x-> rule.followsRules(x.getPlayAsArray())).collect(Collectors.toList());
                AI task = new AI(game,play,depth-1);
                ais.add(task);
                task.fork();
            }
            //get our results back
            for (AI a :
                    ais) {
                a.join();
                sum += a.getAllPotentialPlays().stream().mapToInt(SinglePlay::getScore).sum();
            }
            //get the sum
            singlePlay.setScore(sum);
        }
        else{
            singlePlay.setScore(getAllPotentialPlays().size());
        }
    }

    /**
     * returns the list of all potential plays
     * @return all remaining legal moves
     */
    public List<SinglePlay> getAllPotentialPlays() {
        return allPotentialPlays;
    }
}
