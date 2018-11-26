package client.rest;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import client.service.exception.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;
import shared.domain.effect.impl.ChooseCardsEffect;
import shared.domain.effect.cardaction.impl.TrashCardsAction;
import shared.domain.effect.cardsource.impl.SupplyPileSource;
import shared.dto.AuthRequestDTO;
import shared.dto.GameStateDTO;
import shared.dto.UserDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = client.app.MainApplication.class)
public class RestApiTest {

    private static String authToken = "fake_auth_token";

    private static UserDTO user1 = new UserDTO(1, "user1");
    private static UserDTO no_id_user = new UserDTO("no_id_user");

    private static ObjectMapper objectmapper = new ObjectMapper();
    private static String encodeObject(Object o) throws JsonProcessingException {
        return objectmapper.writeValueAsString(o);
    }

    // helper class that creates a RestApi instance and a MockRestServiceServer instance which can be used in tests
    private class TestObjects {
        public RestApi restApi;
        public MockRestServiceServer mockServer;

        public TestObjects() {
            RestTemplate restTemplate = new RestTemplate();
            restApi = new RestApi(restTemplate);
            RestGatewaySupport gateway = new RestGatewaySupport();
            gateway.setRestTemplate(restTemplate);
            mockServer = MockRestServiceServer.bindTo(gateway).build();
        }
    }

    @Test
    public void getUserByUsername_ShouldReturnUser() throws JsonProcessingException, ServiceException {
        TestObjects testObjects = new TestObjects();
        RestApi restApi = testObjects.restApi;
        MockRestServiceServer mockServer = testObjects.mockServer;

        mockServer.expect(once(), requestTo(restApi.getUrl()+"/user/byusername?username=" + user1.getUserName()))
            .andRespond(withSuccess(encodeObject(user1), MediaType.APPLICATION_JSON));

        UserDTO user = restApi.getUserByUsername(user1.getUserName());

        mockServer.verify();
        Assert.assertEquals(user1, user);
    }

    @Test
    public void getNonexistentUserByUsername_ShouldThrow() throws JsonProcessingException, ServiceException {
        TestObjects testObjects = new TestObjects();
        RestApi restApi = testObjects.restApi;
        MockRestServiceServer mockServer = testObjects.mockServer;

        mockServer.expect(once(), requestTo(restApi.getUrl() + "/user/byusername?username=" + user1.getUserName()))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));

        try {
            restApi.getUserByUsername(user1.getUserName());
            Assert.fail("getUserByUsername didn't throw exception");
        } catch (GetUserInfoException e) {}

        mockServer.verify();
    }

    @Test
    public void addUser_ShouldAddUser() throws JsonProcessingException, ServiceException {
        TestObjects testObjects = new TestObjects();
        RestApi restApi = testObjects.restApi;
        MockRestServiceServer mockServer = testObjects.mockServer;

        AuthRequestDTO authRequestDTO = new AuthRequestDTO(no_id_user.getUserName(), "");

        mockServer.expect(once(), requestTo(restApi.getUrl() + "/user/add"))
            .andExpect(content().string(encodeObject(authRequestDTO)))
            .andRespond(withSuccess(encodeObject(no_id_user), MediaType.APPLICATION_JSON));

        UserDTO user = restApi.addUser(authRequestDTO);

        mockServer.verify();
        Assert.assertEquals(no_id_user, user);
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void addExistingUser_ShouldThrow() throws JsonProcessingException, ServiceException {
        TestObjects testObjects = new TestObjects();
        RestApi restApi = testObjects.restApi;
        MockRestServiceServer mockServer = testObjects.mockServer;

        AuthRequestDTO authRequestDTO = new AuthRequestDTO(no_id_user.getUserName(), "");

        mockServer.expect(once(), requestTo(restApi.getUrl() + "/user/add"))
            .andExpect(content().string(encodeObject(authRequestDTO)))
            .andRespond(withStatus(HttpStatus.CONFLICT));

        restApi.addUser(authRequestDTO);
    }

    @Test
    public void joinGame_ShouldWork() throws JsonProcessingException, ServiceException {
        TestObjects testObjects = new TestObjects();
        RestApi restApi = testObjects.restApi;
        MockRestServiceServer mockServer = testObjects.mockServer;

        mockServer.expect(once(), requestTo(restApi.getUrl()+"/game/join"))
            .andExpect(header("Token", authToken))
            .andRespond(withSuccess(encodeObject(user1), MediaType.APPLICATION_JSON));

        restApi.joinGame(authToken);

        mockServer.verify();
    }

    @Test
    public void getUsersInGame_ShouldReturnUsers() throws JsonProcessingException, ServiceException {
        TestObjects testObjects = new TestObjects();
        RestApi restApi = testObjects.restApi;
        MockRestServiceServer mockServer = testObjects.mockServer;

        Set<UserDTO> users = new HashSet<>();
        users.add(user1);
        users.add(no_id_user);
        mockServer.expect(once(), requestTo(restApi.getUrl()+"/game/getplayers"))
            .andRespond(withSuccess(encodeObject(users), MediaType.APPLICATION_JSON));

        Set<UserDTO> users_in_game = restApi.getUsersInGame();

        mockServer.verify();
        Assert.assertEquals(users, users_in_game);
    }

    @Test
    public void getGamestate_ShouldReturnGamestate() throws JsonProcessingException, ServiceException {
        TestObjects testObjects = new TestObjects();
        RestApi restApi = testObjects.restApi;
        MockRestServiceServer mockServer = testObjects.mockServer;

        GameStateDTO gameState = new GameStateDTO();
        List<Integer> cards = new ArrayList<>();
        cards.add(11);
        cards.add(12);
        cards.add(13);
        ChooseCardsEffect effect = new ChooseCardsEffect(new SupplyPileSource(3), new TrashCardsAction(), 1, 2);
        effect.setChoices(cards);
        gameState.setPendingChoiceEffect(effect);

        mockServer.expect(once(), requestTo(restApi.getUrl()+"/game/getgamestate"))
            .andRespond(withSuccess(encodeObject(gameState), MediaType.APPLICATION_JSON));

        GameStateDTO receivedState = restApi.getGameState(authToken);

        mockServer.verify();

        Assert.assertThat(receivedState.getPendingChoiceEffect(), instanceOf(ChooseCardsEffect.class));
        effect = (ChooseCardsEffect)receivedState.getPendingChoiceEffect();
        Assert.assertThat(effect.getFrom(), is(1));
        Assert.assertThat(effect.getUpTo(), is(2));
        Assert.assertThat(effect.getChoices(), contains(11, 12, 13));
    }
}
