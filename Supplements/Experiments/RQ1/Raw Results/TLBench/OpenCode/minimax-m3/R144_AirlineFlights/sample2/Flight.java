import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        this.openForBooking = false;
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

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
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
        if (this.stopovers == null) {
            this.stopovers = new ArrayList<Stopover>();
        }
        if (stop.getAirport() == null) {
            return false;
        }
        if (stop.getArrivalTime() == null || stop.getDepartureTime() == null) {
            return false;
        }
        if (this.departureTime == null || this.arrivalTime == null) {
            return false;
        }
        if (!stop.getArrivalTime().before(stop.getDepartureTime())) {
            return false;
        }
        if (stop.getArrivalTime().before(this.departureTime) || !stop.getDepartureTime().before(this.arrivalTime)) {
            return false;
        }
        if (this.stopovers.contains(stop)) {
            return false;
        }
        return this.stopovers.add(stop);
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || this.stopovers == null) {
            return false;
        }
        if (now != null && this.departureTime != null && !now.before(this.departureTime)) {
            return false;
        }
        return this.stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<Reservation>();
        if (this.reservations == null) {
            return confirmed;
        }
        if (!this.openForBooking) {
            return confirmed;
        }
        for (Reservation r : this.reservations) {
            if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                confirmed.add(r);
            }
        }
        return confirmed;
    }

    public static boolean isValidTimestamp(String s) {
        if (s == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        try {
            sdf.parse(s);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
