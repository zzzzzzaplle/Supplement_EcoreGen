import static org.junit.Assert.*;

public class CR5_ManageDepartmentsTest {
    // Do not generate @Before public void setUp() {...}

    // Helper methods
    Company createCompany() {
        return new Company();
    }

    Department createDepartment(String id, String email) {
        Department department = new Department();
        department.setID(id);
        department.setEmail(email);
        return department;
    }

    @org.junit.Test
    public void tc1_AddDepartmentWithinLimitAndUniqueName() {
        // Test Case 1: Add department within limit and with unique name
        Company company = createCompany();

        Department d1 = createDepartment("HR", "hr@company.com");
        Department d2 = createDepartment("Finance", "finance@company.com");
        Department d3 = createDepartment("IT", "it@company.com");

        company.addDepartment(d1);
        company.addDepartment(d2);

        assertEquals("Initial department count should be 2", 2, company.getDepartments().size());

        company.addDepartment(d3);

        assertEquals("Department count after adding should be 3", 3, company.getDepartments().size());
        assertTrue("Company should contain department with name IT",
                company.getDepartments().stream().anyMatch(d -> "IT".equals(d.getID())));
    }

    @org.junit.Test
    public void tc2_RejectAddingDepartmentWhenExceedingMaxLimit() {
        // Test Case 2: Reject adding department when reaching maximum limit (8 -> 9)
        Company company = createCompany();

        // Add 8 departments with distinct names
        for (int i = 1; i <= 8; i++) {
            Department d = createDepartment("Dept" + i, "dept" + i + "@company.com");
            company.addDepartment(d);
        }

        assertEquals("Department count should be 8 before adding the 9th", 8, company.getDepartments().size());

        Department d9 = createDepartment("Dept9", "dept9@company.com");

        try {
            company.addDepartment(d9);
            fail("Expected IllegalStateException when adding 9th department");
        } catch (IllegalStateException e) {
            // Expected exception
        }

        assertEquals("Department count should remain 8 after failed addition", 8, company.getDepartments().size());
    }

    @org.junit.Test
    public void tc3_RejectAddingDepartmentWithDuplicateName() {
        // Test Case 3: Reject adding department with duplicate name
        Company company = createCompany();

        Department d1 = createDepartment("Sales", "sales1@company.com");
        Department d2 = createDepartment("Support", "support@company.com");
        Department d3 = createDepartment("Sales", "sales2@company.com"); // duplicate name "Sales"

        company.addDepartment(d1);
        company.addDepartment(d2);

        assertEquals("Initial department count should be 2", 2, company.getDepartments().size());

        try {
            company.addDepartment(d3);
            fail("Expected IllegalArgumentException when adding department with duplicate name");
        } catch (IllegalArgumentException e) {
            // Expected exception
        }

        assertEquals("Department count should remain 2 after failed addition", 2, company.getDepartments().size());
        long salesCount = company.getDepartments().stream()
                .filter(d -> "Sales".equals(d.getID()))
                .count();
        assertEquals("There should still be exactly one department named 'Sales'", 1, salesCount);
    }

    @org.junit.Test
    public void tc4_RemoveDepartmentWhenAboveMinimum() {
        // Test Case 4: Remove department by name when above minimum (3 -> 2)
        Company company = createCompany();

        Department d1 = createDepartment("HR", "hr@company.com");
        Department d2 = createDepartment("Finance", "finance@company.com");
        Department d3 = createDepartment("IT", "it@company.com");

        company.addDepartment(d1);
        company.addDepartment(d2);
        company.addDepartment(d3);

        assertEquals("Initial department count should be 3", 3, company.getDepartments().size());

        company.removeDepartment("IT");

        assertEquals("Department count after removal should be 2", 2, company.getDepartments().size());
        assertFalse("Company should not contain department with name IT after removal",
                company.getDepartments().stream().anyMatch(d -> "IT".equals(d.getID())));
    }

    @org.junit.Test
    public void tc5_RejectRemovingDepartmentByNameAtMinimumLimit() {
        // Test Case 5: Reject removing department by name at minimum limit (2)
        Company company = createCompany();

        Department d1 = createDepartment("HR", "hr@company.com");
        Department d2 = createDepartment("Finance", "finance@company.com");

        company.addDepartment(d1);
        company.addDepartment(d2);

        assertEquals("Initial department count should be 2", 2, company.getDepartments().size());

        try {
            company.removeDepartment("Finance");
            fail("Expected IllegalStateException when removing department at minimum limit");
        } catch (IllegalStateException e) {
            // Expected exception
        }

        assertEquals("Department count should remain 2 after failed removal", 2, company.getDepartments().size());
        assertTrue("Company should still contain department with name Finance",
                company.getDepartments().stream().anyMatch(d -> "Finance".equals(d.getID())));
    }
}