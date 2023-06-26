package couche;

public class Trame{
    private int TAILLE_FIXE = 6;
    public byte[] CRC = new byte[64];
    public byte[] header = new byte[TAILLE_FIXE];
    public byte[] data;
}
