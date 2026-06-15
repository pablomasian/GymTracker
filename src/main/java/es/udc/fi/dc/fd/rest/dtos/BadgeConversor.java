package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;
import java.util.stream.Collectors;

import es.udc.fi.dc.fd.model.entities.Badge;

public class BadgeConversor {
    
    public static final BadgeDto toBadgeDto (Badge Badge){
        return new BadgeDto(Badge.getId(), Badge.getUser(), Badge.getDate(), Badge.getType(), Badge.getDescription(), Badge.getIconUrl());
    }



    public static final List<BadgeDto> toBadgeDtos(List<Badge> Badges) {
        return Badges.stream().map(s -> toBadgeDto(s)).collect(Collectors.toList());
    }



    } 
