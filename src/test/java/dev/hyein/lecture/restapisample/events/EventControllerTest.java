package dev.hyein.lecture.restapisample.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest //웹용 빈들만 등록해주지 Repository 빈은 등록하지 않는다.
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    //스프링 부트 사용시 MappingJacksonJson이 의존성으로 들어가있으면 ObjectMapper을 자동으로 빈으로 등록해준다.
    @Autowired
    ObjectMapper objectMapper;


    //@WebMvcTest 때문에 자동 빈 등록 안되니 mock으로 만들어줌
    @MockBean
    EventRepository eventRepository;

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
        //MockBean은 메소드 동작 안해서 내가 직접 구현해줘야함
        event.setId(10);
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(event))
                        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION)) //"location" 대신 .LOCATION: type-safe
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,"application/hal+json" ));

    }

}