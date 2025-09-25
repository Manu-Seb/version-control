package diff;

import java.util.ArrayList;
import java.util.Map;

import utils.Helper;

public class CommitDiff extends Diff{
    public void getDiff(String commit1, String commit2) {
    // ignore commit1 and commit2 parameters for stage vs working diff

        System.out.println("Diff between the staged files and the given commit");
        if(commit1 == null  || commit1.trim().isEmpty() || commit2 == null || commit2.trim().isEmpty()){
            System.out.println("Please provide a commit hash");
            return;
        }

        String cwd = System.getProperty("user.dir");
        String objectsPath = cwd + "/.vcs/objects/";

        // Read staged root tree hash from stage.txt
        String firstCommitContent = Helper.readFile(objectsPath + commit1);
        if(firstCommitContent== null || firstCommitContent.trim().isEmpty()){
            System.out.println("The given commit does not exist [0]");
            return;
        }
        String firstRootHash = "tr" + firstCommitContent.trim().split("\n")[0].split(" ")[1];
        // Build staged snapshot mapz
        Map<String, String> firstCommitMap = buildFileMap(firstRootHash, objectsPath);

        String secondrootHash = Helper.readFile(objectsPath + commit2).split("\n")[0].split(" ")[1];

        if(secondrootHash == null || secondrootHash == " "){
            System.out.println("The given commit does not exist [1]");
        }


        //adding tree prefix
        secondrootHash= "tr"+secondrootHash;
        // Build working snapshot mapz
        Map<String, String> secondCommitMap = buildFileMap(secondrootHash, objectsPath);
        

        // printFileHashMap(firstCommitMap);

        // System.out.println("=====================================================================================");

        // printFileHashMap(secondCommitMap);


        //Arraylist to temporarily hold onto modified vs added files
        ArrayList<String> modified = new ArrayList<String>();
        ArrayList<String> added = new ArrayList<String>();

        // Compare staged vs working: identify added, modified, unchanged 
        for (String file : firstCommitMap.keySet()) {
            if (!secondCommitMap.containsKey(file)) {
                added.add("A " + file); // new in working
            } else if (!secondCommitMap.get(file).equals(firstCommitMap.get(file))) {
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
        for (String file : secondCommitMap.keySet()) {
            if (!firstCommitMap.containsKey(file)) {
                System.out.println("D " +file);
            }
        }
    }
}
