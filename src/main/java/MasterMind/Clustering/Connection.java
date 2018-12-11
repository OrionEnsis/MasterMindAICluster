package MasterMind.Clustering;

import MasterMind.AI.SinglePlay;
import MasterMind.Game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

public class Connection implements Runnable,Comparable<Connection> {
    public static final ArrayList<Connection> queue = new ArrayList<>();
    private static Semaphore semaphore = new Semaphore(1);
    private Socket socket;
    private ObjectOutputStream output;
    String name;
    int cores;
    private ObjectInputStream input;

    public Connection(Socket socket) throws IOException{
        this.socket = socket;
        setupStreams();
    }

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

    public void sendGame(Game game){
        try {
            System.out.println("sending game Message to " + name);
            output.writeObject("GAME");
            output.flush();
            System.out.println("sending Game");
            output.writeObject(game);
            output.flush();
            System.out.println("Game Sent");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlay(SinglePlay play){
        try {
            System.out.println("Sending play");
            output.writeObject("PLAY");
            output.flush();
            output.writeObject(play);
            output.flush();
            SinglePlay temp = (SinglePlay)input.readObject();
            play.setScore(temp.getScore());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
}
