package dev.hyein.lecture.restapisample.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

@JsonComponent //spring objectMapper가 직렬화할 때 알아서 이 객체 참조한다.
public class ErrorsSerializer extends JsonSerializer<Errors> {


    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();

        errors.getFieldErrors().forEach(e -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("field", e.getField());
                jsonGenerator.writeStringField("objectName", e.getObjectName());
                jsonGenerator.writeStringField("code", e.getCode());
                jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
                jsonGenerator.writeStringField("rejectedValue", e.getDefaultMessage());
                jsonGenerator.writeEndObject();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        errors.getGlobalErrors().forEach(e -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("objectName", e.getObjectName());
                jsonGenerator.writeStringField("code", e.getCode());
                jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
                jsonGenerator.writeEndObject();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        jsonGenerator.writeEndArray();
    }
}
