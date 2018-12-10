package MasterMind.Clustering;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket server = null;
    private Socket connection;

    public void start(){
        try{
            server = new ServerSocket(2693,100);
            while(true){
                try{
                    waitForConnection();
                    setupCreateNewConnection();
                }catch (EOFException e){
                    System.out.print("Server closed");
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        finally{
            try {
                closeServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupCreateNewConnection() throws IOException {
        new Thread(new Connection(connection)).start();
    }

    private void closeServer()throws IOException {
        connection.close();
        server.close();
    }



    private void waitForConnection() throws IOException{
        System.out.println("Awaiting the connection.");
        connection = server.accept();
        System.out.println("Connection received from: " + connection.getInetAddress().getHostName());
    }



}
