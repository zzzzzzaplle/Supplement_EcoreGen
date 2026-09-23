class Customer extends User {
    private MembershipPackage membershipPackage;
    private java.util.List<Booking> bookings;

    public Customer() {
        this.bookings = new java.util.ArrayList<Booking>();
    }

    public MembershipPackage getMembershipPackage() {
        return membershipPackage;
    }

    public void setMembershipPackage(MembershipPackage membershipPackage) {
        this.membershipPackage = membershipPackage;
    }

    public java.util.List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(java.util.List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        if (bookings == null) {
            bookings = new java.util.ArrayList<Booking>();
        }
        bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new java.util.Date());
        if (booking.isBookingEligible()) {
            addBooking(booking);
            booking.updateTripSeats();
            if (trip != null) {
                trip.addBooking(booking);
            }
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (bookings == null || currentMonth == null || membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) {
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
