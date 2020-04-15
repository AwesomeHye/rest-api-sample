package dev.hyein.lecture.restapisample.events;

import dev.hyein.lecture.restapisample.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
//이 클래스 안의 모든 핸들러들은 produces의 HAL_JSON의 형태로 응답을 보낼거고 value를 base url로 받음.
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
    /*
    * 의존성 주입 받는 법
    * 1. @Autowired
    *
    * 2. 생성자
    * */

/*
    @Autowired
    EventRepository eventRepository;
*/
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    @Autowired
    EventValidator eventValidator;

    //생성자가 하나만 있고 파라미터가 이미 빈으로 등록되어있으면 @Autowired 안 붙여도 된다. (spring 4.3 ~)
    public EventController(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = eventRepository.save(event);

        //Location 헤더에 쓰이는 생성한 이벤트 조회하는 URI // = http://localhost/api/events/%257Bid%257D
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();

        //HATEOAS 링크 추가
        EventResource eventResource = new EventResource(newEvent);
        //이벤트 목록
        Link eventsLink = linkTo(EventController.class).withRel("query-events"); // = http://localhost/api/events
        eventResource.add(eventsLink);
        //이벤트 수정
        Link updateLink = linkTo(EventController.class).slash(newEvent.getId()).withRel("update-event"); // = http://localhost/api/events/1
        eventResource.add(updateLink);
        //profile (API 설명) 추가
        eventResource.add(new Link("/docs/index.html/#resourced-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    private ResponseEntity<ErrorsResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    @GetMapping
    public ResponseEntity getEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler){
        Page<Event> eventPages = eventRepository.findAll(pageable); // 페이지 정보에 해당하는 이벤트만 find함
        //page link 추가: 인자로 받은 PagedResourcesAssembler 가 Page 객체에 link 정보(처음,끝,이전,다음,self)도 추가해줌
        //event link 추가: 2번째 인자로
        PagedModel<EntityModel<Event>> eventPageModel = assembler.toModel(eventPages, e -> new EventResource(e));
        //self-descriptive
        eventPageModel.add(new Link("/docs/index.html/#resources-events-list").withRel("profile"));


        return ResponseEntity.ok(eventPageModel);
    }
}
