package co.jerri.android.jerry;

import java.lang.String;

public interface ProviderInterface{

	public void did(String action, int change);
	public void did(String action);

}