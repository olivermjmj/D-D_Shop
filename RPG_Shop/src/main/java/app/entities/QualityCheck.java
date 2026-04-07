package app.entities;

import app.entities.enums.QualityStatus;
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
@Table(name = "quality_checks")

public class QualityCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Enumerated(EnumType.STRING)
    private QualityStatus status;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant checkedAt;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;
}
