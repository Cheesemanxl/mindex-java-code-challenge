package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ReportingStructure getByEmployeeId(String id) {
        LOG.debug("Getting reporting structure for employee [{}]", id);

        Employee employee = employeeService.read(id);

        return new ReportingStructure(employee,
                countDirectReports(employee.getDirectReports()));
    }


    private int countDirectReports(List<Employee> directReports) {
        if (directReports == null) {
            return 0;
        }

        int sumOfReports = directReports.size();

        for (Employee report : directReports) {
            report = employeeService.read(report.getEmployeeId());
            sumOfReports += countDirectReports(report.getDirectReports());
        }

        return sumOfReports;
    }
}