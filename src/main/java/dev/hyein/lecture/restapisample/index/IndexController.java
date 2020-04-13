package dev.hyein.lecture.restapisample.index;

        import dev.hyein.lecture.restapisample.events.EventController;
        import org.springframework.hateoas.RepresentationModel;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RestController;

        import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
        import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class IndexController {

    @GetMapping("/api")
    public RepresentationModel index(){
        RepresentationModel index = new RepresentationModel();
        index.add(linkTo(EventController.class).withRel("events"));
        return index;
    }
}
