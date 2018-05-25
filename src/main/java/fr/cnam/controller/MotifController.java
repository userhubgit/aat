package fr.cnam.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.cnam.dao.AvisRepository;
import fr.cnam.dao.EnqueteRepository;
import fr.cnam.dao.MotifAATRepository;
import fr.cnam.dto.MotifView;
import fr.cnam.model.Avis;
import fr.cnam.model.Enquete;
import fr.cnam.model.Motif;
import fr.cnam.model.MotifAAT;
import fr.cnam.service.LuceneIndexRecherche;
import fr.cnam.util.Constante;
import fr.cnam.util.DataBaseDump;
import fr.cnam.util.MotifMapper;
import fr.cnam.util.ReferentielCSVReaderUtil;

@RestController
public class MotifController {

	private final Logger logger = LoggerFactory.getLogger(MotifController.class);

	@Autowired
	private LuceneIndexRecherche luceneService;
	
    @Autowired
    private ResourceLoader resourceLoader;
    
	@Autowired
	DataSource dataSource;
	
	@Autowired
	private AvisRepository avisRepository;
	
	@Autowired
	private EnqueteRepository enqueteRepository;
	
	@Autowired
	private MotifAATRepository motifRepository;

	@Autowired
	private DataBaseDump dbBaseDump;
	
	private Gson gson = new Gson();

	@GetMapping("/aat/motif")
	public String getMotif(@RequestParam("param") String msg) {
		logger.info("Le motif saisie par l'utilisateur est :" + msg);
		List<Motif> output = luceneService.rechercher(msg);
		List<MotifView> listMotifView = new ArrayList<MotifView>();
		if (null != output) {
			for (Motif motif : output) {
				listMotifView.add(MotifMapper.convertMotifToMotifView(motif));
			}
		}
		
		return gson.toJson(listMotifView);
		
	}

	@GetMapping(path = "/aat/motif/cim10")
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

	@GetMapping("/aat/motif/liste")
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
		
	@GetMapping("/aat/avis")
	public ResponseEntity<Iterable<Avis>> listeAvis(HttpSession session) throws IOException {
		Iterable<Avis> avis = avisRepository.findAll();
		return new ResponseEntity<Iterable<Avis>>(avis, HttpStatus.OK);
	}
	
	@GetMapping("/aat/motifs")
	public ResponseEntity<Iterable<MotifAAT>> listeMotif(HttpSession session) throws IOException {
		Iterable<MotifAAT> motifAAT = motifRepository.findAll();
		return new ResponseEntity<Iterable<MotifAAT>>(motifAAT, HttpStatus.OK);
	}
	
	@GetMapping("/aat/enquetes")
	public ResponseEntity<Iterable<Enquete>> listeEnquetes(HttpSession session) throws IOException {
		Iterable<Enquete> enquete = enqueteRepository.findAll();
		return new ResponseEntity<Iterable<Enquete>>(enquete, HttpStatus.OK);
	}
	
	@RequestMapping(path = "/aat/dump", method = RequestMethod.GET)
	public ResponseEntity<Resource> download(String param) throws IOException {

		StringBuffer result = new StringBuffer();
		dbBaseDump.dumpDB(result);
		File file = dbBaseDump.createDumpFile(result);
	    Path path = Paths.get(file.getAbsolutePath());
	    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
		
		Date date = new Date();
		String fileName = "aat_dump";
	    return ResponseEntity.ok()
	            .contentLength(file.length())
	            .header("Content-Disposition", String.format("attachment; filename=\"" + fileName +"_"+ new SimpleDateFormat("YYYYMMddHHmmss").format(date) + ".sql"+ "\""))
	            .contentType(MediaType.parseMediaType("application/octet-stream"))
	            .body(resource);
	}
}
