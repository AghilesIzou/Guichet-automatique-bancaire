package com.central;

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

import com.guichetAutomatiqueBancaire.ConfirmerController;
import com.guichetAutomatiqueBancaire.Connexion;
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

public class Routage  {
	
	private static Connection connexion = null;
	private static Statement statement = null;
	private static ResultSet resultSet = null;
	private static String requete = null;
	private static String dateExpiration = null;
	
	// permet d'établir une connexion avec la base de donnée externe
		
	public static Connection connexionBddExt() throws SQLException {

		Connection conx = Connexion.connect(
			"jdbc:mysql://localhost/Bdd_externe?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
			"l2d1", "l2d1l2d1");

			return conx;
	}
		
	// permet de fermer les objets qui permettent d'intéragir avec la base de donnée
	public static void closeObject() throws SQLException {
			
		try{
				if (connexion != null)
					connexion.close();
				if (statement != null)
					statement.close();
				if (resultSet != null)
					resultSet.close();
		}catch(SQLException e) {
				
			System.err.println(e);
		}
	
	}
		
		// permet de vérifier si la carte est valide en comparant sa date d'expiraion avec  la date du jour 
		
	public static boolean carteValide(String numeroCarte) throws SQLException {
			
		boolean validite = false;

		try{

			connexion = Routage.connexionBddExt(); //  établir une connexion avec la base de donnée externe
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); // instancier  d'un objet de type Statement
			requete = "SELECT date_expir FROM carte2 where num_carte = '" + numeroCarte + "'"; // requete à exécuter

			resultSet = statement.executeQuery(requete); // exécution de la requête
			resultSet.next();// aller sur le premier enregistrement du résultat
			dateExpiration = resultSet.getString("date_expir"); //récuperer la date d'expiration de la carte
				
			validite = (dateExpiration.compareTo(Central.dateAujourdhui()) >= 0); // la méthode compareTo retourne un entier positif si dateExpiration > date du jour

		}catch (SQLException e){

			System.err.println(e);

		}finally{

			Central.closeObject(); // fermer les objets
		}

		return validite;

	}
		
	// vérifie l'existance de la carte insérée dans la base de donnée externe
		
	public static boolean existeExterne(String numeroCarte) throws SQLException {
			
		boolean existe = false;
			
		try{
			
			connexion = Routage.connexionBddExt();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			requete = "SELECT num_carte FROM carte2 Where num_carte = '" + numeroCarte + "'";
			resultSet = statement.executeQuery(requete);
			resultSet.next();
			existe = (resultSet.getRow() != 0);

		}catch(SQLException e) {
				
			System.err.println(e);
			
		}finally{
			
			closeObject();
		}

		return existe;

	}
		
		
	// retourne le solde d'un client externe
	
	public static float getSoldeExterne(String numeroCarte) throws SQLException {

		float solde = 0.0f;
			
		try{
			
			connexion = Routage.connexionBddExt();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			requete = "SELECT solde FROM compte  WHERE  num_client IN (SELECT num_client FROM carte2 WHERE num_carte = '"
						+ numeroCarte + "') ";
			
			resultSet = statement.executeQuery(requete);
			resultSet.next();
			solde = resultSet.getFloat(1);
			operation(numeroCarte, "Consultation solde ", 0);

		}catch(SQLException e){
				
			System.err.println(e);
				
		}finally{

			closeObject();
		}
			
		return solde;

	}
		
	// Cette méthode permet d'enregistrer une opération effectuée par un client externe dans la base de donnée externe
	public static void operation(String numeroCarte, String operation, float montant) throws SQLException {

			connexion = Routage.connexionBddExt();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			requete = "SELECT num_client FROM carte2 WHERE num_carte = '" + numeroCarte + "'";
			resultSet = statement.executeQuery(requete);
			resultSet.next();
			String numeroClient = resultSet.getString("num_client");
			requete = "INSERT INTO `operation` (`date`, `action_oprt`,`montant`, `num_client`, `num_carte`) VALUES ('"
					+ Central.date() + "', '" + operation + "','"+montant+"' ,'" + numeroClient + "', '" + numeroCarte + "');";
			statement.executeUpdate(requete);

	}
		
	/* cela permet de modifier le mot de passe d'une carte bancaire dans la base de
	 donnée externe  depuis notre programme.*/
		
	public static void setMotDePasse(String numeroCarte, String motDePasse)throws SQLException, UnsupportedEncodingException, NoSuchAlgorithmException {

		try{
			connexion = Routage.connexionBddExt();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			requete = "UPDATE carte2 SET code = '" + Crypte.MD5(motDePasse) + "' WHERE num_carte = '" + numeroCarte
						+ "' ";
			statement.executeUpdate(requete);
			
		}catch(SQLException e) {
			
			System.err.println(e);
			
		}finally{
				
			closeObject();
		}

	}
		
	// renvoie le mot de passe  correspondent à la carte insérée appaartenant à un client externe
	
	public static String getMotDePasse(String numeroCarte) throws SQLException {

		String motDePasse = null;

		try{
			
			connexion = Routage.connexionBddExt();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			requete = "SELECT code from carte2 where num_carte = '" + numeroCarte + "'";
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
		

	/*
	 * mise à jour du solde du client détenteur de la carte (dont le numéro de carte
	 * correspand au "numeroCarte" passé en paramètre ) après avoir effectué un
	 * retrait en fonction du montant retiré cela met à jour aussi les 2 types de
	 * seuils
	*/
	public static void retraitExterne(String numeroCarte, float montant) throws SQLException {
			
		try{

			float nouveauSolde = getSoldeExterne(numeroCarte) - montant;
			connexion = Routage.connexionBddExt();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			requete = "UPDATE compte set solde = '" + nouveauSolde
					+ "' where num_client IN (SELECT num_client From carte2 WHERE num_carte = '" + numeroCarte + "') ";

			statement.executeUpdate(requete);
			Routage.setMontantRestant(numeroCarte, montant);
			Routage.operation(numeroCarte, "Retrait", montant);

		}catch(SQLException e){

			System.err.println(e);

		}finally{
				
			closeObject();
		}

	}
		
	// retourne le Seuil Hebdomadaire d'un client externe
	
	public static float getSeuilHebdomadaire(String numeroCarte) throws SQLException {
		
		float seuilHebd = 0.f;

		try{

			connexion = Routage.connexionBddExt();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			requete = "SELECT seuil_hebd FROM compte WHERE num_client IN (SELECT num_client FROM carte2 WHERE num_carte = '"
						+ numeroCarte + "')";
			resultSet = statement.executeQuery(requete);
			resultSet.next();
			seuilHebd = resultSet.getFloat("seuil_hebd");

		}catch(SQLException e){
				
			System.err.println(e);
		}

			return seuilHebd;
	}
		
		
	// ca permet de récupérer le Seuil Quotidien d'un client externe
	
	public static float getSeuilQuotidien(String numeroCarte) throws SQLException {
			
		float seuilquot = 0.f;

		try{

			connexion = Routage.connexionBddExt();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			requete = "SELECT seuil_quot FROM compte WHERE num_client IN (SELECT num_client FROM carte2 WHERE num_carte = '"
						+ numeroCarte + "')";
			resultSet = statement.executeQuery(requete);
			resultSet.next();
			seuilquot = resultSet.getFloat("seuil_quot");

		}catch(SQLException e){
				
			System.err.println(e);
		}

		return seuilquot;

	}
		
		// permet de mettre à jour le montant du seuil quotidien et hebdomadaire d'un
		// client externe après avoir effectué une opération
		
	public static void setMontantRestant(String numeroCarte, float montant) throws SQLException {
			
		float montantRestant;

		try{

			connexion = Routage.connexionBddExt();

			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			montantRestant = getSeuilQuotidien(numeroCarte) - montant;
			requete = "UPDATE compte set seuil_quot = '" + montantRestant
						+ "' WHERE num_client IN (SELECT num_client FROM carte2 WHERE num_carte = '" + numeroCarte + "')";
			statement.executeUpdate(requete);

			montantRestant = getSeuilHebdomadaire(numeroCarte) - montant;

			requete = "UPDATE compte set seuil_hebd = '" + montantRestant
						+ "' WHERE num_client IN (SELECT num_client FROM carte2 WHERE num_carte = '" + numeroCarte + "')";
			statement.executeUpdate(requete);

		}catch (SQLException e){
				
			System.err.println(e);
		}

	}
		
		
	/* cela permet de supprimer une carte de notre base de donnée externe 
		 ce sera utiliser si le client se trompe dans son code à 3 reprises ce qui
		 bloquera sa carte en la supprimant de la BDD*/
		
	public static void supprimerCarte(String numeroCarte) throws SQLException {

		try{
			
			connexion = Routage.connexionBddExt();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			requete = "DELETE FROM carte2 WHERE  num_carte = '" + numeroCarte + "'";
			statement.executeUpdate(requete);

		}catch(SQLException e){
				
			System.err.println(e);
			
		}finally{

			closeObject();
		}

	}

	// cela permet de supprimer un client de notre basse de donnée externe

	public static void supprimerClient(String numeroCarte) throws SQLException {
			
		try{
			
			connexion = Routage.connexionBddExt();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			requete = "DELETE FROM client WHERE  num_carte = '" + numeroCarte + "'";
			statement.executeUpdate(requete);

		}catch(SQLException e){
				
			System.err.println(e);
				
		}finally{
				
			closeObject();
		}

	}
		
	/* cela permet de supprimer le compte d'un client externe de notre basse de
	 donnée externe */

	public static void supprimerCompte(String numeroCarte) throws SQLException {

		try{
			
			connexion = Routage.connexionBddExt();
			statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			requete = "DELETE FROM compte WHERE  num_carte = '" + numeroCarte + "'";
			statement.executeUpdate(requete);

		}catch(SQLException e){

			System.err.println(e);

		}finally{

			closeObject();
		}

	}
		
		
	//méthode qui permet d'imprimer un ticket pour  un client externe après avoir effectué une opération de dépôt ou de retrait
	   
	public static void getTicket(String numeroCarte) throws SQLException, IOException {

	   Document ticket = new Document(PageSize.A5);
	   StringBuilder sb = new StringBuilder();

	   try{

	       	connexion = Routage.connexionBddExt();
	 		statement = connexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

	        PdfWriter.getInstance(ticket, new FileOutputStream("/home/rilles/projet_L2D1/GAB/src/com/guichetAutomatiqueBancaire/TicketExt.pdf"));
	        requete = "SELECT num_compte FROM compte WHERE num_carte = '"+numeroCarte+"'";
	        resultSet = statement.executeQuery(requete);

	        resultSet.next();
	        ticket.open();// ouvrir le fichier pdf;

	            //ajouter les informations au fichier ;
	        sb.append("Banque D-CARD \n");
	        sb.append("Ticket de "+ConfirmerController.getModeOperation()+"\n");
	            
	           
	        sb.append(Central.date()+"\n");
	        sb.append("Type d'opération : "+ConfirmerController.getModeOperation()+" d'espèce \n");
	        sb.append("N° Compte : "+(resultSet.getString("num_compte"))+"\n");

	          
	        sb.append("Montant : "+RetraitController.getMontantRetrait()+" euros \n");
	            
	            
	           

	        sb.append("(Veuillez conserver ce ticket)\n");
	        sb.append("Merci de votre visite à bientôt\n");
	        ticket.addTitle("Ticket de "+ConfirmerController.getModeOperation());
	        Font font = new Font(Font.FontFamily.TIMES_ROMAN,20,Font.BOLD,BaseColor.BLACK);
	        Paragraph paragraphe = new Paragraph("D-CARD\n\n",font);
	        paragraphe.setAlignment(Element.ALIGN_CENTER);
	 			
	        ticket.add(paragraphe);
	 		Font font2 = new Font(Font.FontFamily.TIMES_ROMAN,16,0, BaseColor.BLACK);
	        ticket.add(new Paragraph(sb.toString(),font2));

	        ticket.close();

	        operation(numeroCarte, "Impression_Ticket", 0);
	             
	        File ticketFile = new File("/home/rilles/projet_L2D1/GAB/src/com/guichetAutomatiqueBancaire/TicketExt.pdf");
	             
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
