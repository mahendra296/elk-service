package com.elk.service;

import com.elk.dto.DepartmentDTO;

import java.util.List;

public interface DepartmentService {

    DepartmentDTO addDepartment(DepartmentDTO department);

    DepartmentDTO updateDepartment(Long departmentId, DepartmentDTO departmentDTO);

    List<DepartmentDTO> getDepartments();

    DepartmentDTO getDepartmentById(Long departmentId);
}
