package com.social.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.model.post.Post;
import com.social.model.user.User;

public interface PostRepository {

  Post save(Post entity);

  Optional<Post> findById(Long id);

  Optional<Post> findByIdAndAuthor(Long id, User author);

  Page<Post> findAll(Pageable pageable);

  Page<Post> findAllByAuthor(User author, Pageable pageable);

  void delete(Post entity);

}
