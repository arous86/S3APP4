package main.couche;

import java.util.Arrays;

/**
 * Objet contenant les donnees du paquet à envoyer et des fonction "helpers" pour
 *  l'encodage et decodage de l'entête et des donnees en bytes.
 */
public class Trame{
    private final int TAILLE_ENTETE = 14;
    public long CRC;
    public int id;
    public byte ACK = 0;
    public byte dataLenght;
    public byte[] data;

    /**
     * Concatene les donnees de la Trame (paquet) en un buffer de byte
      * @return array de byte
     */
    public byte[] toByte(){
        byte[] bArr = new byte[TAILLE_ENTETE+(int)(dataLenght)];
        int ptr = 0;

        bArr[ptr++] = (byte)(CRC>>56);
        bArr[ptr++] = (byte)(CRC>>48);
        bArr[ptr++] = (byte)(CRC>>40);
        bArr[ptr++] = (byte)(CRC>>32);
        bArr[ptr++] = (byte)(CRC>>24);
        bArr[ptr++] = (byte)(CRC>>16);
        bArr[ptr++] = (byte)(CRC>>8);
        bArr[ptr++] = (byte)(CRC);

        bArr[ptr++] = (byte)(id>>24);
        bArr[ptr++] = (byte)(id>>16);
        bArr[ptr++] = (byte)(id>>8);
        bArr[ptr++] = (byte)(id);

        bArr[ptr++] = ACK;

        bArr[ptr++] = dataLenght;

        for(int i = 0;i<(int)(dataLenght);i++)
        {
            bArr[ptr++] = data[i];
        }

        return bArr;
    }

    /**
     * Concatene les donnees de la Trame (paquet) sauf le crc en un
     * buffer de byte pour le calcul du crc
     * @return array de bytes
     */
    public byte[] toByteForCRC(){
        byte[] bArr = new byte[TAILLE_ENTETE+(int)(dataLenght)-8];
        int ptr = 0;

        bArr[ptr] = (byte)(id>>24);
        bArr[ptr] = (byte)(id>>16);
        bArr[ptr] = (byte)(id>>8);
        bArr[ptr] = (byte)(id);
        ptr+=4;
        bArr[ptr] = ACK;
        ptr++;
        bArr[ptr] = dataLenght;
        ptr++;
        for(int i = 0;i<(int)(dataLenght);i++)
        {
            bArr[ptr] = data[i];
            ptr++;
        }
        return bArr;
    }

    /**
     * Fonction permettant de decoder un array de byte entrant en objet trame
      * @param bArr array de bytes entrants
     * @param nbytes nombre de byte de donnees dans le bArr
     */
    public void decode(byte[] bArr, int nbytes){
        if (nbytes < TAILLE_ENTETE) return;

        try {
            int ptr = 0;

            CRC = ((long) (bArr[ptr] & 0xff) << 56) |
                    ((long) (bArr[ptr + 1] & 0xff) << 48) |
                    ((long) (bArr[ptr + 2] & 0xff) << 40) |
                    ((long) (bArr[ptr + 3] & 0xff) << 32) |
                    ((long) (bArr[ptr + 4] & 0xff) << 24) |
                    ((long) (bArr[ptr + 5] & 0xff) << 16) |
                    ((long) (bArr[ptr + 6] & 0xff) << 8) |
                    (long) (bArr[ptr + 7] & 0xff);
            ptr += 8;

            id = ((bArr[ptr] & 0xff) << 24) |
                    ((bArr[ptr + 1] & 0xff) << 16) |
                    ((bArr[ptr + 2] & 0xff) << 8) |
                    (bArr[ptr + 3] & 0xff);
            ptr += 4;

            ACK = bArr[ptr];
            ptr++;
            dataLenght = bArr[ptr];
            ptr++;
            data = new byte[dataLenght];
            for (int i = 0; i< dataLenght; i++){
                data[i] = bArr[ptr];
                ptr++;
            }

        }catch (Exception e){
            System.out.println("probleme survenu lors du decodage de la trame");
        }
    }



}
