package ai.fitnova.domain;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal;
@Entity @Table(name="profiles") @Getter @Setter @NoArgsConstructor
public class Profile { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @OneToOne(optional=false) @JoinColumn(name="user_id") private User user; private Integer age; @Column(name="height_cm") private BigDecimal heightCm; @Column(name="weight_kg") private BigDecimal weightKg; private String goal; private String experience; private String location; private String equipment; private String diet; private String allergies; private String limitations; @Column(name="onboarding_complete") private boolean onboardingComplete; }
