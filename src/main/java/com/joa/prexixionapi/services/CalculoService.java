package com.joa.prexixionapi.services;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class CalculoService {

    /**
     * Calcula la fecha final de depreciación basada en la fecha de inicio y el porcentaje anual.
     * Réplica exacta de DateUtils.depreciacionesCalcularFechaFinal del proyecto legacy.
     */
    public String calcularFechaFinal(String fechaInicio, double porcentaje) {
        if (fechaInicio == null || fechaInicio.isEmpty() || porcentaje == 0) {
            return fechaInicio;
        }

        try {
            LocalDate initialDate = LocalDate.parse(fechaInicio);
            double cien = 100.00;
            
            // Lógica legacy:
            // int years = (int) (100 / percentage);
            // int months = (int) (12 * ((100 / percentage) - years));
            // int days = (int) Math.round(30 * ((12 * ((100 / percentage) - years)) - months));
            
            double ratio = cien / porcentaje;
            int years = (int) ratio;
            double remainingRatio = ratio - years;
            
            int months = (int) (12 * remainingRatio);
            double remainingMonths = (12 * remainingRatio) - months;
            
            int days = (int) Math.round(30 * remainingMonths);

            LocalDate finalDate = initialDate.plusYears(years).plusMonths(months).plusDays(days);
            return finalDate.toString();

        } catch (DateTimeParseException e) {
            return fechaInicio;
        }
    }
}
