package uk.digitalsquid.contactrecall;

import uk.digitalsquid.contactrecall.ingame.Game;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.mgr.Question.QuestionAnswerPair;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ModeSelect extends Activity implements OnClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modeselect);
		
		findViewById(R.id.guessName).setOnClickListener(this);
		findViewById(R.id.questionMix).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		GameDescriptor descriptor = new GameDescriptor();
		switch(view.getId()) {
		case R.id.guessName:
			descriptor.setQuestionTypes(new Question.QuestionAnswerPair[] {
					new Question.QuestionAnswerPair(Question.FIELD_PHOTO, Question.FIELD_DISPLAY_NAME),
			});
			break;
		case R.id.questionMix:
			descriptor.setQuestionTypes(new Question.QuestionAnswerPair[] {
					new Question.QuestionAnswerPair(Question.FIELD_PHOTO, Question.FIELD_DISPLAY_NAME),
					new Question.QuestionAnswerPair(Question.FIELD_PHOTO, Question.FIELD_FIRST_NAME),
					new Question.QuestionAnswerPair(Question.FIELD_PHOTO, Question.FIELD_LAST_NAME),
					new Question.QuestionAnswerPair(Question.FIELD_LAST_NAME, Question.FIELD_FIRST_NAME),
			});
			descriptor.setOtherAnswersMinimum(1);
			descriptor.setOtherAnswersMaximum(7);
			descriptor.setMaxQuestions(20);
			break;
		default:
			return;
		}
		
		Intent intent = new Intent(this, Game.class);
		intent.putExtra(Game.GAME_DESRIPTOR, descriptor);
		startActivity(intent);
	}
}
