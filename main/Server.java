package main;

import main.couche.Physique;
import main.couche.Transport;

import java.io.*;
import java.net.*;

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
        p.ReceiveFrame(44419);

        Transport t = Transport.getInstance();
        byte[] receivedData = t.SendFile();
        System.out.println("Received data : " + receivedData);
    }
}