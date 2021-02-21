package com.guichetAutomatiqueBancaire;
import java.util.List;


import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

public class SmartCard {
	
	private TerminalFactory factory;
	private List <CardTerminal> terminals;
	private CardTerminal terminal;
	private Card card;
	private byte[] numeroCarte;
	
	public SmartCard() throws CardException {
		//renvoie le TerminalFactory par défaut qui est  tous le temps disponible afin de créer un objet de même type  
		factory = TerminalFactory.getDefault();
		// terminals() retourne une liste de terminals supporté par ce factory 
		// list() retourne une liste de terminals disponible parmi les terminals supportés 
		terminals = factory.terminals().list();
		//retourne le premier terminal de la liste
		terminal = (CardTerminal) terminals.get(0);
		
	}
	
	// prend un tableau de byte en argument et le retourne  sous forme  d'une chaîne de caractère(sera utilisé par la suite pour retourner une chaine de caractère qui correspond au numéro de la carte) 
	 public static String toString(byte[] tabBytes) {
        
		 StringBuffer sb = new StringBuffer();
        
		 for(byte b : tabBytes){
                 
			 sb.append(String.format("%X", b));
         }
         
		 return sb.toString();
	 }
	 
	 //renvoie le numéro de la carte sous forme d'une chaine de caractère
	 public String getNumeroCarte() throws CardException {
		// établit une connexion à la carte ->  en utilisant l'un de ces protocoles ("T=0", "T=1", or "T=CL"), ou "*" pour n'import quel protocole disponible
		 card = terminal.connect("*");
		 
		// getATR()  ->  retourne l'ATR de cette carte qui se trouve dans la puce
		// ATR :  séquence d'octets émise par la puce (Answer To Reset, ou réponse à l'initialisation).donne des info sur le fonctionnement de celle-ci 
		 // getBytes() ->  retourne une copie des bytes de cet ATR
			
		 numeroCarte=card.getATR().getBytes(); 
		
		 card.disconnect(true);     // true pour réinitialiser la carte après l'avoir déconnecté.
		
		 return  SmartCard.toString(numeroCarte);
	}
	 
	 // renvoie true si la carte est présente dans le lecteur 
	 public boolean cartePresente() throws CardException {
		 
		// card.disconnect(true);
		 return terminal.isCardPresent();
		 
	 }
	 // renvoie true si la carte est présente dans le lecteur 
	 public boolean attendreCarte() throws CardException {
		 
		 return terminal.waitForCardPresent(0);
	 }
	 // renvoie true si la carte est retiré du lecteur 
	 public boolean attendreCarteOut() throws CardException {
		 
		 return terminal.waitForCardAbsent(0);
		 
	 }

}
