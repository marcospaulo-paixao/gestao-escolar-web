package br.dev.mpp.web;

import br.dev.mpp.modelo.Curso;
import br.dev.mpp.modelo.Nivel;
import br.dev.mpp.persistencia.Dao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/cursos")
public class CursoServlet extends CrudServlet<Curso> {

    public CursoServlet() {
        super(new Dao<>(Curso.class, "codigo"), "cursos");
    }

    @Override
    protected Curso nova() {
        return new Curso();
    }

    @Override
    protected void preencher(Curso curso, HttpServletRequest request) {
        curso.setCodigo(request.getParameter("codigo"));
        curso.setNome(request.getParameter("nome"));
        curso.setCargaHoraria(Integer.valueOf(request.getParameter("cargaHoraria")));
        curso.setNivel(Nivel.valueOf(request.getParameter("nivel")));
    }

    @Override
    protected void prepararFormulario(HttpServletRequest request) {
        request.setAttribute("niveis", Nivel.values());
    }
}
