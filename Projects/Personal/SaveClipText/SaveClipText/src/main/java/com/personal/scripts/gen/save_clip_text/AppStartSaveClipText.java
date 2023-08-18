package com.personal.scripts.gen.save_clip_text;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

final class AppStartSaveClipText {

	private AppStartSaveClipText() {
	}

	public static void main(
			final String[] args) {

		final Instant start = Instant.now();

		if (args.length >= 1 && "-help".equals(args[0])) {

			final String helpMessage = createHelpMessage();
			System.out.println(helpMessage);
			System.exit(0);
		}

		if (args.length < 1) {

			final String helpMessage = createHelpMessage();
			System.err.println("insufficient arguments" + System.lineSeparator() + helpMessage);
			System.exit(1);
		}

		final String outputTextFileFolderPathString = args[0];

		String outputTextFileName;
		if (args.length >= 2) {

			outputTextFileName = args[1];
			String ext = ext(outputTextFileName);
			if (ext == null) {

				ext = "txt";
				outputTextFileName += "." + ext;
			}

		} else {
			final String dateTimeString = createDateTimeString();
			outputTextFileName = "clipboard_text_" + dateTimeString + ".txt";
		}

		final Path outputTextFilePath =
				Paths.get(outputTextFileFolderPathString, outputTextFileName).toAbsolutePath().normalize();
		copyTo(outputTextFilePath);

		final Duration executionTime = Duration.between(start, Instant.now());
		System.out.println("done; execution time: " + durationToString(executionTime));
	}

	private static String createHelpMessage() {

		return "usage: save_clip_text FOLDER_PATH (OUTPUT_FILE_NAME)";
	}

	private static String createDateTimeString() {

		return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
	}

	static String ext(
			final String filename) {

		String ext = null;
		final int lastIndexOf = filename.lastIndexOf('.');
		if (lastIndexOf >= 0) {
			ext = filename.substring(lastIndexOf + 1);
		}
		return ext;
	}

	private static void copyTo(
			final Path outputTextFilePath) {

		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		final Transferable content = clipboard.getContents(null);
		if (content == null) {
			System.err.println("ERROR - nothing found in clipboard");
			System.exit(1);
		}

		System.out.println("--> saving clipboard text to:");
		System.out.println(outputTextFilePath);

		try {
			final Path outputTextFileFolderPath = outputTextFilePath.getParent();
			if (!Files.isDirectory(outputTextFileFolderPath)) {
				Files.createDirectories(outputTextFileFolderPath);
			}

			if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				saveTextRegular(content, outputTextFilePath);

			} else {
				noTextFound();
			}

		} catch (final Exception exc) {
			System.err.println("ERROR - failed to write text to file");
			exc.printStackTrace();
			System.exit(3);
		}
	}

	private static void saveTextRegular(
			final Transferable content,
			final Path outputTextFilePath) throws Exception {

		final String text = (String) content.getTransferData(DataFlavor.stringFlavor);
		Files.writeString(outputTextFilePath, text);
	}

	private static void noTextFound() {

		System.err.println("ERROR - no text found in clipboard");
		System.exit(2);
	}

	private static String durationToString(
			final Duration duration) {

		final StringBuilder stringBuilder = new StringBuilder();
		final long allSeconds = duration.get(ChronoUnit.SECONDS);
		final long hours = allSeconds / 3600;
		if (hours > 0) {
			stringBuilder.append(hours).append("h ");
		}

		final long minutes = (allSeconds - hours * 3600) / 60;
		if (minutes > 0) {
			stringBuilder.append(minutes).append("m ");
		}

		final long nanoseconds = duration.get(ChronoUnit.NANOS);
		final double seconds = allSeconds - hours * 3600 - minutes * 60 +
				nanoseconds / 1_000_000_000.0;
		stringBuilder.append(doubleToString(seconds)).append('s');

		return stringBuilder.toString();
	}

	private static String doubleToString(
			final double d) {

		final String str;
		if (Double.isNaN(d)) {
			str = "";

		} else {
			final String format;
			format = "0.000";
			final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
			final DecimalFormat decimalFormat = new DecimalFormat(format, decimalFormatSymbols);
			str = decimalFormat.format(d);
		}
		return str;
	}
}
