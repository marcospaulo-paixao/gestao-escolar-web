package br.dev.mpp.web;

import br.dev.mpp.modelo.EntidadeBase;
import br.dev.mpp.persistencia.Dao;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.DateTimeException;

/**
 * Fluxo comum dos cadastros:
 * <ul>
 *     <li>GET: lista os registros; com ?editar=ID carrega o registro no formulário</li>
 *     <li>POST: grava (insere ou atualiza) ou, com acao=excluir, remove o registro</li>
 * </ul>
 * Depois de gravar/excluir redireciona para a listagem (Post/Redirect/Get).
 */
public abstract class CrudServlet<T extends EntidadeBase> extends HttpServlet {

    protected final Dao<T> dao;
    private final String pagina;

    /**
     * @param pagina nome da página em /WEB-INF/paginas, sem extensão
     */
    protected CrudServlet(Dao<T> dao, String pagina) {
        this.dao = dao;
        this.pagina = pagina;
    }

    /** Cria uma entidade vazia para receber os dados do formulário. */
    protected abstract T nova();

    /** Copia os parâmetros do formulário para a entidade. Pode lançar {@link ValidacaoException}. */
    protected abstract void preencher(T entidade, HttpServletRequest request);

    /** Coloca no request o que o formulário precisar além do registro (opções de selects etc.). */
    protected void prepararFormulario(HttpServletRequest request) {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String editar = request.getParameter("editar");
        T item = editar == null ? null : dao.buscar(Long.valueOf(editar));
        mostrar(request, response, item);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("excluir".equals(request.getParameter("acao"))) {
            excluir(request, response);
        } else {
            salvar(request, response);
        }
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        T item = nova();
        item.setId(idDoFormulario(request));
        try {
            preencher(item, request);
            dao.salvar(item);
        } catch (ValidacaoException e) {
            request.setAttribute("erro", e.getMessage());
            mostrar(request, response, item);
            return;
        } catch (IllegalArgumentException | DateTimeException e) {
            request.setAttribute("erro", "Dados inválidos. Confira os campos do formulário.");
            mostrar(request, response, item);
            return;
        } catch (PersistenceException e) {
            request.setAttribute("erro", "Não foi possível gravar. Verifique se o código já está cadastrado.");
            mostrar(request, response, item);
            return;
        }
        redirecionar(request, response, "Registro gravado com sucesso.");
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            dao.excluir(idDoFormulario(request));
        } catch (PersistenceException e) {
            request.setAttribute("erro", "Não foi possível excluir: o registro está sendo usado em outro cadastro.");
            mostrar(request, response, null);
            return;
        }
        redirecionar(request, response, "Registro excluído com sucesso.");
    }

    private void mostrar(HttpServletRequest request, HttpServletResponse response, T item)
            throws ServletException, IOException {
        request.setAttribute("item", item);
        request.setAttribute("lista", dao.listar());
        prepararFormulario(request);
        request.getRequestDispatcher("/WEB-INF/paginas/" + pagina + ".jspx").forward(request, response);
    }

    /** A mensagem fica na sessão e o layout a exibe uma única vez, depois do redirect. */
    private void redirecionar(HttpServletRequest request, HttpServletResponse response, String mensagem)
            throws IOException {
        request.getSession().setAttribute("sucesso", mensagem);
        response.sendRedirect(request.getContextPath() + request.getServletPath());
    }

    private Long idDoFormulario(HttpServletRequest request) {
        String id = request.getParameter("id");
        return id == null || id.isBlank() ? null : Long.valueOf(id);
    }
}
