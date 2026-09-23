// ==version1==
Online Rideshare System (ORS) manages shared road trips between drivers and customers. Every user is identified by an id and also stores an email address and a phone number so that the platform can distinguish and contact the user.

A driver can publish trips. A trip records a departure station, an arrival station, the number of available seats, a departure date, a departure time, an arrival time, and a price. Trips can be direct or indirect. An indirect trip contains one or more stops, and each stop stores the name of its stop station.

A customer can create bookings for trips. A booking records the requested number of seats, the booking date, the related customer, and the related trip. A booking is valid only when the trip exists, enough seats are still available, the customer has no overlapping booking on the same day, and the booking is made more than two hours before the trip departure time. When a booking is accepted, the trip seat inventory must be reduced accordingly.

Customers may subscribe to a membership package. A membership package stores one or more rewards, and each reward is one of cashback, discounts, or points. A customer with a discount reward receives a 20 percent discount only when the booking is made at least 24 hours before departure. A customer with a points reward earns 5 points per booked seat for bookings created within the requested month.

A driver may publish a new trip only when the proposed trip has a valid time window and does not overlap with the driver’s existing trips. Two indirect trips posted by the same driver are considered overlapping by stops when they share at least one stop station, and stop-name comparison is case-sensitive.
// ==end==
