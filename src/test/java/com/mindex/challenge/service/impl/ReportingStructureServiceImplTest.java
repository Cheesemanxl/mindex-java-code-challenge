package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureGetUrl;
    private String employeeCreateUrl;

    @Autowired
    private ReportingStructureService reportingStructureService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        reportingStructureGetUrl = "http://localhost:" + port + "/reporting-structure/{id}";
        employeeCreateUrl = "http://localhost:" + port + "/employee";
    }

    @Test
    public void testRead() {
        Employee testIC1 = new Employee();
        testIC1.setFirstName("John");
        testIC1.setLastName("Doe");
        testIC1.setDepartment("Engineering");
        testIC1.setPosition("IC");

        testIC1 = restTemplate.postForEntity(employeeCreateUrl, testIC1, Employee.class).getBody();

        Employee testIC2 = new Employee();
        testIC2.setFirstName("Jane");
        testIC2.setLastName("Doe");
        testIC2.setDepartment("Engineering");
        testIC2.setPosition("IC");

        testIC2 = restTemplate.postForEntity(employeeCreateUrl, testIC2, Employee.class).getBody();

        Employee testManager1 = new Employee();
        testManager1.setFirstName("John Manager");
        testManager1.setLastName("Doe");
        testManager1.setDepartment("Engineering");
        testManager1.setPosition("Manager");
        testManager1.setDirectReports(List.of(testIC1, testIC2));

        testManager1 = restTemplate.postForEntity(employeeCreateUrl, testManager1, Employee.class).getBody();

        Employee testIC3 = new Employee();
        testIC3.setFirstName("John");
        testIC3.setLastName("Doe");
        testIC3.setDepartment("Marketing");
        testIC3.setPosition("IC");

        testIC3 = restTemplate.postForEntity(employeeCreateUrl, testIC3, Employee.class).getBody();

        Employee testIC4 = new Employee();
        testIC4.setFirstName("Jane");
        testIC4.setLastName("Doe");
        testIC4.setDepartment("Marketing");
        testIC4.setPosition("IC");

        testIC4 = restTemplate.postForEntity(employeeCreateUrl, testIC4, Employee.class).getBody();

        Employee testManager2 = new Employee();
        testManager2.setFirstName("Jane Manager");
        testManager2.setLastName("Doe");
        testManager2.setDepartment("Marketing");
        testManager2.setPosition("Manager");
        testManager2.setDirectReports(List.of(testIC3, testIC4));

        testManager2 = restTemplate.postForEntity(employeeCreateUrl, testManager2, Employee.class).getBody();

        Employee testCEO = new Employee();
        testCEO.setFirstName("John Executive");
        testCEO.setLastName("Doe");
        testCEO.setDepartment("Executive");
        testCEO.setPosition("CEO");
        testCEO.setDirectReports(List.of(testManager1, testManager2));

        testCEO = restTemplate.postForEntity(employeeCreateUrl, testCEO, Employee.class).getBody();

        ReportingStructure reportingStructure = restTemplate.getForEntity(
                reportingStructureGetUrl,
                ReportingStructure.class,
                testCEO.getEmployeeId()).getBody();

        assertEquals(6, reportingStructure.getNumberOfReports());
    }
}
