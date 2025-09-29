package checkout;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import utils.Helper;

public class CheckoutFunc {
    public void checking(String commit) {
        System.out.println("Starting checkout process for commit: " + commit);
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
            System.out.println("Created temp directory: " + tempDIR);

            temp.mkdirs();
            System.out.println("Created empty temp directory: " + tempDIR);

            String content = Helper.readFile(objectsPath + commit);
            if (content == null || content.trim().isEmpty()) {
                System.err.println("Commit content is empty or missing for commit: " + commit);
                return;
            }

            String tr = content.split("\n")[0];
            String type = tr. split(" ")[0];
            String treeHash = tr.split(" ")[1];
            System.out.println("Extracted tree hash from commit content: " + treeHash);

            if (type.equals("tree")) {
                writeTree(treeHash, temp, objectsPath);
            }

            File cwdDir = new File(cwd);
            // Delete all files/folders in cwd except .vcs and .vcs/.temp
            for (File file : cwdDir.listFiles()) {
                if (!file.getName().equals(".vcs") && !file.getName().equals(".vcs/.temp")) {
                    System.out.println("Deleting file/folder: " + file.getAbsolutePath());
                    deleteDirectoryRecursively(file);
                }
            }

            // Move all contents from temp to cwd
            for (File file : temp.listFiles()) {
                File dest = new File(cwd, file.getName());
                System.out.println("Moving file: " + file.getAbsolutePath() + " to " + dest.getAbsolutePath());
                boolean success = file.renameTo(dest);
                if (!success) {
                    System.err.println("Failed to move " + file.getAbsolutePath() + " to " + dest.getAbsolutePath());
                    // Optionally handle this failure (retry, copy-and-delete, etc.)
                }
            }

            // Finally delete the temp directory (should be empty now)
            System.out.println("Deleting temp directory: " + tempDIR);
            temp.delete();
            System.out.println("Finished checkout process for commit: " + commit);
        } catch (IOException e) {
            System.err.println("An error occurred during checkout: " + e.getMessage());
        }
    }




    private void writeTree(String treeHash, File dir, String objectsPath) throws IOException {
        System.out.println("Starting writeTree process for treeHash: " + treeHash);
        String treeContent = Helper.readFile(objectsPath + "tr" + treeHash);
        if (treeContent == null) {
            System.out.println("Tree content is empty or missing for treeHash: " + treeHash);
            return;
        }

        System.out.println("Extracted tree content for treeHash: " + treeHash);
        for (String line : treeContent.split("\n")) {
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split(" ");
            if (parts.length < 3) {
                continue;
            }
            String type = parts[0], name = parts[1], hash = parts[2];

            System.out.println("Processing line: " + line);
            System.out.println("Type: " + type);
            System.out.println("Name: " + name);
            System.out.println("Hash: " + hash);

            if ("blob".equals(type)) {
                System.out.println("Processing blob type");
                String blobContent = Helper.readFile(objectsPath + "ob" + hash);
                int idx = blobContent.indexOf("\n");
                String content = idx == -1 ? blobContent : blobContent.substring(idx + 1);
                File file = new File(dir, name);
                file.getParentFile().mkdirs(); // Ensure parent dirs
                System.out.println("Creating file: " + file.getAbsolutePath());
                try (FileWriter fw = new FileWriter(file)) {
                    fw.write(content);
                    System.out.println("Wrote content to file: " + file.getAbsolutePath());
                } catch (IOException e) {
                    System.err.println("Failed to write content to file: " + file.getAbsolutePath() + " due to error: " + e.getMessage());
                }
            } else if ("tree".equals(type)) {
                System.out.println("Processing tree type");
                File subdir = new File(dir, name);
                subdir.mkdirs();
                System.out.println("Created subdirectory: " + subdir.getAbsolutePath());
                writeTree(hash, subdir, objectsPath);
            }
        }
        System.out.println("Finished writeTree process for treeHash: " + treeHash);
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
