import static org.junit.Assert.*;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

public class CR3_OverdueRentalsTest {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void tc1_singleOverdueRental() throws Exception {
        Store store = new Store();

        Customer john = createCustomer("John", "Doe");

        Rental r1 = createRental(john, createDate(2023, 9, 1), null);
        store.addRental(r1);

        Date current = createDate(2023, 9, 5); // > dueDate

        OverdueNotice overdueNotice = new OverdueNotice();
        store.addNotice(overdueNotice);

        List<Customer> overdue = store.findCustomersWithOverdueRentals(current);

        assertEquals(1, overdue.size());
        assertEquals("John", overdue.get(0).getName());
    }

    @Test
    public void tc2_noOverdueRentals() throws Exception {
        Store store = new Store();

        Customer jane = createCustomer("Jane", "Smith");

        Rental r2 = createRental(jane, createDate(2025, 9, 10), null); // 未到期

        store.addRental(r2);

        Date current = createDate(2025, 9, 1); // < dueDate
        List<Customer> overdue = store.findCustomersWithOverdueRentals(current);

        assertTrue(overdue.isEmpty());
    }

    @Test
    public void tc3_mixedStatusRentals() throws Exception {
        Store store = new Store();

        Customer alice = createCustomer("Alice", "Johnson");
        Customer john = createCustomer("John", "Doe");

        // 两条逾期
        Rental overdue1 = createRental(alice, createDate(2023, 9, 3), null);
        Rental overdue2 = createRental(john, createDate(2023, 9, 3), null);

        // 已归还（不计逾期）
        Rental returned = createRental(alice, createDate(2024, 9, 2), createDate(2024, 9, 1));

        store.addRental(overdue1);
        store.addRental(overdue2);
        store.addRental(returned);

        Date current = createDate(2023, 9, 5);
        List<Customer> overdue = store.findCustomersWithOverdueRentals(current);

        assertEquals(2, overdue.size());
        // 用集合校验姓名
        Set<String> names = new HashSet<>();
        for (Customer c : overdue)
            names.add(c.getName());
        assertTrue(names.contains("Alice"));
        assertTrue(names.contains("John"));
    }

    @Test
    public void tc4_rentalWithBackDate() throws Exception {
        Store store = new Store();

        Customer bob = createCustomer("Bob", "Brown");

        Rental r5 = createRental(bob, createDate(2023, 9, 3), createDate(2023, 9, 4)); // 已归还

        store.addRental(r5);

        Date current = createDate(2023, 9, 5);
        List<Customer> overdue = store.findCustomersWithOverdueRentals(current);

        assertTrue(overdue.isEmpty());
    }

    @Test
    public void tc5_futureDueDateRentals() throws Exception {
        Store store = new Store();

        Customer charlie = createCustomer("Charlie", "Green");

        Rental r6 = createRental(charlie, createDate(2025, 9, 15), null); // 未到期

        store.addRental(r6);

        Date current = createDate(2025, 9, 10);
        List<Customer> overdue = store.findCustomersWithOverdueRentals(current);

        assertTrue(overdue.isEmpty());
    }

    @Test
    public void tc6_nullCurrentDateReturnsEmptyList() throws Exception {
        Store store = new Store();

        Customer emma = createCustomer("Emma", "White");
        Rental overdueRental = createRental(emma, createDate(2024, 0, 10), null);
        store.addRental(overdueRental);

        List<Customer> overdue = store.findCustomersWithOverdueRentals(null);

        assertNotNull(overdue);
        assertTrue(overdue.isEmpty());
    }

    private Customer createCustomer(String name, String surname) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setSurname(surname);
        return customer;
    }

    private Rental createRental(Customer customer, Date dueDate, Date backDate) {
        Rental rental = new Rental();
        rental.setCustomer(customer);
        rental.setDueDate(dueDate);
        rental.setBackDate(backDate);
        return rental;
    }

    private Date createDate(int year, int month, int day) {
        return new Date(year - 1900, month, day);
    }
}

/*
 * compile_result:
 * 
 * 
 * 
 * run_result:
 * JUnit version 4.13.2
 * .....
 * Time: 0.046
 * 
 * OK (5 tests)
 * 
 * 
 */
