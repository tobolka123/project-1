import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws RestException {
        RestourantManager manager = new RestourantManager();

        Dish dish1 = new Dish("Kuřecí řízek obalovaný", BigDecimal.valueOf(150), 20, "kuřecí-rizek.jpg");
        Dish dish2 = new Dish("Hranolky", BigDecimal.valueOf(80), 15, "hranolky.jpg");
        Dish dish3 = new Dish("Pstruh na víně", BigDecimal.valueOf(220), 30, "pstruh.jpg");
        Dish dish4 = new Dish("Kofola", BigDecimal.valueOf(40), 5, "kofola.jpg");

        manager.addDish(dish1);
        manager.addDish(dish2);
        manager.addDish(dish3);
        manager.addDish(dish4);
        manager.loadFromFile("invalid_data.txt");

        Order order1 = new Order(dish1, 2, 15, LocalDateTime.of(2019, 5, 25, 14, 15));
        Order order2 = new Order(dish2, 2, 15, LocalDateTime.of(2019, 5, 25, 15, 20));
        Order order3 = new Order(dish4, 2, 15, LocalDateTime.of(2019, 5, 25, 16, 40));
        Order order4 = new Order(dish3, 1, 2, LocalDateTime.of(2020, 4, 7, 2, 52));
        Order order5 = new Order(dish4, 1, 2, LocalDateTime.of(2024, 1, 4, 8, 48));

        order1.setPaid();
        order3.setPaid();


        manager.addOrder(order1);
        manager.addOrder(order2);
        manager.addOrder(order3);
        manager.addOrder(order4);
        manager.addOrder(order5);

        System.out.println("nedodelane objednavky: " + manager.getNotdoneOrdersCount());
        System.out.println("Objednavky srovnane podle casu: " + manager.getOrdersSortedByTime());
        System.out.println("prumnerny cas na objednavce: " + manager.getAverageOrderTime());

        manager.loadToFile("data.txt");
        //System.out.println(manager.loadFromFile("data2.txt"));

        double totalBillForTable15 = manager.calculateTotalBillForTable(15);
        System.out.println("celkova cena pro stul 15: " + totalBillForTable15);

        String ordersForTable15 = manager.exportOrdersForTable(2);
        System.out.println(ordersForTable15);

        System.out.println("dnes objednanych jidel: " + manager.getDailyOrderedDishes());

        System.out.println(manager.getPrettyMenu());

        System.out.println("\n");
        try {
            manager.loadFromFile("data.txt");
        } catch (RestException e) {
            System.err.println("chybne zadana data");
        }
        System.out.println(manager.exportOrdersForTable(2));
        try {
            manager.loadFromFile("data1.txt");
        } catch (RestException e) {
            System.err.println("chybne zadana data");
        }
    }
}