package com.qiang.taskmanager.mapper;

import com.qiang.taskmanager.entity.Task;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface TaskMapper {

    @Select("SELECT * FROM task")
    List<Task> findAll();

    @Select("SELECT * FROM task WHERE id = #{id}")
    Task findById(Long id);

    @Insert("INSERT INTO task(title, status, created_at) VALUES(#{title}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Task task);

    @Update("UPDATE task SET title = #{title}, status = #{status} WHERE id = #{id}")
    void update(Task task);

    @Delete("DELETE FROM task WHERE id = #{id}")
    void deleteById(Long id);
}
