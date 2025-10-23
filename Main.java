import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final String PERSISTENCE_FILE = "inventory.txt";

        public static void main(String[] args) throws IOException {
            Warehouse warehouse = new Warehouse();
            WarehousePersistence persistence = new WarehousePersistence(PERSISTENCE_FILE);

            // load previous state if available
            persistence.load(warehouse);

            // Attach a console observer
            StockObserver consoleAlert = new ConsoleAlertService();
            warehouse.addObserver(consoleAlert);

            // If no products loaded, add an example product
            try {
                if (warehouse.listProducts().isEmpty()) {
                Product laptop = new Product("P1001", "Laptop", 0, 5);
                warehouse.addProduct(laptop);

                // receive initial shipment
                warehouse.receiveShipment("P1001", 10);
                }
            } catch (Exception e) {
            // ignore example setup errors
            }

            // Start a simple multithreaded simulation as a demonstration
            System.out.println("Inventory saved to " + PERSISTENCE_FILE);
        }


        private static void runDemoSimulation(Warehouse warehouse) {
            System.out.println("Starting demo simulation (multithreaded) ...");
            ExecutorService ex = Executors.newFixedThreadPool(4);


            // simulate shipments and orders concurrently
            ex.submit(() -> {
                try {
                Thread.sleep(300);
                warehouse.receiveShipment("P1001", 5);
                System.out.println("Received shipment of 5 laptops");
                } catch (Exception ignored) {}
            });

            ex.submit(() -> {
                try {
                Thread.sleep(500);
                warehouse.fulfillOrder("P1001", 6);
                System.out.println("Fulfilled 6 laptop orders");
                } catch (Exception e) {
                System.out.println("Order failed in demo: " + e.getMessage());
                }
           });


            ex.submit(() -> {
                try {
                 Thread.sleep(600);
                 warehouse.fulfillOrder("P1001", 5);
                 System.out.println("Fulfilled 5 laptop orders");
                 } catch (Exception e) {
                 System.out.println("Order failed in demo: " + e.getMessage());
                 }
            });


            ex.shutdown();
            try { ex.awaitTermination(2, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
            System.out.println("Demo simulation finished.");
            }
            private static void runInteractiveConsole(Warehouse warehouse) {
                Scanner sc = new Scanner(System.in);
                System.out.println("\n--- Warehouse Console ---");
                printHelp();
                while (true) {
                System.out.print("cmd> ");
                String cmd = sc.nextLine().trim();
                if (cmd.isEmpty()) continue;
                if (cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("quit")) break;
                if (cmd.equalsIgnoreCase("help")) { printHelp(); continue; }
                try {
                  if (cmd.startsWith("add ")) {
                  // format: add id|name|quantity|threshold
                  String[] parts = cmd.substring(4).split("\\|", -1);
                  if (parts.length != 4) { 
                    System.out.println("Invalid add command. See help."); continue; }
                    String id = parts[0];
                    String name = parts[1];
                    int qty = Integer.parseInt(parts[2]);
                    int thr = Integer.parseInt(parts[3]);
                    warehouse.addProduct(new Product(id, name, qty, thr));
                    System.out.println("Product added: " + id);
                  } else if (cmd.startsWith("ship ")) {
                    // ship id qty
                    String[] parts = cmd.split(" ");
                    if (parts.length != 3) { System.out.println("Invalid ship command."); continue; }
                    String id = parts[1];
                    int qty = Integer.parseInt(parts[2]);
                    warehouse.receiveShipment(id, qty);
                    System.out.println("Shipment processed.");
                  } else if (cmd.startsWith("order ")) {
                    String[] parts = cmd.split(" ");
                    if (parts.length != 3) { System.out.println("Invalid order command."); continue; }
                    String id = parts[1];
                    int qty = Integer.parseInt(parts[2]);
                    warehouse.fulfillOrder(id, qty);
                    System.out.println("Order fulfilled.");
                  } else if (cmd.equalsIgnoreCase("list")) {
                    for (var p : warehouse.listProducts()) System.out.println(p);
                  } else {
                    System.out.println("Unknown command. Type 'help' for usage.");
                  }
                } catch (ProductNotFoundException | InsufficientStockException e) {
                  System.out.println("Error: " + e.getMessage());
                } catch (NumberFormatException nfe) {
                  System.out.println("Invalid number format.");
                } catch (Exception e) {
                  System.out.println("Operation failed: " + e.getMessage());
                }
            }
        }
        private static void printHelp() {
        System.out.println("Commands:");
        System.out.println(" add id|name|quantity|threshold -> add new product");
        System.out.println(" ship <id> <qty> -> receive shipment (increase)");
        System.out.println(" order <id> <qty> -> fulfill order (decrease)");
        System.out.println(" list -> list products");
        System.out.println(" help -> show this help");
        System.out.println(" exit / quit -> exit and save\n");
    }
}
