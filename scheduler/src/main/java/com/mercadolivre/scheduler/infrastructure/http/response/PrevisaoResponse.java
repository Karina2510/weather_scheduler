package com.mercadolivre.scheduler.infrastructure.http.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PrevisaoResponse {
    @JacksonXmlProperty(localName = "dia")
    private LocalDate dia;

    @JacksonXmlProperty(localName = "tempo")
    private String tempo;

    @JacksonXmlProperty(localName = "maxima")
    private Integer maxima;

    @JacksonXmlProperty(localName = "minima")
    private Integer minima;

    @JacksonXmlProperty(localName = "iuv")
    private Double iuv;
} 