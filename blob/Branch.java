import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Branch {
    public static String getBranch(){

        File currBranch = new File (System.getProperty("user.dir") + "/.vcs/branch.txt");
        String branchName = Helper.readFile(currBranch.getPath()).split("\n")[0];

        return branchName;

    }

    public static void setBranch(String newBranch){
        File currBranch = new File (System.getProperty("user.dir") + "/.vcs/branch.txt");
        try (FileWriter writer = new FileWriter(currBranch)) {
            writer.write(newBranch);
        } catch (IOException e) {
            System.out.println("Error occurred while setting branch: " + e.getMessage());
        }
    }

    public static void createBranch(String BranchName){
        File newBranch = new File(System.getProperty("user.dir") + "/.vcs/refs/" + BranchName + ".txt");
        if(newBranch.exists()){
            System.out.println("Branch already exists");
            return;
        }
        try {
            newBranch.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating branch: " + e.getMessage());
        }

    }
}
