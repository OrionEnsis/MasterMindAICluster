package MasterMind.GUI;

import javax.swing.*;
import java.awt.*;

public class SingleGuessPanel extends JPanel{

    private PegResultsPanel results;
    public static void main(String[] args){
        JFrame frame = new JFrame("Single Guess");
        frame.add( new SingleGuessPanel(4));
        frame.setLayout(null);
        frame.setSize(400,400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public SingleGuessPanel(int pegs){
        super();
        setSize(200,50);
        GridLayout gridLayout = new GridLayout(1,pegs + 1);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEtchedBorder());
        for (int i = 0; i < gridLayout.getColumns()-1; i++) {
            JButton button = new JButton();
            button.setText("");
            button.setBackground(Color.WHITE);
            add(button);

        }
        setLayout(gridLayout);
        PegResultsPanel p = new PegResultsPanel();
        results = p;
        add(p);
    }

    public PegResultsPanel getResults() {
        return results;
    }
}
