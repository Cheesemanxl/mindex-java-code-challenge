package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationCreateUrl;
    private String compensationReadUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationCreateUrl = "http://localhost:" + port + "/compensation";
        compensationReadUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void testCreateRead() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        UUID employeeId = UUID.randomUUID();
        Date effectiveDate = sdf.parse("2024-11-05");
        BigDecimal salary = BigDecimal.valueOf(100_000.50);

        Compensation compensation = new Compensation(employeeId.toString(), salary, effectiveDate);

        Compensation createdCompensation = restTemplate.postForEntity(
                compensationCreateUrl,
                compensation,
                Compensation.class).getBody();

        assertNotNull(createdCompensation);
        assertCompensationEquivalence(compensation, createdCompensation);

        Compensation readCompensation = restTemplate.getForEntity(
                compensationReadUrl,
                Compensation.class,
                createdCompensation.getEmployeeId()).getBody();

        assertNotNull(readCompensation);
        assertCompensationEquivalence(createdCompensation, readCompensation);
    }

    @Test
    public void testReadWithNonExistingEmployeeId() {
        String badID = "nonExistentEmployeeId";
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> compensationService.read(badID))
                .withMessage("No compensation object found for employee with Id: " + badID);
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }
}
