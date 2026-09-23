import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

// ============================================================
// Enumerations
// ============================================================

enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELED
}

// ============================================================
// City class
// ============================================================

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

// ============================================================
// Airport class
// ============================================================

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
        if (servesForCities == null) {
            servesForCities = new ArrayList<>();
        }
        if (!servesForCities.contains(c)) {
            servesForCities.add(c);
        }
    }
}

// ============================================================
// Passenger class
// ============================================================

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

// ============================================================
// Stopover class
// ============================================================

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

// ============================================================
// Flight class
// ============================================================

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

    // Getters and Setters
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

    // Key operations
    public boolean addStopover(Stopover stop, Date now) {
        if (openForBooking || now.after(departureTime)) {
            return false;
        }
        if (!stopovers.contains(stop)) {
            stopovers.add(stop);
        }
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (openForBooking || now.after(departureTime)) {
            return false;
        }
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                confirmed.add(r);
            }
        }
        return confirmed;
    }
}

// ============================================================
// Reservation class
// ============================================================

class Reservation {
    private String id;
    private ReservationStatus status;
    private Passenger passenger;
    private Flight flight;

    public Reservation() {
        this.id = UUID.randomUUID().toString();
        this.status = ReservationStatus.PENDING;
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

// ============================================================
// Booking class
// ============================================================

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

    // Key operations
    public boolean createReservation(Flight flight, String passengerName, Date now) {
        if (now.after(flight.getDepartureTime())) {
            return false;
        }
        // Check for duplicate passenger on this flight
        for (Reservation existing : flight.getReservations()) {
            if (existing.getPassenger().getName().equals(passengerName)) {
                return false;
            }
        }
        Passenger passenger = new Passenger();
        passenger.setName(passengerName);

        Reservation reservation = new Reservation();
        reservation.setPassenger(passenger);
        reservation.setFlight(flight);
        reservation.setStatus(ReservationStatus.PENDING);

        reservations.add(reservation);
        flight.getReservations().add(reservation);
        return true;
    }
}

// ============================================================
// Customer class
// ============================================================

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

    // Key operations
    public boolean confirm(String reservationID, Date now) {
        for (Booking booking : bookings) {
            for (Reservation reservation : booking.getReservations()) {
                if (reservation.getId().equals(reservationID)) {
                    Flight flight = reservation.getFlight();
                    if (now.after(flight.getDepartureTime()) || !flight.isOpenForBooking()) {
                        return false;
                    }
                    if (reservation.getStatus() == ReservationStatus.PENDING) {
                        reservation.setStatus(ReservationStatus.CONFIRMED);
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Booking booking : bookings) {
            for (Reservation reservation : booking.getReservations()) {
                if (reservation.getId().equals(reservationID)) {
                    Flight flight = reservation.getFlight();
                    if (now.after(flight.getDepartureTime()) || !flight.isOpenForBooking()) {
                        return false;
                    }
                    if (reservation.getStatus() == ReservationStatus.PENDING || reservation.getStatus() == ReservationStatus.CONFIRMED) {
                        reservation.setStatus(ReservationStatus.CANCELED);
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight flight, Date now, List<String> listOfPassengerNames) {
        if (!flight.isOpenForBooking() || now.after(flight.getDepartureTime())) {
            return false;
        }
        // Check for duplicate passengers on the flight
        for (String passengerName : listOfPassengerNames) {
            for (Reservation existing : flight.getReservations()) {
                if (existing.getPassenger().getName().equals(passengerName)) {
                    return false;
                }
            }
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String passengerName : listOfPassengerNames) {
            if (!booking.createReservation(flight, passengerName, now)) {
                return false;
            }
        }
        bookings.add(booking);
        return true;
    }
}

// ============================================================
// Airline class
// ============================================================

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
        if (!flights.contains(f)) {
            flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        // Validate timestamps format and consistency
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }
        if (!now.before(f.getDepartureTime()) || !f.getDepartureTime().before(f.getArrivalTime())) {
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
        // Check if flight already exists in the system
        for (Flight existing : flights) {
            if (existing.getId().equals(f.getId())) {
                return false;
            }
        }
        f.setOpenForBooking(true);
        flights.add(f);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                if (!f.isOpenForBooking() || now.after(f.getDepartureTime())) {
                    return false;
                }
                f.setOpenForBooking(false);
                // Cancel all confirmed reservations
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Flight f : flights) {
            if (f.isOpenForBooking() &&
                f.getDepartureAirport().getId().equals(origin) &&
                f.getArrivalAirport().getId().equals(dest) &&
                sdf.format(f.getDepartureTime()).equals(sdf.format(date))) {
                result.add(f);
            }
        }
        return result;
    }
}