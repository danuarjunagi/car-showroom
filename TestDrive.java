public class TestDrive {
    private final String id, customerId, carId, dateTime, salesperson;
    private String status;
    public TestDrive(String id,String customerId,String carId,String dateTime,String salesperson,String status){
        this.id=id;this.customerId=customerId;this.carId=carId;this.dateTime=dateTime;this.salesperson=salesperson;this.status=status;
    }
    public String getId(){return id;} public String getCustomerId(){return customerId;}
    public String getCarId(){return carId;} public String getDateTime(){return dateTime;}
    public String getSalesperson(){return salesperson;} public String getStatus(){return status;}
    public void setStatus(String status){this.status=status;}
    public String toDataLine(){return DataStore.pack(id,customerId,carId,dateTime,salesperson,status);}
    public static TestDrive fromDataLine(String line){
        String[] p=DataStore.unpack(line,6);return p==null?null:new TestDrive(p[0],p[1],p[2],p[3],p[4],p[5]);
    }
}
