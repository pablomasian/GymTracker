package es.udc.fi.dc.fd.model.services;

import java.util.List;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.SetLog;


public interface SetLogService {
    List <SetLog> getSetsOfSession (Long sessionId);
}
