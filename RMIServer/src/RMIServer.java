import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            BookingService service = new BookingServiceImpl();
            Naming.rebind("BookingService", service);
            System.out.println("RMI Server...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
