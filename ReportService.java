import java.util.*;
import java.util.concurrent.*;

/** Demonstrates concurrent generation of independent showroom reports. */
public class ReportService {
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public void generate(List<Car> carSnapshot, List<Sale> saleSnapshot,
                         List<Customer> customerSnapshot, List<ServiceBooking> serviceSnapshot) {
        Callable<String> inventoryTask = () -> {
            int units=0; double value=0;
            for(Car c:carSnapshot){units+=c.getStock();value+=c.getStock()*c.getPrice();}
            return "[Inventory Thread] Models: "+carSnapshot.size()+", units: "+units+
                    ", stock value: "+Money.format(value);
        };
        Callable<String> salesTask = () -> {
            double revenue=0; for(Sale s:saleSnapshot) revenue+=s.getTotalAmount();
            return "[Sales Thread] Invoices: "+saleSnapshot.size()+", gross sales: "+Money.format(revenue);
        };
        Callable<String> serviceTask = () -> {
            long open=serviceSnapshot.stream().filter(s->!"COMPLETED".equals(s.getStatus()) && !"CANCELLED".equals(s.getStatus())).count();
            return "[Service Thread] Customers: "+customerSnapshot.size()+", active service bookings: "+open;
        };
        List<Future<String>> results = new ArrayList<>();
        results.add(executor.submit(inventoryTask));
        results.add(executor.submit(salesTask));
        results.add(executor.submit(serviceTask));
        for(Future<String> future:results){
            try{System.out.println(future.get());}
            catch(InterruptedException e){Thread.currentThread().interrupt();System.out.println("Report interrupted.");}
            catch(ExecutionException e){System.out.println("Report error: "+e.getCause().getMessage());}
        }
    }
    public void shutdown(){executor.shutdown();}
}
