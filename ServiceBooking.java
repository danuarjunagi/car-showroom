public class ServiceBooking {
    private final String id, customerId, vehicleDetails, bookingDate, issue, serviceType;
    private String status;
    private double estimatedCost;
    public ServiceBooking(String id,String customerId,String vehicleDetails,String bookingDate,
                          String issue,String serviceType,String status,double estimatedCost){
        this.id=id;this.customerId=customerId;this.vehicleDetails=vehicleDetails;this.bookingDate=bookingDate;
        this.issue=issue;this.serviceType=serviceType;this.status=status;this.estimatedCost=estimatedCost;
    }
    public String getId(){return id;} public String getCustomerId(){return customerId;}
    public String getVehicleDetails(){return vehicleDetails;} public String getBookingDate(){return bookingDate;}
    public String getIssue(){return issue;} public String getServiceType(){return serviceType;}
    public String getStatus(){return status;} public double getEstimatedCost(){return estimatedCost;}
    public void setStatus(String status){this.status=status;}
    public void setEstimatedCost(double cost){this.estimatedCost=cost;}
    public String toDataLine(){return DataStore.pack(id,customerId,vehicleDetails,bookingDate,issue,serviceType,status,String.valueOf(estimatedCost));}
    public static ServiceBooking fromDataLine(String line){
        String[] p=DataStore.unpack(line,8);if(p==null)return null;
        try{return new ServiceBooking(p[0],p[1],p[2],p[3],p[4],p[5],p[6],Double.parseDouble(p[7]));}
        catch(RuntimeException e){return null;}
    }
}
