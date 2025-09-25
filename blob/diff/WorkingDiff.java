package diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.HashBuilder;
import utils.Helper;

public class WorkingDiff extends Diff{
    
    public void getDiff(String commit1, String commit2) {
    // ignore commit1 and commit2 parameters for stage vs working diff

        String cwd = System.getProperty("user.dir");
        String objectsPath = cwd + "/.vcs/objects/";

        // Read staged root tree hash from stage.txt
        String stageContent = Helper.readFile(objectsPath + "stage.txt");
        String stagedRootHash = "tr" + stageContent.trim().split("\n")[0];

        // Build staged snapshot mapz
        Map<String, String> stagedFiles = buildFileMap(stagedRootHash, objectsPath);

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
        ArrayList<String> modified = new ArrayList<String>();
        ArrayList<String> added = new ArrayList<String>();

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
}
