import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Flight {
    private String id;
    private boolean openForBooking;
    private Date departureTime;
    private Date arrivalTime;
    private Airport departureAirport;
    private Airport arrivalAirport;
    private List<Stopover> stopovers;
    private List<Reservation> reservations;

    public Flight() {
        this.stopovers = new ArrayList<>();
        this.reservations = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public boolean isOpenForBooking() { return openForBooking; }
    public void setOpenForBooking(boolean openForBooking) { this.openForBooking = openForBooking; }
    public Date getDepartureTime() { return departureTime; }
    public void setDepartureTime(Date departureTime) { this.departureTime = departureTime; }
    public Date getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Date arrivalTime) { this.arrivalTime = arrivalTime; }
    public Airport getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(Airport departureAirport) { this.departureAirport = departureAirport; }
    public Airport getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(Airport arrivalAirport) { this.arrivalAirport = arrivalAirport; }
    public List<Stopover> getStopovers() { return stopovers; }
    public void setStopovers(List<Stopover> stopovers) { this.stopovers = stopovers; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public boolean addStopover(Stopover stop, Date now) { return false; }
    public boolean removeStopover(Stopover stop, Date now) { return false; }
    
    public List<Reservation> getConfirmedReservations() {
        if (!isOpenForBooking()) return new ArrayList<>();
        List<Reservation> confirmed = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                confirmed.add(r);
            }
        }
        return confirmed;
    }
}
