import java.util.ArrayList;
import java.util.Calendar;
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
        if (f == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        Date departureTime = f.getDepartureTime();
        Date arrivalTime = f.getArrivalTime();
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        if (now == null) {
            return false;
        }
        if (!isValidTimestampFormat(departureTime) || !isValidTimestampFormat(arrivalTime)) {
            return false;
        }
        if (now.getTime() >= departureTime.getTime()) {
            return false;
        }
        if (departureTime.getTime() >= arrivalTime.getTime()) {
            return false;
        }
        Airport departureAirport = f.getDepartureAirport();
        Airport arrivalAirport = f.getArrivalAirport();
        if (departureAirport == null || arrivalAirport == null) {
            return false;
        }
        if (departureAirport.getId() != null && departureAirport.getId().equals(arrivalAirport.getId())) {
            return false;
        }
        return f.publish(now);
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }
        Flight f = findFlightById(flightId);
        if (f == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        return f.closeFlight(now);
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<>();
        if (origin == null || dest == null || date == null) {
            return result;
        }
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        int dateYear = calDate.get(Calendar.YEAR);
        int dateMonth = calDate.get(Calendar.MONTH);
        int dateDay = calDate.get(Calendar.DAY_OF_MONTH);
        for (Flight f : flights) {
            if (f != null) {
                Airport dep = f.getDepartureAirport();
                Airport arr = f.getArrivalAirport();
                if (dep != null && arr != null) {
                    boolean originMatches = false;
                    boolean destMatches = false;
                    for (City c : dep.getCities()) {
                        if (c != null && origin.equalsIgnoreCase(c.getName())) {
                            originMatches = true;
                            break;
                        }
                    }
                    for (City c : arr.getCities()) {
                        if (c != null && dest.equalsIgnoreCase(c.getName())) {
                            destMatches = true;
                            break;
                        }
                    }
                    if (originMatches && destMatches) {
                        if (f.getDepartureTime() != null) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(f.getDepartureTime());
                            if (cal.get(Calendar.YEAR) == dateYear &&
                                cal.get(Calendar.MONTH) == dateMonth &&
                                cal.get(Calendar.DAY_OF_MONTH) == dateDay) {
                                result.add(f);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private Flight findFlightById(String flightId) {
        for (Flight f : flights) {
            if (f != null && f.getId() != null && f.getId().equals(flightId)) {
                return f;
            }
        }
        return null;
    }

    private boolean isValidTimestampFormat(Date d) {
        return d != null;
    }
}
