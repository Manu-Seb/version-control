import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
public class BlobGen {
    public static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) {
            hexString.append('0');
        }
        hexString.append(hex);
    }
    return hexString.toString();
    }

    public static String createHash(String content){
        try {
            MessageDigest digest = MessageDigest.getInstance("sha-256");
            byte[] bytecode = digest.digest(content.getBytes());
            // You may want to return the hex string representation
            return bytesToHex(bytecode);
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println("No such algorithm: " + e.getMessage());
            return null;
        }
    }
    
    public static String readFile(String fileName){
        File myFile = new File(fileName);
        System.out.println("File exists: " + myFile.exists());
        
        String file = "";
        
        try(Scanner myReader  = new Scanner(myFile)) {
            while(myReader.hasNextLine()){
                file += myReader.nextLine() + "\n";
            }
            myReader.close();
            return file;
        } catch (Exception e) {
            System.out.println("An exception occurred: " + e.getMessage());
            return "";
        }
    }

    public static ArrayList getFiles(String dirPath){
            Path start = Paths.get(dirPath);
			ArrayList<String> files = new ArrayList<>();
            try {
                Files.walk(start)
                        .forEach(path -> files.add(path.toAbsolutePath().toString()));
            } catch (IOException e) {
                System.out.println("Error walking file tree: " + e.getMessage());
                
            }   
            return files;
    }

    

}



