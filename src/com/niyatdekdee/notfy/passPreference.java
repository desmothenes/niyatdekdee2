package com.niyatdekdee.notfy;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;

public class passPreference extends EditTextPreference {
    //private Calendar calendar;
    //private TimePicker picker = null;

    public passPreference(Context ctxt) {
        super(ctxt);
    }

    public passPreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

    }

    public passPreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);

        //setPositiveButtonText(R.string.save);
        //setNegativeButtonText(R.string.cancel);
    }

	/*    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(true);
        return (picker);
    }*/

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            //calendar.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
            //calendar.set(Calendar.MINUTE, picker.getCurrentMinute());
            //setSummary(getSummary());
            String UserName = getText();
            if (callChangeListener(getText())) {
                persistString(encrypt(UserName));
                notifyChanged();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {


        if (restoreValue) {
            if (defaultValue == null) {
                String old = getPersistedString("old username");
                try {
                    setText(decrypt(old)/*.replace("?", "")*/);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                String old = getPersistedString("old " + (String) defaultValue);
                setText(decrypt(old)/*.replace("?", "")*/);
            }
        } else {
            if (defaultValue == null) {
                setText("user");
            } else {
                String old = (String) defaultValue;
                setText(old);
            }
        }
        //setSummary(getSummary());
    }

    static String decrypt(String old, Context context) {
        return old;
        /*
        System.out.println("pass decrypt");
		System.out.println("old "+old);
		SharedPreferences perf = PreferenceManager.getDefaultSharedPreferences(context);
		final int shash = perf.getInt("shash2",0);
		System.out.print("hash ");
		System.out.println(shash);
		StringBuilder temp = new StringBuilder();
		int hash = 0;

		while(shash != 0) {
			hash = 0;
			for (int i = 0;i < old.length();i++) 
				hash += (int) old.charAt(i);
			System.out.print("hash ");
			System.out.println(hash);
			temp = new StringBuilder();
			if (shash > hash) {
				for (int i = 0;i<old.length();i++) {
					temp.append((char) (1+(int)old.charAt(i)));
				}
				old = temp.toString();
			}
			else if  (shash < hash) {
				for (int i = 0;i<old.length();i++) {
					temp.append((char) (-1+(int)old.charAt(i)));
				}
				old = temp.toString();
			}
			else
				break;
		} 
		temp = new StringBuilder();
		for (int i = 0;i<old.length();i++) {
			temp.append((char) (-1+(int)old.charAt(i)));
		}
		System.out.println("temp.toString() "+temp.toString());
		return temp.toString();	*/
    }

    String decrypt(String old) {
        return old;
        /*
		System.out.println("pass decrypt");
		System.out.println("old "+old);
		SharedPreferences perf = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
		final int shash = perf.getInt("shash2",0);
		System.out.print("hash ");
		System.out.println(shash);
		StringBuilder temp = new StringBuilder();
		int hash = 0;

		while(shash != 0) {
			hash = 0;
			for (int i = 0;i < old.length();i++) 
				hash += (int) old.charAt(i);
			System.out.print("hash ");
			System.out.println(hash);
			temp = new StringBuilder();
			if (shash > hash) {
				for (int i = 0;i<old.length();i++) {
					temp.append((char) (1+(int)old.charAt(i)));
				}
				old = temp.toString();
			}
			else if  (shash < hash) {
				for (int i = 0;i<old.length();i++) {
					temp.append((char) (-1+(int)old.charAt(i)));
				}
				old = temp.toString();
			}
			else
				break;
		} 
		temp = new StringBuilder();
		for (int i = 0;i<old.length();i++) {
			temp.append((char) (-1+(int)old.charAt(i)));
		}
		System.out.println("temp.toString() "+temp.toString());
		return temp.toString();	*/
    }

    String encrypt(String value) {
        return value;
		/*
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.context).edit();

		StringBuilder temp = new StringBuilder();
		for (int i = 0;i<value.length();i++) {
			temp.append((char) (1+(int)value.charAt(i)));
		}
		final String sum = temp.toString();
		int hash = 0;
		for (int i = 0;i < sum.length();i++) 
			hash += (int)sum.charAt(i);
		editor.putInt("shash2",hash);
		System.out.println(hash);
		editor.commit();
		return sum;		*/
    }
/*	String encrypt( String value ) {
		System.out.println("zone encrypt ");
		StringBuilder sum = new StringBuilder();
		//System.out.print("value.length() = ");
		//System.out.println(value.length());
		for (int index = 0;index < value.length();index++) {	
			//System.out.println("zone encrypt "+value.charAt(index));
			//System.out.print(((int) value.charAt(index)) + (349-index) - (349 + value.length()));
			//System.out.println(String.format(" = %d + %d - %d",((int) value.charAt(index)),(349-index),(349 + value.length())));
			sum.append((char)(((int) value.charAt(index)) + (349-index) - (349 + value.length())));
		}
		System.out.println("zone end encrypt "+sum);
		return sum.toString();
	}
	
	static String decrypt( String value ) {
		System.out.println("zone decrypt ");
		StringBuilder sum = new StringBuilder();
		for (int index = 0;index < value.length();index++) {
			//System.out.print(((int) value.charAt(index)) - (349-index) + (349 + value.length()));
			//System.out.println(String.format(" = %d - %d + %d",((int) value.charAt(index)),(349-index),(349 + value.length())));
			//System.out.println("zone decrypt "+ String.valueOf(((int)value.charAt(index)) - (349-index) + (349 + value.length())));
			sum.append((char) (((int)value.charAt(index)) - (349-index) + (349 + value.length())));
		}
		System.out.println("zone end decrypt "+sum);
		return sum.toString();
	}*/
	/*    @Override
    public CharSequence getSummary() {
        if (calendar == null) {
            return null;
        }
        return DateFormat.getTimeFormat(getContext()).format(new Date(calendar.getTimeInMillis()));
    }*/

/*	protected static final String UTF8 = "utf-8";
	private static final char[] SEKRIT = "DateFormat.getTimeFormat(getContext()).format(".toCharArray() ;
	protected String encrypt( String value ) {

		try {
			final byte[] bytes = value!=null ? value.getBytes(UTF8) : new byte[0];
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT));
			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(Settings.Secure.getString(context.getContentResolver(),Settings.System.ANDROID_ID).getBytes(UTF8), 20));
			return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP),UTF8);

		} catch( Exception e ) {
			throw new RuntimeException(e);
		}

	}

	protected String decrypt(String value){
		try {
			final byte[] bytes = value!=null ? Base64.decode(value,Base64.DEFAULT) : new byte[0];
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT));
			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(Settings.Secure.getString(context.getContentResolver(),Settings.System.ANDROID_ID).getBytes(UTF8), 20));
			return new String(pbeCipher.doFinal(bytes),UTF8);

		} catch( Exception e) {
			throw new RuntimeException(e);
		}
	}
	protected static String decrypt(String value,Context ctxt){
		try {
			final byte[] bytes = value!=null ? Base64.decode(value,Base64.DEFAULT) : new byte[0];
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT));
			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(Settings.Secure.getString(ctxt.getContentResolver(),Settings.System.ANDROID_ID).getBytes(UTF8), 20));
			return new String(pbeCipher.doFinal(bytes),UTF8).replace("???????????", "");

		} catch( Exception e) {
			throw new RuntimeException(e);
		}
	}*/
} 
