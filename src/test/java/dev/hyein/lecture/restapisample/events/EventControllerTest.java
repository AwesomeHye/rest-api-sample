package dev.hyein.lecture.restapisample.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hyein.lecture.restapisample.common.RestDocsConfiguration;
import dev.hyein.lecture.restapisample.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest //@SpringBootApplication 부터 시작해서 모든 빈 등록
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    //스프링 부트 사용시 MappingJacksonJson이 의존성으로 들어가있으면 ObjectMapper을 자동으로 빈으로 등록해준다.
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

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
                .andExpect(header().exists(HttpHeaders.LOCATION)) //"location" 대신 HttpHeaders.LOCATION: type-safe
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,"application/hal+json" ))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                links(
                        linkWithRel("self").description("link to self"),
                        linkWithRel("query-events").description("link to query events"),
                        linkWithRel("update-event").description("link to update"),
                        linkWithRel("profile").description("link to profile")

                ),
                requestHeaders(
                        headerWithName(HttpHeaders.ACCEPT).description("HAL+JSON"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL+JSON")
                        ),
                requestFields(
                        fieldWithPath("name").description("new event name"),
                        fieldWithPath("description").description("new event description"),
                        fieldWithPath("beginEnrollmentDateTime").description("new event begin enrollment time"),
                        fieldWithPath("closeEnrollmentDateTime").description("new event close enrollment time"),
                        fieldWithPath("beginEventDateTime").description("new event begin time"),
                        fieldWithPath("endEventDateTime").description("new event end time"),
                        fieldWithPath("location").description("new event location"),
                        fieldWithPath("basePrice").description("new event base price"),
                        fieldWithPath("maxPrice").description("new event location max price"),
                        fieldWithPath("limitOfEnrollment").description("new event limit of enrollment")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.LOCATION).description("get events"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL+JSON")
                ),
                responseFields(
                        fieldWithPath("id").description("new event id"),
                        fieldWithPath("name").description("new event name"),
                        fieldWithPath("description").description("new event description"),
                        fieldWithPath("beginEnrollmentDateTime").description("new event begin enrollment time"),
                        fieldWithPath("closeEnrollmentDateTime").description("new event close enrollment time"),
                        fieldWithPath("beginEventDateTime").description("new event begin time"),
                        fieldWithPath("endEventDateTime").description("new event end time"),
                        fieldWithPath("location").description("new event location"),
                        fieldWithPath("basePrice").description("new event base price"),
                        fieldWithPath("maxPrice").description("new event location max price"),
                        fieldWithPath("limitOfEnrollment").description("new event limit of enrollment"),
                        fieldWithPath("offline").description("new event is offline"),
                        fieldWithPath("free").description("new event is free"),
                        fieldWithPath("eventStatus").description("new event status"),
                        fieldWithPath("_links.self.href").description("link to self"),
                        fieldWithPath("_links.query-events.href").description("link to query-events"),
                        fieldWithPath("_links.update-event.href").description("link to update-event"),
                        fieldWithPath("_links.profile.href").description("link to profile")
                )
        ))
                ;

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
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }


    @Test
    @TestDescription("30개 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception{
        //Given 이벤트 30개
        IntStream.range(0, 30).forEach(i -> generateEvent(i));

        //then
        mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-events"))
                ;
    }

    private void generateEvent(int i) {
        Event event = Event.builder()
                        .name("event"+i)
                        .description("sample event")
                        .build();

        eventRepository.save(event);
    }
}