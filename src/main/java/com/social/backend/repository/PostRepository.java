package com.social.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public interface PostRepository {

  Post save(Post entity);

  Optional<Post> findById(Long id);

  Optional<Post> findByIdAndAuthor(Long id, User author);

  Page<Post> findAll(Pageable pageable);

  Page<Post> findAllByAuthor(User author, Pageable pageable);

  void delete(Post entity);

}
