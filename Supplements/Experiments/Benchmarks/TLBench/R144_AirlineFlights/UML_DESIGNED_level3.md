// ==version1==
```
class Airline {
  - List<Flight> flights

  //getter,setter
  + List<Flight> getFlights()

  //key operations
  + void addFlight(Flight f)
  + void removeFlight(Flight f)
  + boolean publishFlight(Flight f, Date now)
  + boolean closeFlight(String flightId, Date now)
  + List<Flight> searchFlights(String origin, String dest, Date date)
}

class Flight {
    - String id
    - boolean openForBooking
    - Date departureTime
    - Date arrivalTime
    - Airport departureAirport
    - Airport arrivalAirport
    - List<Stopover> stopovers
    - List<Reservation> reservations

    //getter,setter
    + String getId()
    + void setId(String id)
    + Date getDepartureTime()
    + void setDepartureTime(Date t)
    + Date getArrivalTime()
    + void setArrivalTime(Date t)
    + Airport getDepartureAirport()
    + void setDepartureAirport(Airport D)
    + Airport getArrivalAirport()
    + void setArrivalAirport(Airport A)
    + boolean isOpenForBooking()
    + void setOpenForBooking(boolean openForBooking)
    + List<Stopover> getStopovers()
    + List<Reservation> getReservations()

    //key operations
    + boolean addStopover(Stopover stop, Date now)
    + boolean removeStopover(Stopover stop, Date now)
    + List<Reservation> getConfirmedReservations()
}

class Stopover {
  - Date departureTime
  - Date arrivalTime
  - Airport airport

  //getter，setter
  + Date getDepartureTime()
  + void setDepartureTime(Date d)
  + Date getArrivalTime()
  + void setArrivalTime(Date d)
  + Airport getAirport()
  + void setAirport(Airport a)
}

Airline *-- "*" Flight : flights
Flight *-- "0..*" Stopover : stopovers
Stopover --> "1" Airport :airport
Flight --> "1" Airport : departureAirport
Flight --> "1" Airport : arrivalAirport

class Airport {
  - String id
  - List<City> servesForCities

  //getter，setter
  + String getId()
  + List<City> getCities()

  //key operations
  + void addCity(City c)
}

class City

Airport --> "1..*" City : servesForCities

class Customer {
  - List<Booking> bookings

  //getter,setter
  + List<Booking> getBookings()
  + void setBookings(List<Booking> bookings)

  //key operations
  + boolean confirm(String reservationID, Date now)
  + boolean cancel(String reservationID, Date now)
  + boolean addBooking(Flight f, Date now, List<String> listOfPassengerNames)

}

class Passenger {
  - String name
  //getter,setter
  + String getName()
  + void setName(String name)
}

class Booking {
  - Customer customer
  - List<Reservation> reservations

  //getter,setter
  + Customer getCustomer()
  + void setCustomer(Customer customer)
  + List<Reservation> getReservations()
  + void setReservations(List<Reservation> reservations)

  //key operations
  + boolean createReservation(Flight f, String passenger, Date now)
}

enum ReservationStatus {
    PENDING
    CONFIRMED
    CANCELED
}

class Reservation {
  - String id
  - ReservationStatus status
  - Passenger passenger
  - Flight flight

  //getter,setter
  + String getId()
  + void setFlight(Flight flight)
  + ReservationStatus getStatus()
  + void setStatus(ReservationStatus s)
  + Passenger getPassenger()
  + void setPassenger(Passenger passenger)
  + Flight getFlight()
  + void setFlight(Flight flight)
}

Booking *-- "1..*" Reservation : reservations
Reservation --> "1" Passenger : passenger
Reservation --> "1" Flight : flight
Booking --> "1" Customer : customer
Customer --> "*"  Booking : bookings
```
// ==end==