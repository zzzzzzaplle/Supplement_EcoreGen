import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

 class Main {

    enum EmployeeType {
        TEMPORARY,
        PERMANENT
    }

    enum FundingGroupType {
        PRIVATE,
        GOVERNMENT,
        MIXED
    }

    static class FundingGroup {
        private String name;
        private FundingGroupType type;

        public FundingGroup() {
            this.name = "";
            this.type = FundingGroupType.PRIVATE;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public FundingGroupType getType() {
            return type;
        }

        public void setType(FundingGroupType type) {
            this.type = type;
        }
    }

    static abstract class Project {
        private String title;
        private String description;
        private double budget;
        private Date deadline;
        private List<Employee> workingEmployees;

        public Project() {
            this.title = "";
            this.description = "";
            this.budget = 0.0;
            this.deadline = new Date();
            this.workingEmployees = new ArrayList<>();
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getBudget() {
            return budget;
        }

        public void setBudget(double budget) {
            this.budget = budget;
        }

        public Date getDeadline() {
            return deadline;
        }

        public void setDeadline(Date deadline) {
            this.deadline = deadline;
        }

        public List<Employee> getWorkingEmployees() {
            return workingEmployees;
        }

        public void addWorkingEmployee(Employee employee) {
            this.workingEmployees.add(employee);
        }
    }

    static class ProductionProject extends Project {
        private String siteCode;

        public ProductionProject() {
            super();
            this.siteCode = "";
        }

        public String getSiteCode() {
            return siteCode;
        }

        public void setSiteCode(String siteCode) {
            this.siteCode = siteCode;
        }
    }

    static class ResearchProject extends Project {
        public ResearchProject() {
            super();
        }
    }

    static class EducationProject extends Project {
        private FundingGroup fundingGroup;

        public EducationProject() {
            super();
            this.fundingGroup = new FundingGroup();
        }

        public FundingGroup getFundingGroup() {
            return fundingGroup;
        }

        public void setFundingGroup(FundingGroup group) {
            this.fundingGroup = group;
        }
    }

    static class CommunityProject extends Project {
        private FundingGroup fundingGroup;

        public CommunityProject() {
            super();
            this.fundingGroup = new FundingGroup();
        }

        public FundingGroup getFundingGroup() {
            return fundingGroup;
        }

        public void setFundingGroup(FundingGroup group) {
            this.fundingGroup = group;
        }
    }

    static class Employee {
        private EmployeeType type;
        private String name;
        private String email;
        private String ID;
        private String number;

        public Employee() {
            this.type = EmployeeType.TEMPORARY;
            this.name = "";
            this.email = "";
            this.ID = "";
            this.number = "";
        }

        public EmployeeType getType() {
            return type;
        }

        public void setType(EmployeeType type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getID() {
            return ID;
        }

        public void setID(String id) {
            this.ID = id;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }

    static class Department {
        private String ID;
        private String email;
        private List<Employee> employees;
        private List<Project> projects;

        public Department() {
            this.ID = "";
            this.email = "";
            this.employees = new ArrayList<>();
            this.projects = new ArrayList<>();
        }

        public String getID() {
            return ID;
        }

        public void setID(String id) {
            this.ID = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<Employee> getEmployees() {
            return employees;
        }

        public void addEmployee(Employee employee) {
            this.employees.add(employee);
        }

        public List<Project> getProjects() {
            return projects;
        }

        public void addProject(Project project) {
            this.projects.add(project);
        }

        public double calculateAverageBudget() {
            if (projects == null || projects.isEmpty()) {
                return 0.0;
            }
            double sum = 0.0;
            for (Project p : projects) {
                sum += p.getBudget();
            }
            return sum / projects.size();
        }

        public List<FundingGroupType> getFundingGroupTypeCommunityProjects() {
            List<FundingGroupType> types = new ArrayList<>();
            if (projects == null) {
                return types;
            }
            for (Project p : projects) {
                if (p instanceof CommunityProject) {
                    CommunityProject cp = (CommunityProject) p;
                    if (cp.getFundingGroup() != null) {
                        types.add(cp.getFundingGroup().getType());
                    }
                }
            }
            return types;
        }
    }

    static class Company {
        private List<Department> departments;

        public Company() {
            this.departments = new ArrayList<>();
        }

        public List<Department> getDepartments() {
            return departments;
        }

        public double calculateTotalBudget() {
            double total = 0.0;
            if (departments == null) {
                return total;
            }
            for (Department dept : departments) {
                if (dept.getProjects() != null) {
                    for (Project p : dept.getProjects()) {
                        total += p.getBudget();
                    }
                }
            }
            return total;
        }

        public int countEmployeesInProductionProjects() {
            Set<Employee> uniqueEmployees = new HashSet<>();
            if (departments == null) {
                return 0;
            }
            for (Department dept : departments) {
                if (dept.getProjects() != null) {
                    for (Project p : dept.getProjects()) {
                        if (p instanceof ProductionProject) {
                            if (p.getWorkingEmployees() != null) {
                                for (Employee emp : p.getWorkingEmployees()) {
                                    uniqueEmployees.add(emp);
                                }
                            }
                        }
                    }
                }
            }
            return uniqueEmployees.size();
        }

        public void addDepartment(Department department) {
            if (departments.size() < 8) {
                departments.add(department);
            }
        }

        public void removeDepartment(String name) {
            if (departments.size() > 2) {
                departments.removeIf(d -> d.getID().equals(name));
            }
        }
    }
}