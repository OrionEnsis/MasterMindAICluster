package MasterMind.GUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ColorsPanel extends JPanel {

    List<JButton> colors;
    public static void main(String[] args){
        JFrame frame = new JFrame("Colors");
        frame.add( new ColorsPanel(4));
        frame.setLayout(null);
        frame.setSize(400,400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public ColorsPanel(int colors){
        super();
        setSize(50,50*colors);
        this.colors = new ArrayList<>();
        for (int i = 0; i < colors; i++) {
            JButton b = new JButton();
            b.setBackground(GUI.COLORS[i]);
            this.colors.add(b);
            add(b);
        }

        GridLayout g = new GridLayout(colors,1);
        setLayout(g);
        setBorder(BorderFactory.createEtchedBorder());

    }

    //public
}
