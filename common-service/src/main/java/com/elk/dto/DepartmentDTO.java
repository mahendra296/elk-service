package com.elk.dto;

import com.elk.model.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDTO {

    private Long id;
    private String departmentName;

    public static DepartmentDTO buildDTO(Department department) {
        DepartmentDTO departmentDTO = null;
        if (department != null) {
            departmentDTO = DepartmentDTO.builder()
                    .id(department.getId())
                    .departmentName(department.getDepartmentName())
                    .build();
        }
        return departmentDTO;
    }
}