package couche;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Physique {
    private static Physique instance = null;
    public static Physique getInstance() {
        if (instance == null) {
            instance = new Physique();
        }
        return instance;
    }
    private static Socket socket;
    public boolean SendFrame(String serverIP, int port, byte[] frameToSend) {
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket(serverIP, port);
                System.out.println("Connected to server at " + serverIP + ":" + port);
            }

            // Envoi des données au serveur
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(frameToSend);

            // Attente de la réponse du serveur (ACK)
            InputStream inputStream = socket.getInputStream();
            byte ack = (byte) inputStream.read();
            System.out.println("ACK reçu : " + ack);
            if (ack == 0) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void ReceiveFrame(int port){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Serveur en attente de connexions...");

            Socket clientSocket = serverSocket.accept(); // Attente de la connexion d'un client
            System.out.println("Client connecté.");

            // Lecture des données envoyées par le client
            InputStream inputStream = clientSocket.getInputStream();
            byte[] receivedData = new byte[256];

            Liaison liaison = Liaison.getInstance();
            liaison.RecevoirTrames(receivedData, serverSocket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void CloseSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null; // Réinitialise le socket à null après la fermeture
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
