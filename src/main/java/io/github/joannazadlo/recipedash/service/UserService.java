package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.exception.user.CannotBlockYourselfException;
import io.github.joannazadlo.recipedash.exception.user.UserAlreadyExistsException;
import io.github.joannazadlo.recipedash.exception.user.UserNotFoundException;
import io.github.joannazadlo.recipedash.mapper.UserMapper;
import io.github.joannazadlo.recipedash.model.enums.Role;
import io.github.joannazadlo.recipedash.model.enums.Status;
import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.model.user.UserSignUpDto;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.repository.entity.User;
import io.github.joannazadlo.recipedash.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDto createUser(UserSignUpDto userDto) {
        if (userRepository.existsById(userDto.getUid())) {
            throw new UserAlreadyExistsException("User already exists with uid: " + userDto.getUid());
        }

        User user = User.builder()
                .uid(userDto.getUid())
                .email(userDto.getEmail())
                .status(Status.ACTIVE)
                .role(userRepository.count() == 0 ? Role.ADMIN : Role.USER)
                .build();

        userRepository.save(user);
        log.info("New user created in DB: [uid={}]", userDto.getUid());

        return userMapper.toDto(user);
    }

    public UserDto getUserByUid(String uid) {
        return userRepository.findById(uid).map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + uid + " not found."));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateUserStatus(String uid, Status status) {
        UserDto currentUser = SecurityUtils.getCurrentUser();

        if (currentUser.getUid().equals(uid) && status == Status.BLOCKED) {
            throw new CannotBlockYourselfException();
        }

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + uid + " not found."));

        user.setStatus(status);

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }
}
