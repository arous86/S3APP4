package main.couche;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * descritpion
 *
 */
public class Transport {
    private final int PAQUET_MAX_LENGHT = 200;

    private int totalPaquets = 0;
    public ArrayList<Trame> listeTrame;

    public boolean EnvoyerFichier(String filename, byte[] buffer, String serverIP, int port)
    {
        listeTrame = new ArrayList<Trame>();

        int numDataPaquet = (buffer.length-1) / (PAQUET_MAX_LENGHT+1); //https://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil

        //construire premier paquet
        Trame premier = buildFirstPaquet(filename, numDataPaquet);

        listeTrame.add(premier);

        //construire paquets de données
        listeTrame.addAll(buildDataPaquets(buffer, numDataPaquet));

        Liaison l = Liaison.getInstance();
        l.EnvoyerTrames(listeTrame, serverIP, port);

        return true;
    }

    private Trame buildFirstPaquet(String filename, int numpaquets)
    {
        Trame tempTrame = new Trame();

        ///DATA
        byte[] BFN = filename.getBytes();
        tempTrame.data = new byte[BFN.length+4]; //+4 car int est 4 byte

        tempTrame.data[0] = (byte)(numpaquets >> 24);
        tempTrame.data[1] = (byte)(numpaquets >> 16);
        tempTrame.data[2] = (byte)(numpaquets >> 8);
        tempTrame.data[3] = (byte)(numpaquets);

        for (int i = 0; i< BFN.length; i++)
        {
            tempTrame.data[i+4] = BFN[i];
        }

        ////HEADER
        //id
        int id = totalPaquets++;
        tempTrame.header[0] = (byte)(id >> 24);
        tempTrame.header[1] = (byte)(id >> 16);
        tempTrame.header[2] = (byte)(id >> 8);
        tempTrame.header[3] = (byte)(id);
        //ack
        tempTrame.header[4] = 0;
        //datalenght
        tempTrame.header[5] = (byte)(tempTrame.data.length); //ne dépasse jamais 200 donc fit dans 8 bit même si on perd une partie du int

        return tempTrame;
    }

    private ArrayList<Trame> buildDataPaquets(byte[] buffer, int numpaquets)
    {
        for (int i=0; i<numpaquets; i++)
        {
            Trame temp = new Trame();

            //set header
            //set data
        }
        return new ArrayList<Trame>();
    }
}