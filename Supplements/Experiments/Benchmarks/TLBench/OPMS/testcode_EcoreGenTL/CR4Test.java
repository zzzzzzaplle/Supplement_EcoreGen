package edu.project.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.junit.Before;
import org.junit.Test;

import edu.project.CommunityProject;
import edu.project.Company;
import edu.project.Department;
import edu.project.FundingGroup;
import edu.project.FundingGroupType;
import edu.project.ProjectFactory;
import edu.project.ResearchProject;

public class CR4Test {
    private ProjectFactory factory;

    @Before
    public void setUp() {
        factory = ProjectFactory.eINSTANCE;
    }

    @Test
    public void testRetrieveFundingGroupTypeForSingleCommunityProject() {
        Department department = createDepartment("D001", "department1@example.com");
        department.getProjects().add(createCommunityProject(
                "Community Clean-Up",
                "A project to clean the local park",
                5000.0,
                FundingGroupType.GOVERNMENT));

        EList<FundingGroupType> result = department.getFundingGroupTypeCommunityProjects();

        assertEquals(1, result.size());
        assertEquals(FundingGroupType.GOVERNMENT, result.get(0));
    }

    @Test
    public void testRetrieveFundingGroupTypeForMultipleCommunityProjects() {
        Department department = createDepartment("D002", "department2@example.com");
        department.getProjects().add(createCommunityProject(
                "Food Drive",
                "Collect food for the needy",
                3000.0,
                FundingGroupType.PRIVATE));
        department.getProjects().add(createCommunityProject(
                "Health Awareness Campaign",
                "Promote health screenings",
                2000.0,
                FundingGroupType.MIXED));

        EList<FundingGroupType> result = department.getFundingGroupTypeCommunityProjects();

        assertEquals(2, result.size());
        assertEquals(FundingGroupType.PRIVATE, result.get(0));
        assertEquals(FundingGroupType.MIXED, result.get(1));
    }

    @Test
    public void testNoCommunityProjectsInDepartment() {
        Department department = createDepartment("D003", "department3@example.com");

        assertTrue(department.getFundingGroupTypeCommunityProjects().isEmpty());
    }

    @Test
    public void testRetrieveFundingGroupTypeWithMultipleDepartments() {
        Department department4 = createDepartment("D004", "department4@example.com");
        Department department5 = createDepartment("D005", "department5@example.com");

        department4.getProjects().add(createCommunityProject(
                "Neighborhood Beautification",
                "Enhancing community space",
                7500.0,
                FundingGroupType.MIXED));
        department5.getProjects().add(createCommunityProject(
                "Local Library Improvement",
                "",
                0.0,
                FundingGroupType.GOVERNMENT));

        EList<FundingGroupType> result4 = department4.getFundingGroupTypeCommunityProjects();
        EList<FundingGroupType> result5 = department5.getFundingGroupTypeCommunityProjects();

        assertEquals(1, result4.size());
        assertEquals(FundingGroupType.MIXED, result4.get(0));
        assertEquals(1, result5.size());
        assertEquals(FundingGroupType.GOVERNMENT, result5.get(0));
    }

    @Test
    public void testFundingGroupTypeWithInvalidDepartmentId() {
        Company company = factory.createCompany();
        Department existing = createDepartment("D001", "department1@example.com");
        existing.getProjects().add(createCommunityProject("Existing", "", 100.0, FundingGroupType.PRIVATE));
        company.getDepartments().add(existing);

        List<FundingGroupType> result = findCommunityFundingTypes(company, "D999");

        assertTrue(result.isEmpty());
    }

    private Department createDepartment(String id, String email) {
        Department department = factory.createDepartment();
        department.setID(id);
        department.setEmail(email);
        return department;
    }

    private CommunityProject createCommunityProject(
            String title,
            String description,
            double budget,
            FundingGroupType fundingGroupType) {
        CommunityProject project = factory.createCommunityProject();
        project.setTitle(title);
        project.setDescription(description);
        project.setBudget(budget);
        project.setFundingGroup(createFundingGroup(fundingGroupType));
        return project;
    }

    private FundingGroup createFundingGroup(FundingGroupType type) {
        FundingGroup fundingGroup = factory.createFundingGroup();
        fundingGroup.setType(type);
        return fundingGroup;
    }

    private List<FundingGroupType> findCommunityFundingTypes(Company company, String departmentId) {
        for (Department department : company.getDepartments()) {
            if (departmentId.equals(department.getID())) {
                return department.getFundingGroupTypeCommunityProjects();
            }
        }
        return Collections.emptyList();
    }
}
