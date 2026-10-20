package br.dev.mpp.persistencia;

import br.dev.mpp.modelo.Estudante;
import br.dev.mpp.modelo.Matricula;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Consultas de matrícula feitas por turma.
 */
public class MatriculaDao extends Dao<Matricula> {

    public MatriculaDao() {
        super(Matricula.class, "id");
    }

    public List<Matricula> listarPorTurma(Long turmaId) {
        try (EntityManager em = JPAUtil.criarEntityManager()) {
            return em.createQuery("from Matricula m where m.turma.id = :turma order by m.estudante.nome", Matricula.class)
                    .setParameter("turma", turmaId)
                    .getResultList();
        }
    }

    public long contarPorTurma(Long turmaId) {
        try (EntityManager em = JPAUtil.criarEntityManager()) {
            return em.createQuery("select count(m) from Matricula m where m.turma.id = :turma", Long.class)
                    .setParameter("turma", turmaId)
                    .getSingleResult();
        }
    }

    /** Quantidade de matrículas de cada turma, indexada pelo id da turma. Turmas sem matrícula não aparecem. */
    public Map<Long, Long> contarPorTurma() {
        try (EntityManager em = JPAUtil.criarEntityManager()) {
            return em.createQuery("select m.turma.id, count(m) from Matricula m group by m.turma.id", Object[].class)
                    .getResultStream()
                    .collect(Collectors.toMap(linha -> (Long) linha[0], linha -> (Long) linha[1]));
        }
    }

    /** Estudantes que ainda não estão matriculados na turma. */
    public List<Estudante> estudantesSemMatricula(Long turmaId) {
        try (EntityManager em = JPAUtil.criarEntityManager()) {
            return em.createQuery("""
                            from Estudante e
                            where e.id not in (select m.estudante.id from Matricula m where m.turma.id = :turma)
                            order by e.nome""", Estudante.class)
                    .setParameter("turma", turmaId)
                    .getResultList();
        }
    }
}
