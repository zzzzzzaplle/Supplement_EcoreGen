public class Test {
    public static void main(String[] args) {
        Company company = new Company();
        
        Department d1 = new Department();
        d1.setID("D1");
        d1.setName("Dep1");
        
        Department d2 = new Department();
        d2.setID("D2");
        d2.setName("Dep2");
        
        company.addDepartment(d1);
        company.addDepartment(d2);
        
        // Ensure constraints work
        System.out.println("Departments: " + company.getDepartments().size());
        
        // Add project and employee testing
        ProductionProject pp = new ProductionProject();
        pp.setBudget(100.0);
        Employee e1 = new Employee();
        e1.setID("E1");
        pp.addWorkingEmployee(e1);
        d1.addProject(pp);
        
        System.out.println("Total Budget: " + company.calculateTotalBudget());
        System.out.println("Production Employees: " + company.countEmployeesInProductionProjects());
        
        System.out.println("Compilation Successful");
    }
}
