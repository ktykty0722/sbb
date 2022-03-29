package com.mysite.sbb.controller;

import com.mysite.sbb.form.AnswerForm;
import com.mysite.sbb.form.QuestionForm;
import com.mysite.sbb.model.Question;
import com.mysite.sbb.model.SiteUser;
import com.mysite.sbb.service.QuestionService;
import com.mysite.sbb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "keyword", defaultValue = "") String keyword) {

        Page<Question> paging = this.questionService.getList(page, keyword);
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        return "question_list";
    }

    @RequestMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Optional<Question> question = this.questionService.getQuestion(id);
        if(question.isPresent()) {
            model.addAttribute("question", question.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal) {
        if(bindingResult.hasErrors())
            return "question_form";
        Optional<SiteUser> user = this.userService.getUser(principal.getName());
        if(user.isPresent()) {
            this.questionService.create(questionForm.getSubject(), questionForm.getContent(), user.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Optional<Question> question = this.questionService.getQuestion(id);
        if(question.isPresent()) {
            Question q = question.get();
            if(!q.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
            }
            questionForm.setSubject(q.getSubject());
            questionForm.setContent(q.getContent());
        }
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if(bindingResult.hasErrors()) {
            return "question_form";
        }
        Optional<Question> question = this.questionService.getQuestion(id);
        if(question.isPresent()) {
            Question q = question.get();
            if(!q.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
            }
            this.questionService.modify(q,questionForm.getSubject(), questionForm.getContent());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Optional<Question> question = this.questionService.getQuestion(id);
        if (question.isPresent()) {
            Question q = question.get();
            if (!q.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
            }
            this.questionService.delete(q);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Optional<Question> question = this.questionService.getQuestion(id);
        Optional<SiteUser> user = this.userService.getUser(principal.getName());
        if (question.isPresent() && user.isPresent()) {
            Question q = question.get();
            SiteUser u = user.get();
            this.questionService.vote(q,u);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return String.format("redirect:/question/detail/%s", id);
    }
}
