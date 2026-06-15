package es.udc.fi.dc.fd.model.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.udc.fi.dc.fd.model.entities.Badge;
import es.udc.fi.dc.fd.model.entities.Badge.BadgeType;
import jakarta.transaction.Transactional;
import es.udc.fi.dc.fd.model.entities.BadgeDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;


@Service
@Transactional
public class BadgeServiceImplementation implements BadgeService {
    

    @Autowired
    private BadgeDao badgeDao;

    @Autowired
    private UserDao userDao;

    @Override
    public void giveBadge(long userId, BadgeType type) {

        User user = userDao.getById(userId);

        Badge badge;

        switch (type) {
            case HUNDRED -> badge = new Badge(user, LocalDateTime.now(), BadgeType.HUNDRED);
            case FIFTY_WORKOUTS -> badge = new Badge(user, LocalDateTime.now(), BadgeType.FIFTY_WORKOUTS);
            case EARLY_BIRD -> badge = new Badge(user, LocalDateTime.now(), BadgeType.EARLY_BIRD);
            case VOLUME_KING -> badge = new Badge(user, LocalDateTime.now(), BadgeType.VOLUME_KING);
            case CONSISTENCY_CHAMPION -> badge = new Badge(user, LocalDateTime.now(), BadgeType.CONSISTENCY_CHAMPION);
            case OTHER   -> badge = new Badge(user, LocalDateTime.now(), BadgeType.OTHER);
            default      -> throw new IllegalArgumentException("Unsupported badge type: " + type);
        }

        badgeDao.save(badge);
    }

    @Override
    public List<Badge> getBadgesByUser(long userId){

        return badgeDao.findByUserId(userId);
    }

    @Override
    public boolean hasBadge (long userId, BadgeType type){

        return badgeDao.existsByUserIdAndType(userId, type);

    }

}
