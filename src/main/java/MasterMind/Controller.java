package MasterMind;

import MasterMind.AI.AI;
import MasterMind.Clustering.Connection;
import MasterMind.GUI.GUI;
import MasterMind.GUI.PegResultsPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ForkJoinPool;

/**
 * This class controls the interactions between the GUI, AI and Game classes.  It handles GUI events, starts the AI and
 * holds a reference to the game to enable communications.
 * @author Jim Spagnola
 */
public class Controller implements ActionListener {
    private final int BENCHMARKTESTS = 10000;
    private Game game;
    private GUI gui;
    private long startTime;

    /**
     * The constructor.
     *
     * @param gui The user interface.
     * @param game The game of MasterMind
     */
    Controller(GUI gui, Game game){
        this.game = game;
        this.gui = gui;
        setupListeners();
    }

    /**
     * this method sets up the action listener connections to the view
     */
    private void setupListeners() {
        gui.settingsPanel.startButton.addActionListener(this);
        gui.gameBoardPanel.submitTurnButton.addActionListener(this);
        gui.gameBoardPanel.guesses.forEach(g->g.pegs.forEach(p->p.addActionListener(this)));
        gui.colorsPanel.colors.forEach(b->b.addActionListener(this));
        gui.settingsPanel.resetButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton)e.getSource();

        //if submit button was pressed
        if(b.getText().equals("Submit")){
            System.out.println("Submit pressed");
            playTurn();
        }
        //if color button was pressed
        if(gui.colorsPanel.colors.contains(b)){
            //change that to active color
            changeHighlightedColor(b);
        }

        //if peg was clicked
        if(gui.gameBoardPanel.guesses.get(gui.gameBoardPanel.getCurrentGuess()).pegs.contains(b)){
            changePegColor(b);
        }

        //if start was clicked
        if(b.getText().equals("Start")){
            startGame();
        }

        //if start was clicked
        if(b.getText().equals("Reset")){
            reset();
        }

    }

    private void reset(){
        gui.reset();
        game.reset();
    }
    /**
     * this method starts recording the time and starting the game for which ever mode it is in.
     */
    private void startGame() {
        startTime = System.nanoTime();
        gui.settingsPanel.startButton.setEnabled(false);

        //player mode
        if(gui.settingsPanel.getSelected() == 0){
            gui.gameBoardPanel.submitTurnButton.setEnabled(true);
        }
        //AI mode
        else if(gui.settingsPanel.getSelected() == 1){
            new Thread(this::runAI).start();
        }
        //AI cluster mode
        else if(gui.settingsPanel.getSelected() == 2){
            new Thread(this::runAICluster).start();
        }
        //benchmark cluster AI
        else if(gui.settingsPanel.getSelected() == 3){
            new Thread(this::benchmark).start();
        }
    }

    private void benchmark(){
        FileWriter fw;
        try {
            fw = new FileWriter("benchmark.csv");
            fw.append("Test ID");
            fw.append(',');
            fw.append("Guesses");
            fw.append(',');
            fw.append("Time");
            fw.append(',');
            fw.append('\n');
            reset();

            for (int i = 1; i <= BENCHMARKTESTS; i++) {
                startTime = System.nanoTime();
                runAICluster();
                fw.write(i+"");
                fw.append(',');
                fw.write(game.pastGuesses.size()+"");
                fw.append(',');
                fw.write(endGame()+"");
                fw.append(',');
                fw.append('\n');
                reset();
                System.out.println("Current Thread Count: " + ManagementFactory.getThreadMXBean().getThreadCount());
            }

            fw.flush();
            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            runAICluster();

        }
        finally{
            System.out.println("Benchmark Done");
        }

    }

    /**
     * this method filters checks that all information on a submitted turn is accurate.  if it is, it will submit it to the
     * game.  If it is not, it will display an error message instead.  Additionally, if a unique state is reached, IE, game is
     * won or lost, this method will inform the view of the change.
     */
    private void playTurn(){
        //check/verify valid submission
        int[] guess = getGuessFromPegs();
        int[] results;
        boolean b = true;
        for (int gues : guess) {
            if (gues == -1) {
                b = false;
                break;
            }
        }

        if(b) {
            //lock current row
            gui.gameBoardPanel.lockCurrentRow();

            //send to game
            results = game.guess(guess);

            //if no win add results
            if(game.checkWin()) {
                updateResults(results);
                //if not game over unlock next row
                if(game.checkLost()) {
                    gui.gameBoardPanel.unlockNextRow();
                }
                else{
                    System.out.println("Game Over");
                    gui.gameBoardPanel.revealAnswer(game.getAnswer());
                    endGame();
                }
            }

            //else you win
            else{
                System.out.println("You win");
                gui.gameBoardPanel.revealAnswer(game.getAnswer());
                endGame();
            }
        }
        else{
            JOptionPane.showMessageDialog(null,"You have not finished your turn!!","Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * this method ends the timer and displays the result in seconds
     */
    private double endGame() {
        long endTime = System.nanoTime();
        double time = endTime -startTime/1000000000.0;
        System.out.println("Final Time: " + time);
        return time;
    }

    /**
     * this method will update the results of the current turn that was submitted.
     * @param results the results of the submitted turn from the game.
     */
    private void updateResults(int[] results) {
        PegResultsPanel pegPanel = gui.gameBoardPanel.getCurrentTurn().getResults();
        pegPanel.setResults(results);
    }

    /**
     * this method updates the active color on the view
     * @param b the button to highlight
     */
    private void changeHighlightedColor(JButton b){
        //if previous highlighted color, unhighlight.
        if(gui.colorsPanel.currentColor != null) {
            //set new color to highlighted.
            gui.colorsPanel.currentColor.setBorder(null);
        }

        b.setBorder(BorderFactory.createEtchedBorder(Color.GRAY,Color.BLACK));
        gui.colorsPanel.currentColor = b;
    }

    /**
     * this method changes a peg on the view that is selected.
     * @param peg this peg to change
     */
    private void changePegColor(JButton peg){
        //if there is highlighted color.
        if(gui.colorsPanel.currentColor != null){
            //change peg to that color.
            peg.setBackground(gui.colorsPanel.currentColor.getBackground());
        }
    }

    /**
     * this method retrieves the current guess from the game board.
     * @return the guess from the gameboard
     */
    private int[] getGuessFromPegs(){
        return gui.gameBoardPanel.getGuess();
    }

    /**
     * this method is a latch for the AI to submit a turn to the game
     * @param guess the guess the AI has submitted
     */
    private void submit(int[] guess){
        //ask game for result
        gui.gameBoardPanel.updateCurrentGuess(guess);
        playTurn();
    }

    /**
     * this method will run the AI without using clustering had be selected.
     */
    private void runAI(){
        AI ai;
        ForkJoinPool pool = new ForkJoinPool();
        System.out.println("AI has started");

        while(game.checkLost() && game.checkWin()){
            ai = new AI(game,false);
            submit(pool.invoke(ai).getPlayAsArray());
            System.out.println("Turn Submitted");
        }
        System.out.println("AI done");
    }

    /**
     * this method will run the AI and use network connections to cluster.
     */
    private void runAICluster(){
        AI ai;
        ForkJoinPool pool = new ForkJoinPool();
        System.out.println("AI Network has started");

        while(game.checkLost() && game.checkWin()){
            Connection.queue.forEach(x->x.sendGame(game));
            ai = new AI(game,true);
            submit(pool.invoke(ai).getPlayAsArray());
            System.out.println("Turn Submitted");
        }
        System.out.println("AI done");
    }
}
