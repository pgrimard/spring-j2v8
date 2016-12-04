package com.patrickgrimard.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> req = new HashMap<>();
        String root = request.getServletPath().equals("/index.html") ? "/" : request.getServletPath();
        if(request.getQueryString() != null)
            req.put("location", String.format("%s?%s", root, request.getQueryString()));
        else
            req.put("location", root);
        model.addAttribute("req", mapper.writeValueAsString(req));

        Map<String, Object> initialState = new HashMap<>();
        initialState.put("hello", "world");

        Map<String, Object> deep = new HashMap<>();
        deep.put("deepProp", "deepValue");
        deep.put("anotherProp", "anotherValue");
        deep.put("anArray", new String[]{"hello", "world", "again"});
        deep.put("anIterable", Arrays.asList("hello", "world", "yet again"));
        initialState.put("deep", deep);

        model.addAttribute("initialState", mapper.writeValueAsString(initialState));


        return "index";
    }
}
