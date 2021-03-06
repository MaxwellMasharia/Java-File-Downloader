import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

public class Server {
    private static final String ROOT = "src/web/index.html";
    private static final String ROOT_CSS = "src/web/style.css";
    private static final String ROOT_JS = "src/web/app.js";
    private static File[] files = new File("C:/Users/user/Desktop/Movie").listFiles();
    private static int totalSentFiles = 0;
    private static PrintWriter eventSource = null;

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

    private static void startFileDownload(HttpExchange httpExchange) {
        sendFile(httpExchange, files[totalSentFiles]);
        totalSentFiles++;
        // Send a message to the client to tell the client to request the next file
        eventSource.println("data:" + totalSentFiles + "/" + (files.length));
        eventSource.println("\n\n");
        eventSource.flush();
    }

    private static void sendFile(HttpExchange httpExchange, File fl) {
        new Thread(() -> {
            try {
                // Send the Headers
                Headers responseHeaders = httpExchange.getResponseHeaders();
                responseHeaders.add("Content-Disposition", "attachment; filename=\"" + fl.getName() + "\"");
                httpExchange.sendResponseHeaders(200, fl.length());

                // Send the File
                OutputStream dataTo = httpExchange.getResponseBody();
                FileInputStream dataFrom = new FileInputStream(fl);
                BufferedPipeStream bufferedPipeStream = new BufferedPipeStream(dataFrom, dataTo, (int) fl.length());
                bufferedPipeStream.transfer();

                // close the Steams
                dataTo.close();
                dataFrom.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void startEventSource(HttpExchange httpExchange) {
        try {
            httpExchange.getResponseHeaders().add("Content-Type", "text/event-stream");
            httpExchange.sendResponseHeaders(200, 0);
            eventSource = new PrintWriter(httpExchange.getResponseBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(4040), 0);
        server.createContext("/", httpExchange -> sendRequestedFile(httpExchange, ROOT));
        server.createContext("/app.js", httpExchange -> sendRequestedFile(httpExchange, ROOT_JS));
        server.createContext("/style.css", httpExchange -> sendRequestedFile(httpExchange, ROOT_CSS));
        server.createContext("/downloads", Server::startFileDownload);
        server.createContext("/events", Server::startEventSource);


        server.start();
        System.out.println("Server started ... Listening @port " + server.getAddress().getPort());
    }
}
