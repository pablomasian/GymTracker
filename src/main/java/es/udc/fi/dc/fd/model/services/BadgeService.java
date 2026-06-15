package es.udc.fi.dc.fd.model.services;

import java.util.List;

import es.udc.fi.dc.fd.model.entities.Badge;
import es.udc.fi.dc.fd.model.entities.Badge.BadgeType;

public interface BadgeService {
    

    public void giveBadge (long userId, BadgeType type);

    public boolean hasBadge (long userId, BadgeType type);

    public List<Badge> getBadgesByUser(long userId);
}
