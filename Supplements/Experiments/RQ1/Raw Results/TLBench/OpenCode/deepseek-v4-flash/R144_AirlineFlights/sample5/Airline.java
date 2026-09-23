import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Airline {
    private List<Flight> flights;

    public Airline() {
        this.flights = new ArrayList<>();
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public void addFlight(Flight f) {
        if (f != null) {
            flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String depStr = sdf.format(f.getDepartureTime());
        String arrStr = sdf.format(f.getArrivalTime());
        if (depStr == null || arrStr == null) {
            return false;
        }
        try {
            sdf.parse(depStr);
            sdf.parse(arrStr);
        } catch (Exception e) {
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
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
            return false;
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                if (!f.isOpenForBooking()) {
                    return false;
                }
                if (!now.before(f.getDepartureTime())) {
                    return false;
                }
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
        List<Flight> result = new ArrayList<>();
        for (Flight f : flights) {
            if (!f.isOpenForBooking()) {
                continue;
            }
            if (origin != null && !origin.isEmpty()) {
                if (f.getDepartureAirport() == null || !f.getDepartureAirport().getId().equals(origin)) {
                    continue;
                }
            }
            if (dest != null && !dest.isEmpty()) {
                if (f.getArrivalAirport() == null || !f.getArrivalAirport().getId().equals(dest)) {
                    continue;
                }
            }
            if (date != null) {
                if (!isSameDay(f.getDepartureTime(), date)) {
                    continue;
                }
            }
            result.add(f);
        }
        return result;
    }

    private boolean isSameDay(Date d1, Date d2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(d1).equals(sdf.format(d2));
    }
}
