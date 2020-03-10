package com.craftmaster.lds.fgc.answer;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import com.craftmaster.lds.fgc.db.TransactionalContext;
import com.craftmaster.lds.fgc.user.FamilyRepository;
import com.craftmaster.lds.fgc.user.UserRepository;

public class ScoreControllerBuilder {

	public static final ScoreControllerBuilder get() {
		return new ScoreControllerBuilder();
	}

	private ScoreControllerBuilder() {
	}

	public ScoreController build() {
		return new ScoreController(answerRepository, scoreRepository, userRepository, familyRepository, simpUserRegistry,
				simpMessageSendingOperations, transactionalContext);
	}

	private AnswerRepository answerRepository;

	public ScoreControllerBuilder withAnswerRepository(AnswerRepository answerRepository) {
		this.answerRepository = answerRepository;
		return this;
	}

	private ScoreRepository scoreRepository;

	public ScoreControllerBuilder withScoreRepository(ScoreRepository scoreRepository) {
		this.scoreRepository = scoreRepository;
		return this;
	}

	private UserRepository userRepository;

	public ScoreControllerBuilder withUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
		return this;
	}

	private FamilyRepository familyRepository;

	public ScoreControllerBuilder withFamilyRepository(FamilyRepository familyRepository) {
		this.familyRepository = familyRepository;
		return this;
	}

	private SimpUserRegistry simpUserRegistry;

	public ScoreControllerBuilder withSimpUserRegistry(SimpUserRegistry simpUserRegistry) {
		this.simpUserRegistry = simpUserRegistry;
		return this;
	}

	private SimpMessageSendingOperations simpMessageSendingOperations;

	public ScoreControllerBuilder withSimpMessageSendingOperations(
			SimpMessageSendingOperations simpMessageSendingOperations) {
		this.simpMessageSendingOperations = simpMessageSendingOperations;
		return this;
	}

	private TransactionalContext transactionalContext;

	public ScoreControllerBuilder withTransactionalContext(TransactionalContext transactionalContext) {
		this.transactionalContext = transactionalContext;
		return this;
	}
}
