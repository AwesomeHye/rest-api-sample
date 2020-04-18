package dev.hyein.lecture.restapisample.events;


import dev.hyein.lecture.restapisample.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder @NoArgsConstructor @AllArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {
    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean offline;
    private String location;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;
    @ManyToOne
    private Account manager; // 계정 관리자

    public void update() {
        // update free
        if(basePrice == 0 && maxPrice == 0)
            free = true;
        else
            free = false;

        //update offline
        offline = !(location == null || location.isBlank() ); // isBlank() since java 11

    }
}
