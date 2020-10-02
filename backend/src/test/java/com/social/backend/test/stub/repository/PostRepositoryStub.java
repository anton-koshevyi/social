package com.social.backend.test.stub.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.PostRepository;
import com.social.backend.test.stub.repository.identification.Identification;

public class PostRepositoryStub
    extends IdentifyingRepositoryStub<Long, Post>
    implements PostRepository {

  public PostRepositoryStub(Identification<Post> identification) {
    super(identification);
  }

  @Override
  protected Long getId(Post entity) {
    return entity.getId();
  }

  @Override
  public Post save(Post entity) {
    return super.save(entity);
  }

  @Override
  public Optional<Post> findById(Long id) {
    Post entity = super.find(id);
    return Optional.ofNullable(entity);
  }

  @Override
  public Optional<Post> findByIdAndAuthor(Long id, User author) {
    Post entity = super.find(
        byId(id)
            .and(byAuthor(author))
    );
    return Optional.ofNullable(entity);
  }

  @Override
  public Page<Post> findAll(Pageable pageable) {
    List<Post> entities = super.findAll();
    return new PageImpl<>(entities);
  }

  @Override
  public Page<Post> findAllByAuthor(User author, Pageable pageable) {
    List<Post> entities = super.findAll().stream()
        .filter(byAuthor(author))
        .collect(Collectors.toList());
    return new PageImpl<>(entities);
  }

  @Override
  public void delete(Post entity) {
    super.delete(entity);
  }

  private static Predicate<Post> byId(Long id) {
    return e -> id.equals(e.getId());
  }

  private static Predicate<Post> byAuthor(User author) {
    return e -> author.getId().equals(e.getAuthor().getId());
  }

}
