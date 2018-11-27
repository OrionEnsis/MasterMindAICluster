package MasterMind;

import MasterMind.AI.AI;
import MasterMind.GUI.GUI;
import MasterMind.Game;

import javax.swing.*;

public class Main {
    public static void main(String[] args){
        GUI frame = new GUI();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Game game = new Game();
        Controller controller = new Controller(frame,game);
    }
}
