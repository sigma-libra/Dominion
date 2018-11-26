package server.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import server.persistence.exception.PersistenceException;

import server.persistence.Persistence;
import server.service.exception.ServiceException;
import server.service.exception.UserAlreadyExistsException;
import server.service.exception.UserNotFoundException;
import server.service.impl.UserServiceImpl;
import shared.dto.UserDTO;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    private static UserDTO no_id_user;
    private static UserDTO user1;
    private static UserDTO user2;

    private final Persistence userPersistenceMock = mock(Persistence.class);

    private final UserService userService = new UserServiceImpl(userPersistenceMock);

    @Before
    public void before(){
        no_id_user = new UserDTO("no id user");
        user1 = new UserDTO(1, "user1");
        user2 = new UserDTO(2, "user2");
    }

    @Test
    public void addUser_ShouldAddUser() throws ServiceException, PersistenceException {
        String username = "username";
        UserDTO user = new UserDTO(username);
        UserDTO user_with_id = new UserDTO(17, username);

        when(userPersistenceMock.addUser(user)).thenReturn(user_with_id);

        userService.addUser(username,"");

        verify(userPersistenceMock, times(1)).addUser(user);
        verifyNoMoreInteractions(userPersistenceMock);
    }

    @Test
    public void addUserWithDuplicateName_ShouldThrow() throws ServiceException, PersistenceException {
        when(userPersistenceMock.addUser(no_id_user)).thenReturn(no_id_user);
        userService.addUser(no_id_user.getUserName(),"");

        UserDTO user = new UserDTO(no_id_user.getUserName());
        when(userPersistenceMock.addUser(user)).thenThrow(new server.persistence.exception.UserAlreadyExistsException(""));
        try {
            userService.addUser(user.getUserName(),"");
            Assert.fail("Inserting user with duplicate username didn't throw exception");
        } catch (UserAlreadyExistsException e){}
    }

    @Test
    public void getAllUsers_ShouldReturnUsers() throws ServiceException, PersistenceException {
        Set<UserDTO> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        when(userPersistenceMock.getAllUsers()).thenReturn(users);

        Set<UserDTO> all_users = userService.getAllUsers();
        Assert.assertEquals(users, all_users);
    }

    @Test
    public void getUserById_ShouldReturnUser() throws ServiceException, PersistenceException {
        when(userPersistenceMock.getUserById(user1.getId())).thenReturn(user1);

        UserDTO user = userService.getUserByID(user1.getId());

        Assert.assertEquals(user1, user);
    }

    @Test(expected = UserNotFoundException.class)
    public void getNonexistentUserById_ShouldThrow() throws ServiceException, PersistenceException {
        when(userPersistenceMock.getUserById(user1.getId())).thenThrow(new server.persistence.exception.UserNotFoundException(""));
        UserDTO user = userService.getUserByID(user1.getId());
    }

    @Test
    public void getUserByUsername_ShouldReturnUser() throws ServiceException, PersistenceException {
        when(userPersistenceMock.getUserByUserName(user1.getUserName())).thenReturn(user1);

        UserDTO user = userService.getUserByUsername(user1.getUserName());

        Assert.assertEquals(user1, user);
    }

    @Test(expected = UserNotFoundException.class)
    public void getNonexistentUserByUsername_ShouldThrow() throws ServiceException, PersistenceException {
        when(userPersistenceMock.getUserByUserName(user1.getUserName())).thenThrow(new server.persistence.exception.UserNotFoundException(""));
        UserDTO user = userService.getUserByUsername(user1.getUserName());
    }
}

