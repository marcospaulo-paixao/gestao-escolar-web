package br.dev.mpp.modelo;

public enum Nivel {

    BASICO("BÁSICO"),
    INTERMEDIARIO("INTERMEDIÁRIO"),
    AVANCADO("AVANÇADO");

    private final String descricao;

    Nivel(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
