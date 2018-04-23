package fr.cnam.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import fr.cnam.model.Avis;
import fr.cnam.model.Enquete;
import fr.cnam.model.MotifAAT;

@Controller
public class IndexController {
	
	private final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping("/")
    public String indexV0() {
        return "formulaireV0";
    }

    @GetMapping("/moteur")
    public String index(HttpSession session, @RequestHeader HttpHeaders headers) {
    	if(null == session){
    		logger.info("La session est null");
    	}else{
    		logger.info("UI SESSION : {} ", session.getId());
    		if(session.isNew()){
    			initSession(session, headers);
    		}else{
    			
    			logger.info("Mise a jour des donnees");
    		}
    	}
        return "index";
    }
    
    @GetMapping("/formulaire")
    public String form(HttpSession session) {
        return "recherche";
    }
    
    @GetMapping("/avis")
    public String avis() {
        return "avis";
    }
    
    @GetMapping("/remerciements")
    public String remercier() {
        return "remerciements";
    }
    
	public void pgsqlConnexion() {

		System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
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
				System.out.println("-----"+ next +"--------------- : " + envprop.get(next));
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
	
	private void initSession(HttpSession session, HttpHeaders headers){
		logger.info("Initialisation des donnees");
		logger.info("Navigateur : {}", headers.get("user-agent").get(0));
		
		Enquete enquete = new Enquete();
		enquete.setIdentifiant(session.getId());
		enquete.setNavigateur(headers.get("user-agent").get(0));
		
		MotifAAT motif = new MotifAAT();
		
		Avis avis = new Avis();
		
		session.setAttribute("enquete", enquete);
		session.setAttribute("motif", motif);
		session.setAttribute("avis", avis);
	}
}