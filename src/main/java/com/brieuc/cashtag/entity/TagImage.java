package com.brieuc.cashtag.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tag_image")
public class TagImage {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      
      @OneToOne
      @JoinColumn(name = "tag_id")
      private Tag tag;

      @Column(name = "image_path")
      private String imagePath;
}
