// TicketOrder.java
import java.io.Serializable;

public class TicketOrder implements Serializable {
    private String fullName;
    private String title;
    private String performanceDate;
    private String cardNumber;
    private int tickets;
    
    public TicketOrder(String fullName, String cardNumber , String title, String performanceDate, int tickets){
        this.fullName = fullName;
        this.cardNumber = cardNumber;
        this.title = title;
        this.performanceDate = performanceDate;
        this.tickets = tickets;
    }
    
    public String getTitle(){
        return this.title;
    }
    
    public String getSelectedPerformance(){
        return this.performanceDate;
    }
    
    public int getTickets(){
        return this.tickets;
    }
    
}
