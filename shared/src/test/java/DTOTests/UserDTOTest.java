package DTOTests;

import org.junit.Assert;
import org.junit.Test;
import shared.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserDTOTest {
    @Test
    public void test_userDTO_equality(){
        UserDTO a = new UserDTO("a","x");
        UserDTO b = new UserDTO("b","x");

        assertThat(a, is(not(b)));
    }

    @Test
    public void test_userDTO_isEqual() {
        UserDTO a1 = new UserDTO("a", "x");
        UserDTO a2 = new UserDTO("a", "x");
        Assert.assertEquals(a1, a2);
    }

    @Test
    public void contains_test() {
        UserDTO a = new UserDTO("a","x");
        UserDTO b = new UserDTO("b","x");
        UserDTO a2 = new UserDTO("a", "x");

        List<UserDTO> list = new ArrayList<>();
        list.add(a);
        list.add(b);

        Assert.assertTrue(list.contains(a2));

    }
}
