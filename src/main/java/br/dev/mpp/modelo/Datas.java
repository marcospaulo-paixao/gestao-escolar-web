package br.dev.mpp.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

final class Datas {

    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Datas() {
    }

    static String formatar(LocalDate data) {
        return data == null ? "" : data.format(FORMATO_BR);
    }
}
