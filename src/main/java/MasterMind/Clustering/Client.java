package MasterMind.Clustering;

import MasterMind.AI.AI;
import MasterMind.AI.SinglePlay;
import MasterMind.Game;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

public class Client {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ForkJoinPool pool = new ForkJoinPool();
    private Socket socket;
    private Game game;

    public Client(String name){
        try {
            socket = new Socket(name,2693);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void handleInformation(){
        while(true){
            try {
                System.out.println("waiting for work");
                String temp = (String)input.readObject();
                if(temp.equalsIgnoreCase("GAME")){
                    getNewGame();
                }else if(temp.equalsIgnoreCase("PLAY")){
                    calculate();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getNewGame() throws IOException, ClassNotFoundException {
        game = (Game)input.readObject();
        System.out.println("current guesses " + game.pastGuesses.size());
        System.out.println("Received Game");
    }

    public void sendPrimaryInfo(){
        try {
            output.writeObject(InetAddress.getLocalHost().getHostName());
            output.flush();
            output.writeInt(Runtime.getRuntime().availableProcessors());
            output.flush();
            String temp = (String)input.readObject();
            System.out.println("Response: " + temp);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void calculate(){
        try {
            System.out.println("Starting game calculation");
            int start = input.readInt();
            int end = input.readInt();
            AI ai = new AI(game, start, end, false);
            System.out.println("Size " + ai.allPotentialPlays.size());
            SinglePlay play = pool.invoke(ai);
            System.out.println("play calculated");
            output.writeObject(play);
            System.out.println("play sent" + play);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
