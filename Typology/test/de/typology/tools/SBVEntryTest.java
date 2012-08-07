/**
 * Method test case for class SBVEntry
 * 
 * @author Paul Wagner
 */
package de.typology.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SBVEntryTest {

	public SBVEntry i_n1 = new SBVEntry("i_n1", -1, Integer.class);
	public SBVEntry i_0 = new SBVEntry("i_0", 0, Integer.class);
	public SBVEntry i_0_key = new SBVEntry("i_0_key", 0, Integer.class);
	public SBVEntry i_1 = new SBVEntry("i_1", 1, Integer.class);
	public SBVEntry l_n1 = new SBVEntry("i_n1", -1L, Long.class);
	public SBVEntry l_0 = new SBVEntry("i_0", 0L, Long.class);
	public SBVEntry l_1 = new SBVEntry("i_1", 1L, Long.class);
	public SBVEntry d_n1 = new SBVEntry("d_n1", -1D, Double.class);
	public SBVEntry d_0 = new SBVEntry("d_0", 0D, Double.class);
	public SBVEntry d_1 = new SBVEntry("d_1", 1D, Double.class);
	public SBVEntry f_n1 = new SBVEntry("f_n1", -1F, Float.class);
	public SBVEntry f_0 = new SBVEntry("f_0", 0F, Float.class);
	public SBVEntry f_1 = new SBVEntry("f_1", 1F, Float.class);
	public SBVEntry s_a = new SBVEntry("s_a", "a", String.class);
	public SBVEntry s_m = new SBVEntry("s_m", "m", String.class);
	public SBVEntry s_z = new SBVEntry("s_z", "z", String.class);

	// INTEGER

	@Test
	public void compareTo_IntegersLessAndGreater0_1() {
		int res = i_n1.compareTo(i_1);
		assertEquals("Compare int less and greater 0", 1, res);
	}

	@Test
	public void compareTo_IntegersGreaterAndLess0_sign1() {
		int res = i_1.compareTo(i_n1);
		assertEquals("Compare int greater and less 0", -1, res);
	}

	@Test
	public void compareTo_IntegersEquals0_0() {
		int res = i_0.compareTo(i_0);
		assertEquals("Compare int equals 0", 0, res);
	}

	@Test
	public void compareTo_IntegersEqualValueDifferentKey_sign1() {
		int res = i_0.compareTo(i_0_key);
		assertTrue("Compare int equals 0 but different keys", res < 0);
	}

	// LONG
	@Test
	public void compareTo_LongsLessAndGreater0_1() {
		int res = l_n1.compareTo(l_1);
		assertEquals("Compare long less and greater 0", 1, res);
	}

	@Test
	public void compareTo_LongsGreaterAndLess0_sign1() {
		int res = l_1.compareTo(l_n1);
		assertEquals("Compare long greater and less 0", -1, res);
	}

	@Test
	public void compareTo_LongsEquals0_0() {
		int res = l_0.compareTo(l_0);
		assertEquals("Compare long equals 0", 0, res);
	}

	// Double
	@Test
	public void compareTo_DoublesLessAndGreater0_1() {
		int res = d_n1.compareTo(d_1);
		assertEquals("Compare double less and greater 0", 1, res);
	}

	@Test
	public void compareTo_DoublesGreaterAndLess0_sign1() {
		int res = d_1.compareTo(d_n1);
		assertEquals("Compare double greater and less 0", -1, res);
	}

	@Test
	public void compareTo_DoublesEquals0_0() {
		int res = d_0.compareTo(d_0);
		assertEquals("Compare double equals 0", 0, res);
	}

	// FLOAT
	@Test
	public void compareTo_FloatsLessAndGreater0_1() {
		int res = f_n1.compareTo(f_1);
		assertEquals("Compare float less and greater 0", 1, res);
	}

	@Test
	public void compareTo_FloatsGreaterAndLess0_sign1() {
		int res = f_1.compareTo(f_n1);
		assertEquals("Compare float greater and less 0", -1, res);
	}

	@Test
	public void compareTo_FloatsEquals0_0() {
		int res = f_0.compareTo(f_0);
		assertEquals("Compare float equals 0", 0, res);
	}

	// STRING
	@Test
	public void compareTo_StringAandZ_1() {
		int res = s_a.compareTo(s_z);
		assertTrue("Compare string a less than z", res > 0);
	}

	@Test
	public void compareTo_StringZandA_sign1() {
		int res = s_z.compareTo(s_a);
		assertTrue("Compare string z greater than a", res < 0);
	}

	@Test
	public void compareTo_StringEqualsM_0() {
		int res = s_m.compareTo(s_m);
		assertEquals("Compare string equals m", 0, res);
	}

}
