package Model;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;

public class Beverage implements Serializable{
    private String name;
    private int price;
    private int amount;
    private String size;
    private String moreDetail;
    private String type;

    public static int cappuccinoPrice;
    public static int espressoPrice;
    public static int americanoPrice;
    public static int macchiatoPrice;
    public static int lattePrice;
    public static int mochaPrice;
    public static int cocoaPrice;

    public Beverage()
    {

    }

    public Beverage(String type)
    {
        name = type;
        if (type.equals("Cappuccino"))
        {
            price = cappuccinoPrice;
        }
        else if (type.equals("Espresso"))
        {
            price = espressoPrice;
        }
        else if (type.equals("Americano"))
        {
            price = americanoPrice;
        }
        else if (type.equals("Macchiato"))
        {
            price = macchiatoPrice;
        }
        else if (type.equals("Latte"))
        {
            price = lattePrice;
        }
        else if (type.equals("Mocha"))
        {
            price = mochaPrice;
        }
        else if (type.equals("Cocoa"))
        {
            price = cocoaPrice;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        int finalPrice = this.price;
        if (this.size.equals("big")) finalPrice += 5;
        if (this.type.equals("cold")) finalPrice += 5;
        else if (this.type.equals("frappe")) finalPrice += 10;
        return finalPrice;
    }

    public int getPrice(String type)
    {
        int finalPrice = this.price;
        if (this.size.equals("big")) finalPrice += 5;
        if (this.type.equals("cold")) finalPrice += 5;
        else if (this.type.equals("frappe")) finalPrice += 10;
        if (type.equals("total"))
        {
            return finalPrice * this.amount;
        }
        else
        {
            return finalPrice;
        }
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMoreDetail() {
        return moreDetail;
    }

    public void setMoreDetail(String moreDetail) {
        this.moreDetail = moreDetail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void increaseAmount()
    {
        this.amount += 1;
    }

    public void decreaseAmount()
    {
        if (amount > 1)
        {
            this.amount -= 1;
        }
    }

    public static void setMenuPrice(HashMap<String, Integer> menuPrice)
    {
        cappuccinoPrice = menuPrice.get("Cappuccino");
        espressoPrice = menuPrice.get("Espresso");
        americanoPrice = menuPrice.get("Americano");
        macchiatoPrice = menuPrice.get("Macchiato");
        lattePrice = menuPrice.get("Latte");
        mochaPrice = menuPrice.get("Mocha");
        cocoaPrice = menuPrice.get("Cocoa");
    }
}
