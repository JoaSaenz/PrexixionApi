package com.joa.prexixionapi.common.utils;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryUtils {

    public static void appendIntFilter(StringBuilder sql, MapSqlParameterSource params, String col, List<Integer> list, String param) {
        if (list == null || list.isEmpty()) return;
        List<Integer> nonZero = new ArrayList<>();
        boolean includeZero = false;
        for (Integer v : list) {
            if (v == null) continue;
            if (v == 0) includeZero = true;
            else nonZero.add(v);
        }
        if (!includeZero && nonZero.isEmpty()) return;

        sql.append(" AND (");
        if (!nonZero.isEmpty()) {
            sql.append(col).append(" IN (:").append(param).append(")");
            params.addValue(param, nonZero);
            if (includeZero) sql.append(" OR ");
        }
        if (includeZero) {
            sql.append("(").append(col).append(" IS NULL OR ").append(col).append(" = 0)");
        }
        sql.append(")");
    }

    public static void appendStringFilter(StringBuilder sql, MapSqlParameterSource params, String col, List<String> list, String param) {
        if (list == null || list.isEmpty()) return;
        sql.append(" AND ").append(col).append(" IN (:").append(param).append(")");
        params.addValue(param, list);
    }

    public static void appendIntFilter(StringBuilder sql, MapSqlParameterSource params, String col, String csv,
            String param) {
        if (csv == null || csv.trim().isEmpty())
            return;
        List<Integer> nonZero = new ArrayList<>();
        boolean includeZero = false;
        for (String part : csv.split(",")) {
            String t = part.trim();
            if (t.isEmpty())
                continue;
            int v = Integer.parseInt(t);
            if (v == 0)
                includeZero = true;
            else
                nonZero.add(v);
        }
        if (!includeZero && nonZero.isEmpty())
            return;

        sql.append(" AND (");
        if (!nonZero.isEmpty()) {
            sql.append(col).append(" IN (:").append(param).append(")");
            params.addValue(param, nonZero);
            if (includeZero)
                sql.append(" OR ");
        }
        if (includeZero) {
            sql.append("(").append(col).append(" IS NULL OR ").append(col).append(" = 0)");
        }
        sql.append(")");
    }

    public static void appendStringFilter(StringBuilder sql, MapSqlParameterSource params, String col, String csv,
            String param) {
        if (csv == null || csv.trim().isEmpty())
            return;
        List<String> list = Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        if (!list.isEmpty()) {
            sql.append(" AND ").append(col).append(" IN (:").append(param).append(")");
            params.addValue(param, list);
        }
    }
}
