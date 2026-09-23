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
        if (flights == null) {
            flights = new ArrayList<>();
        }
        flights.add(f);
    }

    public void removeFlight(Flight f) {
        if (flights != null) {
            flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) return false;

        // Check if flight already published
        if (f.isOpenForBooking()) return false;

        // Validate timestamps
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sdf.format(f.getDepartureTime());
            sdf.format(f.getArrivalTime());
        } catch (Exception e) {
            return false;
        }

        // Temporal consistency: currentTime < departureTime < arrivalTime
        if (!now.before(f.getDepartureTime())) return false;
        if (!f.getDepartureTime().before(f.getArrivalTime())) return false;

        // Route integrity: departureAirport != arrivalAirport
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) return false;
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) return false;

        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) return false;

        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                if (!f.isOpenForBooking()) return false;
                if (now.after(f.getDepartureTime())) return false;

                f.setOpenForBooking(false);

                // Cancel every confirmed reservation
                List<Reservation> confirmedReservations = new ArrayList<>();
                for (Reservation r : f.getReservations()) {
                    if (r.getStatus() == ReservationStatus.CONFIRMED) {
                        confirmedReservations.add(r);
                    }
                }
                for (Reservation r : confirmedReservations) {
                    r.setStatus(ReservationStatus.CANCELED);
                }

                return true;
            }
        }
        return false;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<>();
        if (origin == null || dest == null || date == null) return result;

        for (Flight f : flights) {
            if (f.isOpenForBooking() &&
                f.getDepartureAirport().getId().equals(origin) &&
                f.getArrivalAirport().getId().equals(dest)) {
                // Check if the departure date matches (same day)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String flightDate = sdf.format(f.getDepartureTime());
                String searchDate = sdf.format(date);
                if (flightDate.equals(searchDate)) {
                    result.add(f);
                }
            }
        }
        return result;
    }
}
