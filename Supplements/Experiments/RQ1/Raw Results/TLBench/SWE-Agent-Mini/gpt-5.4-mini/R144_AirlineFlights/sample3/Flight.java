import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        if (stop == null || now == null) {
            return false;
        }
        if (stopovers == null) {
            stopovers = new ArrayList<>();
        }
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null || stopovers == null) {
            return false;
        }
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> result = new ArrayList<>();
        if (reservations != null) {
            for (Reservation r : reservations) {
                if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                    result.add(r);
                }
            }
        }
        return result;
    }

    public boolean validateTemporalFormat(String departure, String arrival) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setLenient(false);
            sdf.parse(departure);
            sdf.parse(arrival);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean hasDuplicatePassengers() {
        Set<String> names = new HashSet<>();
        if (reservations == null) {
            return false;
        }
        for (Reservation r : reservations) {
            if (r != null && r.getPassenger() != null && r.getPassenger().getName() != null) {
                if (!names.add(r.getPassenger().getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
