/*******************************************************************************
 * Copyright (c) 2013 Red Hat Inc..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Mickael Istria (Red Hat) - initial API and implementation
 *******************************************************************************/
package org.eclipse.swtbot.generator.test;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.generator.ui.RecorderDialog;
import org.eclipse.swtbot.generator.ui.StartupRecorder;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractGeneratorTest {

	private Display display;
	private TestDialog dialog;
	protected SWTBot bot;
	protected RecorderDialog recorderDialog;

	@Before
	public void setUp() {
		this.recorderDialog = (RecorderDialog)StartupRecorder.openRecorder(null);
		this.bot = new SWTBot();
		bot.waitUntil(shellIsActive("SWTBot Test Recorder"),5000);
		SWTBotShell recorderShell = this.bot.shell("SWTBot Test Recorder");
		recorderShell.bot().button("Start Recording").click();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				recorderDialog.getGeneratedCodeText().setText("");
			}
		});

		this.display = Display.getDefault();
		this.display.syncExec(new Runnable() {
			public void run() {
				dialog = new TestDialog(new Shell());
				dialog.open();
			}
		});
	}

	@After
	public void tearDown() {
		this.display.syncExec(new Runnable() {
			public void run() {
				if (!dialog.getShell().isDisposed()) {
					dialog.close();
				}
				dialog = null;
			}
		});

	}

	/**
	 * Process all recorded events and generates code, ignoring all future events
	 * to compute generated code.
	 */
	public void flushEvents() {
		this.display.syncExec(new Runnable() {
			public void run() {
				AbstractGeneratorTest.this.recorderDialog.getRecorder().flushGenerationRules();
			}
		});
	}
}
