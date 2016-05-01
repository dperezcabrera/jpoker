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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author David Perez Cabrera <dperezcabrera@gmail.com>
 */
public class ExceptionUtilTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Test of checkNullArgument method, of class ExceptionUtil.
     */
    @Test
    public void testCheckNullArgument() {
        System.out.println("checkNullArgument");

        Object o = null;
        String name = "";
        thrown.expect(IllegalArgumentException.class);

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
    @Test
    public void testCheckArrayLengthArgument() {
        System.out.println("checkArrayLengthArgument");

        Object[] a = null;
        String name = "";
        int length = 5;
        thrown.expect(IllegalArgumentException.class);
        ExceptionUtil.checkArrayLengthArgument(a, name, length);
    }

    /**
     * Test of checkArrayLengthArgument method, of class ExceptionUtil.
     */
    @Test
    public void testCheckArrayLengthArgumentWrongLength() {
        System.out.println("checkArrayLengthArgument");
        int length = 5;
        Object[] a = new Object[length + 1];
        String name = "";
        thrown.expect(IllegalArgumentException.class);

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
    @Test
    public void testCheckArgumentThrow() {
        System.out.println("checkArgument");
        boolean throwException = true;
        String message = "error";
        thrown.expect(IllegalArgumentException.class);

        ExceptionUtil.checkArgument(throwException, message);
    }
}
