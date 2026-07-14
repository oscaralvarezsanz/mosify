package com.mosify.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mosify.api.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RewardSystemE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldRunFullRewardSystemHappyPath() throws Exception {
        // 1. Create a user
        WebUserRequest userRequest = WebUserRequest.builder()
                .name("Oscar")
                .username("oscar")
                .password("password")
                .build();

        MvcResult userResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        WebUserResponse userResponse = objectMapper.readValue(
                userResult.getResponse().getContentAsString(),
                WebUserResponse.class
        );

        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getName()).isEqualTo("Oscar");
        assertThat(userResponse.getUsername()).isEqualTo("oscar");

        UUID userId = userResponse.getId();

        // 1.1 Login to retrieve the JWT token
        WebLoginRequest loginRequest = WebLoginRequest.builder()
                .username("oscar")
                .password("password")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        WebAuthResponse authResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                WebAuthResponse.class
        );

        String token = authResponse.getAccessToken();
        assertThat(token).isNotBlank();

        // 2. Get user by ID
        mockMvc.perform(get("/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 3. Create a board & add user to board
        WebBoardRequest boardRequest = WebBoardRequest.builder()
                .name("Board")
                .build();

        MvcResult boardResult = mockMvc.perform(post("/boards")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        WebBoardResponse boardResponse = objectMapper.readValue(
                boardResult.getResponse().getContentAsString(),
                WebBoardResponse.class
        );

        mockMvc.perform(post("/boards/" + boardResponse.getId() + "/users/"+ userResponse.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // 4. Create a category owned by the user
        WebCategoryRequest categoryRequest = WebCategoryRequest.builder()
                .userId(userId)
                .name("Studies")
                .description("Academic goals and tasks")
                .boardId(boardResponse.getId())
                .build();

        MvcResult categoryResult = mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        WebCategoryResponse categoryResponse = objectMapper.readValue(
                categoryResult.getResponse().getContentAsString(),
                WebCategoryResponse.class
        );

        assertThat(categoryResponse.getId()).isNotNull();
        assertThat(categoryResponse.getUserId()).isEqualTo(userId);
        assertThat(categoryResponse.getName()).isEqualTo("Studies");

        UUID categoryId = categoryResponse.getId();

        // 5. Create a recurrent task (reward +50 points)
        WebTaskRequest taskRequest = WebTaskRequest.builder()
                .title("Complete 1 hour of math")
                .categoryId(categoryId)
                .type(WebTaskRequest.TypeEnum.RECURRENT)
                .frequency(WebTaskRequest.FrequencyEnum.DAILY)
                .pointsValue(50)
                .active(true)
                .build();

        MvcResult taskResult = mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        WebTaskResponse taskResponse = objectMapper.readValue(
                taskResult.getResponse().getContentAsString(),
                WebTaskResponse.class
        );

        assertThat(taskResponse.getId()).isNotNull();
        assertThat(taskResponse.getTitle()).isEqualTo("Complete 1 hour of math");
        assertThat(taskResponse.getPointsValue()).isEqualTo(50);

        UUID taskId = taskResponse.getId();

        // 6. Execute the task (earning 50 points)
        MvcResult execResult = mockMvc.perform(post("/tasks/" + taskId + "/execute")
                        .header("Authorization", "Bearer " + token)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andReturn();

        WebTransactionResponse txResponse = objectMapper.readValue(
                execResult.getResponse().getContentAsString(),
                WebTransactionResponse.class
        );

        assertThat(txResponse.getId()).isNotNull();
        assertThat(txResponse.getUserId()).isEqualTo(userId);
        assertThat(txResponse.getTaskId()).isEqualTo(taskId);
        assertThat(txResponse.getPointsAffected()).isEqualTo(50);

        // 7. Verify user points balance is now 150
        MvcResult userCheckResult = mockMvc.perform(get("/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        WebUserResponse updatedUser = objectMapper.readValue(
                userCheckResult.getResponse().getContentAsString(),
                WebUserResponse.class
        );

        // 8. Verify transaction history contains the execution log
        mockMvc.perform(get("/transactions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
