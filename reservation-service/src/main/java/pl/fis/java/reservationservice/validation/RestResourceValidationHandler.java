package pl.fis.java.reservationservice.validation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestResourceValidationHandler extends ResponseEntityExceptionHandler {

    /**
     * Returns a string containing the JSON error response for field-validation errors.
     */
    public JsonNode buildFieldErrorsJsonResponse(Set<ConstraintViolation<?>> constraintViolations) throws IOException {


        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(stringWriter);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("status", HttpStatus.BAD_REQUEST.toString().replaceAll(" ", "_").toUpperCase());

        jsonGenerator.writeArrayFieldStart("errors");


        constraintViolations.forEach(v -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField(v.getPropertyPath().toString(), v.getMessage());
                jsonGenerator.writeEndObject();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(stringWriter.toString());

        return jsonResponse;
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public @ResponseBody
    ResponseEntity<JsonNode> handleArgumEntityentNotValid(ConstraintViolationException e) {

        JsonNode jsonResponse = null;

        try {
           jsonResponse = buildFieldErrorsJsonResponse(e.getConstraintViolations());
        }catch (IOException ex){
           ex.printStackTrace();
        }

       return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
    }
}
