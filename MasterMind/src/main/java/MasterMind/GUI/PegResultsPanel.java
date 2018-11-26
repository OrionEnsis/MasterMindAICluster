package MasterMind.GUI;

import javax.swing.*;
import java.awt.*;

public class PegResultsPanel extends JPanel {

    public static void main(String[] args){
        JFrame frame = new JFrame("PegResultsFrame");
        frame.add( new PegResultsPanel());
        frame.setLayout(null);
        frame.setSize(400,400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public PegResultsPanel(){
        super();
        setSize(50,50);
        GridLayout gridLayout = new GridLayout(2,3);
        gridLayout.setHgap(2);
        gridLayout.setVgap(2);
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(gridLayout);
        for (int i = 0; i < 6; i++) {
            JPanel p = new JPanel();
            p.setBorder(BorderFactory.createEtchedBorder());
            add(p);
        }
    }

    public PegResultsPanel(Color[] colors){
        this();
        setResults(colors);
    }

    public void setResults(Color[] colors){
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            c[i].setBackground(colors[i]);
        }
    }
}