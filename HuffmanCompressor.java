import java.io.*;
import java.nio.file.*;
import java.util.*;
public class HuffmanCompressor{
    private static class Node implements Comparable<Node> {
        char data;
        int frequency;
        Node left;
        Node right;
        Node(char data, int frequency) {
            this.data = data;
            this.frequency = frequency;
        }
        Node(int frequency, Node left, Node right) {
            this.data = '\0';
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }
        public int compareTo(Node other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }
    private Node root;
    private Map<Character, String> encodeM;
    private Map<String, Character> decodeM;
    public HuffmanCompressor() {
        this.encodeM = new HashMap<>();
        this.decodeM = new HashMap<>();
    }
    public void buildHuffmanTree(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Input text cannot be empty");
        }
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        PriorityQueue<Node> minHeap = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            minHeap.offer(new Node(entry.getKey(), entry.getValue()));
        }
        while (minHeap.size() > 1) {
            Node left = minHeap.poll();
            Node right = minHeap.poll();
            Node parent = new Node(left.frequency + right.frequency, left, right);
            minHeap.offer(parent);
        }
        this.root = minHeap.poll();
        this.encodeM.clear();
        this.decodeM.clear();
        generateCodes(root, "");
    }
    private void generateCodes(Node node, String code) {
        if (node == null) {
            return;
        }
        if (node.left == null && node.right == null) {
            encodeM.put(node.data, code.isEmpty() ? "0" : code);
            decodeM.put(code.isEmpty() ? "0" : code, node.data);
            return;
        }
        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    }
    public String encode(String text) {
        if (encodeM.isEmpty()) {
            throw new IllegalStateException("Must call buildHuffmanTree() first");
        }
        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) {
            encoded.append(encodeM.get(c));
        }
        return encoded.toString();
    }
    public String decode(String encodedText) {
        if (decodeM.isEmpty()) {
            throw new IllegalStateException("Must call buildHuffmanTree() first");
        }
        StringBuilder decoded = new StringBuilder();
        StringBuilder code = new StringBuilder();
        for (char bit : encodedText.toCharArray()) {
            code.append(bit);
            if (decodeM.containsKey(code.toString())) {
                decoded.append(decodeM.get(code.toString()));
                code = new StringBuilder();
            }
        }
        return decoded.toString();
    }
    public String readFile(String filePath) throws IOException {
        if (!Files.exists(Paths.get(filePath))) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        return Files.readString(Paths.get(filePath));
    }
    public void writeFile(String filePath, String content) throws IOException {
        Files.writeString(Paths.get(filePath), content);
    }
    public String compressFile(String inputPath, String outputPath) throws IOException {
    String originalText = readFile(inputPath);
    buildHuffmanTree(originalText);
    String encoded = encode(originalText);
    String output = originalText.length() + "|" + encoded + "|" + serializeEncodingMap();
    writeFile(outputPath, output);
    double ratio = ((double) encoded.length() /
            (originalText.length() * 8)) * 100;
    return "COMPRESSION SUCCESSFUL!\n" +
           "Original size: " + originalText.length() + " characters\n" +
           "Compressed size: " + encoded.length() + " bits\n" +
           String.format("Compression ratio: %.2f%%", ratio);
        }
    public String decompressFile(String inputPath, String outputPath) throws IOException {
    String content = readFile(inputPath);
    String[] parts = content.split("\\|", 3);
    if (parts.length < 3) {
        throw new IllegalArgumentException("Invalid compressed file format");
    }
    String encoded = parts[1];
    deserializeEncodingMap(parts[2]);
    String decoded = decode(encoded);
    writeFile(outputPath, decoded);
    boolean verified =
            decoded.length() == Integer.parseInt(parts[0]);
    return "DECOMPRESSION SUCCESSFUL!\n" +
           "Decompressed size: " + decoded.length() + " characters\n" +
           "Verification: " + (verified ? "PASSED" : "FAILED");
}  
  private String serializeEncodingMap() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Character, String> entry : encodeM.entrySet()) {
        sb.append((int) entry.getKey())
          .append(":")
          .append(entry.getValue())
          .append(",");
    }
    return sb.toString();
}
private void deserializeEncodingMap(String mapString) {
    decodeM.clear();
    if (mapString==null || mapString.isEmpty()) {
        return;
    }
    String[] pairs = mapString.split(",");
    for (String pair : pairs) {
        if (pair.isEmpty()) {
            continue;
        }
        String[] kv = pair.split(":", 2);
        if (kv.length == 2) {
            char c = (char) Integer.parseInt(kv[0]);
            decodeM.put(kv[1], c);
        }
    }
}
}