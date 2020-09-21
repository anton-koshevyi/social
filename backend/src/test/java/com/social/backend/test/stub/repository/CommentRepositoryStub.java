package com.social.backend.test.stub.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    Comment entity = super.find(comment ->
        Objects.equals(comment.getId(), id)
            && Objects.equals(comment.getAuthor(), author));
    return Optional.ofNullable(entity);
  }

  @Override
  public Page<Comment> findAllByPost(Post post, Pageable pageable) {
    List<Comment> entities = super.findAll().stream()
        .filter(comment -> Objects.equals(post, comment.getPost()))
        .collect(Collectors.toList());
    return new PageImpl<>(entities);
  }

  @Override
  public void delete(Comment entity) {
    super.delete(entity);
  }

}
