package com.example.retix.controller;

import com.example.retix.model.User;
import com.example.retix.model.Role;
import com.example.retix.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("securePassword123");
        user.setRole(Role.BUYER);
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(user);

        String userJson = "{ \"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"password\": \"securePassword123\", \"role\": \"BUYER\" }";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateUserWithLowerCaseRole() throws Exception {
        User user = new User();
        user.setName("Alice Smith");
        user.setEmail("alice.smith@example.com");
        user.setPassword("password123");
        user.setRole(Role.SELLER);
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(user);

        String userJson = "{ \"name\": \"Alice Smith\", \"email\": \"alice.smith@example.com\", \"password\": \"password123\", \"role\": \"seller\" }";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetUserByEmail() throws Exception {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("securePassword123");
        user.setRole(Role.BUYER);
        Mockito.when(userService.getUserByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"password\":\"securePassword123\",\"role\":\"BUYER\"}"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setName("Jane Doe");
        updatedUser.setEmail("jane.doe@example.com");
        updatedUser.setPassword("newPassword456");
        updatedUser.setRole(Role.SELLER);

        Mockito.when(userService.updateUser(Mockito.eq(1L), Mockito.any(User.class))).thenReturn(Optional.of(updatedUser));

        String updatedUserJson = "{ \"name\": \"Jane Doe\", \"email\": \"jane.doe@example.com\", \"password\": \"newPassword456\", \"role\": \"SELLER\" }";

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"name\":\"Jane Doe\",\"email\":\"jane.doe@example.com\",\"password\":\"newPassword456\",\"role\":\"SELLER\"}"));
    }

    @Test
    public void testUpdateUserWithLowerCaseRole() throws Exception {
        User updatedUser = new User();
        updatedUser.setName("Bob Brown");
        updatedUser.setEmail("bob.brown@example.com");
        updatedUser.setPassword("newPassword789");
        updatedUser.setRole(Role.BUYER);

        Mockito.when(userService.updateUser(Mockito.eq(2L), Mockito.any(User.class))).thenReturn(Optional.of(updatedUser));

        String updatedUserJson = "{ \"name\": \"Bob Brown\", \"email\": \"bob.brown@example.com\", \"password\": \"newPassword789\", \"role\": \"buyer\" }";

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"name\":\"Bob Brown\",\"email\":\"bob.brown@example.com\",\"password\":\"newPassword789\",\"role\":\"BUYER\"}"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
