package main.couche;

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
        byte[] result = new byte[trame.CRC.length + trame.header.length + trame.data.length];
        System.arraycopy(trame.CRC, 0, result, 0, trame.CRC.length);
        System.arraycopy(trame.header, 0, result, trame.CRC.length, trame.header.length);
        System.arraycopy(trame.data, 0, result, trame.CRC.length + trame.header.length, trame.data.length);
        return result;
    }
    public void EnvoyerTrames(ArrayList<Trame> _trames, String serverIP, int port) {
        instance.trames = _trames;
        for (Trame trame : instance.trames) {
            long crc = GenerateCRC(trame.data);
            trame.CRC = instance.longToBytes(crc);
            byte[] trameBytes = instance.ConcatenateTrame(trame);
            Physique phys = Physique.getInstance();
            phys.SendData(serverIP, port, trameBytes);
            
            // TODO: Attendre un ACK

            // TODO: Si ACK reçu, envoyer la prochaine trame
            // TODO: Si ACK non reçu, réenvoyer la trame
            // TODO: Si ACK non reçu après 3 essais, abandonner
        }
    }



}
