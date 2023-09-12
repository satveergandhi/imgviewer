package com.inn.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inn.image.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {


}
