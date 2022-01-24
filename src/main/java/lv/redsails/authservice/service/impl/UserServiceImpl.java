package lv.redsails.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.repository.UserRepository;
import lv.redsails.authservice.security.model.PasswordEncoder;
import lv.redsails.authservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> removeUser(Long id) {
        Optional<User> userToDelete = Optional.of(userRepository.getById(id));
        userToDelete.ifPresent(userRepository::delete);
        return userToDelete;
    }

    @Override
    public List<User> findUsers() {
        return userRepository.findAll();
    }

}
