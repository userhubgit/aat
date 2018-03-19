/**
 *
 */
package fr.cnam.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.PagedBytes.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.apache.lucene.util.Version;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import fr.cnam.model.Motif;
import fr.cnam.service.LuceneIndexRecherche;
import fr.cnam.util.AATLuceneAnalyzerUtil;
import fr.cnam.util.ReferentielCSVReaderUtil;



/**
 * @author ONDONGO-09929.
 *
 */
@Service
public class LuceneIndexRechercheImpl implements LuceneIndexRecherche {

    /**
     * Analyzer.
     */
    private static final Analyzer ANALYZER =
    AATLuceneAnalyzerUtil.getAnalyzer();
    
    @Autowired
    private ResourceLoader resourceLoader;
    /**
     * Directory.
     */
    private final RAMDirectory ramDirectory = new RAMDirectory();
    
    /**
     * Message d'erreur lors de la recherche.
     */
    private static String MSG_ERREUR_RECHERCHE =
    "Une erreur s'est produite lors la recherche de motif pour le terme:=%s";

    /** Maximum de resultat de l'autocompl�tion. */
    private static final int MAX_RESULT = 10;

    /** Le champ code du motif. */
    public static final String CHAMP_CODE = "code";

    /** Le champ libelle du motif. */
    public static final String CHAMP_LIBELLE = "libelle";

    /** Le champ synonyme du motif. */
    public static final String CHAMP_SYNONYME = "synonyme";

    /** Le champ codeFonctionnel du motif. */
    public static final String CHAMP_CODE_FONCTIONNEL = "codeFonctionnel";
    
    private static final float LIBELLE_SCORE_BOOST = 50;

    
    
    /*
     * (non-Javadoc)
     *
     * @see fr.cnamts.yt.metier.bs.LuceneIndexRechercheBS
     *      #indexationDisque(java.io.File, java.io.File)
     */
//    @Override
    public void indexationDisque(final File pReferentielMotifAAT,
            final File pDisquePath){
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.cnamts.yt.metier.bs.LuceneIndexRechercheBS
     *      #indexationMemoire(java.io.File)
     */
//    @Override
    public void indexationMemoire(final File pReferentielMotifAAT) {
        System.out.println("DEBUT LuceneIndexRechercheBSImpl.indexationMemoire()");
        BufferedReader br = null;
        IndexWriter writer  = null;
        try {
        	// Renseignement des analyzer different pour les champs
        	// libelle et synonyme.
        	Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
        	analyzerPerField.put(CHAMP_LIBELLE, ANALYZER);
        	analyzerPerField.put(CHAMP_SYNONYME, AATLuceneAnalyzerUtil.getSynonymeAnalyzer());
        	PerFieldAnalyzerWrapper analyzers = new PerFieldAnalyzerWrapper(
        			new StandardAnalyzer(Version.LUCENE_36), analyzerPerField);
        	
            // Configuration pour indexer en memoire.
            IndexWriterConfig config =
            new IndexWriterConfig(Version.LUCENE_36, analyzers);
            config.setOpenMode(OpenMode.CREATE_OR_APPEND);
            
            writer = new IndexWriter(ramDirectory, config);
            
            String line = "";
            br = new BufferedReader(new FileReader(pReferentielMotifAAT));
            int i = 0;

//            System.out.println("Taille (en byte) memoire du thesaurus := "
//                          + ramDirectory.ramBytesUsed());
            
            // Affichage des tokens dans le libelle
            System.out.println("****** DEBUT : Affichage des tokens dans le libelle *******");
            int readerTermsIndexDivisor = writer.getConfig().getReaderTermsIndexDivisor();
            System.out.println("LuceneIndexRechercheImpl.indexationMemoire()" + readerTermsIndexDivisor);
//            TermEnum terms = writer.getReader().terms(new Term(CHAMP_LIBELLE));
//            if (terms.term() != null) {
//                do {
//                    Term term = terms.term();
//                    if (term.field().endsWith(CHAMP_LIBELLE))
//                    System.out.println("[" + term.field() + "] == "+ term.text());
//                } while(terms.next());
//            }
            System.out.println("****** FIN : Affichage des tokens dans le libelle *******");

            if (!(ramDirectory.sizeInBytes() > 0)) {
                while ((line = br.readLine()) != null) {
                	final String[] fields = line
							.split(ReferentielCSVReaderUtil.CSV_SERAPTOR);
					if (fields.length >= ReferentielCSVReaderUtil.CSV_SYNONYME_INDEX) {
						Motif motifEncours = ReferentielCSVReaderUtil
								.lireLigne(line);
						indexDocs(writer, motifEncours);
					}
					i++;
            	}
            }

			System.out.println("Nombre de documents index�s : ".concat(String
						.valueOf(i)));


            br.close();
            writer.close();
        } catch (Exception e) {
            System.out.println("Une erreur s'est produite lors de l'indexation du thesaurus : " + e);
        } finally {
        	try {
				if(null != br){
					br.close();
				}
				if(null != writer){
					writer.close();
				}
			} catch (CorruptIndexException e) {
	            System.out.println("Une erreur s'est produite lors de l'indexation du thesaurus : " + e);
			} catch (IOException e) {
	            System.out.println("Une erreur s'est produite lors de l'indexation du thesaurus : " +  e);
			}
        }
        System.out.println("FIN LuceneIndexRechercheBSImpl.indexationMemoire()");
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.cnamts.yt.metier.bs.LuceneIndexRechercheBS
     *      #rechercher(java.lang.String)
     */
//    @Override
    public List<Motif> rechercher(final String pLibelleSaisie) {

        System.out.println("DEBUT LuceneIndexRechercheBSImpl.rechercher()");
        System.out.println("La Saisie du Professionel de Sante := [" + pLibelleSaisie +"]");
        List<Motif> resultat = new ArrayList<Motif>();
        String saisieValide = extraireSaiSieValide(pLibelleSaisie);
        
        
        if (saisieValide.length() > 1) {

        	Reader reader = null;
            IndexSearcher searcher = null;
            try {
                // Normalisation de la saisie utilisateur.
                String saisieNormalise = supprAccent(saisieValide);
                System.out.println("La saisie valide := [" + saisieNormalise + "]");
                List<Motif> lListeMotif = null;
                if (lListeMotif == null) {
                    InputStream inputStream = resourceLoader.getResource("FichierReferentielMotifsAAT.csv").getInputStream();
                    File createTempFile = File.createTempFile("thesaurus", "cvs");
                	byte[] buffer = new byte[inputStream.available()];
                	inputStream.read(buffer);
                	OutputStream outStream = new FileOutputStream(createTempFile);
                	outStream.write(buffer);
					lListeMotif = ReferentielCSVReaderUtil.lireFichier(createTempFile);
					outStream.close();
				}
                // Indexation a chaud du referentiel
                indexationMemoire(lListeMotif);
//                reader = DirectoryReader.open(ramDirectory);
                searcher = new IndexSearcher(ramDirectory);
                resultat = singleTermSearch(saisieNormalise, searcher);

            } catch (ParseException e) {
                System.out.println(String.format(MSG_ERREUR_RECHERCHE, pLibelleSaisie) +  e);

            } catch (CorruptIndexException e1) {
            	System.out.println(String.format(MSG_ERREUR_RECHERCHE, pLibelleSaisie) + e1);
            } catch (IOException e2) {
            	System.out.println(String.format(MSG_ERREUR_RECHERCHE,
                		pLibelleSaisie) + e2);
            } finally {
                try {
                	
					if (null != reader) {
//						reader.close();
					}
				} catch (Exception e) {
					System.out.println(String.format(MSG_ERREUR_RECHERCHE,
	                		pLibelleSaisie) + e);
				}
            }
        }
        System.out.println("FIN LuceneIndexRechercheBSImpl.rechercher()");
        return resultat;
    }

    /**
     * Cette methode permet l'indexation l'objet {@link Motif}
     * en tant que document pour Apache lucene.
     * @param pWriter :
     *            indexeur.
     * @param pMotif :
     *            l'objet à indexer
     * @throws Exception :
     *             Notification d'erreur.
     */
    private void indexDocs(final IndexWriter pWriter, final Motif pMotif)
    throws Exception {
        if (pMotif != null) {
            Document doc = new Document();
            
            doc.add(new Field(CHAMP_CODE, pMotif.getCode(), Store.YES, Index.ANALYZED));
            doc.add(new Field(CHAMP_LIBELLE, pMotif.getLibelle(), Store.YES, Index.ANALYZED));
            doc.add(new Field(CHAMP_CODE_FONCTIONNEL, pMotif.getCodification(), Store.YES, Index.ANALYZED));
            
            if (pMotif.getSynonymes() != null) {
              List<String> tableDeSynonyme = pMotif.getSynonymes();
              String syn = "";
              for (String synonyme : tableDeSynonyme) {
                  syn = syn + " " + synonyme;
              }
              doc.add(new Field(CHAMP_SYNONYME, syn, Store.YES, Index.ANALYZED));
            }
            pWriter.addDocument(doc);
        }
    }

    /**
     * M�thode parmettant de valider si la saisie peut faire l'objet d'une
     * recherche par autocompl�tion.
     *
     * L'id�e est retirer tous les termes insignifiants contenus
     * dans la saisie de l'utulisateur.
     *
     * @param pSaisieUtilisateur :
     *            saisie utilisateur
     * @return : saisie valide
     */
    private String extraireSaiSieValide(final String pSaisieUtilisateur) {

    	String[] saisieTermes = pSaisieUtilisateur.split(ReferentielCSVReaderUtil.ESPACE);
        StringBuilder valideSaisie = new StringBuilder();
        // Parcours des termes contenus dans la saisie
        for (int i = 0; i < saisieTermes.length; i++) {
            // verifier que le terme n'est pas insignifiant
            String terme = saisieTermes[i];
            terme = supprAccent(terme.toLowerCase());
            Set<String>  listeInterdite = AATLuceneAnalyzerUtil.etendreFrenchStopWordSet();
            Set<String> listeSansAccent = normaliserListe(listeInterdite);
            if (listeSansAccent.contains(terme)
                    || ReferentielCSVReaderUtil.ESPACE.equals(terme)) {

                pSaisieUtilisateur.replace(terme, ReferentielCSVReaderUtil.VIDE);
            } else {
				if(i < (saisieTermes.length-1)){
					valideSaisie.append(terme).append(" ");
				}else {
					valideSaisie.append(terme);
				}
            }
        }
        return valideSaisie.toString();
    }

    /**
     *
     * @param pLibelleSaisie :
     *            chaine saisie.
     * @return saisie sans accent.
     */
    private String supprAccent(final String pLibelleSaisie) {
        return Normalizer.normalize(pLibelleSaisie, Form.NFD)
        .replaceAll("[^\\p{ASCII}]", "");
    }
    
    /**
     * Suppression des accents dans les mots contenu dans la liste en paramtre.
     * @param listeInterdite : la liste a transformer.
     */
    private Set<String> normaliserListe(Set<String> listeInterdite) {
		
    	Set<String> listeSansAccent = new HashSet<String>();
    	for (String objet : listeInterdite) {
    		String motStr = Normalizer.normalize(objet, Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    		listeSansAccent.add(motStr);
		}
    	return listeSansAccent;
	} 
    /**
     *
     * @param pTerm
     * @param pSearcher
     * @throws ParseException
     * @throws IOException
     */
    private List < Motif > singleTermSearch(String pTerm, IndexSearcher pSearcher)
    throws ParseException, IOException {

        BooleanQuery bq = new BooleanQuery();
        /**
         * R�sultat de la recherche.
         */
        List<Motif> resultat = new ArrayList<Motif>();
        String str = pTerm.trim().concat("*");

        QueryParser qp = new QueryParser(Version.LUCENE_36, CHAMP_LIBELLE, ANALYZER);
        qp.setDefaultOperator(Operator.AND);
        Query query;
		try {
			query = qp.parse(str);
			query.setBoost(LIBELLE_SCORE_BOOST);
	        
	        // Synonyme query
	        QueryParser qpSynonyme = new QueryParser(Version.LUCENE_36, CHAMP_SYNONYME, AATLuceneAnalyzerUtil.getSynonymeAnalyzer());
	        qpSynonyme.setDefaultOperator(Operator.AND);
	        Query querySynonyme = qpSynonyme.parse(pTerm);

	        bq.add(query, Occur.SHOULD);
	        bq.add(querySynonyme, Occur.SHOULD);

//	        TopScoreDocCollector collector = TopScoreDocCollector.create(MAX_RESULT, false);
//	        pSearcher.search(bq, collector);
//	        ScoreDoc[] hits = collector.topDocs().scoreDocs;
	        
	        ScoreDoc[] hits = pSearcher.search(query, null, MAX_RESULT).scoreDocs;
	        
	        System.out.println("======= { Nombre de motif trouv� := " + hits.length + " } ===========\n");
	        
	        for (int i = 0; i < hits.length; i++) {
	            ScoreDoc doc = hits[i];
	            Document document = pSearcher.doc(doc.doc);
	            Motif motif = new Motif();
	            motif.setCode(document.get(CHAMP_CODE));
	            motif.setLibelle(document.get(CHAMP_LIBELLE));
	            motif.setCodification(document.get(CHAMP_CODE_FONCTIONNEL));
	            resultat.add(motif);
//	            System.out.println(pSearcher.explain(bq, doc.doc));
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return resultat;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.cnamts.yt.metier.bs.LuceneIndexRechercheBS
     *      #indexationMemoire(java.util.List).
     */
//    @Override
	public void indexationMemoire(final List<Motif> pReferentielMotifAAT) {

    System.out.println("DEBUT LuceneIndexRechercheBSImpl.indexationMemoire()");
    IndexWriter writer  = null;
    try {
    	// Renseignement des analyzer different pour les champs
    	// libelle et synonyme.
    	Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
    	analyzerPerField.put(CHAMP_LIBELLE, AATLuceneAnalyzerUtil.getAnalyzer());
    	analyzerPerField.put(CHAMP_SYNONYME, AATLuceneAnalyzerUtil.getSynonymeAnalyzer());
    	PerFieldAnalyzerWrapper analyzers = new PerFieldAnalyzerWrapper(
    			new StandardAnalyzer(Version.LUCENE_36), analyzerPerField);
    	
        // Configuration pour indexer en memoire.
        IndexWriterConfig config =
        new IndexWriterConfig(Version.LUCENE_36, analyzers);

        writer = new IndexWriter(ramDirectory, config);
        int i = 0;

        System.out.println("Taille (en byte) memoire du thesaurus := "
                      + ramDirectory.sizeInBytes());


        if (!(ramDirectory.sizeInBytes() > 0)) {

        	for (Motif Motif : pReferentielMotifAAT) {
                indexDocs(writer, Motif);
                writer.commit();
                i++;
            }
        }
        writer.close();
        // Affichage des tokens dans le libelle
       System.out.println("****** DEBUT : Affichage des tokens dans le libelle *******");
//       DirectoryReader reader = DirectoryReader.open(ramDirectory);
//       Terms terms = SlowCompositeReaderWrapper.wrap(DirectoryReader.open(ramDirectory)).terms(CHAMP_LIBELLE);
       System.out.println("LuceneIndexRechercheImpl.indexationMemoire() => ");

//       Field fields = MultiFields.getFields(reader);
//       for (String field : fields) {
//    	   if (field.equals(CHAMP_LIBELLE)) {
//    		   Terms terms = fields.terms(field);
//           
//	           TermsEnum termsEnum = terms.iterator(null);
//	           int count = 0;
//	           while (termsEnum.next() != null) {
//	        	   System.out.println(termsEnum.term().utf8ToString());
//	               count++;
//	           }
//	           System.out.println("\n ********Nombre de token (LIBELLE) dans l'index : " + count + " ********");
//    	   }           
//       }
       
       System.out.println("****** FIN : Affichage des tokens dans le libelle *******");
       
        System.out.println("Nombre de documents index�s : ".concat(String
					.valueOf(i)));
        System.out.println("Taille (en byte) memoire du thesaurus := "
                + ramDirectory.sizeInBytes());
        writer.close();
    } catch (Exception e) {
        System.out.println("Une erreur s'est produite lors de l'indexation du thesaurus : " + e);
    } finally {
    	try {
			if(null != writer){
				writer.close();
			}
		} catch (CorruptIndexException e) {
            System.out.println("Une erreur s'est produite lors de l'indexation du thesaurus : " + e);
		} catch (IOException e) {
            System.out.println("Une erreur s'est produite lors de l'indexation du thesaurus : " + e);
		}
    }
    System.out.println("FIN LuceneIndexRechercheBSImpl.indexationMemoire()");
   }

//	public static void main(String[] args) {
//		String saisiePS = "Angine";
//		LuceneIndexRecherche service = new LuceneIndexRechercheImpl();
//		List<Motif> resultatRecherche = service.rechercher(saisiePS);
//		System.out.println(resultatRecherche.size() + " motif" + (resultatRecherche.size() > 1 ? "s" : "")
//				+ " correspondent � la recherche *****************");
//		for (Motif motifBO : resultatRecherche) {
//			System.err.println("Motif:= [" + motifBO.getLibelle() + "]");
//		}
//	}
}