package business;

import java.util.ArrayList;

public interface IDeliveryServiceProcessing {

    void importProduct(MenuItem p);
    void addOrder(Order o, ArrayList<MenuItem> mi);
    int convertToMinutes(String hour);
}
