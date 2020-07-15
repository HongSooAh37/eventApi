package test.study.demo.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import test.study.demo.common.RestDocsConfiguration;
import test.study.demo.common.TestDescription;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
}
