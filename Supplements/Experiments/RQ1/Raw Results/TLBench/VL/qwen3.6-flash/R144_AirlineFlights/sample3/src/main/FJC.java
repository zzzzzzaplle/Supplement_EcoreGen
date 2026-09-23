import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        if (f == null || flights.isEmpty()) {
            return false;
        }
        
        if (!flights.contains(f)) {
            return false;
        }

        if (f.isOpenForBooking()) {
            return false;
        }

        if (f.getId() == null || f.getDepartureTime() == null || f.getArrivalTime() == null) {
            return false;
        }

        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }

        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
            return false;
        }

        if (now == null) {
            return false;
        }

        if (now.after(f.getDepartureTime())) {
            return false;
        }

        if (f.getDepartureTime().after(f.getArrivalTime())) {
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
        for (Flight f : flights) {
            if (flightId.equals(f.getId())) {
                targetFlight = f;
                break;
            }
        }

        if (targetFlight == null) {
            return false;
        }

        if (!targetFlight.isOpenForBooking()) {
            return false;
        }

        if (now.after(targetFlight.getDepartureTime())) {
            return false;
        }

        targetFlight.setOpenForBooking(false);

        for (Reservation r : targetFlight.getReservations()) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                r.setStatus(ReservationStatus.CANCELED);
            }
        }

        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> results = new ArrayList<>();
        if (origin == null || dest == null || date == null) {
            return results;
        }

        for (Flight f : flights) {
            if (f.isOpenForBooking()) {
                String depCity = getCityName(f.getDepartureAirport());
                String arrCity = getCityName(f.getArrivalAirport());
                
                if ((origin.equalsIgnoreCase(depCity) || origin.equalsIgnoreCase(f.getDepartureAirport().getId())) &&
                    (dest.equalsIgnoreCase(arrCity) || dest.equalsIgnoreCase(f.getArrivalAirport().getId()))) {
                    
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

    private String getCityName(Airport airport) {
        if (airport != null && airport.getCities() != null && !airport.getCities().isEmpty()) {
            return airport.getCities().get(0).getName();
        }
        return null;
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
        this.id = UUID.randomUUID().toString();
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

    public List<Reservation> getReservations() {
        return reservations;
    }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null || now == null) {
            return false;
        }
        if (now.after(this.departureTime) || now.before(this.departureTime)) {
             return false;
        }
        if (this.stopovers.contains(stop)) {
            return false;
        }
        this.stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null) {
            return false;
        }
        return this.stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        for (Reservation r : this.reservations) {
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

        for (Booking b : this.bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f.isOpenForBooking() && now.before(f.getDepartureTime())) {
                        r.setStatus(ReservationStatus.CONFIRMED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null || now == null) {
            return false;
        }

        for (Booking b : this.bookings) {
            for (Reservation r : b.getReservations()) {
                if (r.getId().equals(reservationID)) {
                    Flight f = r.getFlight();
                    if (f.isOpenForBooking() && now.before(f.getDepartureTime())) {
                        if (r.getStatus() == ReservationStatus.CONFIRMED) {
                            r.setStatus(ReservationStatus.CANCELED);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || now == null || listOfPassengerNames == null || listOfPassengerNames.isEmpty()) {
            return false;
        }

        if (!f.isOpenForBooking()) {
            return false;
        }

        if (now.after(f.getDepartureTime())) {
            return false;
        }

        // Check for duplicate passengers on the flight
        for (Reservation existingRes : f.getReservations()) {
            for (String name : listOfPassengerNames) {
                if (existingRes.getPassenger().getName().equals(name)) {
                    return false;
                }
            }
        }

        // Check for duplicate passengers within the current request
        if (listOfPassengerNames.size() != listOfPassengerNames.stream().distinct().count()) {
            return false;
        }

        Booking newBooking = new Booking();
        newBooking.setCustomer(this);
        f.getReservations().add(newBooking); // Link booking to flight reservations logically if needed, or just manage via Customer

        for (String name : listOfPassengerNames) {
            Passenger p = new Passenger();
            p.setName(name);
            Reservation r = new Reservation();
            r.setId(UUID.randomUUID().toString());
            r.setStatus(ReservationStatus.PENDING);
            r.setPassenger(p);
            r.setFlight(f);
            
            newBooking.createReservation(f, name, now);
        }

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

        // Check if passenger already has a reservation on this flight
        for (Reservation r : f.getReservations()) {
            if (r.getPassenger().getName().equals(passenger)) {
                return false;
            }
        }

        Reservation newReservation = new Reservation();
        newReservation.setId(UUID.randomUUID().toString());
        newReservation.setStatus(ReservationStatus.PENDING);
        
        Passenger p = new Passenger();
        p.setName(passenger);
        newReservation.setPassenger(p);
        newReservation.setFlight(f);

        this.reservations.add(newReservation);
        f.getReservations().add(newReservation);

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