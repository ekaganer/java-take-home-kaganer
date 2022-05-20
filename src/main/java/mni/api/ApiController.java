package mni.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by will.schick on 8/3/17.
 */
@RestController
public class ApiController {

    @RequestMapping("/nearestPoint")
    public WordPoint nearestPoint(@RequestParam("x") int x, @RequestParam("y") int y) {
        ArrayList<WordPoint> list = DBUtils.getDBdataAsArrayList();
        Utils.setDistanceValue(x, y, list);
        Collections.sort(list, compareByDistance);

        return list.get(0);
    }

    public Comparator<WordPoint> compareByDistance = new Comparator<WordPoint>() {
        @Override
        public int compare(WordPoint wp1, WordPoint wp2) {
            Double wpD1 = wp1.getDistance();
            Double wpD2 = wp2.getDistance();
            return wpD1.compareTo(wpD2);
        }
    };
}
