// BookingServiceImpl.java
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.*;
import java.net.*;
import java.io.*;


public class BookingServiceImpl extends UnicastRemoteObject implements BookingService  {
    private Map<String, User> users = new HashMap<>();
    private Socket dbSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public BookingServiceImpl() throws RemoteException {
    }

    private void connectToDBServer(String username, String role) {
    try {
        dbSocket = new Socket("localhost", 5550);
        out = new ObjectOutputStream(dbSocket.getOutputStream());
        in = new ObjectInputStream(dbSocket.getInputStream());

        out.writeObject(username); 
        out.writeObject(role);     
    } catch (IOException e) {
        e.printStackTrace();
    }
}


   public boolean registerUser(User u) throws RemoteException {
    if (users.containsKey(u.getUsername())) {
        return false; 
    }
    users.put(u.getUsername(), u);  
    return true;  
}

  @Override
public User login(String username, String password) throws RemoteException {
    User u = users.get(username);
    if (u != null && u.getPassword().equals(password)) {
        connectToDBServer(username, u.getRole()); 
        return u;
    }
    return null;
}



    public void logout(String username) throws RemoteException {}

    public boolean deleteUser(String username, String password) throws RemoteException {
        User u = users.get(username);
        if (u != null && u.getPassword().equals(password)) {
            users.remove(username);
            return true;
        }
        return false;
    }

    public boolean addShow(Show show) throws RemoteException {
        try {
            out.writeObject("ADD_SHOW");
            out.writeObject(show);
            return (boolean) in.readObject();
} catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean deactivateShow(String title) throws RemoteException {
        try {
            out.writeObject("DEACTIVATE_SHOW");
            out.writeObject(title);
            return (boolean) in.readObject();
        } catch (Exception e) {
    e.printStackTrace();
    return false;
}

    }

    public boolean updateShow(Show show) throws RemoteException {
    try {
        out.writeObject("UPDATE_SHOW");
        out.writeObject(show);

        boolean success = (boolean) in.readObject();
        
        return success;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    
    public List<Show> searchShows(String type) throws RemoteException {
        try {
            out.writeObject("SEARCH_SHOWS");
            out.writeObject(type);
            return (List<Show>) in.readObject();
        } catch (Exception e) { return Collections.emptyList(); }
    }


    public boolean cancelOrder(String username, String showTitle, Date datetime) throws RemoteException {
        try {
            out.writeObject("CANCEL_ORDER");
            out.writeObject(username);
            out.writeObject(showTitle);
            out.writeObject(datetime);
            return (boolean) in.readObject();
        } catch (Exception e) { return false; }
    }


 @Override
public List<Show> getAvailableShows() throws RemoteException {
    try {
        out.writeObject("GET_AVAILABLE_SHOWS");
        return (List<Show>) in.readObject();
    } catch (Exception e) {
        e.printStackTrace();
        return Collections.emptyList();
    }
}


@Override
public boolean bookTickets(String username, String performanceKey, int count, boolean cancelOrder) throws RemoteException {
    try {
        out.writeObject("ORDER_TICKETS");
        out.writeObject(username);

        String showTitle = performanceKey.split(" \\| ")[0];
        String dateStr = performanceKey.split(" \\| ")[1];

        out.writeObject(showTitle);
        out.writeObject(dateStr);
        out.writeObject(count);
        out.writeObject(cancelOrder);

        boolean success = (boolean) in.readObject();
        return success;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

 
 @Override
 public boolean cancelOrder(String username, String showTitle, String performanceDateStr, int seats) {
    try {
        out.writeObject("CANCEL_TICKETS");
        out.writeObject(username);
        out.writeObject(showTitle);
        out.writeObject(performanceDateStr); 
        out.writeObject(seats);
        out.flush();

        return (boolean) in.readObject();
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
 
 

 


}
