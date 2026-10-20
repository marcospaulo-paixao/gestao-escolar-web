package br.dev.mpp.persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Cria a fábrica de EntityManager quando a aplicação sobe e fecha quando ela para.
 */
@WebListener
public class JPAUtil implements ServletContextListener {

    private static EntityManagerFactory fabrica;

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        fabrica = Persistence.createEntityManagerFactory("gestao-escolar");
    }

    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        if (fabrica != null) {
            fabrica.close();
        }
    }

    public static EntityManager criarEntityManager() {
        return fabrica.createEntityManager();
    }
}
