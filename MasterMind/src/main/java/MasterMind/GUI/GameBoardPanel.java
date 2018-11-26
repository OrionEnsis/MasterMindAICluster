package MasterMind.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameBoardPanel extends JPanel {
    List<SingleGuessPanel> guesses;
    AnswerPanel answerPanel;
    int currentGuess;
    int maxGuesses;
    public static void main(String[] args){
        JFrame frame = new JFrame("GameBoard");
        frame.add( new GameBoardPanel(10,4,null));
        frame.setLayout(null);
        frame.setSize(400,1200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public GameBoardPanel(int maxGuesses,int pegs,int[] answer){
        super();
        this.maxGuesses = maxGuesses;
        guesses = new ArrayList<>();
        answerPanel = new AnswerPanel(pegs,answer);
        currentGuess = 0;

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
    }


}
