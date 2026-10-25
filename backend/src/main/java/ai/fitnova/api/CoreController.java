package ai.fitnova.api;
import ai.fitnova.api.ApiDtos.*; import ai.fitnova.domain.User; import ai.fitnova.repo.ProfileRepository; import ai.fitnova.service.*; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api") @RequiredArgsConstructor public class CoreController {private final ProfileService profiles;private final ProfileRepository profileRepo;private final ReadinessService readiness;private final WorkoutService workouts;private final ActivityService activity;private final GeminiService gemini;
 @GetMapping("/profile") ProfileView profile(Authentication a){return profiles.get(user(a));}
 @PutMapping("/profile") ProfileView profile(Authentication a,@Valid @RequestBody ProfileRequest r){return profiles.save(user(a),r);}
 @PostMapping("/readiness") ReadinessView readiness(Authentication a,@Valid @RequestBody ReadinessRequest r){return readiness.create(user(a),r);}
 @GetMapping("/readiness/latest") ReadinessView latest(Authentication a){return readiness.latest(user(a));}
 @PostMapping("/workouts/generate") WorkoutView generate(Authentication a,@Valid @RequestBody WorkoutRequest r){return workouts.generate(user(a),r);}
 @GetMapping("/workouts") List<WorkoutView> workouts(Authentication a){return workouts.history(user(a));}
 @PatchMapping("/workouts/{id}/start") WorkoutView start(Authentication a,@PathVariable Long id){return workouts.start(user(a),id);}
 @PatchMapping("/workouts/{id}/complete") WorkoutView complete(Authentication a,@PathVariable Long id,@Valid @RequestBody(required=false) WorkoutFeedback r){return workouts.complete(user(a),id,r);}
 @PostMapping("/meals") MealView meal(Authentication a,@Valid @RequestBody MealRequest r){return activity.saveMeal(user(a),r);}
 @GetMapping("/meals") List<MealView> meals(Authentication a){return activity.meals(user(a));}
 @PostMapping("/form-sessions") FormSessionView form(Authentication a,@Valid @RequestBody FormSessionRequest r){return activity.saveForm(user(a),r);}
 @GetMapping("/form-sessions") List<FormSessionView> forms(Authentication a){return activity.forms(user(a));}
 @GetMapping("/progress") ProgressView progress(Authentication a){return activity.progress(user(a));}
 @GetMapping("/dashboard") DashboardView dashboard(Authentication a){User u=user(a);boolean complete=profileRepo.findByUserId(u.getId()).map(p->p.isOnboardingComplete()).orElse(false);return activity.dashboard(u,complete);}
 @GetMapping("/ai/status") AiStatusView aiStatus(){return gemini.status();}
 @PostMapping("/ai/ask") AskFitNovaResponse ask(Authentication a,@Valid @RequestBody AskFitNovaRequest r){return gemini.ask(user(a),r);}
 @PostMapping("/ai/kitchen") MealView kitchen(Authentication a,@Valid @RequestBody KitchenRequest r){User u=user(a);return activity.saveMeal(u,gemini.meal(u,r));}
 private User user(Authentication a){return ApiSupport.user(a);}}
