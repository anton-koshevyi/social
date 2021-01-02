package com.social.repository.jpa;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social.model.post.Post;
import com.social.model.user.User;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

  Optional<Post> findByIdAndAuthor(Long id, User author);

  Page<Post> findAllByAuthor(User author, Pageable pageable);

}
