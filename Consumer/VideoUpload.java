package Consumer;

public class VideoUpload {
    String fileName;
    byte[] fileData;

    public VideoUpload(String fileName, byte[] fileData) {
        this.fileName = fileName;
        this.fileData = fileData;
    }
}