/*
 * CLiC, Framework for Command Line Interpretation in Eclipse
 *
 *     Copyright (C) 2013 Worldline or third-party contributors as
 *     indicated by the @author tags or express copyright attribution
 *     statements applied by the authors.
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package jenkins.plugins.clic.commands;

import hudson.ExtensionPoint;
import joptsimple.OptionParser;
import joptsimple.OptionSet;


public abstract class AbstractCommand implements ExtensionPoint{

	/**
	 * {@link #parser} contains the {@link joptsimple.OptionParser} to be used by this
	 * command. It allows to define all the available options to be used by your
	 * command. It relies on JOpt-Simple, so please have a look at the dedicated
	 * documentation for more inforamtion: http://pholser.github.io/jopt-simple/
	 *
	 * @see joptsimple.OptionParser
	 */
	protected OptionParser parser = new OptionParser();

	/**
	 * {@link #options} will be computed internally just before executing your
	 * command. It consists in an {@link joptsimple.OptionSet} which will contain the
	 * result of your command parsing.
	 *
	 * In order to ensure a proper execution of a provided command-line, the
	 * {@link #options} must be computed, which is done internally.
	 *
	 * @see joptsimple.OptionSet
	 */
	protected OptionSet options;

	/**
	 * Default Constructor
	 */
	public AbstractCommand() {
	}

	/**
	 * This method allows to define all the options which are available for your
	 * command. To do so, just use the {@link #parser} object which is available
	 * for your command, and specify all the available options. You should have
	 * a look at http://pholser.github.io/jopt-simple/ for more information and
	 * examples on how to specify those options.
	 *
	 * Please consider that this method will be called internally before any
	 * execution, in order to be able to compute all the parameters of your
	 * command. If you don't specify any options in this method, chances are
	 * your {@link #execute(CommandContext)} method will lead to errors.
	 *
	 * @see joptsimple.OptionParser
	 */
	public abstract void configureParser();

	/**
	 * This method should actually contain the actul behavior of your command.
	 * You'll be able to query the {@link #options} object, which will be
	 * populated internally just before calling this function, in order to get
	 * the values of all the specified options. For more information about
	 * querying this object, please refer to JOpt-Simple documentation:
	 * http://pholser.github.io/jopt-simple/
	 *
	 * During the execution, you should use the provided {@link CommandContext}
	 * instance which allows to give some feedbacks to the end-user thanks to
	 * the {@link CommandContext#write(String)} method.
	 *
	 * @param context
	 *            a {@link CommandContext} instance which allows to give
	 *            feedbacks to the end-user during the execution of your command
	 */
	public abstract void execute(CommandContext context);

	/**
	 * This function allows to compute the provided args using the
	 * {@link #parser} which as been configured in {@link #configureParser()}.
	 * It uses {@link joptsimple.OptionParser#parse(String...)} internally.
	 * 
	 * This function will be called automatically in the command management
	 * process, so you shouldn't have to call it anytime.
	 * 
	 * @param args
	 *            is an array of {@link String} containing all the parameters to
	 *            be used by your command. This array of {@link String} will be
	 *            computed by the {@link #parser}
	 */
	public final void parse(final String[] args) {
		options = parser.parse(args);
	}

	/**
	 * Simple getter for {@link #parser}. It aims at being used internally, so
	 * you shouldn't need to use it.
	 * 
	 * @return {@link #parser}
	 */
	public final OptionParser getParser() {
		return parser;
	}

	/**
	 * Simple getter for {@link #options}.
	 * 
	 * @return {@link #options}
	 * @since 1.1
	 */
	public final OptionSet getOptions() {
		return options;
	}


}
