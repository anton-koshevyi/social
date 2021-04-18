package com.social.service;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.social.exception.NotFoundException;
import com.social.model.post.Post;
import com.social.model.user.User;
import com.social.repository.PostRepository;
import com.social.util.NullableUtils;

@Service
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;

  @Autowired
  public PostServiceImpl(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Transactional
  @Override
  public Post create(User author, String title, String body) {
    Post entity = new Post();
    entity.setTitle(title);
    entity.setBody(body);
    entity.setAuthor(author);
    return postRepository.save(entity);
  }

  @Transactional
  @Override
  public Post update(Long id, User author, String title, String body) {
    Post entity = findByIdAndAuthor(id, author);
    entity.setUpdatedAt(ZonedDateTime.now());
    NullableUtils.set(entity::setTitle, title);
    NullableUtils.set(entity::setBody, body);
    return postRepository.save(entity);
  }

  @Transactional
  @Override
  public void delete(Long id, User author) {
    Post entity = findByIdAndAuthor(id, author);
    postRepository.delete(entity);
  }

  @Override
  public Post find(Long id) {
    return postRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("notFound.post.byId", id));
  }

  @Override
  public Page<Post> findAll(Pageable pageable) {
    return postRepository.findAll(pageable);
  }

  @Override
  public Page<Post> findAll(User author, Pageable pageable) {
    return postRepository.findAllByAuthor(author, pageable);
  }

  private Post findByIdAndAuthor(Long id, User author) {
    return postRepository.findByIdAndAuthor(id, author)
        .orElseThrow(() -> new NotFoundException(
            "notFound.post.byIdAndAuthor", id, author.getId()));
  }

}
