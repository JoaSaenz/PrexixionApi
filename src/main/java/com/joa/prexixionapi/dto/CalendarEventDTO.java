package com.joa.prexixionapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarEventDTO {
    private String title;
    private String start;
    private String end;
    private String attendee;
    private String topic;
    private Integer state;
    private Integer stateFiscalizacion;
    private Integer stateFiscalizacionPay;
    private Integer stateTramitesSunat;
    private String stateDescripcion;
    private String color;
    private String flagReunion;
    private String flagFiscalizacion;
    private String type;
    private Boolean allDay;
}
