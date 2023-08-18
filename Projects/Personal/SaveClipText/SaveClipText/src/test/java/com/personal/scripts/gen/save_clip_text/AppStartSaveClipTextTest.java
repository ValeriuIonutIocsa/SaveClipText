package com.personal.scripts.gen.save_clip_text;

import org.junit.jupiter.api.Test;

class AppStartSaveClipTextTest {

	@Test
	void testMain() {

		final String[] args;
		final int input = Integer.parseInt("21");
		if (input == 1) {
			args = new String[] { "D:\\tmp\\SaveClipText" };

		} else if (input == 11) {
			args = new String[] { "D:\\tmp\\SaveClipText", "clipboard_text1" };

		} else if (input == 21) {
			args = new String[] { "D:\\tmp\\SaveClipText", "clipboard_text1.txt" };

		} else if (input == 101) {
			args = new String[] { "" };
		} else if (input == 102) {
			args = new String[] { "-help" };

		} else {
			throw new RuntimeException();
		}

		AppStartSaveClipText.main(args);
	}
}
