import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
public class HuffmanServer {
    private static HuffmanCompressor compressor = new HuffmanCompressor();
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );
        server.createContext("/", HuffmanServer::home);
        server.createContext("/compress", HuffmanServer::compress);
        server.createContext("/decompress", HuffmanServer::decompress);
        server.createContext("/clear", HuffmanServer::clear);
        server.setExecutor(null);
        System.out.println("Huffman Server started!");
        System.out.println("Open: http://localhost:8080");
        server.start();
    }
        private static void home(HttpExchange exchange)
                throws IOException {
                File file = new File("HuffmanCompressor.html");
                if (!file.exists()) {
                sendResponse(exchange,"HuffmanCompressor.html not found.");
                return;
                }
                byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200,bytes.length);
                OutputStream output = exchange.getResponseBody();
                output.write(bytes);
                output.close();
            }
    private static void compress(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, "Invalid request method.");
            return;
        }
        String body = readBody(exchange);
        Map<String, String> data = parseFormData(body);
        String inputPath = data.get("inputPath");
        String outputPath = data.get("outputPath");
        try {
            String result = compressor.compressFile(inputPath, outputPath);
            sendHtmlResponse(exchange, result);
        } catch (Exception e) {
            sendResponse(exchange,
                    "ERROR: " + e.getMessage());
        }
    }
    private static void decompress(HttpExchange exchange)
            throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, "Invalid request method.");
            return;
        }
        String body = readBody(exchange);
        Map<String, String> data = parseFormData(body);
        String inputPath = data.get("inputPath");
        String outputPath = data.get("outputPath");
        try {
            String result = compressor.decompressFile(inputPath, outputPath);
            sendHtmlResponse(exchange, result);
        } 
        catch (Exception e) {
            sendResponse(exchange,"ERROR: " + e.getMessage());
        }
    }
    private static void clear(HttpExchange exchange)
            throws IOException {sendResponse(exchange, "Fields cleared."); }
    private static String readBody(HttpExchange exchange)
        throws IOException {
        InputStream input = exchange.getRequestBody();
        return new String(input.readAllBytes(),StandardCharsets.UTF_8);
    }
    private static Map<String, String> parseFormData(String body)
            throws UnsupportedEncodingException {
        Map<String, String> data = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0],StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1],StandardCharsets.UTF_8);
                data.put(key, value);
            }
        }
        return data;
    }
    private static void sendHtmlResponse(
        HttpExchange exchange,
        String message) throws IOException {
    String html =
            "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<title>Huffman Result</title>" +
            "</head>" +
            "<body>" +
            "<h1>Huffman Compressor</h1>" +
            "<pre>" +
            message +
            "</pre>" +
            "<br>" +
            "<a href='http://localhost:8080'>Go Back</a>" +
            "</body>" +
            "</html>";
    exchange.getResponseHeaders().set("Content-Type","text/html; charset=UTF-8");
    byte[] bytes =html.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(200,bytes.length);
    OutputStream output = exchange.getResponseBody();
    output.write(bytes);
    output.close();
}
    private static void sendResponse(HttpExchange exchange,String response) 
    throws IOException {
        exchange.getResponseHeaders().set("Content-Type","text/plain; charset=UTF-8");
        byte[] bytes =response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200,bytes.length);
        OutputStream output = exchange.getResponseBody();
        output.write(bytes);
        output.close();
    }
}