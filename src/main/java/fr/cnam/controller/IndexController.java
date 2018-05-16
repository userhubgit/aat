package fr.cnam.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import fr.cnam.dao.AvisRepository;
import fr.cnam.dao.EnqueteRepository;
import fr.cnam.dao.MotifAATRepository;
import fr.cnam.model.Avis;
import fr.cnam.model.Enquete;
import fr.cnam.model.MotifAAT;

@Controller
public class IndexController {

	private final Logger logger = LoggerFactory.getLogger(IndexController.class);
	private final static String SESSION_ATTR_ENQUETE = "enquete";
	private final static String SESSION_ATTR_MOTIF = "motif";
	private final static String SESSION_ATTR_AVIS = "avis";
//	private final static String RETOUR_FORMULAIRE = "oui";
	
	/**
	 * FIXME Conrainte de document.cookie qui n'autorise pas d'espace.
	 */
	private final static String SEPARATEUR_ESPACE ="_";
	
	
	@Autowired
	private AvisRepository avisRepository;
	
	@Autowired
	private MotifAATRepository motifRepository;
	
	@Autowired 
	private EnqueteRepository enqueteRepository;

	@GetMapping("/")
	public String indexV0() {
		return "formulaireV0";
	}

	@GetMapping("/moteur")
	public String index(HttpSession session, @RequestHeader HttpHeaders headers) {
		if (null == session) {
			logger.info("La session est null");
		} else {
			logger.info("UI SESSION : {} ", session.getId());
			if (session.isNew()) {
				initSession(session, headers);
			} else {

				logger.info("Mise a jour des donnees");
			}
		}
		return "index";
	}

	@GetMapping("/formulaire")
	public String form(HttpSession session, @RequestParam("retour") String retour) {
		if (null != session) {
			if (null != session.getAttribute(SESSION_ATTR_ENQUETE)) {

				// Renseignement de l'heure de de debut de l'enquête
				// L'initialisation ce fait juste au premier passage.
				Enquete enquete = (Enquete) session.getAttribute(SESSION_ATTR_ENQUETE);
				if (null == enquete.getHorodateur1()) {
					enquete.setHorodateur1(new Date(System.currentTimeMillis()));
				}
//				if (retour.equalsIgnoreCase(RETOUR_FORMULAIRE)) {					
//					logger.info("*************** request {} ", retour);
//					enquete.setHorodateur4(new Date(System.currentTimeMillis()));
//					logger.info("*************** request {} ", new Gson().toJson(enquete));
//					
//				} else {
//					enquete.setHorodateur4(null);
//				}
				
			}
		}
		return "recherche";
	}

	@GetMapping("/avis")
	public String avis(HttpSession session, 
			@CookieValue("libelle") String info,
			@CookieValue("clic-en-ce-moment") String actuel,
			@CookieValue("clic-plus-frequent") String frequent,
			@CookieValue("clic-liste-complete") String complete,
			@CookieValue("motif-origine") String origine,
			@CookieValue("motif-complement") String complement) {
		
		
		logger.info("AFFICHAGE COOKIE ************* {} ", info);
		String libelle = info.replace(SEPARATEUR_ESPACE, " ");
		logger.info("AFFICHAGE LIBELLE CHOISI ************* {} ", libelle);
		
		logger.info("SESSION ************* {} ", session);
		if (null != session) {
			if (null != session.getAttribute(SESSION_ATTR_ENQUETE)) {

				// Renseignement de l'heure de de debut de l'enquête
				// L'initialisation ce fait juste au premier passage.
				Enquete enquete = (Enquete) session.getAttribute(SESSION_ATTR_ENQUETE);
				if (null == enquete.getHorodateur1()) {
					enquete.setHorodateur2(new Date(System.currentTimeMillis()));
				}
				enquete.setHorodateur2(new Date(System.currentTimeMillis()));
				// Motif
				MotifAAT motifAAT = new MotifAAT((MotifAAT) session.getAttribute(SESSION_ATTR_MOTIF));
				motifAAT.setLibelle(libelle);
				motifAAT.setSessionId(enquete.getIdentifiant());
				motifAAT.setClicActuel(actuel);
				motifAAT.setClicListeComplete(complete);
				motifAAT.setClicPlusFrequent(frequent);
				motifAAT.setOrigine(origine);
				motifAAT.setComplement(complement.replace(SEPARATEUR_ESPACE, " "));
				
				motifRepository.save(motifAAT);
			}
		}
		return "avis";
	}

	@GetMapping("/remerciements")
	public String remercier(HttpSession session, @CookieValue("avis-reponse1") String reponse1,
			@CookieValue("avis-reponse2") String reponse2, @CookieValue("avis-commentaire") String commentaire) {
		logger.info("reponse1= {} reponse2= {}  commentaire = {}", reponse1, reponse2, commentaire);

		Avis avis = new Avis();
		avis.setReponse1(reponse1);
		avis.setReponse2(reponse2);
		avis.setCommentaire(commentaire.replace(SEPARATEUR_ESPACE, " "));
		
		
		Enquete enquete = new Enquete((Enquete)session.getAttribute(SESSION_ATTR_ENQUETE));
		enquete.setHorodateur3(new Date(System.currentTimeMillis()));
		
		MotifAAT motifAAT = new MotifAAT((MotifAAT)session.getAttribute(SESSION_ATTR_MOTIF));
		motifAAT.setSessionId(((Enquete)session.getAttribute(SESSION_ATTR_ENQUETE)).getIdentifiant());
		
		avis.setLibelle(motifAAT.getLibelle());
		avis.setSessionId(enquete.getIdentifiant());
		
		enqueteRepository.save(enquete);
		avisRepository.save(avis);
		
		return "remerciements";
	}

	private void initSession(HttpSession session, HttpHeaders headers) {
		
		logger.info("Initialisation des donnees");
		logger.info("Navigateur : {}", headers.get("user-agent").get(0));

		Enquete enquete = new Enquete();
		enquete.setIdentifiant(session.getId());
		enquete.setNavigateur(headers.get("user-agent").get(0));

		MotifAAT motif = new MotifAAT();

		Avis avis = new Avis();

		session.setAttribute(SESSION_ATTR_ENQUETE, enquete);
		session.setAttribute(SESSION_ATTR_MOTIF, motif);
		session.setAttribute(SESSION_ATTR_AVIS, avis);
	}

	public void pgsqlConnexion() {

		System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
			return;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection connection = null;

		try {

			String url = "jdbc:postgresql://" + System.getenv("POSTGRESQL_SERVICE_HOST") + ":"
					+ System.getenv("POSTGRESQL_SERVICE_PORT_POSTGRESQL") + "/" + System.getenv("POSTGRESQL_DATABASE");

			String user = System.getenv("POSTGRESQL_USER");
			String pwd = System.getenv("POSTGRESQL_PASSWORD");

			System.out.println("----- URL  ---------------------- " + url);
			System.out.println("----- USER ---------------------- " + user);
			System.out.println("----- PWD  ---------------------- " + pwd);

			Map<String, String> envprop = System.getenv();

			Set<String> keySet = envprop.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String next = iterator.next();
				System.out.println("-----" + next + "--------------- : " + envprop.get(next));
			}

			connection = DriverManager.getConnection(url, "user83M", "WD1VOhUtrtvikiPU");

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
	}
}