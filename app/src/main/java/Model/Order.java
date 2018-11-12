package Model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Order implements Serializable{
    private String customerName;
    private String status;
    private int sumPrice;
    private ArrayList<Beverage> beverages = new ArrayList<>();
    private String orderTime;
    private String shopName;

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

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderTime = dateFormat.format(date);
    }

    public String orderTimeReverse()
    {
        String temp = orderTime.substring(8, 10);
        temp += orderTime.substring(4, 8);
        temp += orderTime.substring(0, 4);
        temp += orderTime.substring(10, 19);
        return temp;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
