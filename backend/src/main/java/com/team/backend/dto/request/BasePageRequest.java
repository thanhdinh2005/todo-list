package com.team.backend.dto.request;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

@Data
public abstract class BasePageRequest {
  private static final int MAX_SIZE = 100;
  private static final Set<String> DEFAULT_ALLOWED_SORT = Set.of("createdAt", "updatedAt");

  private Integer page;
  private Integer size;
  private String sortBy;
  private String direction;

  public int getPageOrDefault() {
    return page != null && page >= 0 ? page : 0;
  }

  public int getSizeOrDefault() {
    if (size == null || size <= 0) return 10;
    return Math.min(size, MAX_SIZE);
  }

  public String getSortByOrDefault() {
    if (sortBy != null && getAllowedSortFields().contains(sortBy)) {
      return sortBy;
    }
    return "createdAt";
  }

  public Sort.Direction getDirectionOrDefault() {
    return "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
  }

  public Pageable toPageable() {
    return PageRequest.of(
      getPageOrDefault(),
      getSizeOrDefault(),
      Sort.by(getDirectionOrDefault(), getSortByOrDefault())
    );
  }

  protected Set<String> getAllowedSortFields() {
    return DEFAULT_ALLOWED_SORT;
  }
}
