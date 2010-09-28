/** License information:
 *    Component: javaslicer-core
 *    Package:   de.unisb.cs.st.javaslicer.slicing
 *    Class:     TestSimpleSlicing1
 *    Filename:  javaslicer-core/src/test/java/de/unisb/cs/st/javaslicer/slicing/TestSimpleSlicing1.java
 *
 * This file is part of the JavaSlicer tool, developed by Clemens Hammacher at Saarland University.
 * See http://www.st.cs.uni-saarland.de/javaslicer/ for more information.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a
 * letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
package de.unisb.cs.st.javaslicer.slicing;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javaslicer.AbstractSlicingTest;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;


public class TestSimpleSlicing1 extends AbstractSlicingTest {

    @Test
    public void test7a() throws IllegalArgumentException, IOException, URISyntaxException, InterruptedException {
        final List<Instruction> slice = getSlice("/traces/simple1", "main", "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7:{a}");
        checkSlice(slice, new String[] {
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ALOAD 0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ICONST_0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 AALOAD",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ICONST_0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 INVOKEVIRTUAL java/lang/String.charAt(I)C",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 BIPUSH 48",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ISUB",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ISTORE 1",
            });
    }

    @Test
    public void test8a() throws IllegalArgumentException, IOException, URISyntaxException, InterruptedException {
        final List<Instruction> slice = getSlice("/traces/simple1", "main", "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8:{b}");
        checkSlice(slice, new String[] {
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ALOAD 0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ICONST_0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 AALOAD",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ICONST_0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 INVOKEVIRTUAL java/lang/String.charAt(I)C",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 BIPUSH 48",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ISUB",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ISTORE 1",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ICONST_2",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ILOAD 1",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 IMUL",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ISTORE 2",
            });
    }

    @Test
    public void test8b() throws IllegalArgumentException, IOException, URISyntaxException, InterruptedException {
        final List<Instruction> slice = getSlice("/traces/simple1", "main", "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8:{b,c,d}");
        checkSlice(slice, new String[] {
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ALOAD 0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ICONST_0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 AALOAD",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ICONST_0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 INVOKEVIRTUAL java/lang/String.charAt(I)C",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 BIPUSH 48",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ISUB",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ISTORE 1",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ICONST_2",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ILOAD 1",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 IMUL",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ISTORE 2",
            });
    }

    @Test
    public void test8c() throws IllegalArgumentException, IOException, URISyntaxException, InterruptedException {
        final List<Instruction> slice = getSlice("/traces/simple1", "main", "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8:{a,b}");
        checkSlice(slice, new String[] {
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ALOAD 0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ICONST_0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 AALOAD",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ICONST_0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 INVOKEVIRTUAL java/lang/String.charAt(I)C",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 BIPUSH 48",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ISUB",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ISTORE 1",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ICONST_2",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ILOAD 1",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 IMUL",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ISTORE 2",
            });
    }

    @Test
    public void test9c() throws IllegalArgumentException, IOException, URISyntaxException, InterruptedException {
        final List<Instruction> slice = getSlice("/traces/simple1", "main", "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:9:{c}");
        checkSlice(slice, new String[] {
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ALOAD 0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ICONST_0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 AALOAD",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ICONST_0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 INVOKEVIRTUAL java/lang/String.charAt(I)C",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 BIPUSH 48",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ISUB",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ISTORE 1",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ICONST_2",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ILOAD 1",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 IMUL",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ISTORE 2",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:9 ICONST_2",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:9 ILOAD 2",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:9 IMUL",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:9 ISTORE 3",
            });
    }

    @Test
    public void test10d() throws IllegalArgumentException, IOException, URISyntaxException, InterruptedException {
        final List<Instruction> slice = getSlice("/traces/simple1", "main", "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:10:{d}");
        checkSlice(slice, new String[] {
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ALOAD 0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ICONST_0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 AALOAD",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ICONST_0",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 INVOKEVIRTUAL java/lang/String.charAt(I)C",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 BIPUSH 48",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ISUB",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:7 ISTORE 1",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ICONST_2",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ILOAD 1",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 IMUL",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:8 ISTORE 2",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:9 ICONST_2",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:9 ILOAD 2",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:9 IMUL",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:9 ISTORE 3",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:10 ICONST_2",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:10 ILOAD 3",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:10 IMUL",
                "de.unisb.cs.st.javaslicer.tracedCode.Simple1.main:10 ISTORE 4",
            });
    }

}
