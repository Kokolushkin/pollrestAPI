package alexander.rest.controller;

import alexander.rest.services.TimeTableService;
import alexander.rest.model.TimeTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/pool/timetable")
public class TimeTableController {

    private TimeTableService timeTableService;

    @Autowired
    public TimeTableController(TimeTableService timeTableService){
        this.timeTableService = timeTableService;
    }

    @GetMapping("/all/{date}")
    public String getAll(@PathVariable("date") String date){
        return timeTableService.getAll(date).toString();
    }

    @GetMapping("/available/{date}")
    public String getAvailable(@PathVariable("date") String date){
        return timeTableService.getAvailable(date).toString();
    }

    @PostMapping("/reserve")
    public String reserve(@RequestBody TimeTable timeTable){
        return timeTableService.reserve(timeTable).toString();
    }

    @GetMapping("/cancel")
    public void cancel(@RequestBody TimeTable timeTable){
        timeTableService.cancel(timeTable);
    }

    @GetMapping("/by_name/{name}")
    public String getByName(@PathVariable("name") String name){
        return timeTableService.getBookedTimeByName(name).toString();
    }

    @GetMapping("/by_date/{date}")
    public String getByDate(@PathVariable("date") String date){
        return timeTableService.getInfoByDate(date).toString();
    }
}
