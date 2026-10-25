package ai.fitnova;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowTest {
    @Autowired MockMvc mvc;

    @Test
    void completeUserJourneyPersistsAndReturnsRealData() throws Exception {
        String signup = mvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"fullName":"Test User","email":"test@fitnova.ai","password":"password123"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.user.onboardingComplete").value(false))
            .andReturn().getResponse().getContentAsString();

        var tokenMatch = Pattern.compile("\\\"token\\\":\\\"([^\\\"]+)\\\"").matcher(signup);
        assertThat(tokenMatch.find()).isTrue();
        String bearer = "Bearer " + tokenMatch.group(1);

        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@fitnova.ai\",\"password\":\"password123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());

        mvc.perform(put("/api/profile")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"age":25,"heightCm":170,"weightKg":70,"goal":"GENERAL FITNESS",
                     "experience":"BEGINNER","location":"HOME","equipment":["BODYWEIGHT"],
                     "diet":"BALANCED","allergies":"","limitations":""}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.onboardingComplete").value(true));

        mvc.perform(post("/api/readiness")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"sleepHours":8,"sleepQuality":4,"energy":4,"soreness":2,"mood":4,
                     "stress":2,"availableMinutes":30,"currentCondition":""}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.score").isNumber());

        String workout = mvc.perform(post("/api/workouts/generate")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"minutes":30,"location":"HOME","focus":"FULL BODY","equipment":["BODYWEIGHT"]}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PLANNED"))
            .andReturn().getResponse().getContentAsString();

        var idMatch = Pattern.compile("\\\"id\\\":(\\d+)").matcher(workout);
        assertThat(idMatch.find()).isTrue();
        String workoutId = idMatch.group(1);

        mvc.perform(patch("/api/workouts/{id}/start", workoutId).header("Authorization", bearer))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mvc.perform(patch("/api/workouts/{id}/complete", workoutId)
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"feedback\":\"JUST_RIGHT\",\"energyAfter\":4,\"notes\":\"Felt good\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));

        mvc.perform(post("/api/meals")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Protein Toast","goal":"BALANCED","ingredients":["Eggs","Bread"],
                     "instructions":["Cook eggs","Serve on toast"],"calories":380,"proteinG":24,"prepMinutes":12}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Protein Toast"));

        mvc.perform(post("/api/form-sessions")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"exercise":"Squat","reps":10,"score":86,"bestScore":90,
                     "durationSeconds":60,"corrections":["Keep knees aligned"]}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.score").value(86));

        mvc.perform(get("/api/dashboard").header("Authorization", bearer))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.onboardingComplete").value(true))
            .andExpect(jsonPath("$.summary.completedWorkouts").value(1))
            .andExpect(jsonPath("$.recentActivity[0].status").value("COMPLETED"));

        mvc.perform(get("/api/progress").header("Authorization", bearer))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completedWorkouts").value(1))
            .andExpect(jsonPath("$.averageFormScore").value(86));
    }
}
