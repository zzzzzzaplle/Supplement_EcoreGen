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

    public List<Reservation> getReservations() {
        return reservations;
    }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null || now == null) {
            return false;
        }
        Date stopDep = stop.getDepartureTime();
        Date stopArr = stop.getArrivalTime();
        if (stopDep == null || stopArr == null) {
            return false;
        }
        if (stopArr.getTime() <= stopDep.getTime()) {
            return false;
        }
        if (now.getTime() >= stopDep.getTime()) {
            return false;
        }
        if (stopDep.getTime() >= departureTime.getTime()) {
            return false;
        }
        if (stopArr.getTime() <= arrivalTime.getTime()) {
            return false;
        }
        if (!stopovers.contains(stop)) {
            stopovers.add(stop);
            return true;
        }
        return false;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null) {
            return false;
        }
        if (stopovers.remove(stop)) {
            return true;
        }
        return false;
    }

    public static long generateId() {
        return System.nanoTime();
    }

    public boolean publish(Date now) {
        if (now == null || !openForBooking) {
            return false;
        }
        openForBooking = true;
        return true;
    }

    public boolean closeFlight(Date now) {
        if (now == null || !openForBooking) {
            return false;
        }
        openForBooking = false;
        for (Reservation r : reservations) {
            if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                r.setStatus(ReservationStatus.CANCELED);
            }
        }
        return true;
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                confirmed.add(r);
            }
        }
        return confirmed;
    }
}
