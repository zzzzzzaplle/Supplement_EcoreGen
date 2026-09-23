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
        this.openForBooking = false;
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
        if (!now.before(this.departureTime)) return false;
        if (stop.getAirport() == null) return false;
        if (stop.getDepartureTime() == null || stop.getArrivalTime() == null) return false;
        if (!this.departureTime.before(stop.getDepartureTime())) return false;
        if (!stop.getDepartureTime().before(stop.getArrivalTime())) return false;
        if (!stop.getArrivalTime().before(this.arrivalTime)) return false;
        if (this.stopovers == null) this.stopovers = new ArrayList<>();
        this.stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null) return false;
        if (!now.before(this.departureTime)) return false;
        if (this.stopovers != null && this.stopovers.contains(stop)) {
            this.stopovers.remove(stop);
            return true;
        }
        return false;
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        if (this.reservations != null) {
            for (Reservation r : this.reservations) {
                if (r.getStatus() == ReservationStatus.CONFIRMED) {
                    confirmed.add(r);
                }
            }
        }
        return confirmed;
    }
}
