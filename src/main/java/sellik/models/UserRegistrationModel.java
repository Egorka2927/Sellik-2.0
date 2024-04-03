package sellik.models;

import lombok.Data;

@Data
public class UserRegistrationModel {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
}
