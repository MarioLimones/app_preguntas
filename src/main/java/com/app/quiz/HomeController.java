package com.app.quiz;

import com.app.quiz.features.mc.service.MultipleChoiceQuestionService;
import com.app.quiz.features.sc.service.SingleChoiceQuestionService;
import com.app.quiz.features.vf.service.TrueFalseQuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final TrueFalseQuestionService vfService;
    private final SingleChoiceQuestionService scService;
    private final MultipleChoiceQuestionService mcService;

    public HomeController(TrueFalseQuestionService vfService,
            SingleChoiceQuestionService scService,
            MultipleChoiceQuestionService mcService) {
        this.vfService = vfService;
        this.scService = scService;
        this.mcService = mcService;
    }

    @GetMapping({ "/", "/home" })
    public String home(Model model) {
        model.addAttribute("vfCount", vfService.count());
        model.addAttribute("scCount", scService.count());
        model.addAttribute("mcCount", mcService.count());
        return "home";
    }
}



