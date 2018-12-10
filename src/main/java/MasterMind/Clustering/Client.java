package MasterMind.Clustering;

import MasterMind.AI.AI;
import MasterMind.AI.SinglePlay;
import MasterMind.Game;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket socket;
    private Game game;
    private AI ai;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Name: ");
        String temp = scanner.next();
        Client client = new Client(temp);
        client.sendPrimaryInfo();
        client.handleInformation();
    }

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
    private void handleInformation(){
        while(true){
            try {
                String temp = (String)input.readObject();
                if(temp.equalsIgnoreCase("GAME")){
                    getNewGame();
                }else if(temp.equalsIgnoreCase("PLAY")){
                    calculatePlay();
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
        System.out.println("play sent");
        output.flush();
    }

    private void getNewGame() throws IOException, ClassNotFoundException {
        game = (Game)input.readObject();
        System.out.println("Received Game");
        ai = new AI(game,false);
    }

    private void sendPrimaryInfo(){
        try {
            output.writeObject(InetAddress.getLocalHost().getHostName());
            System.out.println("sent hostName");
            output.flush();
            output.writeInt(Runtime.getRuntime().availableProcessors());
            output.flush();
            System.out.println("sent cores");
            String temp = (String)input.readObject();
            System.out.println("received response");
            System.out.println(temp);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
