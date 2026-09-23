package edu.project.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.project.Company;
import edu.project.Department;
import edu.project.ProjectFactory;

public class CR5Test {
    private ProjectFactory factory;

    @Before
    public void setUp() {
        factory = ProjectFactory.eINSTANCE;
    }

    @Test
    public void testAddDepartmentWithinLimitAndWithUniqueName() {
        Company company = createCompanyWithDepartmentNames("HR", "Finance");
        Department it = createDepartment("IT", "it@company.com");

        company.addDepartment(it);

        assertEquals(3, company.getDepartments().size());
        assertTrue(hasDepartmentNamed(company, "HR"));
        assertTrue(hasDepartmentNamed(company, "Finance"));
        assertTrue(hasDepartmentNamed(company, "IT"));
    }

    @Test
    public void testRejectAddingDepartmentWhenReachingMaximumLimit() {
        Company company = createCompanyWithDepartmentNames(
                "Dept1", "Dept2", "Dept3", "Dept4", "Dept5", "Dept6", "Dept7", "Dept8");
        Department dept9 = createDepartment("Dept9", "dept9@company.com");

        expectException(IllegalStateException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                company.addDepartment(dept9);
            }
        });

        assertEquals(8, company.getDepartments().size());
        assertTrue(hasDepartmentNamed(company, "Dept1"));
        assertTrue(hasDepartmentNamed(company, "Dept8"));
        assertFalse(hasDepartmentNamed(company, "Dept9"));
    }

    @Test
    public void testRejectAddingDepartmentWithDuplicateName() {
        Company company = createCompanyWithDepartmentNames("Sales", "Support");
        Department duplicateSales = createDepartment("Sales", "sales2@company.com");

        expectException(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                company.addDepartment(duplicateSales);
            }
        });

        assertEquals(2, company.getDepartments().size());
        assertTrue(hasDepartmentNamed(company, "Sales"));
        assertTrue(hasDepartmentNamed(company, "Support"));
    }

    @Test
    public void testRemoveDepartmentByNameWhenAboveMinimum() {
        Company company = createCompanyWithDepartmentNames("HR", "Finance", "IT");

        String removedName = company.removeDepartment("IT");

        assertEquals("IT", removedName);
        assertEquals(2, company.getDepartments().size());
        assertTrue(hasDepartmentNamed(company, "HR"));
        assertTrue(hasDepartmentNamed(company, "Finance"));
        assertFalse(hasDepartmentNamed(company, "IT"));
    }

    @Test
    public void testRejectRemovingDepartmentByNameAtMinimumLimit() {
        Company company = createCompanyWithDepartmentNames("HR", "Finance");

        expectException(IllegalStateException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                company.removeDepartment("Finance");
            }
        });

        assertEquals(2, company.getDepartments().size());
        assertTrue(hasDepartmentNamed(company, "HR"));
        assertTrue(hasDepartmentNamed(company, "Finance"));
    }

    private Company createCompanyWithDepartmentNames(String... names) {
        Company company = factory.createCompany();
        for (String name : names) {
            company.getDepartments().add(createDepartment(name, name.toLowerCase() + "@company.com"));
        }
        return company;
    }

    private Department createDepartment(String name, String email) {
        Department department = factory.createDepartment();
        department.setID(name);
        department.setEmail(email);
        return department;
    }

    private boolean hasDepartmentNamed(Company company, String name) {
        return departmentNames(company).contains(name);
    }

    private List<String> departmentNames(Company company) {
        List<String> names = new ArrayList<String>();
        for (Department department : company.getDepartments()) {
            names.add(department.getID());
        }
        return names;
    }

    private void expectException(Class<? extends RuntimeException> expectedType, ThrowingRunnable action) {
        try {
            action.run();
        } catch (RuntimeException actual) {
            if (expectedType.isInstance(actual)) {
                return;
            }
            throw actual;
        }
        throw new AssertionError("Expected exception: " + expectedType.getName());
    }

    private interface ThrowingRunnable {
        void run();
    }
}
