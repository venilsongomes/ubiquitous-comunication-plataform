package br.com.yourcompany.platformcore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String username;
    private String displayName;
    private String password;

    public String getUsername() {
        return username;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getPassword() {
        return password;
    }
    


}
