/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2015 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import waffle.mock.MockWindowsIdentity;
import waffle.servlet.WindowsPrincipal;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAuthenticationTokenTests {

    private WindowsPrincipal           principal;
    private WindowsAuthenticationToken token;

    @Before
    public void setUp() {
        List<String> mockGroups = new ArrayList<String>();
        mockGroups.add("group1");
        mockGroups.add("group2");
        MockWindowsIdentity mockIdentity = new MockWindowsIdentity("localhost\\user1", mockGroups);
        this.principal = new WindowsPrincipal(mockIdentity);
        this.token = new WindowsAuthenticationToken(this.principal);
    }

    @Test
    public void testWindowsAuthenticationToken() {
        Assert.assertNull(this.token.getCredentials());
        Assert.assertNull(this.token.getDetails());
        Assert.assertTrue(this.token.isAuthenticated());
        Assert.assertEquals("localhost\\user1", this.token.getName());
        Collection<GrantedAuthority> authorities = this.token.getAuthorities();
        Iterator<GrantedAuthority> authoritiesIterator = authorities.iterator();
        Assert.assertEquals(3, authorities.size());

        final List<String> list = new ArrayList<String>();
        while (authoritiesIterator.hasNext()) {
            list.add(authoritiesIterator.next().getAuthority());
        }
        Collections.sort(list);
        Assert.assertEquals("ROLE_GROUP1", list.get(0));
        Assert.assertEquals("ROLE_GROUP2", list.get(1));
        Assert.assertEquals("ROLE_USER", list.get(2));
        Assert.assertEquals(this.principal, this.token.getPrincipal());
    }

    @Test
    public void testCustomGrantedAuthorityFactory() {

        WindowsAuthenticationToken myToken = new WindowsAuthenticationToken(this.principal,
                new FqnGrantedAuthorityFactory(null, false), null);

        Assert.assertNull(myToken.getCredentials());
        Assert.assertNull(myToken.getDetails());
        Assert.assertTrue(myToken.isAuthenticated());
        Assert.assertEquals("localhost\\user1", myToken.getName());
        Collection<GrantedAuthority> authorities = myToken.getAuthorities();
        Iterator<GrantedAuthority> authoritiesIterator = authorities.iterator();
        Assert.assertEquals(2, authorities.size());

        final List<String> list = new ArrayList<String>();
        while (authoritiesIterator.hasNext()) {
            list.add(authoritiesIterator.next().getAuthority());
        }
        Collections.sort(list);
        Assert.assertEquals("group1", list.get(0));
        Assert.assertEquals("group2", list.get(1));
        Assert.assertEquals(this.principal, myToken.getPrincipal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticated() {
        Assert.assertTrue(this.token.isAuthenticated());
        this.token.setAuthenticated(true);
    }
}
