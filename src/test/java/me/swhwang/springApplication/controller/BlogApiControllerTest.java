
package me.swhwang.springApplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.swhwang.springApplication.dao.Article;
import me.swhwang.springApplication.dto.ArticleCreationRequest;
import me.swhwang.springApplication.dto.ArticleUpdateRequest;
import me.swhwang.springApplication.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach
    public void setUp() {
        blogRepository.deleteAll();
    }

    // 테스트 1. 글 생성 - addArticle()
    @DisplayName("addArticle: 블로그 글 생성에 성공한다.")
    @Test
    public void addArticle() throws Exception {
        // Given
        String title = "블로그 게시글 생성";
        String content = "게시글 생성 기능";
        String url = "/api/articles";

        ArticleCreationRequest req =
                new ArticleCreationRequest(title, content);

        String articleStr = objectMapper.writeValueAsString(req);

        // When
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(articleStr));

        // Then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);

        Article article = articles.get(0);

        assertThat(article.getTitle()).isEqualTo(title);
        assertThat(article.getContent()).isEqualTo(content);
    }

    // 테스트 2. 글 조회 - findArticle()
    @DisplayName("findArticle: 블로그 글 1건 조회에 성공한다")
    @Test
    public void findArticle() throws Exception {

        // Given
        String title = "블로그 게시글 조회";
        String content = "게시글 1건 조회 기능";
        String url = "/api/articles/{id}";

        Article savedArticle = blogRepository.save(
                Article.builder()
                        .title(title)
                        .content(content)
                        .build()
        );

        // When
        ResultActions result = mockMvc.perform(
                get(url, savedArticle.getId())
                        .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content));
    }


    // 테스트 3. 글 수정 - updateArticle()
    @DisplayName("updateArticle: 블로그 글 수정에 성공한다")
    @Test
    public void updateArticle() throws Exception {

        // Given
        String title = "블로그 게시글 수정";
        String content = "게시글 수정 전 내용";

        String updateTitle = "수정된 블로그 게시글";
        String updateContent = "게시글 수정 완료";

        String url = "/api/articles/{id}";

        Article savedArticle = blogRepository.save(
                Article.builder()
                        .title(title)
                        .content(content)
                        .build()
        );

        ArticleUpdateRequest req =
                new ArticleUpdateRequest(updateTitle, updateContent);

        String articleStr = objectMapper.writeValueAsString(req);

        // When
        ResultActions result = mockMvc.perform(
                put(url, savedArticle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleStr)
        );

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateTitle))
                .andExpect(jsonPath("$.content").value(updateContent));

        Article article = blogRepository.findById(
                savedArticle.getId()
        ).orElseThrow();

        assertThat(article.getTitle()).isEqualTo(updateTitle);
        assertThat(article.getContent()).isEqualTo(updateContent);
    }


    // 테스트 4. 글 삭제 - deleteArticle()
    @DisplayName("deleteArticle: 블로그 글 삭제에 성공한다")
    @Test
    public void deleteArticle() throws Exception {

        // Given
        String title = "블로그 게시글 삭제";
        String content = "게시글 삭제 기능";
        String url = "/api/articles/{id}";

        Article savedArticle = blogRepository.save(
                Article.builder()
                        .title(title)
                        .content(content)
                        .build()
        );

        // When
        ResultActions result = mockMvc.perform(
                delete(url, savedArticle.getId())
        );

        // Then
        result.andExpect(status().isOk());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles).isEmpty();
    }


    // 테스트 5. 글 목록 조회 - findAllArticles()
    @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공한다")
    @Test
    public void findAllArticles() throws Exception {

        // Given
        String url = "/api/articles";

        String title1 = "첫 번째 블로그 게시글";
        String content1 = "첫 번째 게시글 조회 기능";

        String title2 = "두 번째 블로그 게시글";
        String content2 = "두 번째 게시글 조회 기능";

        blogRepository.save(
                Article.builder()
                        .title(title1)
                        .content(content1)
                        .build()
        );

        blogRepository.save(
                Article.builder()
                        .title(title2)
                        .content(content2)
                        .build()
        );

        // When
        ResultActions result = mockMvc.perform(
                get(url).accept(MediaType.APPLICATION_JSON)
        );

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value(title1))
                .andExpect(jsonPath("$[0].content").value(content1))
                .andExpect(jsonPath("$[1].title").value(title2))
                .andExpect(jsonPath("$[1].content").value(content2));
    }
}
