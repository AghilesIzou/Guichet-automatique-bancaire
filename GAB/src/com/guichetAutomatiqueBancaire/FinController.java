package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class FinController extends Thread implements Initializable{
	
	
	
	@FXML
	private AnchorPane fin;
	private int tempsPasse;

	@SuppressWarnings("deprecation")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tempsPasse =  new Date().getSeconds(); //retourne un nombre entre 1 et 59
		
		if(tempsPasse >=52) {
			tempsPasse -=8;
		}
		
		this.start();
		
	}
	
	@SuppressWarnings("deprecation")
	public void run(){
		
		int maintenant = new Date().getSeconds(); //retourne un nombre entre 1 et 59
		
    	if(maintenant >=52) {
    		maintenant -=8;
		}
        while (!Thread.currentThread().isInterrupted()){
        	
        	
            if(maintenant==tempsPasse+8){ //compter 8 secondes avant d'afficher l'interface d'acceuille
                try {
                	AnchorPane recommencer = FXMLLoader.load(getClass().getResource("/ressources/fxml/Acceuil.fxml"));
                	
                    Platform.runLater(new Runnable() {
                    	
                        @Override
                        public void run() {
                        	
                        	fin.getChildren().setAll(recommencer);
                        }
                        
                    });
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                	
                    Thread.currentThread().interrupt();
                   
                }
            }
            else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            maintenant++;
        }
    }
	
}
	
	
	

