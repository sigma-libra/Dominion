package server.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import server.app.ServerApplication;
import server.service.exception.UserAlreadyExistsException;
import server.service.exception.UserNotFoundException;
import server.service.UserService;
import shared.dto.AuthRequestDTO;
import shared.dto.UserDTO;

import java.nio.charset.Charset;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ServerApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(),
        Charset.forName("utf8")
    );

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userServiceMock;

    private static ObjectMapper objectmapper = new ObjectMapper();
    private static String encodeObject(Object o) throws JsonProcessingException {
        return objectmapper.writeValueAsString(o);
    }

    @Test
    public void getAllUsers_ShouldReturnFoundUsers() throws Exception {
        Set users = new LinkedHashSet();
        users.add(new UserDTO("a","x"));
        users.add(new UserDTO("b","x"));

        when(userServiceMock.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/user/all"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].username", is("a")))
            .andExpect(jsonPath("$[1].username", is("b")));

        verify(userServiceMock, times(1)).getAllUsers();
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    public void getUserById_ShouldReturnUser() throws Exception {
        UserDTO user = new UserDTO("a","x");
        int id = 4;
        user.setId(id);

        when(userServiceMock.getUserByID(id)).thenReturn(user);

        mockMvc.perform(get("/user/byid")
                        .param("id", Integer.toString(id)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.username", is(user.getUserName())))
            .andExpect(jsonPath("$.id", is(id)));

        verify(userServiceMock, times(1)).getUserByID(id);
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    public void getUserByInvalidId_ShouldReturnError() throws Exception {
        int id = 4;

        when(userServiceMock.getUserByID(id)).thenThrow(new UserNotFoundException(""));

        mockMvc.perform(get("/user/byid")
            .param("id", Integer.toString(id)))
            .andExpect(status().isNotFound());

        verify(userServiceMock, times(1)).getUserByID(id);
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    public void getUserByUsername_ShouldReturnUser() throws Exception {
        String username = "a";
        String password = "x";
        int id = 7;
        UserDTO user = new UserDTO(username,password);
        user.setId(id);

        when(userServiceMock.getUserByUsername(username)).thenReturn(user);

        mockMvc.perform(get("/user/byusername")
                        .param("username", username))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.username", is(username)))
            .andExpect(jsonPath("$.id", is(id)));

        verify(userServiceMock, times(1)).getUserByUsername(username);
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    public void getUserByInvalidUsername_ShouldReturnError() throws Exception {
        String username = "a";

        when(userServiceMock.getUserByUsername(username)).thenThrow(new UserNotFoundException(""));

        mockMvc.perform(get("/user/byusername")
                        .param("username", username))
            .andExpect(status().isNotFound());

        verify(userServiceMock, times(1)).getUserByUsername(username);
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    public void addUser_ShouldAddUser() throws Exception {
        String username = "foobar";
        String password = "x";
        UserDTO user = new UserDTO(username,password);

        when(userServiceMock.addUser(username,password)).thenReturn(user);

        AuthRequestDTO authRequestDTO = new AuthRequestDTO(username, password);
        mockMvc.perform(post("/user/add")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(encodeObject(authRequestDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.username", is(username)));

        verify(userServiceMock, times(1)).addUser(username,password);
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    public void addingExistingUser_ShouldReturnError() throws Exception {
        String username = "foobar";
        String password = "x";
        when(userServiceMock.addUser(username,password)).thenThrow(new UserAlreadyExistsException(""));

        AuthRequestDTO authRequestDTO = new AuthRequestDTO(username, password);
        mockMvc.perform(post("/user/add")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(encodeObject(authRequestDTO)))
            .andExpect(status().isConflict());

        verify(userServiceMock, times(1)).addUser(username,password);
        verifyNoMoreInteractions(userServiceMock);
    }
}
