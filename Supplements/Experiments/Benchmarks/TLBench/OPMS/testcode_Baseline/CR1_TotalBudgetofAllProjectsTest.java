
import static org.junit.Assert.*;
import java.util.Date;

public class CR1_TotalBudgetofAllProjectsTest{
    // Do not generate @Before public void setUp() {...}

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
    
    ProductionProject createProductionProject(String title, double budget) {
        ProductionProject project = new ProductionProject();
        project.setTitle(title);
        project.setBudget(budget);
        return project;
    }
    
    ProductionProject createProductionProject(String title, double budget, Date deadline) {
        ProductionProject project = new ProductionProject();
        project.setTitle(title);
        project.setBudget(budget);
        project.setDeadline(deadline);
        return project;
    }
    
    EducationProject createEducationProject(String title, double budget, Date deadline, FundingGroup fundingGroup) {
        EducationProject project = new EducationProject();
        project.setTitle(title);
        project.setBudget(budget);
        project.setDeadline(deadline);
        project.setFundingGroup(fundingGroup);
        return project;
    }
    
    CommunityProject createCommunityProject(String title, double budget, Date deadline, FundingGroup fundingGroup) {
        CommunityProject project = new CommunityProject();
        project.setTitle(title);
        project.setBudget(budget);
        project.setDeadline(deadline);
        project.setFundingGroup(fundingGroup);
        return project;
    }
    
    FundingGroup createFundingGroup(FundingGroupType type) {
        FundingGroup fundingGroup = new FundingGroup();
        fundingGroup.setType(type);
        return fundingGroup;
    }

    @org.junit.Test
    public void tc1_SingleDepartmentBudgetCalculation() {
        // Test logic (implement test case 1 set up)
        Company company = new Company();
        Department department = createDepartment("D001", "department1@company.com");
        company.addDepartment(department);

        Project project1 = createProductionProject("Website Redevelopment", "Redesigning the company website", 10000, new Date(2025 - 1900, 12 - 1, 31));
        Project project2 = createProductionProject("Mobile App Development", "Developing a customer service app", 15000, new Date(2026 - 1900, 1 - 1, 15));

        department.addProject(project1);
        department.addProject(project2);

        double totalBudget = company.calculateTotalBudget(); // Testing: calculateTotalBudget(): double

        assertEquals("Total budget mismatch", 25000, totalBudget, 0.01);
    }

    @org.junit.Test
    public void tc2_MultipleDepartmentsBudgetCalculation() {
        // Test logic (implement test case 2 set up)
        Company company = new Company();

        Department department1 = createDepartment("D001", "department1@company.com");
        company.addDepartment(department1);

        Project project1 = createProductionProject("HR Software", 20000);
        department1.addProject(project1);

        Department department2 = createDepartment("D002", "department2@company.com");
        company.addDepartment(department2);

        Project project2 = createProductionProject("Sales Training Program", 30000);
        department2.addProject(project2);

        Project project3 = createProductionProject("Marketing Campaign", 25000);
        department2.addProject(project3);

        double totalBudget = company.calculateTotalBudget(); // Testing: calculateTotalBudget(): double

        assertEquals("Total budget mismatch", 75000, totalBudget, 0.01);
    }

    @org.junit.Test
    public void tc3_BudgetCalculationWithZeroProjects() {
        // Test logic (implement test case 3 set up)
        Company company = new Company();
        Department department = createDepartment("D003", "department3@company.com");
        company.addDepartment(department);

        double totalBudget = company.calculateTotalBudget(); // Testing: calculateTotalBudget(): double

        assertEquals("Expected budget should be zero", 0, totalBudget, 0.01);
    }

    @org.junit.Test
    public void tc4_EducationProjectBudgetWithFundingGroup() {
        // Test logic (implement test case 4 set up)
        Company company = new Company();

        Department department1 = createDepartment("D004", "department4@company.com");
        company.addDepartment(department1);

        FundingGroup fundingGroup = createFundingGroup(FundingGroupType.GOVERNMENT);
        EducationProject educationProject = createEducationProject("Scholarship Program", 50000, new Date(2026 - 1900, 5 - 1, 31), fundingGroup);
        department1.addProject(educationProject);

        Project project1 = createProductionProject("R&D Initiative", 70000, new Date(2026 - 1900, 7 - 1, 15));
        department1.addProject(project1);

        Department department2 = createDepartment("D005", "department5@company.com");
        company.addDepartment(department2);

        Project project2 = createProductionProject("R&D5 Initiative", 70000, new Date(2026 - 1900, 7 - 1, 19));
        department2.addProject(project2);

        double totalBudget = company.calculateTotalBudget(); // Testing: calculateTotalBudget(): double

        assertEquals("Total budget mismatch", 190000, totalBudget, 0.01);
    }

    @org.junit.Test
    public void tc5_CommunityProjectBudgetWithMixedFundingGroup() {
        // Test logic (implement test case 5 set up)
        Company company = new Company();

        Department department = createDepartment("D006", "department5@company.com");
        company.addDepartment(department);

        FundingGroup fundingGroup = createFundingGroup(FundingGroupType.MIXED);
        CommunityProject communityProject = createCommunityProject("Community Health Awareness", 40000, new Date(2027 - 1900, 2 - 1, 28), fundingGroup);
        department.addProject(communityProject);

        Project project1 = createProductionProject("Environmental Clean-up Initiative", 60000, new Date(2027 - 1900, 3 - 1, 30));
        department.addProject(project1);

        double totalBudget = company.calculateTotalBudget(); // Testing: calculateTotalBudget(): double

        assertEquals("Total budget mismatch", 100000, totalBudget, 0.01);
    }
}

/*
 * compile_result:
 * 注: D:\eclipse-workshop\testCase\src\test\java\
 * SingleDepartmentBudgetCalculationTest.java使用或覆盖了已过时的 API。
 * 注: 有关详细信息, 请使用 -Xlint:deprecation 重新编译。
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