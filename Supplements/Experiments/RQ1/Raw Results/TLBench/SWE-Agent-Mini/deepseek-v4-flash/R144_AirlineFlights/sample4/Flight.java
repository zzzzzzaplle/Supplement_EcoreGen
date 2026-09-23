import java.util.List;
import java.util.ArrayList;
import java.util.Date;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date t) {
        this.departureTime = t;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date t) {
        this.arrivalTime = t;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport D) {
        this.departureAirport = D;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport A) {
        this.arrivalAirport = A;
    }

    public boolean isOpenForBooking() {
        return openForBooking;
    }

    public void setOpenForBooking(boolean openForBooking) {
        this.openForBooking = openForBooking;
    }

    public List<Stopover> getStopovers() {
        return stopovers;
    }

    public void setStopovers(List<Stopover> stopovers) {
        this.stopovers = stopovers;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null || now == null) return false;
        if (!openForBooking) return false;
        if (!now.before(departureTime)) return false;
        
        // Check stopover times are consistent with flight times
        if (stop.getDepartureTime() == null || stop.getArrivalTime() == null) return false;
        if (!departureTime.before(stop.getArrivalTime())) return false;
        if (!stop.getArrivalTime().before(stop.getDepartureTime())) return false;
        if (!stop.getDepartureTime().before(arrivalTime)) return false;
        
        // Check that the stopover airport is not the departure or arrival airport
        if (stop.getAirport() == null) return false;
        if (stop.getAirport().getId().equals(departureAirport.getId()) ||
            stop.getAirport().getId().equals(arrivalAirport.getId())) return false;
        
        // Check for duplicate stopover at same airport
        for (Stopover s : stopovers) {
            if (s.getAirport() != null && s.getAirport().getId().equals(stop.getAirport().getId())) {
                return false;
            }
        }
        
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null) return false;
        if (!openForBooking) return false;
        if (!now.before(departureTime)) return false;
        
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        if (reservations != null) {
            for (Reservation r : reservations) {
                if (r.getStatus() == ReservationStatus.CONFIRMED) {
                    confirmed.add(r);
                }
            }
        }
        return confirmed;
    }
}
