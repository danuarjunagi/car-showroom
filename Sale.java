public class Sale {
    private final String id, dateTime, customerId, carId, carDescription, paymentMode, salesperson;
    private final double unitPrice, discountAmount, taxAmount, totalAmount;
    private final double downPayment, annualInterestRate, emi;
    private final int tenureMonths;

    public Sale(String id, String dateTime, String customerId, String carId, String carDescription,
                String paymentMode, String salesperson, double unitPrice, double discountAmount,
                double taxAmount, double totalAmount, double downPayment,
                double annualInterestRate, int tenureMonths, double emi) {
        this.id=id; this.dateTime=dateTime; this.customerId=customerId; this.carId=carId;
        this.carDescription=carDescription; this.paymentMode=paymentMode; this.salesperson=salesperson;
        this.unitPrice=unitPrice; this.discountAmount=discountAmount; this.taxAmount=taxAmount;
        this.totalAmount=totalAmount; this.downPayment=downPayment;
        this.annualInterestRate=annualInterestRate; this.tenureMonths=tenureMonths; this.emi=emi;
    }
    public String getId(){return id;} public String getDateTime(){return dateTime;}
    public String getCustomerId(){return customerId;} public String getCarId(){return carId;}
    public String getCarDescription(){return carDescription;} public String getPaymentMode(){return paymentMode;}
    public String getSalesperson(){return salesperson;} public double getUnitPrice(){return unitPrice;}
    public double getDiscountAmount(){return discountAmount;} public double getTaxAmount(){return taxAmount;}
    public double getTotalAmount(){return totalAmount;} public double getDownPayment(){return downPayment;}
    public double getAnnualInterestRate(){return annualInterestRate;} public int getTenureMonths(){return tenureMonths;}
    public double getEmi(){return emi;}

    public String toDataLine() {
        return DataStore.pack(id,dateTime,customerId,carId,carDescription,paymentMode,salesperson,
                String.valueOf(unitPrice),String.valueOf(discountAmount),String.valueOf(taxAmount),
                String.valueOf(totalAmount),String.valueOf(downPayment),String.valueOf(annualInterestRate),
                String.valueOf(tenureMonths),String.valueOf(emi));
    }
    public static Sale fromDataLine(String line) {
        String[] p=DataStore.unpack(line,15); if(p==null)return null;
        try { return new Sale(p[0],p[1],p[2],p[3],p[4],p[5],p[6],Double.parseDouble(p[7]),
                Double.parseDouble(p[8]),Double.parseDouble(p[9]),Double.parseDouble(p[10]),
                Double.parseDouble(p[11]),Double.parseDouble(p[12]),Integer.parseInt(p[13]),Double.parseDouble(p[14])); }
        catch(RuntimeException e){return null;}
    }
}
