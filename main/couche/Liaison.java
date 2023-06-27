package main.couche;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
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

        // Mettre à jour le CRC32 avec les données
        crc32.update(bytes);

        // Obtenir la valeur CRC calculée en long
        return crc32.getValue();
    }
    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    public byte[] ConcatenateTrame(Trame trame) {
        // concatenate header and data
        byte[] trameBytes = new byte[trame.header.length + trame.data.length];
        System.arraycopy(trame.header, 0, trameBytes, 0, trame.header.length);
        System.arraycopy(trame.data, 0, trameBytes, trame.header.length, trame.data.length);
        return trameBytes;
    }
    public void EnvoyerTrames(ArrayList<Trame> _trames, String serverIP, int port) {
        instance.trames = _trames;
        for (Trame trame : instance.trames) {
            byte[] tmp_trameBytes = instance.ConcatenateTrame(trame);

            long crc = GenerateCRC(tmp_trameBytes);
            trame.CRC = instance.longToBytes(crc);
            // concatenate crc first and trame.crc after
            byte[] trameBytes = new byte[trame.CRC.length + tmp_trameBytes.length];
            System.arraycopy(trame.CRC, 0, trameBytes, 0, trame.CRC.length);
            System.arraycopy(tmp_trameBytes, 0, trameBytes, trame.CRC.length, tmp_trameBytes.length);

            Physique phys = Physique.getInstance();

            int maxTentatives = 3; // Nombre maximum de tentatives pour l'envoi d'une trame
            int tentative = 0; // Compteur de tentatives
            boolean isAckReceived = false;

            while (!isAckReceived && tentative < maxTentatives) {
                if (phys.SendFrame(serverIP, port, trameBytes)) {
                    System.out.println("Trame envoyée");
                    isAckReceived = true;
                } else {
                    System.out.println("Trame non envoyée");
                    tentative++;
                }
            }

            if (tentative == maxTentatives) {
                System.out.println("Trame non envoyée après 3 tentatives");
                // TODO: Abandonner
            }
        }
    }
    public static void RecevoirTrames(byte[] receivedData, ServerSocket serverSocket) throws IOException {
        byte[] CRC = new byte[8];
        byte[] header = new byte[6];
        byte[] data = new byte[receivedData.length - CRC.length - header.length];

        System.arraycopy(receivedData, 0, CRC, 0, CRC.length);
        System.arraycopy(receivedData, CRC.length, header, 0, header.length);
        System.arraycopy(receivedData, CRC.length + header.length, data, 0, data.length);

        long crc = GenerateCRC(receivedData);
        byte[] crcBytes = instance.longToBytes(crc);

        byte[] trameBytes = new byte[header.length];
        if (CRC.equals(crcBytes)) {
            // copy the 4 first bytes of header
            System.arraycopy(header, 0, trameBytes, 0, 4);
            // place 0 in the 5th byte and 0 in the 6th byte
            trameBytes[4] = 0;
            trameBytes[5] = 0;
        }
        else {
            // copy the 4 first bytes of header
            System.arraycopy(header, 0, trameBytes, 0, 4);
            // place 1 in the 5th byte and 0 in the 6th byte
            trameBytes[4] = 1;
            trameBytes[5] = 0;
        }

        // Repondre au client
        Socket clientSocket = serverSocket.accept();
        DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());

        outToClient.writeByte(trameBytes[4]);

        outToClient.flush();

        // Fermeture des flux et des sockets
        outToClient.close();
        clientSocket.close();

        // TODO: Envoyer les trames à la couche transport

    }
    }


