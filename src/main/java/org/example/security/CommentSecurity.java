package org.example.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.comment.CommentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("commentSecurity")
@RequiredArgsConstructor
@Slf4j
public class CommentSecurity {
    private final CommentRepository commentRepository;

    public boolean isOwner(Authentication authentication, Long commentId) {
        boolean result = commentRepository.existsByIdAndUser_Username(commentId, authentication.getName());
        log.info("isOwner check: commentId={}, username={}, result={}",
                commentId, authentication.getName(), result);
        return result;
    }
}
