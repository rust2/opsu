package itdelatrisu.opsu.downloads.servers;

import fluddokt.opsu.fake.Log;
import itdelatrisu.opsu.Utils;
import itdelatrisu.opsu.downloads.DownloadNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author NieGestorben
 * Copyright© (c) 2022, All Rights Reserved.
 */
public class NerinyanServer extends DownloadServer {
    /**
     * Server name.
     */

    private static final String SERVER_NAME = "Nerinyan";

    /**
     * Formatted download URL: {@code beatmapSetID}
     */

    private static final String DOWNLOAD_URL = "https://api.nerinyan.moe/d/%d";

    private static final String SEARCH_URL = "https://api.nerinyan.moe/search?q=%s&ps=&d";

    private static final String HOME_URL = "https://api.nerinyan.moe/search/?p=%d";

    /**
     * Maximum beatmaps displayed per page.
     * Supports up to 1000, but response sizes become very large (>100KB).
     */

    private static final int PAGE_LIMIT = 20;

    private int totalResults = -1;

    public NerinyanServer() {}

    @Override
    public String getName()
    {
        return SERVER_NAME;
    }

    @Override
    public String getDownloadURL(int beatmapSetID)
    {
        return String.format(DOWNLOAD_URL, beatmapSetID);
    }

    @Override
    public DownloadNode[] resultList(String query, int page, boolean rankedOnly) throws IOException
    {
        DownloadNode[] nodes = null;
        try
        {
            Utils.setSSLCertValidation(false);
            // read JSON
            int resultIndex = (page - 1) * PAGE_LIMIT;
            String search;
            //String search = String.format(SEARCH_URL, URLEncoder.encode(query, "UTF-8"), PAGE_LIMIT);
            if (query.isEmpty())
            {
                search = String.format(HOME_URL, resultIndex);
            }
            else
            {
                search = String.format(SEARCH_URL, URLEncoder.encode(query, "UTF-8"), resultIndex);
            }
            JSONArray arr = Utils.readJsonArrayFromUrl(new URL(search));
            if (arr == null)
            {
                this.totalResults = -1;
                return null;
            }
            // parse result list
            nodes = new DownloadNode[arr.length()];
            for (int i = 0; i < nodes.length; i++)
            {
                JSONObject item = arr.getJSONObject(i);
                nodes[i] = new DownloadNode(
                        item.getInt("id"), formatDate(item.getString("last_updated")),
                        item.getString("title"), "title_unicode", item.getString("artist"), "artist_unicode",
                        item.getString("creator")
                );
            }

            // store total result count
            int resultCount = nodes.length + resultIndex;
            if (nodes.length == PAGE_LIMIT)
            {
                resultCount++;
            }
            this.totalResults = resultCount;
        }
        catch (JSONException | MalformedURLException | UnsupportedEncodingException e)
        {
            Log.error(e);
        }
        finally
        {
            Utils.setSSLCertValidation(true);
        }
        return nodes;
    }

    @Override
    public int minQueryLength()
    {
        return 50;
    }

    @Override
    public int totalResults()
    {
        return totalResults;
    }

    @Override
    public boolean disableSSLInDownloads()
    {
        return true;
    }


    /**
     * Returns a formatted date string from a raw date.
     *
     * @param s the raw date string (e.g. "2015-09-30T09:39:04Z")
     *
     * @return the formatted date, or the raw string if it could not be parsed
     */
    private String formatDate(String s)
    {
        try
        {
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date d = f.parse(s);
            DateFormat fmt = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
            return fmt.format(d);
        }
        catch (StringIndexOutOfBoundsException | ParseException e)
        {
            return s;
        }
    }
}