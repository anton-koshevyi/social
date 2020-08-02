# Social network model

**User.** Has personal data (email), username, first name, last name, publicity, password, friends, 
chats and posts. Can be regular user, or administrator (moder or admin).

*Regular users:*
* Public user can be added to friend, invited into chat, all posts are free to comment. Personal
  data available for all users.
* Internal user can be added to friend, invited into chat only by a friend, posts can be commented
  only by friends. Personal data available only for authorized users and administration.
* Private user cannot be added to friend, invited to chat (but can add other users and start chat
  with them), posts cannot be commented by friends. Personal data hidden for all users, except 
  administration.

*Administration:*
* Moder can see personal data of any user.
* Admin can promote/downgrade moders.

**Chat.** Has members and their messages.
* Private chat consists of 2 specific users (or only of it creator).
* Group chat has name and can consists of group of users. Since added, user accepts messages of all
  members (even when user is private, and members are not a friends). Has owner, which can edit
  member list (add new or remove existent) and set new owner from chat members.

**Message.** Has creation timestamp, update timestamp, body, and author. Linked to chat.

**Post.** Has creation timestamp, update timestamp, title, body, and author.

**Comment.** Has creation timestamp, update timestamp, body, and author. Linked to post.

## Technologies
### Backend
* **Build:** Gradle, Maven (for Docker)  
* **Main:** Java 8, Spring (MVC, Boot, Security), Hibernate, Flyway  
* **Data:** Postgres  
* **Test:** JUnit5, Rest-Assured, AssertJ, Mockito  
