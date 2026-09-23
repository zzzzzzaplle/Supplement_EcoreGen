// ==version1==
```
class Company {
    - Department[2..8] departments

    //key operations
    + double calculateTotalBudget()
    + int countEmployeesInProductionProjects()
    + void addDepartment(Department department)
    + void removeDepartment(String name)

    //getter，setter
    + List<Department> getDepartments()
}

class Department {
    - String ID
    - String email
    - List<Employee> employees
    - List<Project> projects
    
    //key operations
    + double calculateAverageBudget()
    + List<FundingGroupType> getFundingGroupTypeCommunityProjects()

    //getter，setter
    + String getID()
    + void setID(String id)
    + String getEmail()
    + void setEmail(String email)
    + List<Employee> getEmployees()
    + void addEmployee(Employee employee)
    + List<Project> getProjects()
    + void addProject(Project project)
}

enum EmployeeType {
    TEMPORARY
    PERMANENT
}


class Employee {
    - EmployeeType type
    - String name
    - String email
    - String ID
    - String number

    //getter，setter
    + EmployeeType getType()
    + void setType(EmployeeType type)
    + String getName()
    + void setName(String name)
    + String getEmail()
    + void setEmail(String email)
    + String getID()
    + void setID(String id)
    + String getNumber()
    + void setNumber(String number)
}

abstract class Project {
    - String title
    - String description
    - double budget
    - Date deadline
    - List<Employee> workingEmployees

    //getter，setter
    + String getTitle()
    + void setTitle(String title)
    + String getDescription()
    + void setDescription(String description)
    + double getBudget()
    + void setBudget(double budget)
    + Date getDeadline()
    + void setDeadline(Date deadline)
    + List<Employee> getWorkingEmployees()
    + void addWorkingEmployee(Employee employee)
}

class ProductionProject extends Project {
    - String siteCode

    //getter，setter
    + String getSiteCode()
    + void setSiteCode(String siteCode)
}

class ResearchProject extends Project {}

class EducationProject extends Project {
    - FundingGroup fundingGroup
    
    //getter, setter
    + FundingGroup getFundingGroup()
    + void setFundingGroup(FundingGroup group)
}

class CommunityProject extends Project {
    - FundingGroup fundingGroup

    //getter, setter
    + FundingGroup getFundingGroup()
    + void setFundingGroup(FundingGroup group)
}

enum FundingGroupType {
    PRIVATE
    GOVERNMENT
    MIXED
}

class FundingGroup {
    - String name
    - FundingGroupType type

    //getter，setter
    + String getName()
    + void setName(String name)
    + FundingGroupType getType()
    + void setType(FundingGroupType type)
}

Company *-- "2..8" Department : departments
Department *-- "*" Employee : employees
Department *-- "*" Project : projects
Project --> "*" Employee : workingEmployees

EducationProject "*" -- "1" FundingGroup : fundingGroup
CommunityProject "*" -- "1" FundingGroup : fundingGroup

```
// ==end==
