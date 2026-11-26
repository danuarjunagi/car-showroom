import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CarShowroomSystem {
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "Admin@123";
    private static final String SALES_USER = "sales";
    private static final String SALES_PASSWORD = "Sales@123";
    private static final double GST_RATE = 0.18;
    private static final double MAX_DISCOUNT_PERCENT = 20.0;

    private final Scanner scanner;
    private final Map<String, Car> cars = new LinkedHashMap<>();
    private final Map<String, Customer> customers = new LinkedHashMap<>();
    private final List<Sale> sales = new ArrayList<>();
    private final List<TestDrive> testDrives = new ArrayList<>();
    private final List<ServiceBooking> serviceBookings = new ArrayList<>();
    private final ReportService reportService = new ReportService();

    public CarShowroomSystem(Scanner scanner) {
        this.scanner = scanner;
        loadAll();
        if (cars.isEmpty()) seedCars();
    }

    public void start() {
        System.out.println("===============================================================");
        System.out.println("          CAR SHOWROOM MANAGEMENT SYSTEM");
        System.out.println("===============================================================");
        boolean running = true;
        while (running) {
            System.out.println("\nMAIN MENU");
            System.out.println("1. Admin Login");
            System.out.println("2. Sales Executive Login");
            System.out.println("3. Browse Available Cars");
            System.out.println("4. Exit");
            int choice = readInt("Choose an option: ", 1, 4);
            switch (choice) {
                case 1: adminLogin(); break;
                case 2: salesLogin(); break;
                case 3: displayCars(cars.values().stream().collect(Collectors.toList())); break;
                case 4: saveAll(); running = false; break;
                default: break;
            }
        }
        reportService.shutdown();
        System.out.println("Thank you for using Car Showroom Management System.");
    }

    private void adminLogin() {
        String username = readText("Admin username: ");
        String password = readText("Admin password: ");
        if (ADMIN_USER.equals(username) && ADMIN_PASSWORD.equals(password)) adminMenu();
        else System.out.println("Invalid admin credentials.");
    }

    private void salesLogin() {
        String username = readText("Sales executive username: ");
        String password = readText("Password: ");
        if (SALES_USER.equals(username) && SALES_PASSWORD.equals(password)) salesMenu(username);
        else System.out.println("Invalid sales executive credentials.");
    }

    private void adminMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n========== ADMIN DASHBOARD ==========");
            System.out.println("1. Add new car model");
            System.out.println("2. Update car details");
            System.out.println("3. Add or adjust stock");
            System.out.println("4. Remove car model");
            System.out.println("5. View inventory");
            System.out.println("6. View low-stock alerts");
            System.out.println("7. View customers");
            System.out.println("8. Manage test-drive bookings");
            System.out.println("9. Manage service bookings");
            System.out.println("10. Sales and business reports");
            System.out.println("11. Back to main menu");
            int choice = readInt("Choose an option: ", 1, 11);
            switch (choice) {
                case 1: addCar(); break;
                case 2: updateCar(); break;
                case 3: adjustStock(); break;
                case 4: removeCar(); break;
                case 5: displayCars(new ArrayList<Car>(cars.values())); break;
                case 6: lowStockReport(); break;
                case 7: displayCustomers(); break;
                case 8: manageTestDrives(); break;
                case 9: manageServiceBookings(); break;
                case 10: reportsMenu(); break;
                case 11: back = true; break;
                default: break;
            }
        }
    }

    private void salesMenu(String username) {
        boolean back = false;
        while (!back) {
            System.out.println("\n========== SALES EXECUTIVE DASHBOARD ==========");
            System.out.println("1. Browse and search cars");
            System.out.println("2. Register customer");
            System.out.println("3. Book test drive");
            System.out.println("4. Sell car and generate invoice");
            System.out.println("5. Book vehicle service");
            System.out.println("6. View customer history");
            System.out.println("7. View sales records");
            System.out.println("8. Back to main menu");
            int choice = readInt("Choose an option: ", 1, 8);
            switch (choice) {
                case 1: searchCars(); break;
                case 2: registerCustomer(); break;
                case 3: bookTestDrive(username); break;
                case 4: sellCar(username); break;
                case 5: bookService(); break;
                case 6: customerHistory(); break;
                case 7: displaySales(); break;
                case 8: back = true; break;
                default: break;
            }
        }
    }

    // --------------------------- INVENTORY ---------------------------
    private void addCar() {
        String id = readText("Car ID (unique): ").toUpperCase();
        if (cars.containsKey(id)) { System.out.println("Car ID already exists."); return; }
        String brand = readText("Brand: ");
        String model = readText("Model: ");
        int year = readInt("Manufacturing year (1980-2100): ", 1980, 2100);
        String fuel = readChoice("Fuel type", "PETROL", "DIESEL", "CNG", "HYBRID", "ELECTRIC");
        String transmission = readChoice("Transmission", "MANUAL", "AUTOMATIC");
        String color = readText("Colour: ");
        double price = readDouble("Ex-showroom price (INR): ", 1, 100000000);
        int stock = readInt("Initial stock quantity: ", 0, 100000);
        int reorder = readInt("Low-stock alert threshold: ", 0, 100000);
        cars.put(id, new Car(id, brand, model, year, fuel, transmission, color, price, stock, reorder));
        saveAll();
        System.out.println("Car model added successfully.");
    }

    private void updateCar() {
        String id = readText("Enter car ID to update: ").toUpperCase();
        Car car = cars.get(id);
        if (car == null) { System.out.println("Car not found."); return; }
        System.out.println("Press Enter to keep the existing value.");
        String value = readOptional("Brand [" + car.getBrand() + "]: "); if (!value.isEmpty()) car.setBrand(value);
        value = readOptional("Model [" + car.getModel() + "]: "); if (!value.isEmpty()) car.setModel(value);
        value = readOptional("Year [" + car.getYear() + "]: ");
        if (!value.isEmpty()) { try { int n = Integer.parseInt(value); if (n >= 1980 && n <= 2100) car.setYear(n); else System.out.println("Invalid year; kept old value."); } catch (NumberFormatException e) { System.out.println("Invalid year; kept old value."); } }
        value = readOptional("Fuel type [" + car.getFuelType() + "]: "); if (!value.isEmpty()) car.setFuelType(value.toUpperCase());
        value = readOptional("Transmission [" + car.getTransmission() + "]: "); if (!value.isEmpty()) car.setTransmission(value.toUpperCase());
        value = readOptional("Colour [" + car.getColor() + "]: "); if (!value.isEmpty()) car.setColor(value);
        value = readOptional("Price [" + car.getPrice() + "]: ");
        if (!value.isEmpty()) { try { double n = Double.parseDouble(value); if (n > 0) car.setPrice(n); else System.out.println("Invalid price; kept old value."); } catch (NumberFormatException e) { System.out.println("Invalid price; kept old value."); } }
        saveAll();
        System.out.println("Car details updated.");
    }

    private void adjustStock() {
        String id = readText("Car ID: ").toUpperCase();
        Car car = cars.get(id);
        if (car == null) { System.out.println("Car not found."); return; }
        System.out.println("Current stock: " + car.getStock());
        int newStock = readInt("Enter new stock quantity: ", 0, 100000);
        car.setStock(newStock); saveAll();
        System.out.println("Stock updated.");
    }

    private void removeCar() {
        String id = readText("Car ID to remove: ").toUpperCase();
        Car car = cars.get(id);
        if (car == null) { System.out.println("Car not found."); return; }
        if (car.getStock() > 0) {
            System.out.println("Cannot remove a model with stock. Set stock to zero first."); return;
        }
        boolean hasSales = sales.stream().anyMatch(s -> s.getCarId().equals(id));
        if (hasSales) { System.out.println("This car has sales history; it cannot be deleted. Keep it with zero stock."); return; }
        cars.remove(id); saveAll(); System.out.println("Car model removed.");
    }

    private void displayCars(List<Car> list) {
        if (list.isEmpty()) { System.out.println("No car records found."); return; }
        System.out.println("\n-------------------------------- CAR INVENTORY --------------------------------");
        System.out.printf("%-8s %-12s %-16s %-6s %-10s %-11s %-12s %-8s %-14s%n",
                "CAR ID", "BRAND", "MODEL", "YEAR", "FUEL", "GEAR", "COLOUR", "STOCK", "PRICE");
        for (Car c : list) {
            System.out.printf("%-8s %-12s %-16s %-6d %-10s %-11s %-12s %-8d %-14s%n",
                    c.getId(), c.getBrand(), c.getModel(), c.getYear(), c.getFuelType(),
                    c.getTransmission(), c.getColor(), c.getStock(), Money.format(c.getPrice()));
        }
    }

    private void searchCars() {
        System.out.println("\nSEARCH CARS: 1. Brand/model keyword  2. Fuel type  3. Maximum price  4. Sort by price");
        int option = readInt("Choose: ", 1, 4);
        List<Car> result = new ArrayList<>(cars.values());
        if (option == 1) {
            String key = readText("Enter brand or model keyword: ").toLowerCase();
            result = result.stream().filter(c -> c.getBrand().toLowerCase().contains(key) || c.getModel().toLowerCase().contains(key)).collect(Collectors.toList());
        } else if (option == 2) {
            String fuel = readText("Fuel type: ").toUpperCase();
            result = result.stream().filter(c -> c.getFuelType().equalsIgnoreCase(fuel)).collect(Collectors.toList());
        } else if (option == 3) {
            double max = readDouble("Maximum price: ", 1, 100000000);
            result = result.stream().filter(c -> c.getPrice() <= max).collect(Collectors.toList());
        } else {
            result.sort(Comparator.comparingDouble(Car::getPrice));
        }
        displayCars(result);
    }

    private void lowStockReport() {
        PriorityQueue<Car> low = new PriorityQueue<>(Comparator.comparingInt(Car::getStock));
        for (Car c : cars.values()) if (c.getStock() <= c.getReorderLevel()) low.offer(c);
        if (low.isEmpty()) { System.out.println("No low-stock alerts."); return; }
        System.out.println("\nLOW-STOCK ALERTS (lowest stock first)");
        while (!low.isEmpty()) {
            Car c = low.poll();
            System.out.println(c.getId() + " | " + c.getDisplayName() + " | Stock: " + c.getStock() + " | Reorder at: " + c.getReorderLevel());
        }
    }

    // --------------------------- CUSTOMERS ---------------------------
    private void registerCustomer() {
        String id = nextId("CUS", customers.size() + 1);
        String name = readText("Customer full name: ");
        String phone = readText("Phone number: ");
        if (!phone.matches("[0-9+ -]{7,16}")) { System.out.println("Invalid phone number format."); return; }
        String email = readText("Email: ");
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) { System.out.println("Invalid email address."); return; }
        String city = readText("City: ");
        Customer c = new Customer(id, name, phone, email, city);
        customers.put(id, c); saveAll();
        System.out.println("Customer registered. Customer ID: " + id);
    }

    private Customer findCustomer() {
        String id = readText("Customer ID: ").toUpperCase();
        Customer c = customers.get(id);
        if (c == null) System.out.println("Customer not found. Register the customer first.");
        return c;
    }

    private void displayCustomers() {
        if (customers.isEmpty()) { System.out.println("No customers registered."); return; }
        System.out.println("\nCUSTOMER DIRECTORY");
        for (Customer c : customers.values())
            System.out.println(c.getId() + " | " + c.getName() + " | " + c.getPhone() + " | " + c.getEmail() + " | " + c.getCity());
    }

    private void customerHistory() {
        Customer customer = findCustomer(); if (customer == null) return;
        System.out.println("\nCUSTOMER HISTORY: " + customer.getName() + " (" + customer.getId() + ")");
        for (Sale s : sales) if (s.getCustomerId().equals(customer.getId()))
            System.out.println("SALE " + s.getId() + " | " + s.getCarDescription() + " | " + Money.format(s.getTotalAmount()) + " | " + s.getDateTime());
        for (TestDrive t : testDrives) if (t.getCustomerId().equals(customer.getId()))
            System.out.println("TEST DRIVE " + t.getId() + " | Car: " + t.getCarId() + " | " + t.getDateTime() + " | " + t.getStatus());
        for (ServiceBooking b : serviceBookings) if (b.getCustomerId().equals(customer.getId()))
            System.out.println("SERVICE " + b.getId() + " | " + b.getVehicleDetails() + " | " + b.getStatus() + " | " + Money.format(b.getEstimatedCost()));
    }

    // --------------------------- TEST DRIVES ---------------------------
    private void bookTestDrive(String salesperson) {
        Customer customer = findCustomer(); if (customer == null) return;
        String carId = readText("Car ID for test drive: ").toUpperCase();
        Car car = cars.get(carId);
        if (car == null || car.getStock() <= 0) { System.out.println("Car not found or unavailable for test drive."); return; }
        String dateTime = readText("Preferred date and time (e.g. 25-11-2026 11:30 AM): ");
        boolean slotTaken = testDrives.stream().anyMatch(t -> t.getCarId().equals(carId) && t.getDateTime().equalsIgnoreCase(dateTime) && "SCHEDULED".equals(t.getStatus()));
        if (slotTaken) { System.out.println("This car already has a scheduled test drive at that time."); return; }
        String id = nextId("TD", testDrives.size() + 1);
        testDrives.add(new TestDrive(id, customer.getId(), carId, dateTime, salesperson, "SCHEDULED"));
        saveAll(); System.out.println("Test drive booked. Booking ID: " + id);
    }

    private void manageTestDrives() {
        if (testDrives.isEmpty()) { System.out.println("No test-drive bookings."); return; }
        for (TestDrive t : testDrives) System.out.println(t.getId() + " | Customer: " + t.getCustomerId() + " | Car: " + t.getCarId() + " | " + t.getDateTime() + " | " + t.getStatus());
        String id = readText("Booking ID to update (Enter 0 to return): ").toUpperCase();
        if ("0".equals(id)) return;
        TestDrive found = testDrives.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
        if (found == null) { System.out.println("Booking not found."); return; }
        String status = readChoice("New status", "SCHEDULED", "COMPLETED", "CANCELLED", "NO-SHOW");
        found.setStatus(status); saveAll(); System.out.println("Test-drive booking updated.");
    }

    // --------------------------- SALES / INVOICES ---------------------------
    private synchronized void sellCar(String salesperson) {
        Customer customer = findCustomer(); if (customer == null) return;
        String carId = readText("Car ID to sell: ").toUpperCase();
        Car car = cars.get(carId);
        if (car == null || car.getStock() <= 0) { System.out.println("Car is unavailable or out of stock."); return; }
        System.out.println("Selected: " + car.getDisplayName() + " | Price: " + Money.format(car.getPrice()));
        double discountPercent = readDouble("Discount percentage (0-" + MAX_DISCOUNT_PERCENT + "): ", 0, MAX_DISCOUNT_PERCENT);
        double discount = car.getPrice() * discountPercent / 100.0;
        double taxable = car.getPrice() - discount;
        double tax = taxable * GST_RATE;
        double total = taxable + tax;
        String payment = readChoice("Payment mode", "CASH", "UPI", "CARD", "FINANCE");
        double downPayment = 0, annualRate = 0, emi = 0; int tenure = 0;
        if ("FINANCE".equals(payment)) {
            downPayment = readDouble("Down payment: ", 0, total);
            annualRate = readDouble("Annual interest rate (%): ", 0, 40);
            tenure = readInt("Loan tenure in months: ", 1, 120);
            double principal = total - downPayment;
            emi = calculateEmi(principal, annualRate, tenure);
            System.out.println("Estimated monthly EMI: " + Money.format(emi));
        }
        String id = nextId("INV", sales.size() + 1);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        Sale sale = new Sale(id, date, customer.getId(), car.getId(), car.getDisplayName(), payment,
                salesperson, car.getPrice(), discount, tax, total, downPayment, annualRate, tenure, emi);
        car.setStock(car.getStock() - 1);
        sales.add(sale);
        saveAll();
        generateInvoice(sale, customer);
        System.out.println("Sale completed. Invoice number: " + id);
        System.out.println("Total payable: " + Money.format(total));
        System.out.println("Invoice saved in the invoices folder.");
    }

    private double calculateEmi(double principal, double annualRate, int months) {
        if (principal <= 0) return 0;
        double monthlyRate = annualRate / (12.0 * 100.0);
        if (monthlyRate == 0) return principal / months;
        double factor = Math.pow(1 + monthlyRate, months);
        return principal * monthlyRate * factor / (factor - 1);
    }

    private void generateInvoice(Sale sale, Customer customer) {
        StringBuilder b = new StringBuilder();
        b.append("====================================================\n");
        b.append("                 CAR SHOWROOM INVOICE\n");
        b.append("====================================================\n");
        b.append("Invoice No : ").append(sale.getId()).append('\n');
        b.append("Date       : ").append(sale.getDateTime()).append('\n');
        b.append("Customer   : ").append(customer.getName()).append(" (" ).append(customer.getId()).append(")\n");
        b.append("Phone      : ").append(customer.getPhone()).append('\n');
        b.append("Vehicle    : ").append(sale.getCarDescription()).append(" [").append(sale.getCarId()).append("]\n");
        b.append("----------------------------------------------------\n");
        b.append(String.format("Ex-showroom price : %s%n", Money.format(sale.getUnitPrice())));
        b.append(String.format("Discount          : %s%n", Money.format(sale.getDiscountAmount())));
        b.append(String.format("GST (18%%)         : %s%n", Money.format(sale.getTaxAmount())));
        b.append(String.format("TOTAL             : %s%n", Money.format(sale.getTotalAmount())));
        b.append("Payment mode      : ").append(sale.getPaymentMode()).append('\n');
        if ("FINANCE".equals(sale.getPaymentMode())) {
            b.append("Down payment      : ").append(Money.format(sale.getDownPayment())).append('\n');
            b.append("Annual interest   : ").append(sale.getAnnualInterestRate()).append("%\n");
            b.append("Tenure            : ").append(sale.getTenureMonths()).append(" months\n");
            b.append("Estimated EMI     : ").append(Money.format(sale.getEmi())).append('\n');
        }
        b.append("Sales executive   : ").append(sale.getSalesperson()).append('\n');
        b.append("====================================================\n");
        b.append("Thank you for choosing our showroom!\n");
        DataStore.writeInvoice(sale.getId() + ".txt", b.toString());
    }

    private void displaySales() {
        if (sales.isEmpty()) { System.out.println("No sales recorded."); return; }
        System.out.println("\nSALES REGISTER");
        for (Sale s : sales) System.out.println(s.getId() + " | " + s.getDateTime() + " | Customer: " + s.getCustomerId() +
                " | " + s.getCarDescription() + " | " + Money.format(s.getTotalAmount()) + " | " + s.getPaymentMode());
    }

    // --------------------------- AFTER-SALES SERVICE ---------------------------
    private void bookService() {
        Customer customer = findCustomer(); if (customer == null) return;
        String vehicle = readText("Vehicle registration number / model: ");
        String type = readChoice("Service type", "GENERAL", "OIL-CHANGE", "REPAIR", "INSURANCE", "WARRANTY");
        String date = readText("Preferred service date (DD-MM-YYYY): ");
        String issue = readText("Describe the issue / service request: ");
        double estimate = readDouble("Estimated service cost: ", 0, 10000000);
        String id = nextId("SRV", serviceBookings.size() + 1);
        serviceBookings.add(new ServiceBooking(id, customer.getId(), vehicle, date, issue, type, "BOOKED", estimate));
        saveAll(); System.out.println("Service booking created. Booking ID: " + id);
    }

    private void manageServiceBookings() {
        if (serviceBookings.isEmpty()) { System.out.println("No service bookings."); return; }
        for (ServiceBooking s : serviceBookings) System.out.println(s.getId() + " | " + s.getCustomerId() + " | " + s.getVehicleDetails() +
                " | " + s.getServiceType() + " | " + s.getBookingDate() + " | " + s.getStatus() + " | " + Money.format(s.getEstimatedCost()));
        String id = readText("Service booking ID to update (Enter 0 to return): ").toUpperCase();
        if ("0".equals(id)) return;
        ServiceBooking found = serviceBookings.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
        if (found == null) { System.out.println("Service booking not found."); return; }
        String status = readChoice("New status", "BOOKED", "IN-PROGRESS", "COMPLETED", "CANCELLED");
        found.setStatus(status);
        if ("COMPLETED".equals(status)) found.setEstimatedCost(readDouble("Final service amount: ", 0, 10000000));
        saveAll(); System.out.println("Service record updated.");
    }

    // --------------------------- REPORTS ---------------------------
    private void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n========== REPORTS ==========");
            System.out.println("1. Parallel business summary");
            System.out.println("2. Sales by car model");
            System.out.println("3. Top-selling models");
            System.out.println("4. Payment-mode summary");
            System.out.println("5. Back");
            int c = readInt("Choose: ", 1, 5);
            switch (c) {
                case 1: parallelSummary(); break;
                case 2: salesByCar(); break;
                case 3: topSellingModels(); break;
                case 4: paymentSummary(); break;
                case 5: back = true; break;
            }
        }
    }

    private void parallelSummary() {
        List<Car> carCopy = new ArrayList<>(cars.values());
        List<Sale> saleCopy = new ArrayList<>(sales);
        List<Customer> customerCopy = new ArrayList<>(customers.values());
        List<ServiceBooking> serviceCopy = new ArrayList<>(serviceBookings);
        reportService.generate(carCopy, saleCopy, customerCopy, serviceCopy);
    }

    private void salesByCar() {
        Map<String, Integer> units = new TreeMap<>();
        Map<String, Double> revenue = new TreeMap<>();
        for (Sale s : sales) {
            units.put(s.getCarId(), units.getOrDefault(s.getCarId(), 0) + 1);
            revenue.put(s.getCarId(), revenue.getOrDefault(s.getCarId(), 0.0) + s.getTotalAmount());
        }
        if (units.isEmpty()) { System.out.println("No sales data."); return; }
        for (String id : units.keySet()) System.out.println(id + " | Units sold: " + units.get(id) + " | Revenue: " + Money.format(revenue.get(id)));
    }

    private void topSellingModels() {
        Map<String, Integer> counts = new HashMap<>();
        for (Sale s : sales) counts.put(s.getCarDescription(), counts.getOrDefault(s.getCarDescription(), 0) + 1);
        List<Map.Entry<String, Integer>> ranked = new ArrayList<>(counts.entrySet());
        ranked.sort((a,b) -> Integer.compare(b.getValue(), a.getValue()));
        if (ranked.isEmpty()) { System.out.println("No sales data."); return; }
        int rank = 1; for (Map.Entry<String,Integer> e : ranked) System.out.println((rank++) + ". " + e.getKey() + " - " + e.getValue() + " sale(s)");
    }

    private void paymentSummary() {
        Map<String, Double> totals = new TreeMap<>();
        for (Sale s : sales) totals.put(s.getPaymentMode(), totals.getOrDefault(s.getPaymentMode(), 0.0) + s.getTotalAmount());
        if (totals.isEmpty()) { System.out.println("No sales data."); return; }
        totals.forEach((mode,total) -> System.out.println(mode + " : " + Money.format(total)));
    }

    // --------------------------- PERSISTENCE ---------------------------
    private void loadAll() {
        DataStore.initialize();
        for (String line : DataStore.readLines("cars.txt")) { Car c=Car.fromDataLine(line); if(c!=null)cars.put(c.getId(),c); }
        for (String line : DataStore.readLines("customers.txt")) { Customer c=Customer.fromDataLine(line); if(c!=null)customers.put(c.getId(),c); }
        for (String line : DataStore.readLines("sales.txt")) { Sale s=Sale.fromDataLine(line); if(s!=null)sales.add(s); }
        for (String line : DataStore.readLines("testdrives.txt")) { TestDrive t=TestDrive.fromDataLine(line); if(t!=null)testDrives.add(t); }
        for (String line : DataStore.readLines("services.txt")) { ServiceBooking s=ServiceBooking.fromDataLine(line); if(s!=null)serviceBookings.add(s); }
    }

    private synchronized void saveAll() {
        DataStore.writeLines("cars.txt", cars.values().stream().map(Car::toDataLine).collect(Collectors.toList()));
        DataStore.writeLines("customers.txt", customers.values().stream().map(Customer::toDataLine).collect(Collectors.toList()));
        DataStore.writeLines("sales.txt", sales.stream().map(Sale::toDataLine).collect(Collectors.toList()));
        DataStore.writeLines("testdrives.txt", testDrives.stream().map(TestDrive::toDataLine).collect(Collectors.toList()));
        DataStore.writeLines("services.txt", serviceBookings.stream().map(ServiceBooking::toDataLine).collect(Collectors.toList()));
    }

    private void seedCars() {
        cars.put("CAR101", new Car("CAR101", "Maruti", "Baleno", 2025, "PETROL", "MANUAL", "Blue", 750000, 4, 1));
        cars.put("CAR102", new Car("CAR102", "Hyundai", "Creta", 2025, "PETROL", "AUTOMATIC", "White", 1250000, 3, 1));
        cars.put("CAR103", new Car("CAR103", "Tata", "Nexon EV", 2025, "ELECTRIC", "AUTOMATIC", "Grey", 1450000, 2, 1));
        cars.put("CAR104", new Car("CAR104", "Toyota", "Innova", 2024, "DIESEL", "AUTOMATIC", "Black", 2200000, 2, 1));
        saveAll();
    }

    // --------------------------- INPUT HELPERS ---------------------------
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try { int value=Integer.parseInt(scanner.nextLine().trim()); if(value>=min && value<=max)return value; }
            catch(NumberFormatException ignored) { }
            System.out.println("Enter a whole number between " + min + " and " + max + ".");
        }
    }
    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try { double value=Double.parseDouble(scanner.nextLine().trim()); if(Double.isFinite(value)&&value>=min&&value<=max)return value; }
            catch(NumberFormatException ignored) { }
            System.out.println("Enter a valid amount between " + min + " and " + max + ".");
        }
    }
    private String readText(String prompt) {
        while(true){System.out.print(prompt);String value=scanner.nextLine().trim();if(!value.isEmpty())return value;System.out.println("This field cannot be empty.");}
    }
    private String readOptional(String prompt) { System.out.print(prompt); return scanner.nextLine().trim(); }
    private String readChoice(String label, String... choices) {
        while(true){
            System.out.print(label + " (" + String.join("/", choices) + "): ");
            String value=scanner.nextLine().trim().toUpperCase(Locale.ROOT);
            for(String choice:choices)if(choice.equals(value))return value;
            System.out.println("Choose one of: " + String.join(", ", choices));
        }
    }
    private String nextId(String prefix, int number) { return prefix + String.format("%04d", number); }
}
