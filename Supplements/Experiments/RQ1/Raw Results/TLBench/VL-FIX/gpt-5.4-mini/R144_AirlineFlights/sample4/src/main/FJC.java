import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Single-file implementation of the airline booking domain model.
 * All classes are declared without a package, as required.
 */
 class AirlineSystem {

    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static Date parseDate(String value) {
        if (value == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            sdf.setLenient(false);
            return sdf.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean sameInstant(Date a, Date b) {
        return a != null && b != null && a.getTime() == b.getTime();
    }
}

class Airline {
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
        if (f != null && this.flights != null) {
            this.flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (this.flights != null) {
            this.flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) {
            return false;
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }
        if (!f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (Objects.equals(f.getDepartureAirport().getId(), f.getArrivalAirport().getId())) {
            return false;
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null || flights == null) {
            return false;
        }
        for (Flight f : flights) {
            if (f != null && flightId.equals(f.getId())) {
                if (!f.isOpenForBooking()) {
                    return false;
                }
                if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
                    return false;
                }
                f.setOpenForBooking(false);
                for (Reservation r : f.getReservations()) {
                    if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
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
        if (flights == null) {
            return result;
        }
        for (Flight f : flights) {
            if (f == null) {
                continue;
            }
            boolean matchesOrigin = origin == null || (f.getDepartureAirport() != null && origin.equals(f.getDepartureAirport().getId()));
            boolean matchesDest = dest == null || (f.getArrivalAirport() != null && dest.equals(f.getArrivalAirport().getId()));
            boolean matchesDate = true;
            if (date != null && f.getDepartureTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                matchesDate = sdf.format(date).equals(sdf.format(f.getDepartureTime()));
            }
            if (matchesOrigin && matchesDest && matchesDate) {
                result.add(f);
            }
        }
        return result;
    }
}

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
        this.openForBooking = false;
        this.stopovers = new ArrayList<>();
        this.reservations = new ArrayList<>();
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
        if (stop == null || now == null || !openForBooking || departureTime == null || arrivalTime == null) {
            return false;
        }
        if (!now.before(departureTime)) {
            return false;
        }
        if (stop.getAirport() == null || stop.getDepartureTime() == null || stop.getArrivalTime() == null) {
            return false;
        }
        if (!stop.getDepartureTime().before(stop.getArrivalTime())) {
            return false;
        }
        if (stop.getDepartureTime().before(departureTime) || stop.getArrivalTime().after(arrivalTime)) {
            return false;
        }
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null || !openForBooking || departureTime == null) {
            return false;
        }
        if (!now.before(departureTime)) {
            return false;
        }
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                result.add(r);
            }
        }
        return result;
    }
}

class Stopover {
    private Date departureTime;
    private Date arrivalTime;
    private Airport airport;

    public Stopover() {
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date d) {
        this.departureTime = d;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date d) {
        this.arrivalTime = d;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport a) {
        this.airport = a;
    }
}

class Airport {
    private String id;
    private List<City> servesForCities;

    public Airport() {
        this.servesForCities = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<City> getCities() {
        return servesForCities;
    }

    public void setCities(List<City> cities) {
        this.servesForCities = cities;
    }

    public void addCity(City c) {
        if (c != null && servesForCities != null) {
            servesForCities.add(c);
        }
    }
}

class City {
    private String name;

    public City() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class Customer {
    private List<Booking> bookings;

    public Customer() {
        this.bookings = new ArrayList<>();
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public boolean confirm(String reservationID, Date now) {
        if (reservationID == null || now == null || bookings == null) {
            return false;
        }
        for (Booking b : bookings) {
            if (b == null || b.getReservations() == null) {
                continue;
            }
            for (Reservation r : b.getReservations()) {
                if (r != null && reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null || !f.isOpenForBooking() || f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
                        return false;
                    }
                    r.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null || bookings == null) {
            return false;
        }
        for (Booking b : bookings) {
            if (b == null || b.getReservations() == null) {
                continue;
            }
            for (Reservation r : b.getReservations()) {
                if (r != null && reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null || !f.isOpenForBooking() || f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
                        return false;
                    }
                    r.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null || bookings == null) {
            return false;
        }
        if (!f.isOpenForBooking() || f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
            return false;
        }
        Set<String> seen = new HashSet<>();
        for (String p : listOfPassengerNames) {
            if (p == null || !seen.add(p)) {
                return false;
            }
            for (Reservation existing : f.getReservations()) {
                if (existing != null && existing.getPassenger() != null && p.equals(existing.getPassenger().getName())) {
                    return false;
                }
            }
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setReservations(new ArrayList<>());
        for (String p : listOfPassengerNames) {
            if (!booking.createReservation(f, p, now)) {
                return false;
            }
        }
        bookings.add(booking);
        return true;
    }
}

class Passenger {
    private String name;

    public Passenger() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class Booking {
    private Customer customer;
    private List<Reservation> reservations;

    public Booking() {
        this.reservations = new ArrayList<>();
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public boolean createReservation(Flight f, String passenger, Date now) {
        if (f == null || passenger == null || now == null || reservations == null) {
            return false;
        }
        if (!f.isOpenForBooking() || f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
            return false;
        }
        for (Reservation r : f.getReservations()) {
            if (r != null && r.getPassenger() != null && passenger.equals(r.getPassenger().getName())) {
                return false;
            }
        }
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setStatus(ReservationStatus.PENDING);
        Passenger p = new Passenger();
        p.setName(passenger);
        reservation.setPassenger(p);
        reservation.setFlight(f);
        reservations.add(reservation);
        f.getReservations().add(reservation);
        return true;
    }
}

enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELED
}

class Reservation {
    private String id;
    private ReservationStatus status;
    private Passenger passenger;
    private Flight flight;

    public Reservation() {
        this.status = ReservationStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus s) {
        this.status = s;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
}