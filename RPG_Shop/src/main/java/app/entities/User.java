package app.entities;

import app.entities.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = true)
    private String username;

    @Column(nullable = true)
    private String passwordHash;

    @Column(nullable = true)
    private BigDecimal wallet;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    //User
    public User(String email, String name, String username, String passwordHash, Role role) {

        this.email = email;
        this.name = name;
        this.username = username;
        this.passwordHash = passwordHash;
        wallet = BigDecimal.ZERO;
        this.role = role;
    }

    //Admin or Guest
    public User(String email, String name, Role role) {

        this.email = email;
        this.name = name;
        wallet = null;
        this.role = role;
    }


}