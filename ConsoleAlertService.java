public class ConsoleAlertService implements StockObserver {
    @Override
    public void onLowStock(Product product) {
        System.out.println("[ALERT] Low stock for " + product.getName() + " (ID: " + product.getId() + ") - only " + product.getQuantity() + " left!");
    }
}
