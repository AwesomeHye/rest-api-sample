package dev.hyein.lecture.restapisample.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors){
        int basePrice = eventDto.getBasePrice();
        int maxPrice = eventDto.getMaxPrice();
        if(basePrice > maxPrice && maxPrice != 0){
            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong.");
//            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong.");
            errors.reject( "wrongValue", "MaxPrice is wrong.");
        }

        LocalDateTime beginEnrollmentDateTime = eventDto.getBeginEnrollmentDateTime();
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if(beginEnrollmentDateTime.isAfter(closeEnrollmentDateTime) ||
            beginEnrollmentDateTime.isAfter(beginEventDateTime) ||
            beginEnrollmentDateTime.isAfter(endEventDateTime)) {
            errors.rejectValue("beginEnrollmentDateTime", "wrongValue", "beginEnrollmentDateTime is wrong.");
        }

        if(closeEnrollmentDateTime.isBefore(beginEnrollmentDateTime) ||
                closeEnrollmentDateTime.isAfter(endEventDateTime)){
            errors.rejectValue("closeEnrollmentDateTime", "wrongValue", "closeEnrollmentDateTime is wrong.");
        }

        if(beginEventDateTime.isBefore(beginEnrollmentDateTime) ||
            beginEventDateTime.isAfter(endEventDateTime)){
            errors.rejectValue("beginEventDateTime", "wrongValue", "beginEventDateTime is wrong.");
        }

        if(endEventDateTime.isBefore(beginEnrollmentDateTime) ||
            endEventDateTime.isBefore(closeEnrollmentDateTime)){
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong.");

        }

    }
}
