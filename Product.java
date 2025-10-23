import java.util.Objects;

public class Product {
    private final String id;
    private final String name;
    private int quantity;
    private final int reorderThreshold;


public Product(String id, String name, int quantity, int reorderThreshold) {
if (id == null || id.isEmpty()) throw new IllegalArgumentException("Product id required");
if (name == null || name.isEmpty()) throw new IllegalArgumentException("Product name required");
if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
if (reorderThreshold < 0) throw new IllegalArgumentException("Threshold cannot be negative");
this.id = id;
this.name = name;
this.quantity = quantity;
this.reorderThreshold = reorderThreshold;
}


public String getId() { return id; }
public String getName() { return name; }


// synchronized to be thread-safe when modifying stock
public synchronized int getQuantity() { return quantity; }
public int getReorderThreshold() { return reorderThreshold; }


public synchronized void increase(int amount) {
if (amount <= 0) throw new IllegalArgumentException("Increase amount must be positive");
this.quantity += amount;
}


public synchronized void decrease(int amount) {
if (amount <= 0) throw new IllegalArgumentException("Decrease amount must be positive");
if (amount > this.quantity) throw new IllegalArgumentException("Insufficient stock to decrease");
this.quantity -= amount;
}


@Override
public String toString() {
return String.format("Product{id='%s', name='%s', quantity=%d, threshold=%d}", id, name, quantity, reorderThreshold);
}


@Override
public boolean equals(Object o) {
if (this == o) return true;
if (!(o instanceof Product)) return false;
Product product = (Product) o;
return id.equals(product.id);
}


@Override
public int hashCode() {
return Objects.hash(id);
}
}
