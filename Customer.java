public class Customer {
    private final String id;
    private final String name;
    private final String phone;
    private final String email;
    private final String city;

    public Customer(String id, String name, String phone, String email, String city) {
        this.id = id; this.name = name; this.phone = phone; this.email = email; this.city = city;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getCity() { return city; }
    public String toDataLine() { return DataStore.pack(id, name, phone, email, city); }
    public static Customer fromDataLine(String line) {
        String[] p = DataStore.unpack(line, 5);
        return p == null ? null : new Customer(p[0], p[1], p[2], p[3], p[4]);
    }
}
