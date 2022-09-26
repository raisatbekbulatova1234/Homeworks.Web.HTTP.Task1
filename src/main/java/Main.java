import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import server.Server;


public class Main {
    public static void main(String[] args) {
        server.Server server = new Server(9999);
        initializeHandlers(server);
        server.printHandlers();
        server.start();
    }

    private static void initializeHandlers(Server server) {
        server.addHandler("GET",
                "/index.html",
                (request, responseStream) -> {
                    try {
                        final Path filePath = Path.of(".", "public", "/index.html");
                        final String mimeType = Files.probeContentType(filePath);
                        final long length = Files.size(filePath);
                        responseStream.write((
                                "HTTP/1.1 200 OK\r\n" +
                                        "Content-Type: " + mimeType + "\r\n" +
                                        "Content-Length: " + length + "\r\n" +
                                        "Connection: close\r\n" +
                                        "\r\n"
                        ).getBytes());
                        Files.copy(filePath, responseStream);
                        responseStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        server.addHandler("GET", "/classic.html", (request, responseStream) -> {
            try {
                final Path filePath = Path.of(".", "public", "/classic.html");
                final String mimeType = Files.probeContentType(filePath);
                final String template = Files.readString(filePath);
                final byte[] content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                responseStream.write(content);
                responseStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler("POST", "/", (request, responseStream) -> {
            try {
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                responseStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}