package Consumer;
import java.io.*;

public class Consumer {
    int PORT = 5000;

    public static void main(String[] args) {
        int queueCapacity = 10; // default
        int consumerThreads = 4; // default
        // ex. java Consumer 4 10
        if (args.length >= 1) {
            try {
                consumerThreads = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) { }
        }
        if (args.length >= 2) {
            try {
                queueCapacity = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) { }
        }
        
        // Create the upload directory if it does not exist.
        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
     }
 }