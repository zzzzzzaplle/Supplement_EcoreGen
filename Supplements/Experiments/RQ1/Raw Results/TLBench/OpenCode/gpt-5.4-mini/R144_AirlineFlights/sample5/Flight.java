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
        return stopovers;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null || now == null || departureTime == null || arrivalTime == null || !openForBooking) {
            return false;
        }
        if (!now.before(departureTime)) {
            return false;
        }
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null || departureTime == null || !openForBooking || !now.before(departureTime)) {
            return false;
        }
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<Reservation>();
        if (reservations != null) {
            for (Reservation reservation : reservations) {
                if (reservation != null && reservation.getStatus() == ReservationStatus.CONFIRMED) {
                    confirmed.add(reservation);
                }
            }
        }
        return confirmed;
    }
}
