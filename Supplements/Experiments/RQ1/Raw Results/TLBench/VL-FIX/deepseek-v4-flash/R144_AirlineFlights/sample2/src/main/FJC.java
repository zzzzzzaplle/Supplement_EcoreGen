import java.util.*;
import java.text.*;

// ====== City ======
class City {
    // No fields specified in design model; placeholder
    public City() {}
}

// ====== Airport ======
class Airport {
    private String id;
    private List<City> servesForCities = new ArrayList<>();

    public Airport() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<City> getCities() { return servesForCities; }
    public void setCities(List<City> cities) { this.servesForCities = cities; }

    public void addCity(City c) {
        servesForCities.add(c);
    }
}

// ====== Stopover ======
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

// ====== Passenger ======
class Passenger {
    private String name;

    public Passenger() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

// ====== ReservationStatus ======
enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELED
}

// ====== Reservation ======
class Reservation {
    private String id;
    private ReservationStatus status;
    private Passenger passenger;
    private Flight flight;

    public Reservation() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus s) { this.status = s; }

    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }
}

// ====== Flight ======
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
        // can only add stopover if flight not yet published (openForBooking false) and temporal constraints
        if (openForBooking) return false;
        if (stop.getDepartureTime().before(departureTime) || stop.getArrivalTime().after(arrivalTime))
            return false;
        if (stop.getDepartureTime().after(stop.getArrivalTime()))
            return false;
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (openForBooking) return false;
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

// ====== Booking ======
class Booking {
    private Customer customer;
    private List<Reservation> reservations = new ArrayList<>();

    public Booking() {}

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public boolean createReservation(Flight f, String passengerName, Date now) {
        // check flight is open and not departed
        if (!f.isOpenForBooking()) return false;
        if (!now.before(f.getDepartureTime())) return false;
        // check duplicate passenger on flight
        for (Reservation r : f.getReservations()) {
            if (r.getPassenger().getName().equals(passengerName))
                return false;
        }
        // generate unique reservation id (simple timestamp + random)
        String resId = "RES" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        Reservation res = new Reservation();
        res.setId(resId);
        res.setStatus(ReservationStatus.PENDING);
        Passenger p = new Passenger();
        p.setName(passengerName);
        res.setPassenger(p);
        res.setFlight(f);
        // add to both booking and flight
        reservations.add(res);
        f.getReservations().add(res);
        return true;
    }
}

// ====== Customer ======
class Customer {
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public boolean confirm(String reservationID, Date now) {
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (!f.isOpenForBooking()) return false;
                    if (!now.before(f.getDepartureTime())) return false;
                    if (r.getStatus() != ReservationStatus.PENDING) return false;
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
                if (r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (!f.isOpenForBooking()) return false;
                    if (!now.before(f.getDepartureTime())) return false;
                    if (r.getStatus() != ReservationStatus.PENDING) return false;
                    r.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        // check no duplicates on flight
        Set<String> existing = new HashSet<>();
        for (Reservation r : f.getReservations()) {
            existing.add(r.getPassenger().getName());
        }
        Set<String> newNames = new HashSet<>(listOfPassengerNames);
        if (newNames.size() != listOfPassengerNames.size()) return false; // duplicate in input
        for (String name : listOfPassengerNames) {
            if (existing.contains(name)) return false;
        }
        // check flight open and not departed
        if (!f.isOpenForBooking()) return false;
        if (!now.before(f.getDepartureTime())) return false;

        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String name : listOfPassengerNames) {
            boolean success = booking.createReservation(f, name, now);
            if (!success) return false;
        }
        bookings.add(booking);
        return true;
    }
}

// ====== Airline ======
class Airline {
    private List<Flight> flights = new ArrayList<>();

    public Airline() {}

    public List<Flight> getFlights() { return flights; }
    public void setFlights(List<Flight> flights) { this.flights = flights; }

    public void addFlight(Flight f) {
        flights.add(f);
    }

    public void removeFlight(Flight f) {
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        // check flight not already published
        if (f.isOpenForBooking()) return false;
        // validate timestamps
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) return false;
        if (!now.before(f.getDepartureTime())) return false;
        if (!f.getDepartureTime().before(f.getArrivalTime())) return false;
        // route integrity
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) return false;
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) return false;
        // set open for booking
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                if (!f.isOpenForBooking()) return false;
                if (!now.before(f.getDepartureTime())) return false;
                f.setOpenForBooking(false);
                // cancel all confirmed reservations
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
            if (!f.isOpenForBooking()) continue;
            if (f.getDepartureAirport() != null && f.getDepartureAirport().getId().equals(origin) &&
                f.getArrivalAirport() != null && f.getArrivalAirport().getId().equals(dest)) {
                // check date (same day)
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(f.getDepartureTime());
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(date);
                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
                    result.add(f);
                }
            }
        }
        return result;
    }

    // utility to retrieve all confirmed reservations for a flight (requirement 5)
    public List<Reservation> retrieveConfirmedReservations(Flight f) {
        if (!f.isOpenForBooking()) return new ArrayList<>();
        return f.getConfirmedReservations();
    }
}