package main;

import main.couche.*;

import java.io.*;

/**
 * Cette classe s'occupe d'établir la connexion entre le client et le serveur
 * et d'envoyer les trames au serveur. Elle s'occupe aussi de recevoir du
 * côté du serveur où elle reste en écoute passive.
 */
public class Server {
    /**
     * Cette méthode est le point d'entrée du serveur.
     * @param args Aucun argument n'est requis.
     */
    public static void main(String[] args) {
        Physique p = Physique.getInstance();
        p.ReceiveFrames(44419);

        Transport t = Transport.getInstance();
        System.out.println("received filename : " + t.ci.filename);

        File yourFile = new File("reception/"+t.ci.filename);
        try {
            yourFile.getParentFile().mkdirs();
            yourFile.createNewFile(); // if file already exists will do nothing
        } catch (IOException e) {
            System.out.println("could not create file");
            return;
        }
        FileOutputStream oFile;
        try {
            oFile = new FileOutputStream(yourFile, false);
        } catch (FileNotFoundException e) {
            System.out.println("could not create filestream");
            return;
        }

        try {
            oFile.write(t.receivedFile());
        } catch (IOException e) {
            System.out.println("could not create write to file");
        }
    }
}