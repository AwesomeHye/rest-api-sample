package dev.hyein.lecture.restapisample.account;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER) // enum의 리스트를 가져오므로 선언. EAGER는 모든 데이터를 가져옴.
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
