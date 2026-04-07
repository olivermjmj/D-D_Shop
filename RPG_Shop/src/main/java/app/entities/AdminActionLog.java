package app.entities;

import app.entities.enums.AdminActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "admin_action_logs")

public class AdminActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminActionType action;


    @Column(nullable = false)
    private String targetType;

    private Integer targetId;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;
}
