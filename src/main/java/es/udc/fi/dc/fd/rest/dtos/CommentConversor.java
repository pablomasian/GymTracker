package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;
import java.util.stream.Collectors;

import es.udc.fi.dc.fd.model.entities.Comment;

public class CommentConversor {
    
    

    public static final CommentDto toCommentDto (Comment comment){
        return new CommentDto(comment.getId(), comment.getSession(), comment.getCommenter(), comment.getCommented(), comment.getText(), comment.getCreatedAt());
    }



    public static final List<CommentDto> toCommentDtos(List<Comment> Comments) {
        return Comments.stream().map(s -> toCommentDto(s)).collect(Collectors.toList());
    }


}
