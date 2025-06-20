package com.example.demo.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    // 제목으로 게시글 검색
    List<Post> findByTitleContainingIgnoreCase(String title);

    // 작성자로 게시글 검색
    List<Post> findByAuthorContainingIgnoreCase(String author);

    // 제목 또는 내용으로 게시글 검색
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    List<Post> findByTitleOrContentContaining(@Param("keyword") String keyword);

    // 조회수 순으로 정렬된 게시글 목록
    List<Post> findAllByOrderByViewCountDesc();

    // 최신순으로 정렬된 게시글 목록 (페이징)
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 조회수 증가
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // 특정 작성자의 게시글 수 조회
    @Query("SELECT COUNT(p) FROM Post p WHERE p.author = :author")
    long countByAuthor(@Param("author") String author);

    // 전체 게시글 수 조회
    @Query("SELECT COUNT(p) FROM Post p")
    long getTotalPostCount();

    // 최근 게시글 조회 (최근 10개)
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findRecentPosts(Pageable pageable);

    // 제목으로 게시글 존재 여부 확인
    boolean existsByTitle(String title);

    // ID로 게시글 조회 (Optional 반환)
    Optional<Post> findById(Long id);
} 