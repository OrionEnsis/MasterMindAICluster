package MasterMind.AI;

import MasterMind.Clustering.Connection;
import MasterMind.Game;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * This class is a recursive Task that will iterate over a collection of possibilities in MasterMind.  It looks all
 * future possibilities and returns the move that will remove the most amount of elements possible.
 * @author Jim Spagnola III
 */
public class AI extends RecursiveTask<SinglePlay> implements Comparator<SinglePlay> {

    public List<SinglePlay> allPotentialPlays = new LinkedList<>();
    private static int count = 1;
    private Game game;
    private static final int THRESHOLD = 5000;
    private static final Semaphore lock = new Semaphore(1);
    private int start;
    private int end;
    private int depth;
    private boolean useNetwork;
    private List<Rule> rules = new ArrayList<>(); //we don't need this, but keeping it around just in case we need it.

    /**
     * The constructor.  It will look at all previous turns to add rules that remove possibilities from the list.
     * @param game the game of mastermind currently being played
     */
    public AI(Game game, boolean useNetwork){
        super();
        count = 0;
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
    public AI(Game game,int start, int end, boolean useNetwork){
        this(game,useNetwork);
        this.start = start;
        this.end = end;
        depth = 1;
    }
    private AI(Game game, int start, int end,int depth, List<SinglePlay> list,boolean useNetwork){
        this.allPotentialPlays = list;
        this.start = start;
        this.end = end;
        this.game = game;
        this.depth = depth;
        this.useNetwork = useNetwork;

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
        if ((end - start) < THRESHOLD) {
            result = doWork();
        } else {
            //System.out.println("Split");
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
        AI ai1;
        AI ai2 = new AI(game,start+split,end,depth,allPotentialPlays,useNetwork);
        SinglePlay ai2Result;
        SinglePlay ai1Result;

        //we want to perform a different sort of split for load balancing if the network is involved.
        if(useNetwork && lock.tryAcquire()){
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
            play.setScore(-1);
            for (int i = 0; i < bestPlays.size(); i++) {
                if(play.getScore() == -1 || play.getScore() > bestPlays.get(i).getScore()){
                    play = bestPlays.get(i);
                }
            }
            ai2Result = play;
            System.out.println("Network out");
            lock.release();
        }
        //if we aren't using a network we want to keep things local splitting and joining.
        else {
            //System.out.println("Normal Split");
            ai1 = new AI(game, start, start + split, depth, allPotentialPlays, useNetwork);
            ai1.fork();
            ai2Result = ai2.compute();
            ai1Result = ai1.join();
        }

        //regardless we still want the smallest possible set of answers
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
            if(depth ==1){
                System.out.println(count + "/" + allPotentialPlays.size());
                count++;
            }
            //off load work if we can.  If not, do it ourselves.
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


    @Override
    public int compare(SinglePlay o1, SinglePlay o2) {
        return Integer.compare(o1.getScore(),o2.getScore());
    }
}
