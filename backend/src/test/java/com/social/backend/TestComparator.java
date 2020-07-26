package com.social.backend;

import java.util.Comparator;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.Message;
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
                .thenComparing(Post::getBody)
                .thenComparing(Post::getAuthor, userComparator());
    }
    
    public static Comparator<Comment> commentComparator() {
        return Comparator.comparing(Comment::getId)
                .thenComparing(Comment::getBody)
                .thenComparing(Comment::getPost, postComparator())
                .thenComparing(Comment::getAuthor, userComparator());
    }
    
    public static Comparator<Chat> chatComparator() {
        return (chat1, chat2) -> {
            if (chat1 instanceof GroupChat) {
                return Comparator.comparing(GroupChat::getId)
                        .thenComparing(GroupChat::getName)
                        .thenComparing(GroupChat::getOwner, userComparator())
                        .compare((GroupChat) chat1, (GroupChat) chat2);
            }
            
            return Comparator.comparing(Chat::getId)
                    .compare(chat1, chat2);
        };
    }
    
    public static Comparator<Message> messageComparator() {
        return Comparator.comparing(Message::getId)
                .thenComparing(Message::getBody);
    }
    
    @SuppressWarnings({"checkstyle:AvoidInlineConditionals", "ComparatorMethodParameterNotUsed"})
    public static <T> Comparator<T> notNullFirst() {
        return (actual, expected) -> (actual != null) ? 0 : 1;
    }
    
    private TestComparator() {}
}
