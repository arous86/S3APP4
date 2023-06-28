package main.couche;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Physique {
    private static Physique instance = null;
    public static Physique getInstance() {
        if (instance == null) {
            instance = new Physique();
        }
        return instance;
    }
    private static Socket socket;
    public Trame SendFrame(String serverIP, int port, byte[] frameToSend) {
        try {

            if (socket == null || socket.isClosed()) {
                socket = new Socket(serverIP, port);
                System.out.println("Connected to server at " + serverIP + ":" + port);
                socket.setSoTimeout(1000);
            }

            // Envoi des données au serveur
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(frameToSend);
            System.out.println("sent to server:");
            System.out.println(Arrays.toString(frameToSend));


            // Attente de la réponse du serveur (ACK)
            //
            InputStream inputStream = socket.getInputStream();


            byte[] buffer = new byte[1024];

            int numbytes = inputStream.read(buffer); // Problème ici

            Trame trameRecu = new Trame();
            trameRecu.decode(buffer, numbytes);

            return trameRecu;


        } catch (IOException e) {
            e.printStackTrace();
            return new Trame();
        }
    }
    public void ReceiveFrames(int port){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Serveur en attente de connexions...");

            Socket clientSocket = serverSocket.accept(); // Attente de la connexion d'un client
            System.out.println("Client connecté.");
            clientSocket.setSoTimeout(10000);
            // Lecture des données envoyées par le client
            InputStream inputStream = clientSocket.getInputStream();

            byte[] receivedData = new byte[256];
            int numbytes = 0;
            Liaison liaison = Liaison.getInstance();
            try {
                numbytes = inputStream.read(receivedData);
            } catch (Exception e) {
                System.out.println("timeout du serveur1");
                return;
            }
            liaison.RecevoirPremiereTrame(receivedData, numbytes, clientSocket);

            while(numbytes!= -1) {
                try {
                    numbytes = inputStream.read(receivedData);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println("timeout du serveur2");
                }
                if(numbytes != -1) {
                    liaison.RecevoirTrames(receivedData, numbytes, clientSocket);
                }
            }
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
