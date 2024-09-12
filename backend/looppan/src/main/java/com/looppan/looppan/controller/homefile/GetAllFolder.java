package com.looppan.looppan.controller.homefile;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GetAllFolder {

    @RequestMapping(value = "/file/loadAllFolder")
    public Map<String, String> loadAllFolder() {

        Map<String, String> mp = new HashMap<>();
        mp.put("message", "success");
        return mp;
    }
}
