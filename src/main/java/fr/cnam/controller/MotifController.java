package fr.cnam.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.cnam.dto.MotifView;
import fr.cnam.model.Motif;
import fr.cnam.service.LuceneIndexRecherche;
import fr.cnam.service.impl.LuceneIndexRechercheImpl;
import fr.cnam.util.Constante;
import fr.cnam.util.MotifMapper;
import fr.cnam.util.ReferentielCSVReaderUtil;


@RestController
public class MotifController {

    private final Logger logger = LoggerFactory.getLogger(MotifController.class);

	private LuceneIndexRecherche luceneService = new LuceneIndexRechercheImpl();

    private Gson gson = new Gson();
    

    @GetMapping("/aat/motif")
	public String getMotif(@RequestParam("param") String msg) {
		logger.info("Le motif saisie par l'utilisateur est :" + msg);
		List<Motif> output = luceneService .rechercher(msg);
		List<MotifView> listMotifView = new ArrayList<MotifView>();
		if (null != output) {
			for (Motif motif : output) {
				listMotifView.add(MotifMapper.convertMotifToMotifView(motif));
			}
		}
		return gson.toJson(listMotifView);
	}
    
    @GetMapping(path="/aat/motif/cim10")
	public ResponseEntity<?> getListOrdonneCIM10() {		
		/**
		 * Motifs triés par lette
		 */
		List<String> listeCIM10 = new ArrayList<String>();		
		BufferedReader br = null;
        String line =  "";
        try {
        	File cim10File = new File(getClass().getClassLoader().getResource("CIM10.csv").getFile());
			br = new BufferedReader(new FileReader(cim10File));		
			while ((line = br.readLine()) != null) {  
				String[] fields = line.split(Constante.CSV_SERAPTOR);
				listeCIM10.add(fields[1]);
			}
		} catch (FileNotFoundException e1) {
			logger.error("Une erreur s'est produite lors de la récupération des CIM10", e1);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			
		} catch (IOException e) {
			logger.error("Une erreur s'est produite lors de la récupération des CIM10", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		logger.info(gson.toJson(listeCIM10));
		return new ResponseEntity<String>(gson.toJson(listeCIM10), HttpStatus.OK);
	}
    
    @GetMapping("/aat/motif/liste")
	public String getListOrdonneMotif() throws IOException {		
		/**
		 * Motifs triés par lette
		 */
		SortedMap<String, List<MotifView>> mapMotif = null;
//		List<Motif> listeTrieeMotif = null;
//		Cache cache = cm.getCache("cacheMotif");
//		String keyList = "liste";
//		Element element = cache.get(keyList);
//		if (null == element) {
			mapMotif = new TreeMap<String, List<MotifView>>();
			List<Motif> listeTrieeMotif = ReferentielCSVReaderUtil.lireFichier(new File(getClass().getClassLoader().getResource("FichierReferentielMotifsAAT.csv").getFile()));
			mapMotif = MotifMapper.listMotifToSortedMap(listeTrieeMotif);
//			cache.put(new Element(keyList, mapMotif));
//		} else {
//			mapMotif = (SortedMap<String, List<MotifView>>) element.getObjectValue();
//		}
		logger.info(gson.toJson(mapMotif));
		return gson.toJson(mapMotif);
	}
}
