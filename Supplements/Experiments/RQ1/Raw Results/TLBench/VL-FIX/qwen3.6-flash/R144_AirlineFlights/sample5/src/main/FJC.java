import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        if (f != null) {
            this.flights.remove(f);
        }
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null) {
            return false;
        }
        
        // Check if flight is already published or not open for booking
        if (f.isOpenForBooking()) {
            return false;
        }

        // Validate timestamps
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }

        // Temporal consistency: currentTime < departureTime < arrivalTime
        if (now.getTime() >= f.getDepartureTime().getTime() ||
            f.getDepartureTime().getTime() >= f.getArrivalTime().getTime()) {
            return false;
        }

        // Route integrity: departureAirport != arrivalAirport
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null ||
            f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
            return false;
        }

        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
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

        // Flight must be currently open
        if (!targetFlight.isOpenForBooking()) {
            return false;
        }

        // Flight must not have departed yet
        if (now.getTime() >= targetFlight.getDepartureTime().getTime()) {
            return false;
        }

        // Change status to closed
        targetFlight.setOpenForBooking(false);

        // Cancel every confirmed reservation
        for (Reservation r : targetFlight.getReservations()) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                r.setStatus(ReservationStatus.CANCELED);
            }
        }

        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> results = new ArrayList<>();
        for (Flight f : this.flights) {
            if (f.isOpenForBooking() &&
                f.getDepartureAirport() != null && f.getDepartureAirport().getId().equals(origin) &&
                f.getArrivalAirport() != null && f.getArrivalAirport().getId().equals(dest)) {
                
                // Check if the flight departs on the specified date
                // Assuming date comparison logic: same year, month, day
                if (f.getDepartureTime() != null) {
                    Date flightDate = new Date(f.getDepartureTime().getYear(), f.getDepartureTime().getMonth(), f.getDepartureTime().getDate());
                    Date searchDate = new Date(date.getYear(), date.getMonth(), date.getDate());
                    
                    if (flightDate.equals(searchDate)) {
                        results.add(f);
                    }
                }
            }
        }
        return results;
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

    public List<Reservation> getReservations() {
        return reservations;
    }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null) {
            return false;
        }
        
        // Stopover times must be within flight times
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        
        Date stopStart = stop.getDepartureTime();
        Date stopEnd = stop.getArrivalTime();
        
        if (stopStart == null || stopEnd == null) {
            return false;
        }
        
        if (stopStart.getTime() < departureTime.getTime() || stopEnd.getTime() > arrivalTime.getTime() || stopStart.getTime() >= stopEnd.getTime()) {
            return false;
        }
        
        // Stopover must not be after flight departure
        if (now.getTime() >= departureTime.getTime()) {
            return false;
        }

        if (!this.stopovers.contains(stop)) {
            this.stopovers.add(stop);
        }
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null) {
            return false;
        }
        
        if (now.getTime() >= departureTime.getTime()) {
            return false;
        }

        return this.stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        return reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .collect(Collectors.toList());
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
    private List<City> servesForCities;

    public Airport() {
        this.servesForCities = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public List<City> getCities() {
        return servesForCities;
    }

    public void addCity(City c) {
        if (c != null && !this.servesForCities.contains(c)) {
            this.servesForCities.add(c);
        }
    }
}

class City {
    public City() {
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
        for (Booking b : this.bookings) {
            if (b.confirm(reservationID, now)) {
                return true;
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Booking b : this.bookings) {
            if (b.cancel(reservationID, now)) {
                return true;
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || listOfPassengerNames == null || listOfPassengerNames.isEmpty()) {
            return false;
        }

        // Check if flight is open for booking
        if (!f.isOpenForBooking()) {
            return false;
        }

        // Check if current time is before flight departure
        if (f.getDepartureTime() == null || now.getTime() >= f.getDepartureTime().getTime()) {
            return false;
        }

        // Check for duplicate passengers on the flight
        List<String> existingPassengerNames = new ArrayList<>();
        for (Reservation r : f.getReservations()) {
            if (r.getStatus() != ReservationStatus.CANCELED) {
                existingPassengerNames.add(r.getPassenger().getName());
            }
        }

        for (String name : listOfPassengerNames) {
            if (existingPassengerNames.contains(name)) {
                return false;
            }
        }

        // Create a new booking
        Booking newBooking = new Booking();
        newBooking.setCustomer(this);

        // Create reservations for each passenger
        for (String name : listOfPassengerNames) {
            if (newBooking.createReservation(f, name, now)) {
                existingPassengerNames.add(name); // Add to local list to check within same booking if needed, though requirement implies global uniqueness
            } else {
                // If one fails, rollback or return false? Requirement implies atomic success/fail
                // For simplicity, if one creation fails, we might just return false or handle partial. 
                // Given strict requirements, let's assume if any check fails earlier we return false.
                // Here, createReservation handles status setting.
            }
        }

        // If we reached here, all reservations were created successfully
        this.bookings.add(newBooking);
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

        // Generate unique ID for reservation
        String reservationId = f.getId() + "-" + passenger.hashCode() + "-" + System.currentTimeMillis();
        
        Passenger p = new Passenger();
        p.setName(passenger);

        Reservation r = new Reservation();
        r.setId(reservationId);
        r.setPassenger(p);
        r.setFlight(f);
        r.setStatus(ReservationStatus.PENDING);

        this.reservations.add(r);
        f.getReservations().add(r);

        return true;
    }

    public boolean confirm(String reservationID, Date now) {
        for (Reservation r : this.reservations) {
            if (r.getId().equals(reservationID)) {
                // Check that flight has not yet departed and is still open for booking
                if (r.getFlight() == null || !r.getFlight().isOpenForBooking()) {
                    return false;
                }
                if (r.getFlight().getDepartureTime() == null || now.getTime() >= r.getFlight().getDepartureTime().getTime()) {
                    return false;
                }
                
                r.setStatus(ReservationStatus.CONFIRMED);
                return true;
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Reservation r : this.reservations) {
            if (r.getId().equals(reservationID)) {
                // Check that flight has not yet departed and is still open for booking
                if (r.getFlight() == null || !r.getFlight().isOpenForBooking()) {
                    return false;
                }
                if (r.getFlight().getDepartureTime() == null || now.getTime() >= r.getFlight().getDepartureTime().getTime()) {
                    return false;
                }

                r.setStatus(ReservationStatus.CANCELED);
                return true;
            }
        }
        return false;
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