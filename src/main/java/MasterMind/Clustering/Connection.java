package MasterMind.Clustering;

import MasterMind.AI.SinglePlay;
import MasterMind.Game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * this class represents a connection with a client.  it stores its individual socket, the name of the client and the
 * number of available cores.
 */
public class Connection implements Runnable,Comparable<Connection> {
    public static final ArrayList<Connection> queue = new ArrayList<>();
    private static final Semaphore semaphore = new Semaphore(1);
    private Socket socket;
    private ObjectOutputStream output;
    private String name;
    private int cores;
    private ObjectInputStream input;

    /**
     * the default constructor
     * @param socket the socket established by the server.
     * @throws IOException
     */
    Connection(Socket socket) throws IOException{
        this.socket = socket;
        setupStreams();
    }

    /**
     * this method sets up the input/output streams for a given connection.
     * @throws IOException
     */
    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socket.getInputStream());
        System.out.println("Streams setup");
        try {
            semaphore.acquire();
            queue.add(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            semaphore.release();
        }
    }

    @Override
    public int compareTo(Connection o) {
        return Integer.compare(cores,o.cores);
    }

    @Override
    public void run(){
        try{
            initialInfo();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * upon establishing a connection the client will send default info through the connection such as the name and cores.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void initialInfo() throws IOException, ClassNotFoundException {
        name = (String)input.readObject();
        System.out.println("name received " + name);
        cores = input.readInt();
        System.out.println("cores sent");
        System.out.println(name + " " + cores);
        output.writeObject("Thank you, " + name);
        output.flush();
        System.out.println("response sent");
    }

    /**
     * sends the current instance of the game to client.
     * @param game the current instance of the game
     */
    public void sendGame(Game game){
        try {
            System.out.println("sending game Message to " + name);
            output.writeObject("GAME");
            output.flush();
            System.out.println("sending Game");
            output.writeObject(game);
            output.flush();
            System.out.println("game turns " + game.pastGuesses.size());
            System.out.println("Game Sent");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * closes an established connection.  Do not call if the connection has already been established.
     */
    public void closeConnection(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method determines the optimal play given a range of indexes.
     * @param start the index to start calculating scores
     * @param end the end index for calculating scores
     * @return the optimal play
     */
    public SinglePlay sendPlays(int start, int end) {
        try {
            System.out.println("prepping play to " + name);
            output.writeObject("PLAY");
            output.flush();
            output.writeInt(start);
            output.flush();
            output.writeInt(end);
            output.flush();
            System.out.println("waiting for play");
            SinglePlay temp = (SinglePlay)input.readObject();
            System.out.println("received play." + temp);
            return temp;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getCores() {
        return cores;
    }

    public String getName() {
        return name;
    }
}
