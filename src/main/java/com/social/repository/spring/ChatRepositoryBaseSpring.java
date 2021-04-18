package com.social.repository.spring;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social.model.chat.Chat;
import com.social.model.user.User;

public interface ChatRepositoryBaseSpring<T extends Chat> extends JpaRepository<T, Long> {

  Optional<T> findByIdAndMembersContaining(Long id, User user);

  Page<T> findAllByMembersContaining(User user, Pageable pageable);

}
