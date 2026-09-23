# Task D - Review Progress

## 1. Overview

| ID | benchmark | system | model | sample | Report Status | Failed Test Cases | Extracted Failed Cases | Root Cause Analysis Done | Error Type | Review Notes |
|---|---|---|---|---|---|---|---|---|---|---|
| OLRS001 | TLBench | OLRS | deepseek-v4-flash | olrs2 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS002 | TLBench | OLRS | deepseek-v4-flash | olrs3 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS003 | TLBench | OLRS | deepseek-v4-flash | olrs4 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS004 | TLBench | OLRS | deepseek-v4-flash | olrs5 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS005 | TLBench | OLRS | gpt-5.4-mini | olrs1 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS006 | TLBench | OLRS | gpt-5.4-mini | olrs2 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS007 | TLBench | OLRS | gpt-5.4-mini | olrs3 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS008 | TLBench | OLRS | gpt-5.4-mini | olrs4 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS009 | TLBench | OLRS | gpt-5.4-mini | olrs5 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS010 | TLBench | OLRS | gemini-3.1-flash-lite | olrs1 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS011 | TLBench | OLRS | gemini-3.1-flash-lite | olrs2 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS012 | TLBench | OLRS | gemini-3.1-flash-lite | olrs3 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS013 | TLBench | OLRS | gemini-3.1-flash-lite | olrs5 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS014 | TLBench | OLRS | minimax-m3 | olrs1 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS015 | TLBench | OLRS | minimax-m3 | olrs2 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS016 | TLBench | OLRS | minimax-m3 | olrs3 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS017 | TLBench | OLRS | minimax-m3 | olrs4 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS018 | TLBench | OLRS | minimax-m3 | olrs5 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS019 | TLBench | OLRS | qwen3.6-flash | olrs1 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS020 | TLBench | OLRS | qwen3.6-flash | olrs2 | Test Failure | 2 |  | Yes | Business logic error | Passed 23/25, pass_rate=0.920 |
| OLRS021 | TLBench | OLRS | qwen3.6-flash | olrs3 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS022 | TLBench | OLRS | qwen3.6-flash | olrs4 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OLRS023 | TLBench | OLRS | qwen3.6-flash | olrs5 | Test Failure | 1 |  | Yes | Missing precondition check | Passed 24/25, pass_rate=0.960 |
| OPMS001 | TLBench | OPMS | gemini-3.1-flash-lite | project5 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| OPMS002 | TLBench | OPMS | minimax-m3 | project4 | Test Failure | 1 |  | Yes | Null pointer exception | Passed 24/25, pass_rate=0.960 |
| OPMS003 | TLBench | OPMS | qwen3.6-flash | project1 | Test Failure | 1 |  | Yes | Return value format error | Passed 24/25, pass_rate=0.960 |
| OPMS004 | TLBench | OPMS | qwen3.6-flash | project2 | Test Failure | 1 |  | Yes | Return value format error | Passed 24/25, pass_rate=0.960 |
| OPMS005 | TLBench | OPMS | qwen3.6-flash | project3 | Test Failure | 1 |  | Yes | Return value format error | Passed 24/25, pass_rate=0.960 |
| OPRS001 | TLBench | OPRS | deepseek-v4-flash | conference5 | Test Failure | 4 |  | Yes | Business logic error | Passed 29/33, pass_rate=0.879 |
| OPRS002 | TLBench | OPRS | qwen3.6-flash | conference1 | Test Failure | 4 |  | Yes | Business logic error | Passed 29/33, pass_rate=0.879 |
| OPRS003 | TLBench | OPRS | qwen3.6-flash | conference4 | Test Failure | 4 |  | Yes | Business logic error | Passed 29/33, pass_rate=0.879 |
| ORS001 | TLBench | ORS | deepseek-v4-flash | rideshare1 | Test Failure | 2 |  | Yes | Business logic error | Passed 33/35, pass_rate=0.943 |
| ORS002 | TLBench | ORS | deepseek-v4-flash | rideshare2 | Test Failure | 8 |  | Yes | Business logic error+Null pointer | Passed 27/35, pass_rate=0.771 |
| ORS003 | TLBench | ORS | deepseek-v4-flash | rideshare3 | Test Failure | 2 |  | Yes | Business logic error | Passed 33/35, pass_rate=0.943 |
| ORS004 | TLBench | ORS | deepseek-v4-flash | rideshare4 | Test Failure | 2 |  | Yes | Business logic error | Passed 33/35, pass_rate=0.943 |
| ORS005 | TLBench | ORS | deepseek-v4-flash | rideshare5 | Test Failure | 6 |  | Yes | Business logic error+Null pointer | Passed 29/35, pass_rate=0.829 |
| ORS006 | TLBench | ORS | gpt-5.4-mini | rideshare1 | Test Failure | 8 |  | Yes | Business logic error | Passed 27/35, pass_rate=0.771 |
| ORS007 | TLBench | ORS | gpt-5.4-mini | rideshare2 | Test Failure | 10 |  | Yes | Business logic error | Passed 25/35, pass_rate=0.714 |
| ORS008 | TLBench | ORS | gpt-5.4-mini | rideshare3 | Test Failure | 8 |  | Yes | Business logic error | Passed 27/35, pass_rate=0.771 |
| ORS009 | TLBench | ORS | gpt-5.4-mini | rideshare4 | Test Failure | 6 |  | Yes | Business logic error | Passed 29/35, pass_rate=0.829 |
| ORS010 | TLBench | ORS | gpt-5.4-mini | rideshare5 | Test Failure | 10 |  | Yes | Business logic error | Passed 25/35, pass_rate=0.714 |
| ORS011 | TLBench | ORS | gemini-3.1-flash-lite | rideshare1 | Test Failure | 5 |  | Yes | Business logic error | Passed 30/35, pass_rate=0.857 |
| ORS012 | TLBench | ORS | gemini-3.1-flash-lite | rideshare2 | Test Failure | 4 |  | Yes | Business logic error | Passed 31/35, pass_rate=0.886 |
| ORS013 | TLBench | ORS | gemini-3.1-flash-lite | rideshare3 | Test Failure | 4 |  | Yes | Business logic error | Passed 31/35, pass_rate=0.886 |
| ORS014 | TLBench | ORS | gemini-3.1-flash-lite | rideshare4 | Compile Failure | N/A |  |  |  | Overall status compile_failed |
| ORS015 | TLBench | ORS | gemini-3.1-flash-lite | rideshare5 | Compile Failure | N/A |  |  |  | Overall status compile_failed |
| ORS016 | TLBench | ORS | minimax-m3 | rideshare2 | Test Failure | 2 |  | Yes | Business logic error | Passed 33/35, pass_rate=0.943 |
| ORS017 | TLBench | ORS | minimax-m3 | rideshare3 | Test Failure | 4 |  | Yes | Business logic error+Null pointer | Passed 31/35, pass_rate=0.886 |
| ORS018 | TLBench | ORS | minimax-m3 | rideshare4 | Test Failure | 2 |  | Yes | Business logic error | Passed 33/35, pass_rate=0.943 |
| ORS019 | TLBench | ORS | minimax-m3 | rideshare5 | Test Failure | 6 |  | Yes | Business logic error+Null pointer | Passed 29/35, pass_rate=0.829 |
| ORS020 | TLBench | ORS | qwen3.6-flash | rideshare1 | Compile Failure | N/A |  |  |  | Overall status compile_failed |
| ORS021 | TLBench | ORS | qwen3.6-flash | rideshare2 | Test Failure | 11 |  | Yes | Date parse error+Business logic error | Passed 24/35, pass_rate=0.686 |
| ORS022 | TLBench | ORS | qwen3.6-flash | rideshare3 | Compile Failure | N/A |  |  |  | Overall status compile_failed |
| ORS023 | TLBench | ORS | qwen3.6-flash | rideshare4 | Test Failure | 10 |  | Yes | Date parse error+Business logic error | Passed 25/35, pass_rate=0.714 |
| ORS024 | TLBench | ORS | qwen3.6-flash | rideshare5 | Test Failure | 8 |  | Yes | Date parse error+Business logic error | Passed 27/35, pass_rate=0.771 |
| R123001 | TLBench | R123_School | minimax-m3 | trainingSchool3 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R123002 | TLBench | R123_School | qwen3.6-flash | trainingSchool2 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R123003 | TLBench | R123_School | qwen3.6-flash | trainingSchool4 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R132001 | TLBench | R132_MunicipalLibrary | minimax-m3 | municipalLibrary4 | Test Failure | 6 |  | Yes | Null pointer exception | Passed 19/25, pass_rate=0.760 |
| R132002 | TLBench | R132_MunicipalLibrary | qwen3.6-flash | municipalLibrary1 | Test Failure | 3 |  | Yes | Business logic error | Passed 22/25, pass_rate=0.880 |
| R132003 | TLBench | R132_MunicipalLibrary | qwen3.6-flash | municipalLibrary4 | Test Failure | 12 |  | Yes | Business logic error+Null pointer | Passed 13/25, pass_rate=0.520 |
| R144001 | TLBench | R144_AirlineFlights | deepseek-v4-flash | flights1 | Test Failure | 2 |  | Yes | Business logic error | Passed 23/25, pass_rate=0.920 |
| R144002 | TLBench | R144_AirlineFlights | deepseek-v4-flash | flights2 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R144003 | TLBench | R144_AirlineFlights | deepseek-v4-flash | flights3 | Test Failure | 2 |  | Yes | Business logic error | Passed 23/25, pass_rate=0.920 |
| R144004 | TLBench | R144_AirlineFlights | deepseek-v4-flash | flights4 | Test Failure | 2 |  | Yes | Business logic error | Passed 23/25, pass_rate=0.920 |
| R144005 | TLBench | R144_AirlineFlights | deepseek-v4-flash | flights5 | Test Failure | 2 |  | Yes | Business logic error | Passed 23/25, pass_rate=0.920 |
| R144006 | TLBench | R144_AirlineFlights | gpt-5.4-mini | flights1 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R144007 | TLBench | R144_AirlineFlights | gpt-5.4-mini | flights2 | Test Failure | 2 |  | Yes | Business logic error | Passed 23/25, pass_rate=0.920 |
| R144008 | TLBench | R144_AirlineFlights | gpt-5.4-mini | flights4 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R144009 | TLBench | R144_AirlineFlights | gpt-5.4-mini | flights5 | Test Failure | 4 |  | Yes | Business logic error | Passed 21/25, pass_rate=0.840 |
| R144010 | TLBench | R144_AirlineFlights | gemini-3.1-flash-lite | flights1 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R144011 | TLBench | R144_AirlineFlights | gemini-3.1-flash-lite | flights2 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R144012 | TLBench | R144_AirlineFlights | gemini-3.1-flash-lite | flights3 | Test Failure | 2 |  | Yes | Business logic error | Passed 23/25, pass_rate=0.920 |
| R144013 | TLBench | R144_AirlineFlights | gemini-3.1-flash-lite | flights4 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R144014 | TLBench | R144_AirlineFlights | gemini-3.1-flash-lite | flights5 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R144015 | TLBench | R144_AirlineFlights | minimax-m3 | flights1 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R144016 | TLBench | R144_AirlineFlights | minimax-m3 | flights2 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R144017 | TLBench | R144_AirlineFlights | minimax-m3 | flights3 | Test Failure | 1 |  | Yes | Business logic error | Passed 24/25, pass_rate=0.960 |
| R144018 | TLBench | R144_AirlineFlights | minimax-m3 | flights5 | Test Failure | 2 |  | Yes | Business logic error | Passed 23/25, pass_rate=0.920 |
| R144019 | TLBench | R144_AirlineFlights | qwen3.6-flash | flights1 | Compile Failure | N/A |  |  |  | Overall status compile_failed |
| R144020 | TLBench | R144_AirlineFlights | qwen3.6-flash | flights2 | Test Failure | 3 |  | Yes | Business logic error | Passed 22/25, pass_rate=0.880 |
| R144021 | TLBench | R144_AirlineFlights | qwen3.6-flash | flights3 | Test Failure | 2 |  | Yes | Business logic error | Passed 23/25, pass_rate=0.920 |
| R144022 | TLBench | R144_AirlineFlights | qwen3.6-flash | flights4 | Compile Failure | N/A |  |  |  | Overall status compile_failed |
| R144023 | TLBench | R144_AirlineFlights | qwen3.6-flash | flights5 | Test Failure | 3 |  | Yes | Business logic error | Passed 22/25, pass_rate=0.880 |
| R2001 | TLBench | R2_EmployeeManagementSystem | deepseek-v4-flash | employee1 | Test Failure | 2 |  | Yes | Null pointer exception | Passed 23/25, pass_rate=0.920 |
| R2002 | TLBench | R2_EmployeeManagementSystem | deepseek-v4-flash | employee3 | Test Failure | 1 |  | Yes | Null pointer exception | Passed 24/25, pass_rate=0.960 |
| R2003 | TLBench | R2_EmployeeManagementSystem | deepseek-v4-flash | employee5 | Test Failure | 1 |  | Yes | Null pointer exception | Passed 24/25, pass_rate=0.960 |
| R2004 | TLBench | R2_EmployeeManagementSystem | minimax-m3 | employee1 | Test Failure | 1 |  | Yes | Null pointer exception | Passed 24/25, pass_rate=0.960 |
| R2005 | TLBench | R2_EmployeeManagementSystem | minimax-m3 | employee3 | Test Failure | 2 |  | Yes | Null pointer exception | Passed 23/25, pass_rate=0.920 |
| R22001 | TLBench | R22_IPOApplication | deepseek-v4-flash | ipo1 | Test Failure | 1 |  | Yes | Business logic error | Passed 25/26, pass_rate=0.962 |
| R22002 | TLBench | R22_IPOApplication | deepseek-v4-flash | ipo4 | Test Failure | 4 |  | Yes | Business logic error | Passed 22/26, pass_rate=0.846 |
| R22003 | TLBench | R22_IPOApplication | deepseek-v4-flash | ipo5 | Test Failure | 1 |  | Yes | Business logic error | Passed 25/26, pass_rate=0.962 |
| R22004 | TLBench | R22_IPOApplication | gpt-5.4-mini | ipo1 | Test Failure | 1 |  | Yes | Business logic error | Passed 25/26, pass_rate=0.962 |
| R22005 | TLBench | R22_IPOApplication | gpt-5.4-mini | ipo2 | Test Failure | 1 |  | Yes | Business logic error | Passed 25/26, pass_rate=0.962 |
| R22006 | TLBench | R22_IPOApplication | gpt-5.4-mini | ipo3 | Test Failure | 1 |  | Yes | Business logic error | Passed 25/26, pass_rate=0.962 |
| R22007 | TLBench | R22_IPOApplication | gemini-3.1-flash-lite | ipo1 | Test Failure | 1 |  | Yes | Business logic error | Passed 25/26, pass_rate=0.962 |
| R22008 | TLBench | R22_IPOApplication | gemini-3.1-flash-lite | ipo2 | Test Failure | 2 |  | Yes | Business logic error | Passed 24/26, pass_rate=0.923 |
| R22009 | TLBench | R22_IPOApplication | gemini-3.1-flash-lite | ipo3 | Test Failure | 2 |  | Yes | Business logic error | Passed 24/26, pass_rate=0.923 |
| R22010 | TLBench | R22_IPOApplication | gemini-3.1-flash-lite | ipo4 | Test Failure | 1 |  | Yes | Business logic error | Passed 25/26, pass_rate=0.962 |
| R22011 | TLBench | R22_IPOApplication | gemini-3.1-flash-lite | ipo5 | Test Failure | 2 |  | Yes | Business logic error | Passed 24/26, pass_rate=0.923 |
| R22012 | TLBench | R22_IPOApplication | minimax-m3 | ipo2 | Test Failure | 1 |  | Yes | Business logic error | Passed 25/26, pass_rate=0.962 |
| R22013 | TLBench | R22_IPOApplication | minimax-m3 | ipo3 | Test Failure | 3 |  | Yes | Business logic error | Passed 23/26, pass_rate=0.885 |
| R22014 | TLBench | R22_IPOApplication | minimax-m3 | ipo4 | Test Failure | 1 |  | Yes | Business logic error | Passed 25/26, pass_rate=0.962 |
| R22015 | TLBench | R22_IPOApplication | minimax-m3 | ipo5 | Test Failure | 1 |  | Yes | Business logic error | Passed 25/26, pass_rate=0.962 |
| R22016 | TLBench | R22_IPOApplication | qwen3.6-flash | ipo1 | Test Failure | 1 |  | Yes | Business logic error | Passed 25/26, pass_rate=0.962 |
| R22017 | TLBench | R22_IPOApplication | qwen3.6-flash | ipo2 | Test Failure | 4 |  | Yes | Business logic error | Passed 22/26, pass_rate=0.846 |
| R22018 | TLBench | R22_IPOApplication | qwen3.6-flash | ipo4 | Test Failure | 25 |  | Yes | Null pointer exception+Business logic error | Passed 1/26, pass_rate=0.039 |
| R22019 | TLBench | R22_IPOApplication | qwen3.6-flash | ipo5 | Test Failure | 3 |  | Yes | Business logic error | Passed 23/26, pass_rate=0.885 |

## 2. Compilation Error Section

> Records compilation error phenomena and summarizes compilation error information.

| ID | Related Files | Compilation Error Message | Further Analysis/Categorization |
|---|---|---|---|
| ORS014 | `ORS/gemini-3.1-flash-lite/rideshare4/Booking.java` | `Cannot find symbol: variable otherTrip` (4 errors) - `isTimeConflictingWith()` method body references undeclared `otherTrip` variable | LLM hallucination: method parameter named `otherBooking` but code uses `otherTrip` |
| ORS015 | `ORS/gemini-3.1-flash-lite/rideshare5/Booking.java` | `Cannot find symbol: variable otherTrip` (4 errors) - Same as above, identical error pattern to rideshare4 | LLM hallucination: Same model, same hallucination pattern |
| ORS020 | `ORS/qwen3.6-flash/rideshare1/Driver.java` | `Incompatible types: String cannot be converted to Date` (2 errors) - `getDepartureTime()`/`getArrivalTime()` return String but code casts to Date | Type misunderstanding: LLM assumes time fields are Date type, but actual EMF model uses String |
| ORS022 | `ORS/qwen3.6-flash/rideshare3/Trip.java` | `Cannot find symbol: class EDate` (1 error) - `import edu.rideshare.EDate` does not exist | LLM hallucination: invented non-existent `EDate` class |
| R144019 | `R144_AirlineFlights/qwen3.6-flash/flights1/Airline.java` + `Booking.java` | `EList<City> cannot be converted to EList<String>` (2 errors) + `EEnumLiteral cannot be converted to ReservationStatus` (1 error) | Type misunderstanding: LLM assumes airport.getCities() returns `EList<String>` but actually returns `EList<City>`; enum setting uses wrong API |
| R144022 | `R144_AirlineFlights/qwen3.6-flash/flights4/Customer.java` | `Cannot find symbol: method setFlight`/`setPassengerName`/`getReservationId` (4 errors) - called setter/getter that do not exist in EMF model | LLM hallucination: invented non-existent property methods in Booking/Reservation |

## 3. Root Cause Analysis Section

> Failed test samples organized by testcase. If an ID has multiple failed test cases, split into separate testcase blocks with `Test Assertion`, `Test Case`, `Functional Code`, `Root Cause Analysis`, `Error Type`.

### OLRS001

- Sample: `TLBench/OLRS/deepseek-v4-flash/olrs2`
- Status: `Test Failure`
- Test Assertion: `AssertionError: Already completed order should not be handled again expected:<HOLD> but was:<LOAN>` (`CR3Test.testCase4_FailToUpdateAlreadyCompletedOrder`)
- Test Case: `CR3Test.testCase4_FailToUpdateAlreadyCompletedOrder`
- Functional Code: `Order.handleOrder()` method in `E-Workspace/OLRS/deepseek-v4-flash/olrs2/Order.java` (lines 340-360)
- Root Cause Analysis: `Order.handleOrder()` method lacks a check for completed order status. When the order status is already `COMPLETED`, calling `handleOrder()` executes `setStatus(COMPLETED)` and continues to change `HOLD` status book items to `LOAN`, while the test expects the order to remain `COMPLETED` and book items to remain `HOLD` (no processing).
- Error Type: `Missing precondition check`

#### OLRS002

- Sample: `TLBench/OLRS/deepseek-v4-flash/olrs3`
- Status: `Test Failure`
- Test Assertion: Same as OLRS001, `CR3Test.testCase4_FailToUpdateAlreadyCompletedOrder` fails for the same reason
- Functional Code: `Order.handleOrder()` method in `E-Workspace/OLRS/deepseek-v4-flash/olrs3/Order.java`
- Error Type: `Missing precondition check`

#### OLRS003

- Sample: `TLBench/OLRS/deepseek-v4-flash/olrs4`
- Status: `Test Failure`
- Test Assertion: Same as OLRS001
- Test Case: `CR3Test.testCase4_FailToUpdateAlreadyCompletedOrder`
- Functional Code: `Order.handleOrder()` method in `E-Workspace/OLRS/deepseek-v4-flash/olrs4/Order.java`
- Root Cause Analysis: Same as OLRS001
- Error Type: `Missing precondition check`

#### OLRS004

- Sample: `TLBench/OLRS/deepseek-v4-flash/olrs5`
- Status: `Test Failure`
- Test Assertion: Same as OLRS001
- Test Case: `CR3Test.testCase4_FailToUpdateAlreadyCompletedOrder`
- Functional Code: `Order.handleOrder()` method in `E-Workspace/OLRS/deepseek-v4-flash/olrs5/Order.java`
- Root Cause Analysis: Same as OLRS001
- Error Type: `Missing precondition check`

#### OLRS005

- Sample: `TLBench/OLRS/gpt-5.4-mini/olrs1`
- Status: `Test Failure`
- Test Assertion: Same as OLRS001
- Test Case: `CR3Test.testCase4_FailToUpdateAlreadyCompletedOrder`
- Functional Code: `Order.handleOrder()` method in `E-Workspace/OLRS/gpt-5.4-mini/olrs1/Order.java`
- Root Cause Analysis: Same as OLRS001
- Error Type: `Missing precondition check`

#### OLRS006

- Sample: `TLBench/OLRS/gpt-5.4-mini/olrs2`
- Status: `Test Failure`
- Test Assertion: Same as OLRS001
- Test Case: `CR3Test.testCase4_FailToUpdateAlreadyCompletedOrder`
- Functional Code: `Order.handleOrder()` method in `E-Workspace/OLRS/gpt-5.4-mini/olrs2/Order.java`
- Root Cause Analysis: Same as OLRS001
- Error Type: `Missing precondition check`

#### OLRS007 ~ OLRS011

OLRS007 ~ OLRS011 are all identical to OLRS001.

### OLRS012 ~ OLRS019, OLRS021 ~ OLRS023

OLRS012 ~ OLRS019, OLRS021 ~ OLRS023 are all identical to OLRS001 (`CR3Test.testCase4_FailToUpdateAlreadyCompletedOrder`).

### OLRS020

- Sample: `TLBench/OLRS/qwen3.6-flash/olrs2`
- Status: `Test Failure`

#### testcase 1

- Test Assertion: Same as OLRS001, `CR3Test.testCase4_FailToUpdateAlreadyCompletedOrder` fails
- Test Case: `CR3Test.testCase4_FailToUpdateAlreadyCompletedOrder`
- Functional Code: `Order.handleOrder()` method in `E-Workspace/OLRS/qwen3.6-flash/olrs2/Order.java`
- Root Cause Analysis: Same as OLRS001, `Order.handleOrder()` lacks precondition check for `COMPLETED` status.
- Error Type: `Missing precondition check`

#### testcase 2

- Test Assertion: `AssertionError: Disc audio items should not count as downloads expected:<0> but was:<1>`
- Test Case: `CR4Test.testCase2_IgnoreDiscAudioItems`
- Functional Code: `E-Workspace/OLRS/qwen3.6-flash/olrs2/Order.java`
- Root Cause Analysis: The functional code incorrectly counts DiscAudio type digital items when counting downloads, while the test expects DiscAudio items should not be counted. Further analysis of the download counting logic in the `Order` class is needed. Also, *requirement decomposition* did not explicitly mention this; however, the LLM states in its doc "assume input is already a DiscAudio type digital item," believing its logic to be self-consistent.
- Error Type: `Business logic error` (DiscAudio download count error)

### OPMS001
- Test Scenario: Create department D002 and add 2 community projects to this department:
Project 1 "Food Drive", assign FundingGroupType.PRIVATE
Project 2 "Health Awareness Campaign", assign FundingGroupType.MIXED
Call department method department.getFundingGroupTypeCommunityProjects() to get the list of funding group types for all community projects in this department.
Test expectations:
List size is 2 (line 60 assertEquals(2, result.size()))
First element is PRIVATE (line 61 result.get(0))
Second element is MIXED (line 62 result.get(1))

- Sample: `TLBench/OPMS/gemini-3.1-flash-lite/project5/Department.java`
- Status: `Test Failure`
- Test Assertion: `AssertionError: expected:<PRIVATE> but was:<MIXED>` (`CR4Test.testRetrieveFundingGroupTypeForMultipleCommunityProjects`)
- Test Case: `CR4Test.testRetrieveFundingGroupTypeForMultipleCommunityProjects`
- Functional Code: `E-Workspace/OPMS/gemini-3.1-flash-lite/project5/`
- Root Cause Analysis: Test expects `PRIVATE` when the department has multiple different types of community projects, but the actual result is `MIXED`. Need to examine the logic of `Department.getFundingGroupTypeCommunityProjects()` method for determining funding group types.
Root cause: Used a HashSet (unordered collection) to collect types (correct approach should use EList with add to preserve order).
- Error Type: `Business logic error`

### OPMS002

- Sample: `TLBench/OPMS/minimax-m3/project4`
- Status: `Test Failure`
- Test Assertion: `NullPointerException: Cannot invoke "org.eclipse.emf.common.util.EList.iterator()" because "this.projects" is null` (`CR4Test.testNoCommunityProjectsInDepartment`, `Department.java:391`)
- Test Case: `CR4Test.testNoCommunityProjectsInDepartment`
- Functional Code: `E-Workspace/OPMS/minimax-m3/project4/Department.java`
- Root Cause Analysis: `Department.getFundingGroupTypeCommunityProjects()` calls `this.projects.iterator()` directly when `projects` is null, causing NPE. Functional code lacks null checks.
- Error Type: `Lack of EMF knowledge; unaware that EMF requires calling getter for lazy initialization; Null pointer exception`

### OPMS003 ~ OPMS005
- Requirement original document: Ensure that the company always has between 2 and 8 departments (inclusive).
**Allow adding a new department with unique name**, and allow removing an existing department by name (name is unique; in fact, the class diagram only has ID, but ID and name correspond. The DS implementation understands this, but qwen cannot.)
- OPMS003 ~ OPMS005 are all related to the failure of `CR5Test.testRemoveDepartmentByNameWhenAboveMinimum`:
- OPMS003 (qwen3.6-flash/project1): `expected:<[IT]> but was:<[Department removed successfully]>`
- OPMS004 (qwen3.6-flash/project2): `expected:<[IT]> but was:<[Department IT removed successfully.]>`
- OPMS005 (qwen3.6-flash/project3): `expected:<[IT]> but was:<[Department removed successfully]>`
- Root Cause: **The test contract for the return value is: return the name of the deleted department (string), but the code implementation returns: a success notification message for the end user (string).**
The function name `removeDepartmentByName()` also implies this requirement, but qwen consistently fails to follow it. However, models that perform well in our method can generally infer this through requirement decomposition, such as gemini-3.1-flash-lite/sample3.

### OPRS001 ~ OPRS003

OPRS001 ~ OPRS003 are all business logic errors in the Paper Review System (OPRS), involving CR4Test (acceptance rate calculation) and CR5Test (reject tendency/ratio calculation):
CR4Test — calculateAcceptanceRate (author acceptance rate)
The test creates an Author, submits several papers (each with decision set to accept or reject via submitPaper), then calls author.calculateAcceptanceRate().
CR5Test — calculateSubmittedReviewAverageScore (reviewer average score)

- **OPRS002** (qwen3.6-flash/conference1): 4 failures. CR4Test's `perfectAcceptanceRate`, `singlePaperAuthor`, `fiftyPercentAcceptanceRate`, `mixedDecisionsWithOneAcceptance` all return 0.0 (expected 1.0/0.5/0.33); CR5Test all pass.
- Root cause is that author.submitPaper has some authority override issues, because the requirement states: "A final decision is made by the co-chair for each paper." — the paper's decision is determined by the co-chair.
But qwen's submitPaper unconditionally sets to undecided; thus the sequence becomes: 1. Test constructs paper, co-chair calls makeFinalDecision to set ACCEPT
Then calls author.submitPaper(paper), and submitPaper forcibly overwrites the decision back to UNDECIDED; when calculating acceptanceRate afterward, all papers are UNDECIDED → acceptance rate is always 0.
- **OPRS003** (qwen3.6-flash/conference4): 4 failures, similar to OPRS001, directly using enum constants. CR5Test's `recentRejectTendency`, `balancedFiftyFiftyRatio`, `mixedSubmittedAndPendingReviews`, `singleReviewCase` return wrong values.
- **OPRS001** (deepseek-v4-flash/conference5): 4 failures. CR5Test's `recentRejectTendency`, `balancedFiftyFiftyRatio`, `mixedSubmittedAndPendingReviews`, `singleReviewCase` all return wrong values (e.g., 1.8 expected 0.2, 1.5 expected 0.5, 1.333 expected 0.67, 2.0 expected 0.0).
  - Inadequate requirement decomposition: The problem lies in (CR5) Reviewer.calculateSubmitted...the requirement decomposition is not detailed enough. The requirement clearly states "treating ACCEPT as 1 and REJECT as 0." But the function does not correctly map this, instead examining context and finding the enum definition: ACCEPT=1, REJECT=2.

### ORS001 ~ ORS005

Organized by ID/sample, no longer expanded by testcase. Common requirement baseline: Booking eligibility should compare `bookingDate` with `Trip.departureTime` (`yyyy-MM-dd HH:mm`), and must be strictly more than 2 hours before departure; successful booking should reduce `Trip.numberOfSeats`; time overlap should compare full datetime intervals; discount calculation should also directly parse `bookingTime` and `departureTime` strings.

- **ORS001** (deepseek-v4-flash/rideshare1): 2 failures, passed 33/35.
  - Error function: `Booking.isBookingEligible()`.
    - Root cause: Function uses `Calendar.getInstance()` to get system current time as booking time, and uses `SimpleDateFormat("HH:mm")` to parse the full `"2025-06-15 12:00"`, triggering parse exception and returning `false`; meanwhile, the success path only `return true`, without calling `updateTripSeats()`/`Trip.bookSeats()`, so even if eligibility passes, seats are not reduced.
    - model-doc status: **model-doc partially wrong + code error**. Step 4 of the doc writes current time instead of the `bookingDate` from the requirements; however, the doc's post-condition says "if returns true, seats have been reduced," but the code does not implement this state update.
  - Related function: `Booking.overlapsWith()`.
    - Root cause: model-doc says to compare trip's datetime interval, but code uses `"HH:mm"` to parse the full datetime string, returning `false` on exception, causing overlap detection to fail. Currently, the two failures in this sample are primarily blocked by `isBookingEligible()`'s time check; `overlapsWith()` is a potential error on the same business chain.
    - model-doc status: **doc mostly correct, code error**.

- **ORS002** (deepseek-v4-flash/rideshare2): 8 failures (5 failures + 3 errors), passed 27/35.
  - Error function: `Booking.isBookingEligible()`.
    - Root cause: The function calls `trip.bookSeats(numberOfSeats)` to deduct seats before completing the eligibility check, then uses the reduced `trip.numberOfSeats` to check `numberOfSeats > trip.numberOfSeats`, causing a successful 5-seat booking for 3 seats to first reduce 5->2 then determine 3>2 and return `false`; additionally, it calls `updateTripSeats()` later, creating a double-deduction risk. The time check also depends on `trip.getDepartureDate()`, but the test only sets the full string `departureTime`, leaving `departureDate` as null, causing NPE or returning false.
    - model-doc status: **model-doc wrong + code error**. The doc algorithm clearly writes `trip.bookSeats()` first, then checks, followed by `updateTripSeats()`, and writes current time/`departureDate`, inconsistent with requirements.
  - Error function: `Trip.calculateDiscountedPrice()`.
    - Root cause: The function splits departure into `departureDate + departureTime`, calling `dateFormat.format(getDepartureDate())`; but `departureDate` is not set, so after exception, it returns the original price, causing 25h and 24h boundary discounts to still return 100.0.
    - model-doc status: **model-doc partially wrong + code error**. The doc mentions the 24h discount logic correctly, but Related Features/implementation depends on `departureDate`, failing to adhere to the complete `departureTime` string in requirements.
  - Error function: `Driver.canPostTrip()` / `Trip.isTimeConflicting()`.
    - Root cause: `Driver.canPostTrip()` additionally requires `newtrip.getDepartureDate() != null`, but tests never set this field, causing no-conflict/back-to-back cases that should be allowed to be rejected; `Trip.isTimeConflicting()`'s doc/input uses `"HH:mm"` and depends on `departureDate`, not the full datetime string required by the requirements.
    - model-doc status: **model-doc wrong + code error**.

- **ORS003** (deepseek-v4-flash/rideshare3): 2 failures, passed 33/35.
  - Error function: `Booking.isBookingEligible()`.
    - Root cause: Function uses `new Date()` system current time to compare with `Trip.departureTime`, instead of using the current booking's `bookingDate`; the test's departure time is a fixed historical time `"2025-06-15 12:00"`, which relative to the system's current time no longer satisfies the 2-hour advance condition, so a booking that should succeed returns `false`. Additionally, the success path only `return true`, without calling `updateTripSeats()`, so seats are not reduced.
    - model-doc status: **model-doc partially wrong + code error**. The doc overview states time fields are `yyyy-MM-dd HH:mm`, but algorithm step 4 writes current system time; doc post-condition requires seat reduction, but code does not execute it.

- **ORS004** (deepseek-v4-flash/rideshare4): 2 failures, passed 33/35.
  - Error function: `Booking.isBookingEligible()`.
    - Root cause: Function first sets `bookingDate` into `Calendar`, then overwrites it with the current system's hour/minute/second, incorrectly constructing the "booking time" as "bookingDate date + current system time"; thus the fixed test's 09:00/10:00 boundary semantics are broken, `testCase1_SeatsAvailable` is rejected, while "exactly 2-hour cutoff" may be incorrectly accepted and deducted.
    - model-doc status: **model-doc wrong + code error**. The doc explicitly writes "bookingDate is EDate, assume booking time is current system time," which is exactly the source of the violation; the code implements the wrong doc.

- **ORS005** (deepseek-v4-flash/rideshare5): 6 failures (4 failures + 2 errors), passed 29/35.
  - Error function: `Booking.isBookingEligible()` / `Trip.bookSeats()` / `Trip.getBookedSeats()`.
    - Root cause: `isBookingEligible()` calls `trip.bookSeats()` to deduct seats before overlap and time checks, then calls `updateTripSeats()` for a second deduction; more critically, `Trip.bookSeats()` calls `getBookedSeats()`, which directly iterates the field `this.bookings` without using `getBookings()` to trigger EMF lazy initialization, so in empty list scenarios `this.bookings == null`, causing NPE or incorrect state. Some failures manifest as bookings that should be rejected have already had 3 seats deducted prematurely.
    - model-doc status: **model-doc wrong + code error**. The doc algorithm writes `bookSeats()` first, then checks, finally `updateTripSeats()`, creating double deduction; `getBookedSeats()` doc writes iterating `this.bookings` without specifying that EMF getter should be used for empty list handling.
  - Error function: `Trip.getStopStations()` / `Driver.checkStopOverlap()`.
    - Root cause: `getStopStations()` directly iterates `this.stops` field, causing NPE when the empty stop list hasn't been initialized; `checkStopOverlap()` itself only calls `trip.getStopStations()`, the real error is in `Trip.getStopStations()`'s EMF lazy loading usage.
    - model-doc status: **model-doc partially wrong + code error**. The doc writes "stops list is initialized" as a precondition, but requirements require an empty stop collection to return false, not requiring the caller to pre-initialize.
  - Error function: `Trip.calculateDiscountedPrice()`.
    - Root cause: Missing `bookingTime == null` defensive check, `SimpleDateFormat.parse(null)` throws NPE in `CR2Test.testCase7_MissingBookingTime`; requirements require invalid inputs to return the original price.
    - model-doc status: **doc correct, code error**. The doc writes invalid inputs return original price, but the implementation does not cover null bookingTime.

### ORS006 ~ ORS010

Organized by ID/sample, no longer expanded by testcase. Common background: ORS requirements and tests use `Trip.departureTime`/`arrivalTime` as complete `yyyy-MM-dd HH:mm` datetime strings; this group of gpt-5.4-mini samples generally splits time into `departureDate(Date) + departureTime(HH:mm)`, causing booking, discount, and driver departure conflict checks to deviate simultaneously.

- **ORS006** (gpt-5.4-mini/rideshare1): 8 failures, passed 27/35.
  - Error function: `Booking.isBookingEligible()`. Code requires `trip.getDepartureDate()` to be non-null, and parses the full `departureTime` using `split(":")` as hour/minute; tests only set `departureTime="2025-06-15 12:00"`, with `departureDate` as null, or parse `"2025-06-15 12"` triggering `NumberFormatException`, causing eligible bookings to return false, without seat deduction.
  - Error function: `Trip.calculateDiscountedPrice()`. Code similarly depends on `departureDate`, directly returning original price when `departureDate == null`, so DISCOUNTS members with 25h and exactly 24h advance don't get a discount.
  - Error function: `Trip.isTimeConflicting()`/`Driver.canPostTrip()`. `isTimeConflicting()` uses `SimpleDateFormat("HH:mm")` to parse full datetime strings, returning false on exception; `canPostTrip()` therefore cannot detect overlaps, allowing overlapping trips that should be rejected.
  - model-doc status: **doc also wrong/incomplete, code error**. The doc for `isBookingEligible()` and `calculateDiscountedPrice()` explicitly describes time as `departureDate + departureTime`; `isTimeConflicting()` doc does not clearly require full datetime interval comparison, leaving room for the code to misuse `HH:mm`.

- **ORS007** (gpt-5.4-mini/rideshare2): 10 failures, passed 25/35.
  - Error function: `Booking.isBookingEligible()`. Code requires `trip.getDepartureDate() != null`, but tests only set the full `departureTime` string, so legitimate bookings directly return false without seat deduction.
  - Error function: `Trip.calculateDiscountedPrice()`. Implementation requires `departureDate` to be non-null, and further splits `departureTime` into `HH:mm`; since `departureDate` is null, it returns original price even when discount conditions are met.
  - Error function: `Trip.isTimeConflicting()`/`Driver.canPostTrip()`. `isTimeConflicting()` uses `LocalTime.parse()` to parse `"yyyy-MM-dd HH:mm"`, throwing exception and returning false, causing overlapping trips to go undetected.
  - Error function: `Driver.checkStopOverlap()`. Code additionally requires `trip1.getDriver() == trip2.getDriver() == this`, but the common stop requirement only requires comparing stop stations; the test's trips do not have a driver set, so shared stops also return false.
  - model-doc status: **Time-related doc wrong/incomplete, code error; stop overlap doc mostly correct, code error**. Time docs still lead the model toward `departureDate + departureTime`; `checkStopOverlap()`'s driver consistency constraint is not part of the requirements.

- **ORS008** (gpt-5.4-mini/rideshare3): 8 failures, passed 27/35.
  - Error function: `Booking.isBookingEligible()`. Implementation requires `departureDate`, `departureTime`, `arrivalTime` all non-null, and parses `departureTime.split(":")` as `HH:mm`; full datetime strings fail at the hour parse step, causing legitimate bookings to return false.
  - Error function: `Trip.calculateDiscountedPrice()`. Implementation requires `departureDate` non-null, and parses `bookingTime` in ISO format `yyyy-MM-dd'T'HH:mm:ss.SSSX`; tests use `yyyy-MM-dd HH:mm`. Actually, due to `departureDate == null`, it first returns original price, so discounts don't take effect.
  - Error function: `Trip.isTimeConflicting()`/`Driver.canPostTrip()`. Implementation splits full datetime into `HH:mm` numeric minutes, failing to parse `"2025-06-15 09"` and returning false, causing overlapping trips to be misjudged as non-conflicting.
  - model-doc status: **doc also wrong/incomplete, code error**. `isBookingEligible()`/discount docs still depend on `departureDate`; conflict detection doc only says "same time format" without fixing the requirement's full datetime interval rule.

- **ORS009** (gpt-5.4-mini/rideshare4): 6 failures, passed 29/35.
  - Error function: `Booking.isBookingEligible()`. Code directly requires `trip.getDepartureDate() != null`, and uses `departureDate.getTime()` to compare with `bookingDate`, completely ignoring the complete datetime already present in `departureTime`; tests do not set `departureDate`, so legitimate bookings return false without seat deduction.
  - Error function: `Trip.calculateDiscountedPrice()`. Code returns original price after checking `departureDate == null`; even if `departureDate` is non-null, it concatenates `departureDate.toString() + " " + departureTime` and parses as `yyyy-MM-dd HH:mm`, where format mismatch causes discounts to easily fail.
  - Error function: `Driver.canPostTrip()`. Implementation additionally requires `newtrip.getDepartureDate() != null` and `newtrip.getNumberOfSeats() > 0`, but the driver departure conflict test only needs to compare the full datetime interval of `departureTime`/`arrivalTime`; thus non-conflicting/back-to-back legitimate new trips are also rejected prematurely.
  - Related risk function: `Trip.isTimeConflicting()` still uses `SimpleDateFormat("HH:mm")` to parse full datetime; if conflict comparison is executed, it will also misjudge conflicts.
  - model-doc status: **doc also wrong, code error**. `canPostTrip()` doc explicitly includes `departureDate`, `numberOfSeats` preconditions unrelated to conflict detection; booking and discount docs also follow the incorrect `departureDate + departureTime` modeling.

- **ORS010** (gpt-5.4-mini/rideshare5): 10 failures, passed 25/35.
  - Error function: `Booking.isBookingEligible()`. Code does not check if `trip.getDepartureDate()` is null, directly calling `departureCal.setTime(trip.getDepartureDate())`; in tests, `departureDate` is null, so multiple CR1 test cases throw `NullPointerException: date must not be null`, not normal true/false returns.
  - Error function: `Trip.calculateDiscountedPrice()`. Implementation requires `getDepartureDate() != null`, and concatenates `formatter.format(getDepartureDate()) + " " + getDepartureTime()` for date-time; tests only set the full `departureTime`, so discount conditions met still return original price.
  - Error function: `Trip.isTimeConflicting()`/`Driver.canPostTrip()`. `isTimeConflicting()` uses `SimpleDateFormat("HH:mm")` to parse full datetime, returning false on parse failure; `canPostTrip()` therefore allows overlapping/included/identical time windows that should be rejected.
  - model-doc status: **doc incomplete/partially wrong, code error**. `Booking.isBookingEligible()` doc writes seat deduction after acceptance, but does not correctly specify that `departureTime` is already a full datetime; discount and conflict-related docs still do not fix the requirement's full datetime parsing rule.

### ORS011 ~ ORS013

Organized by ID/sample, no longer expanded by testcase. Common background: Requirements require all time comparisons to use the full `yyyy-MM-dd HH:mm` datetime strings from `Trip.departureTime`/`arrivalTime`; this group of three gemini-3.1-flash-lite samples still splits time into `departureDate(Date) + departureTime`, and exhibits placeholder-style implementations in discount logic.

- **ORS011** (gemini-3.1-flash-lite/rideshare1): 5 failures, passed 30/35.
  - Error function: `Booking.isBookingEligible()`. After passing seat check, code first calls `overlapsWith(getTrip())`, then directly executes `getTrip().getDepartureDate().getTime()`; tests only set the full `departureTime`, with `departureDate` as null, so `CR1Test.testCase1_SeatsAvailable` and two-hour boundary cases throw NPE, legitimate bookings cannot complete, and seats are not deducted.
  - Error function: `Booking.overlapsWith()`. Code uses `b.getTrip().getDepartureDate().equals(trip.getDepartureDate())` to check if same day; when an existing booking's trip also has no `departureDate`, it directly NPEs, causing non-overlapping/overlapping booking tests to fail via exception rather than normal true/false returns.
  - Error function: `Trip.calculateDiscountedPrice()`. Code hardcodes `isEarlyEnough` to `true`, applying 20% discount as long as the member has `Award.DISCOUNTS`; therefore, late bookings 1.5h before departure incorrectly return 160.0 instead of 200.0.
  - model-doc status: **Booking/overlap doc wrong, discount doc mostly correct but code error**. Booking and overlap docs continue to depend on `departureDate`, not meeting the full datetime string requirement; discount doc writes the 24h rule, but the implementation bypasses time checks with a placeholder variable.

- **ORS012** (gemini-3.1-flash-lite/rideshare2): 4 failures, passed 31/35.
  - Error function: `Booking.isBookingEligible()`. Implementation concatenates `trip.getDepartureDate().toString() + " " + trip.getDepartureTime()` for departure time; tests have `departureDate` as null, NPE is caught by `catch` and returns false, so legitimate bookings with sufficient seats and no overlap are rejected without seat deduction.
  - Error function: `Trip.calculateDiscountedPrice()`. Function precondition includes `getDepartureDate() == null` directly returning original price; therefore DISCOUNTS members with 25h and exactly 24h advance both return 100.0 instead of 80.0.
  - Related risk function: `Booking.overlapsWith()` also uses `this.trip.getDepartureDate().equals(otherTrip.getDepartureDate())` with `HH:mm` assumptions for overlap detection; however, in failed cases, the primary issue is `isBookingEligible()` returning false at the time parsing step.
  - model-doc status: **doc also wrong/incomplete, code error**. The docs for `isBookingEligible()`, `overlapsWith()`, and `calculateDiscountedPrice()` all treat `departureDate + departureTime` as the time source, failing to adhere to the requirement that `departureTime` already contains the complete datetime.

- **ORS013** (gemini-3.1-flash-lite/rideshare3): 4 failures, passed 31/35.
  - Error function: `Booking.isBookingEligible()`. Code first checks if existing bookings overlap, then parses departure time using `getTrip().getDepartureDate().toString() + " " + getTrip().getDepartureTime()`; when `departureDate` is null, the exception is caught and returns false, so legitimate bookings with sufficient seats are rejected.
  - Error function: `Booking.overlapsWith()`. Code uses `thisTrip.getDepartureDate().equals(otherTrip.getDepartureDate())` to check if same day; when existing booking's `departureDate` is null, NPE causes non-overlapping/overlapping booking tests to fail via exception.
  - Error function: `Trip.calculateDiscountedPrice()`. Code checks membership and `bookingTime` non-null, then directly calculates `price * 0.8` without parsing the difference between `bookingTime` and departure time; therefore, late bookings are also discounted, returning 160.0 instead of 200.0.
  - model-doc status: **Booking/overlap doc wrong, discount doc mostly correct but code error**. Booking and overlap docs incorrectly depend on `departureDate`; discount doc writes "difference >= 24 hours," but the implementation is a placeholder unconditional discount.

### ORS016 ~ ORS019

Organized by ID/sample, no longer expanded by testcase. Common background: The minimax-m3 group is closer to the requirements than the previous groups; many time comparisons already directly parse full `yyyy-MM-dd HH:mm` strings; remaining issues mainly focus on "state update after successful booking," null input defense, and a few samples continuing to misuse `departureDate`.

- **ORS016** (minimax-m3/rideshare2): 2 failures, passed 33/35.
  - Error function: `Booking.isBookingEligible()`. The function fully checks customer/trip/bookingDate, seat availability, 2-hour advance, and existing booking overlap; but after all checks pass, it only `return true`, without calling `updateTripSeats()` or `trip.bookSeats(numberOfSeats)`. Therefore, the test accepts the booking, but `Trip.numberOfSeats` remains 5/40, and the expected 2/36 does not appear.
  - Related function: `Booking.updateTripSeats()` itself would call `trip.bookSeats(seats)`, but `isBookingEligible()` does not trigger it, breaking the state update path.
  - model-doc status: **doc incomplete/deviates from requirements, code error**. The doc for `isBookingEligible()` describes seat deduction as being done by `updateTripSeats` or subsequent calls, while tests/requirements require that accepted bookings directly reflect in trip seat status.

- **ORS017** (minimax-m3/rideshare3): 4 failures, passed 31/35.
  - Error function: `Booking.isBookingEligible()`. Similar to ORS016, legitimate bookings pass checks and directly `return true` without deducting seats; therefore, the non-overlapping booking case returns success but `Trip.numberOfSeats` remains 40.
  - Error function: `Trip.calculateDiscountedPrice()`. Code directly calls `sdf.parse(bookingTime)` after confirming customer has DISCOUNTS, without handling `bookingTime == null`; `CR2Test.testCase7_MissingBookingTime` expects original price but actually throws `NullPointerException: String.length()`.
  - Error function: `Booking.isInMonth()`. Code only checks `bookingDate == null`, then directly calls `month.split("-")`; when `month == null`, it throws NPE, causing the loyalty points case for missing target month that should return 0 to fail.
  - model-doc status: **Seat update doc/code inconsistent and code error; null defense is doc incomplete/code error**. `updateTripSeats()` doc writes deduction, but `isBookingEligible()` does not call it; `calculateDiscountedPrice()` and `isInMonth()` do not implement the requirements for invalid input returning original value/false as preconditions.

- **ORS018** (minimax-m3/rideshare4): 2 failures, passed 33/35.
  - Error function: `Booking.isBookingEligible()`. Code can parse `departureTime`/`arrivalTime` in `yyyy-MM-dd HH:mm` format and check 2-hour advance; but before checking existing bookings, it requires `thisTrip.getDepartureDate() != null`, otherwise directly returns false. Tests only set the full `departureTime`, without setting `departureDate`, so legitimate bookings with sufficient seats and no overlap are all rejected.
  - Related function: `Booking.updateTripSeats()` also calls `isBookingEligible()` again; if called externally, it will be blocked by the same `departureDate` precondition.
  - model-doc status: **doc also wrong, code error**. The docs for `isBookingEligible()`/`overlapsWith()` bind the same-day check to `departureDate`, failing to adhere to the requirement that `departureTime` already includes the complete date and time.

- **ORS019** (minimax-m3/rideshare5): 6 failures, passed 29/35.
  - Error function: `Booking.isBookingEligible()`. Code uses `dateFormat.format(getTrip().getDepartureDate())` to get the date, then concatenates `getTrip().getDepartureTime()`; in tests, `departureDate` is null, directly throwing `NullPointerException: date must not be null`, affecting seat availability, non-overlap, overlap, and two-hour boundary CR1 cases.
  - Error function: `Trip.calculateDiscountedPrice()`. Code requires `this.departureDate != null`, then concatenates `departureDate` with `departureTime` for full departure time; tests only provide full datetime in `departureTime`, so DISCOUNTS members with 25h and exactly 24h advance both return original price 100.0.
  - Related function: `Booking.overlapsWith()` also depends on `getTrip().getDepartureDate().equals(trip.getDepartureDate())` and parses full datetime as `HH:mm`; current failures are primarily exposed by `isBookingEligible()`'s `departureDate` NPE, but the overlap judgment itself also deviates from requirements.
  - model-doc status: **doc also wrong, code error**. Booking, overlap, and discount docs all model time as `departureDate + departureTime`, while requirements/tests actually use `departureTime`/`arrivalTime` as complete datetime strings.

### ORS020 ~ ORS024

Failure patterns for qwen3.6-flash model's ORS system fall into two categories:

**rideshare2/rideshare4**: `NumberFormatException` date parsing errors
- `CR1Test.testCase1/3/4/5_SeatsAvailable`: All fail due to `Integer.parseInt("2025-06-15 12")` — `Booking.isBookingEligible()` line 376 uses `parseInt()` on hyphenated date strings instead of correctly parsing dates.
- `CR2Test.testCase1/3_EarlyBooking`: Same `NumberFormatException` in `isBookingEligible()`.
- `CR5Test.testCase2/4/5/8_TimeOverlap`: Time window conflict detection logic error — should reject overlap but allows it.
- `CR4Test.testCase7_MissingTargetMonth`: NPE — `Booking.isInMonth()` line 516 calls `split()` when `month` parameter is null.

**rideshare5**: Date-time format parsing errors
- `CR1Test`: Seat availability calculation error (`Remaining seats should be 2 expected:<2> but was:<5>`) and `isBookingEligible()` boundary judgment errors.
- `CR2Test.testCase1/2/3/7`: Multiple `DateTimeParseException: Text '2025-06-14 12:00' could not be parsed at index 10` — `Trip.calculateDiscountedPrice()` line 609 uses `LocalDateTime.parse()` but the input string format is `yyyy-MM-dd HH:mm` instead of standard ISO format, missing the T separator.
- `CR5Test`: No failures (all passed).

### R123001 ~ R123003

R123_School system (training school course/coach/participant management system) failures are all single test failures:

CR3Test's `testCase2_AddDuplicateSessionDate` (verify duplicate dates are not added: first add session 2025-02-06 to course, then call `course.addSession(2025-02-06)`, expected to return list size=1, duplicate date ignored)
_Requirement (CR_detail #3): "The operation returns the course's up-to-date list of sessions."
1. addSession ensures each course has only one session per day
2. The operation returns the course's latest session list
3. Duplicate dates should not add a new session, but should properly return the list_
- Example of not meeting requirement 3 (throwing exception instead of silent return):
    - **R123002**(qwen3.6-flash/trainingSchool2) — `Course.addSession()` detects duplicate date and throws `IllegalArgumentException("A session already exists for the given date.")` instead of returning the existing list
- Example of not meeting requirement 2 (returning null causing NPE):
    - **R123003**(qwen3.6-flash/trainingSchool4) — `Course.addSession()` detects duplicate date and `return null`, test calls `sessions.size()` → NPE: Cannot invoke "EList.size()" because "<local4>" is null

CR5Test's `testCase2_DuplicateEnrolmentBlocked` (verify duplicate enrollment is rejected: first register PA501 to session, then call `session.registerParticipant(PA501, today)`, expected to return false, session still has 1 person)
_Requirement (CR_detail #5):
1. Register participant to specified session
2. Ensure the same participant is not double-registered to the same session (no duplicate enrollments)
3. Return true for success, false for failure_
- Example of not meeting requirement 2 (no duplicate check):
    - **R123001**(minimax-m3/trainingSchool3) — `Session.registerParticipant()` only performs null check and date check, does not check if participant is already in registeredParticipants → directly `getRegisteredParticipants().add(p)` → returns true (expected false), and session has 2 people (expected 1)

### R132001 ~ R132003

R132_MunicipalLibrary system (municipal library borrowing/returning/renewal management):

- **R132001** (minimax-m3/municipalLibrary4): 6 failures, passed 19/25
- **R132002** (qwen3.6-flash/municipalLibrary1): 3 failures, passed 22/25
- **R132003** (qwen3.6-flash/municipalLibrary4): 12 failures, passed 13/25

#### Common Failure Root Causes

**Root Cause A: Direct field access to borrowRecords → EMF lazy loading NPE (R132001 only)**
minimax-m3/municipalLibrary4's `Member.borrowBook()`, `returnBook()`, `listBorrowedBookTitles()` all directly use the `borrowRecords` field instead of the `getBorrowRecords()` getter. EMF's borrowRecords is lazily initialized, only created through the getter. When a Member is newly created and the getter has never been called, `borrowRecords == null` → NPE.

**Root Cause B: extendReturnDueDate adds +7 days to today instead of returnDue (R132002 only)**
In qwen3.6-flash/municipalLibrary1's `Member.extendReturnDueDate()`: `cal.setTime(today); cal.add(Calendar.DATE, 7)` → new due = today + 7. Requirements specify adding 7 days to the **current due date**. Example: TC1: today=2025-01-05, due=2025-01-08 → code returns 01-12 (today+7), expected 01-15 (due+7).

**Root Cause C: Matching book titles with bookId → complete collapse (R132003 only)**
qwen3.6-flash/municipalLibrary4's `borrowBook()`, `returnBook()`, `extendReturnDueDate()`, `listBorrowedBookTitles()` all use `book.getBookId().equals(bookTitle)` for matching, but the test's bookTitle is a book name (e.g., "Time Travel"), while bookId is a code (e.g., "B-TT"). Resulting in:
- borrowBook can't find the book → returns false
- returnBook can't find the record → returns false
- extendReturnDueDate can't find the record → returns null
- listBorrowedBookTitles returns bookId (e.g., "B-AZ") instead of title (e.g., "Alpha Zone")
- qwen cannot properly understand the semantic meaning of attributes.

#### Organized by testCase

CR2Test's `testCase1_BorrowFirstBook` (verify first time borrowing: library has "Time Travel"(bookId="B-TT"), member calls `borrowBook("Time Travel", 2025-06-01)`, expected to return true, borrowRecords size=1, returnDue=2025-06-08)
_Requirement (CR_detail #2):
1. Member borrows a book by title
2. The book must be of Book type, not currently borrowed out, member must have fewer than 3 current loans
3. On success, create a borrowRecord, borrowingDate=today, returnDue=today+7_
- Example not meeting requirement 1 (Root Cause C — bookId matching title fails):
    - **R132003**(qwen3.6-flash/municipalLibrary4) — `b.getBookId().equals("Time Travel")` but bookId="B-TT" → not found → return false
- Example not meeting requirement 3 (Root Cause A — borrowRecords NPE):
    - **R132001**(minimax-m3/municipalLibrary4) — `borrowRecords.size()` → NPE: borrowRecords is null

CR2Test's `testCase5_SecondBookStillAllowed` (verify borrowing allowed after old record removed: member first has "Deep Sea" record then removed, then borrows "Open Ocean", expected success)
_Requirement: Same as TC1_ (complex algorithm incorrect)
- Examples not meeting requirement 1/3:
    - **R132003** — bookId matching fails → return false
    - **R132001** — borrowRecords NPE

CR3Test's `testCase1_EligibleForExtension` (verify renewal: borrowDate=2025-01-01, due=2025-01-08, today=2025-01-05, expected due becomes 2025-01-15)
_Requirement (CR_detail #3):
1. Member renews their book by title
2. When today >= borrowingDate and today < dueDate, extend due by 7 days
3. Otherwise, due remains unchanged, return original due_
- Example not meeting requirement 2 (Root Cause B — +7 added to today):
    - **R132002**(qwen3.6-flash/municipalLibrary1) — `cal.setTime(today); cal.add(DATE, 7)` → 2025-01-05+7=2025-01-12 (expected 2025-01-08+7=2025-01-15)
- Example not meeting requirement 1 (Root Cause C — bookId matching fails):
    - **R132003** — `r.getBook().getBookId().equals("Solar Physics")` → not found → return null

CR3Test's `testCase4_MultipleBorrowingByMember` (verify only the specified book is renewed: has "Modern Art"(due=04-08) and "Color Theory"(due=04-09), renew "Modern Art", today=2025-04-05, expected Modern Art due→04-15, Color Theory unchanged)
_Requirement: Same as TC1_
- Example not meeting requirement 2 (Root Cause B):
    - **R132002** — today+7 = 2025-04-12 (expected 04-08+7=04-15)

CR3Test's `testCase5_SecondExtensionAttempt` (verify renewal: due=2025-05-22, today=2025-05-18, expected due→2025-05-29)
_Requirement: Same as TC1_
- Example not meeting requirement 2 (Root Cause B):
    - **R132002** — today+7 = 2025-05-25 (expected 05-22+7=05-29)

CR4Test's `testCase1_ReturnOwnedBook` (verify normal return: member has "Ocean Currents"(due=2025-07-09), today=2025-07-08 not overdue, expected returnBook returns true, borrowRecords cleared)
_Requirement (CR_detail #4):
1. Member returns a book by title
2. If overdue, reject the return (return false, keep record)
3. If not overdue, remove borrowRecord, return true_
- Example not meeting requirement 1 (Root Cause C — bookId matching fails):
    - **R132003** — `rec.getBook().getBookId().equals("Ocean Currents")` → not found → return false
- Root Cause A — borrowRecords NPE:
    - **R132001** — `for (BorrowRecord r : borrowRecords)` → NPE

CR4Test's `testCase5_ReturnLastRemainingBook` (verify returning last book: member has "Polar Night"(due=2025-08-17), today=2025-08-11, expected returnBook returns true, borrowRecords cleared)
_Requirement: Same as TC1_
- Examples not meeting requirement 1/3:
    - **R132003** — bookId matching fails → return false
    - **R132001** — borrowRecords NPE

CR5Test's `testCase1_MemberWithThreeLoans` (verify listing 3 book titles: member has "Mars Diary"/"Venus Roses"/"Earth Chronicle", expected listBorrowedBookTitles returns ["Mars Diary", "Venus Roses", "Earth Chronicle"])
_Requirement (CR_detail #5):
1. List the titles of all books currently borrowed by the member
2. Return empty list when no books are borrowed_
- Example not meeting requirement 1 (Root Cause C — returning bookId instead of title):
    - **R132003** — `titles.add(rec.getBook().getBookId())` → returns ["B-MD", "B-VR", "B-EC"] (expected ["Mars Diary", "Venus Roses", "Earth Chronicle"])
- Root Cause A — borrowRecords NPE:
    - **R132001** — `for (BorrowRecord r : borrowRecords)` → NPE

CR5Test's `testCase3_OneReturnedOneActive` (verify only active loans are listed: member first has "City Lights" then removed, then has "Moon Orbit", expected returns ["Moon Orbit"])
_Requirement: Same as TC1_
- Examples not meeting requirement 1:
    - **R132003** — returns bookId ["B-MO"] (expected ["Moon Orbit"])
    - **R132001** — borrowRecords NPE

CR5Test's `testCase4_MemberAtLimit` (verify list of 3 book titles: expected ["Alpha Zone", "Beta Drift", "Gamma Ray"])
_Requirement: Same as TC1_
- Example not meeting requirement 1 (Root Cause C):
    - **R132003** — returns ["B-AZ", "B-BD", "B-GR"] (expected ["Alpha Zone", "Beta Drift", "Gamma Ray"])

### R144001 ~ R144023

R144_AirlineFlights system (airline flight booking management): 23 samples total, failure patterns highly consistent across multiple models.

#### Common Failure Root Causes

**Root Cause A: addBooking does not detect duplicate passenger names within the same request (most deepseek/gemini/minimax samples)**
`Customer.addBooking()` only iterates `flight.getReservations()` to check for duplicates in existing bookings, but does not check if `listOfPassengerNames` itself has duplicates. When names("Alice", "Alice"), neither "Alice" is in flight.reservations, so duplicates go undetected.

**Root Cause B: addBooking does not check the same passenger across different bookings (almost all samples)**
In test testCase3, "Jucy" already exists in a customer's old booking, but the corresponding reservation is not added to `flight.getReservations()` (test code line 68 is commented). Code only checks `flight.getReservations()` → cannot find "Jucy" → allows duplicate booking.

**Root Cause C: closeFlight does not properly cancel confirmed reservations / does not reject already closed flights (some gpt-5.4-mini samples)**
- Does not change CONFIRMED reservation status to CANCELED when closing flight
- Does not return false when closing an already closed flight
- Missing check for not allowing closing on departure day

#### Organized by testCase

CR2Test's `testCase2_DuplicatePassengerInSameRequest` (verify duplicate passengers in same request rejected: create open flight, call `customer.addBooking(flight, now, names("Alice", "Alice"))`, expected to return false, customer.bookings empty, flight.reservations empty)
_Requirement (CR_detail #2):
1. Check for no duplicate passengers on the flight
2. Current time must be before departure time
3. On success, create PENDING status reservation for each passenger_
- Example not meeting requirement 1 (Root Cause A — no check within request):
    - **R144001~R144005**(deepseek-v4-flash all) — Code only checks `flight.getReservations()` for existing booking duplicates, does not check `listOfPassengerNames` for internal duplicates → "Alice","Alice" pass check → returns true (expected false) -- but gpt-flight1 successfully checks this.
    - **R144010~R144014**(gemini-3.1-flash-lite partial), **R144015~R144018**(minimax-m3 partial) — same as above

CR2Test's `testCase3_PassengerAlreadyBookedEarlier` (verify already booked passenger cannot rebook: customer already has booking containing "Jucy", call `customer.addBooking(flight, now, names("Jucy"))`, expected to return false)
_Requirement: Same as TC2 (Requirement 1)_
- Example not meeting requirement 1 (Root Cause B — no cross-booking check):
    - **R144001~R144005**(deepseek-v4-flash all) — Code only checks `flight.getReservations()`, but test "Jucy"'s reservation is not added to flight.reservations (in customer's old booking) → not found → returns true (expected false)
    - **R144006~R144009**(gpt-5.4-mini partial), **R144010~R144014**(gemini partial) — same as above

CR4Test's `testCase2_ThreeConfirmedReservationsCanceled` (verify closing flight cancels all confirmed reservations: flight has 3 CONFIRMED reservations, call `airline.closeFlight("F201", now)`, expected all changed to CANCELED)
_Requirement (CR_detail #4):
1. Set openForBooking to false when closing flight
2. Cancel all confirmed reservations (change status to CANCELED)_
- Example not meeting requirement 2 (Root Cause C — did not cancel confirmed bookings):
    - **R144009**(gpt-5.4-mini/flights5) — `closeFlight()` did not change CONFIRMED to CANCELED → `expected:<CANCELED> but was:<CONFIRMED>`
    - This case had correct requirement decomposition but completely failed to execute it.

CR4Test's `testCase3_FlightAlreadyClosed` (verify already closed flight cannot be closed again: flight.openForBooking=false, call closeFlight, expected to return false)
_Requirement: Same as TC2 (Requirement 1)_
- Example not meeting requirement 1 (Root Cause C — did not check closed status):
    - **R144009**(gpt-5.4-mini/flights5) — closeFlight did not check if openForBooking is already false → returns true (expected false)

CR4Test's `testCase4_CloseOnDepartureDayAfterDepartureTime` (verify closing not allowed on departure day: departure time 09:00, current time 09:10 same day, expected closeFlight returns false)
_Requirement: Same as TC2 (Requirement 1 — closing not allowed on same calendar day as departure)_
- Example not meeting requirement 1 (Root Cause C — no same-day check):
    - **R144009**(gpt-5.4-mini/flights5) — closeFlight did not check if current date is same as departure date → returns true (expected false)

CR4Test's `testCase5_AttemptToCloseAfterDeparture` (verify cannot close after departure: departure 22:00, current 22:05, expected closeFlight returns false)
_Requirement: Same as TC2 (Requirement 1 — flight has not yet departed)_
- Example not meeting requirement 1 (Root Cause C):
    - **R144009**(gpt-5.4-mini/flights5) — closeFlight did not check if current time is past departure time → returns true (expected false)

### R2_EmployeeManagementSystem (R2)

**R2001~R2005** failure patterns are highly consistent, all being NPE (`employees`/`coordinates` not initialized):

R2_EmployeeManagementSystem system (employee management system): 5 sample failures are all classic EMF lazy loading field direct access NPE.

#### Common Failure Root Causes

**Root Cause A: Department.calculateAverageWorkerWorkingHours() directly accesses `employees` field → NPE**
Some samples' `Department.java` use `for (Employee employee : employees)` instead of `for (Employee employee : getEmployees())`. In EMF, `employees` is a lazily initialized field, only created through the getter. An empty department that has never called the getter → `employees == null` → NPE.
- Affected samples: deepseek-v4-flash/employee1, minimax-m3/employee3
- Unaffected samples (using getter): deepseek-v4-flash/employee3/5, minimax-m3/employee1

**Root Cause B: Manager.getDirectSubordinateEmployeesCount() directly accesses `subordinates` field → NPE**
All 5 samples' `Manager.java` use `this.subordinates.size()` instead of `getSubordinates().size()`. A newly created Manager that has never called `getSubordinates()` → `subordinates == null` → NPE.
- Affected samples: All 5 (deepseek-v4-flash/employee1/3/5, minimax-m3/employee1/3)

#### Organized by testCase

CR2Test's `testCase4_EmptyDepartment` (verify empty department returns 0: create Delivery department with no workers, call `dept.calculateAverageWorkerWorkingHours()`, expected to return 0.00)
_Requirement (CR_detail #2):
1. Calculate the average weeklyWorkingHour of all Worker employees in the department
2. If the department has no workers, return 0_
- Example not meeting requirement 2 (Root Cause A — employees field NPE):
    - **R2001**(deepseek-v4-flash/employee1) — `for (Employee employee : employees)` → NPE: Cannot invoke "EList.iterator()" because "this.employees" is null
    - **R2004**(minimax-m3/employee3) — same

CR5Test's `testCase2_NoDirectReports` (verify Manager without subordinates returns 0: create Manager with no subordinates, call `m1.getDirectSubordinateEmployeesCount()`, expected to return 0)
_Requirement (CR_detail #5):
1. Get the count of direct subordinate employees for each manager_
- Example not meeting requirement 1 (Root Cause B — subordinates field NPE):
    - **R2001~R2003**(deepseek-v4-flash/employee1/3/5) — `this.subordinates.size()` → NPE: Cannot invoke "EList.size()" because "this.subordinates" is null
    - **R2004~R2005**(minimax-m3/employee1/3) — same

The same model exhibits exactly consistent NPE issues across different samples, indicating that the generated EMF model classes' reference lists (`employees`, `subordinates`) all use lazy initialization but the functional code does not explicitly initialize them.

### R22_IPOApplication

R22_IPOApplication system (IPO stock application system): 19 samples with diverse failure patterns.

#### Common Failure Root Causes

**Root Cause A: Customer methods directly access `this.applications` field → EMF lazy loading NPE (qwen3.6-flash/ipo4 only)**
All Customer methods in qwen3.6-flash/ipo4 (`createApplication()`, `cancelApplication()`, `getApplicationCount()` etc.) use `this.applications` instead of `getApplications()`. A newly created Customer that has never called the getter → `applications == null` → All tests NPE, 15 failures.

**Root Cause B: createApplication does not validate shares>0, amount>0, doc!=null (deepseek-v4-flash/ipo4, qwen3.6-flash/ipo2 etc.)**
Code does not implement the input validation required by the specification: shares=0, amount=0, negative shares, null document are all accepted.

**Root Cause C: cancel() sets status to REJECTED instead of CANCELED → getApplicationCount() overcounts (deepseek-v4-flash/ipo1/4/5)**
`Application.cancel()` sets `setStatus(ApplicationStatus.REJECTED)`, while `getApplicationCount()` counts APPROVAL + REJECTED → count after cancel = 1 (expected 0).

**Root Cause D: cancel() always returns false (gemini-3.1-flash-lite/ipo2/3/5)**
`Application.cancel()` directly `return false` in the PENDING branch (without setting any status), causing all cancellation operations to fail.

**Root Cause E: Application.cancel() and Customer.cancelApplication() mutually recursive → StackOverflowError (minimax-m3/ipo3 only)**
`Application.cancel()` calls `customer.cancelApplication()`, which calls `app.cancel()` → infinite recursion.

**Root Cause F: approve() does not send email (gpt-5.4-mini/ipo1)**
`approve()` does not send notification emails to customer and company; tests expect the emails list to contain 2 messages but actual is 0.

#### Organized by testCase

CR1Test's `testCase4_NoDocument` (verify no document is rejected: eligible customer submits doc=null application, expected to return false)
_Requirement (CR_detail #1):
1. Application must provide a non-null allowance document
2. shares > 0 and amount > 0
3. Customer must be eligible and have no approved application for the same company_
- Example not meeting requirement 1 (Root Cause B — no null doc check):
    - **R22002**(deepseek-v4-flash/ipo4) — Code does not check `doc == null` → creates application returning true (expected false)
    - **R22017**(qwen3.6-flash/ipo2) — same

CR1Test's `testCase5_ZeroSharesAndAmount` (verify zero shares and zero amount rejected: shares=0, amount=0.0, expected to return false)
_Requirement: Same as TC4 (Requirement 2)_
- Example not meeting requirement 2 (Root Cause B — no positive number check):
    - **R22002**(deepseek-v4-flash/ipo4) — Code does not check `shares > 0` and `amount > 0` → creates application returning true
    - **R22017**(qwen3.6-flash/ipo2) — same
- Root Cause A — applications NPE:
    - **R22019**(qwen3.6-flash/ipo4) — `this.applications` NPE at Customer.java:377

CR1Test's `testCase6_NegativeSharesAndAmount` (verify negative values rejected: shares=-5, amount=-100, expected to return false)
_Requirement: Same as TC4 (Requirement 2)_
- Example not meeting requirement 2 (Root Cause B):
    - **R22002**(deepseek-v4-flash/ipo4), **R22017**(qwen3.6-flash/ipo2) — no positive check → return true
- Root Cause A:
    - **R22019**(qwen3.6-flash/ipo4) — NPE

CR3Test's `testCase5_CancelThenCount` (verify count resets after cancellation: create PENDING application then cancel, expected getApplicationCount() returns 0)
_Requirement (CR_detail #3):
1. Only count APPROVAL and REJECTED status applications
2. PENDING and CANCELED are not counted_
- Example not meeting requirement 2 (Root Cause C — cancel sets REJECTED instead of CANCELED):
    - **R22001**(deepseek-v4-flash/ipo1) — `cancel()` sets `REJECTED` → `getApplicationCount()` counts REJECTED → returns 1 (expected 0)
    - **R22002**(deepseek-v4-flash/ipo4), **R22003**(deepseek-v4-flash/ipo5) — same
- Root Cause E — StackOverflowError:
    - **R22013**(minimax-m3/ipo3) — `cancel()` ↔ `cancelApplication()` mutual recursion → StackOverflowError
- Root Cause A:
    - **R22019**(qwen3.6-flash/ipo4) — NPE

CR5Test's `testCase1_CancelPending` (verify canceling PENDING application succeeds: create PENDING application, call cancelApplication, expected to return true)
_Requirement (CR_detail #5):
1. Only PENDING applications can be canceled
2. APPROVED and REJECTED cannot be canceled
3. Return true on success, false otherwise_
- Example not meeting requirement 1 (Root Cause D — cancel always returns false):
    - **R22008**(gemini-3.1-flash-lite/ipo2), **R22009**(ipo3), **R22011**(ipo5) — `cancel()` directly `return false` → cancellation fails
- Root Cause E — StackOverflowError:
    - **R22013**(minimax-m3/ipo3) — mutual recursion

CR5Test's `testCase5_CancelOneKeepsOther` (verify canceling one does not affect another: has UrbanTech and AgroSeed both PENDING, cancel UrbanTech, expected AgroSeed still PENDING)
_Requirement: Same as TC1_
- Example not meeting requirement 1 (Root Cause D):
    - **R22008**(gemini-3.1-flash-lite/ipo2), **R22009**(ipo3) — cancel returns false
- Root Cause E:
    - **R22013**(minimax-m3/ipo3) — mutual recursion

CR2Test's `testCase1_ApprovePending` (verify approval sends notification emails: approve PENDING application, expected customer's emails list contains 2 messages)
_Requirement (CR_detail #2):
1. On approval, send two informational emails (one to customer, one to company)_
- Example not meeting requirement 1 (Root Cause F — no email sent):
    - **R22004**(gpt-5.4-mini/ipo1) — `approve()` did not call email sending → `expected:<2> but was:<0>`
