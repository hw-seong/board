package com.example.demo.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.demo.post.QPost.post;

@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Post> findByTitleOrContentContaining(String keyword) {
        return queryFactory
                .selectFrom(post)
                .where(post.title.containsIgnoreCase(keyword)
                        .or(post.content.containsIgnoreCase(keyword)))
                .orderBy(post.createdAt.desc())
                .fetch();
    }
    @Override
    public long countByAuthor(String author) {
        return queryFactory
                .select(post.count())
                .from(post)
                .where(post.author.eq(author))
                .fetchOne();
    }

    @Override
    public long getTotalPostCount() {
        return queryFactory
                .select(post.count())
                .from(post)
                .fetchOne();
    }

    @Override
    public List<Post> findRecentPosts(int limit) {
        return queryFactory
                .selectFrom(post)
                .orderBy(post.createdAt.desc())
                .limit(limit)
                .fetch();
    }
} 