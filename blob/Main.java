import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {

        String dir = System.getProperty("user.dir");

        MainFunc mf = new MainFunc();

        // mf.init();
        // mf.addFiles(dir);

        // mf.commit("This is a test commit 110", "manu");

        // mf.status();

        // mf.log();

        mf.listBranches();

        mf.createBranch("newBranch");

        mf.switchBranch("newBranch");


    }
}