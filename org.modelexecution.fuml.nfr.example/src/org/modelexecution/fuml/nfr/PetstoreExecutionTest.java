/*
 * Copyright (c) 2013 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Tanja Mayerhofer - initial API and implementation
 */
package org.modelexecution.fuml.nfr;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.modelexecution.fumldebug.core.ExecutionEventListener;
import org.modelexecution.fumldebug.core.event.Event;
import org.modelexecution.fumldebug.core.trace.tracemodel.Trace;
import org.modelexecution.fumldebug.papyrus.PapyrusModelExecutor;

import fUML.Semantics.Classes.Kernel.CompoundValue;
import fUML.Semantics.Classes.Kernel.ExtensionalValue;
import fUML.Semantics.Classes.Kernel.FeatureValue;
import fUML.Semantics.Classes.Kernel.IntegerValue;
import fUML.Semantics.Classes.Kernel.Link;
import fUML.Semantics.Classes.Kernel.Object_;
import fUML.Semantics.Classes.Kernel.Reference;
import fUML.Semantics.Classes.Kernel.StringValue;
import fUML.Semantics.Classes.Kernel.Value;
import fUML.Semantics.Classes.Kernel.ValueList;
import fUML.Semantics.CommonBehaviors.BasicBehaviors.ParameterValueList;
import fUML.Syntax.Classes.Kernel.Association;
import fUML.Syntax.Classes.Kernel.Classifier;
import fUML.Syntax.Classes.Kernel.Property;

public class PetstoreExecutionTest implements ExecutionEventListener {

	private StringBuffer log = new StringBuffer();

	@After
	public void printLog() {
		System.out.println(log.toString());
		log = new StringBuffer();
	}

	/**
	 * login: Successful login
	 */
	@Test
	public void testScenario1() {
		PapyrusModelExecutor executor = new PapyrusModelExecutor(
				"model/petstore/petstore.di");
		clearLocus(executor);
		Trace trace = executor.executeActivity("scenario1", null, null);

		ParameterValueList output = executor.getExecutionContext()
				.getActivityOutput(
						trace.getActivityExecutions().get(0)
								.getActivityExecutionID());
		Assert.assertEquals(1, output.size());
		Assert.assertEquals(1, output.get(0).values.size());
		Assert.assertEquals(1,
				((IntegerValue) output.get(0).values.get(0)).value);

		Set<Object_> objects = getObjects(executor, "ApplicationController");
		Assert.assertEquals(1, objects.size());
		Object_ applicationController = objects.iterator().next();
		objects = getObjects(executor, "Session");
		Assert.assertEquals(1, objects.size());
		Object_ session = objects.iterator().next();
		Set<Link> links = getLinks(executor, "applicationController_session_1");
		Assert.assertEquals(1, links.size());
		Link sessionlink = links.iterator().next();
		Assert.assertEquals(1, getFeatureValue(sessionlink, "sessions").size());
		Assert.assertEquals(
				session,
				((Reference) getFeatureValue(sessionlink, "sessions").get(0)).referent);
		Assert.assertEquals(1,
				getFeatureValue(sessionlink, "applicationController").size());
		Assert.assertEquals(
				applicationController,
				((Reference) getFeatureValue(sessionlink,
						"applicationController").get(0)).referent);

		Assert.assertEquals(0,
				getFeatureValue(applicationController, "foundCustomer").size());
	}

	/**
	 * login: Login with wrong password
	 */
	@Test
	public void testScenario2() {
		PapyrusModelExecutor executor = new PapyrusModelExecutor(
				"model/petstore/petstore.di");
		clearLocus(executor);
		Trace trace = executor.executeActivity("scenario2", null, null);

		ParameterValueList output = executor.getExecutionContext()
				.getActivityOutput(
						trace.getActivityExecutions().get(0)
								.getActivityExecutionID());
		Assert.assertEquals(1, output.size());
		Assert.assertEquals(1, output.get(0).values.size());
		Assert.assertEquals(-1,
				((IntegerValue) output.get(0).values.get(0)).value);

		Set<Object_> objects = getObjects(executor, "ApplicationController");
		Assert.assertEquals(1, objects.size());
		Object_ applicationController = objects.iterator().next();
		objects = getObjects(executor, "Session");
		Assert.assertEquals(0, objects.size());
		Set<Link> links = getLinks(executor, "applicationController_session_1");
		Assert.assertEquals(0, links.size());

		Assert.assertEquals(0,
				getFeatureValue(applicationController, "foundCustomer").size());
	}

	/**
	 * findItem: Find single item by name
	 */
	@Test
	public void testScenario3() {
		PapyrusModelExecutor executor = new PapyrusModelExecutor(
				"model/petstore/petstore.di");
		clearLocus(executor);
		Trace trace = executor.executeActivity("scenario3", null, null);

		ParameterValueList output = executor.getExecutionContext()
				.getActivityOutput(
						trace.getActivityExecutions().get(0)
								.getActivityExecutionID());
		Assert.assertEquals(1, output.size());
		Assert.assertEquals(1, output.get(0).values.size());
		Assert.assertTrue(output.get(0).values.get(0) instanceof Reference);

		Object_ item_poodle = ((Reference) output.get(0).values.get(0)).referent;
		Assert.assertEquals("Item", item_poodle.types.get(0).name);
		Assert.assertEquals(1, getFeatureValue(item_poodle, "name").size());
		Assert.assertEquals(
				"Poodle",
				((StringValue) getFeatureValue(item_poodle, "name").get(0)).value);
	}

	/**
	 * findItem: Find two items with same name
	 */
	@Test
	public void testScenario4() {
		PapyrusModelExecutor executor = new PapyrusModelExecutor(
				"model/petstore/petstore.di");
		clearLocus(executor);
		Trace trace = executor.executeActivity("scenario4", null, null);

		ParameterValueList output = executor.getExecutionContext()
				.getActivityOutput(
						trace.getActivityExecutions().get(0)
								.getActivityExecutionID());
		Assert.assertEquals(1, output.size());
		Assert.assertEquals(2, output.get(0).values.size());

		Object_ item_bulldog_1 = ((Reference) output.get(0).values.get(0)).referent;
		Assert.assertEquals("Item", item_bulldog_1.types.get(0).name);
		Assert.assertEquals(1, getFeatureValue(item_bulldog_1, "name").size());
		Assert.assertEquals(
				"Bulldog",
				((StringValue) getFeatureValue(item_bulldog_1, "name").get(0)).value);

		Object_ item_bulldog_2 = ((Reference) output.get(0).values.get(1)).referent;
		Assert.assertEquals("Item", item_bulldog_2.types.get(0).name);
		Assert.assertEquals(1, getFeatureValue(item_bulldog_2, "name").size());
		Assert.assertEquals(
				"Bulldog",
				((StringValue) getFeatureValue(item_bulldog_2, "name").get(0)).value);

		Assert.assertTrue(item_bulldog_1.equals(item_bulldog_2));
		Assert.assertFalse(item_bulldog_1 == item_bulldog_2);
	}

	/**
	 * findItem: Find no item with corresponding name
	 */
	@Test
	public void testScenario5() {
		PapyrusModelExecutor executor = new PapyrusModelExecutor(
				"model/petstore/petstore.di");
		clearLocus(executor);
		Trace trace = executor.executeActivity("scenario5", null, null);

		ParameterValueList output = executor.getExecutionContext()
				.getActivityOutput(
						trace.getActivityExecutions().get(0)
								.getActivityExecutionID());
		Assert.assertEquals(1, output.size());
		Assert.assertEquals(0, output.get(0).values.size());
	}

	/**
	 * addItemToCart: Add first item once, add second item twice
	 */
	@Test
	public void testScenario6() {
		PapyrusModelExecutor executor = new PapyrusModelExecutor(
				"model/petstore/petstore.di");
		clearLocus(executor);
		executor.executeActivity("scenario6", null, null);

		// get customers
		Set<Object_> customers = getObjects(executor, "Customer");
		Assert.assertEquals(2, customers.size());
		Object_ bill = getCustomerByLogin(executor, "bill");
		Object_ liz = getCustomerByLogin(executor, "liz");
		Assert.assertNotNull(bill);
		Assert.assertNotNull(liz);

		// get cart
		Set<Object_> cartObjects = getObjects(executor, "Cart");
		Assert.assertEquals(1, cartObjects.size());
		Object_ cart = cartObjects.iterator().next();

		// get items
		Object_ bulldogItem = getItemByName(executor, "Bulldog");
		Object_ poodleItem = getItemByName(executor, "Poodle");
		
		// check cart
		Set<Object_> lizCarts = getLinkedObjects(executor, "cart_customer_1", liz, "customer");
		Assert.assertEquals(1,  lizCarts.size());
		Object_ lizCart = lizCarts.iterator().next();
		Assert.assertEquals(cart, lizCart);		

		Set<Object_> cartitems = getObjects(executor, "CartItem");
		Assert.assertEquals(2, cartitems.size());
		Set<Link> cartItemLinks = getLinks(executor, "cart_cartItem_1");
		Assert.assertEquals(2, cartItemLinks.size());		
		
		Object_ lizCartItemPoodle = null;
		Object_ lizCartItemBulldog = null;
		
		Set<Link> itemLinks = getLinks(executor, "cartItem_item_1");
		Assert.assertEquals(2, itemLinks.size());
		Iterator<Link> itemLinksIterator = itemLinks.iterator();
		while (itemLinksIterator.hasNext()) {
			Link itemLink = itemLinksIterator.next();
			Object_ item = ((Reference) getFeatureValue(itemLink, "item")
					.get(0)).referent;
			Object_ cartitem = ((Reference) getFeatureValue(itemLink,
					"cartItem").get(0)).referent;
			if (item.equals(bulldogItem)) {
				lizCartItemBulldog = cartitem;
			} else if (item.equals(poodleItem)) {
				lizCartItemPoodle = cartitem;
			}
		}

		// check cart item quantity
		Assert.assertEquals(2,
				((IntegerValue) getFeatureValue(lizCartItemBulldog, "quantity")
						.get(0)).value);
		Assert.assertEquals(1,
				((IntegerValue) getFeatureValue(lizCartItemPoodle, "quantity")
						.get(0)).value);
	}

	/**
	 * confirmOrder: Confirm order created in scenario6
	 */
	@Test
	public void testScenario7() {
		PapyrusModelExecutor executor = new PapyrusModelExecutor(
				"model/petstore/petstore.di");
		clearLocus(executor);

		Trace trace = executor.executeActivity("scenario7", null, null);
		ParameterValueList output = executor.getExecutionContext()
				.getActivityOutput(
						trace.getActivityExecutions().get(0)
								.getActivityExecutionID());

		// get output order
		Assert.assertEquals(1, output.size());
		Assert.assertEquals(1, output.get(0).values.size());
		Assert.assertTrue(output.get(0).values.get(0) instanceof Reference);

		// check order
		Set<Object_> orderObjects = getObjects(executor, "Order");
		Assert.assertEquals(1, orderObjects.size());
		Object_ order = orderObjects.iterator().next();
		Assert.assertEquals(order,
				((Reference) output.get(0).values.get(0)).referent);

		// get liz customer
		Object_ liz = getCustomerByLogin(executor, "liz");
		Set<Object_> lizOrders = getLinkedObjects(executor, "order_customer_1",
				liz, "customer");
		Assert.assertEquals(1, lizOrders.size());
		Assert.assertEquals(order, lizOrders.iterator().next());

		// get items
		Object_ bulldog = getItemByName(executor, "Bulldog");
		Object_ poodle = getItemByName(executor, "Poodle");

		// check order
		Object_ orderLineBulldog = null;
		Object_ orderLinePoodle = null;

		Set<Link> orderLineLinks = getLinks(executor, "order_orderLine_1");
		Assert.assertEquals(2, orderLineLinks.size());

		Iterator<Link> orderLineLinksIterator = orderLineLinks.iterator();
		while (orderLineLinksIterator.hasNext()) {
			Link orderLinksLink = orderLineLinksIterator.next();
			Object_ orderLine = ((Reference) getFeatureValue(orderLinksLink,
					"orderLines").get(0)).referent;

			Set<Object_> orderLineItems = getLinkedObjects(executor,
					"orderLine_item_1", orderLine, "orderLine");
			Assert.assertEquals(1, orderLineItems.size());
			Object_ orderLineItem = orderLineItems.iterator().next();
			if (orderLineItem.equals(bulldog)) {
				orderLineBulldog = orderLine;
			} else if (orderLineItem.equals(poodle)) {
				orderLinePoodle = orderLine;
			}
		}

		Assert.assertNotNull(orderLineBulldog);
		Assert.assertNotNull(orderLinePoodle);

		Assert.assertEquals(2,
				((IntegerValue) getFeatureValue(orderLineBulldog, "quantity")
						.get(0)).value);
		Assert.assertEquals(1,
				((IntegerValue) getFeatureValue(orderLinePoodle, "quantity")
						.get(0)).value);

		Set<Link> orderCustomerLinks = getLinks(executor, "order_customer_1");
		Assert.assertEquals(1, orderCustomerLinks.size());
		Link orderCustomerLink = orderCustomerLinks.iterator().next();
		Assert.assertEquals(liz,
				((Reference) getFeatureValue(orderCustomerLink, "customer")
						.get(0)).referent);

		// check carts: no cart objects or cart items should exist
		Set<Object_> carts = getObjects(executor, "Cart");
		Assert.assertEquals(0, carts.size());

		Set<Object_> cartitems = getObjects(executor, "CartItem");
		Assert.assertEquals(0, cartitems.size());
	}

	private Object_ getItemByName(PapyrusModelExecutor executor, String name) {
		Set<Object_> items = getObjects(executor, "Item");
		Iterator<Object_> itemsIterator = items.iterator();
		while (itemsIterator.hasNext()) {
			Object_ item = itemsIterator.next();
			if (((StringValue) getFeatureValue(item, "name").get(0)).value
					.equals(name)) {
				return item;
			}
		}
		return null;
	}

	private Object_ getCustomerByLogin(PapyrusModelExecutor executor,
			String customerlogin) {
		Set<Object_> customers = getObjects(executor, "Customer");
		Iterator<Object_> customersIterator = customers.iterator();
		while (customersIterator.hasNext()) {
			Object_ customer = customersIterator.next();
			if (((StringValue) getFeatureValue(customer, "login").get(0)).value
					.equals(customerlogin)) {
				return customer;
			}
		}
		return null;
	}

	private void clearLocus(PapyrusModelExecutor executor) {
		executor.getExecutionContext().removeEventListener(this);
		executor.getExecutionContext().addEventListener(this);
		executor.getExecutionContext().getLocus().extensionalValues.clear();
	}

	private ValueList getFeatureValue(CompoundValue value, String featurename) {
		for (FeatureValue fv : value.featureValues) {
			if (fv.feature.name.equals(featurename)) {
				return fv.values;
			}
		}
		return null;
	}

	private Set<Object_> getObjects(PapyrusModelExecutor executor,
			String classifiername) {
		Set<Object_> objects = new HashSet<Object_>();
		for (ExtensionalValue extensionalValue : executor.getExecutionContext()
				.getLocus().extensionalValues) {
			if (extensionalValue.getClass() == Object_.class) {
				Object_ object_ = (Object_) extensionalValue;
				for (Classifier c : object_.getTypes()) {
					if (c.name.equals(classifiername)) {
						objects.add(object_);
					}
				}
			}
		}
		return objects;
	}

	private Set<Link> getLinks(PapyrusModelExecutor executor,
			String associationname) {
		Set<Link> links = new HashSet<Link>();
		for (ExtensionalValue extensionalValue : executor.getExecutionContext()
				.getLocus().extensionalValues) {
			if (extensionalValue instanceof Link) {
				Link link = (Link) extensionalValue;
				for (Classifier c : link.getTypes()) {
					if (c.name.equals(associationname)) {
						links.add(link);
					}
				}
			}
		}
		return links;
	}

	private Set<Object_> getLinkedObjects(PapyrusModelExecutor executor,
			String associationname, Object_ knownObject, String knownEndName) {
		Set<Object_> linkedObjects = new HashSet<Object_>();
		Association association = getAssociation(executor, associationname);
		Property knownEnd = getAssociationEnd(association, knownEndName);
		Property otherEnd = getOtherAssociationEnd(association, knownEnd);
		Set<Link> links = getLinks(executor, associationname);
		if (links.size() > 0 && otherEnd != null && knownEnd != null) {
			Iterator<Link> linkIterator = links.iterator();
			while (linkIterator.hasNext()) {
				Link link = linkIterator.next();
				if (getLinkedObject(link, knownEnd).equals(knownObject)) {
					linkedObjects.add(getLinkedObject(link, otherEnd));
				}
			}
		}
		return linkedObjects;
	}

	private Property getAssociationEnd(Association association, String endName) {
		for (Property memberEnd : association.memberEnd) {
			if (memberEnd.name.equals(endName)) {
				return memberEnd;
			}
		}
		return null;
	}

	private Property getOtherAssociationEnd(Association association,
			Property end) {
		for (Property memberEnd : association.memberEnd) {
			if (!end.equals(memberEnd)) {
				return memberEnd;
			}
		}
		return null;
	}

	private Association getAssociation(PapyrusModelExecutor executor,
			String associationname) {
		Iterator<Link> linksIterator = getLinks(executor, associationname)
				.iterator();
		while (linksIterator.hasNext()) {
			Link link = linksIterator.next();
			if (link.type != null) {
				return link.type;
			}
		}
		return null;
	}

	private Object_ getLinkedObject(Link link, Property end) {
		FeatureValue featureValue = link.getFeatureValue(end);
		for (Value value : featureValue.values) {
			if (value instanceof Object_) {
				return (Object_) value;
			} else if (value instanceof Reference) {
				return ((Reference) value).referent;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.modelexecution.fumldebug.core.ExecutionEventListener#notify(org.
	 * modelexecution.fumldebug.core.event.Event)
	 */
	@Override
	public void notify(Event event) {
		log.append(event.toString());
		log.append(System.getProperty("line.separator"));
	}
}
