package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.mapper.UserIngredientMapper;
import io.github.joannazadlo.recipedash.model.enums.Role;
import io.github.joannazadlo.recipedash.model.enums.Status;
import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientCreateDto;
import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientDto;
import io.github.joannazadlo.recipedash.repository.UserIngredientRepository;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.repository.entity.User;
import io.github.joannazadlo.recipedash.repository.entity.UserIngredient;
import io.github.joannazadlo.recipedash.utils.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserIngredientServiceTest {

    @Mock
    private UserIngredientRepository userIngredientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserIngredientMapper userIngredientMapper;

    @InjectMocks
    private UserIngredientService userIngredientService;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    private User user;

    @BeforeEach
    void setup() {
        UserDto userDto = UserDto.builder()
                .uid("user123")
                .email("test@test.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        user = User.builder()
                .uid("user123")
                .email("test@test.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        securityUtilsMock = Mockito.mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getCurrentUser).thenReturn(userDto);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }

    @Test
    void createUserIngredient_shouldSaveAndReturnDto() {

        UserIngredientCreateDto createDto = UserIngredientCreateDto.builder()
                .ingredient("Avocado")
                .build();

        UserIngredientDto returnedDto = UserIngredientDto.builder()
                .ingredient("Avocado")
                .id(1L)
                .build();

        UserIngredient mappedEntity = UserIngredient.builder()
                .ingredient("Avocado")
                .build();

        UserIngredient savedEntity = UserIngredient.builder()
                .id(1L)
                .ingredient("Avocado")
                .user(user)
                .build();

        when(userIngredientMapper.toEntity(createDto)).thenReturn(mappedEntity);
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(userIngredientRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(userIngredientMapper.toDto(savedEntity)).thenReturn(returnedDto);

        UserIngredientDto result = userIngredientService.createUserIngredient(createDto);

        assertEquals(returnedDto, result);
    }
}
