package client.rest.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when exception arrives in or occurs in RestAPI
 */
public class RestException extends Exception {
    private HttpStatus statusCode;

    public RestException(HttpStatus statusCode, String message){
        super(message);

        this.statusCode = statusCode;
    }

    @JsonCreator
    public RestException(@JsonProperty("status") int status,
                         @JsonProperty("error") String httpErrorType,
                         @JsonProperty("exception") String exceptionType,
                         @JsonProperty("message") String message,
                         @JsonProperty("path") String requestPath,
                         @JsonProperty("timestamp") Double timestamp) {
        super(message);

        this.statusCode = HttpStatus.valueOf(status);
    }

    public HttpStatus getStatusCode(){
        return statusCode;
    }
}
