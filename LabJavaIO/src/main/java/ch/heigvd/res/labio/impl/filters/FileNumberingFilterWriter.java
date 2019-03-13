package ch.heigvd.res.labio.impl.filters;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * This class transforms the streams of character sent to the decorated writer.
 * When filter encounters a line separator, it sends it to the decorated writer.
 * It then sends the line number and a tab character, before resuming the write
 * process.
 *
 * Hello\n\World -> 1\Hello\n2\tWorld
 *
 * @author Olivier Liechti
 */
public class FileNumberingFilterWriter extends FilterWriter {

	private int counter = 0;
	private boolean mightBeWindowsReturn = false;

	private static final Logger LOG = Logger.getLogger(FileNumberingFilterWriter.class.getName());

	public FileNumberingFilterWriter(Writer out) {
		super(out);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		write(str.toCharArray(), off, len);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		for (int i = off; i < off + len; ++i) {
			write(cbuf[i]);
		}
	}

	@Override
	public void write(int c) throws IOException {
		if (counter == 0) {
			super.write("" + ++counter + '\t');
		}

		if (mightBeWindowsReturn) {
			mightBeWindowsReturn = false;
			if (c == '\n') {
				super.write(c);
				super.write("" + ++counter + '\t');
				return;
			} else {
				super.write("" + ++counter + '\t');
			}
		}

		if (c == '\r') {
			mightBeWindowsReturn = true;
			super.write(c);
		}
		else if (c == '\n') {
			super.write(c);
			super.write("" + ++counter + '\t');
		} else {
			super.write(c);
			mightBeWindowsReturn = false;
		}
	}

}
