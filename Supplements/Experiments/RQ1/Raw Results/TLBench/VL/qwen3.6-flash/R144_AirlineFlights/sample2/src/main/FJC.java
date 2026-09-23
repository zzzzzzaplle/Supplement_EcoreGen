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
        if (f == null) {
            return false;
        }
        if (f.isOpenForBooking()) {
            return false;
        }
        Date departure = f.getDepartureTime();
        Date arrival = f.getArrivalTime();
        if (departure == null || arrival == null) {
            return false;
        }
        if (departure.compareTo(now) <= 0 || arrival.compareTo(now) <= 0) {
            return false;
        }
        if (departure.compareTo(arrival) >= 0) {
            return false;
        }
        Airport depAir = f.getDepartureAirport();
        Airport arrAir = f.getArrivalAirport();
        if (depAir == null || arrAir == null) {
            return false;
        }
        if (depAir.getId().equals(arrAir.getId())) {
            return false;
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        Flight target = null;
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                target = f;
                break;
            }
        }
        if (target == null) {
            return false;
        }
        if (!target.isOpenForBooking()) {
            return false;
        }
        if (target.getDepartureTime().compareTo(now) <= 0) {
            return false;
        }
        target.setOpenForBooking(false);
        for (Reservation r : target.getReservations()) {
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                r.setStatus(ReservationStatus.CANCELED);
            }
        }
        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> results = new ArrayList<>();
        for (Flight f : flights) {
            if (f.isOpenForBooking()) {
                Airport dep = f.getDepartureAirport();
                Airport arr = f.getArrivalAirport();
                boolean matchOrigin = true;
                boolean matchDest = true;
                if (origin != null) {
                    if (dep == null) {
                        matchOrigin = false;
                    } else {
                        boolean found = false;
                        for (City c : dep.getCities()) {
                            if (c.getName().equalsIgnoreCase(origin)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            matchOrigin = false;
                        }
                    }
                }
                if (dest != null) {
                    if (arr == null) {
                        matchDest = false;
                    } else {
                        boolean found = false;
                        for (City c : arr.getCities()) {
                            if (c.getName().equalsIgnoreCase(dest)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            matchDest = false;
                        }
                    }
                }
                if (matchOrigin && matchDest) {
                    results.add(f);
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
        if (stop == null || now == null) {
            return false;
        }
        if (!openForBooking) {
            return false;
        }
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        if (now.compareTo(departureTime) >= 0) {
            return false;
        }
        Date stopDep = stop.getDepartureTime();
        Date stopArr = stop.getArrivalTime();
        if (stopDep == null || stopArr == null) {
            return false;
        }
        if (stopDep.compareTo(now) < 0 || stopArr.compareTo(now) < 0) {
            return false;
        }
        if (stopDep.compareTo(stopArr) >= 0) {
            return false;
        }
        if (stopDep.compareTo(departureTime) < 0 || stopArr.compareTo(arrivalTime) > 0) {
            return false;
        }
        stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || now == null) {
            return false;
        }
        if (!openForBooking) {
            return false;
        }
        if (departureTime == null || now.compareTo(departureTime) >= 0) {
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
        this.id = UUID.randomUUID().toString();
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
        if (f == null || listOfPassengerNames == null) {
            return false;
        }
        Booking b = new Booking();
        b.setCustomer(this);
        for (String name : listOfPassengerNames) {
            if (!b.createReservation(f, name, now)) {
                return false;
            }
        }
        bookings.add(b);
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
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime().compareTo(now) <= 0) {
            return false;
        }
        for (Reservation r : reservations) {
            if (r.getPassenger().getName().equals(passenger)) {
                return false;
            }
        }
        Passenger p = new Passenger();
        p.setName(passenger);
        Reservation r = new Reservation();
        r.setId(UUID.randomUUID().toString());
        r.setStatus(ReservationStatus.PENDING);
        r.setPassenger(p);
        r.setFlight(f);
        reservations.add(r);
        f.getReservations().add(r);
        return true;
    }

    public boolean confirm(String reservationID, Date now) {
        for (Reservation r : reservations) {
            if (r.getId().equals(reservationID)) {
                return r.confirm(now);
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        for (Reservation r : reservations) {
            if (r.getId().equals(reservationID)) {
                return r.cancel(now);
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

    public boolean confirm(Date now) {
        if (this.status != ReservationStatus.PENDING) {
            return false;
        }
        Flight f = this.flight;
        if (f == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime().compareTo(now) <= 0) {
            return false;
        }
        this.status = ReservationStatus.CONFIRMED;
        return true;
    }

    public boolean cancel(Date now) {
        if (this.status != ReservationStatus.PENDING && this.status != ReservationStatus.CONFIRMED) {
            return false;
        }
        Flight f = this.flight;
        if (f == null) {
            return false;
        }
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime().compareTo(now) <= 0) {
            return false;
        }
        this.status = ReservationStatus.CANCELED;
        return true;
    }
}