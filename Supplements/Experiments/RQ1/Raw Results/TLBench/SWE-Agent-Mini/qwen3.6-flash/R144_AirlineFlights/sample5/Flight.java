import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        this.id = UUID.randomUUID().toString();
        this.openForBooking = false;
        this.departureTime = new Date();
        this.arrivalTime = new Date();
        this.departureAirport = null;
        this.arrivalAirport = null;
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
        if (stop == null || now == null) {
            return false;
        }
        
        if (now.getTime() >= departureTime.getTime()) {
            return false;
        }

        return stopovers.add(stop);
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null) {
            return false;
        }
        
        if (now.getTime() >= departureTime.getTime()) {
            return false;
        }

        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        return reservations.stream()
            .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
            .collect(Collectors.toList());
    }
}
