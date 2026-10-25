package ai.fitnova.domain;
import jakarta.persistence.*; import lombok.*; import java.time.Instant;
@Entity @Table(name="form_sessions") @Getter @Setter @NoArgsConstructor
public class FormSession { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false) @JoinColumn(name="user_id") private User user; private String exercise; private Integer reps,score; @Column(name="best_score") private Integer bestScore; @Column(name="duration_seconds") private Integer durationSeconds; @Column(columnDefinition="text") private String feedback; @Column(name="created_at") private Instant createdAt=Instant.now(); }
