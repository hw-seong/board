package com.example.demo.post;

import com.example.demo.user.User;
import com.example.demo.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    // 현재 로그인한 사용자 정보 가져오기
    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            try {
                User user = (User) userService.loadUserByUsername(authentication.getName());
                return Optional.of(user);
            } catch (Exception e) {
                log.warn("현재 사용자 정보를 가져올 수 없습니다: {}", e.getMessage());
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    // 게시글 목록 조회 (페이징)
    public Page<PostDto> getPostList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        return postPage.map(PostDto::fromEntity);
    }

    // 게시글 상세 조회
    @Transactional
    public Optional<PostDto> getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            // 조회수 증가
            postRepository.incrementViewCount(id);
            return post.map(PostDto::fromEntity);
        }
        return Optional.empty();
    }

    // 게시글 작성
    @Transactional
    public PostDto createPost(PostDto postDto) {
        Optional<User> currentUser = getCurrentUser();
        
        Post.PostBuilder postBuilder = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .author(postDto.getAuthor());
        
        // 로그인한 사용자가 있으면 연결
        if (currentUser.isPresent()) {
            postBuilder.user(currentUser.get());
        }
        
        Post post = postBuilder.build();
        Post savedPost = postRepository.save(post);
        log.info("게시글 작성 완료: ID={}, 제목={}, 작성자={}", savedPost.getId(), savedPost.getTitle(), savedPost.getAuthor());
        
        return PostDto.fromEntity(savedPost);
    }

    // 게시글 수정
    @Transactional
    public Optional<PostDto> updatePost(Long id, PostDto postDto) {
        Optional<Post> existingPost = postRepository.findById(id);
        
        if (existingPost.isPresent()) {
            Post post = existingPost.get();
            post.setTitle(postDto.getTitle());
            post.setContent(postDto.getContent());
            post.setAuthor(postDto.getAuthor());
            
            Post updatedPost = postRepository.save(post);
            log.info("게시글 수정 완료: ID={}, 제목={}", updatedPost.getId(), updatedPost.getTitle());
            
            return Optional.of(PostDto.fromEntity(updatedPost));
        }
        
        return Optional.empty();
    }

    // 게시글 삭제
    @Transactional
    public boolean deletePost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            postRepository.deleteById(id);
            log.info("게시글 삭제 완료: ID={}, 제목={}", id, post.get().getTitle());
            return true;
        }
        return false;
    }

    // 게시글 검색 (제목 또는 내용)
    public List<PostDto> searchPosts(String keyword) {
        List<Post> posts = postRepository.findByTitleOrContentContaining(keyword);
        return posts.stream()
                .map(PostDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 제목으로 게시글 검색
    public List<PostDto> searchPostsByTitle(String title) {
        List<Post> posts = postRepository.findByTitleContainingIgnoreCase(title);
        return posts.stream()
                .map(PostDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 작성자로 게시글 검색
    public List<PostDto> searchPostsByAuthor(String author) {
        List<Post> posts = postRepository.findByAuthorContainingIgnoreCase(author);
        return posts.stream()
                .map(PostDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 조회수 순으로 게시글 목록 조회
    public List<PostDto> getPostsByViewCount() {
        List<Post> posts = postRepository.findAllByOrderByViewCountDesc();
        return posts.stream()
                .map(PostDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 최근 게시글 조회
    public List<PostDto> getRecentPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> posts = postRepository.findRecentPosts(pageable);
        return posts.stream()
                .map(PostDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 전체 게시글 수 조회
    public long getTotalPostCount() {
        return postRepository.getTotalPostCount();
    }

    // 특정 작성자의 게시글 수 조회
    public long getPostCountByAuthor(String author) {
        return postRepository.countByAuthor(author);
    }

    // 게시글 존재 여부 확인
    public boolean existsById(Long id) {
        return postRepository.existsById(id);
    }

    // 제목 중복 확인
    public boolean existsByTitle(String title) {
        return postRepository.existsByTitle(title);
    }
} 