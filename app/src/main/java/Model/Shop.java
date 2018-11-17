package Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.HashMap;

public class Shop implements Serializable{

    private String ShopName;
    private String owner;
    private String shopDescription;
    private String pictureName;
    private HashMap<String, Integer> menuPrice;
    private HashMap<String, Double> shopPosition;

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

    public String getShopDescription() {
        return shopDescription;
    }

    public void setShopDescription(String shopDescription) {
        this.shopDescription = shopDescription;
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

    public HashMap<String, Double> getShopPosition() {
        return shopPosition;
    }

    public void setShopPosition(HashMap<String, Double> shopPosition) {
        this.shopPosition = shopPosition;
    }

    public void setDefaultMenuPrice()
    {
        menuPrice = new HashMap<>();
        this.menuPrice.put("Cappuccino", 20);
        this.menuPrice.put("Espresso", 20);
        this.menuPrice.put("Americano", 20);
        this.menuPrice.put("Macchiato", 20);
        this.menuPrice.put("Latte", 20);
        this.menuPrice.put("Mocha", 20);
        this.menuPrice.put("Cocoa", 20);
    }
}
