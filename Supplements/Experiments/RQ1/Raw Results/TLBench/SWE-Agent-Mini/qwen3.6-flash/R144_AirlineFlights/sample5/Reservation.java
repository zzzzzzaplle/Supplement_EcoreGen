import java.util.Date;
import java.util.UUID;

public class Reservation {
    private String id;
    private ReservationStatus status;
    private Passenger passenger;
    private Flight flight;

    public Reservation() {
        this.id = UUID.randomUUID().toString();
        this.status = ReservationStatus.PENDING;
        this.passenger = null;
        this.flight = null;
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
