package co.jerri.android.jerry;

import junit.framework.TestCase;
import org.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import co.jerri.android.jerry.JerryUser;
import org.json.JSONObject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.fest.assertions.api.Assertions.*;

@RunWith(RobolectricTestRunner.class)
public class JerryUserTest {

    private JerryUser user;
    private String json_data = "{\"profile\": \"free\",\"default\": \"deny\",\"account\": {\"credits\": 100},\"states\": {\"take_photo\": [{\"class_\": \"BinaryRestriction\", \"allow\": True}], \"take_photo_private\": [{\"class_\": \"AccountAmountRestriction\",\"account_item\": \"credtis\"}], \"upload_photo\": [ {\"class_\": \"PerTimeRestriction\", \"limit_to\": 100,\"duration\": 86400, \"left\": 3}],\"share_photo\": [ {\"class_\": \"PerTimeRestriction\", \"limit_to\": 100,\"duration\": 86400, \"left\": 20},{\"class_\": \"TotalAmountRestriction\", \"total_max\": 100,\"left\": 10}],\"share_photo_private\": [{\"class_\": \"AccountAmountRestriction\",\"account_item\": \"credits\", \"quantity_change\": 35}]}}";

    @Test
    public void simpleIDTest() throws Exception {
		user = new JerryUser("Mr T.");
        assertThat(user.user_id).isEqualTo("Mr T.");
        assertThat(user.device_id).isEqualTo(null);
    }


    @Test
    public void simpleDeviceTest() throws Exception {
    	user = new JerryUser(null, "1234");
        assertThat(user.user_id).isEqualTo(null);
        assertThat(user.device_id).isEqualTo("1234");
    }

    @Test
    public void simpleCan() throws Exception {
    	user = new JerryUser("Mr T.");
    	JSONObject jsonobj = new JSONObject(json_data);
    	user.load_state(jsonobj);
        assertThat(user.can("take_photo")).isTrue();
		assertThat(user.can("take_photo", 10)).isTrue();
	}

    @Test
    public void canExtended() throws Exception {
    	user = new JerryUser("Mr T.");
    	JSONObject jsonobj = new JSONObject(json_data);
    	user.load_state(jsonobj);
    	// assertThat(user.restrictions.size()).isEqualTo(1);
        assertThat(user.can("take_photo")).isTrue();
		assertThat(user.can("take_photo", 10)).isTrue();

		assertThat(user.can("upload_photo")).isTrue();
		assertThat(user.can("upload_photo", 3)).isTrue();
		assertThat(user.can("upload_photo", 4)).isFalse();

		assertThat(user.can("share_photo")).isTrue();
		assertThat(user.can("share_photo", 9)).isTrue();
	    // total amount kicks in though daily woud allow it
		assertThat(user.can("share_photo", 12)).isFalse();

		// // do we have credits to share photos privately?
		// assertThat(user.can("share_photo_private")).isTrue();
		// assertThat(user.can("share_photo_private", 2)).isTrue();
		// assertThat(user.can("share_photo_private", 3)).isFalse();

		// // typo
		// assertThat(user.can("take_photo_private")).isFalse();

		// // do we have credits to share photos privately?
		// user.did("share_photo_private", 1)
		// mocky.calls.should.have.length_of(1)
		// user.can("share_photo_private").should.be.ok
		// user.did("share_photo_private", 2)
		// mocky.calls.should.have.length_of(2)
		// user.can("share_photo_private").should.be.false

		// // not defined: default is deny
		// user.can("view_photo").should_not.be.ok
		// user.can("view_photo", 10).should_not.be.ok

		// // lets modify and play again:
		// user.did("upload_photo")
		// mocky.calls.should.have.length_of(3)
		// user.can("upload_photo").should.be.ok
		// user.can("upload_photo", 2).should.be.ok
		// user.did("upload_photo", 2)
		// mocky.calls.should.have.length_of(4)
		// user.can("upload_photo").should_not.be.ok
    }
}