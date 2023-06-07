package scienceGraphDrawer;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class doclistener2 extends DocumentFilter {

	/** The Constant _maxCharacters. */
	private static final int maxCharacters = 2;

	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {

		String text = fb.getDocument().getText(0, fb.getDocument().getLength());
		text += str;
		if ((fb.getDocument().getLength() + str.length() - length) <= maxCharacters && text.matches("^[0-9]*$")) {
			super.replace(fb, offs, length, str, a);
		}
		else 
		{
			Toolkit.getDefaultToolkit().beep();
		}
	}
	//i dont know what this does maybe delete
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {

		String text = fb.getDocument().getText(0, fb.getDocument().getLength());
		text += str;
		if ((fb.getDocument().getLength() + str.length()) <= maxCharacters && text.matches("^[0-9]*$")) {
			super.insertString(fb, offs, str, a);
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}
}
