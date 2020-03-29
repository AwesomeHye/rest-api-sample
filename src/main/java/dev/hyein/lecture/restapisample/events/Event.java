package dev.hyein.lecture.restapisample.events;


import lombok.*;

import java.time.LocalDateTime;

@Builder @NoArgsConstructor @AllArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private boolean offline;
    private String location;
    private boolean free;
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
    private EventStatus eventStatus = EventStatus.DRAFT;
}
