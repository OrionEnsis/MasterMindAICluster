package MasterMind;

import MasterMind.AI.AI;
import MasterMind.GUI.GUI;
import MasterMind.GUI.PegResultsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ForkJoinPool;

/**
 * This class controls the interactions between the GUI, AI and Game classes.  It handles GUI events, starts the AI and
 * holds a reference to the game to enable communications.
 * @author Jim Spagnola
 */
public class Controller implements ActionListener {
    private Game game;
    private GUI gui;

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

    private void setupListeners() {
        gui.settingsPanel.startButton.addActionListener(this);
        gui.gameBoardPanel.submitTurnButton.addActionListener(this);
        gui.gameBoardPanel.guesses.forEach(g->g.pegs.forEach(p->p.addActionListener(this)));
        gui.colorsPanel.colors.forEach(b->b.addActionListener(this));
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

        if(b.getText().equals("Start")){
            startGame();
        }
    }

    private void startGame() {
        gui.settingsPanel.startButton.setEnabled(false);
        if(gui.settingsPanel.getSelected() == 0){
            gui.gameBoardPanel.submitTurnButton.setEnabled(true);
        }else{
            new Thread(this::runAI).start();

        }
    }


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
            if(!game.checkWin()) {
                updateResults(results);
                //if not game over unlock next row
                if(!game.checkLost()) {
                    gui.gameBoardPanel.unlockNextRow();
                }
                else{
                    System.out.println("Game Over");
                    gui.gameBoardPanel.revealAnswer(game.getAnswer());

                }
            }

            //else you win
            else{
                System.out.println("You win");
                gui.gameBoardPanel.revealAnswer(game.getAnswer());
            }
        }
        else{
            JOptionPane.showMessageDialog(null,"You have not finished your turn!!","Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateResults(int[] results) {
        PegResultsPanel pegPanel = gui.gameBoardPanel.getCurrentTurn().getResults();
        pegPanel.setResults(results);
    }

    private void changeHighlightedColor(JButton b){
        //if previous highlighted color, unhighlight.
        if(gui.colorsPanel.currentColor != null) {
            //set new color to highlighted.
            gui.colorsPanel.currentColor.setBorder(null);
        }

        b.setBorder(BorderFactory.createEtchedBorder(Color.GRAY,Color.BLACK));
        gui.colorsPanel.currentColor = b;
    }


    private void changePegColor(JButton peg){
        //if there is highlighted color.
        if(gui.colorsPanel.currentColor != null){
            //change peg to that color.
            peg.setBackground(gui.colorsPanel.currentColor.getBackground());
        }
    }

    private int[] getGuessFromPegs(){
        return gui.gameBoardPanel.getGuess();
    }

    //latch for AI to play
    private void submit(int[] guess){
        //ask game for result
        gui.gameBoardPanel.updateCurrentGuess(guess);
        playTurn();
    }

    private void runAI(){
        AI ai;
        ForkJoinPool pool = new ForkJoinPool();
        int[] temp = new int[game.getPegs()];

        System.out.println("AI has started");
        submit(temp);

        while(!game.checkLost() && !game.checkWin()){
            ai = new AI(game,false);
            submit(pool.invoke(ai).getPlayAsArray());
            System.out.println("Turn Submitted");
        }
    }
}
