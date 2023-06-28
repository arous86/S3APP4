package main.couche;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Cette classe s'occupe d'etablir la connexion entre le client et le serveur
 * et d'envoyer les trames au serveur. Elle s'occupe aussi de recevoir du
 * côte du serveur où elle reste en ecoute passive.
 */
public class Physique {
    private static Physique instance = null;

    /**
     * Permet d'obtenir le singleton
     * @return instance singleton de Physique
     */
    public static Physique getInstance() {
        if (instance == null) {
            instance = new Physique();
        }
        return instance;
    }
    private static Socket socket;

    /**
     * Fonction qui envoie une trame et attend une reponse sous forme de trame
     * @param serverIP ip du server a se connecter
     * @param port port du server a se connecter
     * @param frameToSend format de byte de l'objet Trame
     * @return la trame reçu en reponse
     */
    public Trame SendFrame(String serverIP, int port, byte[] frameToSend) {
        try {

            if (socket == null || socket.isClosed()) {
                socket = new Socket(serverIP, port);
                System.out.println("Connected to server at " + serverIP + ":" + port);
                socket.setSoTimeout(1000);
            }

            // Envoi des donnees au serveur
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(frameToSend);
            System.out.println("sent to server:");
            System.out.println(Arrays.toString(frameToSend));


            // Attente de la reponse du serveur (ACK)
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

    /**
     * Fonction de reception d'une communication d'un client
     * @param port port du serveur
     */
    public void ReceiveFrames(int port){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Serveur en attente de connexions...");

            Socket clientSocket = serverSocket.accept(); // Attente de la connexion d'un client
            System.out.println("Client connecte.");
            clientSocket.setSoTimeout(10000);
            // Lecture des donnees envoyees par le client
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

    /**
     * Fonction permettant de fermer le socket de communication
     */
    public void CloseSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null; // Reinitialise le socket à null après la fermeture
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
