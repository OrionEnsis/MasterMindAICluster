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
    private AI ai;

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

    private void calculatePlay() throws IOException, ClassNotFoundException {
        System.out.println("Awaiting Play");
        SinglePlay play = (SinglePlay)input.readObject();
        System.out.println("calculating play");
        ai.determineScore(play);
        System.out.println("sending Play");
        output.writeObject(play);
        System.out.println("play sent" + play);
        output.flush();
    }

    private void getNewGame() throws IOException, ClassNotFoundException {
        game = (Game)input.readObject();
        System.out.println("Received Game");
        //ai = new AI(game,false);
    }

    public void sendPrimaryInfo(){
        try {
            output.writeObject(InetAddress.getLocalHost().getHostName());
            //System.out.println("sent hostName");
            output.flush();
            output.writeInt(Runtime.getRuntime().availableProcessors());
            output.flush();
            //System.out.println("sent cores");
            String temp = (String)input.readObject();
            //System.out.println("received response");
            //System.out.println(temp);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void calculate(){
        try {
            System.out.println("Starting game calculation");
            int start = input.readInt();
            int end = input.readInt();
            ai = new AI(game,start,end,false);
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
