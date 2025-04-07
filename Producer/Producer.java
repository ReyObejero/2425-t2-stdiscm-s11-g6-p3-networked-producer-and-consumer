package producer;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.security.*;

public class Producer {

    public static void main(String[] args) {
        // cli input
        // ex. java Producer 2 videos1 videos2
        if (args.length < 2) {
            System.out.println("Usage: java Producer <nThreads> <dir1> <dir2> ...");
            System.exit(1);
        }
        int numProducers = Integer.parseInt(args[0]);
        if (args.length < numProducers + 1) {
            System.out.println("Provide " + numProducers + " directories for producer threads.");
            System.exit(1);
        }
        ExecutorService executor = Executors.newFixedThreadPool(numProducers);
        for (int i = 0; i < numProducers; i++) {
            String folderPath = args[i + 1];
            executor.submit(new ProducerThread(folderPath));
        }
        executor.shutdown();
    }
}

class ProducerThread implements Runnable {
    private String folderPath;
    private String HOST = "localhost";
    private int PORT = 5000;

    public ProducerThread(String folderPath) {
        this.folderPath = folderPath;
    }

    public void run() {
        try {
            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                System.err.println("Folder " + folderPath + " does not exist");
                return;
            }

            File[] files = folder.listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".mp4") || lower.endsWith(".avi") || lower.endsWith(".mov");
            });

            if (files == null || files.length == 0) {
                System.out.println("Empty video files in folder " + folderPath);
                return;
            }

            for (File file : files) {
                sendVideo(file);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendVideo(File file) {
        try (Socket socket = new Socket(HOST, PORT);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // send file name
            String fileName = file.getName();
            dos.writeUTF(fileName);

            // send file size
            long fileSize = file.length();
            dos.writeLong(fileSize);

            // send file bytes
            byte[] buffer = new byte[4096];
            int read;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((read = fis.read(buffer)) != -1) {
                md.update(buffer, 0, read);
                baos.write(buffer, 0, read);
            }

            byte[] hashBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            String sha256Hash = sb.toString();

            byte[] fileBytes = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);

            while ((read = bais.read(buffer)) != -1) {
                dos.write(buffer, 0, read);
            }

            dos.writeUTF(sha256Hash);

            dos.flush();
            System.out.println("Uploaded file: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to upload file: " + file.getName());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
