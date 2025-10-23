import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.StringJoiner;

public class WarehousePersistence {
    private final Path file;

    public WarehousePersistence(String filename) {
        this.file = Path.of(filename);
    }

    public void save(Warehouse warehouse) throws IOException {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        for (Product p : warehouse.listProducts()) {
            // format: id|name|quantity|threshold
            sj.add(String.format("%s|%s|%d|%d", p.getId(), p.getName().replace("|"," "), p.getQuantity(), p.getReorderThreshold()));
        }
        Files.writeString(file, sj.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }


    public void load(Warehouse warehouse) throws IOException {
        if (!Files.exists(file)) return;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length != 4) continue;
                String id = parts[0];
                String name = parts[1];
                int qty = Integer.parseInt(parts[2]);
                int thr = Integer.parseInt(parts[3]);
                try {
                    warehouse.addProduct(new Product(id, name, qty, thr));
                } catch (IllegalArgumentException ex) {
                    // ignore duplicates or invalid rows
                }
            }
        }
    }
}
