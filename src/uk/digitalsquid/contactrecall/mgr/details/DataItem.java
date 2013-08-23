package uk.digitalsquid.contactrecall.mgr.details;

import java.util.ArrayList;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.mgr.Question;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Represents a single piece of data from a {@link Contact}.
 * @author william
 *
 */
public class DataItem implements Parcelable {
	
	private Contact contact;
	
	private int field;
	
	public DataItem() {
		
	}

	public DataItem(Contact contact, int field) {
		this.contact = contact;
		this.field = field;
	}
	
	protected DataItem(Parcel in) {
		contact = in.readParcelable(Contact.class.getClassLoader());
		field = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(contact, 0);
		dest.writeInt(field);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public int getField() {
		return field;
	}

	public void setField(int field) {
		this.field = field;
	}

	public static final Creator<DataItem> CREATOR = new Creator<DataItem>() {
		@Override
		public DataItem[] newArray(int size) {
			return new DataItem[size];
		}
		
		@Override
		public DataItem createFromParcel(Parcel source) {
			return new DataItem(source);
		}
	};
	
	public static final class DataItemAdapter extends BaseAdapter {
		
		private ArrayList<DataItem> data;
		private LayoutInflater inflater;
		private App app;
		private int textLayout, imageLayout;
		
		public DataItemAdapter(App app, Context context,
				ArrayList<DataItem> data, int textLayout, int imageLayout) {
			this.data = data;
			this.inflater = LayoutInflater.from(context);
			this.app = app;
			this.textLayout = textLayout;
			this.imageLayout = imageLayout;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public DataItem getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final DataItem item = getItem(position);
			final Contact contact = item.contact;
			final int format = Question.getFieldFormat(item.field);
			final int layout = format == Question.FORMAT_IMAGE ? imageLayout : textLayout;
	        if (convertView == null) {
	            convertView = inflater.inflate(layout, null);
	        }
	        // Might need converting to different layout type
	        switch(format) {
	        case Question.FORMAT_IMAGE:
		        if(convertView.findViewById(R.id.photo) == null)
		            convertView = inflater.inflate(layout, null);
		        break;
	        case Question.FORMAT_TEXT:
		        if(convertView.findViewById(R.id.text) == null)
		            convertView = inflater.inflate(layout, null);
		        break;
	        }
	        
	        switch(format) {
	        case Question.FORMAT_IMAGE:
		        ImageView photo = (ImageView) convertView.findViewById(R.id.photo);
		        photo.setImageBitmap(contact.getPhoto(app.getPhotos()));
		        break;
	        case Question.FORMAT_TEXT:
		        TextView text = (TextView) convertView.findViewById(R.id.text);
		        text.setText(contact.getTextField(item.field));
		        break;
	        }
	        
	        // TODO FIXME: set up selection somehow

			return convertView;
		}
		
	}
}
