package dev.hyein.lecture.restapisample.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    //스프링 부트 사용시 MappingJacksonJson이 의존성으로 들어가있으면 ObjectMapper을 자동으로 빈으로 등록해준다.
    @Autowired
    ObjectMapper objectMapper;
    @Test
    public void createEvent() throws Exception {
        Event event = Event.builder()
                        .name("Spring")
                        .description("REST API WITH SPRING")
                        .beginEnrollmentDateTime(LocalDateTime.of(2020, 1, 1, 18, 0))
                        .closeEnrollmentDateTime(LocalDateTime.of(2020, 1, 5, 18, 0))
                        .beginEventDateTime(LocalDateTime.of(2020, 2,1,13,0))
                        .endEventDateTime(LocalDateTime.of(2020,3, 1,13,0))
                        .basePrice(100)
                        .maxPrice(200)
                        .limitOfEnrollment(100)
                        .location("D2 Factory")
                        .build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(event))
                        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists());

    }

}