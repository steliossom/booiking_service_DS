import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DBServer {
    private static Map<String, Show> shows = new HashMap<>();

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(5550);
        System.out.println("DB Server...");

        while (true) {
            Socket client = server.accept();
            new Thread(() -> handleUser(client)).start();
        }
    }

    private static void handleUser(Socket socket) {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            String username = (String) in.readObject();
            String role = (String) in.readObject(); // "admin" ή "client"
            System.out.println("User '" + username + "' connected as " + role);

            while (true) {
                String command = (String) in.readObject();

                    switch (command) {
                        case "ADD_SHOW": {
                            Show show = (Show) in.readObject();
                            synchronized (shows) {
                                shows.put(show.getTitle(), show);
                            }
                            out.writeObject(true);
                            break;
                        }
                       case "DEACTIVATE_SHOW": {
    String title = (String) in.readObject();
    boolean success;
    synchronized (shows) {
        success = shows.remove(title) != null;
        System.out.println(success);
    }
    out.writeObject(success);
    break;
}

                        case "SEARCH_SHOWS": {
                            String type = (String) in.readObject();
                            List<Show> result = new ArrayList<>();
                            synchronized (shows) {
                                for (Show s : shows.values()) {
                                    if (s.getType().equalsIgnoreCase(type) || type.isEmpty()) {
                                        result.add(s);
                                    }
                                }
                            }
                            out.writeObject(result);
                            break;
                        }
                       case "UPDATE_SHOW": {
    Show updatedShow = (Show) in.readObject();
    boolean success = false;

    synchronized (shows) {
        Show existing = shows.get(updatedShow.getTitle());
        if (existing != null) {
            // Ενημέρωσε τα βασικά πεδία (χωρίς να αλλάξεις τις performances εκτός αν θέλεις)
            existing.setType(updatedShow.getType());
            existing.setPerformances(updatedShow.getPerformances()); // αν θες να αντικατασταθούν όλες
            success = true;
        }
    }

    out.writeObject(success); // ✅ Στέλνουμε boolean και ΟΧΙ string!
    break;
}

                        
                        case "GET_AVAILABLE_SHOWS": {
    List<Show> availableShows = new ArrayList<>();
    synchronized (shows) {
        for (Show s : shows.values()) {
            List<Performance> filtered = new ArrayList<>();
            for (Performance p : s.getPerformances()) {
                if (p.getAvailableSeats() > 0 &&
                    p.getDateTime().after(new Date()) &&
                    p.getActive()) {
                    filtered.add(p);
                }
            }
            if (!filtered.isEmpty()) {
                Show filteredShow = new Show(s.getTitle(), s.getType(), filtered);
                availableShows.add(filteredShow);
            }
        }
    }
out.writeObject(availableShows);
out.flush();
    break;
}
                        
                      case "ORDER_TICKETS": {
    username = (String) in.readObject();
    String showTitle = (String) in.readObject();
    String dateStr = (String) in.readObject();
    int seats = (int) in.readObject(); // Εδώ έσκασε αν client έστειλε String αντί για int
    boolean cancelOrder = (boolean)in.readObject();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Date dt = sdf.parse(dateStr);
    
    boolean success = false;
    int remainingSeats = 0;
    synchronized (shows) {
        Show sh = shows.get(showTitle);
        if (sh != null) {
            for (Performance p : sh.getPerformances()) {
                if (p.getDateTime().equals(dt)) {
                    synchronized (p) {
                        if (p.getAvailableSeats() >= seats && !cancelOrder) {
                            p.setAvailableSeats(p.getAvailableSeats() - seats);
                            success = true;
                            remainingSeats = p.getAvailableSeats();
                        } else {
                            remainingSeats = p.getAvailableSeats();
                        }
                    }
                    break;
                }
            }
        }
    }

    out.writeObject(success);
    out.writeObject(remainingSeats);
    break;
}
                      


                      case "CANCEL_TICKETS": {
    String user = (String) in.readObject();
    String showTitle = (String) in.readObject();
    String dateStr = (String) in.readObject();
    int seatsToCancel = (int) in.readObject();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Date dt = sdf.parse(dateStr);
    boolean cancelled = false;

    synchronized (shows) {
        Show sh = shows.get(showTitle);
        if (sh != null) {
            for (Performance p : sh.getPerformances()) {
                if (p.getDateTime().equals(dt)) {
                    // Ελέγχει αν είναι σημερινή η παράσταση
                    Calendar perfCal = Calendar.getInstance();
                    perfCal.setTime(p.getDateTime());

                    Calendar now = Calendar.getInstance();
                    boolean sameDay = perfCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                                      perfCal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR);

                    if (sameDay) {
                        System.out.println("Ακύρωση απορρίφθηκε: Η παράσταση είναι για σήμερα.");
                        break;
                    }

                    synchronized (p) {
                        p.setAvailableSeats(p.getAvailableSeats() + seatsToCancel);
                        cancelled = true;
                        System.out.println("Ακυρώθηκαν " + seatsToCancel + " θέσεις για το show " + showTitle + " στη παράσταση " + dateStr);
                    }
                    break;
                }
            }
        }
    }

    out.writeObject(cancelled);
    break;
}

                      

                        default:
                            out.writeObject("UNKNOWN_COMMAND");
                            break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
