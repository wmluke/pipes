package app.models;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User implements Model {

    private int id;
    private String email;
    private String firstName;
    private String lastName;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    @Email
    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    @NotBlank
    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    @NotBlank
    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}
