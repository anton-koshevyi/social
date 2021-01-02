package com.social.backend.model.post;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.model.Reply;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment extends Reply {
  
  @ManyToOne
  @JoinColumn(name = "post_id")
  private Post post;
  
}
