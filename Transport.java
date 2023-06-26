import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.floor;

public class Transport {
    private int PAQUET_MAX_LENGHT = 200;

    public List<Trame> construirePaquets(String filename, byte[] buffer)
    {
        List<Trame> listeFrame = new ArrayList<Trame>();

        int numDataPaquet = (buffer.length-1) / (PAQUET_MAX_LENGHT+1); //https://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil

        //construire premier paquet
        Trame premier = buildFirstPaquet(filename, numDataPaquet);
        listeFrame.add(premier);

        //construire paquets de données
        for(Trame tr : buildDataPaquets(buffer, numDataPaquet))
        {
            listeFrame.add(tr);
        }

        return listeFrame;
    }

    private Trame buildFirstPaquet(String filename, int numpaquets)
    {
        return new Trame();
    }

    private List<Trame> buildDataPaquets(byte[] buffer, int numpaquets)
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
