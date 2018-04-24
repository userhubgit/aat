package fr.cnam.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.cnam.model.Motif;

/**
 * Classe utilitaire qui permet de lire le référentiel(CSV) de motif AAT.
 *
 * @author ONDONGO-09929
 *
 */
public final class ReferentielCSVReaderUtil {

    /**
     * Espace.
     */
    public static final String ESPACE = " ";

    /**
     * Chaine vide.
     */
    public static final String VIDE = "";

    /**
     * Separateur des données.
     */
    public static final String CSV_SERAPTOR = ";";
    /**
     * Séparateur des synonymes.
     */
    public static final String SYNONYME_SERAPTOR = ",";

    /**
     * Constructeur.
     */
    private ReferentielCSVReaderUtil() {
        super();
    }

    /**
     * Méthode qui permet de lire une ligne spécifique du fichier CSV.
     * @param pLigne : la ligne en cours de lecture
     * @return : une instance de {@link Motif}
     * qui encapsule les infos de la ligne
     */
    public static Motif lireLigne(final String pLigne) {
        final String[] fields = pLigne.split(CSV_SERAPTOR);
        Motif motif = new Motif();
        
        if (fields.length >= Constante.CSV_SYNONYME_INDEX) {
        	
            motif.setCode(fields[Constante.CSV_CODE_INDEX]);
            motif.setLibelle(fields[Constante.CSV_LIBELLE_INDEX]);
            motif.setCodification(fields[Constante.CSV_CODIFICATION_INDEX]);

            String syn = "";
            if (fields.length > Constante.CSV_SYNONYME_INDEX) {
                String synonymesString = fields[Constante.CSV_SYNONYME_INDEX];
                String[] tableDeSynonyme = synonymesString
                .split(Constante.SYNONYME_SERAPTOR);
                for (String synonyme : tableDeSynonyme) {
                    syn = syn + ESPACE + synonyme;
                }
                motif.setSynonymes(Arrays.asList(tableDeSynonyme));
            }
        }
        return motif;
    }

    /**
     * Méthode qui permet de lire toute
     * les lignes du fichier (référentiel) CSV.
     * @param pReferentielCSV : le fichier référentiel.
     * @return : liste d'objet de la classe {@link Motif}.
     * @throws IOException : Notification d'une erreur technique
     * lors de la lecture du fichier.
     */
    public static List < Motif > lireFichier(final File pReferentielCSV)
    throws IOException {

        List < Motif > liste = new ArrayList < Motif >();


        String line = "";
        BufferedReader br = new BufferedReader(new FileReader(pReferentielCSV));

        while ((line = br.readLine()) != null) {

            String[] fields = line.split(CSV_SERAPTOR);
            if (fields.length >= Constante.CSV_SYNONYME_INDEX) {
            	Motif motifEncours = new Motif();
                motifEncours.setCode(fields[Constante.CSV_CODE_INDEX]);
                motifEncours.setLibelle(fields[Constante.CSV_LIBELLE_INDEX]);
                motifEncours.setCodification(fields[Constante.CSV_CODIFICATION_INDEX]);
                
                if (fields.length > Constante.CSV_ACRONYME_INDEX) {
                    String acroonymesString = fields[Constante.CSV_ACRONYME_INDEX];
                    String[] tableAcronyme = acroonymesString
                        .split(SYNONYME_SERAPTOR);
                    motifEncours.setAcronymes(Arrays.asList(tableAcronyme));
                }

                if (fields.length > Constante.CSV_SYNONYME_INDEX) {
                    String synonymesString = fields[Constante.CSV_SYNONYME_INDEX];
                    String[] tableDeSynonyme = synonymesString
                        .split(SYNONYME_SERAPTOR);
                    motifEncours.setSynonymes(Arrays.asList(tableDeSynonyme));
                }
                
                if (fields.length > Constante.CSV_GENERIQUE_INDEX) {
                	String generiqueString = fields[Constante.CSV_GENERIQUE_INDEX];
                	String[] tableGenerique = generiqueString.split(SYNONYME_SERAPTOR);
                	motifEncours.setGenerique(Arrays.asList(tableGenerique));
                }
                
                liste.add(motifEncours);
            }
        }
        br.close();
        return liste;
    }
    
    
}
