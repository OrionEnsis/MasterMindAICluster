package MasterMind.GUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class SettingsPanel extends JPanel {

    public static void main(String[] args){
        JFrame frame = new JFrame("Settings");
        frame.add( new SettingsPanel());
        frame.setLayout(null);
        frame.setSize(400,400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public SettingsPanel(){
        super();
        setSize(250,100);
        GridBagLayout bagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1;
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(bagLayout);

        JLabel pegLabel = new JLabel();
        pegLabel.setText("Number of Pegs");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(pegLabel,gridBagConstraints);

        JTextField pegField = new JTextField();
        gridBagConstraints.gridx = 1;
        add(pegField,gridBagConstraints);

        JLabel guessLabel = new JLabel();
        guessLabel.setText("Number of Guesses");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(guessLabel,gridBagConstraints);

        JTextField guessField = new JTextField();
        gridBagConstraints.gridx = 1;
        add(guessField,gridBagConstraints);

        JLabel colorsLabel = new JLabel();
        colorsLabel.setText("Number of Colors");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        add(colorsLabel,gridBagConstraints);

        JTextField colorsField = new JTextField();
        gridBagConstraints.gridx = 1;
        add(colorsField,gridBagConstraints);

        JButton newGameButton = new JButton();
        newGameButton.setText("New Game!");
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(newGameButton,gridBagConstraints);
    }

}
