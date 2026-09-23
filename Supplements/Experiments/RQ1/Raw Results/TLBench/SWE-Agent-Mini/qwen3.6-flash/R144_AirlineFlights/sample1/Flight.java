import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Flight {
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
        if (stop == null || now == null) {
            return false;
        }
        if (!openForBooking) {
            return false;
        }
        Date depT = departureTime;
        Date arrT = arrivalTime;
        Date stopDep = stop.getDepartureTime();
        Date stopArr = stop.getArrivalTime();
        if (depT == null || arrT == null || stopDep == null || stopArr == null) {
            return false;
        }
        if (now.after(depT)) {
            return false;
        }
        if (stopDep.before(depT) || stopArr.after(arrT)) {
            return false;
        }
        if (stopDep.after(stopArr)) {
            return false;
        }
        if (!stopovers.contains(stop)) {
            stopovers.add(stop);
        }
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null) {
            return false;
        }
        if (!openForBooking) {
            return false;
        }
        if (departureTime != null && now.after(departureTime)) {
            return false;
        }
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                confirmed.add(r);
            }
        }
        return confirmed;
    }
}
