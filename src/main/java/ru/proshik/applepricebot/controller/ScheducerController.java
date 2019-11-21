package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.FetchType;
import ru.proshik.applepricebot.service.ScreeningService;

import java.util.List;

@RestController
@RequestMapping("api/v1/scheduler")
public class ScheducerController {

    @Autowired
    private ScreeningService screeningService;

    @PostMapping("run")
    public List<Assortment> run(){
        return screeningService.provideProducts(FetchType.BY_REQUEST, false);
    }

}
