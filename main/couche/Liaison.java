package main.couche;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;

/**
 * Classe permettant de gerer la couche liason de la communication.
 * Cette classe ajoute le CRC à la trame et verifie les CRC des trames reçu
 */
public class Liaison {
    private ArrayList<Trame> trames;
    private static Liaison instance = null;

    /**
     * Permet d'obtenir le singleton
     * @return instance singleton de Liaison
     */
    public static Liaison getInstance() {
        if (instance == null) {
            instance = new Liaison();
        }
        return instance;
    }

    /**
     * Fonction permettant de generer un CRC
     * @param bytes Array de bytes à signer
     * @return la valeur du CRC calculer
     */
    private static long GenerateCRC(byte[] bytes) {
        // Creer une instance de CRC32
        CRC32 crc32 = new CRC32();
        crc32.reset();

        // Mettre à jour le CRC32 avec les donnees
        crc32.update(bytes);

        // Obtenir la valeur CRC calculee en long
        return crc32.getValue();
    }

    /**
     * Fonction qui permet de gerer l'envoies d'une liste de trame
     * @param _trames liste de trames
     * @param serverIP ip du serveur de reception
     * @param port port du serveur de reception
     */
    public void EnvoyerTrames(ArrayList<Trame> _trames, String serverIP, int port) {

        instance.trames = _trames;
        Physique phys = Physique.getInstance();
        for (Trame trameSent : instance.trames) {

            trameSent.CRC = GenerateCRC(trameSent.toByteForCRC());

            int maxTentatives = 3; // Nombre maximum de tentatives pour l'envoi d'une trame
            int tentative = 0; // Compteur de tentatives
            boolean isAckReceived = false;
            Trame trameRecu;
            while (!isAckReceived && tentative < maxTentatives) {
                trameRecu = phys.SendFrame(serverIP, port, trameSent.toByte());
                    if (trameRecu.ACK == 0) {
                        // Start timer
                        System.out.println("Trame ok");
                        isAckReceived = true;
                    } else {
                        System.out.println("Trame non ok - ack");
                        tentative++;
                    }
            }

            if (tentative == maxTentatives) {
                System.out.println("Trame non envoyee après 3 tentatives");
                return;
            }
        }
        phys.CloseSocket();
    }

    /**
     * Fonction qui permet de gerer la reception d'une liste de trame
     * @param receivedData buffer de byte recu
     * @param numbytes nombre de byte dans le buffer
     * @param clientSocket socket du client pour repondre ACK ou NACK
     * @throws IOException si il y a une erreur avec le stream de donnees
     */
    public static void RecevoirTrames(byte[] receivedData, int numbytes, Socket clientSocket) throws IOException {
        Trame trameRecu = new Trame();
        trameRecu.decode(receivedData, numbytes);

        long crc = GenerateCRC(trameRecu.toByteForCRC());
        Trame trameAck = new Trame();

        if (crc == trameRecu.CRC) {
            trameAck.id = trameRecu.id;
            trameAck.ACK = 0;
            trameAck.dataLenght = 0;
            trameAck.CRC = GenerateCRC(trameAck.toByteForCRC());
        } else {
            trameAck.id = trameRecu.id;
            trameAck.ACK = 1;
            trameAck.dataLenght = 0;
            trameAck.CRC = GenerateCRC(trameAck.toByteForCRC());
        }

        // Repondre au client
        DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
        // Envoyer trameBytes au client
        outToClient.write(trameAck.toByte());
        outToClient.flush();

        Transport t = Transport.getInstance();
        t.ReceiveFrame(trameRecu.data);

    }

    /**
     * Fonction permetant de gerer la reception de la premiere trame de la recepton d'un fichier
     * @param receivedData buffer de byte recu
     * @param numbytes nombre de byte dans le buffer
     * @param clientSocket socket du client pour repondre ACK ou NACK
     * @throws IOException si il y a une erreur avec le stream de donnees
     */
    public static void RecevoirPremiereTrame(byte[] receivedData, int numbytes, Socket clientSocket) throws IOException {
        Trame trameRecu = new Trame();
        trameRecu.decode(receivedData, numbytes);

        long crc = GenerateCRC(trameRecu.toByteForCRC());
        Trame trameAck = new Trame();

        if (crc == trameRecu.CRC) {
            trameAck.id = trameRecu.id;
            trameAck.ACK = 0;
            trameAck.dataLenght = 0;
            trameAck.CRC = GenerateCRC(trameAck.toByteForCRC());
        } else {
            trameAck.id = trameRecu.id;
            trameAck.ACK = 1;
            trameAck.dataLenght = 0;
            trameAck.CRC = GenerateCRC(trameAck.toByteForCRC());
        }

        // Repondre au client
        DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
        // Envoyer trameBytes au client
        outToClient.write(trameAck.toByte());
        outToClient.flush();

        Transport t = Transport.getInstance();
        t.ReceiveFirstFrame(trameRecu.data);
    }
}


