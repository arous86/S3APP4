import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 30000); // Connexion au serveur sur le port 30000
            System.out.println("Connecté au serveur.");

            // Flux de lecture et d'écriture pour la communication avec le serveur
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);

            String messageToSend = "Bonjour, serveur !";
            outToServer.println(messageToSend); // Envoi du message au serveur

            String serverResponse = inFromServer.readLine(); // Lecture de la réponse du serveur
            System.out.println("Réponse du serveur : " + serverResponse);

            // Fermeture des flux et du socket
            inFromServer.close();
            outToServer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
