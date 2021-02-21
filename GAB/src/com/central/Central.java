package com.central;


import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.guichetAutomatiqueBancaire.ConfirmerController;
import com.guichetAutomatiqueBancaire.Connexion;
import com.guichetAutomatiqueBancaire.DepotController;
import com.guichetAutomatiqueBancaire.RetraitController;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.application.Application;
import javafx.stage.Stage;


public class Central  {

	private static Connection connexion = null;
	private static Statement statement = null;
	private static ResultSet resultSet = null;
	private static String requete = null;
	private static String dateExpiration = null;

	// permet d'établir une connexion avec la base de donnée interne
	
	public static Connection connexionBddInterne() throws SQLException {

		Connection conx = Connexion.connect(
				"jdbc:mysql://localhost/Bdd_interne?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
				"l2d1", "l2d1l2d1");

		return conx;
	}

	// permet de fermer les objets qui permettent d'intéragir avec la base de donnée
	public static void closeObject() throws SQLException {
		try{
			if(connexion != null)
				
				connexion.close();
			
			if(statement != null)
				
				statement.close();
			
			if(resultSet != null)
				
				resultSet.close();
			
		} catch(SQLException e){
			
			System.err.println(e);
		}
	}

	// renvoie la date du jour avec le format indiqué "année-mois-jour"
	
	public static String dateAujourdhui() {

		final Date date = new Date();
		return new SimpleDateFormat("yyyy-MM-dd").format(date);

	}

	// renvoie la date du jour et la date exacte avec le format indiqué "année-mois-jour-heure-minutes-secondes"
	
	public static String date() {

		Date date = new Date();
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	// méthode qui permet de vérifier si la carte est valide en comparant sa date d'expiraion avec  la date du jour 
	
	public static boolean carteValide(String numeroCarte) throws SQLException {
		boolean validite = false;

		try{

			connexion = Central.connexionBddInterne(); //  établir une connexion avec la base de donnée interne 
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); // instancier  un objet de type Statement afin d'exécuter des requêtes
			requete = "SELECT date_expir FROM carte where num_carte = '" + numeroCarte + "'"; // requete à exécuter

			resultSet = statement.executeQuery(requete); // exécution de la requête et stocker le résultat dans un objet de type ResulSet
			resultSet.next(); // aller sur le première ligne du résultat
			dateExpiration = resultSet.getString("date_expir"); //récuperer la date d'expiration de la carte
			
			validite = (dateExpiration.compareTo(Central.dateAujourdhui()) >= 0); // la méthode compareTo retourne un entier positif si dateExpiration > date du jour

		}catch (SQLException e){

			System.err.println(e);

		}finally{

			Central.closeObject(); // fermer les objets

		}

		return validite; // true : carte valide, flase : invalide

	}

	// retourne le solde d'un client interne
	public static float getSoldeInterne(String numeroCarte) throws SQLException {

		float solde = 0.0f;
		try{
			connexion = Central.connexionBddInterne(); //établir une connexion à la Bdd interne
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //création d'un objet de type statement
			requete = "SELECT solde FROM compte  WHERE  num_client IN (SELECT num_client FROM carte WHERE num_carte = '"
					+ numeroCarte + "') "; // requête pour avoir le solde d'un client interne dont le numéro de carte correspond à numeroCarte passé en paramètre
			resultSet = statement.executeQuery(requete); //exécution de la requête et la stocker dans un ResultSet
			resultSet.next(); // aller sur le première ligne du résultat
			solde = resultSet.getFloat(1); // recupérer le solde
			operation(numeroCarte, "Consultation solde ", 0); // enregistrer l'opération effectuée dans la table operation de notre bdd interne

		}catch(SQLException e){
			
			System.err.println(e);
			
		}finally{

			closeObject(); // fermer les objet une fois on aura fini de travailler avec.
		}
		
		return solde;

	}

	/*
	 * mise à jour du solde du client détenteur de la carte (dont le numéro de carte
	 * correspand au "numeroCarte" passé en paramètre ) après avoir effectué un
	 * retrait en fonction du montant retiré cela met à jour aussi les 2 types de
	 * seuils
	 */
	public static void retraitInterne(String numeroCarte, float montant) throws SQLException {
		try{

			float nouveauSolde = getSoldeInterne(numeroCarte) - montant;
			connexion = Central.connexionBddInterne();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			requete = "UPDATE compte set solde = '" + nouveauSolde
					+ "' where num_client IN (SELECT num_client From carte WHERE num_carte = '" + numeroCarte + "') ";

			statement.executeUpdate(requete);
			Central.setMontantRestant(numeroCarte, montant);
			Central.operation(numeroCarte, "Retrait", montant);

		}catch(SQLException e){

			System.err.println(e);

		}finally{
			
			closeObject();
		}

	}

	/*
	 * mise à jour du solde du client détenteur de la carte (dont le numéro de carte
	 * correspand au "numeroCarte" passé en paramètre ) après avoir effectué un
	 * dépot d'espèces en fonction du montant déposé
	 */

	public static void depotInterne(String numeroCarte, float montant) throws SQLException {
		try{
			
			float nouveauSolde = Central.getSoldeInterne(numeroCarte) + montant;
			connexion = Central.connexionBddInterne();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			requete = "UPDATE compte set solde = '" + nouveauSolde
					+ "' WHERE num_client IN (SELECT num_client FROM carte WHERE num_carte = '" + numeroCarte + "')";

			statement.executeUpdate(requete);
			Central.operation(numeroCarte, "Dépôt", montant);

		}catch(SQLException e){
			
			System.err.println(e);
			
		}finally{
			
			closeObject();
		}

	}

	// renvoie le mot de passe correspondent à la carte insérée
	
	public static String getMotDePasse(String numeroCarte) throws SQLException {

		String motDePasse = null;

		try{
			
			connexion = Central.connexionBddInterne();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			requete = "SELECT code from carte where num_carte = '" + numeroCarte + "'";
			resultSet = statement.executeQuery(requete);
			resultSet.next();
			motDePasse = resultSet.getString("code");

		}catch(SQLException e){

			System.err.println(e);
			
		}finally{
			
			closeObject();
		}
		
		return motDePasse;
	}

	// vérifie l'existance de la carte insérée dans la base de donnée interne

	public static boolean existeInterne(String numeroCarte) throws SQLException {
		
		boolean existe = false;
		
		try{

			connexion = Central.connexionBddInterne();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			requete = "SELECT num_carte FROM carte Where num_carte = '" + numeroCarte + "'";
			resultSet = statement.executeQuery(requete);
			resultSet.next();
			existe = (resultSet.getRow() != 0);

		}catch(SQLException e){
			
			System.err.println(e);
			
		}finally{
			
			closeObject();
			
		}

		return existe;

	}

	// // retourne le Seuil Hebdomadaire d'un client interne
	
	public static float getSeuilHebdomadaire(String numeroCarte) throws SQLException {
		
		float seuilHebd = 0.f;

		try{

			connexion = Central.connexionBddInterne();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			requete = "SELECT seuil_hebd FROM compte WHERE num_client IN (SELECT num_client FROM carte WHERE num_carte = '"
					+ numeroCarte + "')";
			resultSet = statement.executeQuery(requete);
			resultSet.next();
			seuilHebd = resultSet.getFloat("seuil_hebd");

		}catch(SQLException e){
			
			System.err.println(e);
			
		}

		return seuilHebd;
	}

	// ca permet de récupérer le Seuil Quotidien d'un client interne
	
	public static float getSeuilQuotidien(String numeroCarte) throws SQLException {
		float seuilquot = 0.f;

		try{

			connexion = Central.connexionBddInterne();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			requete = "SELECT seuil_quot FROM compte WHERE num_client IN (SELECT num_client FROM carte WHERE num_carte = '"
					+ numeroCarte + "')";
			resultSet = statement.executeQuery(requete);
			resultSet.next();
			seuilquot = resultSet.getFloat("seuil_quot");

		}catch(SQLException e){
			
			System.err.println(e);
		}

		return seuilquot;

	}

	/* permet de mettre à jour le montant du seuil quotidien et hebdomadaire d'un
	 
	 client interne  après avoir effectué une opération */
	
	public static void setMontantRestant(String numeroCarte, float montant) throws SQLException {
		float montantRestant;

		try{

			connexion = Central.connexionBddInterne();

			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			montantRestant = getSeuilQuotidien(numeroCarte) - montant;
			requete = "UPDATE compte set seuil_quot = '" + montantRestant
					+ "' WHERE num_client IN (SELECT num_client FROM carte WHERE num_carte = '" + numeroCarte + "')";
			statement.executeUpdate(requete);

			montantRestant = getSeuilHebdomadaire(numeroCarte) - montant;

			requete = "UPDATE compte set seuil_hebd = '" + montantRestant
					+ "' WHERE num_client IN (SELECT num_client FROM carte WHERE num_carte = '" + numeroCarte + "')";
			statement.executeUpdate(requete);

		}catch(SQLException e){
			
			System.err.println(e);
		}

	}
	
	/* cela permet de modifier le mot de passe d'une carte bancaire dans la base de
	
	 donnée depuis notre programme. */

	public static void setMotDePasse(String numeroCarte, String motDePasse)throws SQLException, UnsupportedEncodingException, NoSuchAlgorithmException {

		try{
			connexion = Central.connexionBddInterne();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			requete = "UPDATE carte SET code = '" + Crypte.MD5(motDePasse) + "' WHERE num_carte = '" + numeroCarte
					+ "' ";
			
			
			statement.executeUpdate(requete); // requête de mise à jour
			
		}catch(SQLException e) {
			
			System.err.println(e);
		
		}finally{
			
			closeObject();
		}

	}

	// Cette méthode permet d'enregistrer une opération dans la base de donnée interne
	
	public static void operation(String numeroCarte, String operation, float montant) throws SQLException {

		connexion = Central.connexionBddInterne();
		statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		requete = "SELECT num_client FROM carte WHERE num_carte = '" + numeroCarte + "'";
		resultSet = statement.executeQuery(requete);
		resultSet.next();
		String numeroClient = resultSet.getString("num_client");
		requete = "INSERT INTO `operation` (`date`, `action_oprt`,`montant`, `num_client`, `num_carte`) VALUES ('"
				+ Central.date() + "', '" + operation + "','"+montant+"' ,'" + numeroClient + "', '" + numeroCarte + "');";
		statement.executeUpdate(requete);

	}

	// cela permet de supprimer une carte de notre base de donnée interne 
	
	// ce sera utiliser si le client se trompe dans son code à 3 reprises ce qui
	// bloquera sa carte en la supprimant de la BDD
	
	public static void supprimerCarte(String numeroCarte) throws SQLException {

		
		try{
			connexion = Central.connexionBddInterne();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			requete = "DELETE FROM carte WHERE  num_carte = '" + numeroCarte + "'";
			statement.executeUpdate(requete);

		}catch(SQLException e){
			
			System.err.println(e);
			
		}finally{

			closeObject();
		}

	}

	// cela permet de supprimer un client de notre base de donnée interne

	public static void supprimerClient(String numeroCarte) throws SQLException {
		
		try{
			connexion = Central.connexionBddInterne();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			requete = "DELETE FROM client WHERE  num_carte = '" + numeroCarte + "'";
			statement.executeUpdate(requete);

		}catch(SQLException e){
			
			System.err.println(e);
			
		}finally{
			
			closeObject();
		}

	}
	
	/* cela permet de supprimer le compte d'un client interne de notre basse de
	 donnée interne */

	public static void supprimerCompte(String numeroCarte) throws SQLException {

		try{
			connexion = Central.connexionBddInterne();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			requete = "DELETE FROM compte WHERE  num_carte = '" + numeroCarte + "'";
			statement.executeUpdate(requete);

		}catch(SQLException e){

			System.err.println(e);

		}finally{

			closeObject();
		}

	}

	// cette méthode permet d'imprimer le rib d'un client interne de la banque
	
	public static final void imprimerRib(String numeroCarte) throws DocumentException, IOException, SQLException{
		
		Document rib = new Document(PageSize.A5);
		StringBuilder sb = new StringBuilder();

		try{

			connexion = Central.connexionBddInterne();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			PdfWriter.getInstance(rib, new FileOutputStream("/home/rilles/projet_L2D1/GAB/src/com/guichetAutomatiqueBancaire/Rib.pdf"));
			
			// requete qui permet de récupérer les informations d'un client à afficher dans le RIB
			
			requete = "SELECT com.num_compte, com.RIB, com.IBAN, com.num_client, com.num_carte, cli.nom, cli.prenom\r\n"

					+ "FROM compte as com, client as cli WHERE\r\n"

					+ " com.num_client  = cli.num  AND cli.num_carte = '" + numeroCarte + "';";

			resultSet = statement.executeQuery(requete);
			resultSet.next();

			rib.open();
			// informations du client à afficher dans le RIB
			
			sb.append("\n\nBanque : D-CARD\n");
			sb.append("Guichet  : 02998\n");
			sb.append("N° de compte   : " + resultSet.getString("com.num_compte") + "\n");
			sb.append("Nom du titulaire : " + resultSet.getString("cli.nom") + " " + resultSet.getString("cli.prenom")
					+ "\n");
			sb.append(("N° client  : " + resultSet.getString("com.num_client") + "\n"));
			sb.append(("RIB : " + resultSet.getString("com.RIB") + "\n"));
			sb.append(("IBAN : " + resultSet.getString("com.IBAN") + "\n"));
			sb.append(("BIC : CRLYFRPP " + "\n"));
			
			resultSet.first();

			rib.addTitle("Relevé d'identité bancaire (RIB) "+resultSet.getString("cli.nom")+" "+resultSet.getString("cli.prenom"));
			Font font = new Font(Font.FontFamily.TIMES_ROMAN,20,Font.BOLD,BaseColor.BLACK); //permer de styliser un paragraphe
			Paragraph paragraphe = new Paragraph("D-CARD",font);
			paragraphe.setAlignment(Element.ALIGN_CENTER);
			
			rib.add(paragraphe);
			
			Font font2 = new Font(Font.FontFamily.TIMES_ROMAN,15,Font.BOLD,BaseColor.BLACK);
			Paragraph paragraphe1 = new Paragraph("\n\nRIB : Relevé d'identité  bancaire",font2);
			paragraphe1.setAlignment(Element.ALIGN_LEFT);
			
			rib.add(paragraphe1);
			
			Font font3 = new Font(Font.FontFamily.TIMES_ROMAN,16,0, BaseColor.BLACK);
			
			rib.add(new Paragraph(sb.toString(),font3));
			rib.close();
			operation(numeroCarte, "Impression Rib", 0);
			File ribFile = new File("/home/rilles/projet_L2D1/GAB/src/com/guichetAutomatiqueBancaire/Rib.pdf");
			
			
			Application a = new Application() {
				
				 @Override
                 public void start(Stage stage){ }
            };
             
             a.getHostServices().showDocument(ribFile.toURI().toString());   
		
             
             
		
			
		}catch(FileNotFoundException e){

			System.err.println(e);

		}catch(DocumentException e){

			System.err.println(e);

		}catch(SQLException e){
			
			System.err.println(e);
			
		}finally{
			
			closeObject();
		}

	}
	
	// cette méthode permet d'imprimer l'historique des opérations  d'un client interne de la banque
	
    public static final void imprimerHistorique(String numeroCarte) throws IOException, SQLException  {

        

        Document historique = new Document(PageSize.A5); 

        StringBuilder sb = new StringBuilder();  

        try{

        	connexion = Central.connexionBddInterne();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            PdfWriter.getInstance(historique, new FileOutputStream("/home/rilles/projet_L2D1/GAB/src/com/guichetAutomatiqueBancaire/Historique.pdf"));

         // requete qui permet de récupérer les informations d'un client concernant les opération qu'il avait effectuer afin de les afficher dans l'historqiue
			    
            requete = "SELECT opr.num_client, opr.date, opr.action_oprt, opr.montant, cli.nom, cli.prenom "

                       + "FROM operation AS opr, client AS cli\r\n"

                       + "WHERE opr.num_client = cli.num AND opr.num_carte = '"+numeroCarte+"'"; 

                

            resultSet =  statement.executeQuery(requete);
            historique.open();
            resultSet.first();
            
            historique.addTitle("Historique "+resultSet.getString("cli.nom")+" "+resultSet.getString("cli.prenom")); 
            Font font = new Font(Font.FontFamily.TIMES_ROMAN,20,Font.BOLD,BaseColor.BLACK);
			Paragraph paragraphe = new Paragraph("D-CARD",font);
			paragraphe.setAlignment(Element.ALIGN_CENTER);
			
			historique.add(paragraphe);
			
			Font font2 = new Font(Font.FontFamily.TIMES_ROMAN,15,Font.BOLD,BaseColor.BLACK);
			Paragraph paragraphe1 = new Paragraph("\n\nHistorique des opérations de M/Mme : "+resultSet.getString("cli.nom")+" "+resultSet.getString("cli.prenom")+"\n\n",font2);
			paragraphe1.setAlignment(Element.ALIGN_LEFT);
			
			historique.add(paragraphe1);
            
           
            
            while(resultSet.next()) {

                 sb.append("N° client : "+ resultSet.getString("opr.num_client")+" \t \t");

                 sb.append("\t \tDate : "+ resultSet.getString("opr.date")+" \t");

                 sb.append("\t \t \t Opération : "+ resultSet.getString("opr.action_oprt")+" \t \t \t");
                 sb.append("\t Montant : "+resultSet.getString("opr.montant")+"\n \n");

            }
            resultSet.first();

                
            	Font font3 = new Font(Font.FontFamily.TIMES_ROMAN,16,0, BaseColor.BLACK);

                historique.add(new Paragraph(sb.toString(),font3));

                historique.close();// fermer le fichier;
                
                operation(numeroCarte, "Impression _Historique", 0); 
                
                File historiqueFile = new File("/home/rilles/projet_L2D1/GAB/src/com/guichetAutomatiqueBancaire/Historique.pdf");
                
                Application a = new Application() {

                    @Override
                    public void start(Stage stage)
                    {
                    }
                };
                a.getHostServices().showDocument(historiqueFile.toURI().toString());
   			

               
                
         }catch(SQLException e){  

        	 System.err.println(e);

        }catch(DocumentException e){

        	System.err.println(e);

        }catch(FileNotFoundException e) {

        	System.err.println(e);

        }finally{
        	closeObject();
             
        }

    }
   
    //méthode qui permet d'imprimer le solde d'un client interne
    
    public static void imprimerSolde (String numeroCarte){

        Document solde = new Document(PageSize.A5);
        StringBuilder sb = new StringBuilder();
        
        try{

        	connexion = Central.connexionBddInterne();
 			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            PdfWriter.getInstance(solde, new FileOutputStream("/home/rilles/projet_L2D1/GAB/src/com/guichetAutomatiqueBancaire/Solde.pdf"));
            
            requete = "SELECT * FROM compte WHERE num_carte = '"+numeroCarte+"'";

            resultSet = statement.executeQuery(requete);
            resultSet.next();
            
            solde.open();

            sb.append("Banque D-CARD \n");
            sb.append("Ticket de consultation de solde\n");
            sb.append(Central.date()+"\n");
            sb.append("N° Compte : "+(resultSet.getString("num_compte"))+"\n");                        
            sb.append("Votre solde est de : "+resultSet.getString("solde")+" euros \n \n");
            sb.append("(Veuillez conserver ce ticket)\n");
            sb.append("Merci de votre visite à bientôt\n");
            
            solde.addTitle("solde");
            
            
			Font font = new Font(Font.FontFamily.TIMES_ROMAN,20,Font.BOLD,BaseColor.BLACK);
			Paragraph paragraphe = new Paragraph("D-CARD\n\n",font);
			paragraphe.setAlignment(Element.ALIGN_CENTER);
			
			solde.add(paragraphe);
			
			Font font3 = new Font(Font.FontFamily.TIMES_ROMAN,16,0, BaseColor.BLACK);
            solde.add(new Paragraph(sb.toString(),font3));
            solde.close();
            
            operation(numeroCarte, "Impression_Solde", 0);

            File soldeFile = new File("/home/rilles/projet_L2D1/GAB/src/com/guichetAutomatiqueBancaire/Solde.pdf");
            
            Application a = new Application() {

                @Override
                public void start(Stage stage)
                {
                }
            };
            a.getHostServices().showDocument(soldeFile.toURI().toString());

           }catch(SQLException e){

        	   System.err.println(e);
                 
           }catch(DocumentException e){

        	   System.err.println(e);
                 
          }catch(FileNotFoundException e){

        	  System.err.println(e);

         }finally{

        	 try{
        		 closeObject();
			
        	 }catch(SQLException e){
			
        		 System.err.println(e);
        	 }      
		}

     }
    
  //méthode qui permet d'imprimer un ticket pour  un client interne après avoir effectué une opération de dépôt ou de retrait d'espèces
    
    public static void getTicket(String numeroCarte) throws SQLException, IOException {

        Document ticket = new Document(PageSize.A5);
        StringBuilder sb = new StringBuilder();

        try{

        	connexion = Central.connexionBddInterne();
 			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            PdfWriter.getInstance(ticket, new FileOutputStream("/home/rilles/projet_L2D1/GAB/src/com/guichetAutomatiqueBancaire/Ticket.pdf"));
            requete = "SELECT num_compte FROM compte WHERE num_carte = '"+numeroCarte+"'";
            resultSet = statement.executeQuery(requete);

            resultSet.next();
            ticket.open();// ouvrir le fichier pdf;
            
            Font font = new Font(Font.FontFamily.TIMES_ROMAN,20,Font.BOLD,BaseColor.BLACK);
			Paragraph paragraphe = new Paragraph("D-CARD\n\n",font);
			paragraphe.setAlignment(Element.ALIGN_CENTER);
			
			ticket.add(paragraphe);
            

            //ajouter les informations au fichier ;
            sb.append("Banque D-CARD \n");
            
            if (ConfirmerController.getModeOperation().equals("Depot")) {
            	
            	 sb.append("Ticket de Dépôt \n");
            	
            }else{
            	
            	 sb.append("Ticket de Retrait \n");
            }
           
            
           
            sb.append(Central.date()+"\n");
            
            if (ConfirmerController.getModeOperation().equals("Depot")) {
            	
            	sb.append("Type d'opération : Dépôt d'espèces \n");
            	
            }else{
            	
            	sb.append("Type d'opération : Retrait d'espèces \n");
            }
            
            sb.append("N° Compte : "+(resultSet.getString("num_compte"))+"\n");

            if(ConfirmerController.getModeOperation().equals("Depot")){
            	
            	sb.append("Montant : "+DepotController.getMontantDepot()+" euros \n");
            	
            }else{
            	
            	sb.append("Montant : "+RetraitController.getMontantRetrait()+" euros \n");
            }
            
           

             sb.append("(Veuillez conserver ce ticket)\n");
             sb.append("Merci de votre visite à bientôt\n");
             ticket.addTitle("Ticket de "+ConfirmerController.getModeOperation());
             Font font3 = new Font(Font.FontFamily.TIMES_ROMAN,16,0, BaseColor.BLACK); // styliser un paragraphe
             ticket.add(new Paragraph(sb.toString(),font3));

             ticket.close();

             operation(numeroCarte, "Impression_Ticket", 0);
             
             File ticketFile = new File("/home/rilles/projet_L2D1/GAB/src/com/guichetAutomatiqueBancaire/Ticket.pdf");
             
             Application a = new Application() {

                 @Override
                 public void start(Stage stage)
                 {
                 }
             };
             a.getHostServices().showDocument(ticketFile.toURI().toString());

         }catch(SQLException e){
        	 
        	 System.err.println(e);

         }catch(DocumentException e){

        	 System.err.println(e);

         }catch(FileNotFoundException e){
        	 
        	 System.err.println(e);
        	 
         }finally{

        	 closeObject();
         }

     }

	
}
