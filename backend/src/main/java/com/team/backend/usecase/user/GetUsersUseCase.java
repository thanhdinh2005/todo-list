package com.team.backend.usecase.user;

import com.team.backend.dto.request.user.UserFilterParam;
import com.team.backend.dto.response.PageResponse;
import com.team.backend.dto.response.UserResponse;
import com.team.backend.entity.Role;
import com.team.backend.entity.User;
import com.team.backend.repository.UserRepository;
import com.team.backend.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetUsersUseCase {
  private final UserRepository userRepository;

  public PageResponse<UserResponse> execute(UserFilterParam filter) {
    Specification<User> spec = UserSpecification.buildFilter(filter.getEnabled());
    Page<User> page = userRepository.findAll(spec, filter.toPageable());

    List<UserResponse> items = page.getContent()
      .stream()
      .map(this::toResponse)
      .toList();

    log.info("Get all users with {} elements", page.getTotalElements());

    return PageResponse.of(items, page.getNumber(), page.getSize(), page.getTotalElements());
  }

  private UserResponse toResponse(User user) {
    return UserResponse.builder()
      .id(user.getId())
      .createdAt(user.getCreatedAt())
      .fullName(user.getFullName())
      .enabled(user.isEnabled())
      .roles(user.getRoles().stream().map(Role::getName).toList())
      .build();
  }
}
