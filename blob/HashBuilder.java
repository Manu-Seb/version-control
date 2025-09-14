import java.time.LocalDateTime;

public class HashBuilder {
    public static String treeHash(String content){
        String hash = BlobGen.createHash(content);
        return hash;
        
    }

    public static String blobHash(String content){
        String hash = BlobGen.createHash(content);
        return hash;

    }
}
