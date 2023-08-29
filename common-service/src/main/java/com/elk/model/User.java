package com.elk.model;

import com.elk.dto.UserDTO;
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
@Table(name = "user")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private int age;
    private Long departmentId;

    public static User buildEntity(UserDTO userDTO) {
        User user = null;
        if (userDTO != null) {
            user = User.builder()
                    .firstName(userDTO.getFirstName())
                    .lastName(userDTO.getLastName())
                    .gender(userDTO.getGender())
                    .age(userDTO.getAge())
                    .departmentId(userDTO.getDepartmentId())
                    .build();
        }
        return user;

    }
}