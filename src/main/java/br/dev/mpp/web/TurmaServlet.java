package br.dev.mpp.web;

import br.dev.mpp.modelo.Curso;
import br.dev.mpp.modelo.Turma;
import br.dev.mpp.modelo.Turno;
import br.dev.mpp.persistencia.Dao;
import br.dev.mpp.persistencia.MatriculaDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;

@WebServlet("/turmas")
public class TurmaServlet extends CrudServlet<Turma> {

    private final Dao<Curso> cursoDao = new Dao<>(Curso.class, "nome");
    private final MatriculaDao matriculaDao = new MatriculaDao();

    public TurmaServlet() {
        super(new Dao<>(Turma.class, "codigo"), "turmas");
    }

    @Override
    protected Turma nova() {
        return new Turma();
    }

    @Override
    protected void preencher(Turma turma, HttpServletRequest request) {
        turma.setCodigo(request.getParameter("codigo"));
        turma.setCurso(cursoDao.buscar(Long.valueOf(request.getParameter("curso"))));
        turma.setTurno(Turno.valueOf(request.getParameter("turno")));
        turma.setDataInicio(LocalDate.parse(request.getParameter("dataInicio")));
        turma.setVagas(Integer.valueOf(request.getParameter("vagas")));

        if (turma.getId() != null) {
            long matriculados = matriculaDao.contarPorTurma(turma.getId());
            if (turma.getVagas() < matriculados) {
                throw new ValidacaoException("A turma já tem " + matriculados
                        + " estudantes matriculados. As vagas não podem ser menores que isso.");
            }
        }
    }

    @Override
    protected void prepararFormulario(HttpServletRequest request) {
        request.setAttribute("cursos", cursoDao.listar());
        request.setAttribute("turnos", Turno.values());
        request.setAttribute("ocupadas", matriculaDao.contarPorTurma());
    }
}
