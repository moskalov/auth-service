package lv.redsails.authservice.service;

import lv.redsails.authservice.domain.Role;
import lv.redsails.authservice.domain.User;

import java.util.List;
import java.util.Optional;


public interface UserService {

    User createUser(User user);

    Optional<User> removeUser(Long id);

    List<User> findUsers();

}
