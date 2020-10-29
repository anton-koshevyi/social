package com.social.backend.test.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public final class ComparatorFactory {

  private static final Map<String, Comparator<?>> typeComparators = new HashMap<>();

  private ComparatorFactory() {
  }

  @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:JavaNCSS"})
  public static <T> Comparator<T> getComparator(Class<T> type) {
    String typeName = type.getName();

    if (!typeComparators.containsKey(typeName)) {
      if (User.class.equals(type)) {
        typeComparators.put(typeName, new UserComparator());
      }

      if (Post.class.equals(type)) {
        typeComparators.put(typeName, new PostComparator(
            ComparatorFactory.getComparator(User.class)
        ));
      }

      if (Comment.class.equals(type)) {
        typeComparators.put(typeName, new CommentComparator(
            ComparatorFactory.getComparator(User.class),
            ComparatorFactory.getComparator(Post.class)
        ));
      }

      if (Chat.class.isAssignableFrom(type)) {
        if (PrivateChat.class.equals(type)) {
          typeComparators.put(typeName, new ChatPrivateComparator(
              new CollectionComparatorAdapter<>(
                  ComparatorFactory.getComparator(User.class)
              )
          ));
        } else if (GroupChat.class.equals(type)) {
          Comparator<User> userComparator = ComparatorFactory.getComparator(User.class);
          typeComparators.put(typeName, new ChatGroupComparator(
              new CollectionComparatorAdapter<>(userComparator),
              userComparator
          ));
        } else {
          typeComparators.put(typeName, new ChatCompositeComparator(
              ComparatorFactory.getComparator(PrivateChat.class),
              ComparatorFactory.getComparator(GroupChat.class)
          ));
        }
      }

      if (Message.class.equals(type)) {
        typeComparators.put(typeName, new MessageComparator(
            ComparatorFactory.getComparator(User.class),
            ComparatorFactory.getComparator(Chat.class)
        ));
      }
    }

    return (Comparator<T>) typeComparators.get(typeName);
  }

}
