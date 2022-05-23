package business;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DeliveryService implements IDeliveryServiceProcessing, Serializable {
    public final Map<Order, ArrayList<MenuItem>> orders;
    public List<MenuItem> allProducts;
    public ArrayList<Client> clients;
    public Client currentClient;

    public Client getCurrentClient() {
        return currentClient;
    }

    public void setCurrentClient(Client currentClient) {
        this.currentClient = currentClient;
    }

    public boolean invariant() {
        if (this.allProducts.size() > 0)
            return true;
        return false;
    }

    public DeliveryService() throws IOException, ClassNotFoundException {
        this.orders = new HashMap<>();
        this.allProducts = new ArrayList<>();
        this.clients = new ArrayList<>();
        serializationRead();
        try {
            loadClients();
        } catch (CsvException e) {
            e.printStackTrace();
        }
        try {
            serializationReadOrders();
        }
        catch(EOFException e){

        }
    }

    public void loadClients() throws IOException, CsvException {
        CSVReader reader = new CSVReader(new FileReader("C:\\Users\\user\\Desktop\\SEM 2 MonkaS\\TP\\PT2022_30223_Andrei-Florin_Vasilache_Assignment_4\\src\\main\\java\\database\\users.csv"));
        List<String[]> r = reader.readAll();
        r.forEach(line -> {
            try {
                Client c = new Client(Integer.parseInt(line[0]), line[1], line[2]);
                clients.add(c);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @param id
     * @param nume
     * @pre this!=null
     * @post id == 1
     * @invariant metoda
     */
    public void addClient(int id, String nume) {
        assert this != null;
        this.clients.add(new Client(id, nume));
        assert id == 1;
        assert invariant();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * @throws FileNotFoundException
     * @pre this!= null
     * @post f != null
     * @invariant metoda
     */
    public void readProducts() throws FileNotFoundException {
        assert this != null;
        File f = new File("C:\\Users\\user\\Desktop\\SEM 2 MonkaS\\TP\\PT2022_30223_Andrei-Florin_Vasilache_Assignment_4\\products.csv");
        Scanner read = new Scanner(f);
        read.nextLine();
        while (read.hasNext()) {
            String[] a = read.nextLine().split(",");
            this.allProducts.add(new BaseProduct(a[0], Float.parseFloat(a[1]), Integer.parseInt(a[2]), Integer.parseInt(a[3]), Integer.parseInt(a[4]), Integer.parseInt(a[5]), Integer.parseInt(a[6])));
        }
        read.close();
        this.allProducts = this.allProducts.stream().filter(distinctByKey(BaseProduct::getTitle)).collect(Collectors.toList());
        assert f != null;
        assert invariant();
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    /**
     * @param p
     * @pre this!=null
     * @post p!=null
     * @invariant metoda
     */
    @Override
    public void importProduct(MenuItem p) {
        assert this != null;
        this.allProducts.add(p);
        assert p != null;
        assert invariant();
        //this.allProducts = this.allProducts.stream().filter(distinctByKey(BaseProduct::getTitle)).collect(Collectors.toList());
    }

    /**
     * @param o
     * @param mi
     * @pre this!=null
     * @post o!=null
     * @invariant metoda
     */
    public void addOrder(Order o, ArrayList<MenuItem> mi) {
        assert this != null;
        this.orders.put(o, mi);
        try {
            serializationWriteOrders();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert o != null;
        assert invariant();
    }

    /**
     * @param a
     * @param b
     * @return
     * @pre this!=null
     * @post a!= null
     * @invariant metoda
     */
    // doua composite products sunt la fel daca au aceeasi lista de produse
    public boolean checkTheSameCompositeProducts(CompositeProduct a, CompositeProduct b) {
        assert this != null;
        if (a.getAllBaseProducts().size() != b.getAllBaseProducts().size()) {
            assert a != null;
            return false;
        } else {
            for (int i = 0; i < a.getAllBaseProducts().size(); i++) {
                if (a.getAllBaseProducts().get(i).getTitle() != b.getAllBaseProducts().get(i).getTitle())
                    assert a != null;
                return false;
            }
            assert a != null;
            return true;
        }
    }

    /**
     * @pre this!=null
     * @post p != null
     * @invariant metoda
     * @param p
     */
    public void deleteProduct(MenuItem p) {
        assert this != null;
        for (MenuItem mi : this.allProducts) {
            if (p instanceof BaseProduct && mi instanceof BaseProduct) {
                if (((BaseProduct) mi).getTitle().equals(((BaseProduct) p).getTitle())) {
                    this.allProducts.remove(mi);
                    break;
                }
            } else {
                if (p instanceof BaseProduct && mi instanceof CompositeProduct) {
                    for (BaseProduct b : ((CompositeProduct) mi).getAllBaseProducts())
                        if (b.getTitle().equals(((BaseProduct) p).getTitle())) {
                            ((CompositeProduct) mi).getAllBaseProducts().remove(b);
                            break;
                        }
                }
            }
        }
        for (Order o : this.orders.keySet()) {
            assert this != null;
            for (MenuItem d : this.orders.get(o)) {
                if (p instanceof CompositeProduct && d instanceof CompositeProduct) {
                    if (checkTheSameCompositeProducts((CompositeProduct) p, (CompositeProduct) d)) {
                        this.orders.get(o).remove(d);
                        break;
                    }
                } else {
                    if (p instanceof BaseProduct && d instanceof BaseProduct) {
                        this.orders.get(o).remove(d);
                        break;
                    } else {
                        if (p instanceof BaseProduct && d instanceof CompositeProduct) {
                            ((CompositeProduct) d).getAllBaseProducts().removeIf(b -> b.getTitle().equals(((BaseProduct) p).getTitle()));
                            break;
                        }
                    }
                }
            }
        }
        assert p != null;
        assert invariant();
    }

    /**
     * @pre this!=null
     * @post !hour.equals("")
     * @invariant metoda
     * @param hour
     * @return
     */
    @Override
    public int convertToMinutes(String hour) {
        assert this != null;
        String[] x = hour.split(":");
        assert !hour.equals("");
        assert invariant();
        return Integer.parseInt(x[0]) * 60 + Integer.parseInt(x[1]);
    }

    /**
     * @pre this!=null
     * @post f != null
     * @invariant metoda
     * @param f
     * @param endHour
     * @param startHour
     * @throws IOException
     */
    public void reportTimeInterval(FileWriter f, String endHour, String startHour) throws IOException {
        assert this != null;
        int totalMin = convertToMinutes(endHour) - convertToMinutes(startHour);

        for (Order o : this.orders.keySet()) {
            if (convertToMinutes(endHour) - convertToMinutes(o.getOrderDate().split(" ")[1]) <= totalMin) {
                f.write("OrderID: " + o.getOrderID() + "\nClientID: " + o.getClientID() + "\nOrderDate: " + o.getOrderDate() + "\n\n");
            }
        }
        assert f != null;
        assert invariant();
    }

    /**
     * @pre this!=null
     * @post f!=null
     * @invariant metoda
     * @param f
     * @param nrOfTimesProducts
     * @throws IOException
     */
    public void reportProducts(FileWriter f, int nrOfTimesProducts) throws IOException {
        assert this != null;
        for (MenuItem mi : allProducts) {
            int contor = 0;
            if (mi instanceof BaseProduct) {
                for (Order o : this.orders.keySet()) {
                    for (MenuItem mi2 : this.orders.get(o)) {
                        if (mi2 instanceof BaseProduct && ((BaseProduct) mi).getTitle().equals(((BaseProduct) mi2).getTitle()))
                            contor++;
                    }
                }
            } else if (mi instanceof CompositeProduct) {
                for (Order o : this.orders.keySet()) {
                    for (MenuItem mi2 : this.orders.get(o)) {
                        if (mi2 instanceof CompositeProduct && ((CompositeProduct) mi).getName().equals(((CompositeProduct) mi2).getName()))
                            contor++;
                    }
                }
            }
            if (contor > nrOfTimesProducts) {
                if (mi instanceof BaseProduct)
                    f.write("Product name: " + ((BaseProduct) mi).getTitle() + "\n");
                else {
                    assert mi instanceof CompositeProduct;
                    f.write("Product name: " + ((CompositeProduct) mi).getName() + "\n");
                }
            }
        }
        assert f != null;
        assert invariant();
    }

    /**
     * @pre this!=null
     * @post f != null
     * @invariant metoda
     * @param f
     * @param nrOfTimes
     * @param orderValue
     * @throws IOException
     */
    public void reportClient(FileWriter f, int nrOfTimes, int orderValue) throws IOException {
        assert this != null;
        for (Client c : this.clients) {
            int contor = 0;
            for (Order o : this.orders.keySet()) {
                if (o.getClientID() == c.getId()) {
                    contor++;

                    int price = 0;
                    for (MenuItem mi : this.orders.get(o)) {
                        if (mi instanceof BaseProduct)
                            price += ((BaseProduct) mi).getPrice();
                        else if (mi instanceof CompositeProduct)
                            price += mi.getTotalPrice();
                    }
                    if (price > orderValue)
                        f.write("Value of the order: " + price + " \n");
                }
            }

            if (contor > nrOfTimes)
                f.write("Client: " + c.getNume() + "\n");
        }
        assert f != null;
        assert invariant();
    }

    /**
     * @pre this!=null
     * @post f != null
     * @invariant metoda
     * @param f
     * @param date
     * @throws IOException
     */
    public void reportProductsDate(FileWriter f, String date) throws IOException {
        assert this != null;
        for (Order o : this.orders.keySet()) {
            if (o.getOrderDate().split(" ")[0].equals(date)) {
                for (MenuItem mi : this.orders.get(o)) {
                    if (mi instanceof BaseProduct) {
                        f.write("Base product: " + ((BaseProduct) mi).getTitle() + "\n");
                    } else if (mi instanceof CompositeProduct) {
                        f.write("Composite product: ");
                        for (BaseProduct b : ((CompositeProduct) mi).getAllBaseProducts())
                            f.write(b.getTitle() + " ");
                        f.write("\n");
                    }
                }
            }
        }
        assert f != null;
        assert invariant();
    }

    /**
     * @pre this!=null
     * @post !date.equals("")
     * @invariant metoda
     * @param date
     * @param startHour
     * @param endHour
     * @param reportName
     * @param nrOfTimesProducts
     * @param nrOfTimesClient
     * @param orderValue
     * @throws IOException
     */
    public void generateReport(String date, String startHour, String endHour, String reportName, int nrOfTimesProducts, int nrOfTimesClient, int orderValue) throws IOException {
        assert this != null;

        FileWriter f = new FileWriter("C:\\Users\\user\\Desktop\\SEM 2 MonkaS\\TP\\PT2022_30223_Andrei-Florin_Vasilache_Assignment_4\\src\\main\\java\\reports\\" + reportName + ".txt");
        f.write("Start hour: " + startHour + "\nEnd hour: " + endHour + "\nBase number of times: " + nrOfTimesProducts + "\nBase number of times client: " + nrOfTimesClient + "\nOrder value: " + orderValue + "\n\n");

        f.write("Time interval for orders:\n");
        reportTimeInterval(f, startHour, endHour);

        f.write("\nProducts ordered more than a specific number of times:\n");
        reportProducts(f, nrOfTimesProducts);

        f.write("\nClients who ordered more: \n");
        reportClient(f, nrOfTimesClient, orderValue);

        f.write("\nProducts ordered on the " + date + ":\n");
        reportProductsDate(f, date);

        f.close();
        assert !date.equals("");
        assert invariant();
    }

    public void generateReport2(String reportName, int start, int end, String orderDay, int nrProducts, int nrClientOrders, int minOrderValue) {
        Map<Order,ArrayList<MenuItem>> filteredOrders = filterOrders(start, end, orderDay);
        Pair<Integer, List<Pair<String, Integer>>> countProductsPair = countProducts(filteredOrders, nrProducts);
        List<String> filteredClients = filterClients(filteredOrders, nrClientOrders, minOrderValue);
        try {
            FileWriter f = new FileWriter("C:\\Users\\user\\Desktop\\SEM 2 MonkaS\\TP\\PT2022_30223_Andrei-Florin_Vasilache_Assignment_4\\src\\main\\java\\reports\\" + reportName + ".txt");
            f.write("Start hour: " + start + "\nEnd hour: " + end + "\nOrder day: " + orderDay +"\nSpecified number of products: " + nrProducts + "\nSpecified number of client orders: " + nrClientOrders + "\nOrder value: " + minOrderValue + "\n\n");
            f.write("Number of products bought more than the specified number: " + countProductsPair.getKey()+ "\n");
            for(Pair<String, Integer> product : countProductsPair.getValue()){
                f.write("Product name: " + product.getKey() + "\nProduct count: " + product.getValue() + "\n\n");
            }
            f.write("\nClients who ordered more than a specified number and with an order value bigger than a specified number:\n");
            for(String client: filteredClients){
                f.write(client + "\n");
            }
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Pair<Integer, List<Pair<String, Integer>>> countProducts(Map<Order,ArrayList<MenuItem>> filteredOrders, int nrProducts){
        int count = 0;
        List<Pair<String, Integer>> output = new ArrayList<>();
        Map<String, Integer> menuItemIntegerMap = new HashMap<>();
        for(Map.Entry<Order, ArrayList<MenuItem>> entry:filteredOrders.entrySet()){
            for(MenuItem menuItem : entry.getValue()){
                String name;
                if( menuItem instanceof BaseProduct){
                    name = ((BaseProduct) menuItem).getTitle();
                }else {
                    name = ((CompositeProduct) menuItem).getName();
                }
                if(menuItemIntegerMap.containsKey(name)){
                    menuItemIntegerMap.put(name, menuItemIntegerMap.get(name)+1);
                }else{
                    menuItemIntegerMap.put(name, 1);
                }
            }
        }
        for(Map.Entry<String, Integer> entry:menuItemIntegerMap.entrySet()){
            output.add(new ImmutablePair<String, Integer>(entry.getKey(), entry.getValue()));
            if(entry.getValue() >= nrProducts){
                count++;
            }
        }
        return new ImmutablePair<>(count, output);
    }
    public List<String> filterClients(Map<Order,ArrayList<MenuItem>> filteredOrders, int nrClientOrders, int minOrderValue){
        List<String> output = new ArrayList<>();
        Map<Integer, Integer> clientOrdersCounts = new HashMap<>();
        for(Map.Entry<Order, ArrayList<MenuItem>> entry:filteredOrders.entrySet()){
            int sum = 0;
            for (MenuItem mi : entry.getValue()) {
                if (mi instanceof BaseProduct) {
                    sum += ((BaseProduct) mi).getPrice();
                }
            }
            if(sum >= minOrderValue){
                if(clientOrdersCounts.containsKey(entry.getKey().getClientID())){
                    clientOrdersCounts.put(entry.getKey().getClientID(), clientOrdersCounts.get(entry.getKey().getClientID()) +1);
                }else{
                    clientOrdersCounts.put(entry.getKey().getClientID(), 1);
                }
            }
        }
        for(Map.Entry<Integer, Integer> entry:clientOrdersCounts.entrySet()){
            if(entry.getValue() >= nrClientOrders){
                output.add(clients.get(entry.getKey()-1).getNume());
            }
        }
        return output;
    }
    public Map<Order, ArrayList<MenuItem>> filterOrders(int start, int end, String orderDay) {
       Map<Order,ArrayList<MenuItem>> output = new HashMap<>();
       for(Map.Entry<Order, ArrayList<MenuItem>> entry:orders.entrySet()){
           try {
               Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(entry.getKey().getOrderDate());
               DateFormat formatter = new SimpleDateFormat("EEEE");
               String day = formatter.format(date);
               Calendar c = GregorianCalendar.getInstance();
               c.setTime(date);
               int time = c.get(Calendar.HOUR_OF_DAY);
               System.out.println(day);
               if(day.equals(orderDay)){
                    if(time >= start && time <= end){
                        output.put(entry.getKey(), entry.getValue());
                    }
               }
           } catch (ParseException e) {
               e.printStackTrace();
           }
       }
       return output;
    }
    /**
     * @pre this!=null
     * @post this.allProducts.size>0
     * @invariant metoda
     * @return
     */
    public String[] getAllProducts() {
        assert this != null;
        String[] all = new String[this.allProducts.size()];
        for (int i = 0; i < this.allProducts.size(); i++) {
            MenuItem mi = this.allProducts.get(i);
            if (mi instanceof BaseProduct) {
                all[i] = ((BaseProduct) mi).getTitle() + "," + ((BaseProduct) mi).getRating() + "," + ((BaseProduct) mi).getCalories() + "," + ((BaseProduct) mi).getProtein() + "," + ((BaseProduct) mi).getFat() + "," + ((BaseProduct) mi).getSodium() + "," + ((BaseProduct) mi).getPrice();
            } else if (mi instanceof CompositeProduct) {
                all[i] = ((CompositeProduct) mi).getName() + "; ";
                for (BaseProduct b : ((CompositeProduct) mi).getAllBaseProducts())
                    all[i] = all[i] + b.getTitle() + "; ";
            }
        }
        assert this.allProducts.size() > 0;
        assert invariant();
        return all;
    }

    /**
     * @pre this!=null
     * @post rating >= -1
     * @invariant metoda
     * @param name
     * @param rating
     * @param calories
     * @param protein
     * @param fat
     * @param sodium
     * @param price
     * @return
     */
    public String[] getSearchedProducts(String name, float rating, int calories, int protein, int fat, int sodium, int price) {
        assert this != null;
        ArrayList<MenuItem> newAllProducts = new ArrayList<>(this.allProducts);
        for (int i = 0; i < newAllProducts.size(); i++) {
            if (newAllProducts.get(i) instanceof CompositeProduct) {
                newAllProducts.remove(newAllProducts.get(i));
                i--;
            }
        }

        for (int i = 0; i < newAllProducts.size(); i++) {
            if (newAllProducts.get(i) instanceof BaseProduct) {
                if (!name.equals("") && !((BaseProduct) newAllProducts.get(i)).getTitle().equals(name + " ")) {
                    newAllProducts.remove(newAllProducts.get(i));
                    i--;
                }
            }
        }

        for (int i = 0; i < newAllProducts.size(); i++) {
            if (newAllProducts.get(i) instanceof BaseProduct) {
                if (rating >= 0 && ((BaseProduct) newAllProducts.get(i)).getRating() != rating) {
                    newAllProducts.remove(newAllProducts.get(i));
                    i--;
                }
            }
        }
        for (int i = 0; i < newAllProducts.size(); i++) {
            if (newAllProducts.get(i) instanceof BaseProduct) {
                if (calories >= 0 && ((BaseProduct) newAllProducts.get(i)).getCalories() != calories) {
                    newAllProducts.remove(newAllProducts.get(i));
                    i--;
                }
            }
        }
        for (int i = 0; i < newAllProducts.size(); i++) {
            if (newAllProducts.get(i) instanceof BaseProduct) {
                if (protein >= 0 && ((BaseProduct) newAllProducts.get(i)).getProtein() != protein) {
                    newAllProducts.remove(newAllProducts.get(i));
                    i--;
                }
            }
        }
        for (int i = 0; i < newAllProducts.size(); i++) {
            if (newAllProducts.get(i) instanceof BaseProduct) {
                if (fat >= 0 && ((BaseProduct) newAllProducts.get(i)).getFat() != fat) {
                    newAllProducts.remove(newAllProducts.get(i));
                    i--;
                }
            }
        }
        for (int i = 0; i < newAllProducts.size(); i++) {
            if (newAllProducts.get(i) instanceof BaseProduct) {
                if (sodium >= 0 && ((BaseProduct) newAllProducts.get(i)).getSodium() != sodium) {
                    newAllProducts.remove(newAllProducts.get(i));
                    i--;
                }
            }
        }
        for (int i = 0; i < newAllProducts.size(); i++) {
            if (newAllProducts.get(i) instanceof BaseProduct) {
                if (price >= 0 && ((BaseProduct) newAllProducts.get(i)).getPrice() != price) {
                    newAllProducts.remove(newAllProducts.get(i));
                    i--;
                }
            }
        }
        String[] all = new String[newAllProducts.size()];
        for (int i = 0; i < newAllProducts.size(); i++) {
            MenuItem mi = newAllProducts.get(i);
            if (mi instanceof BaseProduct) {
                all[i] = ((BaseProduct) mi).getTitle() + "," + ((BaseProduct) mi).getRating() + "," + ((BaseProduct) mi).getCalories() + "," + ((BaseProduct) mi).getProtein() + "," + ((BaseProduct) mi).getFat() + "," + ((BaseProduct) mi).getSodium() + "," + ((BaseProduct) mi).getPrice();
            }
        }
        assert rating >= -1;
        assert invariant();
        return all;
    }

    /**
     * @pre this!=null
     * @post b != null
     * @invariant metoda
     * @param b
     */
    public void modifyProduct(BaseProduct b) {
        assert this != null;
        for (MenuItem mi : this.allProducts) {
            if (mi instanceof BaseProduct) {
                if (b.getTitle().equals(((BaseProduct) mi).getTitle())) {
                    ((BaseProduct) mi).setCalories(b.getCalories());
                    ((BaseProduct) mi).setFat(b.getFat());
                    ((BaseProduct) mi).setPrice(b.getPrice());
                    ((BaseProduct) mi).setProtein(b.getProtein());
                    ((BaseProduct) mi).setRating(b.getRating());
                    ((BaseProduct) mi).setSodium(b.getSodium());
                    //break;
                }
            }

        }
        assert b != null;
        assert invariant();
    }

    /**
     * @pre this!=null
     * @post this!= null
     * @invariant metoda
     * @throws IOException
     */
    public void serializationWrite() throws IOException {
        assert this != null;
        FileOutputStream f = new FileOutputStream("C:\\Users\\user\\Desktop\\SEM 2 MonkaS\\TP\\PT2022_30223_Andrei-Florin_Vasilache_Assignment_4\\src\\main\\java\\products.ser");
        ObjectOutputStream out = new ObjectOutputStream(f);
        for (MenuItem mi : this.allProducts) {
            out.writeObject(mi);
        }
        out.close();
        f.close();
        assert invariant();
    }

    /**
     * @pre this!=null
     * @post this!= null
     * @invariant metoda
     * @throws IOException
     */
    public void serializationRead() throws IOException {
        assert this != null;
        FileInputStream f = new FileInputStream("C:\\Users\\user\\Desktop\\SEM 2 MonkaS\\TP\\PT2022_30223_Andrei-Florin_Vasilache_Assignment_4\\src\\main\\java\\products.ser");
        ObjectInputStream in = new ObjectInputStream(f);
        while (true) {
            try {
                this.allProducts.add(((MenuItem) in.readObject()));
            } catch (Exception e) {
                break;
            }
        }
        in.close();
        f.close();
        assert invariant();
    }

    public void serializationWriteOrders() throws IOException {
        assert this != null;
        FileOutputStream f = new FileOutputStream("C:\\Users\\user\\Desktop\\SEM 2 MonkaS\\TP\\PT2022_30223_Andrei-Florin_Vasilache_Assignment_4\\src\\main\\java\\database\\orders.ser");
        ObjectOutputStream out = new ObjectOutputStream(f);
        for (Map.Entry<Order, ArrayList<MenuItem>>entry: this.orders.entrySet()) {
            out.writeObject(entry.getKey());
            out.writeInt(entry.getValue().size());
            for(MenuItem mi : entry.getValue()) {
                out.writeObject(mi);
            }
        }
        out.close();
        f.close();
        assert invariant();
    }

    public void serializationReadOrders() throws IOException {
        assert this != null;
        FileInputStream f = new FileInputStream("C:\\Users\\user\\Desktop\\SEM 2 MonkaS\\TP\\PT2022_30223_Andrei-Florin_Vasilache_Assignment_4\\src\\main\\java\\database\\orders.ser");
        ObjectInputStream in = new ObjectInputStream(f);
        while (true) {
            try {
                Order o = (Order) in.readObject();
                ArrayList<MenuItem> menuItems = new ArrayList<>();
                int size = in.readInt();
                for(int i = 0;i < size; i++){
                    menuItems.add((MenuItem) in.readObject());
                }
                this.orders.put(o, menuItems);
            } catch (Exception e) {
                break;
            }
        }
        in.close();
        f.close();
        assert invariant();
    }

    /**
     * @pre this!=null
     * @post client != null
     * @invariant metoda
     * @param client
     * @throws IOException
     */
    public void generateBill(Client client) throws IOException {
        assert this != null;
        for (Order o : this.orders.keySet()) {
            if (o.getClientID() == client.getId()) {
                System.out.println(client.getNume());
                FileWriter f = new FileWriter("C:\\Users\\user\\Desktop\\SEM 2 MonkaS\\TP\\PT2022_30223_Andrei-Florin_Vasilache_Assignment_4\\src\\main\\java\\bills\\" + client.getNume() + ".txt");
                f.write(client.getNume() + " ordered:\n");
                int sum = 0;
                for (MenuItem mi : this.orders.get(o)) {
                    if (mi instanceof BaseProduct) {
                        f.write(((BaseProduct) mi).getTitle() + " " + ((BaseProduct) mi).getPrice() + "\n");
                        sum += ((BaseProduct) mi).getPrice();
                    }
                }
                f.write("\nTOTAL PRICE: " + sum);
                f.close();
            }
        }
        assert client != null;
        assert invariant();
    }
}
