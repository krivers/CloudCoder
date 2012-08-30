// CloudCoder - a web-based pedagogical programming environment
// Copyright (C) 2011-2012, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011-2012, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.cloudcoder.app.client.view;

import org.cloudcoder.app.client.rpc.RPC;
import org.cloudcoder.app.shared.model.ICallback;
import org.cloudcoder.app.shared.model.OperationResult;
import org.cloudcoder.app.shared.model.ProblemAndTestCaseList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Dialog for sharing an exercise to the exercise repository.
 * 
 * @author David Hovemeyer
 */
public class ShareProblemDialog extends DialogBox {
	private Label licenseNameLabel;
	private Label licenseUrlLabel;
	private Button shareButton;
	private Button cancelButton;
	private TextBox repoUsernameTextBox;
	private PasswordTextBox repoPasswordTextBox;
	private Label errorLabel;
	
	private ProblemAndTestCaseList exercise;
	private ICallback<OperationResult> resultCallback;
	
	public ShareProblemDialog() {
		setTitle("Share problem");
		setGlassEnabled(true);
		
		FlowPanel panel = new FlowPanel();
		
		HTML html = new HTML("Enter your exercise repository username and password. " +
				"Then, click Share to upload this exercise to the exercise repository.");
		html.setWidth("480px");
		panel.add(html);
		panel.add(new HTML("<br />"));
		
		// Labels for license name and URL
		this.licenseNameLabel = new Label("");
		this.licenseUrlLabel = new Label("");
		panel.add(licenseNameLabel);
		panel.add(licenseUrlLabel);
		panel.add(new HTML("<br />"));
		
		// UI for entering repository username and password
		panel.add(new InlineLabel("Repository username:"));
		this.repoUsernameTextBox = new TextBox();
		panel.add(repoUsernameTextBox);
		panel.add(new HTML("<br />"));
		
		panel.add(new InlineLabel("Repository password:"));
		this.repoPasswordTextBox = new PasswordTextBox();
		panel.add(repoPasswordTextBox);
		panel.add(new HTML("<br />"));
		
		this.errorLabel = new Label("");
		panel.add(errorLabel);
		
		this.shareButton = new Button("Share!");
		this.shareButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onClickShare();
			}
		});
		this.cancelButton = new Button("Cancel");
		this.cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		panel.add(shareButton);
		panel.add(cancelButton);
		
		add(panel);
	}
	
	/**
	 * @param exercise the exercise to set
	 */
	public void setExercise(ProblemAndTestCaseList exercise) {
		this.exercise = exercise;
		licenseNameLabel.setText(exercise.getProblem().getLicense().getName());
		licenseUrlLabel.setText(exercise.getProblem().getLicense().getUrl());
	}

	/**
	 * Set the result callback that will receive the {@link OperationResult}
	 * from attempting to share the exercise to the repository.
	 * 
	 * @param resultCallback the result callback
	 */
	public void setResultCallback(ICallback<OperationResult> resultCallback) {
		this.resultCallback = resultCallback;
	}

	protected void onClickShare() {
		String repoUsername = repoUsernameTextBox.getText();
		String repoPassword = repoPasswordTextBox.getText();
		
		// Make sure that the repository username and password were entered correctly.
		if (repoUsername.equals("") || repoPassword.equals("")) {
			errorLabel.setText("Please enter your repository username and password");
			return;
		}
		
		RPC.getCoursesAndProblemsService.submitExercise(exercise, repoUsername, repoPassword, new AsyncCallback<OperationResult>() {
			@Override
			public void onSuccess(OperationResult result_) {
				resultCallback.call(result_);
				hide();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				resultCallback.call(new OperationResult(false, caught.getMessage()));
				hide();
			}
		});
	}
}
