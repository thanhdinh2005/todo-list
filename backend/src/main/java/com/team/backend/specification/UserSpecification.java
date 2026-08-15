package com.team.backend.specification;

import com.team.backend.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

  private UserSpecification() {
  }

  public static Specification<User> enabledEquals(Boolean enabled) {
    if (enabled == null) {
      return null;
    }
    return (root, query, cb) -> cb.equal(root.get("enabled"), enabled);
  }

  public static Specification<User> buildFilter(Boolean enabled) {
    List<Specification<User>> specs = new ArrayList<>();
    addIfPresent(specs, enabledEquals(enabled));

    return specs.stream().reduce(Specification::and).orElse(null);
  }

  private static void addIfPresent(List<Specification<User>> specs, Specification<User> spec) {
    if (spec != null) {
      specs.add(spec);
    }
  }
}
