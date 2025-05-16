// User.java
import java.io.Serializable;

public class User implements Serializable {
    private String name, phone, email, username, password, role;

    
    public User(String name, String phone, String email, String username, String password, String role){
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    public String getName() {
    return this.name;
}

public String getPhone() {
    return this.phone;
}

public String getEmail() {
    return this.email;
}

public String getUsername() {
    return this.username;
}

public String getPassword() {
    return this.password;
}

public String getRole() {
    return this.role;
}

}
