package lv.redsails.authservice.maps;

import lv.redsails.authservice.domain.Role;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.model.request.RegistrationRequestBody;
import lv.redsails.authservice.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMap {

    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    User map(RegistrationRequestBody signUp);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "roles", target = "roles")
    UserDTO map(User user);

    default List<String> mapRoles(Set<Role> roles) {
        return roles.stream().map(Role::getName).collect(Collectors.toList());
    }

    @Mapping(source = "employees", target = ".", qualifiedByName = "mapCollection")
    List<UserDTO> map(List<User> employees);

    default List<UserDTO> mapCollection(List<User> users) {
        return users.stream().map(this::map).collect(Collectors.toList());
    }


}
