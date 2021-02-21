package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.central.Central;
import com.central.Crypte;
import com.central.Routage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;

public class MotDePasseController implements Initializable {
	
	@FXML 
	private PasswordField mdp;
	
	@FXML
	private AnchorPane pane;
	
	@FXML
	private Label motDePasseFaux;
	
	private int nombreDeTentatives; 
	
	public int getNombreDeTentatives() {
		
		return nombreDeTentatives;
	}

	public void setNombreDeTentatives(int nombreDeTentatives) {
		
		this.nombreDeTentatives = nombreDeTentatives;
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle ressources) {
		
		motDePasseFaux.setText(""); 
		setNombreDeTentatives(0); //intialiser le  nombre de tentatives à 0
	}
	
	public void handleRetour(ActionEvent e) throws IOException {
		
		AnchorPane roott = FXMLLoader.load(getClass().getResource("/ressources/fxml/InsererCarte.fxml")); 
		pane.getChildren().setAll(roott);
		
	}
	
	 public  String getMdpSaisi() {
		
		String mdp_saisi = mdp.getText();
		return mdp_saisi;
		
	} 
	
	public void valider(ActionEvent e)  throws Exception {
		
		try {
			String mdp_saisi = mdp.getText(); //récuperer le mot de passe saisi par le client
			if(InsererCarteController.getTypeCLient().equals("Interne")) {
			
				if(Crypte.egal(mdp_saisi, Central.getMotDePasse(InsererCarteController.numeroCarte))){ //comparer le mot de passe saisi avec le mode passe dans la bdd 
				motDePasseFaux.setText("Code Bon !");
				AnchorPane rootchoix = FXMLLoader.load(getClass().getResource("/ressources/fxml/ChoixOperationInt.fxml")); 
				pane.getChildren().setAll(rootchoix);
				
				}else {
				
					nombreDeTentatives++;
					if(nombreDeTentatives<3) {
					
						motDePasseFaux.setText("");
						motDePasseFaux.setText("Code  incorrect !");
						mdp.clear();
				
					}else {
					
					AnchorPane carteBloque = FXMLLoader.load(getClass().getResource("/ressources/fxml/CarteBloque.fxml")); 
					pane.getChildren().setAll(carteBloque);
					Central.supprimerCarte(InsererCarteController.numeroCarte); //suppression de la carte de la BDD
					
					}
				
				}
				
			}else {
				
				if(Crypte.egal(mdp_saisi, Routage.getMotDePasse(InsererCarteController.numeroCarte))){
					
					motDePasseFaux.setText("Code Bon !");
					AnchorPane rootchoixExt = FXMLLoader.load(getClass().getResource("/ressources/fxml/ChoixOperationExt.fxml")); 
					pane.getChildren().setAll(rootchoixExt);
					
				}else {
					
						
						nombreDeTentatives++;
						if(nombreDeTentatives<3) {
						
							motDePasseFaux.setText("");
							motDePasseFaux.setText("Code  incorrect !");
							mdp.clear();
					
						}else {
						
						AnchorPane carteBloque = FXMLLoader.load(getClass().getResource("/ressources/fxml/CarteBloque.fxml")); 
						pane.getChildren().setAll(carteBloque);
						Routage.supprimerCarte(InsererCarteController.numeroCarte); //suppression de la carte de la BDD
						
						}
					
				}
				
				
			}
		
			
		
		}catch(Exception e1){
			
			e1.printStackTrace();
		}
	}

	

}
