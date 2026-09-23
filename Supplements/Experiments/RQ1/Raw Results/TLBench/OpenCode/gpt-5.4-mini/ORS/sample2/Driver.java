class Driver extends User {
    private java.util.List<Trip> trips;

    public Driver() {
        this.trips = new java.util.ArrayList<Trip>();
    }

    public java.util.List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(java.util.List<Trip> trips) {
        this.trips = trips;
    }

    public void addTrip(Trip trip) {
        if (trips == null) {
            trips = new java.util.ArrayList<Trip>();
        }
        trips.add(trip);
    }

    public boolean checkStopOverlap(Trip trip1, Trip trip2) {
        if (trip1 == null || trip2 == null) {
            return false;
        }
        java.util.Set<String> stops1 = trip1.getStopStations();
        java.util.Set<String> stops2 = trip2.getStopStations();
        if (stops1 == null || stops2 == null || stops1.isEmpty() || stops2.isEmpty()) {
            return false;
        }
        for (String stop : stops1) {
            if (stops2.contains(stop)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPostTrip(Trip newTrip) {
        if (newTrip == null) {
            return false;
        }
        if (!newTrip.hasValidTimeWindow()) {
            return false;
        }
        if (trips == null) {
            return true;
        }
        for (Trip trip : trips) {
            if (trip != null && trip.isTimeConflicting(newTrip.getDepartureTime(), newTrip.getArrivalTime())) {
                return false;
            }
        }
        return true;
    }
}
