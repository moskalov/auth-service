package lv.redsails.authservice.maps;

import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapTest {

    @Autowired
    private UserMap userMap;

    @Test
    void map() {
        List<User> userList = List.of(
                new User().setId(2L).setEmail("email"),
                new User().setId(1L).setEmail("email"));

        List<UserDTO> dtoList = userMap.map(userList);
        System.out.println(dtoList);
    }

}