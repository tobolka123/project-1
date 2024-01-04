import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RestourantManager {
    private static List<Dish> menu;
    private static List<Order> orders;
    private static final String regex = ";";

    public RestourantManager() {
        this.menu = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public int getNotdoneOrdersCount() {
        int count = 0;
        for (Order order : orders) {
            if (!order.isPaid()) {
                count++;
            }
        }
        return count;
    }

    public List<Order> getOrdersSortedByTime() {
        List<Order> sortedOrders = new ArrayList<>(orders);
        List<Order> finalOrd = new ArrayList<>();
        Collections.sort(sortedOrders, Comparator.comparing(Order::getOrderedTime));
        finalOrd.addAll(sortedOrders);
        return finalOrd;
    }

    public double getAverageOrderTime() {
        if (orders.isEmpty()) {
            return 0.0;
        }

        int totalTime = 0;
        int finishedOrdersCount = 0;

        for (Order order : orders) {
            if (order.isPaid() && order.getFinishedTime() != null) {
                totalTime += order.calculateFulfilmentTime();
                finishedOrdersCount++;
            }
        }

        return (double) totalTime / finishedOrdersCount;
    }


    public Set<Dish> getDailyOrderedDishes() {
        Set<Dish> dailyOrderedDishes = new HashSet<>();
        LocalDate now = LocalDate.now();
        LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0);
        for (Order order : orders) {
            if (order != null) {
                LocalDateTime orderedTime = order.getOrderedTime();
                if (orderedTime.isAfter(today)) {
                    dailyOrderedDishes.add(order.getDish());
                    }
                }
            }
        return dailyOrderedDishes;

    }


    public String exportOrdersForTable(int tableNumber) {
        StringBuilder output = new StringBuilder();
        Set<Order> orderSet = new HashSet<>();
        output.append("** Objednávky pro stůl č. ").append(String.format("%02d", tableNumber)).append(" **\n****\n");
        int i = 0;
        for (Order ordSetAdd : orders) {
            orderSet.add(ordSetAdd);
        }
        for (Order order : orderSet) {
            if (order != null) {
                if (order.getDish() != null && order.getDish().getImageUrl() != null  && order.getTableNumber() == tableNumber) {
                    i++;
                    output.append(order.exportOrder(i));
                }
            }
        }

        output.append("****\n");

        return output.toString();
    }
    public static void addDish(Dish dish) {
        if (!menu.contains(dish)) {
            menu.add(dish);
        }
    }

    public void removeDish(Dish dish) {
        menu.remove(dish);
    }

    public static void addOrder(Order order) {
        orders.add(order);
    }

    public double calculateTotalBillForTable(int tableNumber) {
        double totalBill = 0.0;

        for (Order order : orders) {
            if (order != null) {
                if (order.isPaid() && order.getTableNumber() == tableNumber) {
                    totalBill += order.calculateTotalPrice();
                }
            }
        }

        return totalBill;
    }

    public static List<Order> loadFromFile(String filename) throws RestException {
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(filename)))) {
            orders = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!Objects.equals(line, "null"))
                    orders.add(parseOrder((line)));
            }
        } catch (FileNotFoundException e) {
            throw new RestException("Nepodařilo se nalézt soubor " + filename + ": " + e.getLocalizedMessage());
        }
        return orders;
    }
    public static void loadToFile(String fileName) throws RestException {
        try (PrintWriter outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {{
            for (Order ord : orders)
                if (ord != null) {
                    outputWriter.println(ord);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Order parseOrder(String line) throws RestException {
        String[] blocks = line.split(regex);
        int numOfBlocks = blocks.length;
        if (line.isEmpty()) {
            return null;
        }
        if (numOfBlocks != 4) {
            throw new RestException(
                    "Nesprávný počet položek na řádku: " + line +
                            "! Počet položek: " + numOfBlocks + ".");
        }
        Dish dish;
        try {
            String block = blocks[0].replace("{", "");
            block = block.replace("}", "");
            String[] blocksDish = block.split(Dish.getReg());
            int numOfBlocksDish = blocksDish.length;
            if (numOfBlocksDish != 4) {
                throw new RestException(
                        "Nesprávný počet položek na řádku: " + line +
                                "! Počet položek: " + numOfBlocksDish + ".");
            }
            String title;
            BigDecimal price;
            int preptime;
            String url;
            title = blocksDish[0].trim();
            try {
                price = BigDecimal.valueOf(Integer.parseInt(blocksDish[1].trim()));
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RestException("cena nesmi byt zaporna" + blocksDish[1]);
                }
            } catch (RuntimeException e) {
                throw new RestException("chybne zadana cena" + blocksDish[1]);
            }
            try {
                preptime = Integer.parseInt(blocksDish[2].trim());
                if (preptime < 0){
                    throw new RestException("Chybne zadane cislo" + blocksDish[2]);
                }
            } catch (NumberFormatException e) {
                throw new RestException("Chybne zadane cislo" + blocksDish[2]);
            }
            url = blocksDish[3].trim();
            dish = new Dish(title, price, preptime, url);
            addDish(dish);

        } catch (NumberFormatException e) {
            throw new RestException("chybne zadane jidlo" + blocks[0]);
        }
        int quantity;
        try {
            quantity = Integer.parseInt(blocks[1].trim());
            if (quantity < 0){
                throw new RestException("Chybne zadane cislo" + blocks[1]);
            }
        } catch (NumberFormatException e) {
            throw new RestException("Chybne zadane cislo" + blocks[1]);
        }
        int table;
        try {
            table = Integer.parseInt(blocks[2].trim());
            if (table < 0){
                throw new RestException("Chybne zadane cislo" + blocks[2]);
            }
        } catch (NumberFormatException e) {
            throw new RestException("Chybne zadane cislo" + blocks[2]);
        }
        LocalDateTime orderTime;
        try {
            orderTime = LocalDateTime.parse(blocks[3].trim());
        } catch (NumberFormatException e) {
            throw new RestException("Chybne zadane datum" + blocks[3]);
        }
        return new Order(dish, quantity, table, orderTime);
    }

    public static String getRegex() {
        return regex;
    }

    @Override
    public String toString() {
        String output = "";
        for (Order ord : orders) {
            if (ord != null) {
                output = output + ord;
            }
            else {
                System.out.println("neco je spatne");
            }
        }
        return output;
    }

    public static List<Dish> getMenu() {
        return menu;
    }
    public static String getPrettyMenu() {
        String prettymenu = "";
        for (Dish dish:menu) {
            if (dish != null) {
                prettymenu += dish.getTitle() + ", ";
            }
        }
        return prettymenu;
    }

    public static String formatTime(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    public static String formatTime2(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("yy:MM:dd"));
    }

}
