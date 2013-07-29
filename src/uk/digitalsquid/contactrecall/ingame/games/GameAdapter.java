package uk.digitalsquid.contactrecall.ingame.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.GameDescriptor.QuestionAnswerPair;
import uk.digitalsquid.contactrecall.GameDescriptor.ShufflingMode;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.ingame.fragments.MultiChoiceView;
import uk.digitalsquid.contactrecall.ingame.fragments.PhotoNameView;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import uk.digitalsquid.contactrecall.misc.Config;
import uk.digitalsquid.contactrecall.misc.Const;
import uk.digitalsquid.contactrecall.misc.ListUtils;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

@SuppressLint("UseSparseArrays")
public class GameAdapter implements Parcelable, Config {
	
	protected transient App app;
	protected transient Context context;
	protected transient GameCallbacks callbacks;
	
	protected GameDescriptor descriptor;
	
	/**
	 * The questions to use - usually pre-shuffled.
	 */
	ArrayList<Question> questions;

	@SuppressWarnings("unchecked")
	public GameAdapter(Context context, App app, GameDescriptor descriptor, GameCallbacks callbacks) {
		this.app = app;
		this.context = context;
		this.callbacks = callbacks;
		this.descriptor = descriptor;
		
		questions = new ArrayList<Question>(descriptor.getMaxQuestions());

		// Construct the lists that are used as false answers to questions.
		ArrayList<Contact> badContacts = null; // TODO: Implement
		ArrayList<Contact> realContacts = new ArrayList<Contact>(app.getContacts().getContacts());
		ArrayList<Contact> nameLists = null; // TODO: Implement

		ArrayList<Contact> allContacts = 
				ListUtils.concat(new ArrayList<Contact>(), badContacts, realContacts, nameLists);

		// The types of contacts we must generate lists of
		Set<Integer> usedFieldTypes = descriptor.getUsedFieldTypes();

		// Construct groups of actual contacts.
		HashMap<Integer, ArrayList<Contact>> contactGroups = new HashMap<Integer, ArrayList<Contact>>();
		// For each field type, accumulate a list of contacts that contain that field type.
		for(int usedFieldType : usedFieldTypes) {
			ArrayList<Contact> contacts = new ArrayList<Contact>();
			contactGroups.put(usedFieldType, contacts);
			
			for(Contact contact : realContacts) {
				if(contact.hasField(usedFieldType))
					contacts.add(contact);
			}
		}

		// Construct groups of all types of contact to use as answers.
		HashMap<Integer, ArrayList<Contact>> allContactGroups = new HashMap<Integer, ArrayList<Contact>>();
		// For each field type, accumulate a list of contacts that contain that field type.
		for(int usedFieldType : usedFieldTypes) {
			ArrayList<Contact> contacts = new ArrayList<Contact>();
			
			for(Contact contact : allContacts) {
				if(contact.hasField(usedFieldType))
					contacts.add(contact);
			}
		
			// Shuffle each group
			allContactGroups.put(usedFieldType,
					shuffleContacts(contacts, descriptor.getShufflingMode()));
		}
		
		// Every time we hit an empty group this decrements
		int emptyRetries = 10;
		for(int i = 0; i < descriptor.getMaxQuestions(); i++) {
			// Select a random question type
			QuestionAnswerPair type =
					descriptor.getQuestionTypes()[Const.RAND.nextInt(
							descriptor.getQuestionTypes().length)];
			
			// Remove the first question from the grouped contact questions
			ArrayList<Contact> possibles = contactGroups.get(type.getQuestionType());
			if(possibles == null) {
				Log.w(TAG, "Null array found in contactGroups - this shouldn't be possible");
			}
			
			// Find the first contact that has both the question and answer types
			Contact question = null;
			for(int j = 0; j < possibles.size(); j++) {
				Contact test = possibles.get(j);
				if(test.hasField(type.getAnswerType())) {
					question = test;
					possibles.remove(j);
					break;
				}
			}
			if(question == null) {
				if(emptyRetries-- == 0) break;
				else continue;
			}
			
			questions.add(createQuestion(question, type, allContactGroups));
		}
	}

	public int getCount() {
		if(descriptor.isFiniteGame()) {
			if(questions == null) return 0;
			return questions.size();
		}
		return 1000; // TODO: Infinite game?!?
	}

	public Question getItem(int pos) {
		if(questions == null) return null;
		if(descriptor.isFiniteGame())
			return questions.get(pos);
		throw new RuntimeException("Infinite mode not yet implemented");
	}

	/**
	 * Compares two contacts. Default instance just compares by contact ID
	 * @return
	 */
	protected Comparator<Contact> getContactComparator() {
		return new Comparator<Contact>() {
			@Override
			public int compare(Contact lhs, Contact rhs) {
				if(lhs == null) return -1;
				if(rhs == null) return 1;
				return lhs.getId() - rhs.getId();
			}
		};
	}
	
	private ArrayList<Contact> shuffleContacts(ArrayList<Contact> selection, ShufflingMode mode) {
		switch(mode) {
		case RANDOM:
			Collections.shuffle(selection);
			return selection;
		default:
			return selection;
		}
	}

	public boolean isEmpty() {
		return questions == null;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	protected Question createQuestion(Contact contact, QuestionAnswerPair type, HashMap<Integer, ArrayList<Contact>> allContactGroups) {
		Question question = new Question(contact);
		question.setQuestionType(type.getQuestionType());
		question.setAnswerType(type.getAnswerType());
		int numOtherChoices = Const.RAND.nextInt(
						descriptor.getOtherAnswersMaximum() -
						descriptor.getOtherAnswersMinimum() + 1) +
						descriptor.getOtherAnswersMinimum();
		question.setCorrectPosition(Const.RAND.nextInt(numOtherChoices + 1));
		
		ArrayList<Contact> otherAnswers = allContactGroups.get(type.getAnswerType());
		
		Contact[] otherChoices = new Contact[numOtherChoices];
        String correctIdentifier = contact.getStringFieldRepresentation(type.getAnswerType());
		for(int i = 0; i < otherChoices.length; i++) {
    		Contact other = Contact.getNullContact();
    		for(int j = 0; j < 20; j++) { // Attempt to find a different contact (as in different identifier)
    			other = otherAnswers.get(Const.RAND.nextInt(otherAnswers.size()));
    			if(!other.getStringFieldRepresentation(type.getAnswerType())
    					.equalsIgnoreCase(correctIdentifier)) break;
    		}
    		otherChoices[i] = other;
		}
		
		question.setOtherAnswers(otherChoices);
		
		return question;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(descriptor, 0);
		
		dest.writeList(questions);
	}
	
	GameAdapter(Parcel in) {
		descriptor = in.readParcelable(null);
		
		questions = new ArrayList<Question>();
		in.readList(questions, null);
	}
	
	/**
	 * Initialises the adapter's transient fields. Must be called after
	 * de-parcelisation
	 * @param app
	 * @param context
	 */
	public void init(App app, Context context, GameCallbacks callbacks) {
		if(app == null || context == null || callbacks == null)
			throw new IllegalArgumentException("Arguments must be non-null");
		this.app = app;
		this.context = context;
		this.callbacks = callbacks;
	}
	
	protected Fragment createFragment(int position) {
		Question question = getItem(position);
        Bundle args = new Bundle();
        
        args.putParcelable(MultiChoiceView.ARG_QUESTION, question);
        args.putParcelable(MultiChoiceView.ARG_DESCRIPTOR, descriptor);
        
        // Find the correct fragment to use
        // TODO: Write implementations for these other cases.
        MultiChoiceView<?, ?> fragment;
        switch(question.getQuestionFormat()) {
        case Question.FORMAT_TEXT:
    	default:
    		switch(question.getAnswerFormat()) {
    		case Question.FORMAT_TEXT:
			default:
				fragment = null;
				break;
			case Question.FORMAT_IMAGE:
				fragment = null;
				break;
    		}
    	case Question.FORMAT_IMAGE:
    		switch(question.getAnswerFormat()) {
    		case Question.FORMAT_TEXT:
			default:
				fragment = new PhotoNameView();
				break;
			case Question.FORMAT_IMAGE:
				fragment = null;
				break;
    		}
        }
        fragment.setArguments(args);
        
		return fragment;
	}
	
	/**
	 * Gets the {@link Fragment} at position.
	 * @param position
	 * @return
	 */
	public Fragment getFragment(int position) {
		return createFragment(position);
	}
	
	public static final Creator<GameAdapter> CREATOR = new Creator<GameAdapter>() {

		@Override
		public GameAdapter createFromParcel(Parcel source) {
			return new GameAdapter(source);
		}

		@Override
		public GameAdapter[] newArray(int size) {
			return new GameAdapter[size];
		}
	};
}
