package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class TaskEntity {
    private long id;
    private long userId;
    private String phrase;
    private String status;

}
