package com.central;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Crypte {
	
	//Methode pour convertir les cases d'un tableau de Byte en Hexadecimal  
    
	
	public static String ByteToHex(byte[] TabByte){
        
		StringBuffer sb= new StringBuffer();
		
		
        for(int i=0; i<TabByte.length;i++) {
           
        	sb.append(Integer.toString((TabByte[i] & 0xff) + 0*100 ,16).substring(1));
        }
  
        
        return sb.toString();
	}
    
	//Cette methode permet de hacher un string passe en parametre en SHA-256
    
    public static String SHA(String code) throws UnsupportedEncodingException, NoSuchAlgorithmException {
           
    	byte[] byteAux = code.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(byteAux);
        byte[] code2_0 = md.digest();
        
        return ByteToHex(code2_0);
    }
    
    //Cette methode peremt de hacher un String passé en parametre en Md5
    
    public static String MD5(String code) throws UnsupportedEncodingException, NoSuchAlgorithmException {
       
    	byte[] byteAux =code.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("md5");
        md.update(byteAux);
        byte[] code2_0 = md.digest();
            
        return ByteToHex (code2_0);
    }
    
    	StringBuffer setCode2_0;
    
     //Cette methode va comparer le code secret entré par le client avec le code secret Haché qui se trouve dans la base de donnees
     
    public static boolean egal (String code_utili, String code_bdd) throws UnsupportedEncodingException, NoSuchAlgorithmException {
                    
    	 String date = Central.dateAujourdhui(); // date du jour
                    
         String aux = SHA(date)+MD5(code_utili); // code saisi hashé en md5 + le sha256 de la date du jour
                   
         String aux2 = SHA(date)+code_bdd;  // code dans la base de donnée hashé en md5 + sha256 de la date du jour 
                  
                    
         return (SHA(aux).equals(SHA(aux2))); //comparaison du sha256 de la chaine de caractère aux avec le sha256 de aux2
     }
   

}
