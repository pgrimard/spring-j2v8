package com.patrickgrimard.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2016-12-04
 *
 * @author Patrick
 */
@Controller
public class IndexController {

    @GetMapping
    public String index(Model model, HttpServletRequest request) throws JsonProcessingException {
        Map<String, Object> req = new HashMap<>();
        String root = request.getServletPath().equals("/index.html") ? "/" : request.getServletPath();
        if(request.getQueryString() != null)
            req.put("location", String.format("%s?%s", root, request.getQueryString()));
        else
            req.put("location", root);
        model.addAttribute("req", req);

        Map<String, Object> initialState = new HashMap<>();
        Map<String, Object> deep = new HashMap<>();
        deep.put("stringVal", "I am a String");
        deep.put("arrayVal", new String[]{"I", "am", "an", "array"});
        deep.put("iterableVal", Arrays.asList("I", "am", "an", "iterable"));
        initialState.put("deep", deep);
        model.addAttribute("initialState", initialState);

        return "index";
    }
}
