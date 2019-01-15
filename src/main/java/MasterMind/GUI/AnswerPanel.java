package MasterMind.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerPanel extends JPanel {

    private List<JButton> answers;
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
        answers = new ArrayList<>();
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
            answers.add(button);
        }
        setLayout(gridLayout);
    }

    void reset(){
        answers.forEach(x->x.setBackground(Color.black));
    }
    void revealAnswers(int[] answers){
        for (int i = 0; i < answers.length; i++) {
            this.answers.get(i).setBackground(GUI.COLORS[answers[i]]);
        }
    }
}
