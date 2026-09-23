import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.text.SimpleDateFormat;

class Airline {
    private List<Flight> flights = new ArrayList<>();

    public Airline() {
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
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || f.isOpenForBooking() || flights.contains(f) == false && flights.size() >= 0) {
            // Allow publishing if flight is in the list or needs to be added
        }
        if (flights.contains(f) && f.isOpenForBooking()) {
            return false;
        }
        if (!flights.contains(f)) {
            flights.add(f);
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sdf.format(f.getDepartureTime());
            sdf.format(f.getArrivalTime());
        } catch (Exception e) {
            return false;
        }
        if (now == null) {
            now = new Date();
        }
        if (!(now.before(f.getDepartureTime()) && f.getDepartureTime().before(f.getArrivalTime()))) {
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
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        for (Flight f : flights) {
            if (f.getId() != null && f.getId().equals(flightId)) {
                if (!f.isOpenForBooking()) {
                    return false;
                }
                if (now != null && f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
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
            if (f.getDepartureAirport() != null && f.getDepartureAirport().getId() != null
                    && f.getDepartureAirport().getId().equals(origin)
                    && f.getArrivalAirport() != null && f.getArrivalAirport().getId() != null
                    && f.getArrivalAirport().getId().equals(dest)) {
                if (date == null || f.getDepartureTime() == null) {
                    result.add(f);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    if (sdf.format(f.getDepartureTime()).equals(sdf.format(date))) {
                        result.add(f);
                    }
                }
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
    private List<Stopover> stopovers = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    public Flight() {
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

    public void setStopovers(List<Stopover> stopovers) {
        this.stopovers = stopovers;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null) {
            return false;
        }
        if (stop.getDepartureTime() == null || stop.getArrivalTime() == null) {
            return false;
        }
        if (!stop.getArrivalTime().before(stop.getDepartureTime())) {
            return false;
        }
        if (departureTime != null && stop.getArrivalTime().before(departureTime)) {
            return false;
        }
        if (arrivalTime != null && stop.getDepartureTime().after(arrivalTime)) {
            return false;
        }
        if (!stopovers.contains(stop)) {
            stopovers.add(stop);
        }
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null) {
            return false;
        }
        if (now != null && departureTime != null && !now.before(departureTime)) {
            return false;
        }
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        if (!openForBooking) {
            return confirmed;
        }
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                confirmed.add(r);
            }
        }
        return confirmed;
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
    private List<City> servesForCities = new ArrayList<>();

    public Airport() {
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
        if (c != null && !servesForCities.contains(c)) {
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
    private List<Booking> bookings = new ArrayList<>();
    private static AtomicInteger reservationIdCounter = new AtomicInteger(0);

    public Customer() {
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public boolean confirm(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId() != null && r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f == null) {
                        return false;
                    }
                    if (now != null && f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
                        return false;
                    }
                    if (!f.isOpenForBooking()) {
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
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId() != null && r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f == null) {
                        return false;
                    }
                    if (now != null && f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
                        return false;
                    }
                    if (!f.isOpenForBooking()) {
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
        if (f == null || listOfPassengerNames == null || listOfPassengerNames.isEmpty()) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (now == null) {
            now = new Date();
        }
        if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
            return false;
        }
        Set<String> existingPassengerNames = new HashSet<>();
        for (Reservation r : f.getReservations()) {
            if (r.getPassenger() != null && r.getPassenger().getName() != null) {
                existingPassengerNames.add(r.getPassenger().getName());
            }
        }
        Set<String> newNames = new HashSet<>();
        for (String name : listOfPassengerNames) {
            if (name == null) {
                return false;
            }
            if (existingPassengerNames.contains(name) || !newNames.add(name)) {
                return false;
            }
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        List<Reservation> reservations = new ArrayList<>();
        for (String name : listOfPassengerNames) {
            Reservation r = new Reservation();
            r.setId("R" + reservationIdCounter.incrementAndGet());
            Passenger p = new Passenger();
            p.setName(name);
            r.setPassenger(p);
            r.setFlight(f);
            r.setStatus(ReservationStatus.PENDING);
            reservations.add(r);
        }
        booking.setReservations(reservations);
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
    private List<Reservation> reservations = new ArrayList<>();

    public Booking() {
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
        if (f == null || passenger == null) {
            return false;
        }
        if (now != null && f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
            return false;
        }
        for (Reservation r : f.getReservations()) {
            if (r.getPassenger() != null && passenger.equals(r.getPassenger().getName())) {
                return false;
            }
        }
        Reservation r = new Reservation();
        r.setId("R" + System.nanoTime());
        Passenger p = new Passenger();
        p.setName(passenger);
        r.setPassenger(p);
        r.setFlight(f);
        r.setStatus(ReservationStatus.PENDING);
        reservations.add(r);
        f.getReservations().add(r);
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
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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