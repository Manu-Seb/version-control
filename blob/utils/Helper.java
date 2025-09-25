package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import core.Branch;
import core.MerkleTreeBuilder;
public class Helper {

    private static final String DIR = ".vcs/";

    // Already exists: getFiles(), readFile(), createHash()

    public static String createCommit(String treeHash, String message, String author,String branchName) {
        // Get parent commit from refs.txt
        String parent = getHead();

        // Commit content
        StringBuilder commitContent = new StringBuilder();
        commitContent.append("tree ").append(treeHash).append("\n");
        if (parent != null) {
            commitContent.append("parent ").append(parent).append("\n");
        }
        else{
            commitContent.append("parent ").append("NoParentFound").append("\n");
        }
        commitContent.append("author ").append(author).append("\n");
        commitContent.append("date ").append(LocalDateTime.now()).append("\n");
        commitContent.append("\n");
        commitContent.append(message).append("\n");

        // Hash it
        String commitHash = HashBuilder.commitHash(commitContent.toString());

        // Store commit object
        writeObject("cm" + commitHash, commitContent.toString());

        // Update HEAD (refs.txt)
        updateHead(commitHash, branchName);

        return commitHash;
    }

    private static void writeObject(String fileName, String content) {
        try {
            File dir = new File(DIR+"objects/");
            if (!dir.exists()) dir.mkdirs();

            File objFile = new File(DIR+"objects/" + fileName);
            if (!objFile.exists()) {
                try (FileWriter writer = new FileWriter(objFile)) {
                    writer.write(content);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing object: " + fileName, e);
        }
    }

    private static void updateHead(String commitHash,String branchHead) {
        try {
            File file = new File(DIR+"refs/"+branchHead+".txt");
            if(!file.exists()) {
                System.out.println("Branch does not exist");
                return;
            }
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(commitHash);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating refs.txt", e);
        }
    }

    private static String getHead() {
        String branch = Branch.getBranch();
        try {
            File file = new File(DIR+"refs/"+branch +".txt");
            if (!file.exists()) return null;
            return Files.readString(file.toPath()).trim();
        } catch (IOException e) {
            throw new RuntimeException("Error reading refs.txt", e);
        }
    }
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
        if(!myFile.exists()) System.out.println("File does not exist");
        
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

    public static ArrayList<String> getFiles(String dirPath){
        Path start = Paths.get(dirPath);
        ArrayList<String> files = new ArrayList<>();
        try {
            Files.walk(start)
            .filter(Files::isRegularFile)
                .filter(path -> {
                    Path relative = start.relativize(path);
                    for (Path p : relative) {
                        if (p.getFileName().toString().startsWith(".")) {
                            return false;
                        }
                    }
                    return true;
                })


                .forEach(path -> files.add(path.toAbsolutePath().toString()));
        } catch (IOException e) {
            System.out.println("Error walking file tree: " + e.getMessage());
        }
        files.sort(String::compareTo);
        return files;
    }

    public static void stageFiles(String content){
        try {
            File objFile = new File(MerkleTreeBuilder.OBJECTS_DIR + "stage.txt");
            try (FileWriter writer = new FileWriter(objFile, false)) { // overwrite mode
                writer.write(content + "\n" );
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing stage.txt", e);
        }
    }

    public static String stripBase(String path, String base) {
        if (path.startsWith(base)) {
            return path.substring(base.length());
        }
        return ""; // ignore if it doesn't match the base
    }

    

}



