package lv.redsails.authservice.service.impl;

import lombok.AllArgsConstructor;
import lv.redsails.authservice.domain.Role;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.repository.RoleRepository;
import lv.redsails.authservice.repository.UserRepository;
import lv.redsails.authservice.service.RolesManagementService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RolesManagementServiceImpl implements RolesManagementService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public Role createRoles(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public List<Role> createRoles(List<Role> role) {
        return roleRepository.saveAll(role);
    }

    @Override
    public void removeRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public User giveRoleToUser(Long roleId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        Role role = roleRepository.findById(roleId).orElseThrow(RuntimeException::new);
        user.getRoles().add(role);
        userRepository.save(user);
        return user;
    }

    @Override
    public User takeAwayRoleFromUser(Long roleId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        Role role = roleRepository.findById(roleId).orElseThrow(RuntimeException::new);
        user.getRoles().remove(role);
        return user;
    }

    @Override
    public List<Role> getAllUserRoles(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        return List.copyOf(user.getRoles());
    }

}
