package com.example.demo.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    // 현재 로그인한 사용자 이름 가져오기
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }

    // 게시글 목록 페이지
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "10") int size,
                      @RequestParam(required = false) String searchKeyword,
                      @RequestParam(required = false) String searchType,
                      Model model) {
        
        log.info("게시글 목록 조회 - 페이지: {}, 크기: {}, 검색어: {}, 검색타입: {}", page, size, searchKeyword, searchType);
        
        Page<PostDto> postPage;
        List<PostDto> searchResults = null;
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // 검색 기능
            switch (searchType) {
                case "title":
                    searchResults = postService.searchPostsByTitle(searchKeyword);
                    break;
                case "author":
                    searchResults = postService.searchPostsByAuthor(searchKeyword);
                    break;
                default:
                    searchResults = postService.searchPosts(searchKeyword);
                    break;
            }
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("searchKeyword", searchKeyword);
            model.addAttribute("searchType", searchType);
        } else {
            // 일반 목록 조회
            postPage = postService.getPostList(page, size);
            model.addAttribute("posts", postPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", postPage.getTotalPages());
            model.addAttribute("totalElements", postPage.getTotalElements());
        }
        
        // 최근 게시글 (사이드바용)
        List<PostDto> recentPosts = postService.getRecentPosts(5);
        model.addAttribute("recentPosts", recentPosts);
        
        // 전체 게시글 수
        long totalCount = postService.getTotalPostCount();
        model.addAttribute("totalCount", totalCount);
        
        return "posts/list";
    }

    // 게시글 작성 페이지
    @GetMapping("/write")
    public String writeForm(Model model) {
        PostDto postDto = new PostDto();
        // 현재 로그인한 사용자 이름 설정
        String currentUsername = getCurrentUsername();
        if (currentUsername != null) {
            postDto.setAuthor(currentUsername);
        }
        model.addAttribute("postDto", postDto);
        return "posts/form";
    }

    // 게시글 작성 페이지 (new 경로)
    @GetMapping("/new")
    public String newForm(Model model) {
        PostDto postDto = new PostDto();
        // 현재 로그인한 사용자 이름 설정
        String currentUsername = getCurrentUsername();
        if (currentUsername != null) {
            postDto.setAuthor(currentUsername);
        }
        model.addAttribute("postDto", postDto);
        return "posts/form";
    }

    // 게시글 작성 처리
    @PostMapping("/write")
    public String write(@ModelAttribute PostDto postDto, RedirectAttributes redirectAttributes) {
        log.info("게시글 작성 요청 - 제목: {}, 작성자: {}", postDto.getTitle(), postDto.getAuthor());
        
        try {
            PostDto savedPost = postService.createPost(postDto);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 작성되었습니다.");
            return "redirect:/posts/" + savedPost.getId();
        } catch (Exception e) {
            log.error("게시글 작성 실패", e);
            redirectAttributes.addFlashAttribute("error", "게시글 작성에 실패했습니다.");
            return "redirect:/posts/write";
        }
    }

    // 게시글 작성 처리 (new 경로)
    @PostMapping("/new")
    public String newPost(@ModelAttribute PostDto postDto, RedirectAttributes redirectAttributes) {
        log.info("게시글 작성 요청 (new) - 제목: {}, 작성자: {}", postDto.getTitle(), postDto.getAuthor());
        
        try {
            PostDto savedPost = postService.createPost(postDto);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 작성되었습니다.");
            return "redirect:/posts/" + savedPost.getId();
        } catch (Exception e) {
            log.error("게시글 작성 실패", e);
            redirectAttributes.addFlashAttribute("error", "게시글 작성에 실패했습니다.");
            return "redirect:/posts/new";
        }
    }

    // 게시글 상세 페이지
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        log.info("게시글 상세 조회 - ID: {}", id);
        
        return postService.getPostById(id)
                .map(postDto -> {
                    model.addAttribute("post", postDto);
                    return "posts/detail";
                })
                .orElse("redirect:/posts");
    }

    // 게시글 수정 페이지
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        log.info("게시글 수정 페이지 요청 - ID: {}", id);
        
        return postService.getPostById(id)
                .map(postDto -> {
                    // 현재 로그인한 사용자가 작성자인지 확인
                    String currentUsername = getCurrentUsername();
                    if (currentUsername == null || !postDto.getAuthor().equals(currentUsername)) {
                        log.warn("권한 없음 - 게시글 수정 시도: ID={}, 요청자={}, 작성자={}", 
                                id, currentUsername, postDto.getAuthor());
                        return "redirect:/posts/" + id + "?error=unauthorized";
                    }
                    
                    model.addAttribute("postDto", postDto);
                    return "posts/form";
                })
                .orElse("redirect:/posts");
    }

    // 게시글 수정 처리
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute PostDto postDto, RedirectAttributes redirectAttributes) {
        log.info("게시글 수정 요청 - ID: {}, 제목: {}", id, postDto.getTitle());
        
        // 현재 로그인한 사용자가 작성자인지 확인
        String currentUsername = getCurrentUsername();
        if (currentUsername == null || !postDto.getAuthor().equals(currentUsername)) {
            log.warn("권한 없음 - 게시글 수정 시도: ID={}, 요청자={}, 작성자={}", 
                    id, currentUsername, postDto.getAuthor());
            redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
            return "redirect:/posts/" + id;
        }
        
        try {
            postService.updatePost(id, postDto)
                    .ifPresentOrElse(
                            updatedPost -> {
                                redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다.");
                                log.info("게시글 수정 완료 - ID: {}", id);
                            },
                            () -> {
                                redirectAttributes.addFlashAttribute("error", "게시글을 찾을 수 없습니다.");
                                log.warn("게시글 수정 실패 - 존재하지 않는 ID: {}", id);
                            }
                    );
            return "redirect:/posts/" + id;
        } catch (Exception e) {
            log.error("게시글 수정 실패 - ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "게시글 수정에 실패했습니다.");
            return "redirect:/posts/" + id + "/edit";
        }
    }

    // 게시글 삭제 처리
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("게시글 삭제 요청 - ID: {}", id);
        
        // 현재 로그인한 사용자가 작성자인지 확인
        String currentUsername = getCurrentUsername();
        Optional<PostDto> postDto = postService.getPostById(id);
        
        if (postDto.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "게시글을 찾을 수 없습니다.");
            return "redirect:/posts";
        }
        
        if (currentUsername == null || !postDto.get().getAuthor().equals(currentUsername)) {
            log.warn("권한 없음 - 게시글 삭제 시도: ID={}, 요청자={}, 작성자={}", 
                    id, currentUsername, postDto.get().getAuthor());
            redirectAttributes.addFlashAttribute("error", "삭제 권한이 없습니다.");
            return "redirect:/posts/" + id;
        }
        
        if (postService.deletePost(id)) {
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
            log.info("게시글 삭제 완료 - ID: {}", id);
        } else {
            redirectAttributes.addFlashAttribute("error", "게시글을 찾을 수 없습니다.");
            log.warn("게시글 삭제 실패 - 존재하지 않는 ID: {}", id);
        }
        
        return "redirect:/posts";
    }

    // Ajax 게시글 작성 처리
    @PostMapping("/api/write")
    @ResponseBody
    public ApiResponse<PostDto> writeApi(@RequestBody PostDto postDto) {
        log.info("Ajax 게시글 작성 요청 - 제목: {}", postDto.getTitle());
        
        try {
            PostDto savedPost = postService.createPost(postDto);
            return ApiResponse.success(savedPost, "게시글이 성공적으로 작성되었습니다.");
        } catch (Exception e) {
            log.error("Ajax 게시글 작성 실패", e);
            return ApiResponse.error("게시글 작성에 실패했습니다.");
        }
    }

    // Ajax 게시글 수정 처리
    @PutMapping("/api/{id}")
    @ResponseBody
    public ApiResponse<PostDto> editApi(@PathVariable Long id, @RequestBody PostDto postDto) {
        log.info("Ajax 게시글 수정 요청 - ID: {}, 제목: {}", id, postDto.getTitle());
        
        try {
            return postService.updatePost(id, postDto)
                    .map(updatedPost -> ApiResponse.success(updatedPost, "게시글이 성공적으로 수정되었습니다."))
                    .orElse(ApiResponse.error("게시글을 찾을 수 없습니다."));
        } catch (Exception e) {
            log.error("Ajax 게시글 수정 실패 - ID: {}", id, e);
            return ApiResponse.error("게시글 수정에 실패했습니다.");
        }
    }

    // Ajax 게시글 삭제 처리
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ApiResponse<Void> deleteApi(@PathVariable Long id) {
        log.info("Ajax 게시글 삭제 요청 - ID: {}", id);
        
        // 현재 로그인한 사용자가 작성자인지 확인
        String currentUsername = getCurrentUsername();
        Optional<PostDto> postDto = postService.getPostById(id);
        
        if (postDto.isEmpty()) {
            return ApiResponse.error("게시글을 찾을 수 없습니다.");
        }
        
        if (currentUsername == null || !postDto.get().getAuthor().equals(currentUsername)) {
            log.warn("권한 없음 - Ajax 게시글 삭제 시도: ID={}, 요청자={}, 작성자={}", 
                    id, currentUsername, postDto.get().getAuthor());
            return ApiResponse.error("삭제 권한이 없습니다.");
        }
        
        if (postService.deletePost(id)) {
            return ApiResponse.success(null, "게시글이 성공적으로 삭제되었습니다.");
        } else {
            return ApiResponse.error("게시글을 찾을 수 없습니다.");
        }
    }

    // API 응답을 위한 내부 클래스
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static <T> ApiResponse<T> success(T data, String message) {
            return new ApiResponse<>(true, message, data);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public T getData() { return data; }
    }
} 