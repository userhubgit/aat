package fr.cnam.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String indexV0() {
    	System.out.println("TENTATIVE DE CONNEXION VIA JDBC *********");
    	pgsqlConnexion();
        return "formulaireV0";
    }

    @GetMapping("/moteur")
    public String index() {
        return "index";
    }
    
    @GetMapping("/formulaire")
    public String form() {
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

			connection = DriverManager.getConnection(
					"jdbc:postgresql://"
							+ System.getenv("POSTGRESQL_SERVICE_HOST") 
							+ ":" 
							+ System.getenv("POSTGRESQL_SERVICE_PORT_POSTGRESQL") 
							+ "/" 
							+ System.getenv("POSTGRESQL_DATABASE"), 
							System.getenv("POSTGRESQL_USER"), 
							System.getenv("POSTGRESQL_PASSWORD"));

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