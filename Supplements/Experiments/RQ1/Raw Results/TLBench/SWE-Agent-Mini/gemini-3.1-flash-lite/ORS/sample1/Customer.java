import java.util.*;

public class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}

    public MembershipPackage getMembershipPackage() { return membershipPackage; }
    public void setMembershipPackage(MembershipPackage m) { this.membershipPackage = m; }
    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking b) { bookings.add(b); }
    
    public void bookTrip(Trip t, int seats) {
        Booking b = new Booking();
        b.setCustomer(this);
        b.setTrip(t);
        b.setNumberOfSeats(seats);
        b.setBookingDate(new Date());
        if (b.isBookingEligible()) {
            t.addBooking(b);
            addBooking(b);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        int total = 0;
        for (Booking b : bookings) {
            if (b.isInMonth(currentMonth)) {
                total += b.getNumberOfSeats() * 5;
            }
        }
        return total;
    }
}
