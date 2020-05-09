package ru.managementtool.ppmtool.exceptions.invalidResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsernameAlreadyExistsResponse {
    private String username;
}
