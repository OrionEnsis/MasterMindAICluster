package MasterMind;

import MasterMind.AI.AI;
import MasterMind.GUI.GUI;

import javax.swing.*;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args){
        Game game = new Game(15,8,5);
        ForkJoinPool pool = new ForkJoinPool();
        GUI frame = new GUI(game);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Controller controller = new Controller(frame,game);

        AI ai;
        int[] temp = {0,0,0,0,0};//,1,1,1};
        controller.submit(temp);
        while(!game.checkLost() && !game.checkWin()){
            ai = new AI(game);
            System.out.println(ai.allPotentialPlays.size());
            controller.submit(pool.invoke(ai).getPlayAsArray());
            System.out.println("Turn Submitted");
            ai.quietlyJoin();
        }
    }
}
