package org.dhicc.parkingserviceonboarding.logging;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.awaitility.Awaitility;
import org.dhicc.parkingserviceonboarding.reposiotry.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class LoggingAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ElasticsearchClient elasticsearchClient;  // ✅ 최신 Java API Client 사용

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // 기존 사용자 삭제
    }


    @Test
    void testRegisterUser_andCheckLogInElasticsearch() throws Exception {
        String userJson = """
            {
                "username": "testuser",
                "email": "testuser@example.com",
                "password": "password123",
                "role": "ROLE_USER"
            }
        """;

        // ✅ 1. 회원가입 API 호출
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("회원가입이 완료되었습니다."));

        // ✅ 2. Elasticsearch에 로그 저장 여부 확인
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(hasLogInElasticsearch("api-logs")).isTrue();
        });
    }

    private boolean hasLogInElasticsearch(String indexName) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s.index(indexName));
        SearchResponse<Object> searchResponse = elasticsearchClient.search(searchRequest, Object.class);

        for (Hit<Object> hit : searchResponse.hits().hits()) {
            System.out.println("[Elasticsearch 로그 확인] " + hit.source());
        }

        return searchResponse.hits().total().value() > 0;
    }
}
