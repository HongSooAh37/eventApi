package test.study.demo.events;

import org.assertj.core.api.AssertionsForClassTypes;
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

    @Test
    public void testFree(){
        //given 1
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isTrue();

        //given2
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isFalse();


        //given3
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testoffLine(){
        //given3
        Event event = Event.builder()
                .location("여의도")
                .build();

        //when
        event.update();
        //then
        AssertionsForClassTypes.assertThat(event.isOffline()).isTrue();

        //given4
        event = Event.builder()
                .build();
        //when
        event.update();

        //then
        AssertionsForClassTypes.assertThat(event.isOffline()).isFalse();
    }


}