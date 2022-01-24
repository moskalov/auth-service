package lv.redsails.authservice.domain;


import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

import java.util.Objects;

import static javax.persistence.GenerationType.*;


@Getter
@Setter
@ToString
@Accessors(chain = true)

@Entity
@Table(name = "role")
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
