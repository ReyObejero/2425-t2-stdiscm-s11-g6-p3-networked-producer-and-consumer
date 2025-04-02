package Producer;
import java.io.*;
import java.net.*;

public class Producer {

    private String CONSUMER_HOST = "localhost";
    private int CONSUMER_PORT = 5000;

    public static void main(String[] args) {
        // cli input
        // ex. java Producer 4 videos1 videos2
        if (args.length < 2) {
            System.out.println("Usage: java Producer <nThreads> <dir1> <dir2> ...");
            System.exit(1);
        }
        int numProducers = Integer.parseInt(args[0]);
        if (args.length < numProducers + 1) {
            System.out.println("Provide " + numProducers + " directories for producer threads.");
            System.exit(1);
        }
    }

    private void sendVideo(File file) {
        try (Socket socket = new Socket(CONSUMER_HOST, CONSUMER_PORT);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             FileInputStream fis = new FileInputStream(file)) {

            // send file name
            String fileName = file.getName();
            dos.writeUTF(fileName);

            // send file size
            long fileSize = file.length();
            dos.writeLong(fileSize);

            // send file bytes
            byte[] buffer = new byte[4096];
            int read;

            while ((read = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, read);
            }

            dos.flush();
            System.out.println("Uploaded file: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to upload file: " + file.getName());
            e.printStackTrace();
        }
    }
}