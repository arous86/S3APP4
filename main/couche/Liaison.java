package main.couche;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;

public class Liaison {
    private ArrayList<Trame> trames;
    private static Liaison instance = null;

    public static Liaison getInstance() {
        if (instance == null) {
            instance = new Liaison();
        }
        return instance;
    }

    private static long GenerateCRC(byte[] bytes) {
        // Créer une instance de CRC32
        CRC32 crc32 = new CRC32();
        crc32.reset();

        // Mettre à jour le CRC32 avec les données
        crc32.update(bytes);

        // Obtenir la valeur CRC calculée en long
        return crc32.getValue();
    }

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
                System.out.println("Trame non envoyée après 3 tentatives");
                return;
            }
        }
        phys.CloseSocket();
    }

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


