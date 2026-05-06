package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.repository.comment.CommentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("commentSecurity")
@RequiredArgsConstructor
public class CommentSecurity {
    private final CommentRepository commentRepository;

    public boolean isOwner(Authentication authentication, Long commentId) {
        return commentRepository.existsByIdAndUser_Username(commentId, authentication.getName());
    }
}
