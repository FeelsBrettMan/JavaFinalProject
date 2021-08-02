package com.cognixia.jump.javaFinalProject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class EmployeeManagementSystem implements Serializable {
    @Serial
    private static final long serialVersionUID = -8698467514680864504L;

    //nested employee and exception class
    private class Employee implements Serializable {
        @Serial
        private static final long serialVersionUID = -2164774094799010440L;

        private int id;
        private String name;
        private String dept;
        private int salary;


        public Employee(String name, String dept, int salary, int id) {
            super();
            this.id = id;
            this.name = name;
            this.dept = dept;
            this.salary = salary;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDept() {
            return dept;
        }

        public void setDept(String dept) {
            this.dept = dept;
        }

        public int getSalary() {
            return salary;
        }

        public void setSalary(int salary) {
            this.salary = salary;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", dept='" + dept + '\'' +
                    ", salary=" + salary +
                    '}';
        }
    }
    private class DuplicateEmployeeID extends Exception{

        @Serial
        private static final long serialVersionUID = 5607382074092700703L;
        public DuplicateEmployeeID(int id){
            super("Employee with id: " + id + " already exist!");
        }
    }

    private enum fields { NAME, DEPT, SALARY}

    private List<Employee> employeeList = new ArrayList<>();
    private final File file = new File("resources/employees.txt");

    public static void main(String[] args) {
        EmployeeManagementSystem ems = new EmployeeManagementSystem();
        ems.readIn();

        //adds the 3 starting employees, prints stack trace of a custom exception when they already exist
        ems.addEmployee("Foo Bar", "IT", 100000, 1);
        ems.addEmployee("John Doe", "Finance", 50000, 2);
        ems.addEmployee("Jane Doe", "HR", 75000, 3);



        System.out.println("ems.employeeList = " + ems.employeeList);
        //removes an employee that doesn't exist
        ems.removeEmployee(4);

        System.out.println("all departments = " + ems.listDept());

        ems.addEmployee("Foo Bar #2", "IT", 120000, 4);

        System.out.println("all employees in IT = " + ems.employeesInDept("IT"));


        //removes an employee that does exist
        ems.removeEmployee(4);

        //changes name and salary of an employee
        ems.updateEmployee(2, fields.NAME, "Jonathon Doe");
        ems.updateEmployee(2, fields.SALARY, "100");

        System.out.println("ems.employeeList = " + ems.employeeList);

        ems.updateEmployee(2, fields.NAME, "John Doe");
        ems.updateEmployee(2, fields.SALARY, "50000");

        ems.writeOut();
    }

    //adds an employee or throws the custom exception DuplicateEmployeeID
    private void addEmployeeHelper(String name, String dept, int salary, int id) throws DuplicateEmployeeID{
        if (employeeList.stream().anyMatch(employee -> employee.getId() ==id))
            throw new DuplicateEmployeeID(id);
        employeeList.add(new Employee(name,dept,salary, id));
    }

    //passes information to helper and catches the custom exception
    public void addEmployee(String name, String dept, int salary, int id){
        try {
            addEmployeeHelper(name, dept, salary, id);
            System.out.println("Employee " + name + " has been added!");
        }catch (DuplicateEmployeeID e){
            System.out.println(e.getMessage());;
        }
    }

    //removes an employee or prints an error if the employee does not exist
    public void removeEmployee(int id) {
        try {
            Employee toRemove = employeeList.stream().filter(employee -> employee.getId() == id).findFirst().get();
            employeeList.remove(toRemove);
            System.out.println("Employee ID:" + id + " was removed!");
        }catch (NoSuchElementException e) {
            System.out.println("No Employee with ID: " + id);
        }
    }

    public void updateEmployee(int id, fields field, String info){

        try {
            Employee toUpdate = employeeList.stream().filter(employee -> employee.getId() == id).findFirst().get();
            switch (field) {
                case NAME:
                    toUpdate.setName(info);
                    break;
                case DEPT:
                    toUpdate.setDept(info);
                    break;
                case SALARY:
                    toUpdate.setSalary(Integer.parseInt(info));
                    break;
                default:
                    System.out.println("Please enter a valid updatable field: name, dept, or salary");
            }
        }catch (NoSuchElementException e) {
            System.err.println("No Employee with ID: " + id);
        }


    }

    //list all the distinct departments at least one employee belongs to
    public List<String> listDept(){
        return employeeList.stream().map(Employee::getDept).distinct().collect(Collectors.toList());
    }

    //list all the employees of a specific department
    public List<Employee> employeesInDept(String dept){
        return employeeList.stream().filter(employee -> employee.getDept().equals(dept)).collect(Collectors.toList());
    }

    //reads in from file
    public void readIn()  {
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(file))) {
            Employee employee;
            while((employee = (Employee) reader.readObject()) !=null){ //watches for EOF marker
                employeeList.add(employee);
                System.out.println("employee added from file: " + employee);
            }
            System.out.println("File read complete!");

        } catch (FileNotFoundException e) {
            try {
                System.out.println("File not found! creating new!");
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //writes out to file
    public void writeOut(){
        try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(file))) {
            employeeList.forEach(employee -> {
                try {
                    writer.writeObject(employee);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("employee written to file: " + employee);
            });

            writer.writeObject(null); //used as an EOF marker
            System.out.println("File write complete");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
