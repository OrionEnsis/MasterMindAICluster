package MasterMind.GUI;

import javax.swing.*;
import java.awt.*;

public class PegResultsPanel extends JPanel {
    private static Color[] resultColors = {Color.LIGHT_GRAY,Color.black,Color.WHITE};
    public static void main(String[] args){
        JFrame frame = new JFrame("PegResultsFrame");
        frame.add( new PegResultsPanel());
        frame.setLayout(null);
        frame.setSize(400,400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    PegResultsPanel(){
        super();
        setSize(50,50);
        GridLayout gridLayout = new GridLayout(2,4);
        gridLayout.setHgap(2);
        gridLayout.setVgap(2);
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(gridLayout);
        for (int i = 0; i < 8; i++) {
            JPanel p = new JPanel();
            p.setBorder(BorderFactory.createEtchedBorder());
            p.setBackground(resultColors[0]);
            add(p);
        }
    }


    public void setResults(int[] colors){
        Component[] c = getComponents();
        for (int i = 0; i < colors.length; i++) {
            c[i].setBackground(resultColors[colors[i]]);
        }
    }
}