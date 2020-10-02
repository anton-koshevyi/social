package com.social.backend.test.stub.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.CommentRepository;
import com.social.backend.test.stub.repository.identification.Identification;

public class CommentRepositoryStub
    extends IdentifyingRepositoryStub<Long, Comment>
    implements CommentRepository {

  public CommentRepositoryStub(Identification<Comment> identification) {
    super(identification);
  }

  @Override
  protected Long getId(Comment entity) {
    return entity.getId();
  }

  @Override
  public Comment save(Comment entity) {
    return super.save(entity);
  }

  @Override
  public Optional<Comment> findByIdAndAuthor(Long id, User author) {
    Comment entity = super.find(
        byId(id)
            .and(byAuthor(author))
    );
    return Optional.ofNullable(entity);
  }

  @Override
  public Page<Comment> findAllByPost(Post post, Pageable pageable) {
    List<Comment> entities = super.findAll().stream()
        .filter(byPost(post))
        .collect(Collectors.toList());
    return new PageImpl<>(entities);
  }

  @Override
  public void delete(Comment entity) {
    super.delete(entity);
  }

  private static Predicate<Comment> byId(Long id) {
    return e -> id.equals(e.getId());
  }

  private static Predicate<Comment> byAuthor(User author) {
    return e -> author.getId().equals(e.getAuthor().getId());
  }

  private static Predicate<Comment> byPost(Post post) {
    return e -> post.getId().equals(e.getPost().getId());
  }

}
