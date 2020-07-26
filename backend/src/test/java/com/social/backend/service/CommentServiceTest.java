package com.social.backend.service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.list;

import static com.social.backend.TestComparator.commentComparator;
import static com.social.backend.TestComparator.notNullFirst;
import static com.social.backend.TestEntity.comment;
import static com.social.backend.TestEntity.post;
import static com.social.backend.TestEntity.user;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Import(CommentServiceImpl.class)
public class CommentServiceTest {
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void create_exception_whenPostOfPrivateAuthor_andCommentNotOfPostAuthor() {
        User postAuthor = entityManager.persist(user()
                .setPublicity(Publicity.PRIVATE));
        Post post = entityManager.persist(post()
                .setAuthor(postAuthor));
        User commentator = entityManager.persist(user()
                .setEmail("commentator@mail.com")
                .setUsername("commentator"));
    
        assertThatThrownBy(() -> commentService.create(post, commentator, "body"))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.comment.privatePost"});
    }
    
    @Test
    public void create_exception_whenPostOfInternalAuthor_andCommentNotOfFriend() {
        User postAuthor = entityManager.persist(user()
                .setPublicity(Publicity.INTERNAL));
        Post post = entityManager.persist(post()
                .setAuthor(postAuthor));
        User commentAuthor = entityManager.persist(user()
                .setEmail("commentator@mail.com")
                .setUsername("commentator"));
        
        assertThatThrownBy(() -> commentService.create(post, commentAuthor, "body"))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.comment.internalPost"});
    }
    
    @Test
    public void create_whenPostOfPrivateAuthor_andCommentOfPostAuthor() {
        User postAuthor = entityManager.persist(user()
                .setPublicity(Publicity.PRIVATE));
        Post post = entityManager.persist(post()
                .setAuthor(postAuthor));
        
        commentService.create(post, postAuthor, "body");
        
        assertThat(entityManager.find(Comment.class, 1L))
                .usingComparator(commentComparator())
                .isEqualTo(new Comment()
                        .setPost(post()
                                .setId(1L)
                                .setAuthor(user()
                                        .setId(1L)))
                        .setId(1L)
                        .setBody("body")
                        .setAuthor(user()
                                .setId(1L)));
    }
    
    @Test
    public void create_whenPostOfInternalAuthor_andCommentOfFriend() {
        User postAuthor = entityManager.persist(user()
                .setEmail("postAuthor@mail.com")
                .setUsername("postAuthor")
                .setPublicity(Publicity.INTERNAL)
                .setFriends(list(user()
                        .setId(2L)
                        .setEmail("commentator@mail.com")
                        .setUsername("commentator"))));
        Post post = entityManager.persist(post()
                .setAuthor(postAuthor));
        User commentator = entityManager.persist(user()
                .setEmail("commentator@mail.com")
                .setUsername("commentator")
                .setFriends(list(user()
                        .setId(1L)
                        .setEmail("commentator@mail.com")
                        .setUsername("commentator"))));
    
        commentService.create(post, commentator, "body");
        
        assertThat(entityManager.find(Comment.class, 1L))
                .usingComparator(commentComparator())
                .isEqualTo(new Comment()
                        .setPost(post()
                                .setId(1L)
                                .setAuthor(user()
                                        .setId(1L)
                                        .setEmail("postAuthor@mail.com")
                                        .setUsername("postAuthor")
                                        .setPublicity(Publicity.INTERNAL)))
                        .setId(1L)
                        .setBody("body")
                        .setAuthor(user()
                                .setId(2L)
                                .setEmail("commentator@mail.com")
                                .setUsername("commentator")));
    }
    
    @Test
    public void create_whenPostOfPublicAuthor() {
        User postAuthor = entityManager.persist(user()
                .setEmail("postAuthor@mail.com")
                .setUsername("postAuthor")
                .setPublicity(Publicity.PUBLIC));
        Post post = entityManager.persist(post()
                .setAuthor(postAuthor));
        User commentator = entityManager.persist(user()
                .setEmail("commentator@mail.com")
                .setUsername("commentator"));
    
        commentService.create(post, commentator, "body");
        
        assertThat(entityManager.find(Comment.class, 1L))
                .usingComparator(commentComparator())
                .isEqualTo(new Comment()
                        .setPost(post()
                                .setId(1L)
                                .setAuthor(user()
                                        .setId(1L)
                                        .setEmail("postAuthor@mail.com")
                                        .setUsername("postAuthor")
                                        .setPublicity(Publicity.PUBLIC)))
                        .setId(1L)
                        .setBody("body")
                        .setAuthor(user()
                                .setId(2L)
                                .setEmail("commentator@mail.com")
                                .setUsername("commentator")));
    }
    
    @Test
    public void update_exception_whenNoEntityWithIdAndAuthorId() {
        entityManager.persist(user());
        
        assertThatThrownBy(() -> commentService.update(0L, 1L, "body"))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.comment.byIdAndAuthorId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void update() {
        User postAuthor = entityManager.persist(user());
        Post post = entityManager.persist(post()
                .setAuthor(postAuthor));
        entityManager.persist(new Comment()
                .setPost(post)
                .setBody("comment body")
                .setAuthor(postAuthor));
        
        commentService.update(1L, 1L, "new body");
        
        assertThat(entityManager.find(Comment.class, 1L))
                .usingComparator(commentComparator())
                .usingComparatorForFields(notNullFirst(), "updated")
                .isEqualTo(new Comment()
                        .setPost(post()
                                .setId(1L)
                                .setAuthor(user()
                                        .setId(1L)))
                        .setId(1L)
                        .setBody("new body")
                        .setAuthor(user()
                                .setId(1L)));
    }
    
    @Test
    public void delete_exception_whenNoEntityWithIdAndAuthorId() {
        entityManager.persist(user());
        
        assertThatThrownBy(() -> commentService.delete(0L, 1L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.comment.byIdAndAuthorId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
    }
    
    @Test
    public void delete() {
        User postAuthor = entityManager.persist(user());
        Post post = entityManager.persist(post()
                .setAuthor(postAuthor));
        entityManager.persist(comment()
                .setPost(post)
                .setAuthor(postAuthor));
        
        commentService.delete(1L, 1L);
        
        assertThat(entityManager.find(Comment.class, 1L))
                .isNull();
    }
    
    @Test
    public void findAllByPostId_exception_onNullPageable() {
        assertThatThrownBy(() -> commentService.findAllByPostId(1L, null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("Pageable must not be null");
    }
    
    @Test
    public void findAllByPostId() {
        User postAuthor = entityManager.persist(user());
        Post post = entityManager.persist(post()
                .setAuthor(postAuthor));
        entityManager.persist(comment()
                .setPost(post)
                .setAuthor(postAuthor));
        
        assertThat(commentService.findAllByPostId(1L, Pageable.unpaged()))
                .usingComparatorForType(commentComparator(), Comment.class)
                .containsExactly((Comment) comment()
                        .setPost(post()
                                .setId(1L)
                                .setAuthor(user()
                                        .setId(1L)))
                        .setId(1L)
                        .setAuthor(user()
                                .setId(1L)));
    }
}
