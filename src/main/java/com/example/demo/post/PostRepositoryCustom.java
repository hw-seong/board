package com.example.demo.post;

import java.util.List;

public interface PostRepositoryCustom {
    
    // 제목 또는 내용으로 게시글 검색 (QueryDSL 사용)
    List<Post> findByTitleOrContentContaining(String keyword);
    
    // 특정 작성자의 게시글 수 조회 (QueryDSL 사용)
    long countByAuthor(String author);
    
    // 전체 게시글 수 조회 (QueryDSL 사용)
    long getTotalPostCount();
    
    // 최근 게시글 조회 (QueryDSL 사용)
    List<Post> findRecentPosts(int limit);
} 