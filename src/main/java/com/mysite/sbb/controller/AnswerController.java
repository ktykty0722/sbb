package com.mysite.sbb.controller;

import com.mysite.sbb.form.AnswerForm;
import com.mysite.sbb.model.Answer;
import com.mysite.sbb.model.Question;
import com.mysite.sbb.model.SiteUser;
import com.mysite.sbb.service.AnswerService;
import com.mysite.sbb.service.QuestionService;
import com.mysite.sbb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/answer")
public class AnswerController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id,
                               @Valid AnswerForm answerForm, BindingResult bindingResult,
                               Principal principal) {
        Optional<Question> question = this.questionService.getQuestion(id);
        Optional<SiteUser> user = this.userService.getUser(principal.getName());

        if (question.isPresent() && user.isPresent()) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("question", question.get());
                return "question_detail";
            }
            Answer a = this.answerService.create(question.get(), answerForm.getContent(), user.get());
            return String.format("redirect:/question/detail/%s#answer_%s", a.getQuestion().getId(), a.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        Optional<Answer> answer = this.answerService.getAnswer(id);
        if(answer.isPresent()) {
            Answer a = answer.get();
            if(!a.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
            }
            answerForm.setContent(a.getContent());
        }
        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, @PathVariable("id") Integer id,
                               BindingResult bindingResult, Principal principal) {
        if(bindingResult.hasErrors()) {
            return "answer_form";
        }
        Optional<Answer> answer = this.answerService.getAnswer(id);
        if(answer.isPresent()) {
            Answer a = answer.get();
            if(!a.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정 권한이 없습니다.");
            }
            this.answerService.modify(a,answerForm.getContent());
            return String.format("redirect:/question/detail/%s#answer_%s", a.getQuestion().getId(), a.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
        Optional<Answer> answer = this.answerService.getAnswer(id);
        if(answer.isPresent()) {
            Answer a = answer.get();
            if(!a.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
            }
            this.answerService.delete(a);
            return String.format("redirect:/question/detail/%s", a.getQuestion().getId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id) {
        Optional<Answer> answer = this.answerService.getAnswer(id);
        Optional<SiteUser> user = this.userService.getUser(principal.getName());
        if (answer.isPresent() && user.isPresent()) {
            Answer a = answer.get();
            SiteUser u = user.get();
            this.answerService.vote(a, u);
            return String.format("redirect:/question/detail/%s#answer_%s", a.getQuestion().getId(), a.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }
}
