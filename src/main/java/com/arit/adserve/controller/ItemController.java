package com.arit.adserve.controller;

import com.arit.adserve.entity.Item;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alex Ryjoukhine
 * @since Sep 08, 2020
 */
@Slf4j
@RestController
public class ItemController {

    @Operation(summary = "Retrieve a list of items.")
    @RequestMapping(value = "/items", method = RequestMethod.GET, produces = {"application/json"})
    public List<Item> listItems() {
        log.debug("Received request to list all items");
        var item1 = new Item();
        var item2 = new Item();
        item1.setProviderName("id1");
        item2.setId("id2");
        return Arrays.asList(item1, item2);
    }
}
