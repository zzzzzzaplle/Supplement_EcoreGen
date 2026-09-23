import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
        if (f != null && !this.flights.contains(f)) {
            this.flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        this.flights.remove(f);
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
        if (!(now.before(f.getDepartureTime()) && f.getDepartureTime().before(f.getArrivalTime()))) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (Objects.equals(f.getDepartureAirport().getId(), f.getArrivalAirport().getId())) {
            return false;
        }

        f.setOpenForBooking(true);
        if (!this.flights.contains(f)) {
            this.flights.add(f);
        }
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }
        for (Flight flight : flights) {
            if (flight != null && Objects.equals(flight.getId(), flightId)) {
                if (!flight.isOpenForBooking()) {
                    return false;
                }
                if (flight.getDepartureTime() == null || !now.before(flight.getDepartureTime())) {
                    return false;
                }
                flight.setOpenForBooking(false);
                for (Reservation reservation : flight.getReservations()) {
                    if (reservation != null && reservation.getStatus() == ReservationStatus.CONFIRMED) {
                        reservation.setStatus(ReservationStatus.CANCELED);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<>();
        if (origin == null || dest == null || date == null) {
            return result;
        }
        for (Flight flight : flights) {
            if (flight == null || flight.getDepartureAirport() == null || flight.getArrivalAirport() == null || flight.getDepartureTime() == null) {
                continue;
            }
            if (Objects.equals(flight.getDepartureAirport().getId(), origin)
                    && Objects.equals(flight.getArrivalAirport().getId(), dest)
                    && isSameDay(flight.getDepartureTime(), date)) {
                result.add(flight);
            }
        }
        return result;
    }

    private boolean isSameDay(Date a, Date b) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(a).equals(sdf.format(b));
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
        if (stop == null || now == null || !now.before(getDepartureTime())) {
            return false;
        }
        if (stop.getAirport() == null || stop.getDepartureTime() == null || stop.getArrivalTime() == null) {
            return false;
        }
        if (!stop.getDepartureTime().before(stop.getArrivalTime())) {
            return false;
        }
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        if (stop.getDepartureTime().before(departureTime) || stop.getArrivalTime().after(arrivalTime)) {
            return false;
        }
        this.stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null || !now.before(getDepartureTime())) {
            return false;
        }
        return this.stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation != null && reservation.getStatus() == ReservationStatus.CONFIRMED) {
                result.add(reservation);
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
        if (c != null) {
            this.servesForCities.add(c);
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
        if (reservationID == null || now == null) {
            return false;
        }
        for (Booking booking : bookings) {
            if (booking == null) {
                continue;
            }
            for (Reservation reservation : booking.getReservations()) {
                if (reservation != null && Objects.equals(reservation.getId(), reservationID)) {
                    Flight flight = reservation.getFlight();
                    if (flight == null || flight.getDepartureTime() == null) {
                        return false;
                    }
                    if (!flight.isOpenForBooking() || !now.before(flight.getDepartureTime())) {
                        return false;
                    }
                    if (reservation.getStatus() == ReservationStatus.CANCELED) {
                        return false;
                    }
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) {
            return false;
        }
        for (Booking booking : bookings) {
            if (booking == null) {
                continue;
            }
            for (Reservation reservation : booking.getReservations()) {
                if (reservation != null && Objects.equals(reservation.getId(), reservationID)) {
                    Flight flight = reservation.getFlight();
                    if (flight == null || flight.getDepartureTime() == null) {
                        return false;
                    }
                    if (!flight.isOpenForBooking() || !now.before(flight.getDepartureTime())) {
                        return false;
                    }
                    reservation.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null || listOfPassengerNames.isEmpty()) {
            return false;
        }
        if (!f.isOpenForBooking() || f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
            return false;
        }

        List<String> seen = new ArrayList<>();
        for (String name : listOfPassengerNames) {
            if (name == null || seen.contains(name)) {
                return false;
            }
            seen.add(name);
        }

        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String passengerName : listOfPassengerNames) {
            if (!booking.createReservation(f, passengerName, now)) {
                return false;
            }
        }
        this.bookings.add(booking);
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
        if (f == null || passenger == null || now == null) {
            return false;
        }
        if (!f.isOpenForBooking() || f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
            return false;
        }
        for (Reservation existing : f.getReservations()) {
            if (existing != null && existing.getPassenger() != null && passenger.equals(existing.getPassenger().getName())) {
                return false;
            }
        }
        Passenger p = new Passenger();
        p.setName(passenger);

        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setPassenger(p);
        reservation.setFlight(f);
        reservation.setStatus(ReservationStatus.PENDING);

        this.reservations.add(reservation);
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