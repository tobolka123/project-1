import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Order {
    private static int nextOrderId = 1;

    private int orderId;
    private Dish dish;
    private int quantity;
    private int tableNumber;
    private LocalDateTime orderedTime;
    private LocalDateTime fulfilmentTime;
    private String regex = ";";
    private boolean isPaid;

    public Order(Dish dish, int quantity, int tableNumber, LocalDateTime orderedTime) {
        this.orderId = nextOrderId++;
        this.dish = dish;
        this.quantity = quantity;
        this.tableNumber = tableNumber;
        this.orderedTime = orderedTime;
        this.fulfilmentTime = orderedTime.plusMinutes(dish.getPreparationTime());
        this.isPaid = false;
    }

    public int getOrderId() {
        return orderId;
    }

    public Dish getDish() {
        return dish;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public LocalDateTime getOrderedTime() {
        return orderedTime;
    }

    public LocalDateTime getFinishedTime() {
        return fulfilmentTime;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid() {
        this.isPaid = true;
    }

    public int calculateFulfilmentTime() {
        if (fulfilmentTime != null) {
            return (int) orderedTime.until(fulfilmentTime, ChronoUnit.MINUTES);
        }
        return 0;
    }

    public double calculateTotalPrice() {
        if (dish != null) {
            return quantity * dish.getPrice().intValue();
        }
        return 0.0;
    }

    public String exportOrder(int poradi) {
        if (fulfilmentTime != null) {
            fulfilmentTime.plusMinutes(dish.getPreparationTime());
        }
        return poradi + ". " + dish.getTitle() + " " +
                quantity + "x (" + dish.getPrice().intValue() + " Kč): " +
                formatTime(orderedTime) + "-" +
                (fulfilmentTime != null ? formatTime(fulfilmentTime): "") +
                (isPaid ? " zaplaceno" : "") + "\n";
    }

    @Override
    public String toString() {
        return dish + regex +"\t" + quantity + regex +"\t" + tableNumber + regex + "\t" + orderedTime.toString();
    }

    public static String formatTime(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

}