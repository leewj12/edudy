package com.kosmo.backend.consult.dto;

import com.kosmo.backend.consult.ConsultKeyword;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeywordCountResponse {
    private ConsultKeyword keyword;
    private Long count;
}