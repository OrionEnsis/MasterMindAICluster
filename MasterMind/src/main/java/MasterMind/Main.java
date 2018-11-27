package MasterMind;

import MasterMind.AI.AI;
import MasterMind.GUI.GUI;
import MasterMind.Game;

import javax.swing.*;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args){
        ForkJoinPool pool = new ForkJoinPool();
        GUI frame = new GUI();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Game game = new Game();
        Controller controller = new Controller(frame,game);
        AI ai = new AI(game);
        int[] temp = {0,0,0,1,1,1};
        controller.submit(temp);
        while(!game.checkLost()){
            controller.submit(pool.invoke(ai));
            ai.quietlyJoin();
        }
    }
}
