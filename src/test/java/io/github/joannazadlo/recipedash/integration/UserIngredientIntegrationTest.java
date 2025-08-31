package io.github.joannazadlo.recipedash.integration;

import io.github.joannazadlo.recipedash.integration.common.AbstractIntegrationTest;
import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientCreateDto;
import io.github.joannazadlo.recipedash.repository.UserIngredientRepository;
import io.github.joannazadlo.recipedash.repository.entity.UserIngredient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserIngredientIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserIngredientRepository userIngredientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanUp() {
        userIngredientRepository.deleteAll();
    }

    @Test
    void createUserIngredient_ShouldSaveUserIngredient() throws Exception {
        UserIngredientCreateDto requestDto = UserIngredientCreateDto.builder()
                .ingredient("Avocado")
                .build();

        mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ingredient").value("Avocado"));

        List<UserIngredient> ingredients = userIngredientRepository.findAll();
        assertEquals(1, ingredients.size());
        assertEquals("Avocado", ingredients.get(0).getIngredient());
    }
}
