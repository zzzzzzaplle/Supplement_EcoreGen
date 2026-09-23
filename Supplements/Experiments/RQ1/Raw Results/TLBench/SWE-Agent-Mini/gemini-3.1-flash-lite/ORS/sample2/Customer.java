import java.util.*;

public class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}

    public MembershipPackage getMembershipPackage() { return membershipPackage; }
    public void setMembershipPackage(MembershipPackage membershipPackage) { this.membershipPackage = membershipPackage; }
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    public void addBooking(Booking booking) { bookings.add(booking); }
    
    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking b = new Booking();
        b.setCustomer(this);
        b.setTrip(trip);
        b.setNumberOfSeats(numberOfSeats);
        b.setBookingDate(new Date());

        if (b.isBookingEligible()) {
            b.updateTripSeats();
            this.addBooking(b);
            trip.addBooking(b);
        }
    }
    
    public int computeMonthlyRewardPoints(String currentMonth) {
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) return 0;
        
        int points = 0;
        for (Booking b : bookings) {
            if (b.isInMonth(currentMonth)) {
                points += b.getNumberOfSeats() * 5;
            }
        }
        return points;
    }
}
