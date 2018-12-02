package MasterMind.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SingleGuessPanel extends JPanel{
    public List<JButton> pegs;
    private PegResultsPanel results;
    public static void main(String[] args){
        JFrame frame = new JFrame("Single Guess");
        frame.add( new SingleGuessPanel(4));
        frame.setLayout(null);
        frame.setSize(400,400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    SingleGuessPanel(int pegs){
        super();
        this.pegs = new ArrayList<>();
        setSize(200,50);
        GridLayout gridLayout = new GridLayout(1,pegs + 1);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEtchedBorder());
        for (int i = 0; i < gridLayout.getColumns()-1; i++) {
            JButton button = new JButton();
            button.setText("");
            button.setBackground(Color.WHITE);
            add(button);
            this.pegs.add(button);
        }
        setLayout(gridLayout);
        PegResultsPanel p = new PegResultsPanel();
        results = p;
        add(p);
    }
    public void setPegs(int[] guess){
        for (int i = 0; i < guess.length; i++) {
            pegs.get(i).setBackground(GUI.COLORS[guess[i]]);
        }
    }
    public PegResultsPanel getResults() {
        return results;
    }
}
