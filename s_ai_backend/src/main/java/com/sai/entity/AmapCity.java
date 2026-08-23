package com.sai.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmapCity {
    private String cname;
    private String adcode;
}
