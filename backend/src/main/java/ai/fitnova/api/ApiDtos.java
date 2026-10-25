package ai.fitnova.api;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() {}

    public record UserView(Long id, String name, String email, boolean onboardingComplete) {}
    public record AuthResponse(String token, UserView user) {}
    public record SignupRequest(@NotBlank @Size(max=120) String fullName, @NotBlank @Email String email, @Size(min=8,max=100) String password) {}
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record ProfileRequest(@Min(13) @Max(100) Integer age, @DecimalMin("100") @DecimalMax("250") BigDecimal heightCm,
        @DecimalMin("30") @DecimalMax("350") BigDecimal weightKg, @NotBlank String goal, @NotBlank String experience,
        @NotBlank String location, List<String> equipment, @NotBlank String diet, String allergies, String limitations) {}
    public record ProfileView(Integer age, BigDecimal heightCm, BigDecimal weightKg, String goal, String experience,
        String location, List<String> equipment, String diet, String allergies, String limitations, boolean onboardingComplete) {}
    public record ReadinessRequest(@NotNull @DecimalMin("0") @DecimalMax("12") BigDecimal sleepHours,
        @NotNull @Min(1) @Max(5) Integer sleepQuality, @NotNull @Min(1) @Max(5) Integer energy,
        @NotNull @Min(1) @Max(5) Integer soreness, @NotNull @Min(1) @Max(5) Integer mood,
        @NotNull @Min(1) @Max(5) Integer stress, @NotNull @Min(10) @Max(180) Integer availableMinutes,
        @Size(max=300) String currentCondition, @Min(30) @Max(220) Integer restingHeartRate, @Min(1) @Max(300) Integer hrv) {}
    public record ReadinessView(Long id, LocalDate date, int score, String status, String recommendation, int availableMinutes) {}
    public record WorkoutRequest(@Min(10) @Max(180) int minutes, @NotBlank String location, @NotBlank String focus, List<String> equipment) {}
    public record WorkoutView(Long id, String title, int durationMinutes, String intensity, String location, String status,
        List<String> exercises, String feedback, Instant startedAt, Instant completedAt) {}
    public record WorkoutFeedback(@Pattern(regexp="TOO_EASY|EASY|JUST_RIGHT|HARD|TOO_HARD") String feedback,
        @Min(1) @Max(5) Integer energyAfter, @Size(max=500) String notes) {}
    public record MealRequest(@NotBlank @Size(max=180) String name, @NotBlank String goal, @NotEmpty List<String> ingredients,
        @NotEmpty List<String> instructions, @Min(0) Integer calories, @Min(0) Integer proteinG, @Min(1) Integer prepMinutes) {}
    public record MealView(Long id, String name, String goal, List<String> ingredients, List<String> instructions,
        Integer calories, Integer proteinG, Integer prepMinutes, Instant createdAt) {}
    public record FormSessionRequest(@NotBlank String exercise, @Min(0) int reps, @Min(0) @Max(100) int score,
        @Min(0) @Max(100) Integer bestScore, @Min(1) Integer durationSeconds, List<String> corrections) {}
    public record FormSessionView(Long id, String exercise, int reps, int score, List<String> corrections, Instant createdAt) {}
    public record Summary(long completedWorkouts, int trainingMinutes, int streak, Integer averageFormScore) {}
    public record DashboardView(UserView user, ReadinessView readiness, WorkoutView todayWorkout, Summary summary,
        List<WorkoutView> recentActivity) {}
    public record ProgressView(long completedWorkouts, int trainingMinutes, int streak, Integer averageReadiness,
        Integer averageFormScore, List<ReadinessView> readinessTrend, List<FormSessionView> formTrend) {}
    public record MessageResponse(String message) {}
    public record AiStatusView(boolean configured, String model) {}
    public record AskFitNovaRequest(@NotBlank @Size(max=1000) String message) {}
    public record AskFitNovaResponse(String answer) {}
    public record KitchenRequest(@NotEmpty @Size(max=20) List<@NotBlank @Size(max=80) String> ingredients,
        @NotBlank @Size(max=80) String goal) {}
}
