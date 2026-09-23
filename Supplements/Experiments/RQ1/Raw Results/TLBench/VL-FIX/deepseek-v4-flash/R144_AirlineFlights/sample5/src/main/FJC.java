import java.util.*;
import java.text.*;

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
        flights.add(f);
    }

    public void removeFlight(Flight f) {
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) return false;
        if (f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) return false;
        if (!now.before(f.getDepartureTime())) return false;
        if (!f.getDepartureTime().before(f.getArrivalTime())) return false;
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) return false;
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) return false;
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) return false;
        for (Flight f : flights) {
            if (f.getId() != null && f.getId().equals(flightId)) {
                if (!f.isOpenForBooking()) return false;
                if (now.after(f.getDepartureTime()) || now.equals(f.getDepartureTime())) return false;
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
        for (Flight f : flights) {
            if (f.isOpenForBooking() && f.getDepartureAirport() != null && f.getArrivalAirport() != null) {
                if (f.getDepartureAirport().getId().equals(origin) && f.getArrivalAirport().getId().equals(dest)) {
                    if (isSameDay(f.getDepartureTime(), date)) {
                        result.add(f);
                    }
                }
            }
        }
        return result;
    }

    private boolean isSameDay(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public boolean isOpenForBooking() { return openForBooking; }
    public void setOpenForBooking(boolean openForBooking) { this.openForBooking = openForBooking; }

    public Date getDepartureTime() { return departureTime; }
    public void setDepartureTime(Date t) { this.departureTime = t; }

    public Date getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Date t) { this.arrivalTime = t; }

    public Airport getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(Airport D) { this.departureAirport = D; }

    public Airport getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(Airport A) { this.arrivalAirport = A; }

    public List<Stopover> getStopovers() { return stopovers; }
    public void setStopovers(List<Stopover> stopovers) { this.stopovers = stopovers; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null || now == null) return false;
        if (openForBooking) return false;
        if (now.after(departureTime) || now.equals(departureTime)) return false;
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null) return false;
        if (openForBooking) return false;
        if (now.after(departureTime) || now.equals(departureTime)) return false;
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        if (!openForBooking) return confirmed;
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

    public Stopover() {}

    public Date getDepartureTime() { return departureTime; }
    public void setDepartureTime(Date d) { this.departureTime = d; }

    public Date getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Date d) { this.arrivalTime = d; }

    public Airport getAirport() { return airport; }
    public void setAirport(Airport a) { this.airport = a; }
}

class Airport {
    private String id;
    private List<City> servesForCities;

    public Airport() {
        this.servesForCities = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<City> getCities() { return servesForCities; }
    public void setCities(List<City> cities) { this.servesForCities = cities; }

    public void addCity(City c) {
        servesForCities.add(c);
    }
}

class City {
    private String name;

    public City() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

class Customer {
    private List<Booking> bookings;

    public Customer() {
        this.bookings = new ArrayList<>();
    }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public boolean confirm(String reservationID, Date now) {
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId() != null && r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f == null) return false;
                    if (!f.isOpenForBooking()) return false;
                    if (now.after(f.getDepartureTime()) || now.equals(f.getDepartureTime())) return false;
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
                if (r.getId() != null && r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f == null) return false;
                    if (!f.isOpenForBooking()) return false;
                    if (now.after(f.getDepartureTime()) || now.equals(f.getDepartureTime())) return false;
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
        if (now.after(f.getDepartureTime()) || now.equals(f.getDepartureTime())) return false;

        // Check for duplicate passenger names on the same flight
        Set<String> existingNames = new HashSet<>();
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getFlight() != null && r.getFlight().getId() != null && r.getFlight().getId().equals(f.getId())) {
                    if (r.getPassenger() != null && r.getPassenger().getName() != null) {
                        existingNames.add(r.getPassenger().getName());
                    }
                }
            }
        }
        for (String name : listOfPassengerNames) {
            if (existingNames.contains(name)) {
                return false;
            }
        }

        Booking newBooking = new Booking();
        newBooking.setCustomer(this);
        for (String passengerName : listOfPassengerNames) {
            Passenger p = new Passenger();
            p.setName(passengerName);
            Reservation res = new Reservation();
            res.setId(UUID.randomUUID().toString());
            res.setStatus(ReservationStatus.PENDING);
            res.setPassenger(p);
            res.setFlight(f);
            newBooking.getReservations().add(res);
            f.getReservations().add(res);
        }
        bookings.add(newBooking);
        return true;
    }
}

class Passenger {
    private String name;

    public Passenger() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

class Booking {
    private Customer customer;
    private List<Reservation> reservations;

    public Booking() {
        this.reservations = new ArrayList<>();
    }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public boolean createReservation(Flight f, String passenger, Date now) {
        if (f == null || passenger == null || now == null) return false;
        if (!f.isOpenForBooking()) return false;
        if (now.after(f.getDepartureTime()) || now.equals(f.getDepartureTime())) return false;

        // Check for duplicate passenger
        for (Reservation r : f.getReservations()) {
            if (r.getPassenger() != null && passenger.equals(r.getPassenger().getName())) {
                return false;
            }
        }

        Passenger p = new Passenger();
        p.setName(passenger);
        Reservation res = new Reservation();
        res.setId(UUID.randomUUID().toString());
        res.setStatus(ReservationStatus.PENDING);
        res.setPassenger(p);
        res.setFlight(f);
        reservations.add(res);
        f.getReservations().add(res);
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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus s) { this.status = s; }

    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }
}