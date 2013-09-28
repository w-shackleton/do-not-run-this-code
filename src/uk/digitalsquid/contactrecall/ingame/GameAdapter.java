package uk.digitalsquid.contactrecall.ingame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.GameDescriptor.ShufflingMode;
import uk.digitalsquid.contactrecall.ingame.questions.ImageTextMCFragment;
import uk.digitalsquid.contactrecall.ingame.questions.ImageTextPFragment;
import uk.digitalsquid.contactrecall.ingame.questions.ImageTextTFFragment;
import uk.digitalsquid.contactrecall.ingame.questions.MultiChoiceFragment;
import uk.digitalsquid.contactrecall.ingame.questions.TextImageMCFragment;
import uk.digitalsquid.contactrecall.ingame.questions.TextTextMCFragment;
import uk.digitalsquid.contactrecall.ingame.questions.TextTextPFragment;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.mgr.Question.QuestionAnswerPair;
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
	
	private HashMap<Integer, ArrayList<Contact>> contactGroups, allContactGroups;

	/**
	 * Constructor.
	 * This takes a LONG time (seconds), as there is a lot of data to crunch.
	 * This needs to be backgrounded at a loading screen at some point.
	 * @param context
	 * @param app
	 * @param descriptor
	 * @param callbacks
	 */
	public GameAdapter(Context context, App app, GameDescriptor descriptor, GameCallbacks callbacks) {
		this.app = app;
		this.context = context;
		this.callbacks = callbacks;
		this.descriptor = descriptor;

		// Construct the lists that are used as false answers to questions.
		// FIXME: Decide what to do here.
		ArrayList<Contact> badContacts = null; // TODO: Implement
		ArrayList<Contact> realContacts = new ArrayList<Contact>(app.getContacts().getContacts());
		ArrayList<Contact> nameLists = null; // TODO: Implement

		// Combine these lists
		@SuppressWarnings("unchecked")
		ArrayList<Contact> allContacts = 
				ListUtils.concat(new ArrayList<Contact>(), badContacts, realContacts, nameLists);

		// The types of field used throughout all Question types
		Set<Integer> usedFieldTypes = descriptor.getUsedFieldTypes();

		// Construct groups of actual contacts. Contacts are grouped by the fields they have
		// available
		contactGroups = new HashMap<Integer, ArrayList<Contact>>();
		// For each field type, accumulate a list of contacts that contain that field type.
		for(int usedFieldType : usedFieldTypes) {
			ArrayList<Contact> contacts = new ArrayList<Contact>();
			contactGroups.put(usedFieldType, contacts);
			
			for(Contact contact : realContacts) {
				if(contact.hasField(usedFieldType))
					contacts.add(contact);
			}
		
			// Shuffle each group. Shuffling is currently only random.
			// Shuffling is necessary as contacts are selected sequentially later.
			contactGroups.put(usedFieldType,
					shuffleContacts(contacts, descriptor.getShufflingMode()));
		}
		
		// Repeat process for allContacts

		// Construct groups of all types of contact to use as answers.
		// This is the same as contactGroups, but doesn't get removed from later.
		allContactGroups = new HashMap<Integer, ArrayList<Contact>>();
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
		
		if(descriptor.isFiniteGame()) generateMoreQuestions(descriptor.getMaxQuestions());
		else generateMoreQuestions(10); // Generate 10 at a time
	}

	private void generateMoreQuestions(int number) {
		if(questions == null)
			questions = new ArrayList<Question>(number);

		Log.i(TAG, "Generating " + number + " more questions");
		
		// Every time we hit an empty group this decrements
		int emptyRetries = 10;
		for(int i = 0; i < number; i++) {
			// Select a random question type
			Question.QuestionAnswerPair type =
					descriptor.getQuestionTypes()[Const.RAND.nextInt(
							descriptor.getQuestionTypes().length)];
			
			// Switch based on question style
			switch(type.getQuestionStyle()) {
			case Question.STYLE_MULTI_CHOICE: // These two are quite similar
			case Question.STYLE_TRUE_FALSE:
			{
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
				break;
			case Question.STYLE_PAIRING:
			{
				// Get the contacts that match the question criteria
				// Note that we are not getting these from the depleting lists -
				// contacts aren't deleted when constructing these questions.
				ArrayList<Contact> orderedPossibles = allContactGroups.get(type.getQuestionType());
				if(orderedPossibles == null) {
					Log.w(TAG, "Null array found in contactGroups - this shouldn't be possible");
				}
				
				// Shuffle contacts so that same ones don't get chosen repeatedly.
				@SuppressWarnings("unchecked")
				ArrayList<Contact> possibles = (ArrayList<Contact>) orderedPossibles.clone();
				Collections.shuffle(possibles);
				
				ArrayList<Contact> choices = new ArrayList<Contact>();
				
				int pairingChoices = Const.RAND.nextInt(
						descriptor.getPairingChoicesMaximum() -
						descriptor.getPairingChoicesMinimum() + 1) +
						descriptor.getPairingChoicesMinimum();
				
				for(Contact possible : possibles) {
					if(possible.hasField(type.getAnswerType())) {
						// Possible contact found, check has different answer
						// field to all other previous contacts.
						String possibleRepresentation =
								possible.getStringFieldRepresentation(type.getAnswerType());
						boolean unique = true;
						for(Contact test : choices) {
							if(possibleRepresentation.equals(
									test.getStringFieldRepresentation(type.getAnswerType())))
								unique = false;
						}
						if(unique) choices.add(possible);
						if(choices.size() >= pairingChoices) break;
					}
				}
				// If there are several matching contacts, add to question
				if(choices.size() > 1)
					questions.add(createPairingQuestion(
							choices.toArray(new Contact[choices.size()]),
							type));
			}
				break;
			}
		}
	}

	public int getCount() {
		if(descriptor.isFiniteGame()) {
			if(questions == null) return 0;
			return questions.size();
		} else return Integer.MAX_VALUE;
	}

	public Question getItem(int pos) {
		if(questions == null) return null;
		if(descriptor.isFiniteGame()) {
			if(pos < questions.size())
				return questions.get(pos);
			else return null;
		} else {
			if(pos < questions.size())
				return questions.get(pos);
			else {
				// Generate more questions, then continue
				generateMoreQuestions(10);
				return questions.get(pos);
			}
		}
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
	
	/**
	 * Creates a multi choice OR true/false question
	 * @param contact The {@link Contact} to use as a question
	 * @param type The type of question to ask. This includes the question and answer fields,
	 * and the question style
	 * @param allContactGroups Groups of contacts to use as other answers (multi-choice only)
	 * @return
	 */
	protected Question createQuestion(Contact contact, Question.QuestionAnswerPair type, HashMap<Integer, ArrayList<Contact>> allContactGroups) {
		Question question = new Question(contact);
		question.setQuestionAnswerPair(type);

		int numOtherChoices = 0;
		switch(type.getQuestionStyle()) {
		case Question.STYLE_MULTI_CHOICE:
			numOtherChoices = Const.RAND.nextInt(
							descriptor.getOtherAnswersMaximum() -
							descriptor.getOtherAnswersMinimum() + 1) +
							descriptor.getOtherAnswersMinimum();
			break;
		case Question.STYLE_TRUE_FALSE:
			numOtherChoices = 1; // Just one choice. See Question.setCorrectPosition for what this means.
			break;
		case Question.STYLE_PAIRING:
			throw new IllegalArgumentException("For pairing questions, use createPairingQuestion");
		}
		question.setCorrectPosition(Const.RAND.nextInt(numOtherChoices + 1));
		
		ArrayList<Contact> otherAnswers = allContactGroups.get(type.getAnswerType());
		
		Contact[] otherChoices = new Contact[numOtherChoices];
        String correctIdentifier = contact.getStringFieldRepresentation(type.getAnswerType());
		for(int i = 0; i < otherChoices.length; i++) {
    		Contact other = Contact.getNullContact();
    		for(int j = 0; j < 20; j++) { // Attempt to find a different contact (as in different identifier)
    			// Get a random contact to test against the other data
    			Contact attempt = otherAnswers.get(Const.RAND.nextInt(otherAnswers.size()));
    			// Check this answer against the actual answer
    			String otherIdentifier = attempt.getStringFieldRepresentation(type.getAnswerType());
    			if(!otherIdentifier.equalsIgnoreCase(correctIdentifier)) {
    				// Then check this answer against all other answers
    				boolean different = true;
    				for(int k = 0; k < i; k++) {
    					if(otherIdentifier.equalsIgnoreCase(
    							otherChoices[k].getStringFieldRepresentation(
    									type.getAnswerType()))) {
    						different = false;
    						break;
    					}
    				}
    				if(different)
    					other = attempt;
    			}
    		}
    		otherChoices[i] = other;
		}
		
		question.setOtherAnswers(otherChoices);
		
		// Calculate a score for answering this question
		// TODO: Calculate this using current difficulty and other stuff
		// Perhaps more points in time trial mode?
		int initialPoints = 1000;
		int maxPointsGain = (int) (app.getStats().computeScoreWeight(contact) * initialPoints);
		question.setMaxPoints(maxPointsGain);
		
		return question;
	}

	protected Question createPairingQuestion(Contact[] contacts, QuestionAnswerPair type) {
		Question question = new Question(contacts);
		question.setQuestionAnswerPair(type);
		
		// Construct a sequential list (1, 2, 3, 4 etc), then shuffle it.
		List<Integer> correctPairings = new ArrayList<Integer>(contacts.length);
		for(int i = 0; i < contacts.length; i++) {
			correctPairings.add(i);
		}
		
		Collections.shuffle(correctPairings);
		question.setCorrectPairings(correctPairings);

		// Calculate a score for answering this question
		// TODO: Calculate this using current difficulty and other stuff
		// Perhaps more points in time trial mode?
		int initialPoints = 1000;
		float combinedScoreWeight = 0;
		for(Contact contact : contacts)
			combinedScoreWeight += app.getStats().computeScoreWeight(contact);
		// We'll assume that a pairing is worth 3x a normal question
		combinedScoreWeight *= 3f / 4f;
		int maxPointsGain = (int) (combinedScoreWeight * initialPoints);
		question.setMaxPoints(maxPointsGain);

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
		if(question == null) return null;
        Bundle args = new Bundle();
        
        args.putParcelable(MultiChoiceFragment.ARG_QUESTION, question);
        args.putParcelable(MultiChoiceFragment.ARG_DESCRIPTOR, descriptor);
        
        // Find the correct fragment to use
        // TODO: Write implementations for these other cases. (Don't think image->image will be needed)
        Fragment fragment;
        Log.v(TAG, String.format("Creating question fragment of format (%d,%d,%d)",
        		question.getQuestionStyle(),
        		question.getQuestionFormat(),
        		question.getAnswerFormat()));
        // Using a bitmask to make code more clear (well, hopefully).
        // 0bABCD - AB is style, C is Question format, D is answer format
        // AB - 00 -> mc
        //      01 -> tf
        //      10 -> p
        // C, D - 1 -> text
        //        0 -> image
        int styleMask = 0;
        switch(question.getQuestionStyle()) {
        case Question.STYLE_MULTI_CHOICE: styleMask = 0x0; break;
        case Question.STYLE_TRUE_FALSE: styleMask = 0x4; break;
        case Question.STYLE_PAIRING: styleMask = 0x8; break;
        }
        final int bits = styleMask | 
        		(question.getQuestionFormat() == Question.FORMAT_TEXT ? 2 : 0) |
        		(question.getAnswerFormat() == Question.FORMAT_TEXT ? 1 : 0);
        switch(bits) {
        case 0x0: // 0b0000 - I I MC
        	fragment = null; break; // NOT IMPLEMENTING
    	default: case 0x1: // 0b0001 - I T MC
        	fragment = new ImageTextMCFragment(); break;
        case 0x2: // 0b0010 - T I MC
        	fragment = new TextImageMCFragment(); break;
        case 0x3: // 0b0011 - T T MC
        	fragment = new TextTextMCFragment(); break;
        case 0x4: // 0b0100 - I I TF
        	fragment = null; break; // NOT IMPLEMENTING
        case 0x5: // 0b0101 - I T TF
        	fragment = new ImageTextTFFragment(); break;
        case 0x6: // 0b0110 - T I TF
        	fragment = null; break;
        case 0x7: // 0b0111 - T T TF
        	fragment = null; break;
        case 0x8: // 0b1000 - I I PA
        	fragment = null; break; // NOT IMPLEMENTING
        case 0x9: // 0b1001 - I T PA
        	fragment = new ImageTextPFragment(); break;
        case 0xA: // 0b1010 - T I PA
        	fragment = null; break;
        case 0xB: // 0b1011 - T T PA
        	fragment = new TextTextPFragment(); break;
        }
        if(fragment != null) fragment.setArguments(args);
        
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

	public int getExpectedScore() {
		if(questions == null) return 0;
		// TODO: Adjust for difficulty
		// Expect user to get 2/3 right, at 1/2 the score
		int maxScore = 0;
		for(Question question : questions) {
			maxScore += question.getMaxPoints();
		}
		// Expect half the timer time; half the score is given if no timer anyway
		maxScore /= 2;
		// Expect 2/3 to be correct
		maxScore *= 2f/3f;
		return maxScore;
	}
}
