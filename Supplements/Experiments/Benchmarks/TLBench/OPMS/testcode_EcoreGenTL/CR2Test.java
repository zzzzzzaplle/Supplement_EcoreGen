package edu.project.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.project.Department;
import edu.project.ProductionProject;
import edu.project.Project;
import edu.project.ProjectFactory;

public class CR2Test {
    private static final double EPSILON = 0.01;

    private ProjectFactory factory;

    @Before
    public void setUp() {
        factory = ProjectFactory.eINSTANCE;
    }

    @Test
    public void testSingleProjectAverageBudgetCalculation() {
        Department marketing = createDepartment("D001", "marketing@company.com");
        marketing.getProjects().add(createProject("Ads Campaign", "Q1 advertising budget", 50000.0));
        marketing.getProjects().add(createProject("Market Research", "Research on consumer behavior", 30000.0));

        Department development = createDepartment("D002", "development@company.com");
        development.getProjects().add(createProject("New Feature Development", "Developing a new feature", 200000.0));

        assertEquals(40000.0, marketing.calculateAverageBudget(), EPSILON);
    }

    @Test
    public void testMultipleDepartmentsAverageBudgetCalculation() {
        Department hr = createDepartment("D001", "hr@company.com");
        Department sales = createDepartment("D002", "sales@company.com");
        Department it = createDepartment("D003", "it@company.com");

        hr.getProjects().add(createProject("Employee Training", "Training for employees", 20000.0));
        sales.getProjects().add(createProject("Sales Strategy", "New sales strategy implementation", 40000.0));
        it.getProjects().add(createProject("System Upgrade", "Upgrade company systems", 60000.0));

        assertEquals(20000.0, hr.calculateAverageBudget(), EPSILON);
        assertEquals(40000.0, sales.calculateAverageBudget(), EPSILON);
        assertEquals(60000.0, it.calculateAverageBudget(), EPSILON);
        assertEquals(40000.0,
                (hr.calculateAverageBudget() + sales.calculateAverageBudget() + it.calculateAverageBudget()) / 3.0,
                EPSILON);
    }

    @Test
    public void testSingleProjectCalculationForZeroBudget() {
        Department finance = createDepartment("D004", "finance@company.com");
        finance.getProjects().add(createProject("Budget Review", "Review of the annual budget", 0.0));

        assertEquals(0.0, finance.calculateAverageBudget(), EPSILON);
    }

    @Test
    public void testNoProjectsInDepartment() {
        Department research = createDepartment("D005", "research@company.com");

        assertEquals(0.0, research.calculateAverageBudget(), EPSILON);
    }

    @Test
    public void testProjectsWithDifferentBudgetsCalculation() {
        Department product = createDepartment("D006", "product@company.com");
        product.getProjects().add(createProject("Product Launch", "Launching new product", 150000.0));
        product.getProjects().add(createProject("Market Analysis", "Analysis of market trends", 75000.0));
        product.getProjects().add(createProject("Client Engagement", "Engaging with key clients", 50000.0));

        assertEquals(91666.67, product.calculateAverageBudget(), EPSILON);
    }

    private Department createDepartment(String id, String email) {
        Department department = factory.createDepartment();
        department.setID(id);
        department.setEmail(email);
        return department;
    }

    private Project createProject(String title, String description, double budget) {
        ProductionProject project = factory.createProductionProject();
        project.setTitle(title);
        project.setDescription(description);
        project.setBudget(budget);
        return project;
    }
}
