package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskRegisterResponseDTO {
    private long id;
    private long userId;
    private String phrase;
    private String status;

}
