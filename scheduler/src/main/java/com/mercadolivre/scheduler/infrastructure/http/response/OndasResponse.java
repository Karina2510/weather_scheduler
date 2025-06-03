package com.mercadolivre.scheduler.infrastructure.http.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
@JacksonXmlRootElement(localName = "cidade")
public class OndasResponse {
    private String nome;
    private String uf;
    
    private String atualizacao;
    
    private PeriodoResponse manha;
    private PeriodoResponse tarde;
    private PeriodoResponse noite;

    @Data
    public static class PeriodoResponse {
        @JsonFormat(pattern = "dd-MM-yyyy HH'h' Z")
        private String dia;
        private String agitacao;
        
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private Double altura;
        
        private String direcao;
        
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private Double vento;
        
        private String vento_dir;

        @JsonSetter("altura")
        public void setAltura(String altura) {
            if (altura == null || "undefined".equals(altura)) {
                this.altura = null;
            } else {
                try {
                    this.altura = Double.parseDouble(altura);
                } catch (NumberFormatException e) {
                    this.altura = null;
                }
            }
        }

        @JsonSetter("vento")
        public void setVento(String vento) {
            if (vento == null || "undefined".equals(vento)) {
                this.vento = null;
            } else {
                try {
                    this.vento = Double.parseDouble(vento);
                } catch (NumberFormatException e) {
                    this.vento = null;
                }
            }
        }
    }

    public boolean hasValidData() {
        return !"undefined".equals(nome) && 
               !"undefined".equals(uf) && 
               !isInvalidDate(atualizacao) &&
               isValidPeriod(manha) && 
               isValidPeriod(tarde) && 
               isValidPeriod(noite);
    }

    private boolean isValidPeriod(PeriodoResponse periodo) {
        if (periodo == null) return false;
        return !"undefined".equals(periodo.getAgitacao()) &&
               periodo.getAltura() != null &&
               !"undefined".equals(periodo.getDirecao()) &&
               periodo.getVento() != null &&
               !"undefined".equals(periodo.getVento_dir()) &&
               !isInvalidDate(periodo.getDia());
    }

    private boolean isInvalidDate(String date) {
        return date == null || 
               date.startsWith("00/00/0000") || 
               "undefined".equals(date);
    }

    public LocalDate getAtualizacaoAsLocalDate() {
        if (isInvalidDate(atualizacao)) {
            return LocalDate.now();
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return LocalDateTime.parse(atualizacao, formatter).toLocalDate();
        } catch (DateTimeParseException e) {
            return LocalDate.now();
        }
    }
} 