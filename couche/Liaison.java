package couche;

import java.util.zip.CRC32;

public class Liaison {
    public static long GenerateCRC(String data) {
        // Convertir la chaîne en tableau de bytes
        byte[] bytes = data.getBytes();

        // Créer une instance de CRC32
        CRC32 crc32 = new CRC32();

        // Mettre à jour le CRC32 avec les données
        crc32.update(bytes);

        // Obtenir la valeur CRC calculée
        return crc32.getValue();
    }
}
