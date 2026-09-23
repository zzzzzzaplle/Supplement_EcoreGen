
import static org.junit.Assert.*;
import java.util.Date;

public class CR4_FundingGroupTypeTest {
    // Do not generate @Before public void setUp() {...}

    // Helper methods
    Department createDepartment(String id, String email) {
        Department department = new Department();
        department.setID(id);
        department.setEmail(email);
        return department;
    }

    CommunityProject createCommunityProject(String title, String description, double budget, Date deadline,
            FundingGroup fundingGroup) {
        CommunityProject project = new CommunityProject();
        project.setTitle(title);
        project.setDescription(description);
        project.setBudget(budget);
        project.setDeadline(deadline);
        project.setFundingGroup(fundingGroup);
        return project;
    }

    CommunityProject createCommunityProject(String title, FundingGroup fundingGroup) {
        CommunityProject project = new CommunityProject();
        project.setTitle(title);
        project.setFundingGroup(fundingGroup);
        return project;
    }

    FundingGroup createFundingGroup(String name, FundingGroupType type) {
        FundingGroup fundingGroup = new FundingGroup();
        fundingGroup.setName(name);
        fundingGroup.setType(type);
        return fundingGroup;
    }

    @org.junit.Test
    public void tc1_RetrieveFundingGroupTypeForSingleCommunityProject() {
        // Test logic for retrieving funding group type for a single community project
        Department department = createDepartment("D001", "department1@example.com");

        FundingGroup fundingGroup = createFundingGroup("Government Group", FundingGroupType.GOVERNMENT);
        CommunityProject communityProject = createCommunityProject("Community Clean-Up",
                "A project to clean the local park", 5000, new Date(2025 - 1900, 11, 31), fundingGroup); // December 31, 2025

        department.addProject(communityProject);

        // Actual assertion
        assertEquals("Expected funding group type size mismatch", 1,
                department.getFundingGroupTypeCommunityProjects().size());
        assertEquals("Funding group type mismatch", FundingGroupType.GOVERNMENT,
                department.getFundingGroupTypeCommunityProjects().get(0));
    }

    @org.junit.Test
    public void tc2_RetrieveFundingGroupTypeForMultipleCommunityProjects() {
        // Test logic for retrieving funding group types for multiple community projects
        Department department = createDepartment("D002", "department2@example.com");

        FundingGroup fundingGroup1 = createFundingGroup("Private Group", FundingGroupType.PRIVATE);
        CommunityProject project1 = createCommunityProject("Food Drive", "Collect food for the needy", 3000,
                new Date(2025 - 1900, 10, 15), fundingGroup1); // November 15, 2025

        FundingGroup fundingGroup2 = createFundingGroup("Mixed Group", FundingGroupType.MIXED);
        CommunityProject project2 = createCommunityProject("Health Awareness Campaign", "Promote health screenings", 2000,
                new Date(2025 - 1900, 9, 20), fundingGroup2); // October 20, 2025

        department.addProject(project1);
        department.addProject(project2);

        // Actual assertion
        assertEquals("Expected funding group type size mismatch", 2,
                department.getFundingGroupTypeCommunityProjects().size());
        assertEquals("Funding group type at index 0 mismatch", FundingGroupType.PRIVATE,
                department.getFundingGroupTypeCommunityProjects().get(0));
        assertEquals("Funding group type at index 1 mismatch", FundingGroupType.MIXED,
                department.getFundingGroupTypeCommunityProjects().get(1));
    }

    @org.junit.Test
    public void tc3_NoCommunityProjectsInDepartment() {
        // Test logic for retrieving funding group type when no projects exist
        Department department = createDepartment("D003", "department3@example.com");

        // No community projects are added

        // Actual assertion
        assertEquals("Expected funding group type size mismatch", 0,
                department.getFundingGroupTypeCommunityProjects().size());
    }

    @org.junit.Test
    public void tc4_RetrieveFundingGroupTypeWithMultipleDepartments() {
        // Test logic for retrieving funding group types with multiple departments
        Department department1 = createDepartment("D004", "department4@example.com");

        FundingGroup fundingGroup = createFundingGroup("Mixed Group", FundingGroupType.MIXED);
        CommunityProject communityProject = createCommunityProject("Neighborhood Beautification",
                "Enhancing community space", 7500, new Date(2025 - 1900, 11, 1), fundingGroup); // December 1, 2025
        department1.addProject(communityProject);

        Department department2 = createDepartment("D005", "department5@example.com");

        FundingGroup fundingGroup2 = createFundingGroup("Government Group", FundingGroupType.GOVERNMENT);
        CommunityProject communityProject2 = createCommunityProject("Local Library Improvement", fundingGroup2);
        department2.addProject(communityProject2);

        // Actual assertion for department D004
        assertEquals("Expected funding group type size mismatch for D004", 1,
                department1.getFundingGroupTypeCommunityProjects().size());
        assertEquals("Funding group type mismatch in D004", FundingGroupType.MIXED,
                department1.getFundingGroupTypeCommunityProjects().get(0));

        // Actual assertion for department D005
        assertEquals("Expected funding group type size mismatch for D005", 1,
                department2.getFundingGroupTypeCommunityProjects().size());
        assertEquals("Funding group type mismatch in D005", FundingGroupType.GOVERNMENT,
                department2.getFundingGroupTypeCommunityProjects().get(0));
    }

    @org.junit.Test
    public void tc5_FundingGroupTypeWithInvalidDepartmentID() {
        // Test logic for retrieving funding group types in an invalid department
        Department department = createDepartment("D999", null);

        // No community projects are added

        // Actual assertion
        assertEquals("Expected funding group type size mismatch", 0,
                department.getFundingGroupTypeCommunityProjects().size());
    }
}

/*
 * compile_result:
 * 注: D:\eclipse-workshop\testCase\src\test\java\FundingGroupTypeTest.
 * java使用或覆盖了已过时的 API。
 * 注: 有关详细信息, 请使用 -Xlint:deprecation 重新编译。
 * 
 * 
 * 
 * run_result:
 * JUnit version 4.13.2
 * .....
 * Time: 0.035
 * 
 * OK (5 tests)
 * 
 * 
 */