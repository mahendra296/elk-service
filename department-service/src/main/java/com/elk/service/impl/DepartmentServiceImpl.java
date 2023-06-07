package com.elk.service.impl;

import com.elk.dto.DepartmentDTO;
import com.elk.exceptions.InternalServerException;
import com.elk.exceptions.InvalidRequestException;
import com.elk.exceptions.ResourceNotFoundException;
import com.elk.model.Department;
import com.elk.repositories.DepartmentRepository;
import com.elk.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public DepartmentDTO addDepartment(DepartmentDTO departmentDTO) {
        log.info("Invoke addDepartment method.");
        try {
            Department department = Department.buildEntity(departmentDTO);
            departmentRepository.save(department);
            log.info("End addDepartment method.");
            return DepartmentDTO.buildDTO(department);
        } catch (Exception ex) {
            log.error("Exception while add department.", ex);
            throw new InternalServerException("Exception while add department.");
        }
    }

    @Override
    public DepartmentDTO updateDepartment(Long departmentId, DepartmentDTO departmentDTO) {
        log.info("Invoke updateDepartment method.");
        validateUpdateRequest(departmentDTO, departmentId);
        try {
            Department department = Department.buildEntity(departmentDTO);
            department.setId(departmentId);
            departmentRepository.save(department);
            log.info("End updateDepartment method.");
            return DepartmentDTO.buildDTO(department);
        } catch (Exception ex) {
            log.error("Exception while update department.", ex);
            throw new InternalServerException("Exception while update department.");
        }
    }

    @Override
    public List<DepartmentDTO> getDepartments() {
        log.info("Invoke getDepartments method.");
        try {
            List<Department> departmentList = departmentRepository.findAll();
            log.info("End getDepartments method.");
            return departmentList.stream().map(DepartmentDTO::buildDTO).toList();
        } catch (Exception ex) {
            log.error("Exception while get all department.", ex);
            throw new InternalServerException("Exception while get all department.");
        }
    }

    @Override
    public DepartmentDTO getDepartmentById(Long departmentId) {
        log.info("Invoke getDepartmentById method.");
        Department department = getDepartmentByDepartmentId(departmentId);
        if (department == null) {
            throw new ResourceNotFoundException("Department not found by departmentId : " + departmentId);
        }
        log.info("End getDepartmentById method.");
        return DepartmentDTO.buildDTO(department);
    }

    private void validateUpdateRequest(DepartmentDTO departmentDTO, Long departmentId) {
        if (departmentDTO == null) {
            throw new InvalidRequestException("Request is null.");
        }
        if (!Objects.equals(departmentDTO.getId(), departmentId)) {
            throw new InvalidRequestException("DepartmentId is not match with request.");
        }
        Department department = getDepartmentByDepartmentId(departmentId);
        if (department == null) {
            throw new ResourceNotFoundException("Department not found by departmentId : " + departmentId);
        }
    }

    private Department getDepartmentByDepartmentId(Long departmentId) {
        log.info("Invoke getDepartmentByDepartmentId method by departmentId : {}", departmentId);
        Optional<Department> departmentOptional = departmentRepository.findById(departmentId);
        return departmentOptional.orElse(null);
    }

    private void validateUpdateRequest() {
    }
}
