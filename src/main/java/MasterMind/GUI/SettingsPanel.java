package MasterMind.GUI;


import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private ButtonGroup buttonGroup;
    private JRadioButton playerButton;
    private JRadioButton aiButton;

    public JButton startButton;

    public static void main(String[] args){
        JFrame frame = new JFrame("Settings");
        frame.add( new SettingsPanel());
        frame.setLayout(null);
        frame.setSize(400,400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

     SettingsPanel(){
        super();
        setSize(250,100);
        //setup the grid layout
        GridBagLayout bagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1;
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(bagLayout);

        buttonGroup = new ButtonGroup();

        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 1;

        playerButton = new JRadioButton("Player");
        add(playerButton,gridBagConstraints);
        buttonGroup.add(playerButton);
        playerButton.setSelected(true);

        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 2;
        aiButton = new JRadioButton("AI");
        add(aiButton,gridBagConstraints);

        buttonGroup.add(this.aiButton);

        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.gridwidth = 4;
        startButton = new JButton("Start");
        add(startButton,gridBagConstraints);

    }

    public int getSelected(){
        if(playerButton.isSelected()){
            return 0;
        }
        else{
            return 1;
        }
    }

}
