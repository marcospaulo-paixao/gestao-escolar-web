package br.dev.mpp.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

/**
 * Estudante matriculado em uma turma. Um estudante só pode ter uma matrícula por turma.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"turma_id", "estudante_id"}))
public class Matricula extends EntidadeBase {

    @ManyToOne(optional = false)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudante_id", nullable = false)
    private Estudante estudante;

    @Column(nullable = false)
    private LocalDate dataMatricula;

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public void setEstudante(Estudante estudante) {
        this.estudante = estudante;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    /** Data no formato dd/MM/yyyy, para exibição na tabela. */
    public String getDataMatriculaFormatada() {
        return Datas.formatar(dataMatricula);
    }
}
