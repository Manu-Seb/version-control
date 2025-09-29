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

        // mf.commit("This is a test commit ffksdjfls", "manu");

        // mf.status();

        // mf.log();

        // mf.listBranches();

        // mf.createBranch("newBranch");

        // mf.switchBranch("newBranch");

        // mf.diff("cmd1bdcc648c615575161fd16a2e5856be68e036433636c18af5d04a318befc0e7");

        // mf.diff();

        // mf.diff("cme42cef3d4825ca18afc19c64915bfffff3019f1c0ea9d3e9d9096bc2103972fa","cmd1bdcc648c615575161fd16a2e5856be68e036433636c18af5d04a318befc0e7");
        
        mf.checkout("cm43cf9af02da21647a3355fe7dc6f4ad39f57740c1ee4aff1d63c008367c41454");

    }
}