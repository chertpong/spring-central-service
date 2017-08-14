package com.kritacademy.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kritacademy.authorities.Authority;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @Email
    @Column(name = "email", unique = true)
    private String email;

    @Size(min = 1, max = 64)
    private String firstName;

    @Size(max = 64)
    private String lastName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "facebook_id", unique = true)
    private BigInteger facebookId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_authorites",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName="id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
    private Set<Authority> authorities = new HashSet<>();
}
