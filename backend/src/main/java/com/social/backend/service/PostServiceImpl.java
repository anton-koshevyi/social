package com.social.backend.service;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.PostRepository;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepository repository;
    
    @Autowired
    public PostServiceImpl(PostRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public Post create(User author, String body) {
        Post entity = new Post();
        entity.setBody(body);
        entity.setAuthor(author);
        return repository.save(entity);
    }
    
    @Override
    public Post update(Long id, Long authorId, String body) {
        Post entity = this.findByIdAndAuthorId(id, authorId);
        entity.setUpdated(ZonedDateTime.now());
        entity.setBody(body);
        return repository.save(entity);
    }
    
    @Override
    public void delete(Long id, Long authorId) {
        Post entity = this.findByIdAndAuthorId(id, authorId);
        repository.delete(entity);
    }
    
    @Override
    public Post findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("notFound.post.byId", id));
    }
    
    @Override
    public Page<Post> findAll(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        return repository.findAll(pageable);
    }
    
    @Override
    public Page<Post> findAllByAuthorId(Long authorId, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        return repository.findAllByAuthorId(authorId, pageable);
    }
    
    private Post findByIdAndAuthorId(Long id, Long authorId) {
        return repository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new NotFoundException("notFound.post.byIdAndAuthorId", id, authorId));
    }
}
