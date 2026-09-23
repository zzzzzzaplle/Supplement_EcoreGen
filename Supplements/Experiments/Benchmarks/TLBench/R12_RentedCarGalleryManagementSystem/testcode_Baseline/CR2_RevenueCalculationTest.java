import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;

public class CR2_RevenueCalculationTest {
    @Test
    public void tc1_singleRentalCalculation() {
        Store store = new Store();

        // ① Toyota Camry, 100 CNY × 3 天 = 300
        Car camry = createCar("ABC123", "Toyota Camry", 100);
        Rental r1 = createRental(camry, 100 * 3);

        // ② Honda Civic, 150 × 2 = 300
        Car civic = createCar("XYZ789", "Honda Civic", 150);
        Rental r2 = createRental(civic, 150 * 2);

        // ③ Ford Focus, 200 × 1 = 200
        Car focus = createCar("LMN456", "Ford Focus", 200);
        Rental r3 = createRental(focus, 200 * 1);
        
        store.addRental(r1);
        store.addRental(r2);
        store.addRental(r3);

        double revenue = store.calculateTotalRevenue();
        assertEquals(800.0, revenue, 0.001); // 300 + 300 + 200
    }

    @Test
    public void tc2_noRentalsCalculation() {
        Store store = new Store(); // rentals 列表为空
        double revenue = store.calculateTotalRevenue();
        assertEquals(0.0, revenue, 0.001);
    }

    @Test
    public void tc3_sameDailyPriceCalculation() {
        Store store = new Store();

        // Chevrolet Malibu, 120 × 2 = 240
        Car malibu = createCar("CAR001", "Chevrolet Malibu", 120);
        Rental r1 = createRental(malibu, 120 * 2);

        // Hyundai Elantra, 120 × 4 = 480
        Car elantra = createCar("CAR002", "Hyundai Elantra", 120);
        Rental r2 = createRental(elantra, 120 * 4);

        store.addRental(r1);
        store.addRental(r2);

        double revenue = store.calculateTotalRevenue();
        assertEquals(720.0, revenue, 0.001); // 240 + 480
    }

    @Test
    public void tc4_mixedPricesCalculation() {
        Store store = new Store();

        // Mazda 3, 90 × 5 = 450
        Car mazda3 = createCar("SED123", "Mazda 3", 90);
        Rental r1 = createRental(mazda3, 90 * 5);

        // Kia Sportage, 150 × 3 = 450
        Car sportage = createCar("SUV456", "Kia Sportage", 150);
        Rental r2 = createRental(sportage, 150 * 3);

        // Ford F-150, 250 × 1 = 250
        Car f150 = createCar("TRK789", "Ford F-150", 250);
        Rental r3 = createRental(f150, 250 * 1);

        store.addRental(r1);
        store.addRental(r2);
        store.addRental(r3);

        double revenue = store.calculateTotalRevenue();
        assertEquals(1150.0, revenue, 0.001); // 450 + 450 + 250
    }

    @Test
    public void tc5_oneDayRentalsCalculation() {
        Store store = new Store();

        // Mini Cooper, 180 × 1 = 180
        Car mini = createCar("MINI001", "Mini Cooper", 180);
        Rental r1 = createRental(mini, 180 * 1);

        // Harley Davidson, 220 × 1 = 220
        Car harley = createCar("MOTO002", "Harley Davidson", 220);
        Rental r2 = createRental(harley, 220 * 1);

        store.addRental(r1);
        store.addRental(r2);

        double revenue = store.calculateTotalRevenue();
        assertEquals(400.0, revenue, 0.001); // 180 + 220
    }

    private Car createCar(String plate, String model, double dailyPrice) {
        Car car = new Car();
        car.setPlate(plate);
        car.setModel(model);
        car.setDailyPrice(dailyPrice);
        return car;
    }

    private Rental createRental(Car car, double totalPrice) {
        Rental rental = new Rental();
        rental.setCar(car);
        rental.setTotalPrice(totalPrice);
        return rental;
    }
}
