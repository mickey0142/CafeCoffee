package Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable{
    private String customerName;
    private String status;
    private int sumPrice;
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
        boolean check = false;
        for (int i = 0; i < beverages.size(); i++)
        {
            if (beverage.getName().equals(beverages.get(i).getName()) && beverage.getSize().equals(beverages.get(i).getSize()) && beverage.getMoreDetail().equals(beverages.get(i).getMoreDetail()))
            {
                beverages.get(i).setAmount(beverages.get(i).getAmount() + beverage.getAmount());
                check = true;
                break;
            }
        }
        if (!check)
        {
            this.beverages.add(beverage);
        }
    }

    public void removeBeverage(int position)
    {
        this.beverages.remove(position);
    }

    public int getSumPrice() {
        calculateSumPrice();
        return sumPrice;
    }

    public void calculateSumPrice() {
        int sumPrice = 0;
        for(int i = 0; i < beverages.size(); i++)
        {
            sumPrice += beverages.get(i).getPrice("total");
        }
        this.sumPrice = sumPrice;
    }
}
