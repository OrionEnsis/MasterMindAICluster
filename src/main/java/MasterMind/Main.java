package MasterMind;

import MasterMind.Clustering.Client;
import MasterMind.Clustering.Connection;
import MasterMind.Clustering.Server;
import MasterMind.GUI.GUI;
import javax.swing.*;
import java.util.Scanner;

/**
 * This Class sets up the game.
 */
public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Client or Server?:");
        String answer = scanner.next();
        if(answer.equalsIgnoreCase("CLIENT")){
            System.out.println("Enter Name: ");
            String temp = scanner.next();
            Client client = new Client(temp);
            client.sendPrimaryInfo();
            client.handleInformation();
        }
        else {
            System.out.println("Pegs?: ");
            int pegs = scanner.nextInt();
            Game game = new Game(15, 8, pegs);

            GUI frame = new GUI(game);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> Connection.queue.forEach(Connection::closeConnection)));
            Controller controller = new Controller(frame, game);
            new Thread(() -> new Server().start()).start();
        }
    }
}
