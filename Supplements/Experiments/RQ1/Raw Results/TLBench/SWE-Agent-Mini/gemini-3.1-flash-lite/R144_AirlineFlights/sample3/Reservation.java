public class Reservation {
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
    public void setPassenger(Passenger p) { this.passenger = p; }
    public Flight getFlight() { return flight; }
    public void setFlight(Flight f) { this.flight = f; }
}
