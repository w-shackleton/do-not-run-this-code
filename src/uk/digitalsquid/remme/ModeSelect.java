package uk.digitalsquid.remme;

import uk.digitalsquid.remme.ingame.Game;
import uk.digitalsquid.remme.mgr.Question;
import android.app.Activity;
import android.app.FragmentTransaction;
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
		findViewById(R.id.trueFalse).setOnClickListener(this);
		findViewById(R.id.pairings).setOnClickListener(this);
		findViewById(R.id.timerTest).setOnClickListener(this);
		findViewById(R.id.infiniteGame).setOnClickListener(this);
		findViewById(R.id.otherTest).setOnClickListener(this);
		findViewById(R.id.strangeTest).setOnClickListener(this);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        GroupSelectDialog dialog = new GroupSelectDialog();
        dialog.show(ft, "groupSelectDialog");
	}

	@Override
	public void onClick(View view) {
		GameDescriptor descriptor = new GameDescriptor();
		descriptor.setMaxTime(60);
		switch(view.getId()) {
		case R.id.guessName:
			descriptor.setQuestionTypes(new Question.QuestionAnswerPair[] {
					new Question.QuestionAnswerPair(Question.FIELD_PHOTO, Question.FIELD_DISPLAY_NAME),
			});
			descriptor.setMaxQuestions(2);
			break;
		case R.id.questionMix:
			descriptor.setQuestionTypes(new Question.QuestionAnswerPair[] {
					new Question.QuestionAnswerPair(Question.FIELD_PHOTO, Question.FIELD_DISPLAY_NAME),
					new Question.QuestionAnswerPair(Question.FIELD_PHOTO, Question.FIELD_FIRST_NAME),
					new Question.QuestionAnswerPair(Question.FIELD_PHOTO, Question.FIELD_LAST_NAME),
					new Question.QuestionAnswerPair(Question.FIELD_LAST_NAME, Question.FIELD_FIRST_NAME),
					new Question.QuestionAnswerPair(Question.STYLE_TRUE_FALSE, Question.FIELD_PHOTO, Question.FIELD_DISPLAY_NAME),
			});
			descriptor.setOtherAnswersMinimum(1);
			descriptor.setOtherAnswersMaximum(7);
			descriptor.setMaxQuestions(20);
			break;
		case R.id.trueFalse:
			descriptor.setQuestionTypes(new Question.QuestionAnswerPair[] {
					new Question.QuestionAnswerPair(Question.STYLE_TRUE_FALSE, Question.FIELD_PHOTO, Question.FIELD_DISPLAY_NAME),
			});
			break;
		case R.id.pairings:
			descriptor.setQuestionTypes(new Question.QuestionAnswerPair[] {
					new Question.QuestionAnswerPair(Question.STYLE_PAIRING, Question.FIELD_PHOTO, Question.FIELD_DISPLAY_NAME),
			});
			break;
		case R.id.timerTest:
			descriptor.setQuestionTypes(new Question.QuestionAnswerPair[] {
					new Question.QuestionAnswerPair(Question.FIELD_PHOTO, Question.FIELD_DISPLAY_NAME),
			});
			descriptor.setMaxTimePerContact(5);
			descriptor.setMaxTime(0);
			descriptor.setHardTimerPerContact(false);
			break;
		case R.id.infiniteGame:
			descriptor.setQuestionTypes(new Question.QuestionAnswerPair[] {
					new Question.QuestionAnswerPair(Question.STYLE_TRUE_FALSE, Question.FIELD_PHOTO, Question.FIELD_DISPLAY_NAME),
					new Question.QuestionAnswerPair(Question.STYLE_TRUE_FALSE, Question.FIELD_PHOTO, Question.FIELD_FIRST_NAME),
					new Question.QuestionAnswerPair(Question.STYLE_TRUE_FALSE, Question.FIELD_PHOTO, Question.FIELD_LAST_NAME),
					new Question.QuestionAnswerPair(Question.STYLE_TRUE_FALSE, Question.FIELD_PHOTO, Question.FIELD_EMAIL_HOME),
			});
			descriptor.setFiniteGame(false);
			break;
		case R.id.otherTest:
			descriptor.setQuestionTypes(new Question.QuestionAnswerPair[] {
					new Question.QuestionAnswerPair(Question.STYLE_MULTI_CHOICE, Question.FIELD_WEBSITE, Question.FIELD_DISPLAY_NAME),
			});
			break;
		case R.id.strangeTest:
			descriptor.setQuestionTypes(new Question.QuestionAnswerPair[] {
					//new Question.QuestionAnswerPair(Question.STYLE_MULTI_CHOICE, Question.FIELD_DISPLAY_NAME, Question.FIELD_PHOTO),
					//new Question.QuestionAnswerPair(Question.STYLE_MULTI_CHOICE, Question.FIELD_ADDRESS_HOME, Question.FIELD_PHOTO),
					//new Question.QuestionAnswerPair(Question.STYLE_MULTI_CHOICE, Question.FIELD_ADDRESS_OTHER, Question.FIELD_PHOTO),
					//new Question.QuestionAnswerPair(Question.STYLE_TRUE_FALSE, Question.FIELD_DISPLAY_NAME, Question.FIELD_PHOTO),
					//new Question.QuestionAnswerPair(Question.STYLE_TRUE_FALSE, Question.FIELD_DISPLAY_NAME, Question.FIELD_PHONE_MOBILE),
					new Question.QuestionAnswerPair(Question.STYLE_PAIRING, Question.FIELD_DISPLAY_NAME, Question.FIELD_PHOTO),
					new Question.QuestionAnswerPair(Question.STYLE_PAIRING, Question.FIELD_PHOTO, Question.FIELD_DISPLAY_NAME),
					//new Question.QuestionAnswerPair(Question.STYLE_PAIRING, Question.FIELD_DISPLAY_NAME, Question.FIELD_PHONE_MOBILE),
			});
			break;
		default:
			return;
		}
		
		Intent intent = new Intent(this, Game.class);
		intent.putExtra(Game.GAME_DESRIPTOR, descriptor);
		startActivity(intent);
	}
}
