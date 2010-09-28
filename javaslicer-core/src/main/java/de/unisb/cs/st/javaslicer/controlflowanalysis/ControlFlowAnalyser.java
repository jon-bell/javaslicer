/** License information:
 *    Component: javaslicer-core
 *    Package:   de.unisb.cs.st.javaslicer.controlflowanalysis
 *    Class:     ControlFlowAnalyser
 *    Filename:  javaslicer-core/src/main/java/de/unisb/cs/st/javaslicer/controlflowanalysis/ControlFlowAnalyser.java
 *
 * This file is part of the JavaSlicer tool, developed by Clemens Hammacher at Saarland University.
 * See http://www.st.cs.uni-saarland.de/javaslicer/ for more information.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a
 * letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 */
package de.unisb.cs.st.javaslicer.controlflowanalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.hammacher.util.UniqueQueue;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionType;
import de.unisb.cs.st.javaslicer.common.classRepresentation.ReadMethod;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.LabelMarker;
import de.unisb.cs.st.javaslicer.controlflowanalysis.ControlFlowGraph.AbstractInstrNode;
import de.unisb.cs.st.javaslicer.controlflowanalysis.ControlFlowGraph.InstrNode;
import de.unisb.cs.st.javaslicer.controlflowanalysis.ReachabilityNodeFactory.ReachInstrNode;

public class ControlFlowAnalyser {

    private static ControlFlowAnalyser instance = new ControlFlowAnalyser();

    private ControlFlowAnalyser() {
        // private constructor
    }

    public static ControlFlowAnalyser getInstance() {
        return instance;
    }

    /**
     * Computes the (inverted) control dependences for one method.
     *
     * @param method the method for which the dependences are computed
     * @return a map that contains for every instruction all instructions that are dependent on this one
     */
    public Map<Instruction, Set<Instruction>> getInvControlDependences(ReadMethod method) {
        Map<Instruction, Set<Instruction>> invControlDeps = new HashMap<Instruction, Set<Instruction>>();
        Set<Instruction> emptyInsnSet = Collections.emptySet();
        ControlFlowGraph graph = new ControlFlowGraph(method, ReachabilityNodeFactory.getInstance());
        computeReachableNodes(graph);
        for (Instruction insn: method.getInstructions()) {
            InstrNode node = graph.getNode(insn);
            ReachInstrNode reachNode = (ReachabilityNodeFactory.ReachInstrNode)node;
            if (insn.getType() == InstructionType.LABEL) {
                LabelMarker label = (LabelMarker) insn;
                if (label.isCatchBlock()) {
                    Set<AbstractInstrNode> executedIfException = reachNode.getSurelyReached();
                    InstrNode node2 = graph.getNode(
                            method.getInstructions().iterator().next());
                    Set<AbstractInstrNode> availableWithoutException = ((ReachInstrNode)node2).getReachable();
                    Set<Instruction> deps = new HashSet<Instruction>();
                    for (InstrNode succ: executedIfException) {
                        if (!availableWithoutException.contains(succ) && succ.getInstruction() != insn)
                            deps.add(succ.getInstruction());
                    }
                    invControlDeps.put(insn, deps.isEmpty() ? emptyInsnSet : deps);
                } else {
                    invControlDeps.put(insn, emptyInsnSet);
                }
            } else if (node.getOutDegree() > 1) {
                assert node.getOutDegree() == node.getSuccessors().size();
                List<Set<AbstractInstrNode>> succAvailableNodes = new ArrayList<Set<AbstractInstrNode>>(node.getOutDegree());
                Set<AbstractInstrNode> allInstrNodes = new HashSet<AbstractInstrNode>();
                for (InstrNode succ: node.getSuccessors()) {
                    ReachInstrNode reachSucc = (ReachInstrNode) succ;
                    Set<AbstractInstrNode> availableNodes = reachSucc.getSurelyReached();
                    succAvailableNodes.add(availableNodes);
                    allInstrNodes.addAll(availableNodes);
                }
                Set<Instruction> deps = new HashSet<Instruction>();
                for (InstrNode succ: allInstrNodes) {
                    for (Set<AbstractInstrNode> succAv: succAvailableNodes)
                        if (!succAv.contains(succ))
                            deps.add(succ.getInstruction());
                }
                invControlDeps.put(insn, deps.isEmpty() ? emptyInsnSet : deps);
            } else {
                invControlDeps.put(insn, emptyInsnSet);
            }
        }
        return invControlDeps;
    }

    private void computeReachableNodes(ControlFlowGraph cfg) {
        UniqueQueue<InstrNode> queue = new UniqueQueue<InstrNode>(true);
        InstrNode node;
        // using this random to switch between FIFO and LIFO behaviour turned out
        // to decrease the runtime a lot.
        // on an example method with 706 nodes (the critical one in a big example run):
        // - LIFO: 9.55 seconds
        // - FIFO: 1.88 seconds
        // - 0.5*LIFO + 0.5*FIFO: 0.23 seconds
        Random rand = new Random();

        // first, compute the reachable nodes:
        for (Instruction instr: cfg.getMethod().getInstructions())
            queue.add(cfg.getNode(instr));
        while ((node = queue.poll()) != null) {
            Iterator<InstrNode> succIt = node.getSuccessors().iterator();
            if (succIt.hasNext()) {
                boolean change = false;
                ReachInstrNode reachNode = (ReachInstrNode) node;
                Set<AbstractInstrNode> reachable = reachNode.getReachable();
                while (succIt.hasNext()) {
                    ReachInstrNode succ = (ReachInstrNode) succIt.next();
                    change |= reachable.addAll(succ.getReachable());
                }
                if (change) {
                    for (InstrNode pred : node.getPredecessors()) {
                        if (rand.nextBoolean())
                            queue.addLast(pred);
                        else
                            queue.addFirst(pred);
                    }
                }
            }
        }

        // then, compute the surely reached nodes:
        for (Instruction instr: cfg.getMethod().getInstructions())
            queue.add(cfg.getNode(instr));
        while ((node = queue.poll()) != null) {
            Iterator<InstrNode> succIt = node.getSuccessors().iterator();
            if (succIt.hasNext()) {
                boolean change = false;
                ReachInstrNode reachNode = (ReachInstrNode) node;
                ReachInstrNode succ1 = (ReachInstrNode) succIt.next();
                if (succIt.hasNext()) { // more than one successor?
                    Set<AbstractInstrNode> surelyReached = new HashSet<AbstractInstrNode>(succ1.getSurelyReached());
                    do {
                        ReachInstrNode succ2 = (ReachInstrNode) succIt.next();
                        surelyReached.retainAll(succ2.getSurelyReached());
                    } while (succIt.hasNext());
                    change |= reachNode.getSurelyReached().addAll(surelyReached);
                } else {
                    change |= reachNode.getSurelyReached().addAll(succ1.getSurelyReached());
                }
                if (change) {
                    for (InstrNode pred : node.getPredecessors()) {
                        if (rand.nextBoolean())
                            queue.addLast(pred);
                        else
                            queue.addFirst(pred);
                    }
                }
            }
        }
    }

}
