import java.util.*;

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
        this.openForBooking = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getDepartureTime() { return departureTime; }
    public void setDepartureTime(Date t) { this.departureTime = t; }

    public Date getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Date t) { this.arrivalTime = t; }

    public Airport getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(Airport d) { this.departureAirport = d; }

    public Airport getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(Airport a) { this.arrivalAirport = a; }

    public boolean isOpenForBooking() { return openForBooking; }
    public void setOpenForBooking(boolean openForBooking) { this.openForBooking = openForBooking; }

    public List<Stopover> getStopovers() { return stopovers; }

    public List<Reservation> getReservations() { return reservations; }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null || openForBooking == false) {
            return false;
        }
        if (stop.getArrivalTime() == null || stop.getDepartureTime() == null) {
            return false;
        }
        if (now.after(stop.getDepartureTime()) || stop.getDepartureTime().after(stop.getArrivalTime())
                || stop.getDepartureTime().before(departureTime) || stop.getArrivalTime().after(arrivalTime)) {
            return false;
        }
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || openForBooking == false) {
            return false;
        }
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                result.add(r);
            }
        }
        return result;
    }
}
