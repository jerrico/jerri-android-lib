package co.jerri.android.jerry;

import java.lang.String;
import java.util.List;
import java.util.HashMap;

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

public class JerryUser {
	public String user_id;
	public String device_id;
	public String profile_name;
	public boolean default_reaction;
	private HashMap<String, List<Restriction>> restrictions;
	private String profile_state;

	public void load_state(String input){

	}

	public JerryUser(String n_user_id, String n_device_id, String new_state) {
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