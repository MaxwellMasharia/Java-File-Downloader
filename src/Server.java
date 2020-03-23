import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server {
    private static final String ROOT = "src/web/index.html";
    private static final String ROOT_CSS = "src/web/style.css";
    private static final String ROOT_JS = "src/web/app.js";

    private static int getFileSize(String fileName) {
        return (int) new File(fileName).length();
    }

    private static byte[] getFileData(String fileName) throws IOException {
        byte[] fileData = new byte[getFileSize(fileName)];
        FileInputStream fileInputStream = new FileInputStream(fileName);
        int readBytes = fileInputStream.read(fileData);
        System.out.println("Read " + readBytes + " bytes from the file " + fileName);
        fileInputStream.close();
        return fileData;
    }

    private static String getFileType(String fileName) {
        if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "text/javascript";
        }
        return "";
    }

    private static void sendRequestedFile(HttpExchange httpExchange, String fileName) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", getFileType(fileName));
        httpExchange.sendResponseHeaders(200, getFileSize(fileName));

        OutputStream dataOut = httpExchange.getResponseBody();
        dataOut.write(getFileData(fileName));
        dataOut.flush();
        dataOut.close();
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(4040), 0);
        server.createContext("/", httpExchange -> sendRequestedFile(httpExchange, ROOT));
        server.createContext("/app.js", httpExchange -> sendRequestedFile(httpExchange, ROOT_JS));
        server.createContext("/style.css", httpExchange -> sendRequestedFile(httpExchange, ROOT_CSS));


        server.start();
        System.out.println("Server started ... Listening @port " + server.getAddress().getPort());
    }
}
