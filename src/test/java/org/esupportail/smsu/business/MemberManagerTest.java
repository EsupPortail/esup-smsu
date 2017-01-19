package org.esupportail.smsu.business;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Test;

public class MemberManagerTest {

    @Test
    public void test_phoneNumberFormats() throws IOException {
        MemberManager m = new MemberManager();

        assertEquals("0601020304", m.toFrenchPhoneNumber("+33 6 01 02 03 04"));
        assertEquals("0601020304", m.toFrenchPhoneNumber("+33601020304"));
        assertEquals("+33 6 01 02 03 04", m.fromFrenchPhoneNumber("06 01 02 03 04"));
        assertEquals("+33 6 01 02 03 04", m.fromFrenchPhoneNumber("0601020304"));
        assertEquals("+33 6 01 22 23 33", m.fromFrenchPhoneNumber("06 01 222 333"));
    }

}

