package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.central.Central;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class SoldeController  implements Initializable{
	
	@FXML
	private AnchorPane s;
	@FXML
	private Label solde;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			
			solde.setText(Float.toString(Central.getSoldeInterne(InsererCarteController.numeroCarte))+" €"); //affichage du solde d'un client interne
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public void boutonImprimer(ActionEvent e) throws IOException, SQLException {
		
		Central.imprimerSolde(InsererCarteController.numeroCarte); //impression du solde d'un client interne
		AnchorPane r= FXMLLoader.load(getClass().getResource("/ressources/fxml/AutreOperation.fxml"));
		s.getChildren().setAll(r);
		
	}
	
	//retour à l'interface choixOpération
	
	public void boutonRetour(ActionEvent e) throws IOException {
		
		AnchorPane r= FXMLLoader.load(getClass().getResource("/ressources/fxml/ChoixOperationInt.fxml"));
		s.getChildren().setAll(r);
	}

}
