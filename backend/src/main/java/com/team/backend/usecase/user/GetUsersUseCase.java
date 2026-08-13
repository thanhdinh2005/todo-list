package com.team.backend.usecase.user;

import com.team.backend.dto.request.user.UserFilterParam;
import com.team.backend.dto.response.PageResponse;
import com.team.backend.dto.response.UserResponse;
import com.team.backend.entity.Role;
import com.team.backend.entity.User;
import com.team.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    Pageable pageable = PageRequest.of(
      filter.getPageOrDefault(),
      filter.getSizeOrDefault(),
      Sort.by(
        "DESC".equalsIgnoreCase(filter.getOrderByOrDefault())
          ? Sort.Direction.DESC : Sort.Direction.ASC,
        "updatedAt"
      )
    );

    Page<User> page = userRepository.findAll(pageable);

    List<UserResponse> items =
      page.getContent()
        .stream()
        .map(user -> UserResponse.builder()
          .id(user.getId())
          .createdAt(user.getCreatedAt())
          .fullName(user.getFullName())
          .enabled(user.isEnabled())
          .roles(
            user.getRoles().stream()
              .map(Role::getName)
              .toList()
          )
          .build())
        .toList();

    log.info("Get all users with: {} elements", page.getTotalElements());

    return PageResponse.of(
      items,
      page.getNumber(),
      page.getSize(),
      page.getTotalElements()
    );
  }
}
