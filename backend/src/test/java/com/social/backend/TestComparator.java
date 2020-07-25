package com.social.backend;

import java.util.Comparator;

import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

@SuppressWarnings("checkstyle:DeclarationOrder")
public final class TestComparator {
    public static Comparator<User> userComparator() {
        return Comparator.comparing(User::getId)
                .thenComparing(User::getEmail)
                .thenComparing(User::getUsername)
                .thenComparing(User::getFirstName)
                .thenComparing(User::getLastName)
                .thenComparing(User::getPublicity)
                .thenComparing(User::getPassword);
    }
    
    public static Comparator<Post> postComparator() {
        return Comparator.comparing(Post::getId)
                .thenComparing(Post::getBody);
    }
    
    public static Comparator<Comment> commentComparator() {
        return Comparator.comparing(Comment::getId)
                .thenComparing(Comment::getBody);
    }
    
    @SuppressWarnings({"checkstyle:AvoidInlineConditionals", "ComparatorMethodParameterNotUsed"})
    public static <T> Comparator<T> notNullFirst() {
        return (actual, expected) -> (actual != null) ? 0 : 1;
    }
    
    private TestComparator() {}
}
