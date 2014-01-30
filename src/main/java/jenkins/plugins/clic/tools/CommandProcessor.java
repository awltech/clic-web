package jenkins.plugins.clic.tools;

import jenkins.plugins.clic.exception.CommandParsingException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * User: pierremarot
 * Date: 17/01/2014
 * Time: 17:30
 */
public class CommandProcessor {

    /**
     * http://api.dpml.net/ant/1.6.4/org/apache/tools/ant/types/Commandline.
     * html#translateCommandline%28java.lang.String%29
     * <p/>
     * Command-line cracker coming from Ant.
     *
     * @param toProcess the command line to process.
     * @return the command line broken into strings. An empty or null toProcess
     * parameter results in a zero sized array.
     * @throws CommandParsingException
     */
    public static String[] parseCommandLine(final String toProcess) throws CommandParsingException {
        if (toProcess == null || toProcess.length() == 0)
            // no command? no string
            return new String[0];
        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        final StringTokenizer tok = new StringTokenizer(toProcess, "\"\' ", true);
        final List<String> result = new ArrayList<>();
        final StringBuilder current = new StringBuilder();
        boolean lastTokenHasBeenQuoted = false;

        while (tok.hasMoreTokens()) {
            final String nextTok = tok.nextToken();
            switch (state) {
                case inQuote:
                    if ("\'".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else
                        current.append(nextTok);
                    break;
                case inDoubleQuote:
                    if ("\"".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else
                        current.append(nextTok);
                    break;
                default:
                    switch (nextTok) {
                        case "\'":
                            state = inQuote;
                            break;
                        case "\"":
                            state = inDoubleQuote;
                            break;
                        case " ":
                            if (lastTokenHasBeenQuoted || current.length() != 0) {
                                result.add(current.toString());
                                current.setLength(0);
                            }
                            break;
                        default:
                            current.append(nextTok);
                            break;
                    }
                    lastTokenHasBeenQuoted = false;
                    break;
            }
        }
        if (lastTokenHasBeenQuoted || current.length() != 0)
            result.add(current.toString());
        if (state == inQuote || state == inDoubleQuote)
            throw new CommandParsingException(toProcess);
        return result.toArray(new String[result.size()]);
    }


}
