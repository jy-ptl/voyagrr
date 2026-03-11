package com.voyagrr.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int statusCode;
    private String message;
    private Object error;

    public ErrorResponse(String message) {
        super();
        this.message = message;
    }

}
