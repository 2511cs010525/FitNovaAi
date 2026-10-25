package ai.fitnova.domain;
import jakarta.persistence.*; import lombok.*; import java.time.Instant;
@Entity @Table(name="workouts") @Getter @Setter @NoArgsConstructor
public class Workout { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false) @JoinColumn(name="user_id") private User user; private String title; @Column(name="duration_minutes") private Integer durationMinutes; private String intensity,location,goal,status; @Column(columnDefinition="text") private String exercises; private String feedback; @Column(name="energy_after") private Integer energyAfter; @Column(length=500) private String notes; @Column(name="started_at") private Instant startedAt; @Column(name="completed_at") private Instant completedAt; }
