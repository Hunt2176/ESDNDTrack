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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GSONHelper {
    @NonNull
    Gson gson = new Gson();

    public ReadInSpell[] readInSpells(String fileStringData) {
        return gson.fromJson(fileStringData, ReadInSpell[].class);
    }

    public <T> void writeToDisk(T[] toWrite, File fileToWrite) throws IOException {
        String jsonData = gson.toJson(toWrite);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite));
        writer.write(jsonData);
        writer.close();
    }

    public void addCustomSpell(@NonNull Context context, ReadInSpell spell) {
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

    @Nullable
    public String[][] readInArray(@NonNull KotlinFile fileToRead)
    {
        if (fileToRead.exists())
        {
            return gson.fromJson(fileToRead.readToString(), String[][].class);
        }
        return null;
    }

    @Nullable
    public String[] readInArray(String jsonString)
    {
        try
        {
            return gson.fromJson(jsonString, String[].class);

        } catch (Exception err)
        {
            return null;
        }
    }
}
