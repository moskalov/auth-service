package lv.redsails.authservice.controller;

import lombok.RequiredArgsConstructor;
import lv.redsails.authservice.domain.Role;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.dto.UserDTO;
import lv.redsails.authservice.maps.UserMap;
import lv.redsails.authservice.service.RolesManagementService;
import lv.redsails.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final RolesManagementService rolesManagementService;
    private final UserMap userMap;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getRegisteredUsers(HttpServletRequest request, HttpServletResponse response) {
        List<User> users = userService.findUsers();
        return ResponseEntity.ok(userMap.map(users));
    }

    @PutMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<List<Role>> giveRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = rolesManagementService.giveRoleToUser(userId, roleId);
        List<Role> updatedRoleList = List.copyOf(user.getRoles());
        return ok(updatedRoleList);
    }

    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<List<Role>> takeAwayRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = rolesManagementService.takeAwayRoleFromUser(userId, roleId);
        List<Role> updatedRoleList = List.copyOf(user.getRoles());
        return ok(updatedRoleList);
    }

}
