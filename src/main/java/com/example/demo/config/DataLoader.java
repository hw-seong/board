package com.example.demo.config;

import com.example.demo.post.Post;
import com.example.demo.post.PostRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 테스트 사용자 생성
        if (userRepository.count() == 0) {
            createTestUsers();
        }

        // 테스트 게시글 생성
        if (postRepository.count() == 0) {
            createTestPosts();
        }
    }

    private void createTestUsers() {
        User user1 = new User();
        user1.setUsername("admin");
        user1.setPassword(passwordEncoder.encode("123456"));
        user1.setEmail("admin@example.com");
        user1.setDisplayName("관리자");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user1");
        user2.setPassword(passwordEncoder.encode("123456"));
        user2.setEmail("user1@example.com");
        user2.setDisplayName("홍길동");
        userRepository.save(user2);

        User user3 = new User();
        user3.setUsername("user2");
        user3.setPassword(passwordEncoder.encode("123456"));
        user3.setEmail("user2@example.com");
        user3.setDisplayName("김철수");
        userRepository.save(user3);

        log.info("테스트 사용자 생성 완료");
    }
    private void createTestPosts() {
        String[] titles = {
            "Spring Boot 시작하기",
            "QueryDSL 사용법", 
            "Thymeleaf 템플릿 엔진",
            "Spring Security 설정",
            "JPA 엔티티 설계",
            "RESTful API 설계",
            "데이터베이스 최적화",
            "웹 애플리케이션 배포",
            "테스트 코드 작성법",
            "코드 리뷰 가이드",
            "마이크로서비스 아키텍처",
            "Docker 컨테이너 활용",
            "CI/CD 파이프라인 구축",
            "클라우드 네이티브 개발",
            "모니터링과 로깅",
            "성능 튜닝 기법",
            "보안 취약점 분석",
            "API 문서화 도구",
            "데이터베이스 마이그레이션",
            "캐싱 전략 구현",
            "메시지 큐 시스템",
            "웹소켓 실시간 통신",
            "파일 업로드 처리",
            "이메일 발송 서비스",
            "결제 시스템 연동",
            "소셜 로그인 구현",
            "다국어 지원 방법",
            "반응형 웹 디자인",
            "프론트엔드 프레임워크",
            "모바일 앱 개발",
        };

        String[] contents = {
            "Spring Boot는 Java 기반의 웹 애플리케이션을 빠르게 개발할 수 있게 해주는 프레임워크입니다. 이 글에서는 Spring Boot의 기본 설정과 사용법에 대해 알아보겠습니다.",
            "QueryDSL은 타입 안전한 쿼리를 작성할 수 있게 해주는 라이브러리입니다. JPA와 함께 사용하면 동적 쿼리를 쉽게 작성할 수 있습니다.",
            "Thymeleaf는 서버 사이드 템플릿 엔진으로, HTML을 자연스럽게 작성할 수 있습니다. Spring Boot와 함께 사용하면 강력한 웹 페이지를 만들 수 있습니다.",
            "Spring Security는 애플리케이션의 보안을 담당하는 프레임워크입니다. 인증과 권한 관리를 쉽게 구현할 수 있습니다.",
            "JPA 엔티티 설계는 데이터베이스 스키마를 객체 지향적으로 표현하는 방법입니다. 올바른 엔티티 설계는 애플리케이션의 성능에 큰 영향을 미칩니다.",
            "RESTful API는 웹 서비스의 표준적인 설계 방법입니다. HTTP 메서드를 활용하여 리소스를 조작하는 API를 설계할 수 있습니다.",
            "데이터베이스 최적화는 애플리케이션의 성능을 향상시키는 중요한 요소입니다. 인덱스 설계와 쿼리 최적화에 대해 알아보겠습니다.",
            "웹 애플리케이션을 실제 서버에 배포하는 방법에 대해 알아보겠습니다. Docker, AWS, Heroku 등 다양한 배포 방법이 있습니다.",
            "테스트 코드는 애플리케이션의 품질을 보장하는 중요한 요소입니다. JUnit과 Mockito를 사용한 테스트 코드 작성법을 알아보겠습니다.",
            "코드 리뷰는 팀 개발에서 코드 품질을 향상시키는 중요한 프로세스입니다. 효과적인 코드 리뷰 방법에 대해 알아보겠습니다.",
            "마이크로서비스 아키텍처는 대규모 애플리케이션을 작은 서비스로 분해하여 개발하는 방법입니다. 각 서비스는 독립적으로 개발, 배포, 확장할 수 있습니다.",
            "Docker는 애플리케이션을 컨테이너로 패키징하여 일관된 환경에서 실행할 수 있게 해주는 플랫폼입니다. 개발부터 배포까지 환경 차이로 인한 문제를 해결합니다.",
            "CI/CD는 지속적 통합과 지속적 배포를 의미합니다. Jenkins, GitHub Actions 등을 사용하여 자동화된 빌드, 테스트, 배포 파이프라인을 구축할 수 있습니다.",
            "클라우드 네이티브 개발은 클라우드 환경에 최적화된 애플리케이션을 개발하는 방법입니다. 컨테이너, 마이크로서비스, DevOps를 활용합니다.",
            "모니터링과 로깅은 운영 중인 애플리케이션의 상태를 파악하고 문제를 해결하는 데 필수적입니다. Prometheus, ELK 스택 등을 활용할 수 있습니다.",
            "성능 튜닝은 애플리케이션의 응답 시간과 처리량을 개선하는 과정입니다. 프로파일링 도구를 사용하여 병목 지점을 찾고 최적화할 수 있습니다.",
            "보안 취약점 분석은 애플리케이션의 보안 위험을 식별하고 해결하는 과정입니다. 정적 분석 도구와 동적 분석 도구를 활용합니다.",
            "API 문서화는 개발자들이 API를 쉽게 이해하고 사용할 수 있도록 도와줍니다. Swagger, OpenAPI 등을 사용하여 자동으로 문서를 생성할 수 있습니다.",
            "데이터베이스 마이그레이션은 스키마 변경을 관리하는 도구입니다. Flyway, Liquibase 등을 사용하여 버전 관리와 롤백을 지원합니다.",
            "캐싱은 애플리케이션의 성능을 향상시키는 중요한 기술입니다. Redis, EhCache 등을 사용하여 자주 접근하는 데이터를 메모리에 저장합니다.",
            "메시지 큐는 비동기 통신을 위한 시스템입니다. RabbitMQ, Apache Kafka 등을 사용하여 서비스 간 느슨한 결합을 구현할 수 있습니다.",
            "웹소켓은 실시간 양방향 통신을 지원하는 프로토콜입니다. 채팅, 알림, 실시간 업데이트 등에 활용할 수 있습니다.",
            "파일 업로드는 웹 애플리케이션에서 자주 사용되는 기능입니다. 보안, 성능, 저장소 관리 등을 고려하여 구현해야 합니다.",
            "이메일 발송 서비스는 사용자에게 알림을 보내는 중요한 기능입니다. SMTP, 템플릿 엔진, 스케줄링 등을 활용합니다.",
            "결제 시스템 연동은 전자상거래 애플리케이션의 핵심 기능입니다. 다양한 결제 게이트웨이와 안전한 결제 처리를 구현합니다.",
            "소셜 로그인은 사용자 편의성을 높이는 인증 방법입니다. OAuth 2.0을 사용하여 Google, Facebook, GitHub 등과 연동할 수 있습니다.",
            "다국어 지원은 글로벌 서비스를 위한 필수 기능입니다. 국제화(i18n)와 지역화(l10n)를 통해 다양한 언어와 문화를 지원합니다.",
            "반응형 웹 디자인은 다양한 디바이스에서 최적화된 사용자 경험을 제공합니다. CSS Grid, Flexbox, 미디어 쿼리를 활용합니다.",
            "프론트엔드 프레임워크는 사용자 인터페이스를 구축하는 도구입니다. React, Vue.js, Angular 등 다양한 선택지가 있습니다.",
            "모바일 앱 개발은 네이티브 앱과 하이브리드 앱으로 나뉩니다. React Native, Flutter 등을 사용하여 크로스 플랫폼 개발이 가능합니다.",
        };

        String[] authors = {
            "admin", "seonghw", "user2", "admin", "seonghw", "user2", "admin", "seonghw", "user2", "admin",
            "seonghw", "user2", "admin", "seonghw", "user2", "admin", "seonghw", "user2", "admin", "seonghw",
            "user2", "admin", "seonghw", "user2", "admin", "seonghw", "user2", "admin", "seonghw", "user2"
        };

        for (int i = 0; i < titles.length; i++) {
            Post post = Post.builder()
                    .title(titles[i])
                    .content(contents[i])
                    .author(authors[i])
                    .viewCount((int) (Math.random() * 100))
                    .createdAt(LocalDateTime.now().minusDays(i))
                    .build();
            
            postRepository.save(post);
        }

        log.info("테스트 게시글 생성 완료");
    }

} 