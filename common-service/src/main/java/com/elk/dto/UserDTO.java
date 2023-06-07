package com.elk.dto;

import com.elk.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private int age;
    private Long departmentId;
    private DepartmentDTO department;

    public static UserDTO buildDTO(User user) {
        UserDTO userDTO = null;
        if (user != null) {
            userDTO = UserDTO.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .gender(user.getGender())
                    .age(user.getAge())
                    .departmentId(user.getDepartmentId())
                    .build();
        }
        return userDTO;
    }
}