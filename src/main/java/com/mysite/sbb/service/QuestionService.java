package com.mysite.sbb.service;

import com.mysite.sbb.model.Answer;
import com.mysite.sbb.model.Question;
import com.mysite.sbb.model.SiteUser;
import com.mysite.sbb.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {


    private final QuestionRepository questionRepository;

    private Specification<Question> search(String keyword) {
        return new Specification<Question>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + keyword + "%"), // 제목
                        cb.like(q.get("content"), "%" + keyword + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + keyword + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + keyword + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + keyword + "%"));   // 답변 작성자
            }
        };
    }

    public Page<Question> getList(int page, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,10, Sort.by(sorts));
        Specification<Question> spec = search(keyword);
        Page<Question> questionList = this.questionRepository.findAll(spec, pageable);
        return questionList;
    }

    public Optional<Question> getQuestion(Integer id) {
        return this.questionRepository.findById(id);
    }

    public Question create(String subject, String content, SiteUser user) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setAuthor(user);
        q.setCreateDate(LocalDateTime.now());
        q = this.questionRepository.save(q);
        return q;
    }

    public Question modify(Question q, String subject, String content) {
        q.setSubject(subject);
        q.setContent(content);
        q.setModifyDate(LocalDateTime.now());
        q = this.questionRepository.save(q);
        return q;
    }

    public void delete(Question q) {
        this.questionRepository.delete(q);
    }

    public Question vote(Question q, SiteUser user) {
        q.voter.add(user);
        return this.questionRepository.save(q);
    }
}
