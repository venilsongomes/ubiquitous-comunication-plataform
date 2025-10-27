package br.com.yourcompany.platform_core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String username;
    private String displayName;

    public String getUsername() {
        return username;
    }
    public String getDisplayName() {
        return displayName;
    }
    


}
