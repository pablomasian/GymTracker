package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;
import java.util.stream.Collectors;

import es.udc.fi.dc.fd.model.entities.Like;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;

public class LikeConversor {
    
    public static final LikeDto toLikeDto (Like like){
        return new LikeDto(like.getId(), like.getLiker(), like.getLiked(), like.getSession(), like.getCreatedAt());
    }



    public static final List<LikeDto> toLikeDtos(List<Like> likes) {
        return likes.stream().map(s -> toLikeDto(s)).collect(Collectors.toList());
    }


}
