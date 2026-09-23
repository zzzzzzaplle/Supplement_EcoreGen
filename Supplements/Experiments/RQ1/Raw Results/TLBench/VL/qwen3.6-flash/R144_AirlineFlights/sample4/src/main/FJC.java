import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
        if (f == null || !f.isOpenForBooking()) {
            return false;
        }
        
        Date departureTime = f.getDepartureTime();
        Date arrivalTime = f.getArrivalTime();
        Airport departureAirport = f.getDepartureAirport();
        Airport arrivalAirport = f.getArrivalAirport();

        if (departureTime == null || arrivalTime == null) {
            return false;
        }

        if (now.getTime() >= departureTime.getTime() || departureTime.getTime() >= arrivalTime.getTime()) {
            return false;
        }

        if (departureAirport == null || arrivalAirport == null) {
            return false;
        }

        if (departureAirport.getId().equals(arrivalAirport.getId())) {
            return false;
        }

        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        Flight flightToClose = null;
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                flightToClose = f;
                break;
            }
        }

        if (flightToClose == null) {
            return false;
        }

        if (!flightToClose.isOpenForBooking()) {
            return false;
        }
        
        if (flightToClose.getDepartureTime().getTime() <= now.getTime()) {
            return false;
        }

        flightToClose.setOpenForBooking(false);
        
        List<Reservation> reservations = flightToClose.getReservations();
        if (reservations != null) {
            for (Reservation r : reservations) {
                if (r.getStatus() == ReservationStatus.CONFIRMED) {
                    r.setStatus(ReservationStatus.CANCELED);
                }
            }
        }

        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        if (origin == null || dest == null || date == null) {
            return new ArrayList<>();
        }

        return flights.stream()
                .filter(f -> {
                    if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
                        return false;
                    }
                    
                    String origId = f.getDepartureAirport().getId();
                    String destId = f.getArrivalAirport().getId();
                    
                    if (!origId.equals(origin) && !destId.equals(origin)) {
                         // Check if origin is in cities of departure airport or dest is in cities of arrival airport
                         // The prompt says "Airport serves one or more cities". 
                         // Usually search by city implies checking if the city is served by the airport.
                         // However, the design model doesn't explicitly link Airport to City for lookup in a way that suggests 
                         // Airport has a list of cities it serves, but City doesn't have a reference to Airport.
                         // Let's assume origin/dest refer to Airport IDs for simplicity based on "Each airport has its unique id",
                         // OR if they refer to city names, we need to check Airport.getCities().
                         // Given the parameter name "String origin", it's ambiguous. 
                         // Let's look at the Domain Description: "Each airport serves one or more cities."
                         // It is safer to assume the search might be by Airport ID or City Name.
                         // However, without a reverse lookup from City to Airport, we can only check if the provided string 
                         // is an ID of an airport that serves a city matching the string, or if the string IS the ID.
                         // Let's assume for standard implementations in such tasks, if City class exists, we might need to match cities.
                         // But Airport.getCities() returns List<City>. City has no fields visible in the prompt other than being a class.
                         // Let's assume origin/dest are Airport IDs for robustness, or check if City has a name.
                         // Since City class is empty in the diagram, let's assume origin/dest are Airport IDs.
                    }
                    
                    // Re-reading: "A flight has a departure airport and an arrival airport. Each airport serves one or more cities."
                    // If I search by city, I need to find airports serving that city.
                    // Since City has no fields, I cannot compare City objects by name.
                    // Therefore, it is highly likely that "origin" and "dest" in searchFlights refer to Airport IDs.
                    
                    boolean origMatch = origId.equals(origin);
                    boolean destMatch = destId.equals(dest);
                    
                    return (origMatch && destMatch);
                })
                .filter(f -> {
                    // Check if the flight is on the given date
                    Date dep = f.getDepartureTime();
                    if (dep == null) return false;
                    // Simple date comparison: year, month, day
                    return dep.getYear() == date.getYear() && 
                           dep.getMonth() == date.getMonth() && 
                           dep.getDate() == date.getDate();
                })
                .collect(Collectors.toList());
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

    public List<Reservation> getReservations() {
        return reservations;
    }

    public boolean addStopover(Stopover stop, Date now) {
        if (stop == null) {
            return false;
        }
        if (!openForBooking) {
            return false;
        }
        if (departureTime == null || arrivalTime == null || now == null) {
            return false;
        }
        
        // Temporal consistency: stop must be between departure and arrival
        Date stopDep = stop.getDepartureTime();
        Date stopArr = stop.getArrivalTime();
        
        if (stopDep == null || stopArr == null) {
            return false;
        }
        
        if (now.getTime() > departureTime.getTime() || now.getTime() < stopDep.getTime()) {
            return false;
        }
        
        if (stopDep.getTime() >= stopArr.getTime()) {
            return false;
        }
        
        if (stopArr.getTime() > arrivalTime.getTime()) {
            return false;
        }
        
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null) {
            return false;
        }
        if (!openForBooking) {
            return false;
        }
        return stopovers.remove(stop);
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
    // Class defined in domain, no specific fields/methods listed in diagram other than existence
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
        for (Booking b : bookings) {
            if (b.confirm(reservationID, now)) {
                return true;
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Booking b : bookings) {
            if (b.cancel(reservationID, now)) {
                return true;
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || listOfPassengerNames == null || now == null || !f.isOpenForBooking()) {
            return false;
        }

        // Check if flight has departed
        if (f.getDepartureTime() == null || f.getDepartureTime().getTime() <= now.getTime()) {
            return false;
        }

        // Check for duplicate passengers on the flight
        List<String> existingPassengers = new ArrayList<>();
        for (Reservation r : f.getReservations()) {
            if (r.getPassenger() != null && r.getPassenger().getName() != null) {
                existingPassengers.add(r.getPassenger().getName());
            }
        }

        for (String name : listOfPassengerNames) {
            if (name == null || existingPassengers.contains(name)) {
                return false;
            }
        }

        Booking newBooking = new Booking();
        newBooking.setCustomer(this);
        
        for (String name : listOfPassengerNames) {
            if (!newBooking.createReservation(f, name, now)) {
                // If any fail, the whole booking fails? 
                // The requirement says "generates a booking... creates a reservation... for each passenger".
                // If one fails, presumably the whole transaction fails.
                // Since we already checked duplicates and flight status, it should succeed.
                return false;
            }
        }

        bookings.add(newBooking);
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

    public boolean createReservation(Flight f, String passengerName, Date now) {
        if (f == null || passengerName == null || now == null) {
            return false;
        }

        if (!f.isOpenForBooking()) {
            return false;
        }
        
        if (f.getDepartureTime() == null || f.getDepartureTime().getTime() <= now.getTime()) {
            return false;
        }

        // Check for duplicate passenger on the flight
        for (Reservation existingR : f.getReservations()) {
            if (existingR.getPassenger() != null && existingR.getPassenger().getName().equals(passengerName)) {
                return false;
            }
        }

        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setFlight(f);
        reservation.setStatus(ReservationStatus.PENDING);
        
        Passenger p = new Passenger();
        p.setName(passengerName);
        reservation.setPassenger(p);

        f.getReservations().add(reservation);
        reservations.add(reservation);

        return true;
    }

    public boolean confirm(String reservationID, Date now) {
        for (Reservation r : reservations) {
            if (r.getId().equals(reservationID)) {
                // Check flight status
                Flight flight = r.getFlight();
                if (flight == null || !flight.isOpenForBooking()) {
                    return false;
                }
                if (flight.getDepartureTime() == null || flight.getDepartureTime().getTime() <= now.getTime()) {
                    return false;
                }
                r.setStatus(ReservationStatus.CONFIRMED);
                return true;
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Reservation r : reservations) {
            if (r.getId().equals(reservationID)) {
                // Check flight status
                Flight flight = r.getFlight();
                if (flight == null || !flight.isOpenForBooking()) {
                    return false;
                }
                if (flight.getDepartureTime() == null || flight.getDepartureTime().getTime() <= now.getTime()) {
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