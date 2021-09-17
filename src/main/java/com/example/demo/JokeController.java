package com.example.demo;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
public class JokeController {

    @Autowired
    JokeService jokeService;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<Joke> jokes = jokeService.getAllJokes();
        model.addAttribute("jokeList", jokes);
        return "index";
    }

    @PostMapping("/create-joke")
    public String addJoke() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Application");
        ResponseEntity<String> httpResponseEntity =
                restTemplate.exchange(
                        "https://api.chucknorris.io/jokes/random",
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        String.class);

        JSONObject jsonObject = new JSONObject(httpResponseEntity.getBody());

        jokeService.submitJoke(jsonObject.get("value").toString());
        return "redirect:/";
    }
}
