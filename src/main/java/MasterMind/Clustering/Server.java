package MasterMind.Clustering;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * this class manages incoming socket connections for a the mastermind server.  As new socket is created, it is turned
 * immediately into a new Connection and handled from there.
 */
public class Server {
    private ServerSocket server = null;
    private Socket connection;

    /**
     * this method starts a new server.
     */
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

    /**
     * if a new connection has been made, this method will instantiate and start a Connection
     * @throws IOException
     */
    private void setupCreateNewConnection() throws IOException {
        new Thread(new Connection(connection)).start();
    }

    /**
     * this method will close down the server.
     * @throws IOException
     */
    private void closeServer()throws IOException {
        connection.close();
        server.close();
    }

    /**
     * this method will wait to hear from a new socket connection.
     * @throws IOException
     */
    private void waitForConnection() throws IOException{

        System.out.println("Awaiting the connection.");
        connection = server.accept();
        System.out.println("Connection received from: " + connection.getInetAddress().getHostName());
    }

}
