package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.service.v2.ScreeningService;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1")
public class AssortmentController {

    @Autowired
    private ScreeningService screeningService;

    @PostMapping("assortment")
    public List<Assortment> assortment(@RequestParam(required = false, defaultValue = "false") boolean store) {
        List<Assortment> assortments = screeningService.provideProducts(store);

        return assortments;
    }

}
