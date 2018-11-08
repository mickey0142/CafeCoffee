package Model;

import java.io.Serializable;
import java.util.HashMap;

public class Shop implements Serializable{

    private String ShopName;
    private String owner;
    private String location;
    private String pictureName;
    private HashMap<String, Integer> menuPrice;

    public Shop()
    {

    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public HashMap<String, Integer> getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(HashMap<String, Integer> menuPrice) {
        this.menuPrice = menuPrice;
    }

    public void setDefaultMenuPrice()
    {
        this.menuPrice.put("Cappuccino", 20);
        this.menuPrice.put("Espresso", 20);
        this.menuPrice.put("Americano", 20);
        this.menuPrice.put("Macchiato", 20);
        this.menuPrice.put("Latte", 20);
        this.menuPrice.put("Mocha", 20);
        this.menuPrice.put("Cocoa", 20);
    }
}
