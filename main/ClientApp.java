package main;

import main.couche.Transport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
/**
 * Cette classe est le point d'entree du client.
 * Elle s'occupe de lire le fichier à envoyer et de l'envoyer au serveur.
 */

public class ClientApp {
    /**
     * Le port utilise pour la connexion.
     */
    private static int port = 44419;

    /**
     * Cette methode permet de convertir un fichier en un tableau de bytes.
     * @param file Le fichier à convertir.
     * @return Le tableau de bytes correspondant au fichier.
     * @throws IOException si erreur de lecture du fichier
     */
    public static byte[] method(File file)
            throws IOException
    {

        // Creating an object of FileInputStream to
        // read from a file
        FileInputStream fl = new FileInputStream(file);

        // Now creating byte array of same length as file
        byte[] arr = new byte[(int)file.length()];

        // Reading file content to byte array
        // using standard read() method
        fl.read(arr);

        // lastly closing an instance of file input stream
        // to avoid memory leakage
        fl.close();

        // Returning above byte array
        return arr;
    }

    /**
     * Cette methode est le point d'entree du client.
     * @param args Les arguments passes au programme. args[0] doit être l'adresse IP du serveur. args[1] doit être le chemin vers le fichier à envoyer.
     * @throws IOException si erreur de lecture du fichier
     */
    public static void main(String[] args)
            throws IOException
    {
        String serverIP = args[0];
        String filePath = args[1];


        // Creating an object of File class and
        // providing local directory path of a file
        File path = new File(filePath);

        // Calling the Method1 in main() to
        // convert file to byte array
        byte[] array = method(path);

        // Printing the byte array
        System.out.print(Arrays.toString(array));

        // Print the name of the file not the path
        System.out.println(path.getName());

        Transport t = Transport.getInstance();
        t.EnvoyerFichier(path.getName(),array, serverIP, port);

        System.out.println("Envoie termine");

    }
}
