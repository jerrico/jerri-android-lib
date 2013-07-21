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
}