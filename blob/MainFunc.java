import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;

public class MainFunc {

    private String lastTreeHash;


    public void init(){
        String cwd = System.getProperty("user.dir");
        String VCS_DIR = cwd + "/.vcs";
        String OBJECTS_DIR = VCS_DIR + "/objects/";
        String REFS_DIR = VCS_DIR + "/refs/";

        File vcsDir = new File(VCS_DIR);
        if (!vcsDir.exists()) vcsDir.mkdirs();

        File objDir = new File(OBJECTS_DIR);
        if (!objDir.exists()) objDir.mkdirs();

        File refDir = new File(REFS_DIR);
        if (!refDir.exists()) refDir.mkdirs();

        File branch = new File(VCS_DIR+"/branch.txt");
        if(!branch.exists()) {
            try {
                branch.createNewFile();
                try (FileWriter writer = new FileWriter(branch)) {
                    writer.write("main");
                }
            } catch (IOException e) {
                System.out.println("Error creating branch.txt: " + e.getMessage());
            }
        }
        File currBranch = new File(VCS_DIR+"/refs/"+ "main.txt");
        if(!currBranch.exists()) {
            try {
                currBranch.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating main.txt: " + e.getMessage());
            }
        }

        System.out.println("VCS initialized.");
    }
    public  void addFiles(String dirPath){
        String homeDir = System.getProperty("user.home");
        String cwd = System.getProperty("user.dir");
        File initDir= new File(cwd + "/.vcs");
        if(!initDir.exists()){
            System.out.println("Please initialize the vcs");
            return;
        }
        ArrayList paths = Helper.getFiles(dirPath);
        Tree rep = new Tree(paths, cwd);
        Tree.Node root = rep.getRoot();
        MerkleTreeBuilder m = new MerkleTreeBuilder();

        lastTreeHash = m.buildMerkle(root);

        Helper.stageFiles( lastTreeHash);
        // System.out.println(merkle);
        // rep.printTree(root, lastTreeHash);
        System.out.println(lastTreeHash);
        
            
    }   

    public void commit(String message, String author) {
        File stage = new File(System.getProperty("user.dir") + "/.vcs/objects/stage.txt");
        if (!stage.exists()) {
            System.out.println("No changes staged. Run addFiles() first.");
            return;
        }

        String branchName = Branch.getBranch();

        String commitHash = Helper.createCommit(lastTreeHash, message, author,branchName);
        System.out.println("Committed: " + commitHash);

        // Clear staged state (optional, like Git index)
        lastTreeHash = null;
    }

    public void status(){
        String cwd = System.getProperty("user.dir");
        ArrayList<String> files = Helper.getFiles(cwd);

        String branch = Branch.getBranch();
        System.out.println("Current branch is "+ branch);

        ArrayList<String> filesNotAdded = new ArrayList<>();
        ArrayList<String> filesAdded = new ArrayList<>();
        
        String stage= Helper.readFile(cwd + "/.vcs/objects/stage.txt");

        if (stage == null) {
            System.out.println("No changes staged. Run addFiles() first.");
            return;
        }

        String[] hashes = stage.split("\n");
        
        System.out.println("Current Head " + hashes[0]);
        
        for (String file : files) {
            String content = Helper.readFile(file);
            String hash = Helper.createHash(content);
            File fileObj = new File(cwd + ".vcs/objects/" + "ob" + hash);
            if (fileObj.exists()) filesAdded.add(file);
            else filesNotAdded.add(file);
        }
        
        System.out.println("Files added: ");
        for(String file : filesAdded){
            System.out.println(file);
        }
        System.out.println("Files not added: " );
        for(String file : filesNotAdded){
            System.out.println(file);
        }
        

    }

    public void log(){
        String cwd = System.getProperty("user.dir");
        String branch = Branch.getBranch();
        String ref = Helper.readFile(cwd + '/' + ".vcs/refs/"+branch+".txt");
        String[] refLines = ref.split("\n");
        String currCommit = refLines[0];
        String[] commitLines;
        do{
            String commit = Helper.readFile(cwd + '/' + ".vcs/objects/"+"cm"+currCommit);
            commitLines = commit.split("\n");
            System.out.println("Commit: " + currCommit);
            System.out.println("Author: " + commitLines[2]);
            System.out.println("Message" + commitLines[5]);
            System.out.println("Date " + commitLines[3]);
            System.out.println("==================================================================================");
            currCommit= commitLines[1].split(" ")[1];
        }while(!commitLines[1].split(" ")[1].equals("NoParentFound"));
    }

    public void createBranch(String s){
        ArrayList<String> branches = Helper.getFiles(System.getProperty("user.dir") + "/.vcs/refs/");
        for(String branchPath : branches){
            String[] path = branchPath.split("/");
            String branchName = path[path.length-1].split("\\.")[0];
            if(branchName.equals(s)){
                System.out.println("Branch already exists");
                return;
            }
        }
        Branch.createBranch(s);
        System.out.println("New branch created:"+ s);
    }

    public void switchBranch(String s){
        Branch.setBranch(s);
    }


}
