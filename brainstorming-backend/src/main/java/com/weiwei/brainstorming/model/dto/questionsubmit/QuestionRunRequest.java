package com.weiwei.brainstorming.model.dto.questionsubmit;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class QuestionRunRequest {
    @NotBlank
    private String code;
    private String input;
    @NotBlank
    private String language;
}
