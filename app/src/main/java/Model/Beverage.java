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
        this.type = "normal";
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
        return this.price;
    }

    public int getPrice(String type)
    {
        if (type.equals("total"))
        {
            return this.price * this.amount;
        }
        else
        {
            return this.price;
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
        calculatePrice();
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
        calculatePrice();
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

    public static int getCoffeePrice(String coffee)
    {
        if (coffee.equals("Cappuccino"))
        {
            return Beverage.cappuccinoPrice;
        }
        else if (coffee.equals("Espresso"))
        {
            return Beverage.espressoPrice;
        }
        else if (coffee.equals("Americano"))
        {
            return Beverage.americanoPrice;
        }
        else if (coffee.equals("Macchiato"))
        {
            return Beverage.macchiatoPrice;
        }
        else if (coffee.equals("Latte"))
        {
            return Beverage.lattePrice;
        }
        else if (coffee.equals("Mocha"))
        {
            return Beverage.mochaPrice;
        }
        else if (coffee.equals("Cocoa"))
        {
            return Beverage.cocoaPrice;
        }
        else
        {
            return 0;
        }
    }

    void calculatePrice()
    {
        price = getCoffeePrice(name);
        if (size == null)
        {
            return;
        }
        else if (size.equals("big"))
        {
            price += 5;
        }
        if (type == null)
        {
            return;
        }
        else if (type.equals("cold"))
        {
            price += 5;
        }
        else if (type.equals("frappe"))
        {
            price += 10;
        }
    }
}
