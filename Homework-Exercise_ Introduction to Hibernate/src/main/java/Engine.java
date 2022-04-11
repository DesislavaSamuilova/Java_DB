import com.sun.xml.bind.v2.TODO;
import entities.Address;
import entities.Employee;
import entities.Project;
import entities.Town;
import javassist.bytecode.SourceFileAttribute;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Engine implements Runnable {
    private final EntityManager entityManager;
    private BufferedReader bufferedReader;

    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        System.out.println("Select exercise number:");

        try {
            int exerciseNumber = Integer.parseInt(bufferedReader.readLine());
            switch (exerciseNumber) {
                case 2:
                    changeCasingExTwo();
                case 3:
                    containsEmployeeExThree();
                case 4:
                    employeesWithSalaryOverNumExFour();
                case 5:
                    employeesFromDepartmentExFive();
                case 6:
                    addingNewAddressAndUpdateEmployeeExSix();
                case 7:
                    addressesWithEmployeeCountExSeven();
                case 8:
                    getEmployeeWithProjectExEight();
                case 9:
                    findLatest10ProjectsExNine();
                case 10:
                    increaseSalariesExTen();
                case 11:
                    findEmployeesByFirstNameExEleven();
                case 12:
                    employeesMaximumSalariesExTwelve();
                case 13:
                    removeTownsExThirteen();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    private void findLatest10ProjectsExNine() {
        Query getEmployee = entityManager.createQuery("SELECT p FROM Project p " +
                "Order by p.name asc, p.startDate desc ", Project.class)
                .setMaxResults(10);
        List<Project> resultList = getEmployee.getResultList();

        for (Project project : resultList) {
            System.out.printf("Project name: %s%n", project.getName());
            System.out.printf("Project Description: %s%n", project.getDescription());
            System.out.printf("Project Start Date:%s%n", project.getStartDate());
            System.out.printf("Project End Date: %s%n", project.getEndDate());
        }

    }

    private void findEmployeesByFirstNameExEleven() throws IOException {
        System.out.println("Enter pattern:");
        String pattern = bufferedReader.readLine();

        entityManager.createQuery("SELECT e FROM Employee e " +
                "WHERE first_name LIKE 'SA%'", Employee.class)
                .getResultList().forEach(employee -> System.out.printf("%s %s - %s - ($%.2f)%n",
                employee.getFirstName(),
                employee.getLastName(),
                employee.getJobTitle(),
                employee.getSalary()));

    }

    private void removeTownsExThirteen() throws IOException {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        System.out.println("Enter town name:");
        String townName = bufferedReader.readLine();
        Optional<Town> optional = entityManager.createQuery("FROM Town t WHERE t.name = :town_param", Town.class).setParameter("town_param", townName).getResultStream().findFirst();
        if (optional.isPresent()) {
            Integer townId = optional.get().getId();
            Stream<Integer> idsStream = entityManager.createQuery(" SELECT e.id FROM Employee AS e WHERE e.address.town.id = :town_id", Integer.class)
                    .setParameter("town_id", townId)
                    .getResultStream();

            String employeeIds = idsStream.map(String::valueOf).collect(Collectors.joining(", "));

            String sql = String.format("Update Employee AS e SET e.address.id = null WHERE e.id IN (%s)", employeeIds);

            entityManager.createQuery(sql)
                    .executeUpdate();

            int affectedAddresses = entityManager.createQuery("DELETE FROM Address AS a WHERE a.town.id = :town_id")
                    .setParameter("town_id", townId)
                    .executeUpdate();

            entityManager.createQuery("DELETE FROM Town AS t WHERE t.name = :town_name")
                    .setParameter("town_name", townName)
                    .executeUpdate();

            System.out.printf("%d address in %s deleted", affectedAddresses, townName);
        } else {
            System.out.printf("Town %s doesn't exist in the database", townName);
        }

        transaction.commit();
        entityManager.close();
    }

    @SuppressWarnings("unchecked")
    private void employeesMaximumSalariesExTwelve() {
        List<Object[]> rows = entityManager
                .createNativeQuery("SELECT d.name, MAX(e.salary) AS `m_salary` FROM department d " +
                        "JOIN employees e on d.department_id = e.department_id " +
                        "GROUP BY d.name " +
                        "HAVING m_salary NOT BETWEEN 30000 AND 70000")
                .getResultList();
    }

    private void increaseSalariesExTen() {
        List<String> employeeDepartments = List.of("Engineering", "Tool Design",
                "Marketing", "Information Services");

        entityManager.getTransaction().begin();

        entityManager.createQuery("UPDATE Employee e " +
                "set e.salary = e.salary * 1.12 " +
                "where e.department.id in :ids")
                .setParameter("ids", Set.of(1,2,4,11))
                .executeUpdate();


        entityManager.createQuery("select e Employee e" +
                "where e.department.name in (:department_names)", Employee.class)
                .setParameter("department_names", employeeDepartments)
                .getResultList()
                .forEach(e -> {
                    System.out.printf(("%s %s ($%.2f)%n"),
                            e.getFirstName(),
                            e.getLastName(),
                            e.getSalary());
                });

        entityManager.getTransaction().commit();
        ;
    }


    private void getEmployeeWithProjectExEight() throws IOException {
        System.out.println("Enter valid employee id:");
        int id = Integer.parseInt(bufferedReader.readLine());

        Employee employee = entityManager.find(Employee.class, id);
        System.out.printf("%s %s - %s%n",
                employee.getFirstName(),
                employee.getLastName(),
                employee.getJobTitle());
        employee.getProjects()
                .stream().sorted(Comparator.comparing(Project::getName))
                .forEach(project -> {
                    System.out.printf("\t%s%n", project.getName());
                });

    }

    private void addressesWithEmployeeCountExSeven() {
        List<Address> addresses = entityManager
                .createQuery("SELECT a FROM Address a " +
                        "ORDER BY a.employees.size Desc", Address.class)
                .setMaxResults(10)
                .getResultList();

        addresses.forEach(address -> System.out.printf("%s , %s - %d employees%n",
                address.getText(),
                address.getTown() == null
                        ? "Unknown" : address.getTown().getId(),
                address.getEmployees().size()));
    }

    private void addingNewAddressAndUpdateEmployeeExSix() throws IOException {
        System.out.println("Enter employee last name:");
        String lastName = bufferedReader.readLine();

        Employee employee = entityManager
                .createQuery("SELECT e FROM Employee e " +
                        "WHERE e.lastName = :l_name", Employee.class)
                .setParameter("l_name", lastName)
                .getSingleResult();
        Address address = createAddress("Vitoshka 15");
        entityManager.getTransaction().begin();
        employee.setAddress(address);
        entityManager.getTransaction().commit();
    }

    private Address createAddress(String addressText) {
        Address address = new Address();
        address.setText(addressText);
        entityManager.getTransaction().begin();
        entityManager.persist(address);
        entityManager.getTransaction().commit();
        return address;
    }

    private void employeesFromDepartmentExFive() {
        entityManager
                .createQuery("SELECT e FROM Employee e " +
                        "WHERE e.department.name = :d_name " +
                        "ORDER BY e.salary, e.id", Employee.class)
                .setParameter("d_name", "Research and Development")
                .getResultList().forEach(employee ->
                System.out.printf("%s %s from %s - $%.2f%n",
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getDepartment().getName(),
                        employee.getSalary()));
    }

    private void employeesWithSalaryOverNumExFour() {
        entityManager.createQuery("SELECT e FROM Employee e " +
                "WHERE e.salary > :min_salary", Employee.class)
                .setParameter("min_salary", BigDecimal.valueOf(50000L))
                .getResultStream().map(Employee::getFirstName)
                .forEach(System.out::println);
    }

    private void containsEmployeeExThree() throws IOException {
        System.out.println("Enter employee full name:");
        String[] fullName = bufferedReader.readLine().split("\\s+");
        String firstName = fullName[0];
        String lastName = fullName[1];
        Long singleResult = entityManager.createQuery
                ("SELECT count(e) FROM Employee e " +
                        "WHERE e.firstName = :f_name " +
                        "AND e.lastName =:l_name", Long.class)
                .setParameter("f_name", firstName)
                .setParameter("l_name", lastName).getSingleResult();

        System.out.println(singleResult == 0
                ? "No" : "Yes");

    }

    private void changeCasingExTwo() {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("UPDATE Town t " +
                "SET t.name = upper(t.name) " +
                "WHERE length(t.name) >=5 ");
        System.out.println(query.executeUpdate());
        entityManager.getTransaction().commit();
    }
}
