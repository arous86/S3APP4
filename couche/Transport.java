package couche;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import couche.Trame;

/**
 * descritpion
 *
 */
public class Transport {
    private final int PAQUET_MAX_LENGHT = 200;
    List<couche.Trame> listeTrame;

    /**
     * construit les paquets pour l'envoies de fichier
     * @param filename
     * @param buffer
     * @return
     */
    public List<Trame> EnvoyerFichier(String filename, byte[] buffer)
    {
        listeTrame = new Vector<couche.Trame>();

        int numDataPaquet = (buffer.length-1) / (PAQUET_MAX_LENGHT+1); //https://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil

        //construire premier paquet
        Trame premier = buildFirstPaquet(filename, numDataPaquet);

        listeTrame.add(premier);

        //construire paquets de données
        listeTrame.addAll(buildDataPaquets(buffer, numDataPaquet));

        return listeTrame;
    }

    private Trame buildFirstPaquet(String filename, int numpaquets)
    {

        return new Trame();
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
