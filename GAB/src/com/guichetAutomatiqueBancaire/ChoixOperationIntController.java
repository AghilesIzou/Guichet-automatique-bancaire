package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.central.Central;
import com.itextpdf.text.DocumentException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class ChoixOperationIntController implements Initializable {
	
	@FXML
	AnchorPane p;

	@Override
	public void initialize(URL location, ResourceBundle ressources) {}
	
	public void boutonRetrait(ActionEvent e) throws IOException {
		 AnchorPane rootRetrait = FXMLLoader.load(getClass().getResource("/ressources/fxml/Retrait.fxml"));
		 p.getChildren().setAll(rootRetrait);
		
	}
	
	public void boutonRib(ActionEvent e) throws IOException, DocumentException, SQLException{
			
		 Central.imprimerRib(InsererCarteController.numeroCarte); //impression du RIB
		 AnchorPane rootRib = FXMLLoader.load(getClass().getResource("/ressources/fxml/AutreOperation.fxml"));
		 p.getChildren().setAll(rootRib);
		 
	}
	
	public void boutonDepot(ActionEvent e) throws IOException {
		
		AnchorPane rootDepot = FXMLLoader.load(getClass().getResource("/ressources/fxml/Depot.fxml"));
		p.getChildren().setAll(rootDepot);
	}
	
	public void boutonSolde(ActionEvent e) throws IOException, SQLException {
		
		AnchorPane rootSolde = FXMLLoader.load(getClass().getResource("/ressources/fxml/Solde.fxml"));
		p.getChildren().setAll(rootSolde);	
	}
	public void boutonHistorique(ActionEvent e) throws IOException, DocumentException, SQLException {
		 
		Central.imprimerHistorique(InsererCarteController.numeroCarte); //impression de l'historique
		AnchorPane rootHis = FXMLLoader.load(getClass().getResource("/ressources/fxml/AutreOperation.fxml"));
		p.getChildren().setAll(rootHis);
		
	}
	
	//afficher l'interface de fin
	
	public void boutonExit(ActionEvent e ) throws IOException {
		
		AnchorPane vb = FXMLLoader.load(getClass().getResource("/ressources/fxml/Fin.fxml"));
		p.getChildren().setAll(vb);
		
	}
	

}
