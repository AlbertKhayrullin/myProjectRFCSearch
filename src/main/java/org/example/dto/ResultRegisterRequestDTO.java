package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResultRegisterRequestDTO {
    private long id;
    private long taskId;
    private String file;
    private int numberLine;
    private String line;
}
