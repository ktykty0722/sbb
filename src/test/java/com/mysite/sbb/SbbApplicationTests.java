package com.mysite.sbb;
import static org.junit.jupiter.api.Assertions.*;

import com.mysite.sbb.model.Answer;
import com.mysite.sbb.model.Question;
import com.mysite.sbb.repository.AnswerRepository;
import com.mysite.sbb.repository.QuestionRepository;
import com.mysite.sbb.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class SbbApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private QuestionService questionService;

	@Test
	void testJpa() {
		//----------데이터 추가-------------
		/*Question q1 = new Question();
		q1.setSubject("sbb가 무엇인가요?");
		q1.setContent("sbb에 대해서 알고 싶습니다.");
		q1.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q1);

		Question q2 = new Question();
		q2.setSubject("스프링부트 모델 질문입니다.");
		q2.setContent("id는 자동으로 생성되나요?");
		q2.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q2);*/


		//-----------데이터 조회-------------
		//findAll
		/*List<Question> questionList = this.questionRepository.findAll();
		assertEquals(4,questionList.size());

		Question q = questionList.get(0);
		assertEquals("sbb가 무엇인가요?",q.getSubject());*/

		//findById
		/*Optional<Question> oq = this.questionRepository.findById(5);
		if(oq.isPresent()) {
			Question q = oq.get();
			assertEquals("sbb가 무엇인가요?",q.getSubject());
		}*/

		//findBySubject
		/*Question q = this.questionRepository.findBySubject("스프링부트 모델 질문입니다.");
		assertEquals(6,q.getId());*/

		//findBySubjectAndContent
		/*Question q = this.questionRepository.findBySubjectAndContent(
				"스프링부트 모델 질문입니다.","id는 자동으로 생성되나요?");
		assertEquals(6,q.getId());*/

		//findBySubjectLike
		/*List<Question> questionList = this.questionRepository.findBySubjectLike("sbb%");
		Question question = questionList.get(0);
		assertEquals(3,question.getId());*/


		//-----------데이터 수정---------------
		/*Optional<Question> optionalQuestion = this.questionRepository.findById(3);
		assertTrue(optionalQuestion.isPresent());
		Question q = optionalQuestion.get();
		q.setSubject("수정된 제목");
		this.questionRepository.save(q);*/


		//----------데이터 삭제--------------
		/*assertEquals(4,this.questionRepository.count());
		Optional<Question> optionalQuestion = this.questionRepository.findById(4);
		assertTrue(optionalQuestion.isPresent());
		Question q = optionalQuestion.get();
		this.questionRepository.delete(q);
		assertEquals(3,this.questionRepository.count());*/


		//------------답변 데이터 생성 후 저장하기----------
		/*Optional<Question> optionalQuestion = this.questionRepository.findById(3);
		assertTrue(optionalQuestion.isPresent());
		Question q = optionalQuestion.get();

		Answer a = new Answer();
		a.setContent("sbb는 Spring Boot Board의 약자압니다.");
		a.setQuestion(q);
		a.setCreateDate(LocalDateTime.now());
		this.answerRepository.save(a);*/


		//------------답변 조회하기---------------
		/*Optional<Answer> optionalAnswer = this.answerRepository.findById(1);
		assertTrue(optionalAnswer.isPresent());
		Answer a = optionalAnswer.get();
		assertEquals(3,a.getQuestion().getId());*/

		//-------대량 테스트 데이터 만들기---------
		for(int i=1;i<=300;i++) {
			String subject = String.format("테스트 데이터 입니다:[%03d]",i);
			String content = "내용 없음";
			this.questionService.create(subject,content,null);
		}

	}

}
