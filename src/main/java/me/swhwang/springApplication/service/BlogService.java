package me.swhwang.springApplication.service;

import lombok.RequiredArgsConstructor;
import me.swhwang.springApplication.dao.Article;
import me.swhwang.springApplication.dto.ArticleCreationRequest;
import me.swhwang.springApplication.dto.ArticleUpdateRequest;
import me.swhwang.springApplication.repository.BlogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BlogService {
    public final BlogRepository blogRepository;

    public Article save(ArticleCreationRequest request) {
        return blogRepository.save(request.toEntity());
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article getById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글이 없습니다."));
    }

    public void deleteById(Long id) {
        blogRepository.deleteById(id);
    }

    @Transactional
    public Article updateById(long id, ArticleUpdateRequest request) {

        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        article.update(request.getTitle(), request.getContent());

        return article;
    }
}

