package dev.hyein.lecture.restapisample.events;

        import org.springframework.hateoas.EntityModel;
        import org.springframework.hateoas.Link;

        import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> {

    public EventResource(Event event, Link... links) {
        super(event, links);
        //withSelfRel(): self link
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel()); // = http://localhost/api/events/1
    }
}
