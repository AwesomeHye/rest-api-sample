package dev.hyein.lecture.restapisample.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hyein.lecture.restapisample.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest //@SpringBootApplication 부터 시작해서 모든 빈 등록
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    //스프링 부트 사용시 MappingJacksonJson이 의존성으로 들어가있으면 ObjectMapper을 자동으로 빈으로 등록해준다.
    @Autowired
    ObjectMapper objectMapper;


    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
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
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION)) //"location" 대신 .LOCATION: type-safe
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,"application/hal+json" ))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));

    }

    /**
     * eventDto에 들어있는 값이 아닌 비정상정인 값을 받으면 bad request로 응답
     * @throws Exception
     */
    @Test
    @TestDescription("입력받을 수 없는 값을 입력받는 경우 에러 발생하는 테스트")
    public void createEvent_bad_request_unknown_input() throws Exception {
        Event event = Event.builder()
                .id(100)
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
                .free(true)
                .eventStatus(EventStatus.BEGAN_ENROLLMENT)
                .build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
//                .andExpect(status().isCreated())
                .andExpect(status().isBadRequest())

        ;

    }

    @Test
    @TestDescription("입력받이 빈 경우 에러 발생하는 테스트")
    public void createEvent_bad_request_empty_input() throws Exception{
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventDto))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 틀린 경우 에러 발생하는 테스트")
    public void createEvent_bad_request_wrong_input() throws Exception{
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API WITH SPRING")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 2, 1, 18, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 1, 5, 18, 0))
                .beginEventDateTime(LocalDateTime.of(2020, 1,1,13,0))
                .endEventDateTime(LocalDateTime.of(2020,3, 1,13,0))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("D2 Factory")
                .build();
        mockMvc.perform(post("/api/events")
                        .contentType(MediaTypes.HAL_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto))
                        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
        ;
    }
}