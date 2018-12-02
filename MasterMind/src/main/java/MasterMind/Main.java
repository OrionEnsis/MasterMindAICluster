package MasterMind;

import MasterMind.AI.AI;
import MasterMind.GUI.GUI;

import javax.swing.*;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args){
        Game game = new Game(15,8,3);
        ForkJoinPool pool = new ForkJoinPool();
        GUI frame = new GUI(game);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Controller controller = new Controller(frame,game);

        AI ai;
        int[] temp = {0,2,1};//,0,1,1,1};
        controller.submit(temp);
        while(!game.checkLost()){
            ai = new AI(game);
            controller.submit(pool.invoke(ai));
            System.out.println("Turn Submitted");
            ai.quietlyJoin();
        }
    }
}
