package com.elk.controller;

import com.elk.dto.DepartmentDTO;
import com.elk.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.elk.utils.ResponseUtils.successResponseWithBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/hello")
    public ResponseEntity<Object> hello() {
        return successResponseWithBody("Hello");
    }

    @PostMapping("/department")
    public ResponseEntity<Object> addDepartment(@RequestBody DepartmentDTO departmentDTO) {
        departmentDTO = departmentService.addDepartment(departmentDTO);
        return successResponseWithBody(departmentDTO);
    }

    @PutMapping("/department/{departmentId}")
    public ResponseEntity<Object> updateDepartment(@PathVariable("departmentId") Long departmentId,
                                                   @RequestBody DepartmentDTO departmentDTO) {
        departmentDTO = departmentService.updateDepartment(departmentId, departmentDTO);
        return successResponseWithBody(departmentDTO);
    }

    @GetMapping("/department")
    public ResponseEntity<Object> getDepartments() {
        List<DepartmentDTO> departmentDTOList = departmentService.getDepartments();
        return successResponseWithBody(departmentDTOList);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<Object> getDepartments(@PathVariable("departmentId") Long departmentId) {
        DepartmentDTO departmentDTO = departmentService.getDepartmentById(departmentId);
        return successResponseWithBody(departmentDTO);
    }
}
