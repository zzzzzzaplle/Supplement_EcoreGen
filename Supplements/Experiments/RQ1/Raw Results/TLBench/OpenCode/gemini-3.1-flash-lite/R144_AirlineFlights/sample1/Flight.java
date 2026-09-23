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
    private List<Stopover> stopovers = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    public Flight() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public boolean isOpenForBooking() { return openForBooking; }
    public void setOpenForBooking(boolean openForBooking) { this.openForBooking = openForBooking; }
    public Date getDepartureTime() { return departureTime; }
    public void setDepartureTime(Date t) { this.departureTime = t; }
    public Date getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Date t) { this.arrivalTime = t; }
    public Airport getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(Airport D) { this.departureAirport = D; }
    public Airport getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(Airport A) { this.arrivalAirport = A; }
    public List<Stopover> getStopovers() { return stopovers; }
    public void setStopovers(List<Stopover> stopovers) { this.stopovers = stopovers; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public boolean addStopover(Stopover stop, Date now) {
        stopovers.add(stop);
        return true;
    }
    public boolean removeStopover(Stopover stop, Date now) {
        return stopovers.remove(stop);
    }
    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) confirmed.add(r);
        }
        return confirmed;
    }
}
