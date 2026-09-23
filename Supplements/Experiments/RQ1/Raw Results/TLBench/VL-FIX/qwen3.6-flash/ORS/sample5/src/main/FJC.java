import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Online Rideshare System (ORS)
 */
 class FJC {

    // Common date time format for parsing strings from Trip
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 1. Validate Booking Eligibility.
     * The system shall determine whether a customer can book seats on a trip.
     */
    public boolean validateBookingEligibility(Customer customer, Trip trip, LocalDateTime bookingDate, int requestedSeats) {
        // Invalid inputs such as a null customer, null trip, or null booking date shall return false
        if (customer == null || trip == null || bookingDate == null) {
            return false;
        }

        // The requested seats must not exceed the remaining seats
        if (requestedSeats <= 0) {
            return false;
        }

        // The trip must still have enough available seats
        if (trip.getAvailableSeats() < requestedSeats) {
            return false;
        }

        // The booking time must be strictly earlier than the departure time by more than two hours
        String departureTimeStr = trip.getDepartureTime();
        if (departureTimeStr == null || departureTimeStr.isEmpty()) {
            return false;
        }

        LocalDateTime departureTime;
        try {
            departureTime = LocalDateTime.parse(departureTimeStr, TIME_FORMAT);
        } catch (DateTimeParseException e) {
            return false;
        }

        if (departureTime == null) {
            return false;
        }

        Duration timeBeforeDeparture = Duration.between(bookingDate, departureTime);
        
        // More than two hours means duration > 2 hours.
        // timeBeforeDeparture.compareTo(Duration.ofHours(2)) > 0
        if (timeBeforeDeparture.compareTo(Duration.ofHours(2)) <= 0) {
            return false;
        }

        // The customer must not already hold another booking whose trip time overlaps on the same day.
        if (customer.hasOverlappingBooking(trip)) {
            return false;
        }

        return true;
    }

    /**
     * 2. Calculate Discounted Trip Price.
     * The system shall calculate the final price of a booking after reward evaluation.
     */
    public double calculateDiscountedTripPrice(Customer customer, Trip trip, LocalDateTime bookingDate) {
        if (customer == null || trip == null || bookingDate == null) {
            return trip.getPrice();
        }

        double originalPrice = trip.getPrice();
        if (originalPrice <= 0) {
            return 0.0;
        }

        // A fixed 20 percent discount applies only when the customer owns a membership package 
        // containing the DISCOUNTS award and the booking is made at least 24 hours before departure.
        MembershipPackage membership = customer.getMembershipPackage();
        if (membership == null) {
            return originalPrice;
        }

        // Check if membership contains DISCOUNTS reward
        boolean hasDiscount = membership.hasReward(RewardType.DISCOUNTS);
        if (!hasDiscount) {
            return originalPrice;
        }

        // Check if booking is made at least 24 hours before departure
        String departureTimeStr = trip.getDepartureTime();
        if (departureTimeStr == null || departureTimeStr.isEmpty()) {
            return originalPrice;
        }

        LocalDateTime departureTime;
        try {
            departureTime = LocalDateTime.parse(departureTimeStr, TIME_FORMAT);
        } catch (DateTimeParseException e) {
            return originalPrice;
        }

        if (departureTime == null) {
            return originalPrice;
        }

        Duration timeBeforeDeparture = Duration.between(bookingDate, departureTime);
        
        // At least 24 hours means >= 24 hours
        if (timeBeforeDeparture.compareTo(Duration.ofHours(24)) < 0) {
            return originalPrice;
        }

        // Apply 20% discount
        double discountedPrice = originalPrice * 0.8;

        // Keep one decimal place
        return Math.round(discountedPrice * 10.0) / 10.0;
    }

    /**
     * 3. Check Stop Overlap for Indirect Trips.
     * The system shall determine whether two indirect trips posted by the same driver share at least one common stop station.
     */
    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }

        List<Stop> stops1 = trip1.getStops();
        List<Stop> stops2 = trip2.getStops();

        if (stops1 == null || stops1.isEmpty() || stops2 == null || stops2.isEmpty()) {
            return false;
        }

        for (Stop stop1 : stops1) {
            String stationName1 = stop1.getStationName();
            if (stationName1 == null) {
                continue;
            }
            for (Stop stop2 : stops2) {
                String stationName2 = stop2.getStationName();
                if (stationName2 == null) {
                    continue;
                }
                // Comparison is case-sensitive
                if (Objects.equals(stationName1, stationName2)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 4. Compute Monthly Reward Points.
     * The system shall compute the reward points earned by a customer during a given month.
     */
    public int computeMonthlyRewardPoints(Customer customer, LocalDateTime monthStart, LocalDateTime monthEnd) {
        if (customer == null || monthStart == null || monthEnd == null) {
            return 0;
        }

        MembershipPackage membership = customer.getMembershipPackage();
        if (membership == null) {
            return 0;
        }

        // Only customers whose membership package contains the POINTS award are eligible
        if (!membership.hasReward(RewardType.POINTS)) {
            return 0;
        }

        List<Booking> bookings = customer.getBookings();
        if (bookings == null || bookings.isEmpty()) {
            return 0;
        }

        int totalPoints = 0;
        for (Booking booking : bookings) {
            if (booking == null) {
                continue;
            }

            LocalDateTime bookingDate = booking.getBookingDate();
            if (bookingDate == null) {
                continue;
            }

            // Only bookings whose booking date falls inside the requested month shall be counted
            // monthStart is inclusive, monthEnd is exclusive (typical for months)
            // bookingDate >= monthStart && bookingDate < monthEnd
            if (!bookingDate.isBefore(monthStart) && !bookingDate.isEqual(monthEnd) && !bookingDate.isAfter(monthEnd)) {
                // Check if booking is valid/accepted
                if (booking.isAccepted()) {
                    int seats = booking.getRequestedSeats();
                    if (seats > 0) {
                        totalPoints += seats * 5;
                    }
                }
            }
        }

        return totalPoints;
    }

    /**
     * 5. Validate Trip Posting Feasibility.
     * The system shall validate whether a driver can publish a new trip.
     */
    public boolean validateTripPostingFeasibility(Driver driver, Trip newTrip, List<Trip> existingTrips) {
        if (driver == null || newTrip == null || existingTrips == null) {
            return false;
        }

        // The proposed trip must exist (non-null checked above)
        // Its departure time and arrival time must be valid
        String departureTimeStr = newTrip.getDepartureTime();
        String arrivalTimeStr = newTrip.getArrivalTime();

        if (departureTimeStr == null || departureTimeStr.isEmpty() || 
            arrivalTimeStr == null || arrivalTimeStr.isEmpty()) {
            return false;
        }

        LocalDateTime departureTime;
        LocalDateTime arrivalTime;
        
        try {
            departureTime = LocalDateTime.parse(departureTimeStr, TIME_FORMAT);
            arrivalTime = LocalDateTime.parse(arrivalTimeStr, TIME_FORMAT);
        } catch (DateTimeParseException e) {
            return false;
        }

        if (departureTime == null || arrivalTime == null) {
            return false;
        }

        // The departure time must be earlier than the arrival time
        if (!departureTime.isBefore(arrivalTime)) {
            return false;
        }

        // The new trip must not overlap with any existing trip of the driver.
        // Completely identical time periods are conflicts, while adjacent boundaries where one trip ends exactly when another starts are allowed.
        for (Trip existingTrip : existingTrips) {
            if (existingTrip == null) {
                continue;
            }

            String existingDepartureStr = existingTrip.getDepartureTime();
            String existingArrivalStr = existingTrip.getArrivalTime();

            if (existingDepartureStr == null || existingDepartureStr.isEmpty() || 
                existingArrivalStr == null || existingArrivalStr.isEmpty()) {
                continue;
            }

            LocalDateTime existingDeparture;
            LocalDateTime existingArrival;
            
            try {
                existingDeparture = LocalDateTime.parse(existingDepartureStr, TIME_FORMAT);
                existingArrival = LocalDateTime.parse(existingArrivalStr, TIME_FORMAT);
            } catch (DateTimeParseException e) {
                continue;
            }

            if (existingDeparture == null || existingArrival == null) {
                continue;
            }

            // Check for overlap
            // Two trips overlap if one starts before the other ends AND the other starts before the first ends.
            // Adjacent boundaries are allowed: e.g., trip1 ends at T, trip2 starts at T -> no overlap.
            // So overlap exists if: newDeparture < existingArrival AND existingDeparture < newArrival
            boolean overlaps = departureTime.isBefore(existingArrival) && existingDeparture.isBefore(arrivalTime);
            if (overlaps) {
                return false;
            }
        }

        return true;
    }
}