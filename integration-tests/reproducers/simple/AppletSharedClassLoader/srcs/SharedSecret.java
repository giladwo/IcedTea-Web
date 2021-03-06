/* 
 Copyright (C) 2013 Red Hat, Inc.

 This file is part of IcedTea.

IcedTea is free software; you can redistribute it and/or modify it under the
terms of the GNU General Public License as published by the Free Software
Foundation, version 2.

IcedTea is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
IcedTea; see the file COPYING. If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is making a
combined work based on this library. Thus, the terms and conditions of the GNU
General Public License cover the whole combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent modules, and
to copy and distribute the resulting executable under terms of your choice,
provided that you also meet, for each linked independent module, the terms and
conditions of the license of that module. An independent module is a module
which is not derived from or based on this library. If you modify this library,
you may extend this exception to your version of the library, but you are not
obligated to do so. If you do not wish to do so, delete this exception
statement from your version.
*/

import java.applet.Applet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedSecret {

    public static AtomicInteger value = new AtomicInteger(1);
    private final Applet body;
    private static final Random r = new Random();

    public SharedSecret(Applet body) {
        this.body = body;
        System.out.println(body.getCodeBase().toString() + body.toString() + "have " + this.getClass().getClassLoader().toString());
    }

    public void run() {
        while (true) {
            if (body.getParameter("reader") != null) {
                System.out.println(this.toString() + " Reading " + value.toString() + " X");
            } else if (body.getParameter("writer") != null) {
                int a = value.incrementAndGet();
                System.out.println(this.toString() + " Writing " + a + " X");
            } else {
                System.out.println(this.toString() + "Unknown destiny");
            }
            try {
                Thread.sleep(r.nextInt(50) + 50);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
