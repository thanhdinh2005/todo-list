package com.team.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {

  private List<T> content;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;

  public static <T> PageResponse<T> of(Page<T> page) {
    return PageResponse.<T>builder()
      .content(page.getContent())
      .page(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .build();
  }

  public static <T> PageResponse<T> of(
    List<T> content,
    int page,
    int size,
    long totalElements
  ) {
    int totalPages = size == 0
      ? 0
      : (int) Math.ceil((double) totalElements / size);

    return PageResponse.<T>builder()
      .content(content)
      .page(page)
      .size(size)
      .totalElements(totalElements)
      .totalPages(totalPages)
      .build();
  }
}
