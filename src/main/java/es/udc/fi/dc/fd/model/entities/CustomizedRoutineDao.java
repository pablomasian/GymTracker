package es.udc.fi.dc.fd.model.entities;

import java.util.List;

public interface CustomizedRoutineDao {
    /*PRIMERA ENTREGA: solo implementada busca por nombre, no por autor */
    
    List<Routine> findRoutineByKeywordsName (String keywords);
}
