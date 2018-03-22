package fr.cnam.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.cnam.dto.MotifView;
import fr.cnam.model.Motif;
import fr.cnam.service.LuceneIndexRecherche;
import fr.cnam.util.Constante;
import fr.cnam.util.MotifMapper;
import fr.cnam.util.ReferentielCSVReaderUtil;

@RestController
public class MotifControllerV0 {

	private final Logger logger = LoggerFactory.getLogger(MotifControllerV0.class);

	@Autowired
	private LuceneIndexRecherche luceneService;
	
    @Autowired
    private ResourceLoader resourceLoader;

	private Gson gson = new Gson();

	@GetMapping("/aatV0/motif")
	public String getMotif(@RequestParam("param") String msg) {
		logger.info("Le motif saisie par l'utilisateur est :" + msg);
		List<Motif> output = luceneService.rechercherV0(msg);
		List<MotifView> listMotifView = new ArrayList<MotifView>();
		if (null != output) {
			for (Motif motif : output) {
				listMotifView.add(MotifMapper.convertMotifToMotifView(motif));
			}
		}
		logger.info(gson.toJson(listMotifView));
		return gson.toJson(listMotifView);
	}

	@GetMapping(path = "/aatV0/motif/cim10")
	public ResponseEntity<?> getListOrdonneCIM10() {
		/**
		 * Motifs tries par lette
		 */
		List<String> listeCIM10 = new ArrayList<String>();
		BufferedReader br = null;
		String line = "";
		try {
			
			InputStream inputStream = resourceLoader.getResource("CIM10.csv").getInputStream();
	        File createTempFile = File.createTempFile("cim", "cvs");
	    	byte[] buffer = new byte[inputStream.available()];
	    	inputStream.read(buffer);
	    	OutputStream outStream = new FileOutputStream(createTempFile);
	    	outStream.write(buffer);
	    	
			br = new BufferedReader(new FileReader(createTempFile));
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(Constante.CSV_SERAPTOR);
				listeCIM10.add(fields[1]);
			}
			outStream.close();
		} catch (FileNotFoundException e1) {
			logger.error("Une erreur s'est produite lors de la recuperation des CIM10", e1);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		} catch (IOException e) {
			logger.error("Une erreur s'est produite lors de la recuperation des CIM10", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ResponseEntity<String>(gson.toJson(listeCIM10), HttpStatus.OK);
	}

	@GetMapping("/aatV0/motif/liste")
	public String getListOrdonneMotif() throws IOException {
		/**
		 * Motifs tries par lette
		 */
		SortedMap<String, List<MotifView>> mapMotif = null;
		mapMotif = new TreeMap<String, List<MotifView>>();
		
		InputStream inputStream = resourceLoader.getResource("FichierReferentielMotifsAAT.csv").getInputStream();
        File createTempFile = File.createTempFile("thesaurus", "cvs");
    	byte[] buffer = new byte[inputStream.available()];
    	inputStream.read(buffer);
    	OutputStream outStream = new FileOutputStream(createTempFile);
    	outStream.write(buffer);
    	
		List<Motif> listeTrieeMotif = ReferentielCSVReaderUtil.lireFichier(createTempFile);
		mapMotif = MotifMapper.listMotifToSortedMap(listeTrieeMotif);
		outStream.close();
		return gson.toJson(mapMotif);
	}
}
