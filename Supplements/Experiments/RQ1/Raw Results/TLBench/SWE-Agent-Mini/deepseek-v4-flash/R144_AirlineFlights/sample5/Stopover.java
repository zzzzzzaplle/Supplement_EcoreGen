import java.util.Date;

public class Stopover {
    private Date departureTime;
    private Date arrivalTime;
    private Airport airport;
    
    public Stopover() {
    }
    
    public Stopover(Date departureTime, Date arrivalTime, Airport airport) {
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.airport = airport;
    }
    
    public Date getDepartureTime() {
        return departureTime;
    }
    
    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }
    
    public Date getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public Airport getAirport() {
        return airport;
    }
    
    public void setAirport(Airport airport) {
        this.airport = airport;
    }
}
