package com.social.backend;

import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

@SuppressWarnings("checkstyle:DeclarationOrder")
public final class TestEntity {
    public static User user() {
        return new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded");
    }
    
    public static Post post() {
        return new Post()
                .setBody("post body");
    }
    
    public static Comment comment() {
        return new Comment()
                .setBody("comment body");
    }
    
    public static PrivateChat privateChat() {
        return new PrivateChat();
    }
    
    public static GroupChat groupChat() {
        return new GroupChat()
                .setName("name");
    }
    
    private TestEntity() {}
}
