package co.jerri.android.jerry;

import junit.framework.TestCase;
import org.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import co.jerri.android.jerry.JerryUser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class JerryUserTest {

    private JerryUser user;

    @Test
    public void shouldHaveHappySmiles() throws Exception {
        user = new JerryUser();
        assertThat(user.user_id, equalTo("error"));
    }
}