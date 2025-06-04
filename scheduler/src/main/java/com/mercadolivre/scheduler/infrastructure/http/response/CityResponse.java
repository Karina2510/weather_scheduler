package com.mercadolivre.scheduler.infrastructure.http.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JacksonXmlRootElement(localName = "cidade")
public class CityResponse {
    @JacksonXmlProperty(localName = "nome")
    private String name;

    @JacksonXmlProperty(localName = "uf")
    private String uf;

    @JacksonXmlProperty(localName = "atualizacao")
    private LocalDate update;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "previsao")
    private List<PrevisaoResponse> predictions;
} 