package dev.hyein.lecture.restapisample.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

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

    //생성자가 하나만 있고 파라미터가 이미 빈으로 등록되어있으면 @Autowired 안 붙여도 된다. (spring 4.3 ~)
    public EventController(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping()
    public ResponseEntity createEvent(@RequestBody EventDto eventDto){
        Event event = modelMapper.map(eventDto, Event.class);
        Event newEvent = eventRepository.save(event);

        //Location 헤더에 쓰이는 생성한 이벤트 조회하는 URI //http://localhost/api/events/%257Bid%257D
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();

        return ResponseEntity.created(createdUri).body(newEvent);
    }
}
