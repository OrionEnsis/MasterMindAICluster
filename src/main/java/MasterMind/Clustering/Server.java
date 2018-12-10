package MasterMind.Clustering;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private Socket socket = null;
    private ServerSocket server = null;
    private Socket connection;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public void start(){
        try{
            //TODO find my number for port
            server = new ServerSocket(10000,100);
            while(true){
                try{
                    waitForConnection();
                    setupStreams();
                    maintainConnection();
                }catch (EOFException e){
                    System.out.print("Server closed");
                }
                finally{
                    //TODO close the server
                    closeServer();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        System.out.println("Streams setup");
    }

    private void waitForConnection() throws IOException{
        System.out.println("Awaiting the connection.");
        connection = server.accept();
        System.out.println("Connection received from: " + connection.getInetAddress().getHostName());
    }



}
