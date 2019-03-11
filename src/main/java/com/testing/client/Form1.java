/**
 * File Name : Form1.java
 * Package   : com.testing.client
 * Author    : RIENGCS
 * Created   : Sep 22, 2014

 * This source code is part of the  gwt-form-demo, and is copyrighted by JWT. 
 * All rights reserved.  No part of this work may be reproduced, stored in a retrieval system,
 * adopted or transmitted in any form or by any means,electronic, mechanical, photographic,
 * graphic, optic recording or otherwise,translated in any language or computer language,
 * without the prior written permission of JWT 

 Copyright © 2014 - 2014 by JWT
 ----------------------------------------------------------------------------------------------
 * Version Control:
 *       $LastChangedRevision$
 *       $LastChangedBy$
 *       $LastChangedDate$
 *
 ----------------------------------------------------------------------------------------------
 **/
package com.testing.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.testing.base.FormBase;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * <b>Author     :</b> RIENGCS </br>
 * <b>Created    :</b> Sep 22, 2014 </br>
 * <b>Description:</b>
 **/
public class Form1 extends FormBase{

	private TextField name;
	private TextField email;
	private TextField phone;
	private TextField address;

	private TextButton doSave;
	private TextButton doComplete;
	private TextButton doClose;

	@Override
	public VerticalLayoutContainer createForm(){
		VerticalLayoutContainer vlContainer = new VerticalLayoutContainer();

		FramedPanel form = new FramedPanel();
		form.setHeadingText("Form1");
		form.setWidth(400);
		form.setHeight(300);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		name = new TextField();
		email = new TextField();
		phone = new TextField();
		address = new TextField();
		p.add(new FieldLabel(name, "Name"), new VerticalLayoutData(1, -1));
		p.add(new FieldLabel(email, "Email"), new VerticalLayoutData(1, -1));
		p.add(new FieldLabel(phone, "Phone"), new VerticalLayoutData(1, -1));
		p.add(new FieldLabel(address, "Address"), new VerticalLayoutData(1, -1));
		form.add(p);

		doSave = new TextButton("Save");
		doSave.addSelectHandler(doSaveSH);
		doComplete = new TextButton("Complete");
		doComplete.addSelectHandler(doCompleteSH);
		doClose = new TextButton("Close");
		doClose.addSelectHandler(doCloseSH);
		form.addButton(doSave);
		form.addButton(doComplete);
		form.addButton(doClose);

		vlContainer.add(form);
		return vlContainer;
	}

	@Override
	protected void getDatafromParent(Map<String, Object> data, boolean readOnly){
		//Info.display("Form load data", "Form init load data ...!!!");
		this.name.setValue(String.valueOf((data.get("name") != null ? data.get("name") : "")));
		this.email.setValue(String.valueOf((data.get("email") != null ? data.get("email") : "")));
		this.phone.setValue(String.valueOf((data.get("phone") != null ? data.get("phone") : "")));
		this.address.setValue(String.valueOf((data.get("address") != null ? data.get("address") : "")));
		this.birthday.setValue((Date) (data.get("birthday") != null ? data.get("birthday") : new Date()));
		
		this.name.setReadOnly(readOnly);
		this.email.setReadOnly(readOnly);
		this.phone.setReadOnly(readOnly);
		this.address.setReadOnly(readOnly);
		this.birthday.setReadOnly(readOnly);
		
		if(readOnly) {
			this.doSave.hide();
			this.doComplete.hide();
		}
	}

	@Override
	protected Map<String, Object> sendDatatoParent(){
		//Info.display("Form summit data", "Form summit data ...!!!");
		final Map<String, Object> mapData = new HashMap<String, Object>();
		mapData.put("name", name.getCurrentValue());
		mapData.put("email", email.getCurrentValue());
		mapData.put("phone", phone.getCurrentValue());
		mapData.put("address", address.getCurrentValue());
		return mapData;
	}

	final SelectHandler doSaveSH = new SelectHandler(){
		@Override
		public void onSelect(SelectEvent event){
			doSave();
		}
	};

	final SelectHandler doCompleteSH = new SelectHandler(){
		@Override
		public void onSelect(SelectEvent event){
			doComplete();
		}
	};

	final SelectHandler doCloseSH = new SelectHandler(){
		@Override
		public void onSelect(SelectEvent event){
			doClose();
		}
	};
}