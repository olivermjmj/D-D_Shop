package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "items")

public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ItemCategory itemCategory;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToMany
    @JoinTable(
            name = "item_discount",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "discount_id")
    )
    private Set<Discount> discounts;

    @ManyToMany
    @JoinTable(
            name = "item_stamp",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "stamp_id")
    )
    private Set<Stamp> stamps;

    @OneToMany(mappedBy = "item")
    private List<QualityCheck> qualityChecks;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "external_source")
    private String externalSource;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;
}

