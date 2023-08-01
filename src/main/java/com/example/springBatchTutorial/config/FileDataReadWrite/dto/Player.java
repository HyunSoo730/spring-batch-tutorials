package com.example.springBatchTutorial.config.FileDataReadWrite.dto;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    private String Id;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;

}
