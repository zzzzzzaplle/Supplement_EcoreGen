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
        if (stopovers == null) {
            stopovers = new ArrayList<Stopover>();
        }
        return stopovers;
    }

    public List<Reservation> getReservations() {
        if (reservations == null) {
            reservations = new ArrayList<Reservation>();
        }
        return reservations;
    }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null || now == null || departureTime == null) {
            return false;
        }
        if (!now.before(departureTime)) {
            return false;
        }
        getStopovers().add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null || departureTime == null) {
            return false;
        }
        if (!now.before(departureTime)) {
            return false;
        }
        return getStopovers().remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> result = new ArrayList<Reservation>();
        for (Reservation reservation : getReservations()) {
            if (reservation != null && reservation.getStatus() == ReservationStatus.CONFIRMED) {
                result.add(reservation);
            }
        }
        return result;
    }
}
