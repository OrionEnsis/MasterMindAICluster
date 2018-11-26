package MasterMind.GUI;

import javax.swing.*;
import java.awt.*;

public class AnswerPanel extends JPanel {
    int pegs;
    private int[] answers;
    public static void main(String[] args){
        JFrame frame = new JFrame("Answer");
        frame.add( new AnswerPanel(4));
        frame.setLayout(null);
        frame.setSize(400,400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    AnswerPanel(int pegs){
        super();
        this.pegs = pegs;
        setSize(200,50);
        GridLayout gridLayout = new GridLayout(1,pegs + 1);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEtchedBorder());
        for (int i = 0; i < gridLayout.getColumns()-1; i++) {
            JButton button = new JButton();
            button.setText("");
            button.setBackground(Color.BLACK);
            add(button);
            button.setEnabled(false);
            button.setContentAreaFilled(true);
        }
        setLayout(gridLayout);
    }

    public AnswerPanel(int pegs, int[] answers){
        this(pegs);
        this.answers = answers;
    }

    public int[] checkAnswers(int[] answers){
        //TODO implement
        return null;
    }
}
