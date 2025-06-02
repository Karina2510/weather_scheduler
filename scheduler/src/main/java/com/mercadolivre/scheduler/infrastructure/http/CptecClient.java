package com.mercadolivre.scheduler.infrastructure.http;

import com.mercadolivre.scheduler.infrastructure.http.response.CidadeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "cptec",
    url = "${cptec.api.url:http://servicos.cptec.inpe.br/XML}"
)
public interface CptecClient {
    
    @GetMapping(value = "/cidade/{cityId}/previsao.xml", produces = "application/xml;charset=ISO-8859-1")
    CidadeResponse getTemperature(@PathVariable String cityId);
} 