package com.social.backend.test.comparator;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.social.backend.model.user.User;

class UserComparator implements Comparator<User> {

  @Override
  public int compare(User left, User right) {
    return new CompareToBuilder()
        .append(left.getId(), right.getId())
        .append(left.getEmail(), right.getEmail())
        .append(left.getUsername(), right.getUsername())
        .append(left.getFirstName(), right.getFirstName())
        .append(left.getLastName(), right.getLastName())
        .append(left.getPublicity(), right.getPublicity())
        .append(left.getPassword(), right.getPassword())
        .append(left.isModer(), right.isModer())
        .append(left.isAdmin(), right.isAdmin())
        .toComparison();
  }

}
