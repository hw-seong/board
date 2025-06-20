package com.example.demo.post;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity를 DTO로 변환하는 정적 메서드
    public static PostDto fromEntity(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    // DTO를 Entity로 변환하는 메서드
    public Post toEntity() {
        return Post.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .author(this.author)
                .viewCount(this.viewCount)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    // 생성일시를 포맷된 문자열로 반환
    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        return "";
    }

    // 수정일시를 포맷된 문자열로 반환
    public String getFormattedUpdatedAt() {
        if (updatedAt != null) {
            return updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        return "";
    }

    // 제목 길이 제한 (목록에서 사용)
    public String getShortTitle() {
        if (title != null && title.length() > 30) {
            return title.substring(0, 30) + "...";
        }
        return title;
    }

    // 내용 길이 제한 (목록에서 사용)
    public String getShortContent() {
        if (content != null && content.length() > 100) {
            return content.substring(0, 100) + "...";
        }
        return content;
    }
} 