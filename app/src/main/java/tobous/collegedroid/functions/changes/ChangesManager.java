package tobous.collegedroid.functions.changes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tobous.collegedroid.functions.changes.encapsulation.Change;

/**
 * Created by Tob on 16. 1. 2016.
 */
public class ChangesManager {

    public static List<Change> getChanges(String link) throws IOException {

        Document eventInfo = Jsoup.connect(link).get();
        List<Change> changes = new ArrayList();

        Elements info = eventInfo.select("div#p_lt_ctl02_pageplaceholder_p_lt_ctl02_WebPartZone1_WebPartZone1_zone_GRID_pnlList");

        if (info.select("table").size() == 0){
            return changes;
        }

        Element table = info.select("table").get(0); //select the first table.
        Elements rows = table.select("tr");

        //TODO last row -> paiger if more than 1
        for(int i = 1; i<rows.size()-1;i++) {
            Change change = new Change();
            Elements cols = rows.get(i).select("td");

            change.setAuthor(cols.get(0).text());
            change.setStartDate(cols.get(1).text());
            change.setEndDate(cols.get(2).text());
            change.setName(cols.get(3).text());
            change.setReason(cols.get(4).text());
            changes.add(change);


        }
        return changes;

    }

}
