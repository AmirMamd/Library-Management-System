package com.example.library.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "username"})})

public class Patron {

    public Patron(String fullName, String email, String username, String password, String phoneNumber, String country, String address) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.address = address;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "FullName is Required")
    private String fullName;

    @NotBlank(message = "Address is Required")
    private String address;

    @Column(unique = true)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @NotBlank(message = "Email is Required")
    private String email;

    @Column(unique = true)
    @NotBlank(message = "Username is Required")
    private String username;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Weak Password")
    @NotBlank(message = "Password is Required")
    private String password;

    @NotBlank(message = "PhoneNumber is Required")
    @Pattern(regexp = "^\\+[0-9]+$", message = "Phone Number is not valid")
    private String phoneNumber;

    @NotBlank(message = "Country is Required")
    private String country;

    private boolean admin;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Roles> roles;



}
