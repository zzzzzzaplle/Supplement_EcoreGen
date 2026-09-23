import java.util.*;
import java.text.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        if (this.flights == null) {
            this.flights = new ArrayList<>();
        }
        if (f != null && !this.flights.contains(f)) {
            this.flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        if (this.flights != null) {
            this.flights.remove(f);
        }
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        try {
            String depStr = sdf.format(f.getDepartureTime());
            String arrStr = sdf.format(f.getArrivalTime());
            sdf.parse(depStr);
            sdf.parse(arrStr);
        } catch (Exception e) {
            return false;
        }
        if (!now.before(f.getDepartureTime())) {
            return false;
        }
        if (!f.getDepartureTime().before(f.getArrivalTime())) {
            return false;
        }
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) {
            return false;
        }
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) {
            return false;
        }
        f.setOpenForBooking(true);
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        if (flightId == null || now == null) {
            return false;
        }
        Flight target = null;
        for (Flight f : flights) {
            if (f != null && flightId.equals(f.getId())) {
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
        if (target.getDepartureTime() != null && !now.before(target.getDepartureTime())) {
            return false;
        }
        target.setOpenForBooking(false);
        if (target.getReservations() != null) {
            for (Reservation r : target.getReservations()) {
                if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                    r.setStatus(ReservationStatus.CANCELED);
                }
            }
        }
        return true;
    }

    public List<Flight> searchFlights(String origin, String dest, Date date) {
        List<Flight> result = new ArrayList<>();
        if (flights == null) {
            return result;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String targetDate = date == null ? null : sdf.format(date);
        for (Flight f : flights) {
            if (f == null) continue;
            if (!f.isOpenForBooking()) continue;
            if (origin != null && (f.getDepartureAirport() == null || !origin.equals(f.getDepartureAirport().getId()))) continue;
            if (dest != null && (f.getArrivalAirport() == null || !dest.equals(f.getArrivalAirport().getId()))) continue;
            if (targetDate != null) {
                if (f.getDepartureTime() == null) continue;
                if (!targetDate.equals(sdf.format(f.getDepartureTime()))) continue;
            }
            result.add(f);
        }
        return result;
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

    public void setDepartureAirport(Airport d) {
        this.departureAirport = d;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport a) {
        this.arrivalAirport = a;
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
        if (stop == null) return false;
        if (this.stopovers == null) this.stopovers = new ArrayList<>();
        if (stop.getDepartureTime() == null || stop.getArrivalTime()) return false;
        if (!stop.getArrivalTime().after(stop.getDepartureTime())) return false;
        if (this.departureTime != null && !stop.getDepartureTime().after(this.departureTime)) return false;
        if (this.arrivalTime != null && !stop.getArrivalTime().before(this.arrivalTime)) return false;
        this.stopovers.add(stop);
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (stop == null || this.stopovers == null) return false;
        return this.stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> result = new ArrayList<>();
        if (!this.openForBooking) {
            return result;
        }
        if (this.reservations != null) {
            for (Reservation r : this.reservations) {
                if (r != null && r.getStatus() == ReservationStatus.CONFIRMED) {
                    result.add(r);
                }
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

    public void setServesForCities(List<City> servesForCities) {
        this.servesForCities = servesForCities;
    }

    public void addCity(City c) {
        if (this.servesForCities == null) this.servesForCities = new ArrayList<>();
        if (c != null && !this.servesForCities.contains(c)) {
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
    private static final AtomicInteger RESERVATION_COUNTER = new AtomicInteger(0);

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
        if (reservationID == null || now == null) return false;
        for (Booking b : bookings) {
            if (b == null) continue;
            for (Reservation r : b.getReservations()) {
                if (r != null && reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) return false;
                    if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) return false;
                    if (!f.isOpenForBooking()) return false;
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
            if (b == null) continue;
            for (Reservation r : b.getReservations()) {
                if (r != null && reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) return false;
                    if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) return false;
                    if (!f.isOpenForBooking()) return false;
                    r.setStatus(ReservationStatus.CANCELED);
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
        if (!f.isOpenForBooking()) {
            return false;
        }
        if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) {
            return false;
        }
        Set<String> uniqueNames = new HashSet<>();
        for (String name : listOfPassengerNames) {
            if (name == null) return false;
            if (!uniqueNames.add(name)) return false;
        }
        if (f.getReservations() != null) {
            for (String name : listOfPassengerNames) {
                for (Reservation existing : f.getReservations()) {
                    if (existing != null && existing.getPassenger() != null
                            && name.equals(existing.getPassenger().getName())) {
                        return false;
                    }
                }
            }
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        for (String name : listOfPassengerNames) {
            Reservation res = new Reservation();
            res.setId("RES-" + RESERVATION_COUNTER.incrementAndGet());
            Passenger p = new Passenger();
            p.setName(name);
            res.setPassenger(p);
            res.setFlight(f);
            res.setStatus(ReservationStatus.PENDING);
            booking.getReservations().add(res);
            if (f.getReservations() == null) f.setReservations(new ArrayList<>());
            f.getReservations().add(res);
        }
        if (this.bookings == null) this.bookings = new ArrayList<>();
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
        if (f == null || passenger == null || now == null) return false;
        if (!f.isOpenForBooking()) return false;
        if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) return false;
        for (Reservation r : reservations) {
            if (r != null && r.getPassenger() != null
                    && passenger.equals(r.getPassenger().getName())) {
                return false;
            }
        }
        if (f.getReservations() != null) {
            for (Reservation r : f.getReservations()) {
                if (r != null && r.getPassenger() != null
                        && passenger.equals(r.getPassenger().getName())) {
                    return false;
                }
            }
        }
        Reservation res = new Reservation();
        res.setId("RES-" + System.nanoTime());
        Passenger p = new Passenger();
        p.setName(passenger);
        res.setPassenger(p);
        res.setFlight(f);
        res.setStatus(ReservationStatus.PENDING);
        this.reservations.add(res);
        if (f.getReservations() == null) f.setReservations(new ArrayList<>());
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