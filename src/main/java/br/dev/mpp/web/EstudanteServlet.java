package br.dev.mpp.web;

import br.dev.mpp.modelo.Estudante;
import br.dev.mpp.persistencia.Dao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;

@WebServlet("/estudantes")
public class EstudanteServlet extends CrudServlet<Estudante> {

    public EstudanteServlet() {
        super(new Dao<>(Estudante.class, "nome"), "estudantes");
    }

    @Override
    protected Estudante nova() {
        return new Estudante();
    }

    @Override
    protected void preencher(Estudante estudante, HttpServletRequest request) {
        estudante.setMatricula(request.getParameter("matricula"));
        estudante.setNome(request.getParameter("nome"));
        estudante.setEmail(request.getParameter("email"));
        estudante.setDataNascimento(LocalDate.parse(request.getParameter("dataNascimento")));
    }
}
