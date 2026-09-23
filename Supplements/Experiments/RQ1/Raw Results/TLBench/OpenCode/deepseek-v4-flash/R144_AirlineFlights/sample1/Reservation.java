public class Reservation {
    private String id;
    private ReservationStatus status;
    private Passenger passenger;
    private Flight flight;

    public Reservation() {
        this.status = ReservationStatus.PENDING;
    }

    public Reservation(String id, Passenger passenger, Flight flight) {
        this.id = id;
        this.passenger = passenger;
        this.flight = flight;
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
