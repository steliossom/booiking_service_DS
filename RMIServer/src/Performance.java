import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Performance implements Serializable {
    private Date dateTime;
    private int availableSeats;
    private double price;
    private boolean active;

    public Performance(){
        
    }
    
    public Performance(Date dateTime, int availableSeats, double price, boolean active) {
        this.dateTime = dateTime;
        this.availableSeats = availableSeats;
        this.price = price;
        this.active = active;
    }

    public Date getDateTime() {
        return this.dateTime;
    }

    public int getAvailableSeats() {
        return this.availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getPrice() {
        return this.price;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getActive(){
        return this.active;
    }
    
    public String getDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(dateTime.getTime());
    }

    public String getTimeString() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(dateTime.getTime());
    }

    public String getFullDateTimeString() {
        SimpleDateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return fullFormat.format(dateTime.getTime());
    }

    @Override
    public String toString() {
        return getFullDateTimeString() + " | Θέσεις: " + availableSeats + " | Τιμή: " + String.format("%.2f€", price);
    }
}
