import java.util.*;

public class Airline {
    private List<Flight> flights = new ArrayList<>();

    public Airline() {}

    public List<Flight> getFlights() { return flights; }
    public void setFlights(List<Flight> flights) { this.flights = flights; }

    public void addFlight(Flight f) { flights.add(f); }
    public void removeFlight(Flight f) { flights.remove(f); }

    public boolean publishFlight(Flight f, Date now) {
        if (f.isOpenForBooking()) return false;
        if (f.getDepartureTime().before(now) || f.getArrivalTime().before(f.getDepartureTime())) return false;
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) return false;
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                if (!f.isOpenForBooking() || f.getDepartureTime().before(now)) return false;
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_YEAR);

        for (Flight f : flights) {
            Calendar fCal = Calendar.getInstance();
            fCal.setTime(f.getDepartureTime());
            if (f.getDepartureAirport().getId().equals(origin) &&
                f.getArrivalAirport().getId().equals(dest) &&
                fCal.get(Calendar.YEAR) == year && fCal.get(Calendar.DAY_OF_YEAR) == day) {
                result.add(f);
            }
        }
        return result;
    }
}
