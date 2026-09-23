import java.util.*;

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

    public boolean addStopover(Stopover stop, Date now) {
        if (now.after(departureTime)) return false;
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (now.after(departureTime)) return false;
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        if (openForBooking) {
            for (Reservation r : reservations) {
                if (r.getStatus() == ReservationStatus.CONFIRMED) {
                    confirmed.add(r);
                }
            }
        }
        return confirmed;
    }
}
