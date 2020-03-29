package dev.hyein.lecture.restapisample.events;




import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class EventTest {
    @Test
    public void builder(){
        Event event = Event.builder().build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBeanSpec(){
        //Given
        String name = "event";
        String desc = "desc";

        //When
        Event event = new Event();
        event.setName(name);
        event.setDescription(desc);

        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(desc);
    }

}