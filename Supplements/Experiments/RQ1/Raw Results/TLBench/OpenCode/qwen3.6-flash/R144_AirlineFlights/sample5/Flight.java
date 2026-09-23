import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (stop == null || !openForBooking) {
            return false;
        }
        Date stopArrival = stop.getArrivalTime();
        Date stopDep = stop.getDepartureTime();
        if (stopArrival == null || stopDep == null) {
            return false;
        }
        if (now.after(stopArrival) || stopDep.after(stopArrival)) {
            return false;
        }
        if (stop.getAirport() == null) {
            return false;
        }
        if (departureAirport != null && departureAirport.equals(stop.getAirport())) {
            return false;
        }
        if (arrivalAirport != null && arrivalAirport.equals(stop.getAirport())) {
            return false;
        }
        return stopovers.add(stop);
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || !openForBooking) {
            return false;
        }
        Date stopArrival = stop.getArrivalTime();
        if (stopArrival != null && now.after(stopArrival)) {
            return false;
        }
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> result = new ArrayList<>();
        if (!openForBooking) {
            return result;
        }
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                result.add(r);
            }
        }
        return result;
    }
}
