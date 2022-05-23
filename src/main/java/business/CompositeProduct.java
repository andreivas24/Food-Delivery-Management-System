package business;

import java.io.Serializable;
import java.util.ArrayList;

public class CompositeProduct extends MenuItem implements Serializable {
    private String name;
    private ArrayList<BaseProduct> baseProducts;
    private int totalPrice;

    public CompositeProduct(String name){
        this.name = name;
        this.baseProducts = new ArrayList<>();
        this.totalPrice = 0;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addBaseProduct(BaseProduct b){
        this.baseProducts.add(b);
        this.totalPrice += b.getPrice();
    }

    public ArrayList<BaseProduct> getBaseProducts() {
        return baseProducts;
    }

    public void setBaseProducts(ArrayList<BaseProduct> baseProducts) {
        this.baseProducts = baseProducts;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ArrayList<BaseProduct> getAllBaseProducts() {return this.baseProducts;}

    public int getTotalPrice() {return this.totalPrice;}
}
