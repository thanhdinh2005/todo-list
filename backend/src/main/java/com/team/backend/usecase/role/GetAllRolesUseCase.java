package com.team.backend.usecase.role;

import com.team.backend.dto.response.RoleResponse;
import com.team.backend.entity.Role;
import com.team.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetAllRolesUseCase {
  private final RoleRepository roleRepository;

  public List<RoleResponse> execute() {
    List<Role> roles = roleRepository.findAll();

    log.info("Fetched {} roles", roles.size());

    return roles.stream()
      .map(RoleResponse::from)
      .toList();
  }
}
