package checkout;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import utils.Helper;

public class CheckoutFunc {
    public void checking(String commit) {
        String cwd = System.getProperty("user.dir");
        String objectsPath = cwd + "/.vcs/objects/";
        String tempDIR = cwd + "/.vcs/.temp/";

        File temp = new File(tempDIR);
        try {
            // Clear temp dir if it exists
            if (temp.exists()) {
                boolean deleted = deleteDirectoryRecursively(temp);
                if (!deleted) {
                    System.err.println("Failed to delete temp directory: " + tempDIR);
                    return;
                }
            }
            temp.mkdirs();

            String content = Helper.readFile(objectsPath + commit);
            if (content == null || content.trim().isEmpty()) {
                System.err.println("Commit content is empty or missing for commit: " + commit);
                return;
            }

            String treeHash = content.split("\n")[0].split(" ")[1];

            if (treeHash.startsWith("tr")) {
                writeTree(treeHash, temp, objectsPath);
            }

            File cwdDir = new File(cwd);
            // Delete all files/folders in cwd except .vcs and .vcs/.temp
            for (File file : cwdDir.listFiles()) {
                if (!file.getName().equals(".vcs")) {
                    deleteDirectoryRecursively(file);
                }
            }

            // Move all contents from temp to cwd
            for (File file : temp.listFiles()) {
                File dest = new File(cwd, file.getName());
                boolean success = file.renameTo(dest);
                if (!success) {
                    System.err.println("Failed to move " + file.getAbsolutePath() + " to " + dest.getAbsolutePath());
                    // Optionally handle this failure (retry, copy-and-delete, etc.)
                }
            }

            // Finally delete the temp directory (should be empty now)
            temp.delete();

        } catch (IOException e) {
            System.err.println("IO error during checkout: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }




    private void writeTree(String treeHash, File dir, String objectsPath) throws IOException {
        String treeContent = Helper.readFile(objectsPath + "tr" + treeHash.substring(2));
        if (treeContent == null) return;
        for (String line : treeContent.split("\n")) {
            if (line.isEmpty()) continue;
            String[] parts = line.split(" ");
            if (parts.length < 3) continue;
            String type = parts[0], name = parts[1], hash = parts[2];

            if ("blob".equals(type)) {
                String blobContent = Helper.readFile(objectsPath + "ob" + hash);
                int idx = blobContent.indexOf("\n");
                String content = idx == -1 ? blobContent : blobContent.substring(idx + 1);
                File file = new File(dir, name);
                file.getParentFile().mkdirs(); // Ensure parent dirs
                try (FileWriter fw = new FileWriter(file)) {
                    fw.write(content);
                }
            } else if ("tree".equals(type)) {
                File subdir = new File(dir, name);
                subdir.mkdirs();
                writeTree(hash, subdir, objectsPath);
            }
        }
    }

    private static boolean deleteDirectoryRecursively(File dir) {
    if (dir == null || !dir.exists()) return true;
    if (dir.isDirectory()) {
        File[] children = dir.listFiles();
        if (children != null) {
            for (File child : children) {
                boolean success = deleteDirectoryRecursively(child);
                if (!success) {
                    System.err.println("Failed to delete: " + child.getAbsolutePath());
                    return false;
                }
            }
        }
    }
    return dir.delete();
}
}
