package edu.project.test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import edu.project.CommunityProject;
import edu.project.Company;
import edu.project.Department;
import edu.project.EducationProject;
import edu.project.FundingGroup;
import edu.project.FundingGroupType;
import edu.project.ProductionProject;
import edu.project.Project;
import edu.project.ProjectFactory;

public class CR1Test {
    private static final double EPSILON = 0.001;

    private ProjectFactory factory;
    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() {
        factory = ProjectFactory.eINSTANCE;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Test
    public void testSingleDepartmentBudgetCalculation() throws Exception {
        Company company = createCompany();
        Department department = createDepartment("D001", "department1@company.com");
        company.getDepartments().add(department);

        department.getProjects().add(createProductionProject(
                "Website Redevelopment",
                "Redesigning the company website",
                10000.0,
                "2025-12-31"));
        department.getProjects().add(createProductionProject(
                "Mobile App Development",
                "Developing a customer service app",
                15000.0,
                "2026-01-15"));

        assertEquals(25000.0, company.calculateTotalBudget(), EPSILON);
    }

    @Test
    public void testMultipleDepartmentsBudgetCalculation() {
        Company company = createCompany();
        Department department1 = createDepartment("D001", "department1@company.com");
        Department department2 = createDepartment("D002", "department2@company.com");
        company.getDepartments().add(department1);
        company.getDepartments().add(department2);

        department1.getProjects().add(createProductionProject("HR Software", "", 20000.0, null));
        department2.getProjects().add(createProductionProject("Sales Training Program", "", 30000.0, null));
        department2.getProjects().add(createProductionProject("Marketing Campaign", "", 25000.0, null));

        assertEquals(75000.0, company.calculateTotalBudget(), EPSILON);
    }

    @Test
    public void testBudgetCalculationWithZeroProjects() {
        Company company = createCompany();
        company.getDepartments().add(createDepartment("D003", "department3@company.com"));

        assertEquals(0.0, company.calculateTotalBudget(), EPSILON);
    }

    @Test
    public void testEducationProjectBudgetWithFundingGroup() {
        Company company = createCompany();
        Department department1 = createDepartment("D004", "department4@company.com");
        Department department2 = createDepartment("D005", "department5@company.com");
        company.getDepartments().add(department1);
        company.getDepartments().add(department2);

        department1.getProjects().add(createEducationProject(
                "Scholarship Program",
                "",
                50000.0,
                "2026-05-31",
                FundingGroupType.GOVERNMENT));
        department1.getProjects().add(createProductionProject("R&D Initiative", "", 70000.0, "2026-07-15"));
        department2.getProjects().add(createProductionProject("R&D5 Initiative", "", 70000.0, "2026-07-19"));

        assertEquals(190000.0, company.calculateTotalBudget(), EPSILON);
    }

    @Test
    public void testCommunityProjectBudgetWithMixedFundingGroup() {
        Company company = createCompany();
        Department department = createDepartment("D006", "department5@company.com");
        company.getDepartments().add(department);

        department.getProjects().add(createCommunityProject(
                "Community Health Awareness",
                "",
                40000.0,
                "2027-02-28",
                FundingGroupType.MIXED));
        department.getProjects().add(createProductionProject(
                "Environmental Clean-up Initiative",
                "",
                60000.0,
                "2027-03-30"));

        assertEquals(100000.0, company.calculateTotalBudget(), EPSILON);
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

    private ProductionProject createProductionProject(String title, String description, double budget, String deadline) {
        ProductionProject project = factory.createProductionProject();
        setProjectFields(project, title, description, budget, deadline);
        return project;
    }

    private EducationProject createEducationProject(
            String title,
            String description,
            double budget,
            String deadline,
            FundingGroupType fundingGroupType) {
        EducationProject project = factory.createEducationProject();
        setProjectFields(project, title, description, budget, deadline);
        project.setFundingGroup(createFundingGroup(fundingGroupType));
        return project;
    }

    private CommunityProject createCommunityProject(
            String title,
            String description,
            double budget,
            String deadline,
            FundingGroupType fundingGroupType) {
        CommunityProject project = factory.createCommunityProject();
        setProjectFields(project, title, description, budget, deadline);
        project.setFundingGroup(createFundingGroup(fundingGroupType));
        return project;
    }

    private FundingGroup createFundingGroup(FundingGroupType type) {
        FundingGroup fundingGroup = factory.createFundingGroup();
        fundingGroup.setType(type);
        return fundingGroup;
    }

    private void setProjectFields(Project project, String title, String description, double budget, String deadline) {
        project.setTitle(title);
        project.setDescription(description);
        project.setBudget(budget);
        project.setDeadline(parseDate(deadline));
    }

    private Date parseDate(String value) {
        if (value == null) {
            return null;
        }
        try {
            return dateFormat.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
