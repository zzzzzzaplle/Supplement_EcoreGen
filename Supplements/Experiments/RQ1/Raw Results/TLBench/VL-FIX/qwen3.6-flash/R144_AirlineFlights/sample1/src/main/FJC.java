import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELED
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
        if (this.servesForCities == null) {
            this.servesForCities = new ArrayList<>();
        }
        this.servesForCities.add(c);
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

    public boolean createReservation(Flight f, String passengerName, Date now) {
        if (f == null || passengerName == null || now == null) {
            return false;
        }

        // Check if flight is open
        if (!f.isOpenForBooking()) {
            return false;
        }

        // Check if flight has departed
        if (now.after(f.getDepartureTime())) {
            return false;
        }

        // Check for duplicate passengers on this flight within this booking
        Passenger newPassenger = new Passenger();
        newPassenger.setName(passengerName);

        for (Reservation existingRes : this.reservations) {
            if (existingRes.getFlight().equals(f) && 
                existingRes.getPassenger().getName().equals(passengerName)) {
                return false;
            }
        }

        Reservation reservation = new Reservation();
        reservation.setFlight(f);
        reservation.setPassenger(newPassenger);
        
        f.getReservations().add(reservation);
        this.reservations.add(reservation);

        return true;
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
        return updateReservationStatus(reservationID, ReservationStatus.CONFIRMED, now);
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) {
            return false;
        }
        return updateReservationStatus(reservationID, ReservationStatus.CANCELED, now);
    }

    private boolean updateReservationStatus(String reservationID, ReservationStatus newStatus, Date now) {
        for (Booking booking : this.bookings) {
            for (Reservation reservation : booking.getReservations()) {
                if (reservation.getId().equals(reservationID)) {
                    Flight flight = reservation.getFlight();
                    
                    // Check that flight has not yet departed
                    if (now.after(flight.getDepartureTime())) {
                        return false;
                    }
                    
                    // Check that flight is still open for booking
                    if (!flight.isOpenForBooking()) {
                        return false;
                    }

                    reservation.setStatus(newStatus);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null) {
            return false;
        }

        // Check if flight is open for booking
        if (!f.isOpenForBooking()) {
            return false;
        }

        // Check if flight has departed
        if (now.after(f.getDepartureTime())) {
            return false;
        }

        // Check for duplicate passengers on the flight across all bookings for this customer?
        // The requirement says "checks that there are no duplicate passengers on the flight"
        // This usually implies per-booking or per-customer-per-flight.
        // Given the context "A customer can book one or more flights, for different passengers",
        // and "Create a booking... supplies a list of passengers names", it likely means
        // no passenger appears twice in the same list, and potentially no passenger is already
        // booked on this specific flight by this customer.
        
        // Check duplicates in the provided list
        if (listOfPassengerNames.size() != listOfPassengerNames.stream().distinct().count()) {
            return false;
        }

        // Check if any passenger in the list is already booked on this flight by this customer
        for (Booking booking : this.bookings) {
            for (Reservation reservation : booking.getReservations()) {
                if (reservation.getFlight().equals(f)) {
                    for (String passengerName : listOfPassengerNames) {
                        if (reservation.getPassenger().getName().equals(passengerName)) {
                            return false;
                        }
                    }
                }
            }
        }

        Booking booking = new Booking();
        booking.setCustomer(this);
        
        for (String passengerName : listOfPassengerNames) {
            if (!booking.createReservation(f, passengerName, now)) {
                // If creation fails, rollback? 
                // For simplicity in this exercise, if one fails, the whole booking fails.
                // Since createReservation checks duplicates and status, and we pre-checked,
                // it should succeed unless internal state changed.
                // We remove any successfully created reservations if one fails (though unlikely here)
                f.getReservations().removeAll(booking.getReservations());
                return false;
            }
        }

        this.bookings.add(booking);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        if (stop == null || now == null) {
            return false;
        }
        // Simple validation: stopover times should be between departure and arrival?
        // Or just logical consistency. The prompt doesn't specify complex stopover validation logic
        // other than the flight level temporal consistency.
        if (this.departureTime != null && this.arrivalTime != null) {
             if (now.before(this.departureTime) || now.after(this.arrivalTime)) {
                 return false;
             }
             if (stop.getDepartureTime() != null && stop.getDepartureTime().before(this.departureTime)) {
                 return false;
             }
             if (stop.getArrivalTime() != null && stop.getArrivalTime().after(this.arrivalTime)) {
                 return false;
             }
        }
        this.stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null) {
            return false;
        }
        if (this.departureTime != null && now.after(this.departureTime)) {
            return false;
        }
        return this.stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        return this.reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .collect(Collectors.toList());
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
        if (f != null) {
            this.flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (f != null) {
            this.flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null || now == null) {
            return false;
        }

        // A flight may be published only once
        if (f.isOpenForBooking()) {
            return false;
        }

        // Before publishing, the flight's openForBooking status must be false (already checked above, but explicit)
        if (f.isOpenForBooking()) {
            return false;
        }

        // Validate timestamps
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }

        // currentTime < departureTime < arrivalTime
        if (now.after(f.getDepartureTime())) {
            return false;
        }
        if (f.getDepartureTime().after(f.getArrivalTime())) {
            return false;
        }

        // Route integrity: departureAirport != arrivalAirport
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (f.getDepartureAirport().equals(f.getArrivalAirport())) {
            return false;
        }

        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }

        Flight targetFlight = null;
        for (Flight f : this.flights) {
            if (f.getId().equals(flightId)) {
                targetFlight = f;
                break;
            }
        }

        if (targetFlight == null) {
            return false;
        }

        // Verify that the flight is currently open
        if (!targetFlight.isOpenForBooking()) {
            return false;
        }

        // Verify flight has not yet departed
        if (now.after(targetFlight.getDepartureTime())) {
            return false;
        }

        // Change status to closed
        targetFlight.setOpenForBooking(false);

        // Cancel every confirmed reservation
        for (Reservation reservation : targetFlight.getReservations()) {
            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                reservation.setStatus(ReservationStatus.CANCELED);
            }
        }

        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        if (origin == null || dest == null || date == null) {
            return new ArrayList<>();
        }
        
        List<Flight> results = new ArrayList<>();
        for (Flight f : this.flights) {
            if (f.isOpenForBooking()) {
                boolean originMatch = false;
                boolean destMatch = false;

                if (f.getDepartureAirport() != null && f.getDepartureAirport().getCities() != null) {
                    for (City c : f.getDepartureAirport().getCities()) {
                        if (c.getName().equals(origin)) {
                            originMatch = true;
                            break;
                        }
                    }
                }
                
                if (f.getArrivalAirport() != null && f.getArrivalAirport().getCities() != null) {
                    for (City c : f.getArrivalAirport().getCities()) {
                        if (c.getName().equals(dest)) {
                            destMatch = true;
                            break;
                        }
                    }
                }

                if (originMatch && destMatch) {
                    results.add(f);
                }
            }
        }
        return results;
    }
}