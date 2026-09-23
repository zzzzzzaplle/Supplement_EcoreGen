import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELED
}

class City {
    public City() {
    }
}

class Passenger {
    private String name;

    public Passenger() {
    }

    public Passenger(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class Airport {
    private String id;
    private List<City> servesForCities;

    public Airport() {
        this.servesForCities = new ArrayList<>();
    }

    public Airport(String id, List<City> servesForCities) {
        this.id = id;
        this.servesForCities = servesForCities != null ? servesForCities : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public List<City> getCities() {
        return servesForCities;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCities(List<City> cities) {
        this.servesForCities = cities != null ? cities : new ArrayList<>();
    }

    public void addCity(City c) {
        if (c != null) {
            servesForCities.add(c);
        }
    }
}

class Stopover {
    private Date departureTime;
    private Date arrivalTime;
    private Airport airport;

    public Stopover() {
    }

    public Stopover(Date departureTime, Date arrivalTime, Airport airport) {
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.airport = airport;
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

class Reservation {
    private String id;
    private ReservationStatus status;
    private Passenger passenger;
    private Flight flight;

    public Reservation() {
        this.id = UUID.randomUUID().toString();
        this.status = ReservationStatus.PENDING;
    }

    public Reservation(String id, ReservationStatus status, Passenger passenger, Flight flight) {
        this.id = id;
        this.status = status;
        this.passenger = passenger;
        this.flight = flight;
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

class Booking {
    private Customer customer;
    private List<Reservation> reservations;

    public Booking() {
        this.reservations = new ArrayList<>();
    }

    public Booking(Customer customer, List<Reservation> reservations) {
        this.customer = customer;
        this.reservations = reservations != null ? reservations : new ArrayList<>();
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
        this.reservations = reservations != null ? reservations : new ArrayList<>();
    }

    public boolean createReservation(Flight f, String passenger, Date now) {
        if (f == null || passenger == null || passenger.trim().isEmpty() || now == null) {
            return false;
        }
        if (!f.isOpenForBooking() || f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
            return false;
        }
        for (Reservation r : reservations) {
            if (r != null && r.getPassenger() != null && passenger.equalsIgnoreCase(r.getPassenger().getName())) {
                return false;
            }
        }
        Reservation reservation = new Reservation();
        Passenger p = new Passenger();
        p.setName(passenger);
        reservation.setPassenger(p);
        reservation.setFlight(f);
        reservation.setStatus(ReservationStatus.PENDING);
        reservations.add(reservation);
        f.getReservations().add(reservation);
        return true;
    }
}

class Customer {
    private List<Booking> bookings;

    public Customer() {
        this.bookings = new ArrayList<>();
    }

    public Customer(List<Booking> bookings) {
        this.bookings = bookings != null ? bookings : new ArrayList<>();
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings != null ? bookings : new ArrayList<>();
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
                if (reservation != null && reservationID.equals(reservation.getId())) {
                    Flight flight = reservation.getFlight();
                    if (flight == null || flight.getDepartureTime() == null) {
                        return false;
                    }
                    if (now.after(flight.getDepartureTime()) || !flight.isOpenForBooking()) {
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
                if (reservation != null && reservationID.equals(reservation.getId())) {
                    Flight flight = reservation.getFlight();
                    if (flight == null || flight.getDepartureTime() == null) {
                        return false;
                    }
                    if (now.after(flight.getDepartureTime()) || !flight.isOpenForBooking()) {
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
            if (name == null || name.trim().isEmpty()) {
                return false;
            }
            String normalized = name.trim().toLowerCase();
            if (seen.contains(normalized)) {
                return false;
            }
            seen.add(normalized);
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String name : listOfPassengerNames) {
            if (!booking.createReservation(f, name.trim(), now)) {
                return false;
            }
        }
        bookings.add(booking);
        return true;
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
        this.stopovers = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.openForBooking = false;
    }

    public Flight(String id, boolean openForBooking, Date departureTime, Date arrivalTime, Airport departureAirport, Airport arrivalAirport, List<Stopover> stopovers, List<Reservation> reservations) {
        this.id = id;
        this.openForBooking = openForBooking;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.stopovers = stopovers != null ? stopovers : new ArrayList<>();
        this.reservations = reservations != null ? reservations : new ArrayList<>();
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

    public void setStopovers(List<Stopover> stopovers) {
        this.stopovers = stopovers != null ? stopovers : new ArrayList<>();
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations != null ? reservations : new ArrayList<>();
    }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null || now == null || !now.before(departureTime) || !now.before(arrivalTime)) {
            return false;
        }
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null || !now.before(departureTime)) {
            return false;
        }
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation != null && reservation.getStatus() == ReservationStatus.CONFIRMED) {
                confirmed.add(reservation);
            }
        }
        return confirmed;
    }
}

class Airline {
    private List<Flight> flights;

    public Airline() {
        this.flights = new ArrayList<>();
    }

    public Airline(List<Flight> flights) {
        this.flights = flights != null ? flights : new ArrayList<>();
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights != null ? flights : new ArrayList<>();
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
        if (f.getId() == null || f.getDepartureTime() == null || f.getArrivalTime() == null ||
                f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        if (!now.before(f.getDepartureTime()) || !f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        if (Objects.equals(f.getDepartureAirport().getId(), f.getArrivalAirport().getId())) {
            return false;
        }
        for (Flight existing : flights) {
            if (existing != null && Objects.equals(existing.getId(), f.getId()) && existing.isOpenForBooking()) {
                return false;
            }
        }
        f.setOpenForBooking(true);
        if (!flights.contains(f)) {
            flights.add(f);
        }
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }
        for (Flight f : flights) {
            if (f != null && flightId.equals(f.getId())) {
                if (!f.isOpenForBooking() || f.getDepartureTime() == null || !now.before(f.getDepartureTime())) {
                    return false;
                }
                f.setOpenForBooking(false);
                for (Reservation reservation : f.getReservations()) {
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
        for (Flight f : flights) {
            if (f == null || f.getDepartureAirport() == null || f.getArrivalAirport() == null || f.getDepartureTime() == null) {
                continue;
            }
            if (origin.equalsIgnoreCase(f.getDepartureAirport().getId())
                    && dest.equalsIgnoreCase(f.getArrivalAirport().getId())
                    && sameDay(f.getDepartureTime(), date)) {
                result.add(f);
            }
        }
        return result;
    }

    private boolean sameDay(Date d1, Date d2) {
        return d1.getYear() == d2.getYear() && d1.getMonth() == d2.getMonth() && d1.getDate() == d2.getDate();
    }
}