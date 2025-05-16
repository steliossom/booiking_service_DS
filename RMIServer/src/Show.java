// Show.java
import java.io.Serializable;
import java.util.*;

public class Show implements Serializable {
    private String title, type;
    private List<Performance> performances = new ArrayList<>();

    public Show(String title, String type, List<Performance> performances){
        this.title = title;
        this.type = type;
        this.performances = performances;
    }
    
    public String getTitle(){
        return this.title;
    }
    
    public String getType(){
        return this.type;
    }
    
    public void setType(String type){
        this.type = type;
    }
    
    public List <Performance> getPerformances(){
        return this.performances;
    }
    
    public void setPerformances(List <Performance> performances){
        this.performances = performances;
    }
    
    public void addPerformance(Performance performance){
        performances.add(performance);
    }
    
    
}
