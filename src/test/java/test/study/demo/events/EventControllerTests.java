package test.study.demo.events;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import test.study.demo.common.BaseControllerTest;
import test.study.demo.common.TestDescription;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {

        EventDto event = EventDto.builder()
                .name("Srping")
                .description("Spring Api to studying")
                .beginEnrollmentDateTime(LocalDateTime.of(2020,6,5,16,24,1))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,6,6,16,25,1))
                .beginEventDateTime(LocalDateTime.of(2020,7,1,9,1,3))
                .endEventDateTime(LocalDateTime.of(2020,7,2,17,59,59))
                .basePrice(100)
                .maxPrice(200)
                .location("강남역 어딘가")
                .build();

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(event))
                    .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links( linkWithRel("self").description("link to self"),
                               linkWithRel("query-events").description("link to query events"),
                               linkWithRel("update-event").description("link to update an existing event"),
                               linkWithRel("profile").description("link to update an existing event")
                      ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("Date time of beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("Date time of closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("Date time of beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("Date time of endEventDateTime"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new Event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("LOCATION Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content Type Header")

                        ),
                        //relaxedResponseFields 단점 :사용시 정확한 문서를 만들수 없는 단점
                        //                      장점 : 문서 일부분만 테스트 할수 있다.
                        responseFields(
                                fieldWithPath("id").description("Identifier of new Event"),
                                fieldWithPath("name").description("Name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("Date time of beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("Date time of closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("Date time of beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("Date time of endEventDateTime"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new Event"),
                                fieldWithPath("free").description("It tells if this event is free"),
                                fieldWithPath("offline").description("It tells if this event is offline"),
                                fieldWithPath("eventStatus").description("It tells if this event is eventStatus"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query-events"),
                                fieldWithPath("_links.update-event.href").description("link to update-event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                    )
                )
                ;

    }


    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(10)
                .name("Srping")
                .description("Spring Api to studying")
                .beginEnrollmentDateTime(LocalDateTime.of(2020,6,5,16,24,1))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,6,6,16,25,1))
                .beginEventDateTime(LocalDateTime.of(2020,7,1,9,1,3))
                .endEventDateTime(LocalDateTime.of(2020,7,2,17,59,59))
                .basePrice(100)
                .maxPrice(200)
                .location("강남역 어딘가")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.DRAFT)
                .build();

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(event))
                    .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }



    @Test
    @TestDescription("입력값이 잘못된 경우게 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Srping")
                .description("Spring Api to studying")
                .beginEnrollmentDateTime(LocalDateTime.of(2020,6,10,16,24,1))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,6,6,16,25,1))
                .beginEventDateTime(LocalDateTime.of(2020,7,10,9,1,3))
                .endEventDateTime(LocalDateTime.of(2020,7,2,17,59,59))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .build();

        this.mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception{
        Event event = this.generateEvent(100);

        this.mockMvc.perform(get("/api/events/{id}",event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-query"))
                ;
    }

    @Test
    @TestDescription("없는 이벤트를 조회 했을 때 404응답 받기")
    public void getEvent404() throws Exception{
        this.mockMvc.perform(get("/api/events/123543"))
                .andExpect(status().isNotFound())
                ;
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception{

        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event,EventDto.class);

        String eventName = "update Event";
        eventDto.setName(eventName);

        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").value(eventName))
                    .andExpect(jsonPath("_links.self").exists())
                    .andDo(document("update-event"))
        ;

    }

    @Test
    @TestDescription("입력값이 비어 있는 경우에는 이벤트 수정하기")
    public void updateEvent400_Empty() throws Exception {
        Event event = this.generateEvent(200);
        EventDto eventDto = new EventDto();

        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                ;
    }

    @Test
    @TestDescription("입력값이 잘못 되어 있는 경우 이벤트 수정하기")
    public void updateEvent400_Wrong() throws Exception {
        Event event = this.generateEvent(200);

        EventDto eventDto = modelMapper.map(event,EventDto.class);
        eventDto.setBasePrice(99999);
        eventDto.setMaxPrice(10000);

        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @TestDescription("존재하지 않는 이벤트 수정 실패")
    public void updateEvent404() throws Exception {

        Event event = this.generateEvent(200);
        EventDto eventDto = modelMapper.map(event,EventDto.class);

        this.mockMvc.perform(put("/api/events/33333")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
            )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;

    }


    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2020,03,30,22,45,00))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,03,30,22,46,00))
                .beginEventDateTime(LocalDateTime.of(2020,03,31,18,30,00))
                .endEventDateTime(LocalDateTime.of(2020,03,31,22,00,00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("여의도")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }
}
