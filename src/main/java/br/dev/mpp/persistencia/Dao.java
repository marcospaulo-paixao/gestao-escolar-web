package br.dev.mpp.persistencia;

import br.dev.mpp.modelo.EntidadeBase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.function.Consumer;

/**
 * Operações de CRUD genéricas. Cada chamada abre e fecha o próprio EntityManager.
 */
public class Dao<T extends EntidadeBase> {

    private final Class<T> classe;
    private final String ordem;

    /**
     * @param ordem atributo usado no "order by" da listagem
     */
    public Dao(Class<T> classe, String ordem) {
        this.classe = classe;
        this.ordem = ordem;
    }

    public List<T> listar() {
        try (EntityManager em = JPAUtil.criarEntityManager()) {
            String jpql = "from " + classe.getSimpleName() + " order by " + ordem;
            return em.createQuery(jpql, classe).getResultList();
        }
    }

    public T buscar(Long id) {
        try (EntityManager em = JPAUtil.criarEntityManager()) {
            return em.find(classe, id);
        }
    }

    /** Insere quando o id é nulo, atualiza caso contrário. */
    public void salvar(T entidade) {
        emTransacao(em -> em.merge(entidade));
    }

    public void excluir(Long id) {
        emTransacao(em -> {
            T entidade = em.find(classe, id);
            if (entidade != null) {
                em.remove(entidade);
            }
        });
    }

    private void emTransacao(Consumer<EntityManager> operacao) {
        try (EntityManager em = JPAUtil.criarEntityManager()) {
            EntityTransaction transacao = em.getTransaction();
            try {
                transacao.begin();
                operacao.accept(em);
                transacao.commit();
            } catch (RuntimeException e) {
                if (transacao.isActive()) {
                    transacao.rollback();
                }
                throw e;
            }
        }
    }
}
