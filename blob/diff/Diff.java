package diff;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import utils.Helper;

public abstract class Diff {
    public abstract void getDiff(String commit1, String commit2);

    protected Map<String, String> buildFileMap(String objectHash, String objectsPath) {
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

    protected void printFileHashMap(Map<String, String> fileHashMap) {
        // Sort map by key (file paths) for nicer output
        Map<String, String> sortedMap = new TreeMap<>(fileHashMap);
        System.out.println("File Path                               | Object Hash");
        System.out.println("---------------------------------------|---------------------------");
        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            System.out.printf("%-39s | %s\n", entry.getKey(), entry.getValue());
        }
    }
}
