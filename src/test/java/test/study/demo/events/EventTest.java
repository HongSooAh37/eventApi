package test.study.demo.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("test name")
                .description("test description")
                .build();

        assertThat(event).isNotNull();
    }

    @Test
    public void javabean(){

        String name = "test Name";
        String description = "test description";

        Event event = new Event();

        event.setName(name);
        event.setDescription(description);

        assertThat(event.getName()).isNotNull();
        assertThat(event.getDescription()).isNotNull();
    }

}