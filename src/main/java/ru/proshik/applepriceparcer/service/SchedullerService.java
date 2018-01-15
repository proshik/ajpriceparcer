package ru.proshik.applepriceparcer.service;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model.Assortment;
import ru.proshik.applepriceparcer.model.Item;
import ru.proshik.applepriceparcer.model.Product;
import ru.proshik.applepriceparcer.model.Shop;
import ru.proshik.applepriceparcer.storage.Database;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.quartz.SimpleScheduleBuilder.repeatMinutelyForever;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class SchedullerService {

    private final Database db;

    public SchedullerService(Database db) {
        this.db = db;
    }




}
