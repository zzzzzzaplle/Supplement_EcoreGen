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

    public void setDepartureAirport(Airport d) {
        this.departureAirport = d;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport a) {
        this.arrivalAirport = a;
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
        if (stop == null) {
            return false;
        }
        if (now != null && this.departureTime != null && now.after(this.departureTime)) {
            return false;
        }
        if (this.stopovers == null) {
            this.stopovers = new ArrayList<Stopover>();
        }
        if (!this.stopovers.contains(stop)) {
            this.stopovers.add(stop);
        }
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null) {
            return false;
        }
        if (now != null && this.departureTime != null && now.after(this.departureTime)) {
            return false;
        }
        if (this.stopovers != null) {
            return this.stopovers.remove(stop);
        }
        return false;
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> result = new ArrayList<Reservation>();
        if (!this.openForBooking) {
            return result;
        }
        if (this.reservations == null) {
            return result;
        }
        for (Reservation r : this.reservations) {
            if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                result.add(r);
            }
        }
        return result;
    }
}
