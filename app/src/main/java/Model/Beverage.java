package Model;

import java.io.Serializable;

public class Beverage implements Serializable{
    private String name;
    private int price;
    private int amount;
    private String size;
    private String moreDetail;
    private String type;

    public Beverage()
    {

    }

    public Beverage(String type)
    {
        name = type;
        if (type.equals("Cappuccino"))
        {
            price = 2;
        }
        else if (type.equals("Latte"))
        {
            price = 3;
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
}
