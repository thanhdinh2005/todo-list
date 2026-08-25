package com.team.backend.utils;

import com.team.backend.entity.Category;
import com.team.backend.entity.Task;
import com.team.backend.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

public final class TaskTestFactory {
  private TaskTestFactory() {}

  public static Task create(
    UUID id,
    String title,
    String description,
    Instant dueDate,
    User owner,
    Category category
  ) {
    Task task = Task.create(title, description, dueDate, owner, category);
    ReflectionTestUtils.setField(task, "id", id);
    return task;
  }

  public static Task createDefault(User owner) {
    return create(UUID.randomUUID(), "Buy groceries", "Milk, eggs, bread",
      Instant.now().plusSeconds(86400), owner, null);
  }

  public static Task createOverdue(User owner) {
    return create(UUID.randomUUID(), "Overdue task", null,
      Instant.now().minusSeconds(86400), owner, null);
  }

  public static Task createCompleted(User owner) {
    Task task = createDefault(owner);
    task.complete();
    return task;
  }
}
