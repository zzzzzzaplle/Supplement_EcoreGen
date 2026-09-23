// ==version1==
1. Validate Booking Eligibility.
The system shall determine whether a customer can book seats on a trip. The trip must exist, the trip must still have enough available seats, the requested seats must not exceed the remaining seats, and the customer must not already hold another booking whose trip time overlaps on the same day. The booking time must be strictly earlier than the departure time by more than two hours, so a booking made exactly two hours before departure is rejected. When the booking is accepted, the trip seat count shall be reduced. Invalid inputs such as a null customer, null trip, or null booking date shall return `false`.

2. Calculate Discounted Trip Price.
The system shall calculate the final price of a booking after reward evaluation. A fixed 20 percent discount applies only when the customer owns a membership package containing the `DISCOUNTS` award and the booking is made at least 24 hours before departure. The returned amount shall keep one decimal place. When the customer, membership package, booking time, or reward condition is invalid or missing, the original trip price shall be returned.

3. Check Stop Overlap for Indirect Trips.
The system shall determine whether two indirect trips posted by the same driver share at least one common stop station. The comparison is case-sensitive. If either trip is null or one of the stop collections is empty, the result shall be `false`.

4. Compute Monthly Reward Points.
The system shall compute the reward points earned by a customer during a given month. Only customers whose membership package contains the `POINTS` award are eligible. Only bookings whose booking date falls inside the requested month shall be counted. Each booked seat contributes 5 points. Invalid inputs, missing membership packages, or unsupported rewards shall return `0`.

5. Validate Trip Posting Feasibility.
The system shall validate whether a driver can publish a new trip. The proposed trip must exist, its departure time and arrival time must be valid, and the departure time must be earlier than the arrival time. The new trip must not overlap with any existing trip of the driver. Completely identical time periods are conflicts, while adjacent boundaries where one trip ends exactly when another starts are allowed. Invalid inputs shall return `false`.
// ==end==
