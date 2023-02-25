package kww.useless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import fluddokt.opsu.fake.File;
import fluddokt.opsu.fake.Log;
import itdelatrisu.opsu.ErrorHandler;
import itdelatrisu.opsu.beatmap.Beatmap;
import itdelatrisu.opsu.beatmap.HitObject;
import itdelatrisu.opsu.beatmap.TimingPoint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FakeBeatmap {
    @Getter private Beatmap beatmap;
    @Getter private String name;
    @Getter private boolean constructed;
    @Getter
    @Setter(AccessLevel.PRIVATE) // private setter xD
    private String cause = "";

    /**
     * @param folderName the folder, in which "map" files are stored
     * @param osuName    the .osu file needed to be parsed to get song's metadata.
     *                   The audio file will be retrieved from this file
     */
    public FakeBeatmap(String folderName, String osuName)
    {
        this(folderName, null, osuName);
    }

    /**
     * @param folderName    the folder name, in which "map" files are stored
     * @param audioFilename the audio file, that should be played in this theme.
     *                      If {@code null}, audio filename will be retrieved from the .osu file
     * @param osuName       the .osu file needed to be parsed to get song's metadata
     */
    public FakeBeatmap(String folderName, String audioFilename, String osuName)
    {
        this.name = folderName;
        FileHandle dir = Gdx.files.internal("tracks/" + folderName);
        this.beatmap = parseFile(dir.child(osuName), audioFilename, osuName);

        if (cause.isEmpty())
        {
            this.constructed = true; // todo adjust
        }
    }

    /**
     * Tries to parse a beatmap file.
     *
     * @param fileHandle    the internal file to parse
     * @param audioFilename if specified, this file will be used as theme music
     * @param osuFilename   needed for error logging (in other words, unused)
     *
     * @return the brand new fake {@link Beatmap}
     */
    private Beatmap parseFile(FileHandle fileHandle, String audioFilename, String osuFilename)
    {
        System.out.println("Parsing: " + osuFilename);
        Beatmap beatmap = new Beatmap(null);
        beatmap.timingPoints = new ArrayList<>();

        try (
                InputStream inputStream = fileHandle.read();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        )
        {
            String line = reader.readLine();
            String[] tokens = null;
            while (line != null)
            {
                line = line.trim();
                if (!isValidLine(line))
                {
                    line = reader.readLine();
                    continue;
                }
                switch (line)
                {
                    case "[General]":
                    {
                        if (audioFilename != null)
                        {
                            beatmap.audioFilename = new File(audioFilename);
                        }
                        else
                        {
                            while ((line = reader.readLine()) != null)
                            {
                                line = line.trim();
                                if (!isValidLine(line))
                                    continue;
                                if (line.charAt(0) == '[')
                                    break;
                                if ((tokens = tokenize(line)) == null)
                                    continue;
                                try
                                {
                                    /*switch (tokens[0])
                                    {
                                        case "AudioFilename":
                                            beatmap.audioFilename = new File(tokens[1]);
                                            break;
                                    }*/
                                    //if(tokens[0] == "AudioFilename")
                                    if (tokens[0].equals("AudioFilename"))
                                    {
                                        beatmap.audioFilename = new File(tokens[1]);
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.warn(String.format("Failed to read line '%s' for file '%s'.", line, osuFilename), e);
                                }
                            }
                        }
                        break;
                    }
                    case "[Metadata]":
                        while ((line = reader.readLine()) != null)
                        {
                            line = line.trim();
                            if (!isValidLine(line))
                                continue;
                            if (line.charAt(0) == '[')
                                break;
                            if ((tokens = tokenize(line)) == null)
                                continue;
                            try
                            {
                                switch (tokens[0])
                                {
                                    case "Title":
                                        beatmap.title = tokens[1];
                                        break;
                                    case "TitleUnicode":
                                        beatmap.titleUnicode = tokens[1];
                                        break;
                                    case "Artist":
                                        beatmap.artist = tokens[1];
                                        break;
                                    case "ArtistUnicode":
                                        beatmap.artistUnicode = tokens[1];
                                        break;
                                }
                            }
                            catch (Exception e)
                            {
                                Log.warn(String.format("Failed to read metadata '%s' for file '%s'.", line, osuFilename), e);
                            }
                        }
                        break;
                    case "[TimingPoints]":
                        while ((line = reader.readLine()) != null)
                        {
                            line = line.trim();
                            if (!isValidLine(line))
                                continue;
                            if (line.charAt(0) == '[')
                                break;

                            try
                            {
                                // parse timing point
                                TimingPoint timingPoint = new TimingPoint(line);
                                beatmap.timingPoints.add(timingPoint);

                                // calculate BPM
                                /*if (!timingPoint.isInherited())
                                {
                                    int bpm = Math.round(60000 / timingPoint.getBeatLength());
                                    if (beatmap.bpmMin == 0)
                                    {
                                        beatmap.bpmMin = beatmap.bpmMax = bpm;
                                    }
                                    else if (bpm < beatmap.bpmMin)
                                    {
                                        beatmap.bpmMin = bpm;
                                    }
                                    else if (bpm > beatmap.bpmMax)
                                    {
                                        beatmap.bpmMax = bpm;
                                    }
                                }*/
                            }
                            catch (Exception e)
                            {
                                Log.warn(String.format("Failed to read timing point '%s' for file '%s'.", line, osuFilename), e);
                            }
                        }
                        beatmap.timingPoints.trimToSize();
                        break;
                    case "[HitObjects]":
                        int type = 0;
                        while ((line = reader.readLine()) != null)
                        {
                            line = line.trim();
                            if (!isValidLine(line))
                                continue;
                            if (line.charAt(0) == '[')
                                break;
                            /* Only type counts parsed at this time. */
                            tokens = line.split(",");
                            try
                            {
                                type = Integer.parseInt(tokens[3]);
                                if ((type & HitObject.TYPE_CIRCLE) > 0)
                                    beatmap.hitObjectCircle++;
                                else if ((type & HitObject.TYPE_SLIDER) > 0)
                                    beatmap.hitObjectSlider++;
                                else //if ((type & HitObject.TYPE_SPINNER) > 0)
                                    beatmap.hitObjectSpinner++;
                            }
                            catch (Exception e)
                            {
                                Log.warn(String.format("Failed to read hit object '%s' for file '%s'.", line, osuFilename), e);
                            }
                        }

                        try
                        {
                            // map length = last object end time (TODO: end on slider?)
                            if ((type & HitObject.TYPE_SPINNER) > 0)
                            {
                                // some 'endTime' fields contain a ':' character (?)
                                int index = tokens[5].indexOf(':');
                                if (index != -1)
                                    tokens[5] = tokens[5].substring(0, index);
                                beatmap.endTime = Integer.parseInt(tokens[5]);
                            }
                            else if (type != 0)
                                beatmap.endTime = Integer.parseInt(tokens[2]);
                        }
                        catch (Exception e)
                        {
                            Log.warn(String.format("Failed to read hit object end time '%s' for file '%s'.", line, osuFilename), e);
                        }
                        break;
                    default:
                        line = reader.readLine();
                        break;
                }
            }
        }
        catch (IOException e)
        {
            ErrorHandler.error(String.format("Failed to read file '%s'.", osuFilename), e, false);
        }

        // no associated audio file?
        if (beatmap.audioFilename == null)
        {
            this.setCause("There is no audio track");
            return null;
        }
        if (beatmap.endTime == -1)
        {
            this.setCause("beatmap.endTime is null");
            return null;
        }
        if (beatmap.timingPoints.isEmpty())
        {
            this.setCause("There are no uninherited timing points");
            return null;
        }

        return beatmap;
    }

    /**
     * Returns false if the line is too short or commented.
     */
    private static boolean isValidLine(String line)
    {
        return (line.length() > 1 && !line.startsWith("//"));
    }

    /**
     * Splits line into two strings: tag, value.
     * If no ':' character is present, null will be returned.
     */
    private static String[] tokenize(String line)
    {
        int index = line.indexOf(':');
        if (index == -1)
        {
            Log.debug(String.format("Failed to tokenize line: '%s'.", line));
            return null;
        }

        String[] tokens = new String[2];
        tokens[0] = line.substring(0, index).trim();
        tokens[1] = line.substring(index + 1).trim();
        return tokens;
    }

    public FakeBeatmap(/*String name*/)
    {
        /*try
        {
            FileHandle osz = Gdx.files.internal("tracks/" + name + ".osz");
            ZipInputStream zip = new ZipInputStream(osz.read());
            ZipEntry entry = zip.getNextEntry();
            byte[] buffer = new byte[1024];
            while (entry != null)
            {
                String fileName = entry.getName();
                int len;
                while ((len = zip.read(buffer)) > 0)
                {
                    //
                }
                entry = zip.getNextEntry();
            }
            zip.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Beatmap beatmap = new Beatmap(null);*/
    }

    /**
     * Returns a dummy Beatmap containing the theme song.
     *
     * @return the theme song beatmap, or {@code null} if the theme string is malformed
     */
    public static Beatmap getThemeBeatmap()
    {
        String themeString = "theme.mp3,Rainbows,Kevin MacLeod,219350";
        String themeTimingPoint = "1120,545.454545454545,4,1,0,100,0,0";

        String[] tokens = themeString.split(",");
        if (tokens.length != 4)
            return null;

        Beatmap beatmap = new Beatmap(null);
        beatmap.audioFilename = new File(tokens[0]);
        beatmap.title = tokens[1];
        beatmap.artist = tokens[2];
        try
        {
            beatmap.endTime = Integer.parseInt(tokens[3]);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
        try
        {
            beatmap.timingPoints = new ArrayList<>(1);
            beatmap.timingPoints.add(new TimingPoint(themeTimingPoint));
        }
        catch (Exception e)
        {
            return null;
        }

        return beatmap;
    }
}
