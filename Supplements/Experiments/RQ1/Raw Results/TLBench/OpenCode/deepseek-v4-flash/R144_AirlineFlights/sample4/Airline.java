import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
        flights.add(f);
    }

    public void removeFlight(Flight f) {
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) return false;
        if (f.isOpenForBooking()) return false;

        Date dep = f.getDepartureTime();
        Date arr = f.getArrivalTime();
        Airport depAirport = f.getDepartureAirport();
        Airport arrAirport = f.getArrivalAirport();

        if (dep == null || arr == null) return false;
        if (!isValidDateTimeFormat(dep) || !isValidDateTimeFormat(arr)) return false;
        if (!now.before(dep)) return false;
        if (!dep.before(arr)) return false;
        if (depAirport == null || arrAirport == null) return false;
        if (depAirport.getId() == null || arrAirport.getId() == null) return false;
        if (depAirport.getId().equals(arrAirport.getId())) return false;

        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) return false;
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                if (!f.isOpenForBooking()) return false;
                if (now.after(f.getDepartureTime()) || now.equals(f.getDepartureTime())) return false;
                f.setOpenForBooking(false);
                for (Reservation r : f.getReservations()) {
                    if (r.getStatus() == ReservationStatus.CONFIRMED) {
                        r.setStatus(ReservationStatus.CANCELED);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<Flight>();
        for (Flight f : flights) {
            if (!f.isOpenForBooking()) continue;
            if (f.getDepartureAirport() != null && f.getDepartureAirport().getId() != null
                    && f.getDepartureAirport().getId().equals(origin)
                    && f.getArrivalAirport() != null && f.getArrivalAirport().getId() != null
                    && f.getArrivalAirport().getId().equals(dest)
                    && isSameDay(f.getDepartureTime(), date)) {
                result.add(f);
            }
        }
        return result;
    }

    private boolean isValidDateTimeFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        try {
            sdf.format(date);
            sdf.parse(sdf.format(date));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(d1).equals(sdf.format(d2));
    }
}
