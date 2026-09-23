import java.util.ArrayList;
import java.util.List;

class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings;

    public Customer() {
        bookings = new ArrayList<Booking>();
    }

    public MembershipPackage getMembershipPackage() {
        return membershipPackage;
    }

    public void setMembershipPackage(MembershipPackage membershipPackage) {
        this.membershipPackage = membershipPackage;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (bookings == null) {
            bookings = new ArrayList<Booking>();
        }
        bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null || membershipPackage == null || !membershipPackage.hasAward(Award.POINTS) || bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking booking : bookings) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                total += booking.getNumberOfSeats() * 5;
            }
        }
        return total;
    }
}
