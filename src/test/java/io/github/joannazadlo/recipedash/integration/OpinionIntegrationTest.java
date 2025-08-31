package io.github.joannazadlo.recipedash.integration;

import io.github.joannazadlo.recipedash.integration.common.AbstractIntegrationTest;
import io.github.joannazadlo.recipedash.model.enums.Role;
import io.github.joannazadlo.recipedash.model.enums.Status;
import io.github.joannazadlo.recipedash.model.enums.UserOpinion;
import io.github.joannazadlo.recipedash.model.rating.SaveOpinionDto;
import io.github.joannazadlo.recipedash.repository.OpinionRepository;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.repository.entity.Opinion;
import io.github.joannazadlo.recipedash.repository.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class OpinionIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanUp() {
        opinionRepository.deleteAll();
    }

    @Test
    void saveOpinion_shouldCreateOpinionAndReturnRating() throws Exception {
        SaveOpinionDto opinionDto = SaveOpinionDto.builder()
                .userOpinion(UserOpinion.LIKE)
                .build();

        mockMvc.perform(put("/opinions/User/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(opinionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeId").value("1"))
                .andExpect(jsonPath("$.recipeSource").value("User"))
                .andExpect(jsonPath("$.likes").value(1))
                .andExpect(jsonPath("$.dislikes").value(0))
                .andExpect(jsonPath("$.neutral").value(0))
                .andExpect(jsonPath("$.userOpinion").value("like"));

        List<Opinion> opinions = opinionRepository.findAll();
        assertEquals(1, opinions.size());

        Opinion savedOpinion = opinions.get(0);
        assertEquals("1", savedOpinion.getRecipeId());
        assertEquals("User", savedOpinion.getRecipeSource());
        assertEquals(UserOpinion.LIKE, savedOpinion.getUserOpinion());
        Assertions.assertEquals(user.getUid(), savedOpinion.getUser().getUid());
    }

    @Test
    void getRecipeRating_ShouldReturnCorrectRating() throws Exception {
        User user2 = User.builder()
                .uid("user100")
                .email("test100@test.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user2);

        User user3 = User.builder()
                .uid("user200")
                .email("test200@test.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user3);

        Opinion opinionLike = Opinion.builder()
                .user(user)
                .recipeId("1")
                .recipeSource("User")
                .userOpinion(UserOpinion.LIKE)
                .build();

        Opinion opinionDislike = Opinion.builder()
                .user(user2)
                .recipeId("1")
                .recipeSource("User")
                .userOpinion(UserOpinion.DISLIKE)
                .build();

        Opinion opinionNeutral = Opinion.builder()
                .user(user3)
                .recipeId("1")
                .recipeSource("User")
                .userOpinion(UserOpinion.NEUTRAL)
                .build();

        opinionRepository.save(opinionLike);
        opinionRepository.save(opinionDislike);
        opinionRepository.save(opinionNeutral);

        mockMvc.perform(get("/opinions/User/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeId").value("1"))
                .andExpect(jsonPath("$.recipeSource").value("User"))
                .andExpect(jsonPath("$.likes").value(1))
                .andExpect(jsonPath("$.dislikes").value(1))
                .andExpect(jsonPath("$.neutral").value(1))
                .andExpect(jsonPath("$.userOpinion").value("like"));
    }
}
