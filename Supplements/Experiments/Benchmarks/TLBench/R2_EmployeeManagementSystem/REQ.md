// ==version1==
The employee management system manages a named company that owns multiple departments and employs workers, salespeople, and managers.

Every employee stores the department label, name, birth date, and social insurance number.

Workers store weekly working hours and hourly rates.

Off-shift workers additionally store whether they have a weekend permit and whether they have an official holiday permit.

Shift workers additionally store a holiday premium and can only belong to the Delivery department.

Salespeople store a fixed salary, an amount of sales, and a commission percentage.

Managers store a fixed salary, a position title, and a list of direct subordinate employees.

Each department is identified by a department type of PRODUCTION, CONTROL, or DELIVERY, stores its employees, and is controlled by one manager.

The company can calculate the total salary of all employees, the total commission amount of all salespeople, and the total holiday premiums paid to all shift workers. Monetary results are rounded to two decimal places.

A department can calculate the average weekly working hours of its workers and returns 0 when the department has no workers. The average is rounded to two decimal places.

A manager can return the number of direct subordinate employees.
// ==end==
