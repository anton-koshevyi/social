package com.social.backend.service;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.social.backend.dto.post.ContentDto;
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
    public Post create(Long authorId, ContentDto dto) {
        Post entity = new Post()
                .setCreated(ZonedDateTime.now())
                .setBody(dto.getBody())
                .setAuthor(new User(authorId));
        return postRepository.save(entity);
    }
    
    @Override
    public Post update(Long id, Long authorId, ContentDto dto) {
        Post entity = this.findByIdAndAuthorId(id, authorId);
        entity.setUpdated(ZonedDateTime.now())
                .setBody(dto.getBody());
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
    public Page<Post> findAllByAuthorId(Long authorId, Pageable pageable) {
        return postRepository.findAllByAuthorId(authorId, pageable);
    }
    
    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
}
