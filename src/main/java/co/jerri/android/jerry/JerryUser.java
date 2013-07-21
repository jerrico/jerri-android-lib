package co.jerri.android.jerry;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Class;
import java.util.Iterator;
import org.json.*;
import java.util.logging.Logger;

class Restriction {

	protected JSONObject settings;

	public Restriction() {
		settings = null;
	}

	public Restriction(JSONObject new_settings){
		settings = new_settings;
	}

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

public class JerryUser {
	public String user_id;
	public String device_id;
	public String profile_name;
	public boolean default_reaction;
	public HashMap<String, ArrayList<Restriction>> restrictions;
	private JSONObject profile_state;

	private static Logger LOGGER = Logger.getLogger("JerryUser");

	public void load_state(JSONObject input){
		if (restrictions == null){
			restrictions = new HashMap<String, ArrayList<Restriction>>();
		}
		profile_name = input.optString("profile_name");
		profile_state = input;
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
					LOGGER.info("pling");
					restObj = new BinaryRestriction(restJSON);
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

	public JerryUser(String n_user_id, String n_device_id, JSONObject new_state) {
		user_id = n_user_id;
		device_id = n_device_id;
		profile_name = null;
		load_state(new_state);
	}
	
	public JerryUser(String n_user_id, String n_device_id){
		user_id = n_user_id;
		device_id = n_device_id;
		profile_name = null;
	}
	public JerryUser(String n_user_id){
		user_id = n_user_id;
		device_id = null;
		profile_name = null;
	}
}