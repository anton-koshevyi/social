package com.social.backend.service;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.PostRepository;
import com.social.backend.util.NullableUtil;

@Service
@Transactional
public class PostServiceImpl implements PostService {

  private final PostRepository repository;

  @Autowired
  public PostServiceImpl(PostRepository repository) {
    this.repository = repository;
  }

  @Override
  public Post create(User author, String title, String body) {
    Post entity = new Post();
    entity.setTitle(title);
    entity.setBody(body);
    entity.setAuthor(author);
    return repository.save(entity);
  }

  @Override
  public Post update(Long id, User author, String title, String body) {
    Post entity = findByIdAndAuthor(id, author);
    entity.setUpdatedAt(ZonedDateTime.now());
    NullableUtil.set(entity::setTitle, title);
    NullableUtil.set(entity::setBody, body);
    return repository.save(entity);
  }

  @Override
  public void delete(Long id, User author) {
    Post entity = findByIdAndAuthor(id, author);
    repository.delete(entity);
  }

  @Override
  public Post find(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new NotFoundException("notFound.post.byId", id));
  }

  @Override
  public Page<Post> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  @Override
  public Page<Post> findAll(User author, Pageable pageable) {
    return repository.findAllByAuthor(author, pageable);
  }

  private Post findByIdAndAuthor(Long id, User author) {
    return repository.findByIdAndAuthor(id, author)
        .orElseThrow(() -> new NotFoundException(
            "notFound.post.byIdAndAuthor", id, author.getId()));
  }

}
