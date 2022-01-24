package lv.redsails.authservice.dto;

import lombok.Data;

@Data
public class CreatUserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String encodedPassword;
}
