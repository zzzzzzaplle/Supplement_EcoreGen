
import static org.junit.Assert.*;
import java.util.Date;

public class CR2_AverageBudgetTest {
    // Helper methods
    Department createDepartment(String id, String email) {
        Department department = new Department();
        department.setID(id);
        department.setEmail(email);
        return department;
    }

    ProductionProject createProductionProject(String title, String description, double budget, Date deadline) {
        ProductionProject project = new ProductionProject();
        project.setTitle(title);
        project.setDescription(description);
        project.setBudget(budget);
        project.setDeadline(deadline);
        return project;
    }

    ProductionProject createProductionProject(String title, double budget, Date deadline) {
        ProductionProject project = new ProductionProject();
        project.setTitle(title);
        project.setBudget(budget);
        project.setDeadline(deadline);
        return project;
    }

    @org.junit.Test
    public void tc1_SingleProjectAverageBudgetCalculation() {
        // Test logic (implement test case 1 set up)
        Department departmentD001 = createDepartment("D001", "marketing@company.com");

        ProductionProject project1 = createProductionProject("Ads Campaign", "Q1 advertising budget", 50000, new Date(2025 - 1900, 11, 31)); // December 31, 2025

        ProductionProject project2 = createProductionProject("Market Research", "Research on consumer behavior", 30000, new Date(2025 - 1900, 10, 30)); // November 30, 2025

        departmentD001.addProject(project1);
        departmentD001.addProject(project2);

        Department departmentD002 = createDepartment("D002", "development@company.com");

        ProductionProject project3 = createProductionProject("New Feature Development", "Developing a new feature", 200000, new Date(2026 - 1900, 0, 15)); // January 15, 2026

        departmentD002.addProject(project3);

        // Calculate average budget for department D001
        double averageBudgetD001 = departmentD001.calculateAverageBudget();

        // Assertions
        assertEquals("Average budget calculation for department D001 is incorrect", 40000.0, averageBudgetD001, 0.01);
    }

    @org.junit.Test
    public void tc2_MultipleDepartmentsAverageBudgetCalculation() {
        // Test logic (implement test case 2 set up)
        Department departmentD001 = createDepartment("D001", "hr@company.com");

        ProductionProject project1 = createProductionProject("Employee Training", "Training for employees", 20000, new Date(2025 - 1900, 7, 15)); // August 15, 2025
        departmentD001.addProject(project1);

        Department departmentD002 = createDepartment("D002", "sales@company.com");

        ProductionProject project2 = createProductionProject("Sales Strategy", "New sales strategy implementation", 40000, new Date(2025 - 1900, 8, 15)); // September 15, 2025
        departmentD002.addProject(project2);

        Department departmentD003 = createDepartment("D003", "it@company.com");

        ProductionProject project3 = createProductionProject("System Upgrade", "Upgrade company systems", 60000, new Date(2025 - 1900, 9, 1)); // October 1, 2025
        departmentD003.addProject(project3);

        // Calculate average budget for each department
        double averageBudgetD001 = departmentD001.calculateAverageBudget();
        double averageBudgetD002 = departmentD002.calculateAverageBudget();
        double averageBudgetD003 = departmentD003.calculateAverageBudget();

        // Assertions
        assertEquals("Average budget for department D001 is incorrect", 20000.0, averageBudgetD001, 0.01);
        assertEquals("Average budget for department D002 is incorrect", 40000.0, averageBudgetD002, 0.01);
        assertEquals("Average budget for department D003 is incorrect", 60000.0, averageBudgetD003, 0.01);

        // Calculate overall average budget across departments
        double overallAverageBudget = (averageBudgetD001 + averageBudgetD002 + averageBudgetD003) / 3;
        assertEquals("Overall average budget for departments is incorrect", 40000.0, overallAverageBudget, 0.01);
    }

    @org.junit.Test
    public void tc3_SingleProjectCalculationForZeroBudget() {
        // Test logic (implement test case 3 set up)
        Department departmentD004 = createDepartment("D004", "finance@company.com");

        ProductionProject project = createProductionProject("Budget Review", "Review of the annual budget", 0, new Date(2025 - 1900, 6, 30)); // July 30, 2025
        departmentD004.addProject(project);

        // Calculate average budget for department D004
        double averageBudgetD004 = departmentD004.calculateAverageBudget();

        // Assertions
        assertEquals("Average budget for department D004 with zero budget project is incorrect", 0.0, averageBudgetD004,
                0.01);
    }

    @org.junit.Test
    public void tc4_NoProjectsInDepartment() {
        // Test logic (implement test case 4 set up)
        Department departmentD005 = createDepartment("D005", "research@company.com");

        // Calculate average budget for department D005 (which has no projects)
        double averageBudgetD005 = departmentD005.calculateAverageBudget();

        // Assertions
        assertEquals("Average budget for department D005 with no projects is incorrect", 0.0, averageBudgetD005, 0.01);
    }

    @org.junit.Test
    public void tc5_ProjectsWithDifferentBudgetsCalculation() {
        // Test logic (implement test case 5 set up)
        Department departmentD006 = createDepartment("D006", "product@company.com");

        ProductionProject project1 = createProductionProject("Product Launch", "Launching new product", 150000, new Date(2025 - 1900, 11, 1)); // December 1, 2025
        departmentD006.addProject(project1);

        ProductionProject project2 = createProductionProject("Market Analysis", "Analysis of market trends", 75000, new Date(2025 - 1900, 9, 15)); // October 15, 2025
        departmentD006.addProject(project2);

        ProductionProject project3 = createProductionProject("Client Engagement", "Engaging with key clients", 50000, new Date(2025 - 1900, 10, 30)); // November 30, 2025
        departmentD006.addProject(project3);

        // Calculate average budget for department D006
        double averageBudgetD006 = departmentD006.calculateAverageBudget();

        // Assertions
        assertEquals("Average budget for department D006 with different budgets is incorrect", 91666.67,
                averageBudgetD006, 0.01);
    }
}
