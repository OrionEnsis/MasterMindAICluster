package MasterMind;

import MasterMind.GUI.GUI;
import MasterMind.GUI.PegResultsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller implements ActionListener {
    private Game game;
    private GUI gui;

    Controller(GUI gui, Game game){
        this.game = game;
        this.gui = gui;
        setupListeners();
    }

    private void setupListeners() {
        gui.settingsPanel.newGameButton.addActionListener(this);
        gui.gameBoardPanel.submitTurnButton.addActionListener(this);
        gui.gameBoardPanel.guesses.forEach(g->g.pegs.forEach(p->p.addActionListener(this)));
        gui.colorsPanel.colors.forEach(b->b.addActionListener(this));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton)e.getSource();
        if(b.getText().equals("New Game!")){
            newGame();
        }
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
        //change peg to active color.
    }

    //TODO allow for remaking of window.
    void newGame(){
        int pegs = gui.settingsPanel.getPegs();
        int guess = gui.settingsPanel.getGuesses();
        int colors = gui.settingsPanel.getColors();

        //reset(pegs,guess,colors,null);
    }

    int[] playTurn(){
        //check/verify valid submission
        int[] guess = getGuessFromPegs();
        int[] results = null;
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
                    System.out.println("Game OVer");
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
            //TODO invalid error handling.
        }
        return results;
    }

    private void updateResults(int[] results) {
        PegResultsPanel pegPanel = gui.gameBoardPanel.getCurrentTurn().getResults();
        pegPanel.setResults(results);
    }

    void changeHighlightedColor(JButton b){
        //if previous highlighted color, unhighlight.
        if(gui.colorsPanel.currentColor != null) {
            //set new color to highlighted.
            gui.colorsPanel.currentColor.setBorder(null);
        }

        b.setBorder(BorderFactory.createEtchedBorder(Color.GRAY,Color.BLACK));
        gui.colorsPanel.currentColor = b;
    }


    void changePegColor(JButton peg){
        //if there is highlighted color.
        if(gui.colorsPanel.currentColor != null){
            //change peg to that color.
            peg.setBackground(gui.colorsPanel.currentColor.getBackground());
        }
    }

    int[] getGuessFromPegs(){
        return gui.gameBoardPanel.getGuess();
    }

    //latch for AI to play
    int[] submit(int[] guess){
        //ask game for result
        gui.gameBoardPanel.updateCurrentGuess(guess);
        return playTurn();
    }
}
