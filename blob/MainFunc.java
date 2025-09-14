import java.sql.Blob;
import java.util.ArrayList;

public class MainFunc {
    public  void addFiles(String dirPath){
        String homeDir = System.getProperty("user.home");
        String cwd = System.getProperty("user.dir");
        ArrayList paths = BlobGen.getFiles(dirPath);
        Tree rep = new Tree(paths, cwd);
        Tree.Node root = rep.getRoot();
        MerkleTreeBuilder m = new MerkleTreeBuilder();
        String merkle = m.buildMerkle(root);
        // System.out.println(merkle);
        rep.printTree(root, merkle);
        
            
    }   
}
