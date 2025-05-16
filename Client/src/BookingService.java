import java.rmi.*;
import java.util.List;

public interface BookingService extends Remote {
    boolean registerUser(User u) throws RemoteException;
    User login(String username, String password) throws RemoteException;
    void logout(String username) throws RemoteException;
    boolean deleteUser(String username, String password) throws RemoteException;

    boolean addShow(Show show) throws RemoteException;
    boolean deactivateShow(String title) throws RemoteException;

    List<Show> searchShows(String type) throws RemoteException;
    boolean cancelOrder(String username, String showTitle, String datetime, int seats) throws RemoteException;
    
    List<Show> getAvailableShows() throws RemoteException;
    boolean bookTickets(String username, String performanceKey, int count, boolean cancelOrder) throws RemoteException;
    boolean updateShow(Show show) throws RemoteException;
}
