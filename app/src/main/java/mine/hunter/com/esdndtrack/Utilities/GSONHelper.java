package mine.hunter.com.esdndtrack.Utilities;

import com.google.gson.Gson;

public class GSONHelper {
	Gson gson = new Gson();
	
    public Spell[] readIn (String data){
        try {
            return gson.fromJson(data, Spell[].class);
        } catch (Exception e) {
            return null;
        }
    }
    
    public Spell singleRead (String data) {
	    return null;
    }
    
    public ReadInSpell[] readInSpells(String data){
    	return gson.fromJson(data, ReadInSpell[].class);
    }
}
