package test.study.demo.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors){
        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0){
            errors.rejectValue("basePrice","wrongValue","basePrice is Wrong value");
            errors.rejectValue("maxPrice","wrongValue","maxPrice is Wrong value");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if( endEventDateTime.isBefore(eventDto.getBeginEventDateTime())      ||
            endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
            endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())
        ){
            errors.rejectValue("endEventDateTime","wrongValue","endEventDateTime is Wrong value");
        }

        //TODO : 나머지 데이터 검증 추가
        //TODO :beginEventDateTime
        //TODO : CloseEnrollmentDateTIme

    }
}
