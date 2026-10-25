package ai.fitnova.domain;
import jakarta.persistence.*; import lombok.*; import java.time.Instant;
@Entity @Table(name="meals") @Getter @Setter @NoArgsConstructor
public class Meal { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false) @JoinColumn(name="user_id") private User user; private String name,goal; @Column(columnDefinition="text") private String ingredients; @Column(columnDefinition="text") private String instructions; private Integer calories; @Column(name="protein_g") private Integer proteinG; @Column(name="prep_minutes") private Integer prepMinutes; @Column(name="created_at") private Instant createdAt=Instant.now(); }
