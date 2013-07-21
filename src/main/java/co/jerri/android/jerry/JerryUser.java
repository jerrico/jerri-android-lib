package co.jerri.android.jerry;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Class;
import java.util.Iterator;
import co.jerri.android.jerry.ProviderInterface;
import org.json.*;
import java.util.logging.Logger;

class Restriction {

	public boolean allows(String attr, int change){
		return false;
	}

	public boolean allows(String attr){
		return allows(attr, 1);
	}

	public void did(String attr, int change){

	}
	public void did(String attr){
		did(attr, 1);
	}
}

class BinaryRestriction extends Restriction {

	private boolean allow;

	public BinaryRestriction(JSONObject settings){
		allow = settings.optBoolean("allow", false);
	}

	public boolean allows(String attr, int change){
		return allow;
	}
}

class TotalAmountRestriction extends Restriction {
	private int left;

	public TotalAmountRestriction(JSONObject settings){
		left = settings.optInt("left", 0);
	}

	public boolean allows(String attr, int change){
		if (left <= 0){
			return false;
		}

        return (left - change) >= 0;
	}

	public void did(String attr, int change){
		left -= change;
	}
}

class PerTimeRestriction extends Restriction{

	private int left;

	public PerTimeRestriction(JSONObject settings){
		left = settings.optInt("left", 0);
	}

	public boolean allows(String attr, int change){
		if (left <= 0){
			return false;
		}

        return (left - change) >= 0;
	}

	public void did(String attr, int change){
		left -= change;
	}
}

class AccountAmountRestriction extends Restriction {

	private JerryUser user;
	private String account_item;
	private int quantity_change;

	public AccountAmountRestriction(JerryUser jUser, JSONObject settings){
		user = jUser;
		account_item = settings.optString("account_item", "");
		quantity_change = settings.optInt("quantity_change", 1);
	}

	public boolean allows(String attr, int change){
		return (user.account_data.optInt(account_item, 0) - (quantity_change * change)) > 0;
	}
	public void did(String attr, int change){
		try {
		user.account_data.put(account_item,
				user.account_data.optInt(account_item, 0) - (quantity_change * change));

		} catch(org.json.JSONException exc) {
			// what the ...
		}
	}
}

public class JerryUser {
	public String user_id;
	public String device_id;
	public String profile_name;
	public boolean default_reaction;
	public JSONObject account_data;
	public boolean loaded;
	public HashMap<String, ArrayList<Restriction>> restrictions;

	private ProviderInterface provider;
	private JSONObject profile_state;

	private static Logger LOGGER = Logger.getLogger("JerryUser");

	private void defaultSetup(){
		this.device_id = null;
		this.user_id = null;
		this.loaded = false;
		this.profile_name = "";
		this.provider = null;
		this.restrictions = new HashMap<String, ArrayList<Restriction>>();
	}

	public JerryUser(String user_id, String device_id, ProviderInterface provider) {
		this.defaultSetup();
		this.user_id = user_id;
		this.device_id = device_id;
		this.provider = provider;
	}
	
	public JerryUser(String user_id, String device_id){
		this.defaultSetup();
		this.user_id = user_id;
		this.device_id = device_id;
	}
	public JerryUser(String user_id){
		this.defaultSetup();
		this.user_id = user_id;
	}

	public void load_state(JSONObject input){
		profile_name = input.optString("profile_name");
		profile_state = input;
		account_data = input.optJSONObject("account");
		default_reaction = input.optString("default_reaction", "deny").equals("allow");
		JSONObject states = input.optJSONObject("states");

		Iterator<String> state_names = states.keys();

		LOGGER.info("loading");

		while(state_names.hasNext()) {
			String restriction_name = state_names.next();
			LOGGER.info("Checking: " + restriction_name);
			JSONArray restrictionsJSON = states.optJSONArray(restriction_name);
			ArrayList<Restriction> restrictionsList = new ArrayList<Restriction>();
			LOGGER.info("ListLen: " + restrictionsJSON.length());
			for (int x=0; x < restrictionsJSON.length(); x++){
				JSONObject restJSON = restrictionsJSON.optJSONObject(x);
				if (restJSON == null) { // does not actually ever happen in production
					LOGGER.warning("Empty objects... hu?");
					continue;
				} 

				String className = restJSON.optString("class_");
				if (className == null) {
					LOGGER.warning("Object misses class_ definition");
					continue;
				}
				Restriction restObj = null;

				// A map here would be nicer of course
				if (className.equals("BinaryRestriction")) {
					restObj = new BinaryRestriction(restJSON);
				} else if (className.equals("PerTimeRestriction")) {
					restObj = new PerTimeRestriction(restJSON);
				} else if (className.equals("TotalAmountRestriction")) {
					restObj = new TotalAmountRestriction(restJSON);
				} else if (className.equals("AccountAmountRestriction")) {
					restObj = new AccountAmountRestriction(this, restJSON);
				}


				if (restObj != null) {
					restrictionsList.add(restObj);
				} else {
					LOGGER.info("Restriction Unknown: " + className);
				}
				
			}
			LOGGER.info("Restlist: " + restrictionsList.size());
			if (restrictionsList.size() > 0) {
				restrictions.put(restriction_name, restrictionsList);
			}
		}
		this.loaded = true;

	}

	public boolean can(String attr, int change){
		ArrayList<Restriction> states = restrictions.get(attr);
		if (states != null) {
			for(int x=0; x < states.size(); x++){
				if (states.get(x).allows(attr, change) == false) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean can(String attr) {
		return can(attr, 1);
	}

	public void did(String attr, int change){
		ArrayList<Restriction> states = restrictions.get(attr);
		if (states != null) {
			for(int x=0; x < states.size(); x++){
				states.get(x).did(attr, change);
			}
		}
	}

	public void did(String attr) {
		did(attr, 1);
	}

}