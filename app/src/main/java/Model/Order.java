package Model;

import java.util.ArrayList;

public class Order {
    private String customerName;
    private String status;
    private double sumPrice;
    private ArrayList<Beverage> beverages = new ArrayList<>();

    public Order()
    {

    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Beverage> getBeverages() {
        return beverages;
    }

    public void setBeverages(ArrayList<Beverage> beverages) {
        this.beverages = beverages;
    }

    public void addBeverage(Beverage beverage){
        this.beverages.add(beverage);
    }

    public void removeBeverage(int position)
    {
        this.beverages.remove(position);
    }

    public double getSumPrice() {
        calculateSumPrice();
        return sumPrice;
    }

    public void calculateSumPrice() {
        double sumPrice = 0;
        for(int i = 0; i < beverages.size(); i++)
        {
            sumPrice += beverages.get(i).getPrice();
        }
        this.sumPrice = sumPrice;
    }
}
