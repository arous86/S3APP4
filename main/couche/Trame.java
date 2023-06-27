package main.couche;

public class Trame{
    private final int TAILLE_ENTETE = 14;
    public long CRC;
    public int id;
    public byte ACK;
    public byte dataLenght;
    public byte[] data;

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

    public byte[] toByteForCRC(){
        byte[] bArr = new byte[TAILLE_ENTETE+(int)(dataLenght)-8];
        int ptr = 0;

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
    public boolean decode(byte[] bArr){
        if (bArr.length < TAILLE_ENTETE) return false;

        try {
            int ptr = 0;

            CRC = ((bArr[ptr + 0] & 0xff) << 56) |
                    ((bArr[ptr + 1] & 0xff) << 48) |
                    ((bArr[ptr + 2] & 0xff) << 40) |
                    ((bArr[ptr + 3] & 0xff) << 32) |
                    ((bArr[ptr + 4] & 0xff) << 24) |
                    ((bArr[ptr + 5] & 0xff) << 16) |
                    ((bArr[ptr + 6] & 0xff) << 8) |
                    (bArr[ptr + 7] & 0xff);
            ptr += 8;

            id = ((bArr[ptr + 4] & 0xff) << 24) |
                    ((bArr[ptr + 5] & 0xff) << 16) |
                    ((bArr[ptr + 6] & 0xff) << 8) |
                    (bArr[ptr + 7] & 0xff);
            ptr += 4;

            ACK = bArr[ptr++];

            dataLenght = bArr[ptr++];

            data = new byte[dataLenght];
            for (int i = 0; i< dataLenght; i++){
                data[i] = bArr[ptr++];
            }

        }catch (Exception e){
            return false;
        }



        return true;
    }



}
