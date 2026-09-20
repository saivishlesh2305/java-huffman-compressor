# Java Huffman Compression Tool

A Java-based text compression and decompression tool that implements the Huffman Coding algorithm with a simple HTML-based web interface.

## 🚀 Features

- Huffman tree construction using character frequencies
- Lossless text encoding and decoding
- File-based compression and decompression
- Automatic generation of Huffman codes
- Encoding map storage for decompression
- Decompression verification
- Local web interface
- Java HTTP server for handling requests

## 🛠️ Technologies Used

- Java
- HTML
- Huffman Coding Algorithm
- Java PriorityQueue
- Java HTTP Server
- File I/O

## 📂 Project Structure

### `HuffmanCompressor.java`

Implements the core Huffman compression and decompression logic.

It:
- Calculates character frequencies
- Builds the Huffman tree
- Generates binary codes
- Encodes and decodes text
- Reads and writes files
- Stores and reconstructs the encoding map

### `HuffmanServer.java`

Runs a local HTTP server on port `8080` and connects the HTML interface with the Huffman compression engine.

### `HuffmanCompressor.html`

Provides the web-based interface for entering input and output file paths and performing compression or decompression.

## ⚙️ How It Works

1. The input text is read from a file.
2. Character frequencies are calculated.
3. A Huffman tree is constructed using a priority queue.
4. Binary codes are generated for each character.
5. The input text is converted into its Huffman encoded representation.
6. The encoded data and encoding map are stored.
7. During decompression, the encoding map is reconstructed and the original text is restored.

## ▶️ How to Run

Compile the Java files:

```bash
javac HuffmanCompressor.java HuffmanServer.java
