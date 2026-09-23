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

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDepartureTime() {
        return this.departureTime;
    }

    public void setDepartureTime(Date t) {
        this.departureTime = t;
    }

    public Date getArrivalTime() {
        return this.arrivalTime;
    }

    public void setArrivalTime(Date t) {
        this.arrivalTime = t;
    }

    public Airport getDepartureAirport() {
        return this.departureAirport;
    }

    public void setDepartureAirport(Airport d) {
        this.departureAirport = d;
    }

    public Airport getArrivalAirport() {
        return this.arrivalAirport;
    }

    public void setArrivalAirport(Airport a) {
        this.arrivalAirport = a;
    }

    public boolean isOpenForBooking() {
        return this.openForBooking;
    }

    public void setOpenForBooking(boolean openForBooking) {
        this.openForBooking = openForBooking;
    }

    public List<Stopover> getStopovers() {
        if (this.stopovers == null) {
            this.stopovers = new ArrayList<Stopover>();
        }
        return this.stopovers;
    }

    public void setStopovers(List<Stopover> stopovers) {
        this.stopovers = stopovers;
    }

    public List<Reservation> getReservations() {
        if (this.reservations == null) {
            this.reservations = new ArrayList<Reservation>();
        }
        return this.reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null) {
            return false;
        }
        if (stop.getDepartureTime() == null || stop.getArrivalTime() == null) {
            return false;
        }
        if (stop.getAirport() == null) {
            return false;
        }
        if (stop.getArrivalTime().before(stop.getDepartureTime())) {
            return false;
        }
        if (this.departureTime != null && stop.getDepartureTime().before(this.departureTime)) {
            return false;
        }
        if (this.arrivalTime != null && stop.getArrivalTime().after(this.arrivalTime)) {
            return false;
        }
        if (!this.getStopovers().contains(stop)) {
            this.stopovers.add(stop);
        }
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null) {
            return false;
        }
        return this.getStopovers().remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<Reservation>();
        if (!this.openForBooking) {
            return confirmed;
        }
        for (Reservation r : this.getReservations()) {
            if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                confirmed.add(r);
            }
        }
        return confirmed;
    }
}
