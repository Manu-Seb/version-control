package diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import core.Tree;
import utils.HashBuilder;
import utils.Helper;

public class StageDiff implements Diff{
    public void getDiff(String commit1, String commit2) {
    // ignore commit1 and commit2 parameters for stage vs working diff

        String cwd = System.getProperty("user.dir");
        String objectsPath = cwd + "/.vcs/objects/";

        // Read staged root tree hash from stage.txt
        String stageContent = Helper.readFile(objectsPath + "stage.txt");
        String stagedRootHash = "tr" + stageContent.trim().split("\n")[0];

        // Build staged snapshot mapz
        Map<String, String> stagedFiles = buildFileMap(stagedRootHash, objectsPath);

        // printFileHashMap(stagedFiles);

        // Get working files list (relative paths!)
        List<String> workingFiles = Helper.getFiles(cwd); // assume relative paths returned

        // Calculate working hashes
        Map<String, String> workingFileHashes = new HashMap<>();
        for (String file : workingFiles) {
            String content = Helper.readFile(file);

            //strip everything till the starting thing 
            workingFileHashes.put(Helper.stripBase(file, cwd+"/"), "ob"+HashBuilder.blobHash(content));
        }

        // printFileHashMap(workingFileHashes);

        // System.out.println("=====================================================================================");

        // printFileHashMap(stagedFiles);


        //Arraylist to temporarily hold onto modified vs added files
        ArrayList<String> modified = new ArrayList();
        ArrayList<String> added = new ArrayList();

        // Compare staged vs working: identify added, modified, unchanged 
        for (String file : workingFileHashes.keySet()) {
            if (!stagedFiles.containsKey(file)) {
                added.add("A " + file); // new in working
            } else if (!stagedFiles.get(file).equals(workingFileHashes.get(file))) {
                modified.add("M " + file); // changed
            } 
        }

        System.out.println("Added Files : ");
        for(String file : added){
            System.out.println(file);
        }
        System.out.println();
        
        System.out.println("Modified Files : ");
        for(String file : modified){
            System.out.println(file);
        }
        System.out.println();

        // Detect deleted files (in stage but missing in working)
        System.out.println("Deleted Files : ");
        for (String file : stagedFiles.keySet()) {
            if (!workingFileHashes.containsKey(file)) {
                System.out.println("D " +file);
            }
        }
    }



    private Map<String, String> buildFileMap(String objectHash, String objectsPath) {
        Map<String, String> fileMap = new HashMap<>();
        
        if (objectHash.startsWith("tr")) {
            String treeHash = objectHash.substring(2);
            String treeContent = Helper.readFile(objectsPath + "tr" + treeHash);
            if (treeContent != null && !treeContent.isEmpty()) {
                String[] lines = treeContent.split("\n");
                for (String line : lines) {
                    if (line.isEmpty()) continue;
                    String[] parts = line.split(" ");
                    if (parts.length < 3) continue;
                    String type = parts[0];
                    String name = parts[1];
                    String hash = parts[2];
                    if ("blob".equals(type)) {
                        fileMap.put(name, "ob" + hash);
                    } else if ("tree".equals(type)) {
                        Map<String, String> subtreeFiles = buildFileMap("tr" + hash, objectsPath);
                        for (Map.Entry<String, String> e : subtreeFiles.entrySet()) {
                            fileMap.put(name + "/" + e.getKey(), e.getValue());
                        }
                    }
                }
            }
        }
        // Blob alone should not be called here generally
        
        return fileMap;
    }

    public void printFileHashMap(Map<String, String> fileHashMap) {
        // Sort map by key (file paths) for nicer output
        Map<String, String> sortedMap = new TreeMap<>(fileHashMap);
        System.out.println("File Path                               | Object Hash");
        System.out.println("---------------------------------------|---------------------------");
        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            System.out.printf("%-39s | %s\n", entry.getKey(), entry.getValue());
        }
    }


}
