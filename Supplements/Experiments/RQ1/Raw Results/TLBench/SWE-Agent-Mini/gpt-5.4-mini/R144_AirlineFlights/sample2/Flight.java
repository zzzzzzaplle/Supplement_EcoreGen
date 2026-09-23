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
        this.stopovers = new ArrayList<Stopover>();
        this.reservations = new ArrayList<Reservation>();
        this.openForBooking = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Date getDepartureTime() { return departureTime; }
    public void setDepartureTime(Date t) { this.departureTime = t; }
    public Date getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Date t) { this.arrivalTime = t; }
    public Airport getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(Airport D) { this.departureAirport = D; }
    public Airport getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(Airport A) { this.arrivalAirport = A; }
    public boolean isOpenForBooking() { return openForBooking; }
    public void setOpenForBooking(boolean openForBooking) { this.openForBooking = openForBooking; }
    public List<Stopover> getStopovers() { return stopovers; }
    public List<Reservation> getReservations() { return reservations; }
    public void setStopovers(List<Stopover> stopovers) { this.stopovers = stopovers; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null || now == null || !openForBooking) {
            return false;
        }
        if (stopovers == null) {
            stopovers = new ArrayList<Stopover>();
        }
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stopovers == null || stop == null || now == null || !openForBooking) {
            return false;
        }
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> result = new ArrayList<Reservation>();
        if (reservations == null) {
            return result;
        }
        for (Reservation r : reservations) {
            if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                result.add(r);
            }
        }
        return result;
    }
}
