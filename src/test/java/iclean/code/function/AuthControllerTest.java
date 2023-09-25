package iclean.code.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import iclean.code.data.dto.request.LoginUsernamePassword;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void testLoginSuccessful() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        LoginUsernamePassword request = new LoginUsernamePassword("nhatlinh", "123");
        String requestBody = objectMapper.writeValueAsString(request);
        this.mockMvc.perform(post("/auth")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testLoginFailedWithWrongUsernamePassword() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        LoginUsernamePassword request = new LoginUsernamePassword("nhatlinh", "124");
        String requestBody = objectMapper.writeValueAsString(request);
        this.mockMvc.perform(post("/auth")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLoginFailedWithMissingUsername() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        LoginUsernamePassword request = new LoginUsernamePassword();
        request.setPassword("123");
        String requestBody = objectMapper.writeValueAsString(request);
        this.mockMvc.perform(post("/auth")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testLoginFailedWithMissingPassword() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        LoginUsernamePassword request = new LoginUsernamePassword();
        request.setPassword("nhatlinh");
        String requestBody = objectMapper.writeValueAsString(request);
        this.mockMvc.perform(post("/auth")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLoginFailedWithMissingForm() throws Exception {

        this.mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
