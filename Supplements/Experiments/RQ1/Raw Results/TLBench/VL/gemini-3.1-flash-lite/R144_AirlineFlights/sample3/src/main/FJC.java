import java.util.*;

enum ReservationStatus {
    PENDING, CONFIRMED, CANCELED
}

class City {
    public City() {}
}

class Airport {
    private String id;
    private List<City> servesForCities = new ArrayList<>();

    public Airport() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<City> getServesForCities() { return servesForCities; }
    public void setServesForCities(List<City> servesForCities) { this.servesForCities = servesForCities; }
    public void addCity(City c) { this.servesForCities.add(c); }
}

class Stopover {
    private Date departureTime;
    private Date arrivalTime;
    private Airport airport;

    public Stopover() {}

    public Date getDepartureTime() { return departureTime; }
    public void setDepartureTime(Date departureTime) { this.departureTime = departureTime; }
    public Date getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Date arrivalTime) { this.arrivalTime = arrivalTime; }
    public Airport getAirport() { return airport; }
    public void setAirport(Airport airport) { this.airport = airport; }
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

    public Flight() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public boolean isOpenForBooking() { return openForBooking; }
    public void setOpenForBooking(boolean openForBooking) { this.openForBooking = openForBooking; }
    public Date getDepartureTime() { return departureTime; }
    public void setDepartureTime(Date departureTime) { this.departureTime = departureTime; }
    public Date getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Date arrivalTime) { this.arrivalTime = arrivalTime; }
    public Airport getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(Airport departureAirport) { this.departureAirport = departureAirport; }
    public Airport getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(Airport arrivalAirport) { this.arrivalAirport = arrivalAirport; }
    public List<Stopover> getStopovers() { return stopovers; }
    public void setStopovers(List<Stopover> stopovers) { this.stopovers = stopovers; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public boolean addStopover(Stopover stop, Date now) { return false; }
    public boolean removeStopover(Stopover stop, Date now) { return false; }
    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        if (this.openForBooking) {
            for (Reservation r : reservations) {
                if (r.getStatus() == ReservationStatus.CONFIRMED) confirmed.add(r);
            }
        }
        return confirmed;
    }
}

class Reservation {
    private String id;
    private ReservationStatus status;
    private Passenger passenger;
    private Flight flight;

    public Reservation() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }
    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }
}

class Passenger {
    private String name;

    public Passenger() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

class Booking {
    private Customer customer;
    private List<Reservation> reservations = new ArrayList<>();

    public Booking() {}

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
    public boolean createReservation(Flight f, String passengerName, Date now) { return false; }
}

class Customer {
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    public boolean confirm(String reservationID, Date now) { return false; }
    public boolean cancel(String reservationID, Date now) { return false; }
    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) { return false; }
}

class Airline {
    private List<Flight> flights = new ArrayList<>();

    public Airline() {}

    public List<Flight> getFlights() { return flights; }
    public void setFlights(List<Flight> flights) { this.flights = flights; }
    public void addFlight(Flight f) { this.flights.add(f); }
    public void removeFlight(Flight f) { this.flights.remove(f); }
    public boolean publishFlight(Flight f, Date now) {
        if (f.isOpenForBooking() || f.getDepartureAirport() == null || f.getArrivalAirport() == null) return false;
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) return false;
        if (f.getDepartureTime().after(now) && f.getArrivalTime().after(f.getDepartureTime())) {
            f.setOpenForBooking(true);
            return true;
        }
        return false;
    }
    public boolean closeFlight(String flightId, Date now) {
        for (Flight f : flights) {
            if (f.getId().equals(flightId) && f.isOpenForBooking() && f.getDepartureTime().after(now)) {
                f.setOpenForBooking(false);
                for (Reservation r : f.getReservations()) {
                    if (r.getStatus() == ReservationStatus.CONFIRMED) r.setStatus(ReservationStatus.CANCELED);
                }
                return true;
            }
        }
        return false;
    }
    public List<Flight> searchFlights(String origin, String dest, Date date) { return new ArrayList<>(); }
}