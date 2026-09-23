
import static org.junit.Assert.*;
import java.util.Date;

public class CR3_CountEmployeesInProductionProjectsTest {
    // Helper methods
    Department createDepartment(String id, String email) {
        Department department = new Department();
        department.setID(id);
        department.setEmail(email);
        return department;
    }

    ProductionProject createProductionProject(String title, String siteCode) {
        ProductionProject project = new ProductionProject();
        project.setTitle(title);
        project.setSiteCode(siteCode);
        return project;
    }

    Employee createEmployee(String name, String id, EmployeeType type) {
        Employee employee = new Employee();
        employee.setName(name);
        employee.setID(id);
        employee.setType(type);
        return employee;
    }

    ResearchProject createResearchProject(String title) {
        ResearchProject project = new ResearchProject();
        project.setTitle(title);
        return project;
    }

    CommunityProject createCommunityProject(String title) {
        CommunityProject project = new CommunityProject();
        project.setTitle(title);
        return project;
    }

    EducationProject createEducationProject(String title) {
        EducationProject project = new EducationProject();
        project.setTitle(title);
        return project;
    }

    @org.junit.Test
    public void tc1_CountEmployeesInSingleDepartmentWithProductionProjects() {
        // Create a company C001
        Company companyC001 = new Company();

        // Create department D001
        Department departmentD001 = createDepartment("D001", "department1@example.com");
        companyC001.addDepartment(departmentD001);

        // Add production project
        ProductionProject project1 = createProductionProject("Product Launch", "PL123");
        departmentD001.addProject(project1);

        // Hire 3 permanent employees
        Employee emp1 = createEmployee("Alice", "E001", EmployeeType.PERMANENT);
        departmentD001.addEmployee(emp1);
        project1.addWorkingEmployee(emp1);

        Employee emp2 = createEmployee("Bob", "E002", EmployeeType.PERMANENT);
        departmentD001.addEmployee(emp2);
        project1.addWorkingEmployee(emp2);

        Employee emp3 = createEmployee("Charlie", "E003", EmployeeType.PERMANENT);
        departmentD001.addEmployee(emp3);
        project1.addWorkingEmployee(emp3);

        // Hire 2 temporary employees
        Employee emp4 = createEmployee("David", "E004", EmployeeType.TEMPORARY);
        departmentD001.addEmployee(emp4);
        project1.addWorkingEmployee(emp4);

        Employee emp5 = createEmployee("Eve", "E005", EmployeeType.TEMPORARY);
        departmentD001.addEmployee(emp5);
        project1.addWorkingEmployee(emp5);

        // Assertions
        int employeeCount = companyC001.countEmployeesInProductionProjects();
        assertEquals("Total number of permanent employees mismatch", 5, employeeCount);
    }

    @org.junit.Test
    public void tc2_CountEmployeesAcrossMultipleDepartments() {
        // Create a company C002
        Company companyC002 = new Company();

        // Create departments D001 and D002
        Department departmentD001 = createDepartment("D001", "department1@example.com");
        companyC002.addDepartment(departmentD001);

        Department departmentD002 = createDepartment("D002", "department2@example.com");
        companyC002.addDepartment(departmentD002);

        // Add production project to D001
        ProductionProject project2 = createProductionProject("Factory Upgrade", "FU456");
        departmentD001.addProject(project2);

        // Hire 4 permanent employees in D001
        for (int i = 1; i <= 4; i++) {
            Employee emp = createEmployee("Permanent Employee " + i, "E00" + i, EmployeeType.PERMANENT);
            departmentD001.addEmployee(emp);
            project2.addWorkingEmployee(emp);
        }

        // Add production project to D002
        ProductionProject project3 = createProductionProject("New Product Development", "NPD789");
        departmentD002.addProject(project3);

        // Hire 3 temporary employees in D002
        for (int i = 5; i <= 7; i++) {
            Employee emp = createEmployee("Temporary Employee " + (i - 4), "E00" + i, EmployeeType.TEMPORARY);
            departmentD002.addEmployee(emp);
            project3.addWorkingEmployee(emp);
        }

        // Assertions
        int employeeCount = companyC002.countEmployeesInProductionProjects();
        assertEquals("Total number of permanent employees mismatch", 7, employeeCount);
    }

    @org.junit.Test
    public void tc3_CountEmployeesWithNoProductionProjects() {
        // Create a company C003
        Company companyC003 = new Company();

        // Create department D003
        Department departmentD003 = createDepartment("D003", "department3@example.com");
        companyC003.addDepartment(departmentD003);

        // Add a research project (no production project)
        ResearchProject project4 = createResearchProject("Market Research");
        departmentD003.addProject(project4);

        // Hire 2 permanent employees
        Employee emp1 = createEmployee("Frank", "E006", EmployeeType.PERMANENT);
        departmentD003.addEmployee(emp1);

        Employee emp2 = createEmployee("Grace", "E007", EmployeeType.PERMANENT);
        departmentD003.addEmployee(emp2);

        // Assertions
        int employeeCount = companyC003.countEmployeesInProductionProjects();
        assertEquals("Total number of employees mismatch", 0, employeeCount);
    }

    @org.junit.Test
    public void tc4_CountEmployeesWithMixedProjectTypes() {
        // Create a company C004
        Company companyC004 = new Company();

        // Create department D004
        Department departmentD004 = createDepartment("D004", "department4@example.com");
        companyC004.addDepartment(departmentD004);

        // Add a production project
        ProductionProject project5 = createProductionProject("Process Optimization", "PO101");
        departmentD004.addProject(project5);

        // Hire 2 temporary employees
        for (int i = 1; i <= 2; i++) {
            Employee emp = createEmployee("Temporary Employee " + i, "E00" + (i + 10), EmployeeType.TEMPORARY);
            departmentD004.addEmployee(emp);
            project5.addWorkingEmployee(emp);
        }

        // Add a community project and an education project
        CommunityProject project6 = createCommunityProject("Community Support Initiative");
        departmentD004.addProject(project6);

        EducationProject project7 = createEducationProject("Scholarship Program");
        departmentD004.addProject(project7);

        // Assertions
        int employeeCount = companyC004.countEmployeesInProductionProjects();
        assertEquals("Total number of temporary employees mismatch", 2, employeeCount);
    }

    @org.junit.Test
    public void tc5_CountEmployeesInDepartmentWithoutActiveProjects() {
        // Create a company C005
        Company companyC005 = new Company();

        // Create department D005
        Department departmentD005 = createDepartment("D005", "department5@example.com");
        companyC005.addDepartment(departmentD005);

        // Hire 3 permanent employees
        for (int i = 1; i <= 3; i++) {
            Employee emp = createEmployee("Permanent Employee " + i, "E00" + (i + 20), EmployeeType.PERMANENT);
            departmentD005.addEmployee(emp);
        }

        // No projects are currently ongoing in this department

        // Assertions
        int employeeCount = companyC005.countEmployeesInProductionProjects();
        assertEquals("Total number of employees mismatch", 0, employeeCount);
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
 * Time: 0.032
 * 
 * OK (5 tests)
 * 
 * 
 */