//-----BEGIN DISCLAIMER-----
/*******************************************************************************
* Copyright (c) 2011, 2020 JCrypTool Team and Contributors
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
//-----END DISCLAIMER-----
package org.jcryptool.visual.secretsharing.views;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.jcryptool.core.util.colors.ColorService;
import org.jcryptool.core.util.fonts.FontService;
import org.jcryptool.visual.secretsharing.algorithm.Point;
import org.jcryptool.visual.secretsharing.algorithm.ShamirsSecretSharing;
import org.jcryptool.visual.secretsharing.views.Constants;

public class ShamirsSecretSharingComposite extends Composite {

	private Group groupSecretSharing;
	private Button resetButton;
	private Label reconstructPxLabel;
	private StyledText stValue;
	private Group groupShares;
	private ScrolledComposite scrolledShares;
	private Composite compositeShares;
	private Composite sharesButtonComposite;
	private Button selectAllButton;
	private Button deselectAllButton;
	private Group groupParameter;
	private Label numberOfConcernedLabel;
	private Spinner spnrN;
	private Label numberForReconstructionLabel;
	private Spinner spnrT;
	private Label modulLabel;
	private Text modulText;
	private VerifyListener numberOnlyVerifyListenerModul;
	private Label secretLabel;
	private Text secretText;
	private VerifyListener numberOnlyVerifyListenerSecret;
	private Label coefficentLabel;
	private Button selectCoefficientButton;
	private Label polynomLabel;
	private Label pxLabel;
	private StyledText stPolynom;
	private Button computeSharesButton;
	protected String[] result;
	protected BigInteger[] coefficients;
	protected BigInteger modul;
	protected BigInteger secret;
	protected String polynomialString = "";
	protected ShamirsSecretSharing shamirsSecretSharing;
	protected Point[] shares;
	protected Composite canvasCurve;
	private Text infoText;
	private StyledText stInfo;
	private Button[] sharesUseCheckButtonSet;
	private Button reconstructButton;
	private Vector<BigInteger[]> subpolynomial;
	private BigInteger[] reconstructedPolynomial;
	private Group groupReconstruction;
	protected Composite compositeReconstruction;
	private ScrolledComposite scrolledReconstruction;
	private Text shareYCoordinateModText;
	private Text sharesYCoordinateText;
	private Group groupCurve;
	private Composite sharePointInfo;
	private Label shareLabel;
	private Label openLabel;
	private Text xText;
	private Label seperatorLabel;
	private Text yText;
	private Label closeLabel;
	protected int mousePosX;
	protected int mousePosY;
	private int yAxisGap;
	private int xAxisGap;
	protected int pointValue;
	private int gridSizeY;
	private int gridSizeX;
	



	public ShamirsSecretSharingComposite(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new GridLayout(2, false));
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		createCompositeIntro(this);
		
		createGroupSecretSharing(this);

		createGroupCurve(this);
		
	}
	
	
	
    private void createGroupSecretSharing(Composite parent) {
        groupSecretSharing = new Group(parent, SWT.NONE);
        groupSecretSharing.setLayout(new GridLayout(2, true));
        groupSecretSharing.setText(Messages.ShamirsCompositeGraphical_title);
        GridData gd_groupSecretSharing = new GridData(SWT.FILL, SWT.FILL, false, true);
        groupSecretSharing.setLayoutData(gd_groupSecretSharing);

        createGroupParameter(groupSecretSharing);
        
        createGroupShares(groupSecretSharing);
        
        createGroupInfo(groupSecretSharing);
        
        createGroupReconstruction(groupSecretSharing);
        
        createCompositeReset(groupSecretSharing);
		
	}



	private void createCompositeReset(Composite parent) {
		
		Composite resetComposite = new Composite(parent, SWT.NONE);
		resetComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		GridLayout gl_resetComposite = new GridLayout(3, false);
		resetComposite.setLayout(gl_resetComposite);
		
		resetButton = new Button(resetComposite, SWT.NONE);
        resetButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(final SelectionEvent e) {
        		adjustButtonsForReset();
        		
        		coefficients = null;
        		shamirsSecretSharing = null;
        		shares = null;
        		result = null;
        		sharesUseCheckButtonSet = null;
        		subpolynomial = null;
        		reconstructedPolynomial = null;
        		
        		canvasCurve.setBackground(ColorService.WHITE);
        		canvasCurve.redraw();
        	}
        });
        resetButton.setText(Messages.SSSConstants_Reset);
        resetButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        
        reconstructPxLabel = new Label(resetComposite, SWT.NONE);
        reconstructPxLabel.setEnabled(false);
        reconstructPxLabel.setText(Constants.MESSAGE_RECONSTRUCTION);
        reconstructPxLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        stValue = new StyledText(resetComposite, SWT.READ_ONLY | SWT.BORDER);
        stValue.setEnabled(false);
        GridData gd_stValue = new GridData(SWT.FILL, SWT.CENTER, true, false);
        stValue.setLayoutData(gd_stValue);
		
	}



	/**
     * Created the info header. Plugin title + description.
     */
    private void createCompositeIntro(Composite parent) {
    	
        Composite compositeIntro = new Composite(parent, SWT.NONE);
        compositeIntro.setBackground(ColorService.WHITE);
        compositeIntro.setLayout(new GridLayout());
        GridData gd_compositeIntro = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
        gd_compositeIntro.widthHint = 1000;
        compositeIntro.setLayoutData(gd_compositeIntro);

        Label label = new Label(compositeIntro, SWT.NONE);
        label.setFont(FontService.getHeaderFont());
        label.setBackground(ColorService.WHITE);
        label.setText(Messages.ShamirsCompositeGraphical_title);

        Text stDescription = new Text(compositeIntro, SWT.READ_ONLY | SWT.WRAP);
        stDescription.setBackground(ColorService.WHITE);
        stDescription.setText(Messages.SSSConstants_Title_Info + " " 
        		+ Messages.SSSConstants_Title_Info_Formula + " "
        		+ Messages.lagrange_formular); //$NON-NLS-1$ //$NON-NLS-2$
        stDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    }
    
    
    /**
     * Creates the share group
     */
    private void createGroupShares(Composite parent) {
        groupShares = new Group(parent, SWT.NONE);
        groupShares.setLayout(new GridLayout());
        groupShares.setText("Shares");
        groupShares.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        scrolledShares = new ScrolledComposite(groupShares, SWT.V_SCROLL | SWT.BORDER);
        GridData gd_scrolledShares = new GridData(SWT.FILL, SWT.FILL, true, true);
        scrolledShares.setExpandHorizontal(true);
        gd_scrolledShares.heightHint = 110;
        scrolledShares.setLayoutData(gd_scrolledShares);

        compositeShares = new Composite(scrolledShares, SWT.NONE);
        compositeShares.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 10;
        compositeShares.setLayout(gridLayout);
        
        sharesButtonComposite = new Composite(groupShares, SWT.NONE);
        sharesButtonComposite.setLayout(new GridLayout(2, false));
        sharesButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        selectAllButton = new Button(sharesButtonComposite, SWT.NONE);
        selectAllButton.setEnabled(false);
        selectAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                for (int i = 0; i < sharesUseCheckButtonSet.length; i++) {
                    sharesUseCheckButtonSet[i].setSelection(true);
                }
                reconstructButton.setEnabled(true);
                canvasCurve.redraw();
            }
        });
        selectAllButton.setText(Messages.SSSConstants_Select_All_Button);
        selectAllButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        deselectAllButton = new Button(sharesButtonComposite, SWT.NONE);
        deselectAllButton.setEnabled(false);
        deselectAllButton.addSelectionListener(new SelectionAdapter() {
        	
            @Override
            public void widgetSelected(final SelectionEvent e) {
                for (int i = 0; i < sharesUseCheckButtonSet.length; i++) {
                    sharesUseCheckButtonSet[i].setSelection(false);
                }
                reconstructButton.setEnabled(false);
                canvasCurve.redraw();
            }
        });
        deselectAllButton.setText(Messages.SSSConstants_Deselect_All_Button);
        deselectAllButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        

        scrolledShares.setContent(compositeShares);
        compositeShares.pack();

    }
    
    /**
     * Creates the parameter group
     */
    private void createGroupParameter(Composite parent) {
        groupParameter = new Group(parent, SWT.NONE);
        groupParameter.setLayout(new GridLayout(6, false));
        final GridData gd_groupParameter = new GridData(SWT.FILL, SWT.FILL, false, false);
        groupParameter.setLayoutData(gd_groupParameter);
        groupParameter.setText(Messages.SSSConstants_Select_Parameter);

        numberOfConcernedLabel = new Label(groupParameter, SWT.NONE);
        numberOfConcernedLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
        numberOfConcernedLabel.setText(Messages.SSSConstants_Concerned_Persons);

        spnrN = new Spinner(groupParameter, SWT.BORDER);
        spnrN.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                spnrT.setMaximum(spnrN.getSelection());
                spnrT.setSelection(spnrT.getSelection());

            }
        });
        spnrN.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        spnrN.setMinimum(2);
        spnrN.setMaximum(500);
        spnrN.setSelection(4);

        numberForReconstructionLabel = new Label(groupParameter, SWT.NONE);
        numberForReconstructionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
        numberForReconstructionLabel.setText(Messages.SSSConstants_reconstruct_Person);

        spnrT = new Spinner(groupParameter, SWT.BORDER);
        spnrT.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (spnrT.getSelection() >= spnrN.getSelection()) {
                    spnrT.setMaximum(spnrT.getSelection() + 1);
                    spnrN.setSelection(spnrT.getSelection());
                }
            }

        });
        spnrT.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        spnrT.setMinimum(2);
        spnrT.setMaximum(3);
        spnrT.setSelection(3);

        modulLabel = new Label(groupParameter, SWT.NONE);
        modulLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
        modulLabel.setText(Messages.SSSConstants_Modul_info);
        
        ModifyListener modulAndSecretEmptyListener = new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				if (!secretText.getText().isEmpty() && !modulText.getText().isEmpty()) {
					selectCoefficientButton.setEnabled(true);
				} else {
					selectCoefficientButton.setEnabled(false);
				}
			}	
        };

        modulText = new Text(groupParameter, SWT.BORDER);
        modulText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        modulText.setText("23");
        numberOnlyVerifyListenerModul = new VerifyListener() {
            public void verifyText(VerifyEvent e) {
                /*
                 * keyCode == 8 is BACKSPACE and keyCode == 48 is ZERO and keyCode == 127 is DEL
                 */
                if (e.text.matches("[0-9]") || e.keyCode == 8 || e.keyCode == 127) { //$NON-NLS-1$
                    if (modulText.getText().length() == 0 && e.text.compareTo("0") == 0) { //$NON-NLS-1$
                        e.doit = false;
                    } else if (modulText.getSelection().x == 0 && e.keyCode == 48) {
                        e.doit = false;
                    } else {
                        e.doit = true;
                    }
                } else {
                    e.doit = false;
                }
            }
        };
        modulText.addVerifyListener(numberOnlyVerifyListenerModul);
        modulText.addModifyListener(modulAndSecretEmptyListener);

        secretLabel = new Label(groupParameter, SWT.NONE);
        secretLabel.setText(Messages.SSSConstants_Secret_Info);
        secretLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));

        secretText = new Text(groupParameter, SWT.BORDER);
        secretText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        secretText.setText("8");
        numberOnlyVerifyListenerSecret = new VerifyListener() {
            public void verifyText(VerifyEvent e) {
                /*
                 * keyCode == 8 is BACKSPACE and keyCode == 48 is ZERO and keyCode == 127 is DEL
                 */
                if (e.text.matches("[0-9]") || e.keyCode == 8 || e.keyCode == 127) { //$NON-NLS-1$
                    if (secretText.getText().length() == 0 && e.text.compareTo("0") == 0) { //$NON-NLS-1$
                        e.doit = false;
                    } else if (secretText.getSelection().x == 0 && e.keyCode == 48) {
                        e.doit = false;
                    } else {
                        e.doit = true;
                    }
                } else {
                    e.doit = false;
                }
            }
        };
        secretText.addVerifyListener(numberOnlyVerifyListenerSecret);
        secretText.addModifyListener(modulAndSecretEmptyListener);

        coefficentLabel = new Label(groupParameter, SWT.NONE);
        coefficentLabel.setText(Messages.SSSConstants_Coefficient);
        coefficentLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));

        selectCoefficientButton = new Button(groupParameter, SWT.NONE);
        selectCoefficientButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                int statusPrime = 0;
                int statusSecret = 0;

                result = new String[2];
                result[0] = modulText.getText();
                result[1] = secretText.getText();

                coefficients = new BigInteger[spnrT.getSelection()];

                String tmpModul = modulText.getText();
                String tmpSecret = secretText.getText();

                /*
                 * check if the input(modul and secret) isEmpty
                 */
                if (tmpModul.length() > 0 && tmpSecret.length() > 0) {
                    boolean isPrime = false;
                    modul = new BigInteger(tmpModul);
                    secret = new BigInteger(tmpSecret);
                    isPrime = modul.isProbablePrime(2000000);

                    if (modul.compareTo(new BigInteger(spnrT.getText())) >= 0) {
                        /*
                         * check if the modul is prime
                         */
                        if (!isPrime) {
                            PrimeDialog primeDialog = new PrimeDialog(getDisplay().getActiveShell(), modul, result);
                            statusPrime = primeDialog.open();

                            if (statusPrime == 0) {
                                modulText.removeVerifyListener(numberOnlyVerifyListenerModul);
                                modulText.setText(result[0]);
                                modul = new BigInteger(modulText.getText());
                                modulText.addVerifyListener(numberOnlyVerifyListenerModul);
                            }
                        }
                        /*
                         * check if the secret is smaller than the modul
                         */
                        if (secret.compareTo(modul) >= 0 && statusPrime != 1) {

                            SecretDialog secretDialog = new SecretDialog(getDisplay().getActiveShell(), secret, result);
                            statusSecret = secretDialog.open();
                            if (statusSecret == 0) {
                                secretText.removeVerifyListener(numberOnlyVerifyListenerSecret);
                                secretText.setText(result[1]);
                                secret = new BigInteger(secretText.getText());
                                secretText.addVerifyListener(numberOnlyVerifyListenerSecret);
                            }
                        }
                        /*
                         * if the precondition is correct and the input is not empty than select the coefficients
                         */
                        if (statusPrime == 0 && statusSecret == 0) {
                            CoefficientDialog cdialog = new CoefficientDialog(getDisplay().getActiveShell(), spnrT
                                    .getSelection(), secret, coefficients, modul);
                            int statusCoefficient = cdialog.open();
                            if (statusCoefficient == 0) {
                                /*
                                 * make a polynomial string
                                 */
                                polynomialString  = createPolynomialString(coefficients);

                                StyleRange stPolynomStyle = new StyleRange();
                                stPolynomStyle.start = 0;
                                stPolynomStyle.length = polynomialString.length();
                                stPolynomStyle.fontStyle = SWT.BOLD;
                                stPolynomStyle.foreground = Constants.BLUE;
                                stPolynom.setText(polynomialString);
                                stPolynom.setStyleRange(stPolynomStyle);

                                if (polynomialString.isEmpty()) {
                                    computeSharesButton.setEnabled(false);
                                } else {
                                    computeSharesButton.setEnabled(true);
                                }
                                modulText.setEnabled(false);
                                secretText.setEnabled(false);
                                spnrN.setEnabled(false);
                                spnrT.setEnabled(false);
                                spnrN.setEnabled(false);
                                spnrT.setEnabled(false);
                                selectCoefficientButton.setEnabled(false);
                            }
                        }
                    }
                }
            }
        });
        selectCoefficientButton.setText(Messages.SSSConstants_Select);
        selectCoefficientButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        selectCoefficientButton.setEnabled(true);

        polynomLabel = new Label(groupParameter, SWT.NONE);
        polynomLabel.setText(Messages.SSSConstants_Polynom_Info);
        polynomLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));

        pxLabel = new Label(groupParameter, SWT.NONE);
        pxLabel.setText("P(x):");
        pxLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        
        stPolynom = new StyledText(groupParameter, SWT.READ_ONLY | SWT.BORDER);
        stPolynom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));

        computeSharesButton = new Button(groupParameter, SWT.NONE);
        computeSharesButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                BigInteger n = new BigInteger(String.valueOf(spnrN.getSelection()));
                BigInteger t = new BigInteger(String.valueOf(spnrT.getSelection()));
                modul = new BigInteger(modulText.getText());
                shares = new Point[n.intValue()];
                for (int j = 0; j < shares.length; j++) {
                    shares[j] = new Point(new BigInteger(String.valueOf(j + 1)));
                }

                shamirsSecretSharing = new ShamirsSecretSharing(t, n, modul);
                shamirsSecretSharing.setCoefficient(coefficients);

                shares = shamirsSecretSharing.computeShares(shares);
                createShares(shares.length);

                computeSharesButton.setEnabled(false);
                selectAllButton.setEnabled(true);
                deselectAllButton.setEnabled(true);

                canvasCurve.redraw();

            }
        });
        computeSharesButton.setEnabled(false);
        computeSharesButton.setText(Messages.SSSConstants_Compute_Share_Button);
        computeSharesButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 6, 1));
        
    }
    
    /**
     * creates the shares
     *
     * @param n number of share rows
     */
    private void createShares(int n) {
        sharesUseCheckButtonSet = new Button[n];
        for (int i = 0; i < n; i++) {
            Label sharesPLabel = new Label(compositeShares, SWT.NONE);
            sharesPLabel.setText("Share " + (i + 1));
            sharesPLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            
            Label sharesEquivalentLabel = new Label(compositeShares, SWT.NONE);
            sharesEquivalentLabel.setText("=");
            sharesEquivalentLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            Label sharesOpenBracetLabel = new Label(compositeShares, SWT.NONE);
            sharesOpenBracetLabel.setText("(");
            sharesOpenBracetLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            Label sharesXCoordinateLabel = new Label(compositeShares, SWT.NONE);
            sharesXCoordinateLabel.setText(String.valueOf(i + 1));
            sharesXCoordinateLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            Label sharesSeperatorLabel = new Label(compositeShares, SWT.NONE);
            sharesSeperatorLabel.setText("|");
            sharesSeperatorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            shareYCoordinateModText = new Text(compositeShares, SWT.READ_ONLY | SWT.BORDER);
            shareYCoordinateModText.setText(shares[i].getY().mod(modul).toString());
            GridData gd_sharesYCoordinateModText = new GridData(SWT.FILL, SWT.CENTER, true, false);
            gd_sharesYCoordinateModText.widthHint = 50;
            shareYCoordinateModText.setLayoutData(gd_sharesYCoordinateModText);

            Label shareCongruenceLabel = new Label(compositeShares, SWT.NONE);
            shareCongruenceLabel.setText(Constants.uCongruence);
            shareCongruenceLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            sharesYCoordinateText = new Text(compositeShares, SWT.READ_ONLY | SWT.BORDER);
            sharesYCoordinateText.setText(shares[i].getY().toString());
            GridData gd_sharesYCoordinateText = new GridData(SWT.FILL, SWT.CENTER, true, false);
            gd_sharesYCoordinateText.widthHint = 70;
            sharesYCoordinateText.setLayoutData(gd_sharesYCoordinateText);

            Label sharesCloseBarcetLabel = new Label(compositeShares, SWT.NONE);
            sharesCloseBarcetLabel.setText(")");
            sharesCloseBarcetLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            Button sharesUseCheckButton = new Button(compositeShares, SWT.CHECK);
            sharesUseCheckButton.setLayoutData(new GridData(SWT.NONE, SWT.CENTER, false, false));
            sharesUseCheckButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent e) {
                    int checkButtonCounter = 0;
                    for (int j = 0; j < sharesUseCheckButtonSet.length; j++) {
                        if (sharesUseCheckButtonSet[j].getSelection()) {
                            checkButtonCounter++;
                        }
                    }

                    if (checkButtonCounter >= 2) {
                        reconstructButton.setEnabled(true);
                    } else {
                        reconstructButton.setEnabled(false);
                    }
                    canvasCurve.redraw();
                }
            });
            sharesUseCheckButtonSet[i] = sharesUseCheckButton;
        }
        compositeShares.pack();
    }
    


    
	/**
	 * Creates the info group
	 */
	private void createGroupInfo(Composite parent) {
		Group infoGroup = new Group(parent, SWT.NONE);
//		infoGroup.setEnabled(false);
		infoGroup.setText(Messages.SSSConstants_Info_Group);
		final GridData gd_infoGroup = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_infoGroup.widthHint = 250;
		infoGroup.setLayoutData(gd_infoGroup);
		infoGroup.setLayout(new GridLayout());

		infoText = new Text(infoGroup, SWT.WRAP | SWT.READ_ONLY | SWT.MULTI | SWT.BORDER);
		infoText.setEnabled(false);
		infoText.setEditable(false);
		final GridData gd_infoText = new GridData(SWT.FILL, SWT.FILL, true, false);
		infoText.setLayoutData(gd_infoText);
		infoText.setText(Messages.Reconstruct_Info + "\n" + Constants.LAGRANGE_FORMULAR + "\n" + Constants.LAGRANGE_FORMULAR_RANGE); //$NON-NLS-1$ //$NON-NLS-2$

		stInfo = new StyledText(infoGroup, SWT.WRAP | SWT.BORDER);
		stInfo.setEditable(false);
//		stInfo.setEnabled(false);
		final GridData gd_stInfo = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_stInfo.heightHint = 80;
		stInfo.setLayoutData(gd_stInfo);
	}
	
    /**
     * Creates the reconstruction group
     */
    private void createGroupReconstruction(Composite parent) {
        groupReconstruction = new Group(parent, SWT.NONE);
        groupReconstruction.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        groupReconstruction.setLayout(new GridLayout());
        groupReconstruction.setText(Messages.SSSConstants_Reconstruction_Group);
        
        reconstructButton = new Button(groupReconstruction, SWT.NONE);
        reconstructButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                /*
                 * clear the composite for the next visualization
                 */
                Control[] tmpWidgets = compositeReconstruction.getChildren();
                for (int i = 0; i < tmpWidgets.length; i++) {
                    tmpWidgets[i].dispose();
                }
                compositeReconstruction.pack();

                Vector<Point> tmpPointSet = new Vector<Point>();

                for (int i = 0; i < sharesUseCheckButtonSet.length; i++) {
                    if (sharesUseCheckButtonSet[i].getSelection()) {
                        tmpPointSet.add(shares[i]);
                    }
                }
                Point[] pointSet = new Point[tmpPointSet.size()];
                for (int i = 0; i < tmpPointSet.size(); i++) {
                    pointSet[i] = tmpPointSet.get(i);
                }

                reconstructedPolynomial = shamirsSecretSharing.interpolatePoints(pointSet, modul);

                subpolynomial = shamirsSecretSharing.getSubPolynomialNumerical();

                createReconstruction(subpolynomial.size());

                String tmpPolynomial = createPolynomialString(reconstructedPolynomial);
                if (tmpPolynomial.charAt(0) == '0' && tmpPolynomial.length() > 1) {
                    tmpPolynomial = tmpPolynomial.substring(4);
                }
                stValue.setText(tmpPolynomial);

                StyleRange styleValue = new StyleRange();
                StyleRange styleInfo = new StyleRange();
                styleValue.start = 0;
                styleInfo.start = 0;
                styleValue.length = stValue.getText().length();

                if (comparePolynomial(reconstructedPolynomial, coefficients)) {
                    styleValue.foreground = Constants.GREEN;

                    stInfo.setForeground(Constants.BLACK);
                    stInfo.setText(MessageFormat.format(Messages.SSSConstants_Polynom_Equal, secretText.getText()));
                    stInfo.setBackground(Constants.GREEN);
                } else {
                    styleValue.foreground = Constants.RED;

                    // Count how many checkboxes are selected.
                    int counterSelectedShares = 0;
                    for (Button btn : sharesUseCheckButtonSet) {
                    	counterSelectedShares = btn.getSelection() ? counterSelectedShares + 1 : counterSelectedShares;
                    }
                    stInfo.setText(MessageFormat.format(Messages.SSSConstants_Polynom_Not_Equal, counterSelectedShares, spnrT.getSelection()));
                    stInfo.setBackground(Constants.RED);
                }
                styleInfo.length = stInfo.getText().length();
                styleInfo.fontStyle = SWT.BOLD;
                styleValue.fontStyle = SWT.BOLD;
                stInfo.setStyleRange(styleInfo);
                stValue.setStyleRange(styleValue);

                reconstructPxLabel.setEnabled(true);

                canvasCurve.redraw();

            }
        });
        reconstructButton.setEnabled(false);
        reconstructButton.setText(Messages.SSSConstants_Reconstrct);
        reconstructButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        scrolledReconstruction = new ScrolledComposite(groupReconstruction, SWT.V_SCROLL | SWT.BORDER);
        scrolledReconstruction.setExpandHorizontal(true);
        final GridData gd_scrolledReconstruction = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_scrolledReconstruction.heightHint = 109;
        scrolledReconstruction.setLayoutData(gd_scrolledReconstruction);

        compositeReconstruction = new Composite(scrolledReconstruction, SWT.NONE);
        compositeReconstruction.setLocation(0, 0);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        compositeReconstruction.setLayout(gridLayout);

        /*
         * BEGIN dummy for design only
         */
        // final Label w_1Label = new Label(compositeReconstruction, SWT.NONE);
        // w_1Label.setText("w_1");
        //
        // final Label label = new Label(compositeReconstruction, SWT.NONE);
        // label.setText("=");
        //
        // text_1 = new Text(compositeReconstruction, SWT.BORDER);
        // text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        // false));
        // compositeReconstruction.setSize(253, 80);
        /*
         * END dummy
         */

        scrolledReconstruction.setContent(compositeReconstruction);
        compositeReconstruction.pack();
    }
    
    /**
     * Creates the subpolynomial
     *
     * @param n the number of subpolynomial rows
     */
    private void createReconstruction(int n) {
        for (int i = 0; i < n; i++) {
            Label reconstructionLabel = new Label(compositeReconstruction, SWT.NONE);
            reconstructionLabel.setText("w" + convertToSubset(i));

            Label reconstructEquivalentLabel = new Label(compositeReconstruction, SWT.NONE);
            reconstructEquivalentLabel.setText("=");

            Text reconstructPolynomial = new Text(compositeReconstruction, SWT.READ_ONLY | SWT.BORDER);

            String polynomString = createPolynomialString(subpolynomial.get(i));
            if (polynomString.charAt(0) == '0' && polynomString.length() > 1) {
                reconstructPolynomial.setText(polynomString.substring(4));
            } else {
                reconstructPolynomial.setText(polynomString);
            }
            GridData gd_reconstructPolynomial = new GridData(SWT.FILL, SWT.CENTER, true, false);
            reconstructPolynomial.setLayoutData(gd_reconstructPolynomial);
        }
        compositeReconstruction.pack();
    }
    
    /**
     * converts an array containing coefficients to a polynomial string
     *
     * @param coefficients
     * @return a string representation of a polynomial
     */
    private String createPolynomialString(BigInteger[] coefficients) {
        String result = ""; //$NON-NLS-1$

        for (int i = 0; i < coefficients.length; i++) {
            if (i == 0) {
                result = coefficients[i].toString() + " "; //$NON-NLS-1$
            } else {
                BigInteger bi = coefficients[i];
                if (bi.compareTo(BigInteger.ZERO) != 0) {
                    if (bi.compareTo(BigInteger.ZERO) < 0) {
                        if (bi.compareTo(Constants.MINUS_ONE) == 0) {
                            result += "-x" + convertToSuperscript(i) + " "; //$NON-NLS-1$ //$NON-NLS-2$
                        } else {
                            result += coefficients[i] + "x" + convertToSuperscript(i) + " "; //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    } else {
                        if (bi.compareTo(BigInteger.ONE) == 0) {
                            result += "+ " + "x" + convertToSuperscript(i) + " "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        } else {
                            result += "+ " + coefficients[i] + "x" + convertToSuperscript(i) + " "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        }
                    }
                }
            }
        }
        result = result.trim();

        return result;
    }
    
    /**
     * compares two BigInterger array equality
     *
     * @param a a BigInteger array
     * @param b a BigInteger array
     * @return true if the arrays are equal otherwise false
     */
    private boolean comparePolynomial(BigInteger[] a, BigInteger[] b) {
        boolean result = true;
        int n;
        if (a.length < b.length) {
            n = a.length;
        } else {
            n = b.length;
        }

        for (int i = 0; i < n; i++) {
            if (!a[i].mod(modul).equals(b[i].mod(modul))) {
                result = false;
            }
        }
        return result;
    }
    
    /**
     * Convert a number to a superscript index
     *
     * @param id is the number to be converted
     * @return a string which contains only superscript
     */
    private String convertToSuperscript(int id) {
        char[] data = String.valueOf(id).toCharArray();
        StringBuilder result = new StringBuilder();

        if (id == 0 || id == 1)
            return ""; //$NON-NLS-1$

        for (int i = 0; i < data.length; i++) {
            if (data[i] == '2')
                result.append(Constants.sTwo);
            else if (data[i] == '3')
                result.append(Constants.sThree);
            else if (data[i] == '4')
                result.append(Constants.sFour);
            else if (data[i] == '5')
                result.append(Constants.sFive);
            else if (data[i] == '6')
                result.append(Constants.sSix);
            else if (data[i] == '7')
                result.append(Constants.sSeven);
            else if (data[i] == '8')
                result.append(Constants.sEight);
            else if (data[i] == '9')
                result.append(Constants.sNine);
        }

        return result.toString();
    }
    
    /**
     * Converts the id value to subscript
     *
     * @param id
     * @return a subscript converted string
     */
    private String convertToSubset(int id) {
        char[] data = String.valueOf(id).toCharArray();
        String result = ""; //$NON-NLS-1$

        for (int i = 0; i < data.length; i++) {
            if (data[i] == '0')
                result += Constants.uZero;

            if (data[i] == '1')
                result += Constants.uOne;

            if (data[i] == '2')
                result += Constants.uTwo;

            if (data[i] == '3')
                result += Constants.uThree;

            if (data[i] == '4')
                result += Constants.uFour;

            if (data[i] == '5')
                result += Constants.uFive;

            if (data[i] == '6')
                result += Constants.uSix;

            if (data[i] == '7')
                result += Constants.uSeven;

            if (data[i] == '8')
                result += Constants.uEight;

            if (data[i] == '9')
                result += Constants.uNine;
        }

        return result;
    }
    
    /**
     * reset the control elements
     */
    public void adjustButtonsForReset() {
        spnrN.setEnabled(true);
//        spnrN.setSelection(4);
//        spnrN.setMinimum(2);
//        spnrN.setMaximum(500);
        spnrT.setEnabled(true);
//        spnrT.setSelection(3);
//        spnrT.setMinimum(2);
//        spnrT.setMaximum(3);
        computeSharesButton.setEnabled(false);
//        secret = null;
//        modul = null;
//        secretText.removeVerifyListener(numberOnlyVerifyListenerSecret);
//        secretText.setText("8"); //$NON-NLS-1$
        secretText.setEnabled(true);
        secretText.addVerifyListener(numberOnlyVerifyListenerSecret);
//        modulText.removeVerifyListener(numberOnlyVerifyListenerModul);
//        modulText.setText("23"); //$NON-NLS-1$
        modulText.setEnabled(true);
        modulText.addVerifyListener(numberOnlyVerifyListenerModul);
        stPolynom.setText(""); //$NON-NLS-1$
        polynomialString = ""; //$NON-NLS-1$
        stPolynom.setEnabled(true);
        selectCoefficientButton.setEnabled(true);
        computeSharesButton.setEnabled(false);
        reconstructButton.setEnabled(false);
        selectAllButton.setEnabled(false);
        deselectAllButton.setEnabled(false);

        Control[] tmpWidgets = compositeShares.getChildren();
        for (int i = 0; i < tmpWidgets.length; i++) {
            tmpWidgets[i].dispose();
        }
        compositeShares.pack();

        tmpWidgets = compositeReconstruction.getChildren();
        for (int i = 0; i < tmpWidgets.length; i++) {
            tmpWidgets[i].dispose();
        }
        compositeReconstruction.pack();

        stValue.setText(""); //$NON-NLS-1$
        stInfo.setText(""); //$NON-NLS-1$

        stInfo.setBackground(Constants.WHITE);

        reconstructPxLabel.setEnabled(false);
    }
    
    private void createGroupCurve(Composite parent) {
        groupCurve = new Group(parent, SWT.NONE);
        groupCurve.setLayout(new GridLayout(11, false));
        groupCurve.setText("Graph");
        final GridData gd_groupCurve = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_groupCurve.heightHint = 558;
        groupCurve.setLayoutData(gd_groupCurve);

        createCanvasCurve();
        
        sharePointInfo = new Composite(groupCurve, SWT.NONE);
        sharePointInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 11, 1));
        sharePointInfo.setLayout(new GridLayout(8, false));
        
        final Label dummyLabel = new Label(sharePointInfo, SWT.NONE);
        dummyLabel.setText("dummy"); //$NON-NLS-1$
        dummyLabel.setVisible(false);

        shareLabel = new Label(sharePointInfo, SWT.NONE);
        shareLabel.setText("Share"); //$NON-NLS-1$
        shareLabel.setVisible(false);

        openLabel = new Label(sharePointInfo, SWT.NONE);
        openLabel.setText("("); //$NON-NLS-1$
        openLabel.setVisible(false);

        xText = new Text(sharePointInfo, SWT.READ_ONLY | SWT.BORDER);
        final GridData gd_xText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd_xText.heightHint = 20;
        xText.setLayoutData(gd_xText);
        xText.setVisible(false);

        seperatorLabel = new Label(sharePointInfo, SWT.NONE);
        seperatorLabel.setText("|"); //$NON-NLS-1$
        seperatorLabel.setVisible(false);

        yText = new Text(sharePointInfo, SWT.READ_ONLY | SWT.BORDER);
        final GridData gd_yText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd_yText.heightHint = 20;
        yText.setLayoutData(gd_yText);
        yText.setVisible(false);

        closeLabel = new Label(sharePointInfo, SWT.NONE);
        closeLabel.setText(")"); //$NON-NLS-1$
        closeLabel.setVisible(false);

        final Label phLabel = new Label(sharePointInfo, SWT.NONE);
        phLabel.setText("ph"); //$NON-NLS-1$
        phLabel.setVisible(false);

    }
    
    /**
     * Creates the canvas group
     */
    private void createCanvasCurve() {
        canvasCurve = new Composite(groupCurve, SWT.EMBEDDED);
        canvasCurve.addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(final MouseEvent e) {
                mousePosX = e.x;
                mousePosY = e.y;

                if (shares != null) {
                    Point point = nearSharePoint(shares, mousePosX, mousePosY);

                    if (point != null) {
                        xText.setText(point.getX().toString());
                        yText.setText(point.getY().toString());
                        makePointVisible(true);
                    } else if (pointValue == Integer.MAX_VALUE) {
                        boolean found = false;
                        for (int i = 0; i < shares.length; i++) {
                            int tmpX = shares[i].getX().intValue() * 60 + xAxisGap;
                            int tmpY = 408 + 1 * 6;

                            if ((tmpX - 3 == mousePosX || tmpX - 2 == mousePosX || tmpX - 1 == mousePosX
                                    || tmpX == mousePosX || tmpX + 1 == mousePosX || tmpX + 2 == mousePosX || tmpX + 3 == mousePosX)
                                    && (tmpY - 3 == mousePosY || tmpY - 2 == mousePosY || tmpY - 1 == mousePosY
                                            || tmpY == mousePosY || tmpY + 1 == mousePosY || tmpY + 2 == mousePosY || tmpY + 3 == mousePosY)) {
                                xText.setText(shares[i].getX().toString());
                                yText.setText(shares[i].getY().toString());
                                found = true;
                            }
                        }
                        if (found) {
                            makePointVisible(true);
                        } else {
                            makePointVisible(false);
                        }
                    } else {
                        makePointVisible(false);
                    }
                }
            }
        });
        canvasCurve.setBackground(Constants.WHITE);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 11, 1);
        gridData.widthHint = 506;
        canvasCurve.setLayoutData(gridData);

        canvasCurve.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
            	if (!polynomialString.isEmpty()) {
                    drawPolynomial(e);
                }
            }
        });
    }
    
    private Point nearSharePoint(Point[] sharePoints, int x, int y) {
        Point point = null;
        for (int i = 0; i < sharePoints.length; i++) {
            int tmpX = sharePoints[i].getX().intValue() * 60 + xAxisGap;
            int tmpY = yAxisGap - sharePoints[i].getY().intValue() * 6;

            if ((tmpX - 3 == x || tmpX - 2 == x || tmpX - 1 == x || tmpX == x || tmpX + 1 == x || tmpX + 2 == x || tmpX + 3 == x)
                    && (tmpY - 3 == y || tmpY - 2 == y || tmpY - 1 == y || tmpY == y || tmpY + 1 == y || tmpY + 2 == y || tmpY + 3 == y)) {
                point = sharePoints[i];
                return point;
            }
        }
        return point;
    }
    
    private void makePointVisible(boolean visible) {
        shareLabel.setVisible(visible);
        openLabel.setVisible(visible);
        xText.setVisible(visible);
        seperatorLabel.setVisible(visible);
        yText.setVisible(visible);
        closeLabel.setVisible(visible);
    }
    
    
    private int getMaxXCoord() {
    	int tmp = 0;
    	for (Point p : shares) {
    		if (p.getY().intValue() > tmp) {
    			tmp = p.getY().intValue();
    		}
    	}
    	
    	return tmp;
    }
    
    /**
     * @param e the PaintEvent that represents the graphic context
     */
    private void drawPolynomial(PaintEvent e) {
        GC gc = e.gc;
        //canvasCurve.setSize(getMaxXCoord(), getMaxYCoord());
        
        org.eclipse.swt.graphics.Point size = canvasCurve.getSize();
        
        int maxX = getMaxXCoord();
        
        gridSizeY = 1;
        gridSizeX = 60;
        
        if (maxX <= 50) {
        	gridSizeY = 7;
            gridSizeX = 60;
        } else if (maxX <= 100) {
        	gridSizeY = 6;
            gridSizeX = 60;
        } else if (maxX <= 150) {
        	gridSizeY = 4;
        	gridSizeX = 60;
        } else if (maxX <= 220) {
        	gridSizeY = 2;
        	gridSizeX = 60;
        }

        gc.setForeground(Constants.LIGHTGREY);
        
        /*
         * draw the grid in x direction
         */
        xAxisGap = 0;
        for (int i = 0; i < size.x; i += gridSizeX) {
            if (xAxisGap + gridSizeX <= size.x / 2) {
                xAxisGap += gridSizeX;
            }
            xAxisGap = gridSizeX;
            
            //x axis lines
            gc.drawLine(i, 0, i, size.y); 
        }

        /*
         * draw the grid in y direction
         */
        yAxisGap = 0;
        for (int i = 0; i < size.y; i += gridSizeY) {
            if (yAxisGap + gridSizeY <= size.y / 2) {
                yAxisGap += gridSizeY;
            }
            yAxisGap = gridSizeY * ((size.y / gridSizeY)) - 20;
            
            if (maxX <= 150) {
                gc.drawLine(0, i, size.x, i);

            } else if (i % (gridSizeY * 5) == 0) {
                gc.drawLine(0, i, size.x, i);
            }
            
        }
        
        int labeljumps = 1;
        int gapSmall = 3;
        int gapBig = 7;
        int textOffset = gapBig + 8;
        int fontWidth = 6;
        int fontHeight = 10;
        int numberLength = 0;
        
        /*
         * draw the axis
         */
        gc.setForeground(Constants.BLACK);

        gc.drawLine(xAxisGap, 0, xAxisGap, size.y);
        gc.drawLine(0, yAxisGap, size.x, yAxisGap);

        /*
         * draw the x marker
         */
        int i = 0;
        for (int x = xAxisGap; x < size.x; x += gridSizeX) {
            i++;
            numberLength = String.valueOf(i).length();
            /*
             * thin lines
             */
            gc.drawLine(x, yAxisGap - gapSmall, x, yAxisGap + gapSmall);
            gc.drawLine(xAxisGap - i * gridSizeX, yAxisGap - gapSmall, xAxisGap - i * gridSizeX, yAxisGap + gapSmall);
            /*
             * thick lines
             */
            if ((i - 1) % labeljumps == 0) {
                gc.drawLine(x, yAxisGap - gapBig, x, yAxisGap + gapBig);
                gc.drawLine(xAxisGap - i * labeljumps * gridSizeX, yAxisGap - gapBig, xAxisGap - i * labeljumps
                        * gridSizeX, yAxisGap + gapBig);
                if (i != 1) {
                    gc.drawText(String.valueOf(i - 1), x - (fontWidth * numberLength) / 2, yAxisGap - gapBig
                            + textOffset, true);
                    gc.drawText(String.valueOf(i - 1), xAxisGap - i * labeljumps * gridSizeX, yAxisGap + gapBig, true);
                }
            }
        }
        
        //determine label jump
        if (maxX <= 150){
        	labeljumps = 5;
        } else if (maxX <= 250) {
        	labeljumps = 10;
        } else {
        	labeljumps = 20;
        }

        /*
         * draw the y markers
         */
        i = 0;
        for (int y = yAxisGap; y >= 0; y -= gridSizeY) {
            i++;
            numberLength = String.valueOf(i).length();
            /*
             * thin lines
             */
            if (maxX <= 150) {
            	gc.drawLine(xAxisGap - gapSmall, y, xAxisGap + gapSmall, y);
                gc.drawLine(xAxisGap - gapSmall, yAxisGap + i * gridSizeY, xAxisGap + gapSmall, yAxisGap + i * gridSizeY);
            }
           
            /*
             * thick lines
             */
            if ((i - 1) % labeljumps == 0) {
                gc.drawLine(xAxisGap - gapBig, y, xAxisGap + gapBig, y);
                gc.drawLine(xAxisGap - gapBig, yAxisGap + i * labeljumps * gridSizeY, xAxisGap + gapBig, yAxisGap + i
                        * labeljumps * gridSizeY);
                if (i != 1) {
                    gc.drawText(String.valueOf(i - 1), xAxisGap - gapBig - (fontWidth * numberLength) - 3, y
                            - fontHeight / 2 - 4, true);
                }
            }
        }
        gc.drawText(String.valueOf(-5), xAxisGap - gapBig - 2 - (fontWidth * numberLength), yAxisGap + 5 * gridSizeY
                - 8, true);

        /*
         * new GraphicContent for drawing the polynomial curve
         */
        GC polynomial = new GC(canvasCurve);
        Path polynomPath = new Path(null);
        float dx = 2.0f / gridSizeY;
        polynomPath.moveTo(-10, valueAt(-10));

        for (float x = -10.0f; x < size.x / 2; x += dx) {
            polynomPath.lineTo(x, valueAt(x));

        }
        polynomial.setForeground(Constants.BLUE);

        Transform polynomTransform = new Transform(null);
        polynomTransform.translate(xAxisGap, yAxisGap);
        polynomTransform.scale(gridSizeX, -gridSizeY);
        polynomial.setTransform(polynomTransform);

        polynomial.drawPath(polynomPath);

        /*
         * new GraphicContent for drawing the reconstructed polynomial curve
         */
        if (stValue.getText().length() > 0) {
            GC subPolynomial = new GC(canvasCurve);
            Transform subPolynomialTransform = new Transform(null);
            subPolynomialTransform.translate(xAxisGap, yAxisGap);
            subPolynomialTransform.scale(gridSizeX, -gridSizeY);

            Path subPolynomialPath = new Path(null);
            subPolynomialPath.moveTo(-10, valueAt(-10));
            for (float x = -10.0f; x < size.x / 2; x += dx) {
                subPolynomialPath.lineTo(x, valueAtReconstruction(x));
            }

            if (comparePolynomial(coefficients, reconstructedPolynomial)) {
                subPolynomial.setForeground(Constants.GREEN);
            } else {
                subPolynomial.setForeground(Constants.RED);
            }
            subPolynomial.setTransform(polynomTransform);
            subPolynomial.drawPath(subPolynomialPath);
        }

        /*
         * new GraphicContent for drawing shares points
         */
        GC points = new GC(canvasCurve);
        Transform pointTransform = new Transform(null);
        pointTransform.translate(xAxisGap, yAxisGap);
        points.setTransform(pointTransform);

        pointValue = (int) valueAt(shares.length);
        for (int k = 1; k <= shares.length; k++) {
            if (sharesUseCheckButtonSet[k - 1].getSelection()) {
                points.setBackground(Constants.RED);
            } else {
                points.setBackground(Constants.DARKPURPLE);
            }
            if (pointValue == Integer.MAX_VALUE) {
                points.fillOval(gridSizeX * k - 3, (pointValue) * -gridSizeY - 3, 6, 6);
            } else {
                points.fillOval(gridSizeX * k - 3, ((int) valueAt(k)) * -gridSizeY - 3, 6, 6);
            }
        }
    }
    
    /**
     * compute the y value for a given x value for the original polynomial
     *
     * @param x is the point to evaluate
     * @return the corresponding y value
     */
    private float valueAt(float x) {
        float value = 0;
        for (int i = 0; i < coefficients.length; i++) {
            value += coefficients[i].intValue() * Math.pow(x, i);
        }
        return value;
    }
    
    /**
     * compute the y value for a given x value for the reconstructed polynomial
     *
     * @param x is the point to evaluate
     * @return the corresponding y value
     */
    private float valueAtReconstruction(float x) {
        float value = 0;
        for (int i = 0; i < reconstructedPolynomial.length; i++) {
            value += reconstructedPolynomial[i].intValue() * Math.pow(x, i);
        }
        return value;
    }

}
