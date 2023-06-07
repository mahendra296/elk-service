package com.elk.model;

import com.elk.dto.DepartmentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@Table(name = "department")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String departmentName;

    public static Department buildEntity(DepartmentDTO departmentDTO) {
        Department department = null;
        if (departmentDTO != null) {
            department = Department.builder()
                    .departmentName(departmentDTO.getDepartmentName())
                    .build();
        }
        return department;
    }
}