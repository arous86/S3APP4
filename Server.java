import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(41000); // Création du socket serveur sur le port 30000
            System.out.println("Serveur en attente de connexions...");

            Socket clientSocket = serverSocket.accept(); // Attente de la connexion d'un client
            System.out.println("Client connecté.");

            // Flux de lecture et d'écriture pour la communication avec le client
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(), true);

            String clientMessage = inFromClient.readLine(); // Lecture du message du client
            System.out.println("Message reçu du client : " + clientMessage);

            String capitalizedMessage = clientMessage.toUpperCase(); // Conversion en majuscules
            outToClient.println(capitalizedMessage); // Envoi de la réponse au client

            // Fermeture des flux et des sockets
            inFromClient.close();
            outToClient.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
