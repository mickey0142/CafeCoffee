package Model;

public class Condiment{

    private String name;
    private double price;
    private int amount;

    public Condiment()
    {

    }

    public Condiment(String type)
    {
        name = type;
        if (type.equals("Whip"))
        {
            price = 1;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getSumPrice()
    {
        return price * amount;
    }
}
