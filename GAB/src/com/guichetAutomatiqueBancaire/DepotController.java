package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class DepotController implements Initializable {

	@FXML 
	private AnchorPane depot;
	
	@FXML
	private TextField montantDepot;
	private static int mntDpt;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		montantDepot.setText("");
		
	}
	
	//retourne le montant de dépôt choisi par le client
	public static int getMontantDepot() {
        return mntDpt;
    }
	//changer le montant de dépot
    public static void setMontantDepot(int montantDepot) {
        DepotController.mntDpt = montantDepot;
    }

  
	
	public void retour(ActionEvent e) throws IOException {
		
		switch(InsererCarteController.getTypeCLient()) {
		
		case "Interne" :
			
			AnchorPane ret = FXMLLoader.load(getClass().getResource("/ressources/fxml/ChoixOperationInt.fxml"));
			depot.getChildren().setAll(ret);
			break;
	
		case "Externe" :
			
			AnchorPane rett = FXMLLoader.load(getClass().getResource("/ressources/fxml/ChoixOperationExt.fxml"));
			depot.getChildren().setAll(rett);
			break;
		}
	}
	
	public void valider(ActionEvent e) throws IOException {
		 if(!montantDepot.getText().equals("")){ //vérifier que la case n'est pas vide 
			 int valeurDepot = Integer.parseInt(montantDepot.getText());
	            if(valeurDepot<=0){ //montant saisi négatif
	            	
	            	OperationProblemController.setMessage("Vous  avez  saisi un montant négatif  !"); //message d'erreur
	            	OperationProblemController.setMode("depot"); //changer le mode d'opération
	            	AnchorPane roott= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
	            	depot.getChildren().setAll(roott);
	            }
	            else if(valeurDepot <10 && valeurDepot>0){
	            	
	            	OperationProblemController.setMessage("Vous ne pouvez pas déposer une somme inférieur à  10  euros ! ");
            		OperationProblemController.setMode("Depot");
            		AnchorPane roott1= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
                    depot.getChildren().setAll(roott1);
	           
	            }else if(valeurDepot>5000) {
	            	OperationProblemController.setMessage("Vous ne pouvez pas déposer une somme supérieur à  5000  euros ! "); //message d'erreur
            		OperationProblemController.setMode("Depot");
            		AnchorPane roott4= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
                    depot.getChildren().setAll(roott4);
	            	
	            }else if(valeurDepot % 10 !=0) {
            		
	            	OperationProblemController.setMessage("Veuillez  saisir  un  montant  multiple de 10 ! ");
            		OperationProblemController.setMode("Depot");
            		AnchorPane roo= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
                    depot.getChildren().setAll(roo);
            		
                	
                }else {
	            	DepotController.setMontantDepot(valeurDepot);
            		ConfirmerController.setModeOperation("Depot");
            		ConfirmerController.setValue("Depot  de "+valeurDepot+" ?");
            		AnchorPane root =FXMLLoader.load(getClass().getResource("/ressources/fxml/ConfirmerOperation.fxml"));
            		depot.getChildren().setAll(root);
	            }
	        }
	        else {
	        	OperationProblemController.setMessage("Vous  n'avez  saisi  aucun   montant !");
            	OperationProblemController.setMode("Depot");
            	AnchorPane  root1 =FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
            	depot.getChildren().setAll(root1);
	        }
		
		
		
	}

}
