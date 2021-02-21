	package com.guichetAutomatiqueBancaire;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.smartcardio.CardException;

import com.central.Central;
import com.central.Routage;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class InsererCarteController extends Thread implements Initializable {
	
	@FXML
	private AnchorPane insererCarte;
	
	public static String numeroCarte = "";
	private boolean arreterThread = false;
	private static String typeClient;
	
	public static void setTypeClient(String typeClient) {
		InsererCarteController.typeClient = typeClient;
	}
	
	public static String getTypeCLient() {
		
		return InsererCarteController.typeClient;
	}
	
    public static void setNumeroCarte(String numeroCarte) {

    	InsererCarteController.numeroCarte = numeroCarte;

    }

	
	@Override
	public void initialize(URL location, ResourceBundle ressources) {

		  PathTransition tran = new PathTransition();
	      tran.play();
	      this.start();
	}
	
	
	public void handleAbandonner(ActionEvent ev) {
	
		try {
			
			AnchorPane root2 = FXMLLoader.load(getClass().getResource("/ressources/fxml/Fin.fxml"));
			insererCarte.getChildren().setAll(root2);
			
		}catch(Exception e1) {
			
			System.out.println(e1);
		}
		
	} 
	
	@FXML
	public void run() {
		
		while(!Thread.currentThread().isInterrupted()) {
			
			if(arreterThread) {
				
				Thread.currentThread().interrupt(); //interrompre le thread
				
			}
			try {
				
				SmartCard carte = new SmartCard();
				
				if(carte.attendreCarte()) {
					
					InsererCarteController.setNumeroCarte(carte.getNumeroCarte()); //récupérer le numéro de la carte insérée dans le lecteur
					
					if(Central.existeInterne(InsererCarteController.numeroCarte)){
					
						if(Central.carteValide(InsererCarteController.numeroCarte)) {
						
						arreterThread=true;
						setTypeClient("Interne"); //mettre à jour le type de client
						AnchorPane root3 = FXMLLoader.load(getClass().getResource("/ressources/fxml/MotDePasse.fxml"));
					 
					 Platform.runLater(new Runnable() {  
					
						 @Override
						 public void run() { 
                        	
							 insererCarte.getChildren().setAll(root3);
						 }

					 });
					
						}
						else {
							arreterThread=true;
							AnchorPane root4 = FXMLLoader.load(getClass().getResource("/ressources/fxml/CarteInvalide.fxml"));
							Platform.runLater(new Runnable() {  
								@Override
			                    public void run() { 
			                        	
			                    	insererCarte.getChildren().setAll(root4);
			                    }

							});
						}
					
				}else if(Routage.existeExterne(InsererCarteController.numeroCarte)) {
					
					if(Routage.carteValide(InsererCarteController.numeroCarte)) {
						
						arreterThread=true;
						setTypeClient("Externe");//mettre à jour le type de client
						AnchorPane root5 = FXMLLoader.load(getClass().getResource("/ressources/fxml/MotDePasse.fxml"));
					 
						Platform.runLater(new Runnable() {  
					
							@Override
							public void run() { 
                        	
							 insererCarte.getChildren().setAll(root5);
							}

						});
					
					}else {
						
						arreterThread=true;
						AnchorPane root6 = FXMLLoader.load(getClass().getResource("/ressources/fxml/CarteInvalide.fxml"));
						Platform.runLater(new Runnable() {  
							@Override
		                    public void run() { 
		                        	
		                    	insererCarte.getChildren().setAll(root6);
		                    }

						});
						
						
					}
					
				}else{
						
						arreterThread=true;
						AnchorPane root7 = FXMLLoader.load(getClass().getResource("/ressources/fxml/CarteInconnu.fxml"));
						 Platform.runLater(new Runnable() {  
								@Override
			                    public void run() { 
			                        	
			                    	insererCarte.getChildren().setAll(root7);
			                    }

			            });
					}
				}
			
			
			}catch(CardException | IOException | SQLException e) {
				
				try{
					
					Thread.sleep(4000);
					
				}catch(InterruptedException e1){
					
					e1.printStackTrace();
				}
				
				e.printStackTrace();
			}
			
		}
		
			
	} 
	

}
