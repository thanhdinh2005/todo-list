package com.team.backend.dto.request;

import lombok.Data;

@Data
public class BasePageRequest {
  private Integer page;
  private Integer size;
  private String orderBy;

  public int getPageOrDefault() { return page != null ? page : 0; }
  public int getSizeOrDefault() { return size != null ? size : 10; }
  public String getOrderByOrDefault() { return orderBy != null ? orderBy : "DESC"; }
}
