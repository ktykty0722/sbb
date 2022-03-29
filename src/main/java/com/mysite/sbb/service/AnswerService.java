package com.mysite.sbb.service;

import com.mysite.sbb.model.Answer;
import com.mysite.sbb.model.Question;
import com.mysite.sbb.model.SiteUser;
import com.mysite.sbb.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content, SiteUser user) {
        Answer a = new Answer();
        a.setContent(content);
        a.setCreateDate(LocalDateTime.now());
        a.setQuestion(question);
        a.setAuthor(user);
        a = this.answerRepository.save(a);
        return a;
    }

    public Optional<Answer> getAnswer(Integer id) {
        return this.answerRepository.findById(id);
    }

    public Answer modify(Answer a, String content) {
        a.setContent(content);
        a.setModifyDate(LocalDateTime.now());
        a = this.answerRepository.save(a);
        return a;
    }

    public void delete(Answer a) {
        this.answerRepository.delete(a);
    }

    public Answer vote(Answer a, SiteUser user) {
        a.voter.add(user);
        return this.answerRepository.save(a);
    }
}