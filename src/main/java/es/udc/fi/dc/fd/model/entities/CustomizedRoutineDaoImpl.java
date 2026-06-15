package es.udc.fi.dc.fd.model.entities;

import java.util.List;



import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class CustomizedRoutineDaoImpl implements CustomizedRoutineDao {

    
    @PersistenceContext
    private EntityManager entityManager;
    
    private String[] getTokens(String keywords) {

        if (keywords == null || keywords.length() == 0) {
            return new String[0];
        } else {
            return keywords.split("\\s");
        }

    }

    @Override 
    public List<Routine> findRoutineByKeywordsName (String keywords){

        String[] tokens = getTokens(keywords);
        String queryString = "SELECT r FROM Routine r WHERE 1=1";


        if (tokens.length != 0) {

            queryString += " AND ";


            for (int i = 0; i<tokens.length-1; i++) {
                queryString += "LOWER(r.nombre_rutina) LIKE LOWER(:token" + i + ") AND ";
            }

            queryString += "LOWER(r.nombre_rutina) LIKE LOWER(:token" + (tokens.length-1) + ")";

        }
        queryString += "ORDER BY r.nombre_rutina";

        Query query = entityManager.createQuery(queryString);
        List<Routine> routines = query.getResultList();
        return routines;
    }
}
