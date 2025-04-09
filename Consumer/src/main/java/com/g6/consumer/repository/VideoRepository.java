package com.g6.consumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.g6.consumer.model.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {}
