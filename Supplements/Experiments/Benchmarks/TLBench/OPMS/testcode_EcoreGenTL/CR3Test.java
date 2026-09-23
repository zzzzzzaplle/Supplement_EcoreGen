package edu.project.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.project.CommunityProject;
import edu.project.Company;
import edu.project.Department;
import edu.project.EducationProject;
import edu.project.Employee;
import edu.project.EmployeeType;
import edu.project.ProductionProject;
import edu.project.ProjectFactory;
import edu.project.ResearchProject;

public class CR3Test {
    private ProjectFactory factory;

    @Before
    public void setUp() {
        factory = ProjectFactory.eINSTANCE;
    }

    @Test
    public void testCountEmployeesInSingleDepartmentWithProductionProjects() {
        Company company = createCompany();
        Department department = createDepartment("D001", "department1@example.com");
        company.getDepartments().add(department);
        ProductionProject project = createProductionProject("Product Launch", "PL123");
        department.getProjects().add(project);

        addEmployeesToProject(department, project, EmployeeType.PERMANENT, "E001", "Alice", 3);
        addEmployeesToProject(department, project, EmployeeType.TEMPORARY, "E004", "David", 2);

        assertEquals(5, company.countEmployeesInProductionProjects());
    }

    @Test
    public void testCountEmployeesAcrossMultipleDepartments() {
        Company company = createCompany();
        Department department1 = createDepartment("D001", "department1@example.com");
        Department department2 = createDepartment("D002", "department2@example.com");
        company.getDepartments().add(department1);
        company.getDepartments().add(department2);

        ProductionProject factoryUpgrade = createProductionProject("Factory Upgrade", "FU456");
        ProductionProject newProduct = createProductionProject("New Product Development", "NPD789");
        department1.getProjects().add(factoryUpgrade);
        department2.getProjects().add(newProduct);

        addEmployeesToProject(department1, factoryUpgrade, EmployeeType.PERMANENT, "E001", "Permanent", 4);
        addEmployeesToProject(department2, newProduct, EmployeeType.TEMPORARY, "E005", "Temporary", 3);

        assertEquals(7, company.countEmployeesInProductionProjects());
    }

    @Test
    public void testCountEmployeesWithNoProductionProjects() {
        Company company = createCompany();
        Department department = createDepartment("D003", "department3@example.com");
        company.getDepartments().add(department);

        ResearchProject researchProject = factory.createResearchProject();
        researchProject.setTitle("Market Research");
        department.getProjects().add(researchProject);
        department.getEmployees().add(createEmployee("E006", "Frank", EmployeeType.PERMANENT));
        department.getEmployees().add(createEmployee("E007", "Grace", EmployeeType.PERMANENT));

        assertEquals(0, company.countEmployeesInProductionProjects());
    }

    @Test
    public void testCountEmployeesWithMixedProjectTypes() {
        Company company = createCompany();
        Department department = createDepartment("D004", "department4@example.com");
        company.getDepartments().add(department);

        ProductionProject productionProject = createProductionProject("Process Optimization", "PO101");
        CommunityProject communityProject = factory.createCommunityProject();
        EducationProject educationProject = factory.createEducationProject();
        department.getProjects().add(productionProject);
        department.getProjects().add(communityProject);
        department.getProjects().add(educationProject);

        addEmployeesToProject(department, productionProject, EmployeeType.TEMPORARY, "E001", "Temporary", 2);

        assertEquals(2, company.countEmployeesInProductionProjects());
    }

    @Test
    public void testCountEmployeesInDepartmentWithoutActiveProjects() {
        Company company = createCompany();
        Department department = createDepartment("D005", "department5@example.com");
        company.getDepartments().add(department);

        department.getEmployees().add(createEmployee("E008", "Henry", EmployeeType.PERMANENT));
        department.getEmployees().add(createEmployee("E009", "Ian", EmployeeType.PERMANENT));
        department.getEmployees().add(createEmployee("E010", "Jack", EmployeeType.PERMANENT));

        assertEquals(0, company.countEmployeesInProductionProjects());
    }

    private Company createCompany() {
        return factory.createCompany();
    }

    private Department createDepartment(String id, String email) {
        Department department = factory.createDepartment();
        department.setID(id);
        department.setEmail(email);
        return department;
    }

    private ProductionProject createProductionProject(String title, String siteCode) {
        ProductionProject project = factory.createProductionProject();
        project.setTitle(title);
        project.setSiteCode(siteCode);
        return project;
    }

    private void addEmployeesToProject(
            Department department,
            ProductionProject project,
            EmployeeType type,
            String firstId,
            String namePrefix,
            int count) {
        int start = Integer.parseInt(firstId.substring(1));
        for (int i = 0; i < count; i++) {
            String id = String.format("E%03d", start + i);
            Employee employee = createEmployee(id, namePrefix + i, type);
            department.getEmployees().add(employee);
            project.getWorkingEmployees().add(employee);
        }
    }

    private Employee createEmployee(String id, String name, EmployeeType type) {
        Employee employee = factory.createEmployee();
        employee.setID(id);
        employee.setName(name);
        employee.setEmail(id.toLowerCase() + "@example.com");
        employee.setNumber(id);
        employee.setType(type);
        return employee;
    }
}
