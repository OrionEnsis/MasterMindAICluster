package MasterMind.Clustering;

import MasterMind.AI.SinglePlay;
import MasterMind.Game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class Connection implements Runnable,Comparable<Connection> {
    public static final PriorityBlockingQueue<Connection> queue = new PriorityBlockingQueue();
    public static final ConcurrentHashMap<String, Connection> workMap = new ConcurrentHashMap<>();
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
        queue.add(this);
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
        }finally{
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initialInfo() throws IOException, ClassNotFoundException {
        name = (String)input.readObject();
        System.out.println("name recieved " + name);
        cores = input.readInt();
        System.out.println("cores sent");
        System.out.println(name + " " + cores);
        output.writeObject("Thank you, " + name);
        output.flush();
        System.out.println("response sent");
    }

    public void sendGame(Game game){
        try {
            output.writeObject("GAME");
            output.flush();
            output.writeObject(game);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlay(SinglePlay play){
        try {
            output.writeObject("PLAY");
            output.flush();
            output.writeObject(play);
            output.flush();
            play = (SinglePlay)input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
