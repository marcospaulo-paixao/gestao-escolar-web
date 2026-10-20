package br.dev.mpp.modelo;

public enum Turno {

    MANHA("MANHÃ"),
    TARDE("TARDE"),
    NOITE("NOITE");

    private final String descricao;

    Turno(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
