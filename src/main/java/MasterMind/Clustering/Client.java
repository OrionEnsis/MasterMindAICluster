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

/**
 * this class represents a Client for clustering work.  It throws and catches a lot of exceptions.
 *
 * After sending default information across it will wait for a string to identify what it needs to be performed.
 * These tasks are either that it will be receiving a new game, or a new move set to calculate.
 */
public class Client {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ForkJoinPool pool = new ForkJoinPool();
    private Socket socket;
    private Game game;

    /**
     * The default constructor
     *
     * @param name the name of the server to connect to
     */
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

    /**
     * this method parses the first incoming string to decide what action it needs to perform.  GAME will give have it recieve a new
     * game instance.  PLAY will have it search for a recommended play.
     */
    public void handleInformation(){
        //while true is typically bad, but this terminates once the server is closed and it shouldn't terminate before.
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

    /**
     * this method receives a new game object from the server.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void getNewGame() throws IOException, ClassNotFoundException {
        game = (Game)input.readObject();
        System.out.println("current guesses " + game.pastGuesses.size());
        System.out.println("Received Game");
    }

    /**
     * this method sends primary info for the server, including the name of the client, and the processing power available.
     */
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

    /**
     * this method calculates a subset of possibilities and returns the results back to the server when completed.
     */
    private void calculate(){
        try {
            System.out.println("Starting game calculation");
            int start = input.readInt();
            int end = input.readInt();
            AI ai = new AI(game, start, end, false);
            System.out.println("Size " + ai.getAllPotentialPlays().size());
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
