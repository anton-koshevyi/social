package com.social.backend.service;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.PostRepository;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    
    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    @Override
    public Post create(User author, String body) {
        Post entity = new Post()
                .setCreated(ZonedDateTime.now())
                .setBody(body)
                .setAuthor(author);
        return postRepository.save(entity);
    }
    
    @Override
    public Post update(Long id, Long authorId, String body) {
        Post entity = this.findByIdAndAuthorId(id, authorId);
        entity.setUpdated(ZonedDateTime.now())
                .setBody(body);
        return postRepository.save(entity);
    }
    
    @Override
    public void delete(Long id, Long authorId) {
        Post entity = this.findByIdAndAuthorId(id, authorId);
        postRepository.delete(entity);
    }
    
    @Override
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("notFound.post.byId", id));
    }
    
    @Override
    public Post findByIdAndAuthorId(Long id, Long authorId) {
        return postRepository.findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new NotFoundException("notFound.post.byIdAndAuthorId", id, authorId));
    }
    
    @Override
    public Page<Post> findAll(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        return postRepository.findAll(pageable);
    }
    
    @Override
    public Page<Post> findAllByAuthorId(Long authorId, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        return postRepository.findAllByAuthorId(authorId, pageable);
    }
}
