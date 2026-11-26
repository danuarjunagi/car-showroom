import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Simple text-file persistence helper. Each field is Base64 encoded to protect delimiters. */
public final class DataStore {
    private static final String DATA_DIR = "data";
    private DataStore() { }

    public static void initialize() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get("invoices"));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create application data folders", e);
        }
    }

    public static List<String> readLines(String fileName) {
        initialize();
        Path path = Paths.get(DATA_DIR, fileName);
        if (!Files.exists(path)) return new ArrayList<String>();
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Warning: could not read " + path + ": " + e.getMessage());
            return new ArrayList<String>();
        }
    }

    public static void writeLines(String fileName, Collection<String> lines) {
        initialize();
        Path path = Paths.get(DATA_DIR, fileName);
        try {
            Files.write(path, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Warning: could not save " + path + ": " + e.getMessage());
        }
    }

    public static void writeInvoice(String fileName, String content) {
        initialize();
        Path path = Paths.get("invoices", fileName);
        try {
            Files.write(path, content.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Warning: invoice could not be written: " + e.getMessage());
        }
    }

    public static String pack(String... fields) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) result.append('|');
            String value = fields[i] == null ? "" : fields[i];
            result.append(Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8)));
        }
        return result.toString();
    }

    public static String[] unpack(String line, int expectedFields) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != expectedFields) return null;
        String[] result = new String[parts.length];
        try {
            for (int i = 0; i < parts.length; i++) {
                result[i] = new String(Base64.getDecoder().decode(parts[i]), StandardCharsets.UTF_8);
            }
            return result;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
