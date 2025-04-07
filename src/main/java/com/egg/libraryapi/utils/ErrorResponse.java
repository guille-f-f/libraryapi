
package com.egg.libraryapi.utils;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String message;
    private int status;
}