package co.jerri.android.jerry;

import junit.framework.TestCase;
import org.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import co.jerri.android.jerry.JerryUser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class JerryUserTest {

    private JerryUser user;
    private String json_data = "\"profile\": \"free\",\"default\": \"deny\",\"account\": {    \"credits\": 100},\"states\": {    \"take_photo\": [        {\"class_\": \"BinaryRestriction\", \"allow\": True}    ],    \"take_photo_private\": [        {\"class_\": \"AccountAmountRestriction\",                \"account_item\": \"credtis\"}   # typo    ],    \"upload_photo\": [        {\"class_\": \"PerTimeRestriction\", \"limit_to\": 100,                \"duration\": 24 * 60 * 60, \"left\": 3}    ],    \"share_photo\": [        {\"class_\": \"PerTimeRestriction\", \"limit_to\": 100,                \"duration\": 24 * 60 * 60, \"left\": 20},        {\"class_\": \"TotalAmountRestriction\", \"total_max\": 100,                \"left\": 10}    ],    \"share_photo_private\": [        {\"class_\": \"AccountAmountRestriction\",                \"account_item\": \"credits\",                 \"quantity_change\": 35}    ],}}";

    @Before
    public void setUp() throws Exception {
    	user = new JerryUser("Mr T.");
        user.load_state(json_data);
    }

    @Test
    public void simpleIDTest() throws Exception {
        assertThat(user.user_id, equalTo("Mr T."));
    }


    @Test
    public void simpleDeviceTest() throws Exception {
    	user = new JerryUser(null, "1234");
        assertThat(user.user_id, equalTo(null));
        assertThat(user.device_id, equalTo("1234"));
    }
}