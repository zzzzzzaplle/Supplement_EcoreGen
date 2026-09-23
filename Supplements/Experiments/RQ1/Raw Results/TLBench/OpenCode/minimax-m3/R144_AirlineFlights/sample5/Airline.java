import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        if (f != null && !flights.contains(f)) {
            flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (f != null) {
            flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) {
            return false;
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        if (!isValidTimestamp(f.getDepartureTime()) || !isValidTimestamp(f.getArrivalTime())) {
            return false;
        }
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        if (!f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (f.getDepartureAirport().getId() == null || f.getArrivalAirport().getId() == null) {
            return false;
        }
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
            return false;
        }
        f.setOpenForBooking(true);
        if (!flights.contains(f)) {
            flights.add(f);
        }
        return true;
    }

    private boolean isValidTimestamp(Date date) {
        if (date == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        try {
            String formatted = sdf.format(date);
            Date parsed = sdf.parse(formatted);
            return parsed.getTime() == date.getTime();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }
        if (this.flights == null) {
            return false;
        }
        for (Flight f : this.flights) {
            if (f != null && flightId.equals(f.getId())) {
                if (!f.isOpenForBooking()) {
                    return false;
                }
                if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
                    return false;
                }
                if (f.getReservations() != null) {
                    for (Reservation r : f.getReservations()) {
                        if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                            r.setStatus(ReservationStatus.CANCELED);
                        }
                    }
                }
                f.setOpenForBooking(false);
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
            if (f == null || !f.isOpenForBooking()) {
                continue;
            }
            if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
                continue;
            }
            String depId = f.getDepartureAirport().getId();
            String arrId = f.getArrivalAirport().getId();
            if (origin != null && !origin.equals(depId)) {
                continue;
            }
            if (dest != null && !dest.equals(arrId)) {
                continue;
            }
            if (date != null) {
                if (f.getDepartureTime() == null || !isSameDay(f.getDepartureTime(), date)) {
                    continue;
                }
            }
            result.add(f);
        }
        return result;
    }

    private boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }
}
