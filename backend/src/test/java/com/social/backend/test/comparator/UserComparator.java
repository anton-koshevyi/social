package com.social.backend.test.comparator;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import com.social.backend.model.user.User;

class UserComparator implements Comparator<User> {

  @Override
  public int compare(User left, User right) {
    return ComparisonChain.start()
        .compare(left.getId(), right.getId())
        .compare(left.getEmail(), right.getEmail())
        .compare(left.getUsername(), right.getUsername())
        .compare(left.getFirstName(), right.getFirstName())
        .compare(left.getLastName(), right.getLastName())
        .compare(left.getPublicity(), right.getPublicity())
        .compare(left.getPassword(), right.getPassword())
        .compareTrueFirst(left.isModer(), right.isModer())
        .compareTrueFirst(left.isAdmin(), right.isAdmin())
        .result();
  }

}
