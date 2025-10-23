import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Warehouse {
    private final Map<String, Product> inventory = new ConcurrentHashMap<>();
    private final List<StockObserver> observers = Collections.synchronizedList(new ArrayList<>());


    public void addObserver(StockObserver obs) {
        Objects.requireNonNull(obs);
        observers.add(obs);
    }


    public void removeObserver(StockObserver obs) {
        observers.remove(obs);
    }


    private void notifyIfLow(Product product) {
        if (product.getQuantity() < product.getReorderThreshold()) {
        // notify observers in a copy to avoid ConcurrentModification
        List<StockObserver> copy;
        synchronized (observers) {
            copy = new ArrayList<>(observers);
        }
        for (StockObserver obs : copy) {
        obs.onLowStock(product);
        }
    }
 }


 // Add new product. If id exists, throws IllegalArgumentException
 public void addProduct(Product product) {
    Objects.requireNonNull(product);
    if (inventory.putIfAbsent(product.getId(), product) != null) {
    throw new IllegalArgumentException("Product with id " + product.getId() + " already exists");
    }
 }


 public Product getProduct(String id) throws ProductNotFoundException {
    Product p = inventory.get(id);
    if (p == null) throw new ProductNotFoundException("Product with id " + id + " not found");
    return p;
 }


 // Receive shipment -> increase quantity
 public void receiveShipment(String productId, int amount) throws ProductNotFoundException {
    Product p = getProduct(productId);
    p.increase(amount);
    notifyIfLow(p);
 }


 // Fulfill order -> decrease quantity
 public void fulfillOrder(String productId, int amount) throws ProductNotFoundException, InsufficientStockException {
    Product p = getProduct(productId);
    synchronized (p) {
        if (p.getQuantity() < amount) {
            throw new InsufficientStockException("Insufficient stock for product " + p.getName() + " (available=" + p.getQuantity() + ")");
        }
         p.decrease(amount);
    }
    notifyIfLow(p);
  }


  public Collection<Product> listProducts() {
    return Collections.unmodifiableCollection(inventory.values());
  }
}
