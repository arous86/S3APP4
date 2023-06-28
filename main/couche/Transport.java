package main.couche;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Cette classe permet de transformer les donnees de l'application en paquet
 * prêt pour l'envoie au server. Chaque paquet est encapsule d'une entête. *
 */
public class Transport {
    public clientInstance ci = new clientInstance();
    // singleton
    private static Transport instance = null;

    /**
     * Permet d'obtenir le singleton
     * @return instance singleton de Transport
     */
    public static Transport getInstance()
    {
        if(instance == null)
        {
            instance = new Transport();
        }
        return instance;
    }
    private final int PAQUET_MAX_LENGHT = 200;

    private int totalPaquets = 0;
    public ArrayList<Trame> listeTrame;

    /**
     * Fonction d'encapsulation des donnees en paquets
     * @param filename nom du fichier (sans le chemin d'acces)
     * @param buffer array de byte[] de donnees du fichier a envoyer
     * @param serverIP ip du serveur de reception
     * @param port port du serveur de reception
     * @return l'etat de reussite de la fonction
     */
    public boolean EnvoyerFichier(String filename, byte[] buffer, String serverIP, int port)
    {
        if (buffer == null){return false;}

        listeTrame = new ArrayList<Trame>();

        int numDataPaquet = (int)(Math.ceil((double)buffer.length / (double)PAQUET_MAX_LENGHT)); //https://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil

        //construire premier paquet
        Trame premier = buildFirstPaquet(filename, numDataPaquet);

        listeTrame.add(premier);

        //construire paquets de donnees
        listeTrame.addAll(buildDataPaquets(buffer, numDataPaquet));

        Liaison l = Liaison.getInstance();
        l.EnvoyerTrames(listeTrame, serverIP, port);

        return true;
    }

    /**
     * Cree le premier paquet de l'envoie, contient le nombre de paquet et le nom de fichier
     * @param filename nom du fichier
     * @param numpaquets nombre de paquets de donnees
     * @return Objet Trame (paquet)
     */
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
        tempTrame.id = totalPaquets++;
        //ack
        tempTrame.ACK = 0;
        //datalenght
        tempTrame.dataLenght = (byte)(tempTrame.data.length); //ne depasse jamais 200 donc fit dans 8 bit même si on perd une partie du int

        return tempTrame;
    }

    /**
     *  Fonction permetant de separer les donnees du fichier en groupe de 200
     *  bytes max et de les encapsulers en Trames (paquets)
     * @param buffer donnees du fichier a separer en paquets
     * @param numpaquets nombre de paquets de donnees
     * @return Liste d'objet Trame (paquet)
     */
    private ArrayList<Trame> buildDataPaquets(byte[] buffer, int numpaquets)
    {
        ArrayList<Trame> listeTrame = new ArrayList<Trame>();
        int ptr = 0;
        for (int i=0; i<numpaquets; i++)
        {
            Trame tempTrame = new Trame();
            ///DATA
            int byteToCopy = PAQUET_MAX_LENGHT;
            int dataleft = buffer.length - ptr;
            if(dataleft<PAQUET_MAX_LENGHT) { byteToCopy = dataleft; }
            tempTrame.data = new byte[byteToCopy];
            for(int j =0; j< byteToCopy; j++)
            {
                tempTrame.data[j] = buffer[ptr++];
            }
            //HEADER
            //id
            tempTrame.id = totalPaquets++;
            //ack
            tempTrame.ACK = 0;
            //datalenght
            tempTrame.dataLenght = (byte)(byteToCopy); //ne depasse jamais 200 donc fit dans 8 bit même si on perd une partie du int

            listeTrame.add(tempTrame);
        }
        return listeTrame;
    }

    /**
     * permet de concatener deux tableau de bytes
     * @param a tableau de byte 1
     * @param b tableau de byte 2
     * @return tableau de byte concatene
     */
    private byte[] arrayConcat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a,0,result,0,a.length);
        System.arraycopy(b,0,result,a.length,b.length);
        return result;
    }

    /**
     * fonction permettant d'enregistrer les donnees reçu dans un buffer
     * pour la reconstruction du fichier
     * @param data liste de byte a append au buffer
     */
    public void ReceiveFrame(byte[] data) {
        if (data == null ) {return;}
        if (data.length <= 0) {return;}

        byte[] temp = arrayConcat(ci._bytes, data);
        ci._bytes = temp;
        ci.numPaquets++;
    }

    /**
     * fonction permettant d'etablir les paramètres de clientInstance
     * et de creer un nouveau buffer de donnees
     * @param data donnees reçu dans le premier paquet
     */
    public void ReceiveFirstFrame(byte[] data)
    {
        ci.numPaquets = 0;
        ci.totalPaquets = ((data[0] & 0xff) << 24) |
                        ((data[1] & 0xff) << 16) |
                        ((data[2] & 0xff) << 8) |
                        (data[3] & 0xff);
        ci.filename = new String(Arrays.copyOfRange(data, 4, data.length));
        ci._bytes = new byte[0];
    }

    /**
     * fonction permettant d'acceder au buffer de reconstruction une foit la communication termine
     * @return array de byute de donnees du fichier à reconstruire
     */
    public byte[] receivedFile() {
        return ci._bytes;
    }
}
