import java.util.*;
import java.text.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        if (f != null) {
            flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) return false;
        if (f.isOpenForBooking()) return false;
        if (!flights.contains(f)) return false;
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) return false;
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) return false;
        if (f.getDepartureAirport().equals(f.getArrivalAirport())) return false;
        if (!now.before(f.getDepartureTime())) return false;
        if (!f.getDepartureTime().before(f.getArrivalTime())) return false;
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null) return false;
        for (Flight f : flights) {
            if (flightId.equals(f.getId())) {
                if (!f.isOpenForBooking()) return false;
                if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) return false;
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
        if (origin == null || dest == null || date == null) return result;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String targetDate = sdf.format(date);
        for (Flight f : flights) {
            if (!f.isOpenForBooking()) continue;
            if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) continue;
            if (!origin.equals(f.getDepartureAirport().getId())) continue;
            if (!dest.equals(f.getArrivalAirport().getId())) continue;
            if (f.getDepartureTime() == null) continue;
            if (!targetDate.equals(sdf.format(f.getDepartureTime()))) continue;
            result.add(f);
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

    public boolean isOpenForBooking() {
        return openForBooking;
    }

    public void setOpenForBooking(boolean openForBooking) {
        this.openForBooking = openForBooking;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
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
        if (stop == null || stop.getAirport() == null) return false;
        if (stop.getArrivalTime() == null || stop.getDepartureTime() == null) return false;
        if (!stop.getArrivalTime().before(stop.getDepartureTime())) return false;
        if (stopovers.contains(stop)) return false;
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null) return false;
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> result = new ArrayList<>();
        if (!openForBooking) return result;
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
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

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
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
    public City() {
    }
}

class Customer {
    private List<Booking> bookings = new ArrayList<>();
    private static final AtomicInteger RESERVATION_COUNTER = new AtomicInteger(0);

    public Customer() {
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public boolean confirm(String reservationID, Date now) {
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) return false;
                    if (!f.isOpenForBooking()) return false;
                    if (f.getDepartureTime() == null) return false;
                    if (!now.before(f.getDepartureTime())) return false;
                    r.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) return false;
                    if (!f.isOpenForBooking()) return false;
                    if (f.getDepartureTime() == null) return false;
                    if (!now.before(f.getDepartureTime())) return false;
                    r.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null) return false;
        if (!f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null) return false;
        if (!now.before(f.getDepartureTime())) return false;
        Set<String> existing = new HashSet<>();
        for (Reservation r : f.getReservations()) {
            if (r.getPassenger() != null) {
                existing.add(r.getPassenger().getName());
            }
        }
        for (String name : listOfPassengerNames) {
            if (name == null) return false;
            if (existing.contains(name)) return false;
            existing.add(name);
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        List<Reservation> reservations = new ArrayList<>();
        for (String name : listOfPassengerNames) {
            Reservation r = new Reservation();
            r.setId("RES-" + RESERVATION_COUNTER.incrementAndGet());
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
        if (f == null || passenger == null || now == null) return false;
        if (!f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null) return false;
        if (!now.before(f.getDepartureTime())) return false;
        for (Reservation r : f.getReservations()) {
            if (r.getPassenger() != null && passenger.equals(r.getPassenger().getName())) {
                return false;
            }
        }
        Reservation r = new Reservation();
        r.setId("RES-" + System.nanoTime());
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

    public void setStatus(ReservationStatus status) {
        this.status = status;
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