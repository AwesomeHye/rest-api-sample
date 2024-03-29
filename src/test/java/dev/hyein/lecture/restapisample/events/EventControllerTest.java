package dev.hyein.lecture.restapisample.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hyein.lecture.restapisample.account.Account;
import dev.hyein.lecture.restapisample.account.AccountRepository;
import dev.hyein.lecture.restapisample.account.AccountRole;
import dev.hyein.lecture.restapisample.account.AccountService;
import dev.hyein.lecture.restapisample.common.AppProperties;
import dev.hyein.lecture.restapisample.common.BaseControllerTest;
import dev.hyein.lecture.restapisample.common.RestDocsConfiguration;
import dev.hyein.lecture.restapisample.common.TestDescription;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EventControllerTest extends BaseControllerTest {


    @Autowired
    EventRepository eventRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AppProperties appProperties;

    @Before
    public void deleteAll(){
        eventRepository.deleteAll();
        accountRepository.deleteAll();
    }

    /**
     * 액세스 토큰 발급 받아서 요청 헤더에 넣기
     * [Authorization:"Bearer 2a96c9cd-5877-459d-b335-43236928bc81"]
     * @return "2a96c9cd-5877-459d-b335-43236928bc81"
     * @throws Exception
     */
    public String getAccessToken() throws Exception{
        //given
        String userName = appProperties.getUserUsername();
        String password = appProperties.getUserPassword();
        Account account = Account.builder()
                .email(userName)
                .password(password)
                .roles(Set.of(AccountRole.USER))
                .build();
        accountService.saveAccount(account);

        // then
        String reponseBody = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret())) // http basic 인증 사용
                .param("grant_type", "password")
                .param("username", userName)
                .param("password", password))
                .andReturn().getResponse().getContentAsString();

        return new Jackson2JsonParser().parseMap(reponseBody).get("access_token").toString();
    }

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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                .andExpect(jsonPath("manager").value(Matchers.notNullValue()))
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
                        fieldWithPath("_links.profile.href").description("link to profile"),
                        fieldWithPath("manager.id").description("current user id")

                )
        ))
                ;

    }

    private String getBearerToken() throws Exception {
        return "Bearer " + getAccessToken();
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
    @TestDescription("인증 없이 30개 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception{
        //Given 이벤트 30개
        IntStream.range(0, 30).forEach(i -> generateEvent(i));

        //when & then
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

    @Test
    @TestDescription("인증된 사용자가 30개 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEventsWithAuthentication() throws Exception{
        //Given 이벤트 30개
        IntStream.range(0, 30).forEach(i -> generateEvent(i));

        //when & then
        mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("get-events"))
        ;
    }

    @Test
    @TestDescription("인증 없이 기존 한 개 이벤트 조회하기")
    public void getEvent() throws Exception{
        //given
        Event event = generateEvent(100);

        //when & then
        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
                ;
    }

    @Test
    @TestDescription("인증된 사용자가 기존 한 개 이벤트 조회하기")
    public void getEventWithAuthentication() throws Exception{
        //given
        Event event = generateEvent(100);

        //when & then
        mockMvc.perform(get("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("get-an-event"))
        ;
    }

    @Test
    @TestDescription("없는 이벤트를 조회시 404 응답받기")
    public void notExistEvent404() throws Exception{
        mockMvc.perform(get("/api/events/232"))
                .andDo(print())
                .andExpect(status().isNotFound()); //404
    }

    @Test
    @TestDescription("이벤트 수정 - 정상적")
    public void updateEvent200() throws Exception{
        //given
        Event event = generateEvent(10);

        //when
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        String updatedEvent = "updated event";
        eventDto.setName(updatedEvent);

        //then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto))
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(updatedEvent))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                ;

    }

    @Test
    @TestDescription("이벤트 수정 - 입력값이 없는 경우")
    public void updateEvent_empty() throws Exception {
        //given
        Event event = generateEvent(10);

        //when
        EventDto eventDto = new EventDto();

        //then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventDto))
                .accept(MediaTypes.HAL_JSON ))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("이벤트 수정 - 잘못된 값")
    public void updateEvent_wrong() throws Exception {
        //given
        Event event = generateEvent(10);

        //when
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(100);
        eventDto.setMaxPrice(10);

        //then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventDto))
                .accept(MediaTypes.HAL_JSON ))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("이벤트 수정 - 이벤트가 없는 경우")
    public void updateEvent_notExistEvent() throws Exception {
        //given
        Event event = generateEvent(10);
        //when
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(100);
        event.setMaxPrice(10);

        //then
        mockMvc.perform(put("/api/events/292929")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventDto))
                .accept(MediaTypes.HAL_JSON ))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    private Event generateEvent(int i) {
        Event event = Event.builder()
                .name("event"+i)
                .description("sample event")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 1, 1, 18, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 1, 5, 18, 0))
                .beginEventDateTime(LocalDateTime.of(2020, 2,1,13,0))
                .endEventDateTime(LocalDateTime.of(2020,3, 1,13,0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("D2 Factory")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return eventRepository.save(event);
    }


}