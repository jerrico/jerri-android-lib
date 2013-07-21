package co.jerri.android.jerry;

import junit.framework.TestCase;
import org.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import co.jerri.android.jerry.AndroidProvider;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.fest.assertions.api.Assertions.*;

@RunWith(RobolectricTestRunner.class)
public class AndroidProviderTest {

    private AndroidProvider provider;

    @Test
    public void simpleSignatureTest() throws Exception {
		provider = new AndroidProvider("key1234", "secret1455");
        assertThat(
        	provider.sign("GET", "http://api.jerri.co/api/v1/permission_state", "user_id=asdfa&device_id=asdfadsf")
        ).isEqualTo("_key=key1234&user_id=asdfa&device_id=asdfadsf&_signature=nSDAnCS1lmbCbCKToGedEvEEjYvt8gvJ2EzSpFWxzcw%3D%0A");
    }

    @Test
    public void littleMoreComplexSignatureTest() throws Exception {
		provider = new AndroidProvider("key1234", "secret1455");
        assertThat(
        	provider.sign("POST", "http://api.jerri.co/api/v1/logger", "entries=%5B%7B%22action%22%3A+%22take_photo%22%7D%5D&user_id=231499435803498513425&device_id=None")
        ).isEqualTo("_key=key1234&entries=%5B%7B%22action%22%3A+%22take_photo%22%7D%5D&user_id=231499435803498513425&device_id=None&_signature=B%2B%2BsEUCoqFfALoBcKNFdeWBkD745O549T2mYQNXEhEA%3D%0A");
    }

    @Test
    public void lowercaseMethodSignatureTest() throws Exception {
		provider = new AndroidProvider("key1234", "secret1455");
        assertThat(
        	provider.sign("post", "http://api.jerri.co/api/v1/logger", "entries=%5B%7B%22action%22%3A+%22take_photo%22%7D%5D&user_id=231499435803498513425&device_id=None")
        ).isEqualTo("_key=key1234&entries=%5B%7B%22action%22%3A+%22take_photo%22%7D%5D&user_id=231499435803498513425&device_id=None&_signature=B%2B%2BsEUCoqFfALoBcKNFdeWBkD745O549T2mYQNXEhEA%3D%0A");
    }
}