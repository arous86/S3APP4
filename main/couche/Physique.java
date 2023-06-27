package main.couche;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Physique {
    // Singleton
    private static Physique instance = null;
    public static Physique getInstance() {
        if (instance == null) {
            instance = new Physique();
        }
        return instance;
    }
    private static Socket socket;
    public void SendData(String serverIP, int port, byte[] dataToSend)
    {
        try {
            Socket socket = new Socket(serverIP, port);
            //System.out.println("Connected to server at " + serverIP + ":" + port);

            // Envoi des données au serveur
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(dataToSend);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
