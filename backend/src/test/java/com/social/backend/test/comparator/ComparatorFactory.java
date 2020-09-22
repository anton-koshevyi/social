package com.social.backend.test.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public final class ComparatorFactory {

  private static final Map<String, Comparator<?>> typeComparators = new HashMap<>();

  private ComparatorFactory() {
  }

  public static <T> Comparator<T> getComparator(Class<T> type) {
    if (type == null) {
      return null;
    }

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

      if (Message.class.equals(type)) {
        typeComparators.put(typeName, new MessageComparator(
            ComparatorFactory.getComparator(User.class),
            ComparatorFactory.getComparator(Chat.class)
        ));
      }

      if (Chat.class.isAssignableFrom(type)) {
        typeComparators.put(typeName, new ChatComparator(
            ComparatorFactory.getComparator(User.class)
        ));
      }

      if (Comment.class.equals(type)) {
        typeComparators.put(typeName, new CommentComparator(
            ComparatorFactory.getComparator(User.class),
            ComparatorFactory.getComparator(Post.class)
        ));
      }
    }

    return (Comparator<T>) typeComparators.get(typeName);
  }

}
