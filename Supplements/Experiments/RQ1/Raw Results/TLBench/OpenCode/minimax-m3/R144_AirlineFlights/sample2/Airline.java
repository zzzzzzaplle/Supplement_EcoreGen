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
        if (this.flights == null) {
            this.flights = new ArrayList<Flight>();
        }
        if (f != null && !this.flights.contains(f)) {
            this.flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (this.flights != null) {
            this.flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null) {
            return false;
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        String depStr = sdf.format(f.getDepartureTime());
        String arrStr = sdf.format(f.getArrivalTime());
        try {
            Date parsedDep = sdf.parse(depStr);
            Date parsedArr = sdf.parse(arrStr);
            if (!parsedDep.equals(f.getDepartureTime()) || !parsedArr.equals(f.getArrivalTime())) {
                return false;
            }
        } catch (ParseException e) {
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
            if (f != null && flightId.equals(f.getId())) {
                if (!f.isOpenForBooking()) {
                    return false;
                }
                if (now != null && f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String targetDate = date == null ? null : sdf.format(date);
        for (Flight f : this.flights) {
            if (f == null) {
                continue;
            }
            if (!f.isOpenForBooking()) {
                continue;
            }
            if (origin != null && (f.getDepartureAirport() == null
                    || !origin.equals(f.getDepartureAirport().getId()))) {
                continue;
            }
            if (dest != null && (f.getArrivalAirport() == null
                    || !dest.equals(f.getArrivalAirport().getId()))) {
                continue;
            }
            if (targetDate != null && f.getDepartureTime() != null) {
                String depDate = sdf.format(f.getDepartureTime());
                if (!targetDate.equals(depDate)) {
                    continue;
                }
            }
            result.add(f);
        }
        return result;
    }
}
