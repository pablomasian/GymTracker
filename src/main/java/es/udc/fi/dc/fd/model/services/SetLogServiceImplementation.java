package es.udc.fi.dc.fd.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.SetLog;
import es.udc.fi.dc.fd.model.entities.SetLogDao;

import es.udc.fi.dc.fd.model.services.SetLogService;;

@Service
public class SetLogServiceImplementation implements SetLogService{


    @Autowired
    private SetLogDao setLogDao;

    @Override
    public List <SetLog> getSetsOfSession (Long sessionId)  {
        List <SetLog> sets = setLogDao.findBySessionId(sessionId);

        return sets;
    }
}
