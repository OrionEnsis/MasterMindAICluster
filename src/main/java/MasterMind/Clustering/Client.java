package MasterMind.Clustering;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket socket;
    public static void main(String[] args) {
        Client client = new Client();
        client.sendPrimaryInfo();
    }

    private void sendPrimaryInfo(){
        try {
            output.writeObject(InetAddress.getLocalHost().getHostName());
            System.out.println("sent hostName");
            output.flush();
            output.writeInt(Runtime.getRuntime().availableProcessors());
            output.flush();
            System.out.println("send cores");
            String temp = (String)input.readObject();
            System.out.println("recieved response");
            System.out.println(temp);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally{
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Client(){
        try {
            socket = new Socket("localhost",2693);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
