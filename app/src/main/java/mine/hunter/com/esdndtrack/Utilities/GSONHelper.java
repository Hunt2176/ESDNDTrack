package mine.hunter.com.esdndtrack.Utilities;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class GSONHelper {
    Gson gson = new Gson();

    public Spell[] readIn(String data) {
        try {
            return gson.fromJson(data, Spell[].class);
        } catch (Exception e) {
            return null;
        }
    }

    public Spell singleRead(String data) {
        return null;
    }

    public ReadInSpell[] readInSpells(String data) {
        return gson.fromJson(data, ReadInSpell[].class);
    }

    public <T> void writeToDisk(T[] toWrite, File fileToWrite) throws IOException {
        String jsonData = gson.toJson(toWrite);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite));
        writer.write(jsonData);
        writer.close();
    }

    public void addCustomSpell(Context context, ReadInSpell spell) {
        try
        {
            InputStream inputStream = new FileInputStream(new File(context.getFilesDir(), "customspells.json"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String totalInput = "";
            String line;

            while ((line = reader.readLine()) != null)
            {
                totalInput += line;
            }
            ReadInSpell[] readIn = readInSpells(totalInput);
            ArrayList<ReadInSpell> list = new ArrayList<>(Arrays.asList(readIn));
            list.add(spell);
            writeToDisk(list.toArray(), new File(context.getFilesDir(), "customspells.json"));

        }
        catch (IOException error)
        {

        }


    }
}
