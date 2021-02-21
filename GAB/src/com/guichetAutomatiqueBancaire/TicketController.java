package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.central.Central;
import com.central.Routage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class TicketController implements Initializable {

	@FXML
	private AnchorPane ticket; 
	
		
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
	
	public void non(ActionEvent e) throws IOException {
		
		AnchorPane p = FXMLLoader.load(getClass().getResource("/ressources/fxml/AutreOperation.fxml"));
		ticket.getChildren().setAll(p);
	}
	
	public void oui (ActionEvent e) throws IOException {
		
		if(InsererCarteController.getTypeCLient().equals("Interne")) {
			try {
				Central.getTicket(InsererCarteController.numeroCarte); //impression d'un ticket après qu'un client interne a effectué une opération
			} catch (SQLException | IOException e1) {
				
				e1.printStackTrace();
			}
		}else {
			try {
				Routage.getTicket(InsererCarteController.numeroCarte);//impression d'un ticket après qu'un client externe a effectué une opération
			} catch (SQLException | IOException e1) {
				
				e1.printStackTrace();
			}
		
		}
			AnchorPane pi = FXMLLoader.load(getClass().getResource("/ressources/fxml/AutreOperation.fxml"));
			ticket.getChildren().setAll(pi);
		
	}

}
