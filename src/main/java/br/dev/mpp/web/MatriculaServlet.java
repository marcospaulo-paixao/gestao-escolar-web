package br.dev.mpp.web;

import br.dev.mpp.modelo.Estudante;
import br.dev.mpp.modelo.Matricula;
import br.dev.mpp.modelo.Turma;
import br.dev.mpp.persistencia.Dao;
import br.dev.mpp.persistencia.MatriculaDao;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Matrículas de uma turma:
 * <ul>
 *     <li>GET ?turma=ID: lista os matriculados e os estudantes disponíveis</li>
 *     <li>POST acao=matricular (turma, estudante) ou acao=cancelar (turma, id da matrícula)</li>
 * </ul>
 */
@WebServlet("/matriculas")
public class MatriculaServlet extends HttpServlet {

    private final MatriculaDao matriculaDao = new MatriculaDao();
    private final Dao<Turma> turmaDao = new Dao<>(Turma.class, "codigo");
    private final Dao<Estudante> estudanteDao = new Dao<>(Estudante.class, "nome");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Turma turma = turmaDoRequest(request);
        if (turma == null) {
            response.sendRedirect(request.getContextPath() + "/turmas");
            return;
        }
        mostrar(request, response, turma);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Turma turma = turmaDoRequest(request);
        if (turma == null) {
            response.sendRedirect(request.getContextPath() + "/turmas");
            return;
        }

        String mensagem;
        try {
            if ("cancelar".equals(request.getParameter("acao"))) {
                matriculaDao.excluir(Long.valueOf(request.getParameter("id")));
                mensagem = "Matrícula cancelada com sucesso.";
            } else {
                matricular(turma, request.getParameter("estudante"));
                mensagem = "Estudante matriculado com sucesso.";
            }
        } catch (ValidacaoException e) {
            request.setAttribute("erro", e.getMessage());
            mostrar(request, response, turma);
            return;
        } catch (PersistenceException e) {
            request.setAttribute("erro", "Não foi possível concluir a operação. O estudante já pode estar matriculado.");
            mostrar(request, response, turma);
            return;
        }

        request.getSession().setAttribute("sucesso", mensagem);
        response.sendRedirect(request.getContextPath() + "/matriculas?turma=" + turma.getId());
    }

    private void matricular(Turma turma, String estudanteId) {
        if (estudanteId == null || estudanteId.isBlank()) {
            throw new ValidacaoException("Selecione um estudante.");
        }
        if (matriculaDao.contarPorTurma(turma.getId()) >= turma.getVagas()) {
            throw new ValidacaoException("A turma não tem mais vagas.");
        }
        Estudante estudante = estudanteDao.buscar(Long.valueOf(estudanteId));
        if (estudante == null) {
            throw new ValidacaoException("Estudante não encontrado.");
        }

        Matricula matricula = new Matricula();
        matricula.setTurma(turma);
        matricula.setEstudante(estudante);
        matricula.setDataMatricula(LocalDate.now());
        matriculaDao.salvar(matricula);
    }

    private void mostrar(HttpServletRequest request, HttpServletResponse response, Turma turma)
            throws ServletException, IOException {
        long ocupadas = matriculaDao.contarPorTurma(turma.getId());
        request.setAttribute("turma", turma);
        request.setAttribute("matriculas", matriculaDao.listarPorTurma(turma.getId()));
        request.setAttribute("disponiveis", matriculaDao.estudantesSemMatricula(turma.getId()));
        request.setAttribute("ocupadas", ocupadas);
        request.setAttribute("lotada", ocupadas >= turma.getVagas());
        request.getRequestDispatcher("/WEB-INF/paginas/matriculas.jspx").forward(request, response);
    }

    private Turma turmaDoRequest(HttpServletRequest request) {
        String id = request.getParameter("turma");
        return id == null || id.isBlank() ? null : turmaDao.buscar(Long.valueOf(id));
    }
}
