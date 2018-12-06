package MasterMind.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameBoardPanel extends JPanel {
    public List<SingleGuessPanel> guesses;
    private AnswerPanel answerPanel;
    public JButton submitTurnButton;
    private int currentGuess;
    private int pegs;

    public static void main(String[] args){
        JFrame frame = new JFrame("GameBoard");
        frame.add( new GameBoardPanel(10,4));
        frame.setLayout(null);
        frame.setSize(400,1200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    GameBoardPanel(int maxGuesses,int pegs){
        super();
        guesses = new ArrayList<>();
        answerPanel = new AnswerPanel(pegs);
        currentGuess = 0;
        this.pegs = pegs;

        setBorder(BorderFactory.createEtchedBorder());
        setSize(220,54*maxGuesses);

        GridLayout g = new GridLayout(maxGuesses+2,1);
        g.setVgap(2);
        setLayout(g);

        for (int i = 0; i < maxGuesses; i++) {
            SingleGuessPanel s = new SingleGuessPanel(pegs);
            add(s);
            guesses.add(s);
            if(i != 0){
                Arrays.stream(s.getComponents()).forEach(c->{
                    c.setEnabled(false);
                    if(c instanceof JButton){
                        c.setBackground(Color.GRAY);
                    }
                });
            }
        }

        add(answerPanel);
        JButton b = new JButton();
        b.setText("Submit");
        b.setEnabled(true);
        add(b);
        submitTurnButton = b;
        submitTurnButton.setEnabled(false);
    }

    public SingleGuessPanel getCurrentTurn(){
        return guesses.get(currentGuess);
    }

    public int getCurrentGuess() {
        return currentGuess;
    }
    public void lockCurrentRow(){
        SingleGuessPanel singleGuessPanel = guesses.get(currentGuess);
        singleGuessPanel.pegs.forEach(b->b.setEnabled(false));
    }

    public void unlockNextRow(){
        currentGuess++;
        SingleGuessPanel singleGuessPanel = guesses.get(currentGuess);
        singleGuessPanel.pegs.forEach(b->{
            b.setEnabled(true);
            b.setBackground(Color.WHITE);
        });

    }

    public void updateCurrentGuess(int[] guess){
        getCurrentTurn().setPegs(guess);
    }

    public int[] getGuess(){
        int[] guess = new int[pegs];
        List<JButton> pegs = guesses.get(currentGuess).pegs;
        Arrays.fill(guess,-1);
        for (int i = 0; i < pegs.size(); i++) {
            for (int j = 0; j < GUI.COLORS.length; j++) {
                if(pegs.get(i).getBackground().equals(GUI.COLORS[j])){
                    guess[i] = j;
                    break;
                }
            }
        }
        return guess;
    }

    public void revealAnswer(int[] answer){
        answerPanel.revealAnswers(answer);
    }
}
