package photolog.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto<T> {
    private boolean status;
    private String message;
    private T data;

    public ResponseDto() { }

    public ResponseDto(boolean status, String message) {
        this.status = status;
        this.message = message;
    }
}