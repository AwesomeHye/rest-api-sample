package dev.hyein.lecture.restapisample.events;

//엔티티객체에 여러 라이브러리의 애노테이션이 너무 많아질것을 우려하여 사용자로부터 입력받는 필드만 따로 관리

import lombok.*;

import java.time.LocalDateTime;

@Data @Builder
public class EventDto {
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location;
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
}

