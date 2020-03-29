package dev.hyein.lecture.restapisample.events;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;
import java.net.http.HttpRequest;

@Controller
//이 클래스 안의 모든 핸들러들은 produces의 HAL_JSON의 형태로 응답을 보낼거고 value를 base url로 받음.
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
    @PostMapping()
    public ResponseEntity createEvent(@RequestBody Event event){

        //Location 헤더에 쓰이는 생성한 이벤트 조회하는 URI //http://localhost/api/events/%257Bid%257D
        URI createdUri = linkTo(EventController.class).slash("{id}").toUri();
        event.setId(1);

        return ResponseEntity.created(createdUri).body(event);
    }
}
