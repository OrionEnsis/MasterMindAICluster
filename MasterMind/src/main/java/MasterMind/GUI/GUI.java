package MasterMind.GUI;

import javax.swing.*;
import java.awt.*;


public class GUI extends JFrame{
    static Color[] COLORS = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.pink};
    public GameBoardPanel gameBoardPanel;
    public ColorsPanel colorsPanel;
    public SettingsPanel settingsPanel;

    public GUI(){
        super("MasterMind");
        setSize(1000,600);
        setLayout( new GridBagLayout());
        reset(6,15,8,null);
        setupSettings();

    }
    void setupSettings(){
        settingsPanel = new SettingsPanel();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 15;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.weightx = .3;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        add(settingsPanel,gridBagConstraints);
    }
    void reset(int pegs, int guesses, int colors, int[] answer){//TODO determine parameters
        if(gameBoardPanel != null){
            remove(gameBoardPanel);
        }
        if(colorsPanel != null){
            remove(colorsPanel);
        }
        setLayout( new GridBagLayout());
        repaint();
        setupColors(colors);
        setupBoard(pegs,guesses,answer);
    }
    public void setupColors(int colors){
        colorsPanel = new ColorsPanel(colors);
        //TODO handle location.
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridheight = colors;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(colorsPanel,gridBagConstraints);
        colorsPanel.setVisible(true);

    }
    public void setupBoard(int pegs, int guesses, int[] answer){
        gameBoardPanel = new GameBoardPanel(guesses,pegs,answer);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = pegs + 2;
        gridBagConstraints.gridheight = guesses;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        //gridBagConstraints.fill = GridBagConstraints.BOTH;
        add(gameBoardPanel,gridBagConstraints);
    }

    public static void main(String[] args){
        GUI frame = new GUI();
        frame.setVisible(true);
        //frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
