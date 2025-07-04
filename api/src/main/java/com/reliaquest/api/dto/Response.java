package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Response<T>(T data, Status status, String error) {

    public static <T> Response<T> handled() {
        return new Response<>(null, Status.HANDLED, null);
    }

    public static <T> Response<T> handledWith(T data) {
        return new Response<>(data, Status.HANDLED, null);
    }

    public static <T> Response<T> error(String error) {
        return new Response<>(null, Status.ERROR, error);
    }

    @Getter
    public enum Status {
        HANDLED("Successfully processed request."),
        ERROR("Failed to process request.");

        @JsonValue
        private final String value;

        Status(String value) {
            this.value = value;
        }
    }
}
