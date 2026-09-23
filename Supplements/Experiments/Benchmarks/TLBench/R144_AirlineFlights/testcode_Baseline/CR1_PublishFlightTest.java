import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class CR1_PublishFlightTest {

    private Airline airline;
    private Flight flight;
    private Date currentTime;
    private Airport departureAirport;
    private Airport arrivalAirport;

    // Helper methods (A模式)
    private Airport createAirport(String id) {
        Airport airport = new Airport();
        airport.setId(id);
        return airport;
    }

    private Flight createFlight(String id, Date departureTime, Date arrivalTime, Airport departureAirport, Airport arrivalAirport) {
        Flight flight = new Flight();
        flight.setId(id);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        return flight;
    }

    @Test
    public void tc1_CorrectScheduleAndRoute() {
        // Setup
        airline = new Airline();
        departureAirport = createAirport("AP01");
        arrivalAirport = createAirport("AP02");

        // Add cities served by airports
        City cityA = new City();
        City cityB = new City();
        departureAirport.addCity(cityA);
        arrivalAirport.addCity(cityB);

        currentTime = new Date(2024 - 1900, 11, 1, 9, 0, 0);
        flight = createFlight("F100",
                new Date(2025 - 1900, 0, 10, 10, 0, 0),
                new Date(2025 - 1900, 0, 10, 14, 0, 0),
                departureAirport, arrivalAirport);

        // Publish flight
        boolean result = airline.publishFlight(flight, currentTime);

        // Assertions
        assertTrue("Expected flight to be published successfully", result);
    }

    @Test
    public void tc2_DepartureAfterArrival() {
        // Setup
        airline = new Airline();
        departureAirport = createAirport("AP03");
        arrivalAirport = createAirport("AP04");

        // Add cities served by airports
        City cityC = new City();
        City cityD = new City();
        departureAirport.addCity(cityC);
        arrivalAirport.addCity(cityD);

        currentTime = new Date(2024 - 1900, 11, 15, 10, 0, 0);
        flight = createFlight("F101",
                new Date(2025 - 1900, 1, 5, 20, 0, 0),
                new Date(2025 - 1900, 1, 5, 18, 0, 0),
                departureAirport, arrivalAirport);

        // Publish flight
        boolean result = airline.publishFlight(flight, currentTime);

        // Assertions
        assertFalse("Expected flight not to be published due to invalid timestamps", result);
    }

    @Test
    public void tc3_SameDepartureAndArrivalAirport() {
        // Setup
        airline = new Airline();
        departureAirport = createAirport("AP05");
        arrivalAirport = createAirport("AP05"); // Same airport!

        // Add cities served by airports
        City cityE = new City();
        departureAirport.addCity(cityE);

        currentTime = new Date(2025 - 1900, 1, 1, 9, 0, 0);
        flight = createFlight("F102",
                new Date(2025 - 1900, 2, 1, 8, 0, 0),
                new Date(2025 - 1900, 2, 1, 12, 0, 0),
                departureAirport, arrivalAirport);

        // Publish flight
        boolean result = airline.publishFlight(flight, currentTime);

        // Assertions
        assertFalse("Expected flight not to be published due to same airport for departure and arrival", result);
    }

    @Test
    public void tc4_DepartureTimeInPast() {
        // Setup
        airline = new Airline();
        departureAirport = createAirport("AP06");
        arrivalAirport = createAirport("AP07");

        // Add cities served by airports
        City cityF = new City();
        City cityG = new City();

        departureAirport.addCity(cityF);
        arrivalAirport.addCity(cityG);

        currentTime = new Date(2025 - 1900, 3, 1, 9, 0, 0);
        flight = createFlight("F103",
                new Date(2025 - 1900, 2, 30, 10, 0, 0),
                new Date(2025 - 1900, 2, 30, 12, 0, 0),
                departureAirport, arrivalAirport);

        // Publish flight
        boolean result = airline.publishFlight(flight, currentTime);

        // Assertions
        assertFalse("Expected flight not to be published due to past departure time", result);
    }

    @Test
    public void tc5_SecondPublishAttempt() {
        // Setup
        airline = new Airline();
        departureAirport = createAirport("AP08");
        arrivalAirport = createAirport("AP09");

        // Add cities served by airports
        City cityH = new City();
        City cityI = new City();

        departureAirport.addCity(cityH);
        arrivalAirport.addCity(cityI);

        currentTime = new Date(2025 - 1900, 3, 1, 10, 0, 0);
        flight = createFlight("F104",
                new Date(2025 - 1900, 4, 5, 7, 0, 0),
                new Date(2025 - 1900, 4, 5, 10, 0, 0),
                departureAirport, arrivalAirport);

        // Initial publish attempt
        airline.publishFlight(flight, currentTime);

        // Second publish attempt
        boolean result = airline.publishFlight(flight, currentTime);

        // Assertions
        assertFalse("Expected flight not to be published a second time", result);
    }
}
