package fr.cnam.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	public String form(HttpSession session, @RequestHeader HttpHeaders headers, @RequestParam("retour") String retour) {
		if (null != session) {
			if (! session.isNew()) {				
				if (null != session.getAttribute(SESSION_ATTR_ENQUETE)) {
					// Renseignement de l'heure de de debut de l'enquête
					// L'initialisation ce fait juste au premier passage.
					Enquete enquete = (Enquete) session.getAttribute(SESSION_ATTR_ENQUETE);
					if (null == enquete.getHorodateur1()) {
						enquete.setHorodateur1(new Date(System.currentTimeMillis()));
					}
				}
			} else {
				initSession(session, headers);
			}
		}
		return "recherche";
	}

	@RequestMapping(value = "/avis", method = RequestMethod.POST)
	public String avis(HttpSession session, 
			@CookieValue("libelle") String info,
			@CookieValue("clic-en-ce-moment") String actuel,
			@CookieValue("clic-liste-complete") String complete,
			@CookieValue("motif-origine") String origine,
			@RequestParam("recherche-commentaire") String commentaire,
			@CookieValue("resultat-recherche") String resultatRecherche,
			@RequestParam("complement-info-motif") String complement) {
		
		String libelle = info.replace(SEPARATEUR_ESPACE, " ");
		logger.info("Le libelle choisi pas le PS = {} ", libelle);
		
		if (null != session) {
			if (null != session.getAttribute(SESSION_ATTR_ENQUETE)) {

				// Renseignement de l'heure de de debut de l'enquête
				// L'initialisation ce fait juste au premier passage.
				Enquete enquete = (Enquete) session.getAttribute(SESSION_ATTR_ENQUETE);
				if (null == enquete.getHorodateur1()) {
					enquete.setHorodateur2(currentDate().getTime());
				}
				enquete.setHorodateur2(currentDate().getTime());
				// Motif
				MotifAAT motifAAT = new MotifAAT((MotifAAT) session.getAttribute(SESSION_ATTR_MOTIF));
				motifAAT.setLibelle(libelle);
				motifAAT.setSessionId(enquete.getIdentifiant());
				motifAAT.setClicActuel(actuel);
				motifAAT.setClicListeComplete(complete);
				motifAAT.setOrigine(origine.replace(SEPARATEUR_ESPACE, " "));
				motifAAT.setComplement(complement);
				motifAAT.setCommentaire(commentaire);
				motifAAT.setResultatRecherche(resultatRecherche);
				
				motifRepository.save(motifAAT);
			}
		}
		return "avis";
	}

	private Calendar currentDate() {
		Calendar cal = Calendar.getInstance();
		int heure = cal.get(Calendar.HOUR_OF_DAY);
		cal.set(Calendar.HOUR_OF_DAY, heure-2);
		return cal;
	}

//	@GetMapping("/remerciements")
	@RequestMapping(value = "/remerciements", method = RequestMethod.POST)
	public String remercier(HttpSession session, 
			HttpServletResponse response , 
			@CookieValue("avis-reponse1") String reponse1,
			@CookieValue("avis-reponse2") String reponse2, 
			@CookieValue("avis-commentaire") String commentaire,
			@RequestParam("avis-commentaire") String avisComment) {
		
		logger.info("reponse1= {} reponse2= {}  commentaire = {}", reponse1, reponse2, commentaire);

		if(null != session && 
				null != session.getAttribute(SESSION_ATTR_MOTIF) &&
				null !=  session.getAttribute(SESSION_ATTR_ENQUETE) && 
				null !=  session.getAttribute(SESSION_ATTR_AVIS)) {			
			Avis avis = new Avis();
			avis.setReponse1(reponse1);
			avis.setReponse2(reponse2);
			avis.setCommentaire(avisComment);
			
			
			Enquete enquete = new Enquete((Enquete)session.getAttribute(SESSION_ATTR_ENQUETE));
			enquete.setHorodateur3(currentDate().getTime());
			
			MotifAAT motifAAT = new MotifAAT((MotifAAT)session.getAttribute(SESSION_ATTR_MOTIF));
			motifAAT.setSessionId(((Enquete)session.getAttribute(SESSION_ATTR_ENQUETE)).getIdentifiant());
			
			avis.setLibelle(motifAAT.getLibelle());
			avis.setSessionId(enquete.getIdentifiant());
			
			enqueteRepository.save(enquete);
			avisRepository.save(avis);
			
		} else {
			try {
				response.sendRedirect("formulaire?retour=non");
			} catch (IOException e) {
				logger.error("Redirection KO", e);
			}
			return "recherche";
		}
		
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
}