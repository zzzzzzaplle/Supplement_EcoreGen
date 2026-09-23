class Trip {
    private String departureStation;
    private String arrivalStation;
    private int numberOfSeats;
    private java.util.Date departureDate;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private java.util.List<Booking> bookings;
    private java.util.List<Stop> stops;

    public Trip() {
        this.bookings = new java.util.ArrayList<Booking>();
        this.stops = new java.util.ArrayList<Stop>();
    }

    public boolean hasValidTimeWindow() {
        if (departureTime == null || arrivalTime == null) {
            return false;
        }
        return departureTime.compareTo(arrivalTime) < 0;
    }

    public double calculateDiscountedPrice(Customer customer, String bookingTime) {
        if (customer == null || bookingTime == null || departureTime == null || membershipMissing(customer)) {
            return price;
        }
        MembershipPackage membershipPackage = customer.getMembershipPackage();
        if (membershipPackage == null || !membershipPackage.hasAward(Award.DISCOUNTS)) {
            return price;
        }
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        format.setLenient(false);
        try {
            java.util.Date bookingDate = format.parse(bookingTime);
            java.util.Date departureMoment = format.parse(format.format(departureDate) + " " + departureTime);
            long diff = departureMoment.getTime() - bookingDate.getTime();
            if (diff >= 24L * 60L * 60L * 1000L) {
                return Math.round((price * 0.8d) * 10.0d) / 10.0d;
            }
        } catch (Exception e) {
            return price;
        }
        return price;
    }

    private boolean membershipMissing(Customer customer) {
        return customer == null;
    }

    public int getBookedSeats() {
        if (bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking booking : bookings) {
            if (booking != null) {
                total += booking.getNumberOfSeats();
            }
        }
        return total;
    }

    public java.util.List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (bookings == null) {
            bookings = new java.util.ArrayList<Booking>();
        }
        bookings.add(booking);
    }

    public int calculateMonthlyPoints(Customer customer, String currentMonth) {
        if (customer == null || currentMonth == null || customer.getMembershipPackage() == null || !customer.getMembershipPackage().hasAward(Award.POINTS)) {
            return 0;
        }
        return customer.computeMonthlyRewardPoints(currentMonth);
    }

    public java.util.Set<String> getStopStations() {
        java.util.Set<String> stations = new java.util.HashSet<String>();
        if (stops == null) {
            return stations;
        }
        for (Stop stop : stops) {
            if (stop != null && stop.getStopStation() != null) {
                stations.add(stop.getStopStation());
            }
        }
        return stations;
    }

    public boolean isTimeConflicting(String newDepartureTime, String newArrivalTime) {
        if (!hasValidTimeWindow() || newDepartureTime == null || newArrivalTime == null) {
            return false;
        }
        return newDepartureTime.compareTo(arrivalTime) < 0 && departureTime.compareTo(newArrivalTime) < 0;
    }

    public String getDepartureStation() { return departureStation; }
    public void setDepartureStation(String departureStation) { this.departureStation = departureStation; }
    public String getArrivalStation() { return arrivalStation; }
    public void setArrivalStation(String arrivalStation) { this.arrivalStation = arrivalStation; }
    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    public java.util.Date getDepartureDate() { return departureDate; }
    public void setDepartureDate(java.util.Date departureDate) { this.departureDate = departureDate; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public java.util.List<Stop> getStops() { return stops; }
    public void addStop(Stop stop) { if (stops == null) { stops = new java.util.ArrayList<Stop>(); } stops.add(stop); }
}
