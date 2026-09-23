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
        if (stop == null) {
            return false;
        }
        // Check that the flight has not yet departed
        if (now == null || departureTime == null) {
            return false;
        }
        if (!now.before(departureTime)) {
            return false;
        }
        // Validate stopover times: arrival time must be after departure time
        Date sDep = stop.getDepartureTime();
        Date sArr = stop.getArrivalTime();
        if (sDep == null || sArr == null) {
            return false;
        }
        if (!sDep.before(sArr)) {
            return false;
        }
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null) {
            return false;
        }
        if (now == null || departureTime == null) {
            return false;
        }
        if (!now.before(departureTime)) {
            return false;
        }
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        if (reservations != null) {
            for (Reservation res : reservations) {
                if (res != null && res.getStatus() == ReservationStatus.CONFIRMED) {
                    confirmed.add(res);
                }
            }
        }
        return confirmed;
    }
}
