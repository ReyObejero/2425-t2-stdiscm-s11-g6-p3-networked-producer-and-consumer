package Consumer;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Consumer {
    static int PORT = 5000;
    static BlockingQueue<VideoUpload> uploadQueue;

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

        uploadQueue = new ArrayBlockingQueue<>(queueCapacity);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Consumer server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                
                Thread t1 = new Thread(new HandleConnection(clientSocket)); // new thread for each connection
                t1.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

// sasalo ng vids from Producer, queue is handled with ArrayBlockingQueue
static class HandleConnection implements Runnable {
    private Socket socket;

    public HandleConnection(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            // read file name 
            String fileName = dis.readUTF();

            // read file size
            long fileSize = dis.readLong();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); // allows to write into memory
            byte[] buffer = new byte[4096];
            long remaining = fileSize;

            int toRead;
            int read;

            while (remaining > 0) {
                toRead = (int) Math.min(buffer.length, remaining);
                read = dis.read(buffer, 0, toRead);

                if (read == -1) {
                    break;
                }

                baos.write(buffer, 0, read);
                remaining -= read;
            }

            byte[] fileData = baos.toByteArray();
            VideoUpload upload = new VideoUpload(fileName, fileData);

            // add upload to queue
            boolean enqueued = uploadQueue.offer(upload);

            if (enqueued) {
                System.out.println("Enqueued file: " + fileName);
            } else {
                System.out.println("Queue full. Dropping file: " + fileName); // nMaxQueue+1... is saved to upload queue
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException e) { }
        }
    }
}


}
 