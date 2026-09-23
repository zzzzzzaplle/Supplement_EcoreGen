import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.text.SimpleDateFormat;

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
        if (!flights.contains(f)) {
            flights.add(f);
        }
    }

    public void removeFlight(Flight f) {
        flights.remove(f);
    }

    public boolean publishFlight(Flight f, Date now) {
        if (f == null) return false;
        if (f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null || f.getArrivalTime() == null) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String depStr = sdf.format(f.getDepartureTime());
            String arrStr = sdf.format(f.getArrivalTime());
            sdf.parse(depStr);
            sdf.parse(arrStr);
        } catch (Exception e) {
            return false;
        }
        if (!now.before(f.getDepartureTime())) return false;
        if (!f.getDepartureTime().before(f.getArrivalTime())) return false;
        if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) return false;
        if (f.getDepartureAirport().getId().equals(f.getArrivalAirport().getId())) return false;
        f.setOpenForBooking(true);
        if (!flights.contains(f)) {
            flights.add(f);
        }
        return true;
    }

    public boolean closeFlight(String flightId, Date now) {
        for (Flight f : flights) {
            if (f.getId() != null && f.getId().equals(flightId)) {
                if (!f.isOpenForBooking()) return false;
                if (f.getDepartureTime() != null && now.after(f.getDepartureTime())) return false;
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
        for (Flight f : flights) {
            if (!f.isOpenForBooking()) continue;
            if (f.getDepartureAirport() == null || f.getArrivalAirport() == null) continue;
            if (!f.getDepartureAirport().getId().equals(origin)) continue;
            if (!f.getArrivalAirport().getId().equals(dest)) continue;
            if (date != null && f.getDepartureTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String d1 = sdf.format(date);
                String d2 = sdf.format(f.getDepartureTime());
                if (!d1.equals(d2)) continue;
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
        if (this.openForBooking) return false;
        if (stop.getArrivalTime() == null || stop.getDepartureTime() == null) return false;
        if (!stop.getArrivalTime().before(stop.getDepartureTime())) return false;
        if (stop.getAirport() == null) return false;
        if (stop.getArrivalTime().before(this.departureTime) || !stop.getDepartureTime().before(this.arrivalTime)) return false;
        if (!stopovers.contains(stop)) {
            stopovers.add(stop);
        }
        return true;
    }

    public boolean removeStopover(Stopover stop, Date now) {
        if (this.openForBooking) return false;
        return stopovers.remove(stop);
    }

    public List<Reservation> getConfirmedReservations() {
        List<Reservation> confirmed = new ArrayList<>();
        if (!this.openForBooking) return confirmed;
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
        this.id = UUID.randomUUID().toString();
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

    public void setCities(List<City> servesForCities) {
        this.servesForCities = servesForCities;
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
        if (reservationID == null) return false;
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) return false;
                    if (!f.isOpenForBooking()) return false;
                    if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) return false;
                    if (r.getStatus() == ReservationStatus.CANCELED) return false;
                    r.setStatus(ReservationStatus.CONFIRMED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancel(String reservationID, Date now) {
        if (reservationID == null) return false;
        for (Booking b : bookings) {
            for (Reservation r : b.getReservations()) {
                if (reservationID.equals(r.getId())) {
                    Flight f = r.getFlight();
                    if (f == null) return false;
                    if (!f.isOpenForBooking()) return false;
                    if (f.getDepartureTime() != null && !now.before(f.getDepartureTime())) return false;
                    if (r.getStatus() == ReservationStatus.CANCELED) return false;
                    r.setStatus(ReservationStatus.CANCELED);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames) {
        if (f == null || listOfPassengerNames == null || listOfPassengerNames.isEmpty()) return false;
        if (!f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) return false;
        Set<String> existingNames = new HashSet<>();
        for (Reservation r : f.getReservations()) {
            if (r.getPassenger() != null && r.getPassenger().getName() != null) {
                existingNames.add(r.getPassenger().getName());
            }
        }
        Set<String> newNames = new HashSet<>();
        for (String name : listOfPassengerNames) {
            if (name == null) return false;
            if (existingNames.contains(name)) return false;
            if (newNames.contains(name)) return false;
            newNames.add(name);
        }
        Booking b = new Booking();
        b.setCustomer(this);
        for (String name : listOfPassengerNames) {
            if (!b.createReservation(f, name, now)) {
                return false;
            }
        }
        this.bookings.add(b);
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
        if (f == null || passenger == null) return false;
        if (!f.isOpenForBooking()) return false;
        if (f.getDepartureTime() == null || !now.before(f.getDepartureTime())) return false;
        for (Reservation r : f.getReservations()) {
            if (r.getPassenger() != null && passenger.equals(r.getPassenger().getName())) {
                return false;
            }
        }
        Passenger p = new Passenger();
        p.setName(passenger);
        Reservation r = new Reservation();
        r.setId(UUID.randomUUID().toString());
        r.setPassenger(p);
        r.setFlight(f);
        r.setStatus(ReservationStatus.PENDING);
        f.getReservations().add(r);
        this.reservations.add(r);
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