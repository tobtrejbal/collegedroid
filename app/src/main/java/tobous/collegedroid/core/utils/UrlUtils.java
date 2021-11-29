package tobous.collegedroid.core.utils;

import android.util.Log;

/**
 * Created by Tobous on 7. 11. 2015.
 */
public class UrlUtils {
    private final static String STAG = "stagws.uhk.cz/ws/services/rest/";

    private final static String HTTP = "http://";
    private final static String HTTPS = "https://";

    private final static String STAG_SCHEDULE = "rozvrhy/";
    private final static String STAG_SCHEDULE_ACTION = "getRozvrhoveAkce";
    private final static String STAG_SCHEDULE_STUDENT = "getRozvrhByStudent";

    private final static String NEXT_VARIABLE = "&";

    public static String getURLScheduleByStudent(String id) {

        return HTTPS + STAG + STAG_SCHEDULE + STAG_SCHEDULE_STUDENT + "?osCislo=" + id;

    }

    public static String getURLScheduleActions(String year, String semester, String day, String building) {
        if(semester.equals("LS")) {
            int temp = Integer.parseInt(year);
            temp -= 1;
            year = String.valueOf(temp);
        }
        return HTTPS + STAG + STAG_SCHEDULE + STAG_SCHEDULE_ACTION + "?rokVarianty=" + year + NEXT_VARIABLE
                + "semestr=" + semester + NEXT_VARIABLE + "zkrBudovy=" + building + NEXT_VARIABLE
                + "denZkr=" + day;

    }

}
