/*
 * Copyright (C) 2015 David Perez Cabrera <dperezcabrera@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.util.exceptions;

import org.util.exceptions.ExceptionUtil;
import org.junit.Test;

/**
 *
 * @author David Perez Cabrera <dperezcabrera@gmail.com>
 */
public class ExceptionUtilTest {

    /**
     * Test of checkNullArgument method, of class ExceptionUtil.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckNullArgument() {
        System.out.println("checkNullArgument");
        Object o = null;
        String name = "";
        ExceptionUtil.checkNullArgument(o, name);
    }

    /**
     * Test of checkNullArgument method, of class ExceptionUtil.
     */
    @Test
    public void testCheckNullArgumentOk() {
        System.out.println("checkNullArgumentOk");
        String name = "";
        ExceptionUtil.checkNullArgument(name, name);
    }

    /**
     * Test of checkArrayLengthArgument method, of class ExceptionUtil.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckArrayLengthArgument() {
        System.out.println("checkArrayLengthArgument");
        Object[] a = null;
        String name = "";
        int length = 5;
        ExceptionUtil.checkArrayLengthArgument(a, name, length);
    }

    /**
     * Test of checkArrayLengthArgument method, of class ExceptionUtil.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckArrayLengthArgumentWrongLength() {
        System.out.println("checkArrayLengthArgument");
        int length = 5;
        Object[] a = new Object[length+1];
        String name = "";
        ExceptionUtil.checkArrayLengthArgument(a, name, length);
    }

    /**
     * Test of checkArgument method, of class ExceptionUtil.
     */
    @Test
    public void testCheckArgument() {
        System.out.println("checkArgument");
        boolean throwException = false;
        String message = "";
        Object[] args = null;
        ExceptionUtil.checkArgument(throwException, message, args);
    }

    
    /**
     * Test of checkArgument method, of class ExceptionUtil.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckArgumentThrow() {
        System.out.println("checkArgument");
        boolean throwException = true;
        String message = "error";
        ExceptionUtil.checkArgument(throwException, message);
    }
}
