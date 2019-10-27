// -----BEGIN DISCLAIMER-----
/*******************************************************************************
 * Copyright (c) 2019 JCrypTool Team and Contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
// -----END DISCLAIMER-----
package org.jcryptool.visual.crtverification.views;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.CommandManager;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.jcryptool.core.logging.utils.LogUtil;
import org.jcryptool.core.util.colors.ColorService;
import org.jcryptool.core.util.fonts.FontService;
import org.jcryptool.visual.crtverification.Activator;

public class CrtVerViewComposite extends Composite implements PaintListener {
    // Object Controller
    CrtVerViewController controller;
    CrtVerViewComposite crtComposite;
    
	// Variables
	Text textRootCaFromDay;
	Text textCaFromDay;
	Text textCertFromDay;
	Text textRootCaThruDay;
	Text textCaThruDay;
	Text textCertThruDay;
	Text textSignatureDateDay;
	Text textVerificationDateDay;

	Label thruRootCa;
	Label fromRootCa;
	Label thruCa;
	Label fromCa;
	Label thruCert;
	Label fromCert;
	Label signatureDate;
	Label labelValiditySymbol;
	Label verificationDate;

	Scale scaleCertBegin;
	Scale scaleCertEnd;
	Scale scaleCaBegin;
	Scale scaleCaEnd;
	Scale scaleRootCaBegin;
	Scale scaleRootCaEnd;
	Scale scaleVerificationDate;
	Scale scaleSignatureDate;

	Button btnLoadRootCa;
	Button btnLoadCa;
	Button btnLoadUserCert;
	Button btnValidate;
	Button btnShellModel;
	Button btnShellModelModified;
	Button btnChainModel;
//	Button btnReset;
	Canvas canvas1;
	Canvas canvas2;
	int arrowSigDiff = 0;
	int arrowVerDiff = 0;

	Text txtLogWindow;
	
//	private Color light_blue = new Color(Display.getCurrent(), 30, 144, 255);
	private Color violet = new Color(Display.getCurrent(), 72, 61, 139);
	private Color orange = new Color(Display.getCurrent(), 255, 140, 0);

	/**
	 * counter for number of performed validations
	 */
	int validationCounter = 0;

    private void addContributionItem(IContributionManager manager, final String commandId,
           	final ImageDescriptor icon, final String tooltip)
        {
           	CommandContributionItemParameter param
           		= new CommandContributionItemParameter(PlatformUI.getWorkbench(), null, commandId, SWT.PUSH);
           	if(icon != null)
           		param.icon = icon;
           	if(tooltip != null && !tooltip.equals(""))
           		param.tooltip = tooltip;
           	CommandContributionItem item = new CommandContributionItem(param);
           	manager.add(item);
        }

    private void defineCommand(final String commandId, final String name, AbstractHandler handler) {
        ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
    	Command command = commandService.getCommand(commandId);
    	command.define(name,  null, commandService.getCategory(CommandManager.AUTOGENERATED_CATEGORY_ID));
    	command.setHandler(handler);
    }

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CrtVerViewComposite(Composite parent, int style, CrtVerView view) {
		super(parent, style);
		setBackground(ColorService.GRAY);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		this.crtComposite = this;
		this.controller = new CrtVerViewController(this);

		// Adds reset button to the toolbar
		IToolBarManager toolBarMenu = view.getViewSite().getActionBars()
				.getToolBarManager();
		// Action action = new Action("Reset", IAction.AS_PUSH_BUTTON) {public void run() {controller.reset();}}; //$NON-NLS-1$
		// action.setImageDescriptor(Activator
		//		.getImageDescriptor("icons/reset.gif")); //$NON-NLS-1$
		// toolBarMenu.add(action);
		AbstractHandler handler = new AbstractHandler() {
			@Override
			public Object execute(ExecutionEvent event) {
				controller.reset();
				return null;
			}
		
		};
		defineCommand("org.jcryptool.visual.crtVerification.reset", "Reset", handler);
		addContributionItem(toolBarMenu, "org.jcryptool.visual.crtVerification.reset",
			Activator.getImageDescriptor("icons/reset.gif"), null);

		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		composite.setLayout(gl_composite);
		
		//Titel mit kurzer Beschreibung des Plugins
		Composite header = new Composite(composite, SWT.NONE);
		header.setLayout(new GridLayout());
		header.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		header.setBackground(ColorService.WHITE);
		
		Text title = new Text(header, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		title.setBackground(ColorService.WHITE);
		title.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		title.setFont(FontService.getLargeBoldFont());
		title.setText(Messages.CrtVerViewComposite_title);

		Text txtDescription = new Text(header, SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		txtDescription.setBackground(ColorService.WHITE);
		GridData gd_txtDescription = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd_txtDescription.widthHint = 1000;
		txtDescription.setLayoutData(gd_txtDescription);
		txtDescription.setText(Messages.CrtVerViewComposite_description);
		
		//Beginn des Bereichs links oben in dem die 8 Schieberregler sind 
		Composite leftTop = new Composite(composite, SWT.NONE);
		leftTop.setLayout(new GridLayout(5, false));
		leftTop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));
		
		new Label(leftTop, SWT.NONE);		
		
		Label lblNotValidBefore = new Label(leftTop, SWT.CENTER);
		lblNotValidBefore.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblNotValidBefore.setFont(FontService.getNormalBoldFont());
		lblNotValidBefore.setText(Messages.CrtVerViewComposite_notValidBefore);

		Label lblNotValidAfter = new Label(leftTop, SWT.CENTER);
		lblNotValidAfter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblNotValidAfter.setText(Messages.CrtVerViewComposite_notValidAfter);
		lblNotValidAfter.setFont(FontService.getNormalBoldFont());
		
		new Label(leftTop, SWT.NONE);
		
		Label date1 = new Label(leftTop, SWT.LEFT);
		date1.setText(controller.scaleUpdate(0, 180, controller.getDateformat3()));
		date1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		
		Label date2 = new Label(leftTop, SWT.RIGHT);
		date2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		date2.setText(controller.scaleUpdate(360, 180, controller.getDateformat3()));
		
		Label date3 = new Label(leftTop, SWT.LEFT);
		date3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		date3.setText(controller.scaleUpdate(0, 180, controller.getDateformat3()));
		
		Label date4 = new Label(leftTop, SWT.RIGHT);
		date4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		date4.setText(controller.scaleUpdate(360, 180, controller.getDateformat3()));
		
		Label lblRootCa = new Label(leftTop, SWT.NONE);
		lblRootCa.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lblRootCa.setText(Messages.CrtVerViewComposite_RootCa);

		scaleRootCaBegin = new Scale(leftTop, SWT.NONE);
		GridData gd_scaleRootCaBegin = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_scaleRootCaBegin.widthHint = 360;
		scaleRootCaBegin.setLayoutData(gd_scaleRootCaBegin);
		scaleRootCaBegin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				controller.parseDatesFromComposite();
				controller.setLogText(Messages.CrtVerViewComposite_RootCa + " \""
						+ Messages.CrtVerViewComposite_notValidBefore + "\" "
						+ Messages.CrtVerViewComposite_dateSet + " "
						+ controller.getFromRootCa());
			}
		});
		scaleRootCaBegin.setToolTipText("");
		scaleRootCaBegin.setMaximum(360);
		scaleRootCaBegin.setSelection(180);
		
		
		scaleRootCaEnd = new Scale(leftTop, SWT.NONE);
		GridData gd_scaleRootCaEnd = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_scaleRootCaEnd.widthHint = 360;
		scaleRootCaEnd.setLayoutData(gd_scaleRootCaEnd);
		scaleRootCaEnd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				controller.parseDatesFromComposite();
				controller.setLogText(Messages.CrtVerViewComposite_RootCa + " \""
						+ Messages.CrtVerViewComposite_notValidAfter + "\" "
						+ Messages.CrtVerViewComposite_dateSet + " "
						+ controller.getThruRootCa());
			}
		});
		scaleRootCaEnd.setMaximum(360);
		scaleRootCaEnd.setSelection(180);
		
		Label lblCa = new Label(leftTop, SWT.NONE);
		lblCa.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lblCa.setText(Messages.CrtVerViewComposite_Ca);
		
		scaleCaBegin = new Scale(leftTop, SWT.NONE);
		GridData gd_scaleCaBegin = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_scaleCaBegin.widthHint = 360;
		scaleCaBegin.setLayoutData(gd_scaleCaBegin);
		scaleCaBegin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				controller.parseDatesFromComposite();
				controller.setLogText(Messages.CrtVerViewComposite_Ca + " \""
						+ Messages.CrtVerViewComposite_notValidBefore + "\" "
						+ Messages.CrtVerViewComposite_dateSet + " "
						+ controller.getFromCA());
			}
		});
		scaleCaBegin.setMaximum(360);
		
		scaleCaEnd = new Scale(leftTop, SWT.NONE);
		GridData gd_scaleCaEnd = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_scaleCaEnd.widthHint = 360;
		scaleCaEnd.setLayoutData(gd_scaleCaEnd);
		scaleCaEnd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				controller.parseDatesFromComposite();
				controller.setLogText(Messages.CrtVerViewComposite_Ca + " \""
						+ Messages.CrtVerViewComposite_notValidAfter + "\" "
						+ Messages.CrtVerViewComposite_dateSet + " "
						+ controller.getThruCA());
			}
		});
		scaleCaEnd.setMaximum(360);
		scaleCaEnd.setSelection(180);
		
		Label lblUserCertificate = new Label(leftTop, SWT.NONE);
		lblUserCertificate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lblUserCertificate.setText(Messages.CrtVerViewComposite_UserCertificate);
		
		scaleCertBegin = new Scale(leftTop, SWT.NONE);
		GridData gd_scaleCertBegin = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_scaleCertBegin.widthHint = 360;
		scaleCertBegin.setLayoutData(gd_scaleCertBegin);
		scaleCertBegin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				controller.parseDatesFromComposite();
				controller.setLogText(Messages.CrtVerViewComposite_UserCertificate + " \"" + 
								Messages.CrtVerViewComposite_notValidBefore + "\" "	+ 
								Messages.CrtVerViewComposite_dateSet + " " + 
								controller.getFromClient());
			}
		});
		scaleCertBegin.setMaximum(360);
		scaleCertBegin.setSelection(180);
		
		scaleCertEnd = new Scale(leftTop, SWT.NONE);
		GridData gd_scaleCertEnd = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_scaleCertEnd.widthHint = 360;
		scaleCertEnd.setLayoutData(gd_scaleCertEnd);
		scaleCertEnd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				controller.parseDatesFromComposite();
				controller
						.setLogText(Messages.CrtVerViewComposite_UserCertificate + " \"" + 
								Messages.CrtVerViewComposite_notValidAfter + "\" " + 
								Messages.CrtVerViewComposite_dateSet + " " + 
								controller.getThruClient());
			}
		});
		scaleCertEnd.setMaximum(360);
		scaleCertEnd.setSelection(180);
		
		Label lblArrowSig = new Label(leftTop, SWT.NONE);
		lblArrowSig.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lblArrowSig.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
		lblArrowSig.setText(Messages.CrtVerViewComposite_signatureDate);
		
		//left Canvas for the left Arrows
		canvas1 = new Canvas(leftTop, SWT.NONE);
		canvas1.setLayout(new GridLayout(1, false));
		GridData gd_canvas1 = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 2);
		gd_canvas1.heightHint = 51;
		canvas1.setLayoutData(gd_canvas1);
		canvas1.addPaintListener(this);
		
		//right canvas for the two right arrows
		canvas2 = new Canvas(leftTop, SWT.NONE);
		GridData gd_canvas2 = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 2);
		gd_canvas2.heightHint = 51;
		canvas2.setLayoutData(gd_canvas2);
		canvas2.setLayout(new GridLayout(1, false));
		canvas2.addPaintListener(this);
		
		Label lblArrowVer = new Label(leftTop, SWT.NONE);
		lblArrowVer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lblArrowVer.setForeground(violet);
		lblArrowVer.setText(Messages.CrtVerViewComposite_verificationDate);
		
		Label seperatorHorizontal = new Label(leftTop, SWT.SEPARATOR | SWT.HORIZONTAL);
		seperatorHorizontal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 5, 1));
		
		new Label(leftTop, SWT.NONE);
		
		Label date5 = new Label(leftTop, SWT.LEFT);
		date5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		date5.setText(controller.scaleUpdate(0, 360, controller.getDateformat3()));
		
		Label date6 = new Label(leftTop, SWT.RIGHT);
		date6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		date6.setText(controller.scaleUpdate(720, 360, controller.getDateformat3()));
		
		Label lblSignatureDate = new Label(leftTop, SWT.NONE);
		lblSignatureDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lblSignatureDate.setText(Messages.CrtVerViewComposite_signatureDate);
		
		scaleSignatureDate = new Scale(leftTop, SWT.NONE);
		GridData gd_scaleSignatureDate = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
		gd_scaleSignatureDate.widthHint = 720;
		scaleSignatureDate.setLayoutData(gd_scaleSignatureDate);
		scaleSignatureDate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				controller.parseDatesFromComposite();
				controller.setLogText(Messages.CrtVerViewComposite_signatureDate
								+ " " + Messages.CrtVerViewComposite_dateSet
								+ " " + controller.getSigDate());
			}
		});
		scaleSignatureDate.setMaximum(720);
		scaleSignatureDate.setSelection(360);
		
		Label lblVerificationDate = new Label(leftTop, SWT.NONE);
		lblVerificationDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lblVerificationDate.setText(Messages.CrtVerViewComposite_verificationDate);
		
		scaleVerificationDate = new Scale(leftTop, SWT.NONE);
		GridData gd_scaleVerificationDate = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
		gd_scaleVerificationDate.widthHint = 720;
		scaleVerificationDate.setLayoutData(gd_scaleVerificationDate);
		scaleVerificationDate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				controller.parseDatesFromComposite();
				controller
						.setLogText(Messages.CrtVerViewComposite_verificationDate
								+ " "
								+ Messages.CrtVerViewComposite_dateSet
								+ " " + controller.getVerDate());
			}
		});
		scaleVerificationDate.setMaximum(720);
		scaleVerificationDate.setSelection(360);
		
		Composite loadButtonsComposite = new Composite(composite, SWT.NONE);
		loadButtonsComposite.setLayout(new GridLayout());
		loadButtonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		btnLoadRootCa = new Button(loadButtonsComposite, SWT.NONE);
		btnLoadRootCa.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		btnLoadRootCa.setText(Messages.CrtVerViewComposite_loadRootCa);
		btnLoadRootCa.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ChooseCert wiz = new ChooseCert(3, crtComposite);
					WizardDialog dialog = new WizardDialog(new Shell(Display
							.getCurrent()), wiz) {
						@Override
						protected void configureShell(Shell newShell) {
							super.configureShell(newShell);
							// set size of the wizard-window (x,y)
							newShell.setSize(700, 500);
						}
					};
					if (dialog.open() == Window.OK) {
						// Hier kann man Aktionen durfuehren die passieren
						// sollen wenn die WizardPage aufgerufen wird
						// zB aktivieren/deaktivieren von Buttons der
						// Hauptansicht
					}
				} catch (Exception ex) {
					LogUtil.logError(Activator.PLUGIN_ID, ex);
				}
			}
		});

		btnLoadCa = new Button(loadButtonsComposite, SWT.NONE);
		btnLoadCa.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		btnLoadCa.setText(Messages.CrtVerViewComposite_loadCa);
		btnLoadCa.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ChooseCert wiz = new ChooseCert(2, crtComposite);
					WizardDialog dialog = new WizardDialog(new Shell(Display
							.getCurrent()), wiz) {
						@Override
						protected void configureShell(Shell newShell) {
							super.configureShell(newShell);
							// set size of the wizard-window (x,y)
							newShell.setSize(700, 500);
						}
					};
					if (dialog.open() == Window.OK) {
						// Hier kann man Aktionen durfuehren die passieren
						// sollen wenn die WizardPage aufgerufen wird
						// zB aktivieren/deaktivieren von Buttons der
						// Hauptansicht
					}
				} catch (Exception ex) {
					LogUtil.logError(Activator.PLUGIN_ID, ex);
				}
			}
		});

		btnLoadUserCert = new Button(loadButtonsComposite, SWT.NONE);
		btnLoadUserCert.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// Selection Listeners | Scales
		btnLoadUserCert.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ChooseCert wiz = new ChooseCert(1, crtComposite);
					WizardDialog dialog = new WizardDialog(new Shell(Display
							.getCurrent()), wiz) {
						@Override
						protected void configureShell(Shell newShell) {
							super.configureShell(newShell);
							// set size of the wizard-window (x,y)
							newShell.setSize(700, 500);
						}
					};
					
					if (dialog.open() == Window.OK) {
						// Hier kann man Aktionen durfuehren die passieren
						// sollen wenn die WizardPage aufgerufen wird
						// zB aktivieren/deaktivieren von Buttons der
						// Hauptansicht
					}
				} catch (Exception ex) {
					LogUtil.logError(Activator.PLUGIN_ID, ex);
				}
			}
		});
		btnLoadUserCert.setText(Messages.CrtVerViewComposite_loadUserCert);

		Composite logComposite =  new Composite(composite, SWT.NONE);
		logComposite.setLayout(new GridLayout());
		logComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));

		Label lblLog = new Label(logComposite, SWT.NONE);
		lblLog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		lblLog.setText(Messages.CrtVerViewComposite_lblLog_text);

		txtLogWindow = new Text(logComposite, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtLogWindow.setBackground(ColorService.WHITE);
		txtLogWindow.setEditable(false);
		GridData gd_txtLogWindow = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_txtLogWindow.heightHint = 200;
		gd_txtLogWindow.widthHint = 300;
		txtLogWindow.setLayoutData(gd_txtLogWindow);
		
		Group grpDetails = new Group(composite, SWT.NONE);
		GridData gd_grpDetails = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_grpDetails.verticalIndent = 30;
		grpDetails.setLayoutData(gd_grpDetails);
		grpDetails.setLayout(new GridLayout(12, false));
		grpDetails.setText(Messages.CrtVerViewComposite_details);
		
		new Label(grpDetails, SWT.NONE);
		
		Label labelHeaderRootCa = new Label(grpDetails, SWT.NONE);
		labelHeaderRootCa.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
		labelHeaderRootCa.setText(Messages.CrtVerViewComposite_RootCa);
		
		Label labelHeaderCa = new Label(grpDetails, SWT.NONE);
		labelHeaderCa.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
		labelHeaderCa.setText(Messages.CrtVerViewComposite_Ca);
		
		Label labelHeaderCert = new Label(grpDetails, SWT.NONE);
		labelHeaderCert.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
		labelHeaderCert.setText(Messages.CrtVerViewComposite_UserCertificate);
		
		Label separatorDetailsVertical = new Label(grpDetails, SWT.SEPARATOR | SWT.VERTICAL);
		separatorDetailsVertical.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 3));
		
		Label labelHeaderSignatureDate = new Label(grpDetails, SWT.NONE);
		labelHeaderSignatureDate.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
		labelHeaderSignatureDate.setText(Messages.CrtVerViewComposite_signatureDate);
		
		Label labelHeaderVerificationDate = new Label(grpDetails, SWT.NONE);
		labelHeaderVerificationDate.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
		labelHeaderVerificationDate.setText(Messages.CrtVerViewComposite_verificationDate);
		
		
		Label lblValidFrom = new Label(grpDetails, SWT.NONE);
		lblValidFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lblValidFrom.setText(Messages.CrtVerViewComposite_validFrom);
		
		textRootCaFromDay = new Text(grpDetails, SWT.BORDER | SWT.CENTER);
		GridData gd_textRootCaFromDay = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_textRootCaFromDay.horizontalIndent = 30;
		gd_textRootCaFromDay.widthHint = 20;
		textRootCaFromDay.setLayoutData(gd_textRootCaFromDay);
		textRootCaFromDay.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				controller.inputcheck(textRootCaFromDay);
			}
		});
		textRootCaFromDay.setToolTipText(Messages.CrtVerViewComposite_rootCaFromDay);
		textRootCaFromDay.setText("1");
		textRootCaFromDay.setTextLimit(2);
		
		fromRootCa = new Label(grpDetails, SWT.NONE);
		fromRootCa.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		
		textCaFromDay = new Text(grpDetails, SWT.BORDER | SWT.CENTER);
		GridData gd_textCaFromDay = new GridData(SWT.FILL, SWT.CENTER, false, false); 
		gd_textCaFromDay.widthHint = 20;
		textCaFromDay.setLayoutData(gd_textCaFromDay);
		textCaFromDay.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				controller.inputcheck(textCaFromDay);
			}
		});
		textCaFromDay.setToolTipText(Messages.CrtVerViewComposite_caFromDay);
		textCaFromDay.setText("1");
		textCaFromDay.setTextLimit(2);
		
		fromCa = new Label(grpDetails, SWT.NONE);
		fromCa.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		
		textCertFromDay = new Text(grpDetails, SWT.BORDER | SWT.CENTER);
		GridData gd_textCertFromDay = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_textCertFromDay.widthHint = 20;
		textCertFromDay.setLayoutData(gd_textCertFromDay);
		textCertFromDay.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				controller.inputcheck(textCertFromDay);
			}
		});
		textCertFromDay.setToolTipText(Messages.CrtVerViewComposite_userCertificateFromDay);
		textCertFromDay.setText("1");
		textCertFromDay.setTextLimit(2);

		fromCert = new Label(grpDetails, SWT.NONE);
		fromCert.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		
		textSignatureDateDay = new Text(grpDetails, SWT.BORDER | SWT.CENTER);
		GridData gd_textSignatureDateDay = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_textSignatureDateDay.widthHint = 20;
		textSignatureDateDay.setLayoutData(gd_textSignatureDateDay);
		textSignatureDateDay.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				controller.inputcheck(textSignatureDateDay);
			}
		});
		textSignatureDateDay.setToolTipText(Messages.CrtVerViewComposite_signatureDateDay);
		textSignatureDateDay.setText("1");
		textSignatureDateDay.setTextLimit(2);

		signatureDate = new Label(grpDetails, SWT.NONE);
		signatureDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		

		textVerificationDateDay = new Text(grpDetails, SWT.BORDER | SWT.CENTER);
		GridData gd_textVerificationDateDay = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_textVerificationDateDay.widthHint = 20;
		textVerificationDateDay.setLayoutData(gd_textVerificationDateDay);
		textVerificationDateDay.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				controller.inputcheck(textVerificationDateDay);
			}
		});
		textVerificationDateDay.setToolTipText(Messages.CrtVerViewComposite_verificationDateDay);
		textVerificationDateDay.setText("1");
		textVerificationDateDay.setTextLimit(2);
		
		verificationDate = new Label(grpDetails, SWT.NONE);
		verificationDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		Label lblValidThru = new Label(grpDetails, SWT.NONE);
		lblValidThru.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		lblValidThru.setText(Messages.CrtVerViewComposite_validThru);

		textRootCaThruDay = new Text(grpDetails, SWT.BORDER | SWT.CENTER);
		GridData gd_textRootCaThruDay = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_textRootCaThruDay.horizontalIndent = 30;
		gd_textRootCaThruDay.widthHint = 20;
		textRootCaThruDay.setLayoutData(gd_textRootCaThruDay);
		textRootCaThruDay.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				controller.inputcheck(textRootCaThruDay);
			}
		});
		
		textRootCaThruDay.setToolTipText(Messages.CrtVerViewComposite_rootCaThruDay);
		textRootCaThruDay.setText("1");
		textRootCaThruDay.setTextLimit(2);
		
		thruRootCa = new Label(grpDetails, SWT.NONE);
		thruRootCa.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		textCaThruDay = new Text(grpDetails, SWT.BORDER | SWT.CENTER);
		GridData gd_textCaThruDay = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_textCaThruDay.widthHint = 20;
		textCaThruDay.setLayoutData(gd_textCaThruDay);
		textCaThruDay.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				controller.inputcheck(textCaThruDay);
			}
		});
		textCaThruDay.setToolTipText(Messages.CrtVerViewComposite_caThruDay);
		textCaThruDay.setText("1");
		textCaThruDay.setTextLimit(2);
		
		thruCa = new Label(grpDetails, SWT.NONE);
		thruCa.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		textCertThruDay = new Text(grpDetails, SWT.BORDER | SWT.CENTER);
		GridData gd_textCertThruDay = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_textCertThruDay.widthHint = 20;
		textCertThruDay.setLayoutData(gd_textCertThruDay);
		textCertThruDay.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				controller.inputcheck(textCertThruDay);
			}
		});
		textCertThruDay.setToolTipText(Messages.CrtVerViewComposite_userCertificateThruDay);
		textCertThruDay.setText("1");
		textCertThruDay.setTextLimit(2);

		// Initialize Label "Thru User Cert" with actual date
		thruCert = new Label(grpDetails, SWT.NONE);
		thruCert.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		
		Composite settingsComposite = new Composite(composite, SWT.NONE);
		settingsComposite.setLayout(new GridLayout(5, false));
		GridData gd_settingsComposite = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gd_settingsComposite.verticalIndent = 30;
		settingsComposite.setLayoutData(gd_settingsComposite);
		
		//Commented out because it does the same like restart in the left top. Replaced with an empty Label
		// to reenable the Button just comment the next Label out and uncomment the following button.
//		btnReset = new Button(settingsComposite, SWT.PUSH); 
//		btnReset.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
//		btnReset.setText(Messages.CrtVerViewComposite_reset);
//		btnReset.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				controller.reset();
//			}
//		});
//		new Label(settingsComposite, SWT.NONE).setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		//Commented out because they only open another Plugin. This is also possible by selecting the wished Plugin
		//from the menu.
//		Button btnBack = new Button(settingsComposite, SWT.PUSH);
//		btnBack.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
//		btnBack.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				try {
//					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
//						showView("org.jcryptool.visual.jctca.JCTCA_Visual");
//				} catch (PartInitException e1) {
//					LogUtil.logError(Activator.PLUGIN_ID, e1);
//				}
//			}
//		});
//		btnBack.setText(Messages.CrtVerViewComposite_pki_plugin);
//		new Label(settingsComposite, SWT.NONE).setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		//Commented out because they only open another Plugin. This is also possible by selecting the wished Plugin
		//from the menu.
//		Button btnForward = new Button(settingsComposite, SWT.PUSH);
//		btnForward.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
//		btnForward.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				try {
//					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
//						showView("org.jcryptool.visual.sigVerification.view");
//				} catch (PartInitException e1) {
//					LogUtil.logError(Activator.PLUGIN_ID, e1);
//				}
//			}
//		});
//		btnForward.setText(Messages.CrtVerViewComposite_signatureVerification);
//		new Label(settingsComposite, SWT.NONE).setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

		btnShellModel = new Button(settingsComposite, SWT.RADIO);
		btnShellModel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		btnShellModel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				labelValiditySymbol.setVisible(false);
//				scaleVerificationDate.setEnabled(true);
			}
		});
		btnShellModel.setSelection(true);
		btnShellModel.setText(Messages.CrtVerViewComposite_shellModel);

		btnShellModelModified = new Button(settingsComposite, SWT.RADIO);
		btnShellModelModified.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		btnShellModelModified.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				labelValiditySymbol.setVisible(false);
//				scaleVerificationDate.setEnabled(false);
			}
		});
		btnShellModelModified.setText(Messages.CrtVerViewComposite_modifiedshellModel);

		btnChainModel = new Button(settingsComposite, SWT.RADIO);
		btnChainModel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		btnChainModel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				labelValiditySymbol.setVisible(false);
//				scaleVerificationDate.setEnabled(false);
			}
		});
		btnChainModel.setText(Messages.CrtVerViewComposite_chainModel);

		btnValidate = new Button(settingsComposite, SWT.PUSH);
		btnValidate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		btnValidate.setText(Messages.CrtVerViewComposite_validate);
		
		labelValiditySymbol = new Label(settingsComposite, SWT.NONE);

		labelValiditySymbol.setImage(Activator.getImageDescriptor("icons/rotesKreuzKlein.png").createImage());
		labelValiditySymbol.setVisible(false);

		btnValidate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setLogText("### " + String.format("%03d", validationCounter) + " ###");
				validationCounter++;

				controller.logValidityDates();

				if (btnShellModel.getSelection()) {
					controller.validate(0);
				} else if (btnShellModelModified.getSelection()) {
					controller.validate(1);
				} else if (btnChainModel.getSelection()) {
					controller.validate(2);
				}
				controller.setLogText("---------------------------------");
			}
		});

		// Selection Listeners | Scales
		scaleRootCaBegin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Add or Remain Time dependent on selection
				controller.updateElements(fromRootCa, scaleRootCaBegin, 180);
				// Hide Validity Symbols (red/green)
				labelValiditySymbol.setVisible(false);
				setLoadBtnsOrange();
			}
		});

		scaleRootCaEnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Add or Remain Time dependent on selection
				controller.updateElements(thruRootCa, scaleRootCaEnd, 180);
				// Hide Validity Symbols (red/green)
				labelValiditySymbol.setVisible(false);
				setLoadBtnsOrange();
			}
		});

		scaleCaBegin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Add or Remain Time dependent on selection
				controller.updateElements(fromCa, scaleCaBegin, 180);
				// Hide Validity Symbols (red/green)
				labelValiditySymbol.setVisible(false);
				setLoadBtnsOrange();
			}
		});

		scaleCaEnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Add or Remain Time dependent on selection
				controller.updateElements(thruCa, scaleCaEnd, 180);
				// Hide Validity Symbols (red/green)
				labelValiditySymbol.setVisible(false);
				setLoadBtnsOrange();
			}
		});

		scaleCertBegin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Add or Remain Time dependent on selection
				controller.updateElements(fromCert, scaleCertBegin, 180);
				// Hide Validity Symbols (red/green)
				labelValiditySymbol.setVisible(false);
				setLoadBtnsOrange();
			}
		});

		scaleCertEnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Add or Remain Time dependent on selection
				controller.updateElements(thruCert, scaleCertEnd, 180);
				// Hide Validity Symbols (red/green)
				labelValiditySymbol.setVisible(false);
				setLoadBtnsOrange();
			}
		});

		scaleSignatureDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Add or Remain Time dependent on selection
				controller.updateElements(signatureDate, scaleSignatureDate,
						360);
				if (((scaleSignatureDate.getSelection() - 360) % 2) == 0) {
					arrowSigDiff = (scaleSignatureDate.getSelection() - 360) / 2;
				} else {
					arrowSigDiff = ((scaleSignatureDate.getSelection() + 1) - 360) / 2;
				}
				// arrowSigDiff = ScaleSignatureDate.getSelection()-360;
				canvas1.redraw();
				canvas2.redraw();
				// Hide Validity Symbols (red/green)
				labelValiditySymbol.setVisible(false);
				setLoadBtnsOrange();
			}
		});

		scaleVerificationDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Add or Remain Time dependent on selection
				controller.updateElements(verificationDate,
						scaleVerificationDate, 360);
				if (((scaleVerificationDate.getSelection() - 360) % 2) == 0) {
					arrowVerDiff = (scaleVerificationDate.getSelection() - 360) / 2;
				} else {
					arrowVerDiff = ((scaleVerificationDate.getSelection() + 1) - 360) / 2;
				}
				// arrowVerDiff = ScaleVerificationDate.getSelection()-360;
				canvas1.redraw();
				canvas2.redraw();
				// Hide Validity Symbols (red/green)
				labelValiditySymbol.setVisible(false);
				setLoadBtnsOrange();
			}
		});

		controller.reset();
	}

	/**
	 * Sets the symbols for a successful or unsuccessful validation. The symbols
	 * are a red cross or a green checkmark.
	 * 
	 * @param type
	 *            int: [1] valid [2] invalid
	 */
	public void setValidtiySymbol(int type) {
		if (type == 1) {
			labelValiditySymbol.setImage(Activator.getImageDescriptor("icons/gruenerHakenKlein.png").createImage());
			labelValiditySymbol.setToolTipText(Messages.CrtVerViewComposite_validateSuccessful);
			labelValiditySymbol.setVisible(true);
		} else {
			labelValiditySymbol.setImage(Activator.getImageDescriptor("icons/rotesKreuzKlein.png").createImage());
			labelValiditySymbol.setToolTipText(Messages.CrtVerViewComposite_validateUnSuccessful);
			labelValiditySymbol.setVisible(true);
		}
	}

	/**
	 * This method paints the arrows used to indicate the validate date.
	 * 
	 * @param e
	 */
	@Override
	public void paintControl(PaintEvent e) {
		// Set the used color
		Rectangle clientArea;
		int width;
		int height;
		// Coordinates of the document icon
		GC gc;

		gc = e.gc;

		// Max position right are left are +/-180
		if (arrowSigDiff < -180) {
			arrowSigDiff = -180;
		} else if (arrowSigDiff > 180) {
			arrowSigDiff = 180;
		}
		if (arrowVerDiff < -180) {
			arrowVerDiff = -180;
		} else if (arrowVerDiff > 180) {
			arrowVerDiff = 178;
		}

		// Get the size of the canvas area
		clientArea = canvas1.getClientArea();
		width = clientArea.width;
		height = clientArea.height;

		// Draw Arrow Signature Date
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
		gc.fillRectangle(width / 2 + arrowSigDiff - 4, 9, 8, height);
		gc.fillPolygon(new int[] { (width / 2 - 8 + arrowSigDiff), 9,
				(width / 2 + arrowSigDiff), 0, (width / 2 + 8 + arrowSigDiff),
				9 });

		// Draw Arrow Verification Date
		gc.setBackground(violet);
		gc.fillRectangle(width / 2 + arrowVerDiff - 4, 9, 8, height - 4);
		gc.fillPolygon(new int[] { (width / 2 - 8 + arrowVerDiff), 11,
				(width / 2 + arrowVerDiff), 2, (width / 2 + 8 + arrowVerDiff),
				11 });

		gc.dispose();

	}

	/**
	 * Sets the font-color of the buttons btnLoadRootCa, btnLoadCa and
	 * btnLoadUserCert to orange. This happens when the scales are modified.
	 */
	public void setLoadBtnsOrange() {
		btnLoadRootCa.setForeground(orange);
		btnLoadCa.setForeground(orange);
		btnLoadUserCert.setForeground(orange);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
