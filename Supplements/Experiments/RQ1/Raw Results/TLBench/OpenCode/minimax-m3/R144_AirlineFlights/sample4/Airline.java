import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Airline {

    private List<Flight> flights;

    public Airline() {
        this.flights = new ArrayList<Flight>();
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public void addFlight(Flight f) {
        if (f == null) {
            return;
        }
        if (this.flights == null) {
            this.flights = new ArrayList<Flight>();
        }
        if (!this.flights.contains(f)) {
            this.flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (f == null || this.flights == null) {
            return;
        }
        this.flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null) {
            return false;
        }
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }
        if (!isValidTimestampFormat(f.getDepartureTime())
                || !isValidTimestampFormat(f.getArrivalTime())) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (f.getDepartureAirport().getId() != null
                && f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
            return false;
        }
        if (f.getDepartureAirport() == f.getArrivalAirport()) {
            return false;
        }
        if (now == null) {
            return false;
        }
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        if (!f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        f.setOpenForBooking(true);
        if (this.flights == null) {
            this.flights = new ArrayList<Flight>();
        }
        if (!this.flights.contains(f)) {
            this.flights.add(f);
        }
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || this.flights == null) {
            return false;
        }
        for (Flight f : this.flights) {
            if (f == null) {
                continue;
            }
            if (flightId.equals(f.getId())) {
                if (!f.isOpenForBooking()) {
                    return false;
                }
                if (f.getDepartureTime() != null && now != null
                        && !now.before(f.getDepartureTime())) {
                    return false;
                }
                f.setOpenForBooking(false);
                if (f.getReservations() != null) {
                    for (Reservation r : f.getReservations()) {
                        if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                            r.setStatus(ReservationStatus.CANCELED);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<Flight>();
        if (this.flights == null) {
            return result;
        }
        for (Flight f : this.flights) {
            if (f == null) {
                continue;
            }
            if (!f.isOpenForBooking()) {
                continue;
            }
            Airport dep = f.getDepartureAirport();
            Airport arr = f.getArrivalAirport();
            if (dep == null || arr == null) {
                continue;
            }
            if (origin != null && !origin.equals(dep.getId())) {
                continue;
            }
            if (dest != null && !dest.equals(arr.getId())) {
                continue;
            }
            if (date != null) {
                if (f.getDepartureTime() == null) {
                    continue;
                }
                SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
                String fDate = ymd.format(f.getDepartureTime());
                String sDate = ymd.format(date);
                if (!fDate.equals(sDate)) {
                    continue;
                }
            }
            result.add(f);
        }
        return result;
    }

    private boolean isValidTimestampFormat(Date d) {
        if (d == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        try {
            String formatted = sdf.format(d);
            Date parsed = sdf.parse(formatted);
            return parsed != null;
        } catch (ParseException e) {
            return false;
        }
    }
}
