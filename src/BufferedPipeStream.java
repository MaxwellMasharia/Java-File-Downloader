import java.io.*;

public class BufferedPipeStream {
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;
    private int contentLength;

    BufferedPipeStream(InputStream dataIn, OutputStream dataOut, int dataLength) {
        this.bufferedInputStream = new BufferedInputStream(dataIn);
        this.bufferedOutputStream = new BufferedOutputStream(dataOut);
        this.contentLength = dataLength;
    }

    public void transfer() throws IOException {
        int totalReadBytes = 0;
        do {
            bufferedOutputStream.write(bufferedInputStream.read());
            totalReadBytes++;
//            System.out.println("Read Byte >> "+totalReadBytes);
        } while (totalReadBytes != contentLength);
        bufferedOutputStream.flush();
        System.out.println("DONE >> "+totalReadBytes);
    }
}
