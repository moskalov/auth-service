package lv.redsails.authservice.service;


import lv.redsails.authservice.domain.Role;
import lv.redsails.authservice.domain.User;


import java.util.List;

public interface RolesManagementService {

    Role createRoles(Role role);

    List<Role> createRoles(List<Role> role);

    void removeRole(Long roleId);

    List<Role> getRoles();


    User giveRoleToUser(Long roleId, Long userId);

    User takeAwayRoleFromUser(Long roleId, Long userId);

    List<Role> getAllUserRoles(Long userId);

}
