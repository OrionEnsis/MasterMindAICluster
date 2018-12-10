package MasterMind;

import MasterMind.GUI.GUI;
import javax.swing.*;

/**
 * This Class sets up the game.
 */
public class Main {
    public static void main(String[] args){
        Game game = new Game(15,8,4);

        GUI frame = new GUI(game);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Controller controller = new Controller(frame,game);
    }
}
