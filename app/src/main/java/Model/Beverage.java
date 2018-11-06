package Model;

import java.util.ArrayList;

public class Beverage {
    private String name;
    private double price;
    private ArrayList<Condiment> condimentList = new ArrayList<>();;

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

    public double getPrice() {
        double sumPrice = 0;
        for (int i = 0; i < condimentList.size(); i++)
        {
            sumPrice += condimentList.get(i).getPrice();
        }
        return sumPrice + price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList<Condiment> getCondimentList() {
        return condimentList;
    }

    public void setCondimentList(ArrayList<Condiment> condiment) {
        this.condimentList = condiment;
    }

    public void addCondiment(Condiment condiment)
    {
        this.condimentList.add(condiment);
    }

    public void removeCondiment(int position)
    {
        this.condimentList.remove(position);
    }
}
