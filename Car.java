public class Car {
    private final String id;
    private String brand;
    private String model;
    private int year;
    private String fuelType;
    private String transmission;
    private String color;
    private double price;
    private int stock;
    private int reorderLevel;

    public Car(String id, String brand, String model, int year, String fuelType,
               String transmission, String color, double price, int stock, int reorderLevel) {
        this.id = id; this.brand = brand; this.model = model; this.year = year;
        this.fuelType = fuelType; this.transmission = transmission; this.color = color;
        this.price = price; this.stock = stock; this.reorderLevel = reorderLevel;
    }
    public String getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public String getFuelType() { return fuelType; }
    public String getTransmission() { return transmission; }
    public String getColor() { return color; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public int getReorderLevel() { return reorderLevel; }
    public String getDisplayName() { return brand + " " + model + " (" + year + ")"; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setYear(int year) { this.year = year; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    public void setColor(String color) { this.color = color; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
    public String toDataLine() {
        return DataStore.pack(id, brand, model, String.valueOf(year), fuelType,
                transmission, color, String.valueOf(price), String.valueOf(stock),
                String.valueOf(reorderLevel));
    }
    public static Car fromDataLine(String line) {
        String[] p = DataStore.unpack(line, 10);
        if (p == null) return null;
        try {
            return new Car(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4], p[5],
                    p[6], Double.parseDouble(p[7]), Integer.parseInt(p[8]), Integer.parseInt(p[9]));
        } catch (RuntimeException e) { return null; }
    }
}
